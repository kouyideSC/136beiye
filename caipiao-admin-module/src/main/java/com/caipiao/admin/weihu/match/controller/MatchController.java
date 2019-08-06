package com.caipiao.admin.weihu.match.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.service.weihu.lottery.LotteryService;
import com.caipiao.admin.service.weihu.match.MatchService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.SessionUtil;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.DateUtil;
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
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 赛事对阵-控制类
 */
@Controller
@RequestMapping("/weihu/match")
public class MatchController
{
    private static final Logger logger = LoggerFactory.getLogger(MatchController.class);

    @Autowired
    private MatchService matchService;

    /**
     * 显示赛事维护首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_weihu_match")
    public String index(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = new BaseDto();
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.HOUR_OF_DAY) < 11 || (calendar.get(Calendar.HOUR_OF_DAY) == 11 && calendar.get(Calendar.MINUTE) < 30))
        {
            calendar.add(Calendar.DAY_OF_MONTH,-1);
        }
        params.put("currentPeriod", DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE1));
        map.put("params",params);
        return "weihu/match/index";
    }

    /**
     * 显示赛事详细页面
     * @author  mcdog
     */
    @RequestMapping("/initDetail")
    @ModuleAuthorityRequired(mcode = "menu_weihu_match")
    public String initDetail(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> matchList = matchService.queryMatches(params);
        if(matchList != null && matchList.size() > 0)
        {
            matchList.get(0).put("lotteryId",params.get("lotteryId"));
            map.addAttribute("params",matchList.get(0));
        }
        return "weihu/match/detail";
    }

    /**
     * 显示赛果审核页面
     * @author  mcdog
     */
    @RequestMapping("/initAudit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_match_audit")
    public String initEdit(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> matchList = matchService.queryMatches(params);
        if(matchList != null && matchList.size() > 0)
        {
            matchList.get(0).put("lotteryId",params.get("lotteryId"));
            map.addAttribute("params",matchList.get(0));
        }
        return "weihu/match/audit";
    }

    /**
     * 显示玩法销售状态编辑页面
     * @author  mcdog
     */
    @RequestMapping("/initPlaySellStatus")
    @ModuleAuthorityRequired(mcode = "btn_weihu_match_xgwfxszt")
    public String initPlay(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> matchList = matchService.queryMatches(params);
        if(matchList != null && matchList.size() > 0)
        {
            matchList.get(0).put("lotteryId",params.get("lotteryId"));
            map.addAttribute("params",matchList.get(0));
        }
        return "weihu/match/playSellStatus";
    }

    /**
     * 显示sp编辑页面
     * @author  mcdog
     */
    @RequestMapping("/initSp")
    @ModuleAuthorityRequired(mcode = "btn_weihu_match_xgsp")
    public String initSp(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> matchList = matchService.queryMatches(params);
        if(matchList != null && matchList.size() > 0)
        {
            matchList.get(0).put("lotteryId",params.get("lotteryId"));
            map.addAttribute("params",matchList.get(0));
        }
        return "weihu/match/sp";
    }

    /**
     * 显示销售状态编辑页面
     * @author  mcdog
     */
    @RequestMapping("/initSellStatus")
    @ModuleAuthorityRequired(mcode = "btn_weihu_match_xgxszt")
    public String initSellStatus(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        Dto dataDto = new BaseDto();
        if(params.get("id") != null)
        {
            List<Dto> matchList = matchService.queryMatches(params);
            if(matchList != null && matchList.size() > 0)
            {
                dataDto = matchList.get(0);
            }
        }
        dataDto.put("ids",params.get("ids"));
        dataDto.put("lotteryId",params.get("lotteryId"));
        map.addAttribute("params",dataDto);
        return "weihu/match/sellStatus";
    }

    /**
     * 查询赛事对阵信息
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_weihu_match")
    public void getMatches(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",matchService.queryMatches(params));//查询赛事对阵信息
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",matchService.queryMatchesCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询赛事对阵出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 赛果审核
     * @author  mcdog
     */
    @RequestMapping("/audit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_match_audit")
    public void auditMatchResult(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[赛果审核]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数:" + params.toString());
            if(matchService.eidtMatchResult(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","赛果审核成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("赛果审核发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 编辑销售状态
     * @author  mcdog
     */
    @RequestMapping("/editSellStatus")
    @ModuleAuthorityRequired(mcode = "btn_weihu_match_xgxszt")
    public void editMatchSellStatus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[编辑销售状态]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数:" + params.toString());
            if(matchService.editSellStatus(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","操作成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("编辑销售状态发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 编辑玩法销售状态
     * @author  mcdog
     */
    @RequestMapping("/editPlaySellStatus")
    @ModuleAuthorityRequired(mcode = "btn_weihu_match_xgwfxszt")
    public void editPlaySellStatus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[编辑玩法销售状态]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数:" + params.toString());
            if(matchService.editPlaySellStatus(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","操作成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("编辑玩法销售状态发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 编辑sp
     * @author  mcdog
     */
    @RequestMapping("/editSp")
    @ModuleAuthorityRequired(mcode = "btn_weihu_match_xgsp")
    public void editPlaySp(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[编辑sp]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数:" + params.toString());
            if(matchService.editSp(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","操作成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("编辑sp发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 设置热门
     * @author  mcdog
     */
    @RequestMapping("/editHot")
    @ModuleAuthorityRequired(mcode = "btn_weihu_match_szrm")
    public void editHot(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[设置热门]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数:" + params.toString());
            if(matchService.editHot(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","操作成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("设置热门发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 获取对阵场次计奖状态下拉数据
     * @author  mcdog
     */
    @RequestMapping("/getMatchJjStatesCombo")
    public void getMatchStatesCombo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list", LotteryUtils.getMatchJJStates());//查询对阵场次计奖状态信息
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("对阵场次计奖状态获取出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}