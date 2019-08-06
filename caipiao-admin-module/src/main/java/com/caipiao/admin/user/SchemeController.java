package com.caipiao.admin.user;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.base.BaseController;
import com.caipiao.admin.service.user.SchemeService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.scheme.SchemeUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;

/**
 * 用户方案-控制类
 */
@Controller
@RequestMapping("/user/scheme")
public class SchemeController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(SchemeController.class);

    @Autowired
    private SchemeService schemeService;

    /**
     * 显示用户方案首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_order_scheme")
    public String initIndex(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-3);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        params.put("minCreateTime", DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
        map.addAttribute("params",params);
        return "user/scheme/index";
    }

    /**
     * 显示派奖审核首页
     * @author  mcdog
     */
    @RequestMapping("/pjsh")
    @ModuleAuthorityRequired(mcode = "menu_order_schemepjsh")
    public String initPjsh(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-2);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        params.put("minOpenTime", DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
        params.put("minCreateDate", DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE1));
        map.addAttribute("params",params);
        return "user/scheme/pjsh";
    }

    /**
     * 显示方案撤单首页
     * @author  mcdog
     */
    @RequestMapping("/schemecd")
    @ModuleAuthorityRequired(mcode = "menu_order_schemecd")
    public String initSchemeCd()
    {
        return "user/scheme/schemecd";
    }

    /**
     * 显示用户方案详细
     * @author  mcdog
     */
    @RequestMapping("/detail")
    @ModuleAuthorityRequired(mcode = {"menu_order_scheme","menu_order_schemepjsh","menu_order_schemecd"},mflag = 1)
    public String initSchemeDetail(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
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
            schemeDto.put("schemeType","1".equals(params.getAsString("onlyZh"))?
                    SchemeConstants.SCHEME_TYPE_PT : schemeDto.get("schemeType"));//如果本身查询的具体追期方案,则将追期方案类型变为普通
            schemeService.settingSchemeDetail(schemeDto,schemeDto);
            map.addAttribute("params",schemeDto);
        }
        return "user/scheme/detail";
    }

    /**
     * 查询用户方案
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = {"menu_order_scheme","menu_order_schemepjsh","menu_order_schemecd"},mflag = 1)
    public void getUserSchemes(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
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
    @ModuleAuthorityRequired(mcode = {"menu_order_scheme","menu_order_schemepjsh","menu_order_schemecd"},mflag = 1)
    public void getUserSchemeZh(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",schemeService.querySchemeZhuihaoInfo(params));//查询用户方案
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[查询用户方案追号信息]发生异常!异常信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 方案撤单
     * @author  mcdog
     */
    @RequestMapping("/cancel")
    @ModuleAuthorityRequired(mcode = {"btn_order_scheme_cd","btn_order_schemecd_cd"},mflag = 1)
    public void cancelScheme(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[方案撤单]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            JSONArray cdinfosArray = JSONArray.fromObject(params.get("cdinfos"));
            if(cdinfosArray != null && cdinfosArray.size() > 0)
            {
                int cdcount = 0;
                String errmsg = "";
                Dto cdinfoDto = new BaseDto("opfullName",params.get("opfullName"));
                for(Object object : cdinfosArray)
                {
                    JSONObject jsonObject = JSONObject.fromObject(object);
                    cdinfoDto.put("id", jsonObject.get("id"));
                    cdinfoDto.put("iszh",jsonObject.get("iszh"));
                    cdinfoDto.put("iscontinue",jsonObject.get("iscontinue"));
                    int count = schemeService.updateSchemeForCancel(cdinfoDto);
                    cdcount += count;
                    if(count <= 0)
                    {
                        errmsg += "[" + cdinfoDto.getAsString("schemeOrderId") + "]" + cdinfoDto.getAsString("dmsg") + "<br/>";
                    }
                }
                if(cdcount > 0)
                {
                    if(cdinfosArray.size() == 1)
                    {
                        resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                        resultDto.put("dmsg","撤单成功");
                    }
                    else
                    {
                        if(cdcount == cdinfosArray.size())
                        {
                            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                            resultDto.put("dmsg","批量撤单成功");
                        }
                        else
                        {
                            resultDto.put("dmsg","部分方案撤单失败!其中成功" + cdcount + "个,失败" + (cdinfosArray.size() - cdcount) + "个,详细信息:<br/>" + errmsg);
                        }
                    }
                }
                else
                {
                    if(cdinfosArray.size() > 1)
                    {
                        resultDto.put("dmsg","批量撤单失败!详细信息:<br/>" + errmsg);
                    }
                    else
                    {
                        resultDto.put("dcode",StringUtil.isEmpty(cdinfoDto.get("dcode"))? resultDto.get("dcode") : cdinfoDto.get("dcode"));
                        resultDto.put("dmsg",StringUtil.isEmpty(cdinfoDto.get("dmsg"))? "撤单失败" : cdinfoDto.get("dmsg"));
                    }
                }
            }
            else
            {
                resultDto.put("dmsg","解析不到任何有效的方案信息!");
            }
        }
        catch(Exception e)
        {
            logger.error("[方案撤单]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 追号方案撤单
     * @author  mcdog
     */
    @RequestMapping("/zh/cancel")
    @ModuleAuthorityRequired(mcode = {"btn_order_scheme_cd","btn_order_schemecd_cd"},mflag = 1)
    public void cancelSchemeZh(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[追号方案撤单]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            params.put("iszh",1);//设置追号方案撤单标识
            if(schemeService.updateSchemeForCancel(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","追号方案撤单成功");
            }
            else
            {
                resultDto.put("dcode",StringUtil.isEmpty(params.get("dcode"))? resultDto.get("dcode") : params.get("dcode"));
                resultDto.put("dmsg",StringUtil.isEmpty(params.get("dmsg"))? "追号方案撤单失败" : params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[追号方案撤单]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 审核大单
     * @author  mcdog
     */
    @RequestMapping("/auditBigOrder")
    @ModuleAuthorityRequired(mcode = "btn_order_scheme_shdd")
    public void auditBigOrder(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[审核大单]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(schemeService.updateBigOrderForAudit(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","审核大单成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[审核大单]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 方案退款
     * @author  mcdog
     */
    @RequestMapping("/tk")
    @ModuleAuthorityRequired(mcode = {"btn_order_scheme_fatk","btn_order_schemecd_fatk"},mflag = 1)
    public void schemeTk(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[方案退款]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(schemeService.updateSchemeForTk(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","方案退款成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[方案退款]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 设置方案出票成功
     * @author  mcdog
     */
    @RequestMapping("/cpcg")
    @ModuleAuthorityRequired(mcode = "btn_order_schemecd_szcpcg")
    public void schemeCpcg(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[设置方案出票成功]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            JSONArray cdinfosArray = JSONArray.fromObject(params.get("cdinfos"));
            if(cdinfosArray != null && cdinfosArray.size() > 0)
            {
                int cdcount = 0;
                String errmsg = "";
                Dto cdinfoDto = new BaseDto("opfullName",params.get("opfullName"));
                for(Object object : cdinfosArray)
                {
                    JSONObject jsonObject = JSONObject.fromObject(object);
                    cdinfoDto.put("id", jsonObject.get("id"));
                    cdinfoDto.put("iszh",jsonObject.get("iszh"));
                    cdinfoDto.put("iscontinue",jsonObject.get("iscontinue"));
                    int count = schemeService.updateSchemeForCpcg(cdinfoDto);
                    cdcount += count;
                    if(count <= 0)
                    {
                        errmsg += "[" + cdinfoDto.getAsString("schemeOrderId") + "]" + cdinfoDto.getAsString("dmsg") + "<br/>";
                    }
                }
                if(cdcount > 0)
                {
                    if(cdinfosArray.size() == 1)
                    {
                        resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                        resultDto.put("dmsg","方案出票成功设置成功");
                    }
                    else
                    {
                        if(cdcount == cdinfosArray.size())
                        {
                            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                            resultDto.put("dmsg","批量方案出票成功设置成功");
                        }
                        else
                        {
                            resultDto.put("dmsg","部分方案出票成功设置失败!其中成功" + cdcount + "个,失败" + (cdinfosArray.size() - cdcount) + "个,详细信息:<br/>" + errmsg);
                        }
                    }
                }
                else
                {
                    if(cdinfosArray.size() > 1)
                    {
                        resultDto.put("dmsg","批量方案出票成功设置失败!详细信息:<br/>" + errmsg);
                    }
                    else
                    {
                        resultDto.put("dcode",StringUtil.isEmpty(cdinfoDto.get("dcode"))? resultDto.get("dcode") : cdinfoDto.get("dcode"));
                        resultDto.put("dmsg",StringUtil.isEmpty(cdinfoDto.get("dmsg"))? "方案出票成功设置失败" : cdinfoDto.get("dmsg"));
                    }
                }
            }
            else
            {
                resultDto.put("dmsg","解析不到任何有效的方案信息!");
            }
        }
        catch(Exception e)
        {
            logger.error("[设置方案出票成功]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 方案重新出票
     * @author  mcdog
     */
    @RequestMapping("/cxcp")
    @ModuleAuthorityRequired(mcode = "btn_order_schemecd_szcxcp")
    public void schemeCxcp(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[方案重新出票]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            JSONArray cdinfosArray = JSONArray.fromObject(params.get("cdinfos"));
            if(cdinfosArray != null && cdinfosArray.size() > 0)
            {
                int cdcount = 0;
                String errmsg = "";
                Dto cdinfoDto = new BaseDto("opfullName",params.get("opfullName"));
                for(Object object : cdinfosArray)
                {
                    JSONObject jsonObject = JSONObject.fromObject(object);
                    cdinfoDto.put("id", jsonObject.get("id"));
                    cdinfoDto.put("iszh",jsonObject.get("iszh"));
                    cdinfoDto.put("iscontinue",jsonObject.get("iscontinue"));
                    int count = schemeService.updateSchemeForCxcp(cdinfoDto);
                    cdcount += count;
                    if(count <= 0)
                    {
                        errmsg += "[" + cdinfoDto.getAsString("schemeOrderId") + "]" + cdinfoDto.getAsString("dmsg") + "<br/>";
                    }
                }
                if(cdcount > 0)
                {
                    if(cdinfosArray.size() == 1)
                    {
                        resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                        resultDto.put("dmsg","方案重新出票成功");
                    }
                    else
                    {
                        if(cdcount == cdinfosArray.size())
                        {
                            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                            resultDto.put("dmsg","批量方案重新出票成功");
                        }
                        else
                        {
                            resultDto.put("dmsg","部分方案重新出票失败!其中成功" + cdcount + "个,失败" + (cdinfosArray.size() - cdcount) + "个,详细信息:<br/>" + errmsg);
                        }
                    }
                }
                else
                {
                    if(cdinfosArray.size() > 1)
                    {
                        resultDto.put("dmsg","批量方案重新出票失败!详细信息:<br/>" + errmsg);
                    }
                    else
                    {
                        resultDto.put("dcode",StringUtil.isEmpty(cdinfoDto.get("dcode"))? resultDto.get("dcode") : cdinfoDto.get("dcode"));
                        resultDto.put("dmsg",StringUtil.isEmpty(cdinfoDto.get("dmsg"))? "方案重新出票失败" : cdinfoDto.get("dmsg"));
                    }
                }
            }
            else
            {
                resultDto.put("dmsg","解析不到任何有效的方案信息!");
            }
        }
        catch(Exception e)
        {
            logger.error("[方案重新出票]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 方案确认派奖
     * @author  mcdog
     */
    @RequestMapping("/qrpj")
    @ModuleAuthorityRequired(mcode = "btn_order_schemepjsh_qrpj")
    public void schemeQrpj(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[方案确认派奖]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数=" + params.toString());
            if(schemeService.updateSchemeForQrPj(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","方案派奖成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[方案确认派奖]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 导出方案
     * @author  mcdog
     */
    @RequestMapping("/export")
    public void schemeExport(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
        }
        catch(Exception e)
        {
            logger.error("[导出方案]发生异常！异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 获取方案类型下拉数据
     * @author  mcdog
     */
    @RequestMapping("/getSchemeTypeCombo")
    public void getSchemeTypeCombo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list", SchemeUtils.getSchemeTypes());//查询方案类型信息
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[获取方案类型下拉数据]发生异常!异常信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 获取方案状态下拉数据
     * @author  mcdog
     */
    @RequestMapping("/getSchemeStatusCombo")
    public void getSchemeStatusCombo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list", SchemeUtils.getSchemeStatus());//查询方案状态信息
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[获取方案状态下拉数据]发生异常!异常信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 获取客户端来源下拉数据
     * @author  mcdog
     */
    @RequestMapping("/getClientSourceCombo")
    public void getClientSourceCombo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list", SchemeUtils.getClientSources());//查询客户端来源信息
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[获取客户端来源下拉数据]发生异常!异常信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}