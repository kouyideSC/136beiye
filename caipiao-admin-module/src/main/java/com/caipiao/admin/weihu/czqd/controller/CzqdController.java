package com.caipiao.admin.weihu.czqd.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.base.BaseController;
import com.caipiao.admin.service.weihu.czqd.CzqdService;
import com.caipiao.admin.service.weihu.lottery.LotteryService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 充值渠道-控制类
 */
@Controller
@RequestMapping("/weihu/czqd")
public class CzqdController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(CzqdController.class);

    private static Map<String,String> clientTypeMaps = new HashMap<String,String>();//充值方式开放客户端集合

    @Autowired
    private CzqdService czqdService;

    static
    {
        clientTypeMaps.put("-1","所有");
        clientTypeMaps.put("0","web");
        clientTypeMaps.put("1","ios");
        clientTypeMaps.put("2","android");
        clientTypeMaps.put("3","h5");
    }

    /**
     * 显示充值渠道首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_weihu_czqd")
    public String index()
    {
        return "weihu/czqd/index";
    }

    /**
     * 显示渠道充值方式编辑页
     * @author  mcdog
     */
    @RequestMapping("/czfs/initEdit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_czqd_edit")
    public String edit(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> channelPaywayList = czqdService.queryChannelPaywayInfos(params);//查询渠道充值方式信息
        if(channelPaywayList != null && channelPaywayList.size() > 0)
        {
            map.addAttribute("params",channelPaywayList.get(0));
        }
        return "weihu/czqd/czfsedit";
    }

    /**
     * 显示充值渠道详细页
     * @author  mcdog
     */
    @RequestMapping("/detail")
    @ModuleAuthorityRequired(mcode = "menu_weihu_czqd")
    public String detail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> paychannelList = czqdService.queryPayChannelInfos(params);
        if(paychannelList != null && paychannelList.size() > 0)
        {
            Dto datas = new BaseDto();
            datas.putAll(paychannelList.get(0));
            List<Dto> czfslist = czqdService.queryChannelPaywayInfos(new BaseDto("paychannelId",params.get("id")));//查询该渠道已配置的充值方式信息
            if(czfslist != null && czfslist.size() > 0)
            {
                for(Dto czfs : czfslist)
                {
                    if(StringUtil.isNotEmpty(czfs.get("clientTypes")))
                    {
                        String[] clientTypes = czfs.getAsString("clientTypes").split(",");
                        String clientTypestr = "";
                        for(String clientType : clientTypes)
                        {
                            clientTypestr += "," + clientTypeMaps.get(clientType);
                        }
                        clientTypestr = clientTypestr.substring(1);
                        czfs.put("clientTypes",clientTypestr);
                    }
                    czfs.put("minMoney",StringUtil.isEmpty(czfs.get("minMoney"))? "--" : czfs.get("minMoney"));
                    czfs.put("maxMoney",StringUtil.isEmpty(czfs.get("maxMoney"))? "--" : czfs.get("maxMoney"));
                }
            }
            datas.put("czfslist",czfslist);//设置该渠道已配置的充值方式信息
            map.addAttribute("params",datas);
        }
        return "weihu/czqd/detail";
    }

    /**
     * 查询充值渠道信息
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_weihu_czqd")
    public void getPayChannel(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",czqdService.queryPayChannelInfos(params));//查询充值渠道信息
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",czqdService.queryPayChannelInfosCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[查询充值渠道信息]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询渠道充值方式信息
     * @author  mcdog
     */
    @RequestMapping("/czfs/get")
    @ModuleAuthorityRequired(mcode = "menu_weihu_czqd")
    public void getChannelPayWay(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",czqdService.queryChannelPaywayInfos(params));//查询渠道充值方式信息
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[查询渠道充值方式信息]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 启用/关闭充值渠道
     * @author  mcdog
     */
    @RequestMapping("/status/edit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_czqd_edit")
    public void editStatus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[启用/关闭充值渠道]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(czqdService.updatePayChannelForStatus(params) > 0)
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
            logger.error("[启用/关闭充值渠道]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 编辑渠道充值方式
     * @author  mcdog
     */
    @RequestMapping("/czfs/edit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_czqd_edit")
    public void editQdCzfs(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[编辑渠道充值方式]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(czqdService.updateChannelPayway(params) > 0)
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
            logger.error("[编辑渠道充值方式]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 启用/关闭渠道充值方式
     * @author  mcdog
     */
    @RequestMapping("/czfs/status/edit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_czqd_edit")
    public void editQdCzfsStatus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[启用/关闭渠道充值方式]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(czqdService.updateChannelPaywayForStatus(params) > 0)
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
            logger.error("[启用/关闭渠道充值方式]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}