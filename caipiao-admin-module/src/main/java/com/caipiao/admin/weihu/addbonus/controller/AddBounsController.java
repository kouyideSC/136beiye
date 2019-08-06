package com.caipiao.admin.weihu.addbonus.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.service.weihu.activity.ActivityService;
import com.caipiao.admin.service.weihu.addbouns.AddBounsService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.ActivityUser;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 加奖活动控制类
 * Created by Kouyi on 2018/03/26.
 */
@Controller
@RequestMapping("/weihu")
public class AddBounsController
{
    private static final Logger logger = LoggerFactory.getLogger(AddBounsController.class);
    @Autowired
    private AddBounsService addBounsService;
    @Autowired
    private ActivityService activityService;

    /**
     * 加奖活动首页
     * @author  kouyi
     */
    @RequestMapping("/addbonus/index")
    @ModuleAuthorityRequired(mcode = "menu_activity_addbonus")
    public String addbonusIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "weihu/addbonus/index";
    }

    /**
     * 加奖活动添加页面
     * @author kouyi
     */
    @RequestMapping("/addbonus/initAdd")
    @ModuleAuthorityRequired(mcode = "btn_activity_addbonus_add")
    public String initAddbonusAdd(HttpServletRequest request, HttpServletResponse response)
    {
        return "weihu/addbonus/add";
    }

    /**
     * 添加新加奖活动
     * @author kouyi
     */
    @RequestMapping("/addbonus/add")
    @ModuleAuthorityRequired(mcode = "btn_activity_addbonus_add")
    public void addAddbonus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(addBounsService.insertAddBonus(params) > 0)
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
            logger.error("添加新加奖活动出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 删除新加奖活动
     * @author kouyi
     */
    @RequestMapping("/addbonus/delete")
    @ModuleAuthorityRequired(mcode = "btn_activity_addbonus_delete")
    public void deleteAddbonus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(addBounsService.deleteAddBouns(params) > 0)
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
            logger.error("删除加奖活动出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询加奖活动列表
     * @author kouyi
     */
    @RequestMapping("/addbonus/list")
    @ModuleAuthorityRequired(mcode = "menu_activity_addbonus")
    public void getAddbonusVersionList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = addBounsService.queryAddBonusDtoList(params);//查询加奖活动信息
            if(StringUtil.isNotEmpty(dataList)) {
                for(Dto to : dataList) {
                    to.put("lotteryLimit", to.getAsString("lotteryLimit").replaceAll("1700","竞彩足球").replaceAll("1710","竞彩篮球"));
                    to.put("passType", to.getAsString("passType").replaceAll("1\\*1","单关").replaceAll("\\*","串"));
                }
            }
            Dto dataDto = new BaseDto("list",dataList);
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询加奖活动列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询新加奖活动信息
     * @author kouyi
     */
    @RequestMapping("/addbonus/detail")
    @ModuleAuthorityRequired(mcode = "menu_activity_addbonus")
    public String getAddbonusDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> dataList = addBounsService.queryAddBonusDtoList(params);//查询加奖活动信息
        if(dataList != null && dataList.size() > 0)
        {
            map.addAttribute("params",dataList.get(0));
        }
        return "weihu/addbonus/edit";
    }

    /**
     * 更新加奖活动信息
     * @author kouyi
     */
    @RequestMapping("/addbonus/edit")
    @ModuleAuthorityRequired(mcode = "btn_activity_addbonus_edit")
    public void editAddbonusInfo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            if(addBounsService.updateAddBonus(params) > 0)
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
            logger.error("编辑加奖活动出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询加奖活动参与人数
     * @author kouyi
     */
    @RequestMapping("/addbonus/getuser")
    @ModuleAuthorityRequired(mcode = "menu_activity_addbonus")
    public String getAddbonusGetUser(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> dataList = activityService.queryActivityUserList(params);//查询加奖活动参与人数
        map.addAttribute("params",dataList);
        return "weihu/addbonus/getuser";
    }

    /**
     * 后台手工领取活动
     * @author kouyi
     */
    @RequestMapping("/addbonus/binding")
    @ModuleAuthorityRequired(mcode = "btn_activity_addbonus_add")
    public void bindingAddbonus(ActivityUser activityUser, HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            int re = addBounsService.insertActivityUser(activityUser);
            if(re > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","领取成功");
            }
            else
            {
                String msg = "领取失败";
                if(re == -1) {
                    msg = "重复领取活动资格";
                } else if(re == -2) {
                    msg = "用户已被冻结或注销,请联系客服";
                } else if(re == -3) {
                    msg = "活动已过期或无效";
                }
                resultDto.put("dmsg", msg);
            }
        }
        catch(Exception e)
        {
            logger.error("后台手工领取活动出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

}