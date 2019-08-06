package com.caipiao.admin.xiaoshou;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.base.BaseController;
import com.caipiao.admin.service.user.SchemeService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.scheme.SchemeUtils;
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
 * 销售管理-用户方案-控制类
 */
@Controller
@RequestMapping("/sale/scheme")
public class SaleSchemeController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(SaleSchemeController.class);

    @Autowired
    private SchemeService schemeService;

    /**
     * 显示用户方案首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    public String initIndex(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);
        map.addAttribute("params",params);
        return "sale/scheme/index";
    }

    /**
     * 显示用户方案详细页
     * @author  mcdog
     */
    @RequestMapping("/detail")
    public String initSchemeDetail(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        params.put("xsdlFlag","1");//设置销售代理查询标识
        params.put("xsdlMobile",params.get("opaccountName"));
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
            schemeService.settingSchemeDetail(schemeDto,schemeDto);
            map.addAttribute("params",schemeDto);
        }
        return "sale/scheme/detail";
    }

    /**
     * 查询用户方案
     * @author  mcdog
     */
    @RequestMapping("/get")
    public void getUserSchemes(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            params.put("xsdlFlag","1");//设置销售代理查询标识
            params.put("xsdlMobile",params.get("opaccountName"));
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
     * 查询用户方案追号信息
     * @author  mcdog
     */
    @RequestMapping("/zh/get")
    public void getUserSchemeZh(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            params.put("xsdlFlag","1");//设置销售代理查询标识
            params.put("xsdlMobile",params.get("opaccountName"));
            Dto dataDto = new BaseDto("list",schemeService.querySchemeZhuihaoInfo(params));//查询用户方案
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[查询用户方案追号信息]发生异常,异常信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}