package com.caipiao.admin.weihu.erroraward.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.service.setting.role.RoleService;
import com.caipiao.admin.service.weihu.erroraward.ErrorAwardService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.util.DateUtil;
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
 * 审核错误重新计奖控制类
 */
@Controller
@RequestMapping("/weihu")
public class ErrorAwardController
{
    private static final Logger logger = LoggerFactory.getLogger(ErrorAwardController.class);

    @Autowired
    private ErrorAwardService errorAwardService;

    /**
     * 显示重新计奖首页
     * @author  mcdog
     */
    @RequestMapping("/erroraward/index")
    @ModuleAuthorityRequired(mcode = "menu_weihu_erroraward")
    public String index(HttpServletRequest request, HttpServletResponse response)
    {
        return "weihu/erroraward/index";
    }

    /**
     * 审核错误回退重新计奖
     * @author  mcdog
     */
    @RequestMapping("/erroraward/reaward")
    @ModuleAuthorityRequired(mcode = "menu_weihu_erroraward")
    public void errorReaward(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            if(errorAwardService.updateErrorReAward(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","回退重新计奖-处理成功");
            }
            else
            {
                resultDto.put("dmsg","处理订单数据0条");
            }
        }
        catch(Exception e)
        {
            logger.error("回退重新计奖-处理出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

}