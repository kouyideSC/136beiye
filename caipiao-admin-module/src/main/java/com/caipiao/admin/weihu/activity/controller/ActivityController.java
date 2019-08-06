package com.caipiao.admin.weihu.activity.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.base.BaseController;
import com.caipiao.admin.service.config.SysConfig;
import com.caipiao.admin.service.weihu.activity.ActivityService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.SessionUtil;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.Activity;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import com.sun.xml.internal.rngom.parse.host.Base;
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
 * 活动-控制类
 */
@Controller
@RequestMapping("/weihu/activity")
public class ActivityController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;

    /**
     * 显示活动维护首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_activity_activity")
    public String index(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        map.put("params",new BaseDto("staticHost", SysConfig.getHostStatic()));//获取静态文件域名
        return "weihu/activity/index";
    }

    /**
     * 显示活动编辑页面
     * @author  mcdog
     */
    @RequestMapping("/initEdit")
    @ModuleAuthorityRequired(mcode = "btn_activity_activity_edit")
    public String initEdit(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Activity> activityList = activityService.queryActivitys(params);
        if(activityList != null && activityList.size() > 0)
        {
            Activity activity = activityList.get(0);
            map.addAttribute("params",activity);
            if(activity.getCouponExpireTime() != null)
            {
                map.addAttribute("data",new BaseDto("couponExpireTime",DateUtil.formatDate(activity.getCouponExpireTime(),DateUtil.DEFAULT_DATE_TIME)));
            }
        }
        return "weihu/activity/edit";
    }

    /**
     * 显示活动新增页面
     * @author  mcdog
     */
    @RequestMapping("/initAdd")
    @ModuleAuthorityRequired(mcode = "btn_activity_activity_add")
    public String initAdd(HttpServletRequest request, HttpServletResponse response)
    {
        return "weihu/activity/add";
    }

    /**
     * 显示活动详细页面
     * @author  mcdog
     */
    @RequestMapping("/initDetail")
    @ModuleAuthorityRequired(mcode = "menu_activity_activity")
    public String initDetail(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Activity> activityList = activityService.queryActivitys(params);
        if(activityList != null && activityList.size() > 0)
        {
            Activity activity = activityList.get(0);
            map.addAttribute("params",activity);
            if(activity.getCouponExpireTime() != null)
            {
                map.addAttribute("data",new BaseDto("couponExpireTime",DateUtil.formatDate(activity.getCouponExpireTime(),DateUtil.DEFAULT_DATE_TIME)));
            }
        }
        return "weihu/activity/detail";
    }

    /**
     * 查询活动信息
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_activity_activity")
    public void getActivitys(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",activityService.queryActivitys(params));//查询活动信息
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",activityService.queryActivitysCount(params));//查询活动总记录数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[查询活动信息]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto, DateUtil.DEFAULT_DATE_TIME).toString(),response);
    }

    /**
     * 编辑活动
     * @author  mcdog
     */
    @RequestMapping("/edit")
    @ModuleAuthorityRequired(mcode = "btn_activity_activity_edit")
    public void editActivity(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[编辑活动]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数=" + params.toString());
            if(activityService.editActivity(params) > 0)
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
            logger.error("[编辑活动]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 新增活动
     * @author  mcdog
     */
    @RequestMapping("/add")
    @ModuleAuthorityRequired(mcode = "btn_activity_activity_add")
    public void addActivity(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[新增活动]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数=" + params.toString());
            if(activityService.addActivity(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","添加成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[新增活动]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 删除活动
     * @author  mcdog
     */
    @RequestMapping("/delete")
    @ModuleAuthorityRequired(mcode = "btn_activity_activity_delete")
    public void deletePeriods(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[删除活动]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数:" + params.toString());
            if(activityService.deleteActivity(params) > 0)
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
            logger.error("[删除活动]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 上传活动logo
     * @author  mcdog
     */
    @RequestMapping("/uploadLogo")
    public void uploadLogo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);
        try
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> mfileMap = multipartRequest.getFileMap();
            if(mfileMap != null && mfileMap.size() > 0 && mfileMap.get("uplogo") != null)
            {
                //初始化文件上传参数
                MultipartFile multipartFile = mfileMap.get("uplogo");
                String fileName = multipartFile.getOriginalFilename();//提取文件名
                String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);//提取文件后缀
                String staticPath = SysConfig.getString("static.file.path");//从配置文件中提取静态文件根路径
                String fpath = SysConfig.getString("weihu.activity.logo.path");//从配置文件中提取活动logo上传路径

                //上传文件
                File file = new File(staticPath + fpath);
                if(!file.exists())
                {
                    file.mkdirs();
                }
                String fname = System.currentTimeMillis() + "." + fileSuffix;
                fpath = fpath + fname;
                multipartFile.transferTo(new File(staticPath + fpath));
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("datas",new BaseDto("fpath",fpath));//设置文件路径
            }
        }
        catch (Exception e)
        {
            resultDto.put("dmsg","上传logo发生异常,异常信息:" + e);
            logger.error("[上传logo]发生异常,异常信息:" + e);
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 文件上传
     * @author  mcdog
     */
    @RequestMapping("/upload")
    public void uploadFile(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto();
        try
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> mfileMap = multipartRequest.getFileMap();
            if(mfileMap != null && mfileMap.size() > 0 && mfileMap.get("upfile") != null)
            {
                //初始化文件上传参数
                MultipartFile multipartFile = mfileMap.get("upfile");
                String fileName = multipartFile.getOriginalFilename();//提取文件名
                String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);//提取文件后缀
                String staticPath = SysConfig.getString("static.file.path");//从配置文件中提取静态文件根路径
                String fpath = SysConfig.getString("weihu.activity.content.path");//从配置文件中提取活动内容图片上传路径

                //上传文件
                File file = new File(staticPath + fpath);
                if(!file.exists())
                {
                    file.mkdirs();
                }
                String fname = System.currentTimeMillis() + "." + fileSuffix;
                fpath = fpath + fname;
                long size = multipartFile.getSize();
                multipartFile.transferTo(new File(staticPath + fpath));

                //设置返回结果
                resultDto.put("name",fname);//设置上传文件名
                resultDto.put("original",fileName);//设置源文件名
                resultDto.put("size",size);//设置文件大小
                resultDto.put("state","SUCCESS");//设置文件上传状态
                resultDto.put("type",("." + fileSuffix));//设置文件后缀
                resultDto.put("url",fpath);//设置文件路径
            }
        }
        catch (Exception e)
        {
            logger.error("[活动]上传文件发生异常,异常信息:" + e);
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}