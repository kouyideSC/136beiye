package com.caipiao.admin.weihu.txqd.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.base.BaseController;
import com.caipiao.admin.service.weihu.lottery.LotteryService;
import com.caipiao.admin.service.weihu.txqd.TxqdService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.SessionUtil;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 提现渠道-控制类
 */
@Controller
@RequestMapping("/weihu/txqd")
public class TxqdController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(TxqdController.class);

    @Autowired
    private TxqdService txqdService;

    /**
     * 显示提现渠道首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_weihu_txqd")
    public String index()
    {
        return "weihu/txqd/index";
    }

    /**
     * 显示提现渠道设置规则页
     * @author  mcdog
     */
    @RequestMapping("/initRule")
    @ModuleAuthorityRequired(mcode = "btn_weihu_txqd_edit")
    public String initRule(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> paymentWayList = txqdService.queryPaymentWayInfo(params);
        if(paymentWayList != null && paymentWayList.size() > 0)
        {
            map.addAttribute("params",paymentWayList.get(0));
        }
        return "weihu/txqd/rule";
    }

    /**
     * 查询提现渠道信息
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_weihu_txqd")
    public void getRecharges(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",txqdService.queryPaymentWayInfo(params));//查询提现渠道信息
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",txqdService.queryPaymentWayInfoCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询提现渠道信息,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 修改提现渠道启用状态
     * @author  mcdog
     */
    @RequestMapping("/editStatus")
    @ModuleAuthorityRequired(mcode = "btn_weihu_txqd_edit")
    public void editStatus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[修改提现渠道启用状态]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数=" + params.toString());
            if("0".equals(params.getAsString("model")))
            {
                params.remove("timeRangeStart");
                params.remove("timeRangeEnd");
                params.remove("timeCharacter");
            }
            else if("1".equals(params.getAsString("model")))
            {
                params.remove("timeCharacter");
            }
            else if("2".equals(params.getAsString("model")))
            {
                params.remove("timeRangeStart");
                params.remove("timeRangeEnd");
            }
            if(txqdService.editTxqd(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","修改成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("修改提现渠道启用状态发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 设置提现渠道规则
     * @author  mcdog
     */
    @RequestMapping("/setrule")
    @ModuleAuthorityRequired(mcode = "btn_weihu_txqd_edit")
    public void editRate(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[设置提现渠道规则]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数=" + params.toString());
            if(txqdService.editTxqd(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","设置成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("设置提现渠道规则,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}