package com.caipiao.service.lottery;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.ActivityMapper;
import com.caipiao.dao.lottery.PeriodMapper;
import com.caipiao.dao.lottery.ZxMapper;
import com.caipiao.dao.match.MatchBasketBallMapper;
import com.caipiao.dao.match.MatchFootBallMapper;
import com.caipiao.dao.zx.ArticleMapper;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.Activity;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.match.MatchBasketBall;
import com.caipiao.domain.vo.JclqMatchVo;
import com.caipiao.domain.vo.JczqMatchVo;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 资讯服务类
 * @author sjq
 */
@Service("zxService")
public class ZxService
{
    private static Logger logger = LoggerFactory.getLogger(ZxService.class);

    private static Map<Integer,String> articleTypeMap = new HashMap<Integer,String>();

    static
    {
        articleTypeMap.put(0,"推荐");
        articleTypeMap.put(1,"预测");
        articleTypeMap.put(2,"情报");
    }

    @Autowired
    private ZxMapper zxMapper;

    @Autowired
    private MatchFootBallMapper footBallMapper;

    @Autowired
    private MatchBasketBallMapper basketBallMapper;

    @Autowired
    private ActivityMapper activityMapper;

    /**
     * 获取资讯
     * @author  mcdog
     */
    public void getZx(Dto params,ResultBean result) throws ServiceException,Exception
    {
        /**
         * 设置分页参数
         */
        //判断是否有分页标识,如果有,则设置分页查询参数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            //pnum为空或值格式错误,则默认查询第一页
            if(params.get("pnum") == null || params.getAsInteger("pnum") <= 0)
            {
                params.put("pnum",1);
            }
            //设置读取起始位置
            long pstart = (params.getAsLong("pnum") - 1) * params.getAsLong("psize");
            params.put("pstart",pstart);//设置读取起始位置
        }

