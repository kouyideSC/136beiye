package com.caipiao.admin.xiaoshou;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.service.user.UserAccountService;
import com.caipiao.admin.service.user.UserService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.SessionUtil;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.user.UserUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAccount;
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
 * 销售控制类
 */
@Controller
@RequestMapping("/sale")
public class SaleController
{
    private static final Logger logger = LoggerFactory.getLogger(SaleController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private UserAccountService userAccountService;

    /**
     * 显示下级用户首页
     * @author kouyi
     */
    @RequestMapping("/lower/index")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_lower")
    public String index(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "sale/lower/index";
    }

    /**
     * 显示代理用户首页
     * @author kouyi
     */
    @RequestMapping("/proxy/index")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_proxy")
    public String proxyIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "sale/proxy/index";
    }

    /**
     * 显示销售查询首页
     * @author kouyi
     */
    @RequestMapping("/rebate/index")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_rebate")
    public String rebateIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "sale/rebate/index";
    }

    /**
     * 销售自己月提成首页
     * @author kouyi
     */
    @RequestMapping("/monthmy/index")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_wdytc")
    public String monthMyIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "/sale/monthmy/index";
    }

    /**
     * 代理返利明细首页
     * @author kouyi
     */
    @RequestMapping("/fanlidetail/index")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_fanlidetail")
    public String fanLiDetailIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "/sale/fanlidetail/index";
    }

    /**
     * 管理员查询销售月提成首页
     * @author kouyi
     */
    @RequestMapping("/monthsale/index")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_ytc")
    public String monthSaIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "/sale/monthsale/index";
    }

    /**
     * 保存返点设置
     * @author kouyi
     */
    @RequestMapping("/lower/save")
    @ModuleAuthorityRequired(mcode = {"menu_xiaoshou_lower","menu_xiaoshou_proxy","btn_xiaoshou_rebate_szfd"},mflag = 1)
    public void saveUserRebate(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            Dto dto = SessionUtil.getCurrentAccount(request);
            params.put("sale_mobile", dto.getAsString("mobile"));
            params.put("isSale",dto.getAsString("isSale"));
            if(userService.saveUserRebate(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","设置返点成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "设置返点失败");
            }
        }
        catch(Exception e)
        {
            logger.error("设置用户返点出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 销售下属用户转出到新的销售员名下
     * @author kouyi
     */
    @RequestMapping("/lower/change")
    @ModuleAuthorityRequired(mcode = {"btn_user_user_bdsjgs","btn_xiaoshou_rebate_zcxj"},mflag = 1)
    public void saleUserChange(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            Dto dto = SessionUtil.getCurrentAccount(request);
            params.put("sale_mobile", dto.getAsString("mobile"));
            if(userService.updateSaleUserChange(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","用户转出成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "用户转出失败");
            }
        }
        catch(Exception e)
        {
            logger.error("用户转出出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询下级用户列表
     * @author kouyi
     */
    @RequestMapping("/lower/list")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_lower")
    public void getUserList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dto = SessionUtil.getCurrentAccount(request);
            params.put("mobile", dto.getAsString("mobile"));
            List<Dto> dataList = userService.querySaleLowerUserList(params);
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",userService.querySaleLowerUserCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询销售下级用户列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询用户详细信息
     * @author kouyi
     */
    @RequestMapping("/lower/detail")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_lower")
    public String getUserDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        try {
            BaseDto params = WebUtils.getPraramsAsDto(request);//初始化请求参数
            User user = userService.queryUserInfo(params);
            setUserProp(map, user);
            map.put("secrityLevel", UserUtils.getSecrityLevel(user));//安全等级
            map.put("isBank", UserUtils.isBindingBank(user));//是否绑定银行卡
            UserAccount account = userAccountService.queryUserAccountInfoById(user.getId());//账户信息
            map.put("account", account);
            params.clear();
            params.put("appStatus", 1);
            List<Dto> rebateList = userService.queryUserLotteryRebateList(user.getId());
            map.put("rebate", rebateList);
            map.put("rebate_size", rebateList.size());
        } catch (Exception e) {
            logger.error("后台管理-查询用户详情异常", e);
        }
        return "sale/lower/detail";
    }

    /**
     * 销售将用户设置为代理
     * @author kouyi
     */
    @RequestMapping("/lower/setproxy")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_lower")
    public void setUserProxy(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            params.put("isSale", 2);
            if(userService.updateUserProxy(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","设置代理成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "设置代理失败");
            }
        }
        catch(Exception e)
        {
            logger.error("设置代理出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 获取用户模块下拉数据
     * @author kouyi
     */
    @RequestMapping("/getUserModuleDown")
    public void getUserStatus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Integer module = params.getAsInteger("module");
            Map<?, ?> map = null;
            if(module.intValue() == 1) {//查询用户状态
                map = UserConstants.userStatusMap;
            }
            else if(module.intValue() == 2) {//查询VIP等级
                map = UserConstants.userVipLevelMap;
            }
            else if(module.intValue() == 3) {//查询客户端来源
                map = UserConstants.userSourceMap;
            }
            else if(module.intValue() == 4){//查询用户类型
                map = UserConstants.userTypeMap;
            }
            else if(module.intValue() == 5) {//查询代理头衔
                map = UserConstants.userProxyMap;
            }
            Dto dataDto = new BaseDto("list",LotteryUtils.getSelectUtil(map));
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("用户模块下拉列表获取出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 将用户状态码转换为中文提供给页面展示
     * @param map
     * @param user
     */
    private void setUserProp(ModelMap map, User user) {
        if(StringUtil.isEmpty(user)) {
            return;
        }
        String sts = "未知";
        if(UserConstants.userStatusMap.containsKey(user.getStatus())) {
            sts = UserConstants.userStatusMap.get(user.getStatus());
        }
        map.addAttribute("us", sts);
        sts = "未知";
        if(UserConstants.userVipLevelMap.containsKey(user.getVipLevel())) {
            sts = UserConstants.userVipLevelMap.get(user.getVipLevel());
        }
        map.addAttribute("vp", sts);
        sts = "未知";
        if(UserConstants.userSourceMap.containsKey(user.getRegisterFrom())) {
            sts = UserConstants.userSourceMap.get(user.getRegisterFrom());
        }
        map.addAttribute("rf", sts);
        sts = "未知";
        if(UserConstants.userTypeMap.containsKey(user.getUserType())) {
            sts = UserConstants.userTypeMap.get(user.getUserType());
        }
        map.addAttribute("ut", sts);
        sts = "未知";
        if(UserConstants.userProxyMap.containsKey(user.getIsSale())) {
            sts = UserConstants.userProxyMap.get(user.getIsSale());
        }
        map.addAttribute("is", sts);
        map.addAttribute("params", user);
    }

    /**
     * 查询代理用户列表
     * @author kouyi
     */
    @RequestMapping("/proxy/list")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_proxy")
    public void getProxyUserList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dto = SessionUtil.getCurrentAccount(request);
            params.put("mobile", dto.getAsString("mobile"));
            List<Dto> dataList = userService.querySaleProxyUserList(params);
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",userService.querySaleProxyUserCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询销售代理用户列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 销售将用户代理权限取消
     * @author kouyi
     */
    @RequestMapping("/lower/cancelproxy")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_proxy")
    public void cancelUserProxy(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            params.put("isSale", 0);
            if(userService.updateUserProxy(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","取消代理权限成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "取消代理权限失败");
            }
        }
        catch(Exception e)
        {
            logger.error("取消代理权限出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询代理详细信息
     * @author kouyi
     */
    @RequestMapping("/proxy/detail")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_proxy")
    public String getProxyUserDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        try {
            BaseDto params = WebUtils.getPraramsAsDto(request);//初始化请求参数
            User user = userService.queryUserInfo(params);
            setUserProp(map, user);
            map.put("secrityLevel", UserUtils.getSecrityLevel(user));//安全等级
            map.put("isBank", UserUtils.isBindingBank(user));//是否绑定银行卡
            UserAccount account = userAccountService.queryUserAccountInfoById(user.getId());//账户信息
            map.put("account", account);
            params.clear();
            params.put("appStatus", 1);
            List<Dto> rebateList = userService.queryUserLotteryRebateList(user.getId());
            map.put("rebate", rebateList);
            map.put("rebate_size", rebateList.size());
        } catch (Exception e) {
            logger.error("后台管理-查询代理详情异常", e);
        }
        return "sale/proxy/detail";
    }

    /**
     * 查询销售人员列表
     * @author kouyi
     */
    @RequestMapping("/rebate/list")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_rebate")
    public void getRebateList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = userService.querySaleUserList(params);
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",userService.querySaleUserCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询销售人员列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询销售详细信息
     * @author kouyi
     */
    @RequestMapping("/rebate/detail")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_rebate")
    public String getSaleDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        try {
            BaseDto params = WebUtils.getPraramsAsDto(request);//初始化请求参数
            User user = userService.queryUserInfo(params);
            setUserProp(map, user);
            map.put("secrityLevel", UserUtils.getSecrityLevel(user));//安全等级
            map.put("isBank", UserUtils.isBindingBank(user));//是否绑定银行卡
            UserAccount account = userAccountService.queryUserAccountInfoById(user.getId());//账户信息
            map.put("account", account);
            params.clear();
            params.put("appStatus", 1);
            List<Dto> rebateList = userService.queryUserLotteryRebateList(user.getId());
            map.put("rebate", rebateList);
            map.put("rebate_size", rebateList.size());
            params.clear();
            params.put("mobile", user.getMobile());
            map.put("loweruser", userService.querySaleLowerUserList(params));
            map.put("lowerproxy", userService.querySaleProxyUserList(params));
        } catch (Exception e) {
            logger.error("后台管理-查询销售详情异常", e);
        }
        return "sale/rebate/detail";
    }

    /**
     * 将销售权限取消
     * @author kouyi
     */
    @RequestMapping("/rebate/cancelsale")
    @ModuleAuthorityRequired(mcode = "btn_xiaoshou_rebate_qxxszg")
    public void cancelSale(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            params.put("isSale", 0);
            if(userService.updateUserProxy(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","贬为庶民成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "贬为庶民失败");
            }
        }
        catch(Exception e)
        {
            logger.error("贬为庶民权限出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询我的返利模块信息
     * @author kouyi
     */
    @RequestMapping("/fanli/detail")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_proxymoney")
    public String userFanliDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        try {
            Dto dto = SessionUtil.getCurrentAccount(request);
            User user = userService.queryUserInfoByMobile(dto.getAsString("mobile"));
            setUserProp(map, user);
            map.put("secrityLevel", UserUtils.getSecrityLevel(user));//安全等级
            map.put("isBank", UserUtils.isBindingBank(user));//是否绑定银行卡
            UserAccount account = userAccountService.queryUserAccountInfoById(user.getId());//账户信息
            map.put("account", account);
            //返点比例
            BaseDto params = WebUtils.getPraramsAsDto(request);//初始化请求参数
            params.put("appStatus", 1);
            List<Dto> rebateList = userService.queryUserLotteryRebateList(user.getId());
            map.put("rebate", rebateList);
            params.clear();
            //下级用户
            params.put("mobile", user.getMobile());
            map.put("loweruser", userService.querySaleLowerUserList(params));
            map.put("lowerproxy", userService.querySaleProxyUserList(params));
            //近半月返利明细
            params.put("userId", user.getId());
            params.put("psize", 10);
            params.put("pstart", 0);
            map.put("backdetail", userService.queryUserBackDetail(params));
            map.put("detailcount", userService.queryUserBackDetailCount(params));
        } catch (Exception e) {
            logger.error("后台管理-查询我的返利模块信息异常", e);
        }
        return "sale/fanli/detail";
    }

    /**
     * 查询我的销量模块信息
     * @author kouyi
     */
    @RequestMapping("/xliang/detail")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_commiss")
    public String userXiaoLiangDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        try {
            Dto dto = SessionUtil.getCurrentAccount(request);
            User user = userService.queryUserInfoByMobile(dto.getAsString("mobile"));
            setUserProp(map, user);
            map.put("secrityLevel", UserUtils.getSecrityLevel(user));//安全等级
            map.put("isBank", UserUtils.isBindingBank(user));//是否绑定银行卡
            UserAccount account = userAccountService.queryUserAccountInfoById(user.getId());//账户信息
            map.put("account", account);
            //返点比例
            BaseDto params = WebUtils.getPraramsAsDto(request);//初始化请求参数
            params.put("appStatus", 1);
            List<Dto> rebateList = userService.queryUserLotteryRebateList(user.getId());
            map.put("rebate", rebateList);
            params.clear();
            //下级用户
            params.put("mobile", user.getMobile());
            map.put("loweruser", userService.querySaleLowerUserList(params));
            map.put("lowerproxy", userService.querySaleProxyUserList(params));
            //查询销售当月和历史总销量
            map.put("salemoney", userService.queryUserSaleSumMoney(user.getMobile()));
            //查询销售本月销量明细
            map.put("saledetail", userService.queryUserSaleMoneyDetail(user.getMobile()));
        } catch (Exception e) {
            logger.error("后台管理-查询我的销量模块信息异常", e);
        }
        return "sale/xliang/detail";
    }

    /**
     * 查询近半月返利明细列表
     * @author kouyi
     */
    @RequestMapping("/fanli/list")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_proxy")
    public void getFanliList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dto = SessionUtil.getCurrentAccount(request);
            User user = userService.queryUserInfoByMobile(dto.getAsString("mobile"));
            params.put("userId", user.getId());
            List<Dto> dataList = userService.queryUserBackDetail(params);
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",userService.queryUserBackDetailCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询近半月返利明细列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 销售查询我的月提成
     * @author kouyi
     */
    @RequestMapping("/monthmy/list")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_wdytc")
    public void getMonthMyCommList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dto = SessionUtil.getCurrentAccount(request);
            User user = userService.queryUserInfoByMobile(dto.getAsString("mobile"));
            params.put("userId", user.getId());
            List<Dto> dataList = userService.queryUserMonthCommList(params);
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",userService.queryUserMonthCommListCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("销售查询我的月提成列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 管理员查询销售月提成列表
     * @author kouyi
     */
    @RequestMapping("/monthsale/list")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_ytc")
    public void getSaleMonthCommList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = userService.queryUserMonthCommList(params);
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",userService.queryUserMonthCommListCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("管理员查询销售月提成列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 代理员查询返利明细
     * @author kouyi
     */
    @RequestMapping("/fanlidetail/list")
    @ModuleAuthorityRequired(mcode = "menu_xiaoshou_fanlidetail")
    public void getUserFanLiDetailList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            params.put("userId", params.getAsLong("opaccountId"));
            List<Dto> dataList = userService.queryUserBackDetail(params);
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",userService.queryUserBackDetailCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("代理员查询返利明细列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}