package com.caipiao.admin.weihu.market.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.service.config.SysConfig;
import com.caipiao.admin.service.weihu.market.MarketService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.AppMarket;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import com.caipiao.memcache.MemCached;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 市场版本控制类
 * Created by Kouyi on 2017/12/01.
 */
@Controller
@RequestMapping("/weihu")
public class MarketController
{
    private static final Logger logger = LoggerFactory.getLogger(MarketController.class);
    @Autowired
    private MemCached memcache;
    @Autowired
    private MarketService marketService;

    /**
     * 显示版本更新首页
     * @author  kouyi
     */
    @RequestMapping("/market/index")
    @ModuleAuthorityRequired(mcode = "menu_weihu_market")
    public String marketIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "weihu/market/index";
    }

    /**
     * 显示版本发布页面
     * @author kouyi
     */
    @RequestMapping("/market/initAdd")
    @ModuleAuthorityRequired(mcode = "btn_weihu_market_fbxbb")
    public String initMarketAdd(HttpServletRequest request, HttpServletResponse response)
    {
        return "weihu/market/add";
    }

    /**
     * 发布新版本
     * @author kouyi
     */
    @RequestMapping("/market/add")
    @ModuleAuthorityRequired(mcode = "btn_weihu_market_fbxbb")
    public void addMarket(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(marketService.insertMarketVersion(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","发布成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "发布失败");
            }
        }
        catch(Exception e)
        {
            logger.error("发布市场新版本出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 删除新版本
     * @author kouyi
     */
    @RequestMapping("/market/delete")
    @ModuleAuthorityRequired(mcode = "btn_weihu_market_delete")
    public void deleteMarket(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(marketService.deleteMarketVersion(params) > 0)
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
            logger.error("删除市场新版本出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询新版本列表
     * @author kouyi
     */
    @RequestMapping("/market/list")
    @ModuleAuthorityRequired(mcode = "menu_weihu_market")
    public void getMarketVersionList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<AppMarket> dataList = marketService.queryMarketVersionList(params);//查询新版本信息
            if(StringUtil.isNotEmpty(dataList)) {
                for(AppMarket market : dataList) {
                    if(market.getClientType()==1) {
                        market.setDownUrl(SysConfig.getHostStatic() + market.getDownUrl());
                    }
                }
            }
            Dto dataDto = new BaseDto("list",dataList);
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询市场新版本列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询新版本信息
     * @author kouyi
     */
    @RequestMapping("/market/detail")
    @ModuleAuthorityRequired(mcode = "menu_weihu_market")
    public String getMarketDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<AppMarket> marketList = marketService.queryMarketVersionList(params);
        if(marketList != null && marketList.size() > 0)
        {
            map.addAttribute("params",marketList.get(0));
        }
        return "weihu/market/edit";
    }

    /**
     * 更新市场新版本信息
     * @author kouyi
     */
    @RequestMapping("/market/edit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_market_edit")
    public void editMarketInfo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            if(marketService.updateMarketVersion(params) > 0)
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
            logger.error("编辑市场新版本出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 获取市场下拉数据
     * @author kouyi
     */
    @RequestMapping("/market/getMarketList")
    public void getMarketList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list", marketService.queryMarketList());
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("获取市场下拉列表获取出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 上传安装包
     * @author  kouyi
     * @return  fpath   logo上传后的文件路径(包含文件名)
     */
    @RequestMapping("/market/uploadApk")
    private void uploadApk(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> mfileMap = multipartRequest.getFileMap();
            if(mfileMap != null && mfileMap.size() > 0 && mfileMap.get("uplogo") != null)
            {
                //初始化文件上传参数
                MultipartFile multipartFile = mfileMap.get("uplogo");
                String fileName = multipartFile.getOriginalFilename();//提取文件名
                String staticPath = SysConfig.getString("static.file.path");//从配置文件中提取静态文件根路径
                String fpath = SysConfig.getString("apk.install.path");//从配置文件中提取安装包上传路径

                Integer type = params.getAsInteger("clientType");
                if(StringUtil.isNotEmpty(type)) {
                    String client = "android/";
                    if(type == 0) {
                        client = "IOS/";
                    }
                    //上传文件
                    File file = new File(staticPath + fpath + client);
                    if(!file.exists())
                    {
                        file.mkdirs();
                    }
                    fpath = fpath + client + fileName;
                    multipartFile.transferTo(new File(staticPath + fpath));
                    resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                    resultDto.put("datas",new BaseDto("fpath",fpath));//设置文件路径
                } else {
                    resultDto.put("dmsg","请选择一个客户端");
                }
            }
        }
        catch (Exception e)
        {
            resultDto.put("dmsg","上传logo发生异常,异常信息:" + e);
            logger.error("[上传logo]发生异常,异常信息:" + e);
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}