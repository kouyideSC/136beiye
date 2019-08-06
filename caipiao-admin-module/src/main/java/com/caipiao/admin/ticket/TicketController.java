package com.caipiao.admin.ticket;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.service.config.SysConfig;
import com.caipiao.admin.service.ticket.TicketService;
import com.caipiao.admin.service.user.SchemeService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.SessionUtil;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.DoubleUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import com.caipiao.memcache.MemCached;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 出票控制类
 * Created by Kouyi on 2017/12/01.
 */
@Controller
@RequestMapping("/ticket")
public class TicketController
{
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    private static final String VOTE_KEY = "TICKET_VOTES_KEY";
    @Autowired
    private MemCached memcache;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private SchemeService schemeService;

    /**
     * 显示出票商首页
     * @author  kouyi
     */
    @RequestMapping("/vote/index")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_vote")
    public String voteIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "ticket/vote/index";
    }

    /**
     * 显示出票商新增页面
     * @author kouyi
     */
    @RequestMapping("/vote/initAdd")
    @ModuleAuthorityRequired(mcode = "btn_chupiao_vote_add")
    public String initVoteAdd(HttpServletRequest request, HttpServletResponse response)
    {
        return "ticket/vote/add";
    }

    /**
     * 新增出票商
     * @author kouyi
     */
    @RequestMapping("/vote/add")
    @ModuleAuthorityRequired(mcode = "btn_chupiao_vote_add")
    public void addVote(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(ticketService.saveTicketVote(params) > 0)
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
            logger.error("新增出票商出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 删除出票商
     * @author kouyi
     */
    @RequestMapping("/vote/delete")
    @ModuleAuthorityRequired(mcode = "btn_chupiao_vote_delete")
    public void deleteVote(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(ticketService.deleteTicketVote(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","删除成功");
                memcache.delete(VOTE_KEY + params.getAsString("voteId"));//清除缓存
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "删除失败");
            }
        }
        catch(Exception e)
        {
            logger.error("删除出票商出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询出票商列表
     * @author kouyi
     */
    @RequestMapping("/vote/list")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_vote")
    public void getVoteList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = ticketService.queryTicketVoteList(params);//查询出票商信息
            for(Dto dt : dataList) {
                dt.put("key", getSafeKey(dt.getAsString("key")));
            }
            Dto dataDto = new BaseDto("list",dataList);
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询出票商列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询出票商信息
     * @author kouyi
     */
    @RequestMapping("/vote/detail")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_vote")
    public String getVoteDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> voteList = ticketService.queryTicketVoteList(params);
        if(voteList != null && voteList.size() > 0)
        {
            map.addAttribute("params",voteList.get(0));
        }
        return "ticket/vote/edit";
    }

    /**
     * 更新出票商信息
     * @author kouyi
     */
    @RequestMapping("/vote/edit")
    @ModuleAuthorityRequired(mcode = "btn_chupiao_vote_edit")
    public void editVoteInfo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(ticketService.updateTicketVote(params) > 0)
            {
                memcache.delete(VOTE_KEY + params.getAsString("voteId"));//清除缓存
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
            logger.error("编辑出票商出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 显示分票规则首页
     * @author  kouyi
     */
    @RequestMapping("/rule/index")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_rule")
    public String ruleIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "ticket/rule/index";
    }

    /**
     * 显示分票规则新增页面
     * @author kouyi
     */
    @RequestMapping("/rule/initAdd")
    @ModuleAuthorityRequired(mcode = "btn_chupiao_rule_add")
    public String initRuleAdd(HttpServletRequest request, HttpServletResponse response)
    {
        return "ticket/rule/add";
    }

    /**
     * 新增分票规则
     * @author kouyi
     */
    @RequestMapping("/rule/add")
    @ModuleAuthorityRequired(mcode = "btn_chupiao_rule_add")
    public void addRule(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(ticketService.saveTicketRule(params) > 0)
            {
                memcache.delete(VOTE_KEY + params.getAsString("voteId"));//清除缓存
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
            logger.error("新增分票规则出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 删除分票规则
     * @author kouyi
     */
    @RequestMapping("/rule/delete")
    @ModuleAuthorityRequired(mcode = "btn_chupiao_rule_delete")
    public void deleteRule(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(ticketService.deleteTicketRule(params) > 0)
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
            logger.error("删除分票规则出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询分票规则列表
     * @author kouyi
     */
    @RequestMapping("/rule/list")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_rule")
    public void getRuleList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = ticketService.queryTicketRuleList(params);//查询出票商信息
            Dto dataDto = new BaseDto("list",dataList);
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询分票规则列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询分票规则信息
     * @author kouyi
     */
    @RequestMapping("/rule/detail")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_rule")
    public String getRuleDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> voteList = ticketService.queryTicketRuleList(params);
        if(voteList != null && voteList.size() > 0)
        {
            map.addAttribute("params",voteList.get(0));
        }
        return "ticket/rule/edit";
    }

    /**
     * 更新分票规则信息
     * @author kouyi
     */
    @RequestMapping("/rule/edit")
    @ModuleAuthorityRequired(mcode = "btn_chupiao_rule_edit")
    public void editRuleInfo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(ticketService.updateTicketRule(params) > 0)
            {
                memcache.delete(VOTE_KEY + params.getAsString("voteId"));//清除缓存
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
            logger.error("编辑分票规则出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 获取玩法下拉数据
     * @author kouyi
     */
    @RequestMapping("/rule/getPlayType")
    public void getUserStatus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            String lotteryId = params.getAsString("lotteryId");
            Dto dataDto = new BaseDto("list", LotteryUtils.getSelectUtil(LotteryUtils.getLotteryPlay(lotteryId)));
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("分票规则模块下拉列表获取出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 显示出票控制参数首页
     * @author  kouyi
     */
    @RequestMapping("/config/index")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_config")
    public String configIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "ticket/config/index";
    }

    /**
     * 查询出票控制参数列表
     * @author kouyi
     */
    @RequestMapping("/config/list")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_config")
    public void getConfigList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = ticketService.queryTicketConfigList(params);//查询出票商信息
            Dto dataDto = new BaseDto("list",dataList);
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询出票控制参数列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询出票控制参数信息
     * @author kouyi
     */
    @RequestMapping("/config/detail")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_config")
    public String getConfigDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> voteList = ticketService.queryTicketConfigList(params);
        if(voteList != null && voteList.size() > 0)
        {
            map.addAttribute("params",voteList.get(0));
        }
        return "ticket/config/edit";
    }

    /**
     * 更新出票控制参数
     * @author kouyi
     */
    @RequestMapping("/config/edit")
    @ModuleAuthorityRequired(mcode = "btn_chupiao_config_edit")
    public void editConfigInfo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(ticketService.updateTicketConfig(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","编辑成功");
                memcache.delete(Constants.TICKET_CONFIG);//触发控制参数缓存更新
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("编辑出票控制参数出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询出票首页
     * @author  kouyi
     */
    @RequestMapping("/query/index")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_query")
    public String queryIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-2);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        params.put("beginTime", DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
        map.addAttribute("params",params);
        return "ticket/query/index";
    }

    /**
     * 查询出票列表
     * @author kouyi
     */
    @RequestMapping("/query/list")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_query")
    public void getQueryList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = ticketService.queryTicketList(params);//查询出信息
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",ticketService.queryTicketListCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询出票列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询票详细信息
     * @author kouyi
     */
    @RequestMapping("/query/detail")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_query")
    public String getTicketDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        try {
            BaseDto params = WebUtils.getPraramsAsDto(request);//初始化请求参数
            Dto ticket = ticketService.queryTicketInfoById(params);
            String sts = "未知";
            Integer status = ticket.getAsInteger("ticketStatus");
            if(SchemeConstants.ticketStatusMap.containsKey(status)) {
                sts = SchemeConstants.ticketStatusMap.get(status);
            }
            String playType = ticket.getAsString("playTypeId");
            if(LotteryConstants.jcPlayNameMaps.containsKey(playType)) {
                playType = LotteryConstants.jcPlayNameMaps.get(playType);
            } else {
                playType = "";
            }
            String bonusState = ticket.getAsString("bonusState");
            if(bonusState.equals("0")) {
                bonusState = "未计奖";
            } else if(bonusState.equals("1")) {
                bonusState = "中奖匹配完成";
            } else if(bonusState.equals("2")) {
                bonusState = "计算奖金";
            } else {
                bonusState = "汇总批次奖金完成";
            }
            String isWin = ticket.getAsString("isWin");
            if(isWin.equals("0")) {
                isWin = "待计奖";
            } else if(isWin.equals("1")) {
                isWin = "未中奖";
            } else if(isWin.equals("2")) {
                isWin = "已中奖";
            } else {
                isWin = "未知";
            }

            String ticketPrize = DoubleUtil.formatOdds(ticket.getAsDoubleValue("ticketPrize"));
            String ticketSubjoinPrize = ticket.getAsString("ticketSubjoinPrize");
            String ticketPrizeTax = DoubleUtil.formatOdds(ticket.getAsDoubleValue("ticketPrizeTax"));
            String ticketSubjoinPrizeTax = ticket.getAsString("ticketSubjoinPrizeTax");

            map.addAttribute("iw", isWin);
            map.addAttribute("sq", ticketPrize + (StringUtil.isNotEmpty(ticketSubjoinPrize) ? "(含加奖" + ticketSubjoinPrize + ")":""));
            map.addAttribute("sh", ticketPrizeTax + (StringUtil.isNotEmpty(ticketSubjoinPrizeTax) ? "(含加奖" + ticketSubjoinPrizeTax + ")":""));
            map.addAttribute("bs", bonusState);
            map.addAttribute("ts", sts);
            map.addAttribute("lts", playType);
            map.addAttribute("params", ticket);
            map.addAttribute("isJc", LotteryUtils.isJc(ticket.getAsString("lotteryId")));
        } catch (Exception e) {
            logger.error("后台管理-查询票信息详情异常", e);
        }
        return "ticket/query/detail";
    }

    /**
     * 获取票状态下拉数据
     * @author kouyi
     */
    @RequestMapping("/getSchemeStatus")
    public void getSchemeStatus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Integer module = params.getAsInteger("module");
            Map<?, ?> map = null;
            if(module.intValue() == 1) {//出票状态
                map = SchemeConstants.ticketStatusMap;
            }
            else if(module.intValue() == 2) {//玩法状态
                map = LotteryConstants.jcPlayNameMaps;
            }
            Dto dataDto = new BaseDto("list", LotteryUtils.getSelectUtil(map));
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("出票状态下拉列表获取出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 手动切票首页
     * @author  kouyi
     */
    @RequestMapping("/change/index")
    @ModuleAuthorityRequired(mcode = "menu_chupiao_change")
    public String changeIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "ticket/change/index";
    }

    /**
     * 批量切票
     * @author kouyi
     */
    @RequestMapping("/change/qiepiao")
    @ModuleAuthorityRequired(mcode = "btn_chupiao_change_qp")
    public void changeQiePiao(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(ticketService.changeTicket(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","切票成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "切票失败");
            }
        }
        catch(Exception e)
        {
            logger.error("切票出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 实体店-查询出票首页
     * @author  kouyi
     */
    @RequestMapping("/shopquery/index")
    @ModuleAuthorityRequired(mcode = "menu_shopchupiao_query")
    public String shopQueryIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-2);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        params.put("beginTime", DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
        map.addAttribute("params",params);
        return "ticket/shopquery/index";
    }

    /**
     * 实体店-查询出票列表
     * @author kouyi
     */
    @RequestMapping("/shopquery/list")
    @ModuleAuthorityRequired(mcode = "menu_shopchupiao_query")
    public void getShopQueryList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dto = SessionUtil.getCurrentAccount(request);
            String loginName = dto.getAsString("accountName");
            Map<String, String> voteMaps = SysConfig.getShopTicketVoteId();
            if(voteMaps.containsKey(loginName)) {
                params.put("voteId", voteMaps.get(loginName));
            }else {
                params.put("voteId", "00000");
            }

            List<Dto> dataList = ticketService.queryTicketListShop(params);//查询出信息
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",ticketService.queryTicketListCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询出票列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 实体店-查询票详细信息
     * @author kouyi
     */
    @RequestMapping("/shopquery/detail")
    @ModuleAuthorityRequired(mcode = "menu_shopchupiao_query")
    public String getShopTicketDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        try {
            BaseDto params = WebUtils.getPraramsAsDto(request);//初始化请求参数
            Dto dto = SessionUtil.getCurrentAccount(request);
            String loginName = dto.getAsString("accountName");
            Map<String, String> voteMaps = SysConfig.getShopTicketVoteId();
            if(voteMaps.containsKey(loginName)) {
                params.put("voteId", voteMaps.get(loginName));
            }else {
                params.put("voteId", "00000");
            }

            Dto ticket = ticketService.queryTicketInfoById(params);
            String sts = "未知";
            Integer status = ticket.getAsInteger("ticketStatus");
            if(SchemeConstants.ticketStatusMap.containsKey(status)) {
                sts = SchemeConstants.ticketStatusMap.get(status);
            }
            String playType = ticket.getAsString("playTypeId");
            if(LotteryConstants.jcPlayNameMaps.containsKey(playType)) {
                playType = LotteryConstants.jcPlayNameMaps.get(playType);
            } else {
                playType = "";
            }
            String bonusState = ticket.getAsString("bonusState");
            if(bonusState.equals("0")) {
                bonusState = "未计奖";
            } else if(bonusState.equals("1")) {
                bonusState = "中奖匹配完成";
            } else if(bonusState.equals("2")) {
                bonusState = "计算奖金";
            } else {
                bonusState = "汇总批次奖金完成";
            }
            String isWin = ticket.getAsString("isWin");
            if(isWin.equals("0")) {
                isWin = "待计奖";
            } else if(isWin.equals("1")) {
                isWin = "未中奖";
            } else if(isWin.equals("2")) {
                isWin = "已中奖";
            } else {
                isWin = "未知";
            }

            String ticketPrize = DoubleUtil.formatOdds(ticket.getAsDoubleValue("ticketPrize"));
            String ticketSubjoinPrize = ticket.getAsString("ticketSubjoinPrize");
            String ticketPrizeTax = DoubleUtil.formatOdds(ticket.getAsDoubleValue("ticketPrizeTax"));
            String ticketSubjoinPrizeTax = ticket.getAsString("ticketSubjoinPrizeTax");

            Dto codes = new BaseDto();
            ticketService.settingSchemeDetailForTicket(ticket, codes);
            ticket.put("choose", codes);

            map.addAttribute("iw", isWin);
            map.addAttribute("sq", ticketPrize + (StringUtil.isNotEmpty(ticketSubjoinPrize) ? "(含加奖" + ticketSubjoinPrize + ")":""));
            map.addAttribute("sh", ticketPrizeTax + (StringUtil.isNotEmpty(ticketSubjoinPrizeTax) ? "(含加奖" + ticketSubjoinPrizeTax + ")":""));
            map.addAttribute("bs", bonusState);
            map.addAttribute("ts", sts);
            map.addAttribute("lts", playType);
            map.addAttribute("params", ticket);
            map.addAttribute("isJc", LotteryUtils.isJcht(ticket.getAsString("lotteryId")));
            map.addAttribute("isZc", LotteryUtils.isZC(ticket.getAsString("lotteryId")));
            map.addAttribute("isSzc", LotteryUtils.isSzc(ticket.getAsString("lotteryId")));
            map.addAttribute("isGyj", LotteryUtils.isGyj(ticket.getAsString("lotteryId")));
        } catch (Exception e) {
            logger.error("后台管理-查询票信息详情异常", e);
        }
        return "ticket/shopquery/detail";
    }

    /**
     * 实体店设置票状态
     * @author kouyi
     */
    @RequestMapping("/shopquery/setTicketStatus")
    @ModuleAuthorityRequired(mcode = "menu_shopchupiao_query")
    public void setShopTicketStatus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(ticketService.setTicketStatus(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","处理成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "处理失败");
            }
        }
        catch(Exception e)
        {
            logger.error("处理出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 实体店-查询出票首页
     * @author  kouyi
     */
    @RequestMapping("/shopchange/index")
    @ModuleAuthorityRequired(mcode = "menu_shopchupiao_change")
    public String shopChangeIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "ticket/shopchange/index";
    }

    /**
     * 实体店批量切票
     * @author kouyi
     */
    @RequestMapping("/shopchange/qiepiao")
    @ModuleAuthorityRequired(mcode = "btn_shopchupiao_change_qp")
    public void changeQieShopPiao(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(ticketService.changeTicket(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","切票成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "切票失败");
            }
        }
        catch(Exception e)
        {
            logger.error("切票出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 实体店查询出票商列表
     * @author kouyi
     */
    @RequestMapping("/shopvote/list")
    public void getShopVoteList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = ticketService.queryTicketVoteList(params);//查询出票商信息
            for(Dto dt : dataList) {
                dt.put("key", getSafeKey(dt.getAsString("key")));
            }
            Dto dataDto = new BaseDto("list",dataList);
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询出票商列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * key隐藏
     * @param key
     * @return
     */
    public String getSafeKey(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        if (key.length() <= 2) {
            return key;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(key.substring(0, 2));
        sb.append("**");
        sb.append(key.substring(key.length() - 2, key.length()));
        return sb.toString();
    }

    /**
     * 实体店-查询方案首页
     * @author  kouyi
     */
    @RequestMapping("/shopschemequery/index")
    @ModuleAuthorityRequired(mcode = "menu_shopscheme_query")
    public String shopSchemeQueryIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-3);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        params.put("beginTime", DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
        map.addAttribute("params",params);
        return "ticket/shopschemequery/index";
    }

    /**
     * 查询用户方案
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_shopscheme_query")
    public void getShopSchemes(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dto = SessionUtil.getCurrentAccount(request);
            String loginName = dto.getAsString("accountName");
            Map<String, String> voteMaps = SysConfig.getShopTicketVoteId();
            if(voteMaps.containsKey(loginName)) {
                params.put("ticketVoteId", voteMaps.get(loginName));
            }else {
                params.put("ticketVoteId", "00000");
            }

            params.put("minSchemeStatus", "1");
            params.put("userType", 0);
            Dto dataDto = new BaseDto("list",schemeService.queryUserSchemes(params));//查询用户方案
            Dto countDto = schemeService.queryUserSchemesCount(params);//查询用户方案总计
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",countDto.get("tsize"));//设置用户方案总记录条数
            }
            dataDto.put("tmoney",countDto.get("tmoney"));//设置用户方案总金额
            dataDto.put("tpaymoney",countDto.get("tpaymoney"));//设置用户方案实际支付总金额
            dataDto.put("tprize",countDto.get("tprize"));//设置用户方案中奖总金额
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[查询用户方案]发生异常!异常信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 显示用户方案详细
     * @author  mcdog
     */
    @RequestMapping("/detail")
    @ModuleAuthorityRequired(mcode = "menu_shopscheme_query")
    public String initSchemeDetail(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> schemeList = schemeService.queryUserSchemes(params);
        if(schemeList != null && schemeList.size() > 0)
        {
            Dto schemeDto = schemeList.get(0);
            schemeDto.put("schemeTypeDesc",SchemeConstants.schemeTypesMap.get(schemeDto.getAsInteger("schemeType")));
            if(StringUtil.isNotEmpty(schemeDto.get("couponId")))
            {
                Dto couponParams = new BaseDto();
                couponParams.put("cuid",schemeDto.get("couponId"));
                couponParams.put("userId",schemeDto.get("userId"));
                List<Dto> couponList = schemeService.queryUserCoupons(couponParams);
                if(couponList != null && couponList.size() > 0)
                {
                    schemeDto.put("coupon",couponList.get(0));
                }
            }
            schemeDto.put("schemeType","1".equals(params.getAsString("onlyZh"))?
                    SchemeConstants.SCHEME_TYPE_PT : schemeDto.get("schemeType"));//如果本身查询的具体追期方案,则将追期方案类型变为普通
            schemeService.settingSchemeDetail(schemeDto,schemeDto);
            map.addAttribute("params",schemeDto);
        }
        return "ticket/shopschemequery/detail";
    }
}