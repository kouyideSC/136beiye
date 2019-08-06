package com.caipiao.admin.service.weihu.period;

import com.caipiao.admin.service.task.TaskService;
import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.LotteryGrade;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.ParameterMapper;
import com.caipiao.dao.lottery.LotteryMapper;
import com.caipiao.dao.lottery.PeriodMapper;
import com.caipiao.dao.scheme.SchemeMapper;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 期次-服务类
 */
@Service("periodService")
public class PeriodService
{
    @Autowired
    private PeriodMapper periodMapper;

    @Autowired
    private SchemeMapper schemeMapper;

    @Autowired
    private LotteryMapper lotteryMapper;

    @Autowired
    private ParameterMapper parameterMapper;

    @Autowired
    private TaskService taskService;

    /**
     * 查询期次信息
     * @author	sjq
     */
    public List<Dto> queryPeriods(Dto params)
    {
        if(params.get("periodRange") != null)
        {
            //查询范围期次
            int periodRange = params.getAsInteger("periodRange");
            int hnum = 4;
            int qnum = periodRange - hnum;
            params.put("qnum",qnum);
            params.put("hnum",hnum);
            return periodMapper.queryRangePeriods(params);
        }
        return periodMapper.queryPeriods(params);
    }

    /**
     * 查询期次总记录条数
     * @author	sjq
     */
    public int queryPeriodsCount(Dto params)
    {
        return periodMapper.queryPeriodsCount(params);
    }

    /**
     * 设置加奖
     * @author kouyi
     */
    public int updatePeriodAddPrize(Dto params) throws Exception
    {
        int result = 0;
        if(StringUtils.isNotEmpty(params.getAsString("id")) && StringUtils.isNotEmpty(params.getAsString("startPeriod"))
                && StringUtils.isNotEmpty(params.getAsString("endPeriod")) && StringUtils.isNotEmpty(params.getAsString("prizeGrade")))
        {
            //根据id查询期次信息
            List<Period> periodList = periodMapper.queryPeriodsByRange(params);
            if(StringUtil.isNotEmpty(periodList))
            {
                //判断,如果期次的销售状态为已取消/已截止,则不再允许将销售状态变为未开售或销售中
                for(Period pd : periodList) {
                    if(pd.getSellStatus() == -1 || pd.getSellEndTime().getTime() < new Date().getTime() || pd.getState() > 0) {//已截止的期次不能设置加奖
                        continue;
                    }
                    String grade = pd.getPrizeGrade();
                    if(StringUtil.isEmpty(grade)) {
                        continue;
                    }
                    JSONObject jsonGrade = JSONObject.fromObject(grade);
                    JSONObject newGrade = JSONObject.fromObject(params.getAsString("prizeGrade"));
                    Iterator<String> it = newGrade.keys();
                    while (it.hasNext()) {
                        String key = it.next();
                        if(jsonGrade.containsKey(key)) {
                            JSONObject job = jsonGrade.getJSONObject(key);
                            job.put(LotteryGrade.jjjj, newGrade.getJSONObject(key).getString(LotteryGrade.jjjj));
                        }
                    }
                    pd.setPrizeGrade(jsonGrade.toString());
                    result += periodMapper.updatePeriodById(pd);
                }
            }
        }
        else
        {
            params.put("dmsg","缺少必要彩种参数{id}");
        }
        return result;
    }

    /**
     * 修改期次
     * @author	sjq
     */
    public int editPeriod(Dto params) throws  Exception
    {
        int result = 0;
        if(StringUtils.isNotEmpty(params.getAsString("id")))
        {
            //根据id查询期次信息
            List<Dto> periodList = periodMapper.queryPeriods(params);
            if(periodList != null && periodList.size() > 0)
            {
                //判断,如果期次的销售状态为已取消/已截止,则不再允许将销售状态变为未开售或销售中
                Dto periodDto = periodList.get(0);
                if((periodDto.getAsInteger("sellStatus") == -1 || periodDto.getAsInteger("sellStatus") == 2))
                {
                        /*if((params.getAsInteger("sellStatus") == 0 || params.getAsInteger("sellStatus") == 1))
                        {
                            params.put("dmsg","修改非法！无法修改已截止期次的销售状态！");
                            return result;
                        }*/
                    params.put("sellStatus",periodDto.get("sellStatus"));
                    params.put("sellStartTime",periodDto.get("sellStartTime"));
                    params.put("sellEndTime",periodDto.get("sellEndTime"));
                    params.put("authorityEndTime",periodDto.get("authorityEndTime"));
                    params.put("updateFlag",periodDto.get("updateFlag"));
                }
                //更新期次
                result = periodMapper.editPeriod(params);
                if(result > 0)
                {
                    try
                    {
                        //在售期次文件更新任务
                        Task task = new Task();
                        task.setTaskName(Constants.periodUpdateTaskMaps.get(params.getAsString("lotteryId")));
                        taskService.saveTask(task);

                        //历史期次文件更新任务
                        task = new Task();
                        task.setTaskName(Constants.periodHistoryUpdateTaskMaps.get(params.getAsString("lotteryId")));
                        taskService.saveTask(task);
                    }
                    catch(Exception e)
                    {
                        params.put("dmsg","修改失败！插入【文件更新任务】发生异常，异常信息：" + e.getMessage());
                        throw  e;
                    }
                }
            }
            else
            {
                params.put("dmsg","修改非法！无相关记录可供修改");
            }
        }
        else
        {
            params.put("dmsg","缺少必要参数{id}");
        }
        return result;
    }