        /**
         * 查询并封装数据
         */
        //如果资讯类型为公告
        Calendar current = Calendar.getInstance();//获取当前时间
        Dto dataDto = new BaseDto();
        List<Dto> dataList = new ArrayList<Dto>();
        if("0".equals(params.getAsString("lid")))
        {
            params.put("activityType",3);
            List<Activity> activityList = activityMapper.queryActivitys(params);
            if(activityList != null && activityList.size() > 0)
            {
                Dto data = null;
                for(Activity activity : activityList)
                {
                    data = new BaseDto();
                    data.put("tid",activity.getId());//设置id
                    data.put("title",activity.getTitle());//设置标题
                    data.put("logo", SysConfig.getHostStatic() + activity.getPictureUrl());//设置logo

                    //设置内容
                    //data.put("contents",StringUtil.isEmpty(activity.getContent())? "" : activity.getContent());
                    String contents = activity.getContent();//提取公告内容
                    if(StringUtil.isEmpty(contents))
                    {
                        data.put("scontents","");
                    }
                    else
                    {
                        contents = contents.replaceAll("</?[^>]+>", ""); //剔出<html>的标签
                        contents = contents.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符
                        if(contents.length() < 30)
                        {
                            data.put("scontents",contents);
                        }
                        else
                        {
                            data.put("scontents",contents.substring(0,30) + "...");
                        }
                    }
                    //设置发布时间(发布时间在当天,则发布时间为xx小时前,发布时间为昨天xx时xx分,其它情况发布时间为月-日 时:分)
                    String ptime = DateUtil.formatDate(activity.getCreateTime(),DateUtil.DEFAULT_DATE_TIME);
                    Calendar pcalendar = DateUtil.parseCalendar(ptime,DateUtil.DEFAULT_DATE_TIME);
                    int dcx = DateUtil.daysBetween(pcalendar.getTime(),current.getTime());//天差(发布时间跟当前时间相差的天数)
                    if(dcx == 0)
                    {
                        int hcx = current.get(Calendar.HOUR_OF_DAY) - pcalendar.get(Calendar.HOUR_OF_DAY);//时差
                        int mcx = current.get(Calendar.MINUTE) - pcalendar.get(Calendar.MINUTE);//分差
                        ptime = hcx > 0? (hcx + "小时前") : (mcx > 0? (mcx + "分钟前") : ("刚刚"));
                    }
                    else if(dcx == 1)
                    {
                        ptime = "昨天" + DateUtil.formatDate(pcalendar.getTime(),DateUtil.CHINESE_HOUR_TIME);
                    }
                    else
                    {
                        ptime = DateUtil.formatDate(pcalendar.getTime(),DateUtil.MDHM_FORMAT);
                    }
                    ptime += "发布";
                    data.put("ptime",ptime);
                    dataList.add(data);
                }
            }
        }
        else
        {
            List<Dto> articleList = zxMapper.queryTj(params);
            for(Dto articleDto : articleList)
            {
                //封装为前端展示对象
                Dto data = new BaseDto();
                data.put("tid",articleDto.get("id"));//设置id
                data.put("title",articleDto.get("title"));//设置标题
                data.put("logo", SysConfig.getHostStatic() + articleDto.get("logo"));//设置logo

                //设置内容(只截取部分内容)
                //data.put("contents",StringUtil.isEmpty(articleDto.get("contents"))? "" : articleDto.get("contents"));
                String contents = articleDto.getAsString("contents");//提取推荐内容
                if(StringUtil.isEmpty(contents))
                {
                    data.put("scontents","");
                }
                else
                {
                    contents = contents.replaceAll("</?[^>]+>", ""); //剔出<html>的标签
                    contents = contents.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符
                    if(contents.length() < 30)
                    {
                        data.put("scontents",contents);
                    }
                    else
                    {
                        data.put("scontents",contents.substring(0,30) + "...");
                    }
                }
                //设置发布时间(发布时间在当天,则发布时间为xx小时前,发布时间为昨天xx时xx分,其它情况发布时间为月-日 时:分)
                String ptime = articleDto.getAsString("createTime");
                Calendar pcalendar = DateUtil.parseCalendar(ptime,DateUtil.DEFAULT_DATE_TIME);
                int dcx = DateUtil.daysBetween(pcalendar.getTime(),current.getTime());//天差(发布时间跟当前时间相差的天数)
                if(dcx == 0)
                {
                    int hcx = current.get(Calendar.HOUR_OF_DAY) - pcalendar.get(Calendar.HOUR_OF_DAY);//时差
                    int mcx = current.get(Calendar.MINUTE) - pcalendar.get(Calendar.MINUTE);//分差
                    ptime = hcx > 0? (hcx + "小时前") : (mcx > 0? (mcx + "分钟前") : ("刚刚"));
                }
                else if(dcx == 1)
                {
                    ptime = "昨天" + DateUtil.formatDate(pcalendar.getTime(),DateUtil.CHINESE_HOUR_TIME);
                }
                else
                {
                    ptime = DateUtil.formatDate(pcalendar.getTime(),DateUtil.MDHM_FORMAT);
                }
                ptime += "发布";
                data.put("ptime",ptime);
                dataList.add(data);
            }
        }
        dataDto.put("list",dataList);
        result.setData(dataDto);
        result.setErrorCode(ErrorCode_API.SUCCESS);
    }

    /**
     * 获取首页数据
     * @author  mcdog
     */
    public void getHomeDatas(Dto params,ResultBean result) throws ServiceException,Exception
    {
        Dto dataDto = new BaseDto();//用来存放返回数据
        List<Dto> dataList = new ArrayList<Dto>();//用来存放单类型的数据集

        /**
         * 获取轮播图
         */
        params.put("activityType",1);
        params.put("clientType",1);
        params.put("isShow",1);
        List<Activity> activityList = activityMapper.queryActivitys(params);
        if(activityList != null && activityList.size() > 0)
        {
            for(Activity activity : activityList)
            {
                Dto data = new BaseDto();
                data.put("name",activity.getActivityName());//设置名称
                data.put("build",activity.getBuild());//设置详情显示模式
                data.put("picurl",SysConfig.getHostStatic() + activity.getPictureUrl());//设置图片地址
                data.put("linkurl",activity.getLinkUrl());//设置点击活动图片链接到url
                data.put("contents",activity.getContent());//设置详情内容
                dataList.add(data);
            }
        }
        dataDto.put("banner",dataList);//设置轮播图数据

        /**
         * 获取热门赛事
         */
        dataList = new ArrayList<Dto>();
        List<JczqMatchVo> jczqMatchList = footBallMapper.queryJczqSaleMatchList();//查询当前在售的足球对阵
        List<JclqMatchVo> jclqMatchList = basketBallMapper.queryJclqSaleMatchList();//查询当前在售的篮球对阵

        //判断,如果足球/篮球对阵截取的场次数
        int zqcount = (jczqMatchList == null || jczqMatchList.size() == 0)? 0 : 1;//读取足球对阵数
        int lqcount = (jczqMatchList == null || jczqMatchList.size() == 0)? 1 : 0;//读取篮球对阵数
        Dto matchDto = null;
        for(int i = 0; i < zqcount && i < jczqMatchList.size(); i ++)
        {
            matchDto = new BaseDto();
            JczqMatchVo matchVo = jczqMatchList.get(i);
            matchDto.put("mname",matchVo.getName());//设置赛事名称
            matchDto.put("mtime",DateUtil.formatDate(matchVo.getMatchTime(),DateUtil.MDHM_FORMAT));//设置比赛时间
            matchDto.put("hname",matchVo.getHname());//设置主队名称
            matchDto.put("gname",matchVo.getGname());//设置客队名称
            matchDto.put("sp",StringUtil.isEmpty(matchVo.getSpf())? "--" : matchVo.getSpf().replace(","," "));//设置胜平负赔率
            matchDto.put("mtype",0);//设置赛事类型(0-足球 1-篮球)
            dataList.add(matchDto);
        }
        for(int i = 0; i < lqcount && i < jclqMatchList.size(); i ++)
        {
            matchDto = new BaseDto();
            JclqMatchVo matchVo = jclqMatchList.get(i);
            matchDto.put("mname",matchVo.getName());//设置赛事名称
            matchDto.put("mtime",DateUtil.formatDate(matchVo.getMatchTime(),DateUtil.MDHM_FORMAT));//设置比赛时间
            matchDto.put("hname",matchVo.getHname());//设置主队名称
            matchDto.put("gname",matchVo.getGname());//设置客队名称
            matchDto.put("sp",StringUtil.isEmpty(matchVo.getSf())? "--" : matchVo.getSf().replace(","," "));//设置胜负赔率
            matchDto.put("mtype",1);//设置赛事类型(0-足球 1-篮球)
            dataList.add(matchDto);
        }
        dataDto.put("hotmatch",dataList);//设置热门赛事数据

        /**
         * 获取热门推荐
         */
        dataList = new ArrayList<Dto>();
        params.put("pstart",0);
        params.put("psize",15);//默认只查询5条记录
        List<Dto> articleList = zxMapper.queryTj(params);
        if(articleList != null && articleList.size() > 0)
        {
            Calendar current = Calendar.getInstance();//获取当前时间
            for(Dto articleDto : articleList)
            {
                //封装为前端展示对象
                Dto data = new BaseDto();
                data.put("tid",articleDto.get("id"));//设置id
                data.put("title",articleDto.get("title"));//设置标题
                data.put("logo", SysConfig.getHostStatic() + articleDto.get("logo"));//设置logo

                //设置内容(只截取部分内容)
                String contents = articleDto.getAsString("contents");//提取推荐内容
                if(StringUtil.isEmpty(contents))
                {
                    data.put("contents","");
                }
                else
                {
                    contents = contents.replaceAll("</?[^>]+>", ""); //剔出<html>的标签
                    contents = contents.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符
                    if(contents.length() < 30)
                    {
                        data.put("contents",contents);
                    }
                    else
                    {
                        data.put("contents",contents.substring(0,30) + "...");
                    }
                }

                //设置发布时间(发布时间在当天,则发布时间为xx小时前,发布时间为昨天xx时xx分,其它情况发布时间为月-日 时:分)
                String ptime = articleDto.getAsString("createTime");
                Calendar pcalendar = DateUtil.parseCalendar(ptime,DateUtil.DEFAULT_DATE_TIME);
                int dcx = DateUtil.daysBetween(pcalendar.getTime(),current.getTime());//天差(发布时间跟当前时间相差的天数)
                if(dcx == 0)
                {
                    int hcx = current.get(Calendar.HOUR_OF_DAY) - pcalendar.get(Calendar.HOUR_OF_DAY);//时差
                    int mcx = current.get(Calendar.MINUTE) - pcalendar.get(Calendar.MINUTE);//分差
                    ptime = hcx > 0? (hcx + "小时前") : (mcx > 0? (mcx + "分钟前") : ("刚刚"));
                }
                else if(dcx == 1)
                {
                    ptime = "昨天" + DateUtil.formatDate(pcalendar.getTime(),DateUtil.CHINESE_HOUR_TIME);
                }
                else
                {
                    ptime = DateUtil.formatDate(pcalendar.getTime(),DateUtil.MDHM_FORMAT);
                }
                ptime += "发布";
                data.put("ptime",ptime);

                dataList.add(data);
            }
        }
        dataDto.put("hottj",dataList);//设置热门推荐数据

        //设置响应数据
        result.setData(dataDto);
        result.setErrorCode(ErrorCode_API.SUCCESS);
    }

    /**
     * 获取赛事推荐
     * @author  mcdog
     */
    public void getTj(Dto params,ResultBean result) throws ServiceException,Exception
    {
        /**
         * 设置查询参数
         */
        //判断是否有分页标识,如果有,则设置分页查询参数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            //pnum为空或值格式错误,则默认查询第一页
            if(params.get("pnum") == null || params.getAsInteger("pnum") <= 0)
            {
                params.put("pnum",1);
            }
            //设置读取起始位置
            long pstart = (params.getAsLong("pnum") - 1) * params.getAsLong("psize");
            params.put("pstart",pstart);//设置读取起始位置
        }

        /**
         * 查询数据
         */
        Dto dataDto = new BaseDto();
        List<Dto> articleList = zxMapper.queryTj(params);
        List<Dto> dataList = new ArrayList<Dto>();
        if(articleList != null && articleList.size() > 0)
        {
            Calendar current = Calendar.getInstance();//获取当前时间
            for(Dto articleDto : articleList)
            {
                //封装为前端展示对象
                Dto data = new BaseDto();
                data.put("tid",articleDto.get("id"));//设置id
                data.put("title",articleDto.get("title"));//设置标题
                data.put("logo", SysConfig.getHostStatic() + articleDto.get("logo"));//设置logo

                //设置内容(只截取部分内容)
                String contents = articleDto.getAsString("contents");//提取推荐内容
                if(StringUtil.isEmpty(contents))
                {
                    data.put("contents","");
                }
                else
                {
                    contents = contents.replaceAll("</?[^>]+>", ""); //剔出<html>的标签
                    contents = contents.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符
                    if(contents.length() < 30)
                    {
                        data.put("contents",contents);
                    }
                    else
                    {
                        data.put("contents",contents.substring(0,30) + "...");
                    }
                }

                //设置发布时间(发布时间在当天,则发布时间为xx小时前,发布时间为昨天xx时xx分,其它情况发布时间为月-日 时:分)
                String ptime = articleDto.getAsString("createTime");
                Calendar pcalendar = DateUtil.parseCalendar(ptime,DateUtil.DEFAULT_DATE_TIME);
                int dcx = DateUtil.daysBetween(pcalendar.getTime(),current.getTime());//天差(发布时间跟当前时间相差的天数)
                if(dcx == 0)
                {
                    int hcx = current.get(Calendar.HOUR_OF_DAY) - pcalendar.get(Calendar.HOUR_OF_DAY);//时差
                    int mcx = current.get(Calendar.MINUTE) - pcalendar.get(Calendar.MINUTE);//分差
                    ptime = hcx > 0? (hcx + "小时前") : (mcx > 0? (mcx + "分钟前") : ("刚刚"));
                }
                else if(dcx == 1)
                {
                    ptime = "昨天" + DateUtil.formatDate(pcalendar.getTime(),DateUtil.CHINESE_HOUR_TIME);
                }
                else
                {
                    ptime = DateUtil.formatDate(pcalendar.getTime(),DateUtil.MDHM_FORMAT);
                }
                ptime += "发布";
                data.put("ptime",ptime);

                dataList.add(data);
            }
        }
        dataDto.put("list",dataList);

        //如果有分页标识,则查询总记录条数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            long tsize = zxMapper.queryTjCount(params);
            long psize = params.getAsLong("psize");
            dataDto.put("tsize",tsize);//设置账户流水总记录数
            dataDto.put("tpage",tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1));//设置总页数
        }
        result.setData(dataDto);
        result.setErrorCode(ErrorCode_API.SUCCESS);
    }

    /**
     * 获取推荐详情
     * @author  mcdog
     */
    public void getTjDetail(Dto params,ResultBean result) throws ServiceException,Exception
    {
        List<Dto> articleList = zxMapper.queryTj(new BaseDto("id",params.get("tid")));
        if(articleList != null && articleList.size() > 0)
        {
            Dto articleDto = articleList.get(0);
            Dto dataDto = new BaseDto();
            dataDto.put("title",articleDto.get("title"));//设置标题
            dataDto.put("logo", SysConfig.getHostStatic() + articleDto.get("logo"));//设置logo
            dataDto.put("tname",(articleDto.get("lotteryName") + articleTypeMap.get(articleDto.getAsInteger("articleType"))));//设置类型名称
            dataDto.put("contents",articleDto.get("contents"));//设置内容
            dataDto.put("ptime",articleDto.get("createTime"));//设置发布时间
            result.setData(dataDto);
            result.setErrorCode(ErrorCode_API.SUCCESS);
        }
    }

    /**
     * 获取热门比赛
     * @author  mcdog
     */
    public void getHotMatch(Dto params,ResultBean result) throws ServiceException,Exception
    {
        List<Dto> matchList = new ArrayList<Dto>();
        List<JczqMatchVo> jczqMatchList = footBallMapper.queryJczqSaleMatchList();//查询当前在售的足球对阵
        List<JclqMatchVo> jclqMatchList = basketBallMapper.queryJclqSaleMatchList();//查询当前在售的篮球对阵

        //判断,如果足球/篮球对阵截取的场次数
        int zqcount = (jczqMatchList == null || jczqMatchList.size() == 0)? 0 : 2;//读取足球对阵数
        int lqcount = (jczqMatchList == null || jczqMatchList.size() == 0)? 3 : (jczqMatchList.size() == 1? 2 : 1);//读取篮球对阵数
        Dto matchDto = null;
        for(int i = 0; i < zqcount && i < jczqMatchList.size(); i ++)
        {
            matchDto = new BaseDto();
            JczqMatchVo matchVo = jczqMatchList.get(i);
            matchDto.put("mname",matchVo.getName());//设置赛事名称
            matchDto.put("mtime",DateUtil.formatDate(matchVo.getMatchTime(),DateUtil.MDHM_FORMAT));//设置比赛时间
            matchDto.put("hname",matchVo.getHname());//设置主队名称
            matchDto.put("gname",matchVo.getGname());//设置客队名称
            matchDto.put("sp",StringUtil.isEmpty(matchVo.getSpf())? "--" : matchVo.getSpf().replace(","," "));//设置胜平负赔率
            matchDto.put("mtype",0);//设置赛事类型(0-足球 1-篮球)
            matchList.add(matchDto);
        }
        for(int i = 0; i < lqcount && i < jclqMatchList.size(); i ++)
        {
            matchDto = new BaseDto();
            JclqMatchVo matchVo = jclqMatchList.get(i);
            matchDto.put("mname",matchVo.getName());//设置赛事名称
            matchDto.put("mtime",DateUtil.formatDate(matchVo.getMatchTime(),DateUtil.MDHM_FORMAT));//设置比赛时间
            matchDto.put("hname",matchVo.getHname());//设置主队名称
            matchDto.put("gname",matchVo.getGname());//设置客队名称
            matchDto.put("sp",StringUtil.isEmpty(matchVo.getSf())? "--" : matchVo.getSf().replace(","," "));//设置胜负赔率
            matchDto.put("mtype",1);//设置赛事类型(0-足球 1-篮球)
            matchList.add(matchDto);
        }
        result.setData(matchList);
        result.setErrorCode(ErrorCode_API.SUCCESS);
    }

    /**
     * 获取消息通知
     * @author  mcdog
     */
    public void getNotice(Dto params,ResultBean result) throws ServiceException,Exception
    {
        List<Dto> dataList = new ArrayList<Dto>();
        params.put("activityType",2);
        params.put("build","notice");
        List<Activity> activityList = activityMapper.queryActivitys(params);
        if(activityList != null && activityList.size() > 0)
        {
            Activity activity = activityList.get(0);
            Dto data = new BaseDto();
            data.put("title",activity.getTitle());//设置标题
            data.put("content",activity.getContent());//设置内容
            data.put("ptime",DateUtil.formatDate(activity.getCreateTime(),DateUtil.MDHM_FORMAT));//设置发布时间
            dataList.add(data);
        }
        result.setData(dataList);
        result.setErrorCode(ErrorCode_API.SUCCESS);
    }
}