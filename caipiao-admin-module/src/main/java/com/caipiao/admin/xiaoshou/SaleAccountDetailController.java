package com.caipiao.admin.xiaoshou;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.base.BaseController;
import com.caipiao.admin.service.user.UserAccountDetailService;
import com.caipiao.admin.util.ConstantUtils;
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

/**
 * 销售管理-用户账户流水-控制类
 */
@Controller
@RequestMapping("/sale/account/detail")
public class SaleAccountDetailController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(SaleAccountDetailController.class);

    @Autowired
    private UserAccountDetailService userAccountDetailService;

    /**
     * 显示用户账户流水首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_accountdetail")
    public String initIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);
        map.addAttribute("params",params);
        return "sale/accountdetail/index";
    }

    /**
     * 查询用户账户流水
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_accountdetail")
    public void getUserRecharge(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode", ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            params.put("xsdlFlag","1");//设置销售代理查询标识
            params.put("xsdlMobile",params.get("opaccountName"));
            Dto dataDto = new BaseDto("list",userAccountDetailService.queryUserAccountDetailInfo(params));//查询数据
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",userAccountDetailService.queryUserAccountDetailCount(params));//查询数据记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[查询用户账户流水]发生异常,异常信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}