    /**
     * 删除期次
     * @author	sjq
     */
    public int deletePeriods(Dto params) throws  Exception
    {
        int result = 0;
        if(StringUtil.isNotEmpty(params.getAsString("datas")))
        {
            JSONArray jsonArray = JSONArray.fromObject(params.getAsString("datas"));//解析参数串
            Map<String,String> lotteryIdMaps = new HashMap<String,String>();
            if(jsonArray != null && jsonArray.size() > 0)
            {
                List<Dto> queryDtoList = new ArrayList<Dto>();
                StringBuilder dmsgBuilder = new StringBuilder();
                for(int i = 0; i < jsonArray.size(); i ++)
                {
                    //查询彩种期次的方案记录
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Dto queryDto = new BaseDto("lotteryId",obj.get("lotteryId"));
                    queryDto.put("period",obj.get("period"));
                    Dto countDto = schemeMapper.queryUserSchemesCount(queryDto);//查询方案总计
                    int schemeCount = countDto.getAsInteger("tsize");//提取方案记录条数
                    if(schemeCount > 0)
                    {
                        dmsgBuilder.append("<br>期次号-" + queryDto.getAsString("period") + "已有" + schemeCount + "条方案");
                    }
                    queryDto.clear();
                    queryDto.put("id",obj.get("id"));
                    queryDtoList.add(queryDto);
                    lotteryIdMaps.put(obj.getString("lotteryId"),obj.getString("lotteryId"));

                }
                //判断,如果待删除的某个期次已经有方案信息,则不做任何删除的操作
                if(dmsgBuilder.length() > 0)
                {
                    params.put("dmsg","删除失败！" + dmsgBuilder.toString());
                }
                else
                {
                    for(Dto queryDto : queryDtoList)
                    {
                        int tempResult = periodMapper.deletePeriod(queryDto);//执行删除
                        result += tempResult;
                    }
                    if(result > 0)
                    {
                        try
                        {
                            for(Map.Entry<String,String> entry : lotteryIdMaps.entrySet())
                            {
                                //在售期次文件更新任务
                                Task task = new Task();
                                task.setTaskName(Constants.periodUpdateTaskMaps.get(entry.getKey()));
                                taskService.saveTask(task);

                                //历史期次文件更新任务
                                task = new Task();
                                task.setTaskName(Constants.periodHistoryUpdateTaskMaps.get(entry.getKey()));
                                taskService.saveTask(task);
                            }
                        }
                        catch(Exception e)
                        {
                            params.put("dmsg","删除失败！插入【文件更新任务】发生异常，异常信息：" + e.getMessage());
                            throw  e;
                        }
                    }
                }
            }
            else
            {
                params.put("dmsg","无法解析参数串datas");
            }
        }
        else
        {
            params.put("dmsg","请至少选择一条记录进行删除");
        }
        return result;
    }

