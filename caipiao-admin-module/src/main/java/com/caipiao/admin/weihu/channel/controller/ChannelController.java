package com.caipiao.admin.weihu.channel.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.service.config.SysConfig;
import com.caipiao.admin.service.weihu.channel.ChannelService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.Channel;
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
 * 渠道控制类
 * Created by Kouyi on 2018/03/24.
 */
@Controller
@RequestMapping("/weihu")
public class ChannelController
{
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
    @Autowired
    private ChannelService channelService;

    /**
     * 渠道首页
     * @author  kouyi
     */
    @RequestMapping("/channel/index")
    @ModuleAuthorityRequired(mcode = "menu_activity_channel")
    public String channelIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "weihu/channel/index";
    }

    /**
     * 显示渠道添加页面
     * @author kouyi
     */
    @RequestMapping("/channel/initAdd")
    @ModuleAuthorityRequired(mcode = "btn_activity_channel_add")
    public String initChannelAdd(HttpServletRequest request, HttpServletResponse response)
    {
        return "weihu/channel/add";
    }

    /**
     * 添加新渠道
     * @author kouyi
     */
    @RequestMapping("/channel/add")
    @ModuleAuthorityRequired(mcode = "btn_activity_channel_add")
    public void addChannel(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(channelService.insertChannel(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","添加成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "发布失败");
            }
        }
        catch(Exception e)
        {
            logger.error("添加新渠道出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 删除新渠道
     * @author kouyi
     */
    @RequestMapping("/channel/delete")
    @ModuleAuthorityRequired(mcode = "btn_activity_channel_delete")
    public void deleteChannel(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(channelService.deleteChannel(params) > 0)
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
            logger.error("删除新渠道出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询渠道列表
     * @author kouyi
     */
    @RequestMapping("/channel/list")
    @ModuleAuthorityRequired(mcode = "menu_activity_channel")
    public void getchannelVersionList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = channelService.queryChannelDtoList(params);//查询渠道信息
            Dto dataDto = new BaseDto("list",dataList);
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询渠道列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询新渠道信息
     * @author kouyi
     */
    @RequestMapping("/channel/detail")
    @ModuleAuthorityRequired(mcode = "menu_activity_channel")
    public String getchannelDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> dataList = channelService.queryChannelDtoList(params);//查询渠道信息
        if(dataList != null && dataList.size() > 0)
        {
            map.addAttribute("params",dataList.get(0));
        }
        return "weihu/channel/edit";
    }

    /**
     * 更新渠道信息
     * @author kouyi
     */
    @RequestMapping("/channel/edit")
    @ModuleAuthorityRequired(mcode = "btn_activity_channel_edit")
    public void editchannelInfo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            if(channelService.updateChannel(params) > 0)
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
            logger.error("编辑渠道出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

}