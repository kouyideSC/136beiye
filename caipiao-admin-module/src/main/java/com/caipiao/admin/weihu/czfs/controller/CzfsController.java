package com.caipiao.admin.weihu.czfs.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.base.BaseController;
import com.caipiao.admin.service.weihu.cqfs.CzfsService;
import com.caipiao.admin.service.weihu.czqd.CzqdService;
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
 * 充值方式-控制类
 */
@Controller
@RequestMapping("/weihu/czfs")
public class CzfsController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(CzfsController.class);

    @Autowired
    private CzfsService czfsService;

    /**
     * 显示充值方式首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_weihu_czfs")
    public String index()
    {
        return "weihu/czfs/index";
    }

    /**
     * 显示充值方式编辑页
     * @author  mcdog
     */
    @RequestMapping("/initEdit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_czfs_edit")
    public String edit(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> paywayList = czfsService.queryPayWayInfos(params);
        if(paywayList != null && paywayList.size() > 0)
        {
            map.addAttribute("params",paywayList.get(0));
        }
        return "weihu/czfs/edit";
    }

    /**
     * 查询充值方式信息
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_weihu_czfs")
    public void getRecharges(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",czfsService.queryPayWayInfos(params));//查询充值渠道信息
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",czfsService.queryPaymentWayInfoCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询充值渠道信息发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 修改充值方式
     * @author  mcdog
     */
    @RequestMapping("/edit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_czfs_edit")
    public void editStatus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[修改充值渠道]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数=" + params.toString());
            if(czfsService.editPayway(params) > 0)
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
            logger.error("修改充值渠道,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}