    /**
     * 审核期次
     * @author	sjq
     */
    public int updatePeriodForAudit(Dto params) throws  Exception
    {
        int result = 0;
        List<Dto> periodList = periodMapper.queryPeriods(new BaseDto("id",params.get("id")));
        if(periodList == null || periodList.size() == 0)
        {
            params.put("dmsg","查询不到相关的期次信息!");
            return 0;
        }
        Dto periodDto = periodList.get(0);
        if(StringUtil.isEmpty(periodDto.get("drawNumberTime")))
        {
            params.put("drawNumberTime", DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));
        }
        result = periodMapper.auditPeriod(params);
        if(result > 0)
        {
            try
            {
                //在售期次文件更新任务
                Task task = new Task();
                task.setTaskName(Constants.periodUpdateTaskMaps.get(params.getAsString("lotteryId")));
                taskService.saveTask(task);

                //历史期次文件更新任务
                new Task();
                task.setTaskName(Constants.periodUpdateTaskMaps.get(params.getAsString("lotteryId")));
                taskService.saveTask(task);
            }
            catch(Exception e)
            {
                params.put("dmsg","审核失败！插入【文件更新任务】发生异常，异常信息：" + e.getMessage());
                throw  e;
            }
        }
        return result;
    }

    /**
     * 新增期次
     * @author	sjq
     */
    public int addPeriods(Dto params) throws  Exception
    {
        int result = 0;
        if(params.get("lotteryId") != null)
        {
            /**
             * 查询彩票休市时间
             */
            Dto closeTimeDto = parameterMapper.queryParameter(new BaseDto("pmKey",LotteryConstants.LOTTERY_CLOSE_TIME_KEY));
            closeTimeDto = closeTimeDto == null? new BaseDto() : closeTimeDto;
            /**
             * 根据期次生成模式生成期次信息
             */
            List<Map<String,String>> periodList = new ArrayList<Map<String, String>>();
            String periodRange = params.getAsString("periodRange");

            //生成指定年份的期次
            if("1".equals(periodRange))
            {
                if(StringUtils.isEmpty(params.getAsString("periodYear")))
                {
                    params.put("dmsg","请输入要生成期次的年份");
                    return result;
                }
                //生成一年期次
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR,params.getAsInteger("periodYear"));
                periodList.addAll(LotteryUtils.createPeriodsOfYear(
                        params.getAsString("lotteryId"),
                        closeTimeDto.getAsString("pmValue"),
                        calendar));
                //查询本年度已存在的期次
                Dto existsQueryDto = new BaseDto("lotteryId",params.get("lotteryId"));
                existsQueryDto.put("periodPrefix",calendar.get(Calendar.YEAR));
                List<Dto> existsPeriodList = periodMapper.queryPeriods(existsQueryDto);
                if(existsPeriodList != null && existsPeriodList.size() > 0)
                {
                    //剔除掉已存在的期次
                    Map<String,Dto> existsPeriodMaps = new HashMap<String, Dto>();
                    for(Dto periodDto : existsPeriodList)
                    {
                        existsPeriodMaps.put(periodDto.getAsString("period"),periodDto);
                    }
                    List<Map<String,String>> newPeriodList = new ArrayList<Map<String, String>>();
                    for(Map<String,String> periodMap : periodList)
                    {
                        if(!existsPeriodMaps.containsKey(periodMap.get("period")))
                        {
                            newPeriodList.add(periodMap);
                        }
                    }
                    periodList.clear();
                    periodList.addAll(newPeriodList);
                }
            }
            //生成指定期次数的期次
            else if("2".equals(periodRange))
            {
                if(StringUtils.isEmpty(params.getAsString("periodNum")))
                {
                    params.put("dmsg","请输入要生成的期次数");
                    return result;
                }
                //查询彩种当前最大期次信息
                Dto maxPeriodDto = periodMapper.queryMaxPeriod(params);
                maxPeriodDto = maxPeriodDto == null? new BaseDto() : maxPeriodDto;

                //生成指定期数
                periodList.addAll(LotteryUtils.createPeriodsByPeriodNum(
                        params.getAsString("lotteryId"),
                        closeTimeDto.getAsString("pmValue"),
                        maxPeriodDto.getAsString("period"),
                        params.getAsInteger("periodNum")));
            }
            //根据输入的期次信息生成期次
            else if("3".equals(periodRange))
            {
                if(StringUtils.isEmpty(params.getAsString("period")))
                {
                    params.put("dmsg","请输入期次号");
                    return result;
                }
                //查询期次是否存在
                Dto existsQueryDto = new BaseDto("lotteryId",params.get("lotteryId"));
                existsQueryDto.put("period",params.get("period"));
                List<Dto> existsPeriodList = periodMapper.queryPeriods(existsQueryDto);
                if(existsPeriodList != null && existsPeriodList.size() > 0)
                {
                    params.put("dmsg","期次-" + params.getAsString("period") + "已存在");
                    return result;
                }
                //读取录入的期次信息
                Map<String,String> periodMap = new HashMap<String, String>();
                periodMap.put("lotteryId",params.getAsString("lotteryId"));
                periodMap.put("period",params.getAsString("period"));
                periodMap.put("sellStatus",params.getAsString("sellStatus"));
                periodMap.put("sellStartTime",params.getAsString("sellStartTime"));
                periodMap.put("sellEndTime",params.getAsString("sellEndTime"));
                periodMap.put("authorityEndTime",params.getAsString("sellEndTime"));
                periodMap.put("authorityEndTime",params.getAsString("sellEndTime"));
                periodList.add(periodMap);
            }
            /**
             * 保存期次
             */
            if(periodList.size() > 0)
            {
                //查询彩种奖级信息
                String prizeGrade = "";
                List<Dto> lotteryList = lotteryMapper.queryLotterys(new BaseDto("id",params.get("lotteryId")));
                if(lotteryList != null && lotteryList.size() > 0)
                {
                    prizeGrade = lotteryList.get(0).getAsString("prizeGrade");
                }
                //保存期次
                for(Map<String,String> periodMap : periodList)
                {
                    periodMap.put("prizeGrade",prizeGrade);
                    periodMap.put("sellStatus",StringUtils.isEmpty(periodMap.get("sellStatus"))? "1" : periodMap.get("sellStatus"));
                    result += periodMapper.addPeriod(periodMap);
                }
                try
                {
                    //在售期次文件更新任务
                    Task task = new Task();
                    task.setTaskName(Constants.periodUpdateTaskMaps.get(params.getAsString("lotteryId")));
                    taskService.saveTask(task);
                }
                catch(Exception e)
                {
                    params.put("dmsg","新增失败！插入【文件更新任务】发生异常，异常信息：" + e.getMessage());
                    throw  e;
                }
            }
        }
        else
        {
            params.put("dmsg","缺少必要参数{lotteryId}");
        }
        return result;
    }
}