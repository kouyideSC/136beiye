package com.caipiao.admin.user;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.base.BaseController;
import com.caipiao.admin.service.user.*;
import com.caipiao.admin.service.weihu.lottery.LotteryService;
import com.caipiao.admin.service.weihu.match.MatchService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.SessionUtil;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.user.UserUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.NumberUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAccount;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 用户控制类
 */
@Controller
@RequestMapping("/user")
public class UserController
{
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private SchemeService schemeService;

    @Autowired
    private CzTxService czTxService;

    @Autowired
    private UserAccountDetailService userAccountDetailService;

    /**
     * 显示用户查询首页
     * @author kouyi
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_user_user")
    public String index(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "user/index";
    }

    /**
     * 用户日报表统计首页
     * @author kouyi
     */
    @RequestMapping("/daystatis/index")
    @ModuleAuthorityRequired(mcode = "menu_report_daystatis")
    public String dayStatisIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "user/daystatis/index";
    }

    /**
     * 显示黑名单用户查询首页
     * @author kouyi
     */
    @RequestMapping("/black/index")
    @ModuleAuthorityRequired(mcode = "menu_black_user")
    public String blackIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "user/black/index";
    }


    /**
     * 查询用户列表
     * @author kouyi
     */
    @RequestMapping("/list")
    @ModuleAuthorityRequired(mcode = "menu_user_user")
    public void getUserList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = userService.queryUserList(params);
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",userService.queryUserListCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询用户列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询用户详细信息
     * @author kouyi
     */
    @RequestMapping("/detail")
    @ModuleAuthorityRequired(mcode = "menu_user_user")
    public String getUserDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        try {
            BaseDto params = WebUtils.getPraramsAsDto(request);//初始化请求参数
            User user = userService.queryUserInfo(params);
            setUserProp(map, user);
            map.put("secrityLevel", UserUtils.getSecrityLevel(user));//安全等级
            map.put("isBank", UserUtils.isBindingBank(user));//是否绑定银行卡
            map.put("bankInfo",UserUtils.getUserBankInfo(user));//银行卡信息
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

            //查询并设置用户的方案记录
            Dto queryDto = new BaseDto("userId",user.getId());
            queryDto.put("pstart",0);
            queryDto.put("psize",10);
            map.put("schemeList",schemeService.queryUserSchemes(queryDto));

            //查询并设置用户的充值流水
            queryDto.put("payType",PayConstants.PAY_TYPE_RECHARGE + "");
            queryDto.put("status",PayConstants.PAYORDER_STATUS_SUCCESS + "");
            map.put("rechargeList",czTxService.queryUserPayInfos(queryDto));

            //查询并设置用户的提现流水
            queryDto.remove("status");
            queryDto.put("payType",PayConstants.PAY_TYPE_ENCHASHMENT + "");
            List<Dto> txlist = czTxService.queryUserPayInfos(queryDto);
            if(txlist != null && txlist.size() > 0)
            {
                for(Dto txdto : txlist)
                {
                    txdto.put("bankInfo",JsonUtil.jsonToDto(txdto.getAsString("bankInfo")));
                }
            }
            map.put("txList",txlist);

            //查询并设置用户的账户流水
            map.put("detailList",userAccountDetailService.queryUserAccountDetailInfo(queryDto));

        } catch (Exception e) {
            logger.error("后台管理-查询用户详情异常", e);
        }
        return "user/detail";
    }

    /**
     * 更新用户信息
     * @author kouyi
     */
    @RequestMapping("/edit")
    @ModuleAuthorityRequired(mcode = "btn_user_user_edit")
    public void editUserInfo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(userService.updateUserInfoByAdmin(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","操作成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
            User user = userService.queryUserInfo(params);
            resultDto.put("bmdValue", user.getIsWhite());
            resultDto.put("statusValue", user.getStatus());
            resultDto.put("saleValue", user.getIsSale());
        }
        catch(Exception e)
        {
            logger.error("更新用户信息出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 保存返点设置
     * @author kouyi
     */
    @RequestMapping("/saveRebate")
    @ModuleAuthorityRequired(mcode = "btn_user_user_szfd")
    public void saveUserRebate(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(userService.saveUserRebateByAdmin(params) > 0)
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
     * 将用户绑定上级归属
     * @author kouyi
     */
    @RequestMapping("/come")
    @ModuleAuthorityRequired(mcode = "btn_user_user_bdsjgs")
    public void saleUserChange(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            if(userService.updateUserHigherUser(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","绑定归属成功");
            }
            else
            {
                resultDto.put("dmsg", params.get("dmsg") != null? params.get("dmsg") : "绑定归属失败");
            }
        }
        catch(Exception e)
        {
            logger.error("绑定归属出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 获取用户模块下拉数据
     * @author kouyi
     */
    @RequestMapping("/getUserModuleDown")
    @ModuleAuthorityRequired(mcode = "menu_user_user")
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
     * 查询用户日报表数据列表
     * @author kouyi
     */
    @RequestMapping("/daystatis/list")
    @ModuleAuthorityRequired(mcode = "menu_report_daystatis")
    public void getUserDayStatisList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = userService.queryUserDayStatis(params);
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",userService.queryUserDayStatisCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询用户日报表数据，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 用户返利首页
     * @author kouyi
     */
    @RequestMapping("/fanli/index")
    @ModuleAuthorityRequired(mcode = "menu_user_fanli")
    public String fanliIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "user/fanli/index";
    }

    /**
     * 查询用户返利明细
     * @author kouyi
     */
    @RequestMapping("/fanli/list")
    @ModuleAuthorityRequired(mcode = "menu_user_fanli")
    public void getUserFanliList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = userService.queryUserFanliList(params);
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",userService.queryUserFanliListCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询用户返利列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 显示修改帐户密码页面
     * @author  mcdog
     */
    @RequestMapping("/password/initEdit")
    public String index()
    {
        return "user/password";
    }

    /**
     * 修改帐户密码
     * @author  mcdog
     */
    @RequestMapping("/password/edit")
    public void editLottery(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[修改帐户密码]操作帐户=" + params.getAsString("opfullName"));
            Dto accountDto = (Dto)request.getSession().getAttribute(ConstantUtils.USER_LOGGED_SESSION_SKEY);
            params.put("isSale",accountDto.get("isSale"));
            if(!"-1".equals(params.getAsString("isSale")))
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_FAILURE);
                resultDto.put("dmsg","暂不支持修改密码!请去客户端修改.");
            }
            else
            {
                if(userService.editPassword(params) > 0)
                {
                    resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                    resultDto.put("dmsg","修改成功");
                }
                else
                {
                    resultDto.put("dmsg",params.get("dmsg"));
                }
            }
        }
        catch(Exception e)
        {
            logger.error("[修改帐户密码]发生异常!异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询黑名单用户列表
     * @author kouyi
     */
    @RequestMapping("/black/list")
    @ModuleAuthorityRequired(mcode = "menu_black_user")
    public void getBlackUserList(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            params.put("isWhite", 2);
            List<Dto> dataList = userService.queryUserList(params);
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",userService.queryUserListCount(params));//查询黑名单总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询黑名单用户列表出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 取消用户黑名单
     * @author  kouyi
     */
    @RequestMapping("/black/cancelBlack")
    @ModuleAuthorityRequired(mcode = "btn_weihu_lottery_edit")
    public void cancelUserBlack(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[取消用户黑名单]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数:" + params.toString());
            params.put("isWhite", "1");
            if(userService.updateUserInfoByAdmin(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","操作成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("取消用户黑名单发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 导出用户返利
     * @author  mcdog
     */
    @RequestMapping("/fanli/export")
    public void exportUserTx(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        try
        {
            //查询数据
            params.remove("psize");
            params.remove("pnum");
            Dto dataDto = new BaseDto();
            List<Dto> dataList = userService.queryUserFanliList(params);
            if(dataList != null && dataList.size() > 0)
            {
                for(Dto dto : dataList)
                {
                    dto.put("typeDesc",dto.getAsInteger("type") == 0? "收获" : (dto.getAsInteger("type") == 1? "提取" : "未定义"));
                }
            }
            dataDto.put("list",dataList);

            //封装excel数据
            FileInputStream in = new FileInputStream(new File(this.getClass().getClassLoader().getResource("template/user/user.fanli.xlsx").getPath()));
            Workbook workbook = new XLSTransformer().transformXLS(in,dataDto);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);

            //输出excel数据
            String filename = "用户返利.xls";
            response.setContentType("application/x-excel");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition","attachment;filename=" + java.net.URLEncoder.encode(filename,"UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(output.toByteArray());
            outputStream.flush();
            outputStream.close();
        }
        catch(Exception e)
        {
            logger.error("[导出用户返利]发生异常,异常信息：",e);
            Dto resultDto = new BaseDto("dcode", ConstantUtils.OPERATION_FAILURE);//返回数据对象
            resultDto.put("dmsg",e.getMessage());
            WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
        }
    }

    /**
     * 导出日报表统计
     * @author  mcdog
     */
    @RequestMapping("/daystatis/export")
    public void exportDaystatis(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        try
        {
            //查询数据
            params.remove("psize");
            params.remove("pnum");
            Dto dataDto = new BaseDto();
            List<Dto> dataList = userService.queryUserDayStatis(params);
            dataDto.put("list",dataList);

            //封装excel数据
            FileInputStream in = new FileInputStream(new File(this.getClass().getClassLoader().getResource("template/user/daystatis.xlsx").getPath()));
            Workbook workbook = new XLSTransformer().transformXLS(in,dataDto);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);

            //输出excel数据
            String filename = "日报表统计.xls";
            response.setContentType("application/x-excel");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition","attachment;filename=" + java.net.URLEncoder.encode(filename,"UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(output.toByteArray());
            outputStream.flush();
            outputStream.close();
        }
        catch(Exception e)
        {
            logger.error("[出日报表统计]发生异常,异常信息：",e);
            Dto resultDto = new BaseDto("dcode", ConstantUtils.OPERATION_FAILURE);//返回数据对象
            resultDto.put("dmsg",e.getMessage());
            WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
        }
    }
}