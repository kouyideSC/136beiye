package com.caipiao.admin.weihu.params.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.service.ticket.TicketService;
import com.caipiao.admin.service.weihu.params.ParamsService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.lottery.LotteryUtils;
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
import java.util.List;
import java.util.Map;

/**
 * 配置参数控制类
 * Created by Kouyi on 2018/01/31.
 */
@Controller
@RequestMapping("/weihu")
public class ParamsController
{
    private static final Logger logger = LoggerFactory.getLogger(ParamsController.class);
    @Autowired
    private ParamsService paramsService;

    /**
     * 显示参数表首页
     * @author  kouyi
     */
    @RequestMapping("/params/index")
    @ModuleAuthorityRequired(mcode = "menu_weihu_params")
    public String paramsIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "weihu/params/index";
    }

    /**
     * 显示参数表新增页面
     * @author kouyi
     */
    @RequestMapping("/params/initAdd")
    @ModuleAuthorityRequired(mcode = "btn_weihu_params_add")
    public String initParamsAdd(HttpServletRequest request, HttpServletResponse response)
    {
        return "weihu/params/add";
    }

    /**
     * 新增参数表
     * @author kouyi
     */
    @RequestMapping("/params/add")
    @ModuleAuthorityRequired(mcode = "btn_weihu_params_add")
    public void addParams(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(paramsService.saveParams(params) > 0)
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
            logger.error("新增参数表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 删除参数表
     * @author kouyi
     */
    @RequestMapping("/params/delete")
    @ModuleAuthorityRequired(mcode = "btn_weihu_params_delete")
    public void deleteParams(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(paramsService.deleteParams(params) > 0)
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
            logger.error("删除参数表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询参数表列表
     * @author kouyi
     */
    @RequestMapping("/params/list")
    @ModuleAuthorityRequired(mcode = "menu_weihu_params")
    public void getParamsList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<BaseDto> dataList = paramsService.queryParamsList(params);//查询参数表信息
            Dto dataDto = new BaseDto("list",dataList);
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询参数表列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询参数表信息
     * @author kouyi
     */
    @RequestMapping("/params/detail")
    @ModuleAuthorityRequired(mcode = "menu_weihu_params")
    public String getParamsDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<BaseDto> paramsList = paramsService.queryParamsList(params);
        if(paramsList != null && paramsList.size() > 0)
        {
            map.addAttribute("params",paramsList.get(0));
        }
        return "weihu/params/edit";
    }

    /**
     * 更新参数表信息
     * @author kouyi
     */
    @RequestMapping("/params/edit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_params_edit")
    public void editParamsInfo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(paramsService.updateParams(params) > 0)
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
            logger.error("编辑参数表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}