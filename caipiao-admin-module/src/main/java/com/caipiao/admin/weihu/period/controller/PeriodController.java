package com.caipiao.admin.weihu.period.controller;

import com.alibaba.fastjson.JSONArray;
import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.service.weihu.lottery.LotteryService;
import com.caipiao.admin.service.weihu.period.PeriodService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.SessionUtil;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 期次-控制类
 */
@Controller
@RequestMapping("/weihu/period")
public class PeriodController
{
    private static final Logger logger = LoggerFactory.getLogger(PeriodController.class);

    @Autowired
    private PeriodService periodService;
    @Autowired
    private LotteryService lotteryService;

    /**
     * 显示期次维护首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_weihu_period")
    public String index()
    {
        return "weihu/period/index";
    }

    /**
     * 显示期次编辑页面
     * @author  mcdog
     */
    @RequestMapping("/initEdit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_period_edit")
    public String initEdit(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> periodList = periodService.queryPeriods(params);
        if(periodList != null && periodList.size() > 0)
        {
            periodList.get(0).put("lotteryId",params.get("lotteryId"));
            map.addAttribute("params",periodList.get(0));
        }
        return "weihu/period/edit";
    }

    /**
     * 显示期次新增页面
     * @author  mcdog
     */
    @RequestMapping("/initAdd")
    @ModuleAuthorityRequired(mcode = "btn_weihu_period_add")
    public String initAdd(HttpServletRequest request, HttpServletResponse response)
    {
        return "weihu/period/add";
    }

    /**
     * 显示期次设置加奖页
     * @author kouyi
     */
    @RequestMapping("/setAddPrize")
    @ModuleAuthorityRequired(mcode = "btn_weihu_period_szjj")
    public String setAddPrize(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> lotteryList = lotteryService.queryLotterys(params);
        if(lotteryList != null && lotteryList.size() > 0)
        {
            map.addAttribute("params",lotteryList.get(0));
        }
        return "weihu/period/prize";
    }

    /**
     * 显示期次审核页面
     * @author  mcdog
     */
    @RequestMapping("/initAudit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_period_audit")
    public String initAudit(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> periodList = periodService.queryPeriods(params);
        if(periodList != null && periodList.size() > 0)
        {
            periodList.get(0).put("lotteryId",params.get("lotteryId"));
            map.addAttribute("params",periodList.get(0));
        }
        return "weihu/period/audit";
    }

    /**
     * 显示期次详细页面
     * @author  mcdog
     */
    @RequestMapping("/initDetail")
    @ModuleAuthorityRequired(mcode = "menu_weihu_period")
    public String initDetail(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> periodList = periodService.queryPeriods(params);
        if(periodList != null && periodList.size() > 0)
        {
            periodList.get(0).put("lotteryId",params.get("lotteryId"));
            map.addAttribute("params",periodList.get(0));
        }
        return "weihu/period/detail";
    }

    /**
     * 查询期次信息
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_weihu_period")
    public void getLotterys(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = periodService.queryPeriods(params);//查询期次信息
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")) && params.get("periodRange") == null)
            {
                dataDto.put("tsize",periodService.queryPeriodsCount(params));//查询总记录条数
            }
            if(params.get("periodRange") != null)
            {
                dataDto.put("tsize",dataList != null? dataList.size() : 0);//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询彩种出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 编辑期次信息
     * @author  mcdog
     */
    @RequestMapping("/edit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_period_edit")
    public void editLottery(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[编辑期次信息]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数:" + params.toString());
            if(periodService.editPeriod(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","编辑成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("编辑期次信息发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 删除期次
     * @author  mcdog
     */
    @RequestMapping("/delete")
    @ModuleAuthorityRequired(mcode = "btn_weihu_period_delete")
    public void deletePeriods(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[删除期次]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数:" + params.toString());
            if(periodService.deletePeriods(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","删除成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "删除失败");
            }
        }
        catch(Exception e)
        {
            logger.error("删除期次发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 审核期次
     * @author  mcdog
     */
    @RequestMapping("/audit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_period_audit")
    public void auditPeriod(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[审核期次]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数:" + params.toString());
            if(periodService.updatePeriodForAudit(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","审核成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "审核失败");
            }
        }
        catch(Exception e)
        {
            logger.error("审核期次发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 新增期次
     * @author  mcdog
     */
    @RequestMapping("/add")
    @ModuleAuthorityRequired(mcode = "btn_weihu_period_add")
    public void addPeriod(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[新增期次]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数:" + params.toString());
            if(periodService.addPeriods(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","新增成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "新增失败");
            }
        }
        catch(Exception e)
        {
            logger.error("新增期次发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 获取期次状态下拉数据
     * @author  mcdog
     */
    @RequestMapping("/getPeriodStatesCombo")
    public void getPeriodStates(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list", LotteryUtils.getPeriodStates());//查询期次状态信息
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("期次状态获取出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 为期次设置加奖
     * @author kouyi
     */
    @RequestMapping("/addPrize")
    @ModuleAuthorityRequired(mcode = "btn_weihu_period_szjj")
    public void addPrize(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[设置期次加奖]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数:" + params.toString());
            int rows = periodService.updatePeriodAddPrize(params);
            if(rows > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","设置成功 影响行数" + rows + "行");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("设置期次加奖发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}