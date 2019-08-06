package com.caipiao.admin.user;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.base.BaseController;
import com.caipiao.admin.service.user.SchemeService;
import com.caipiao.admin.service.user.UserAccountDetailService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
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

/**
 * 用户账户流水-控制类
 */
@Controller
@RequestMapping("/user/account/detail/")
public class UserAccountDetailController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(UserAccountDetailController.class);

    @Autowired
    private UserAccountDetailService userAccountDetailService;

    /**
     * 显示用户账户流水首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_user_account")
    public String initIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-3);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        params.put("minCreateTime", DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
        map.addAttribute("params",params);
        return "user/account/index";
    }

    /**
     * 查询用户账户流水
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_user_account")
    public void getUserRecharge(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode", ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
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

    /**
     * 加款/扣款
     * @author  mcdog
     */
    @RequestMapping("/change")
    @ModuleAuthorityRequired(mcode = "btn_user_user_jkkk")
    public void cancelScheme(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[加款/扣款]操作帐户=" + params.getAsString("current_login_personal") + ",接收原始参数:" + params.toString());
            if(userAccountDetailService.updateUserAccountForJkAndKk(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","0".equals(params.getAsString("inType"))? "加款成功" : "扣款成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[加款/扣款]发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 导出用户账户流水
     * @author  mcdog
     */
    @RequestMapping("/export")
    public void exportUserTx(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        try
        {
            //查询数据
            params.remove("psize");
            params.remove("pnum");
            Dto dataDto = new BaseDto();
            List<Dto> dataList = userAccountDetailService.queryUserAccountDetailInfo(params);
            if(dataList != null && dataList.size() > 0)
            {
                for(Dto dto : dataList)
                {
                    int status = dto.getAsInteger("status");
                    dto.put("sdesc",status == -1? "无效" : (status == 0? "处理中" : (status == 1? "有效" : "未知")));
                    dto.put("typeDesc",dto.getAsBoolean("inType")? "出账" : "进账");
                }
            }
            dataDto.put("list",dataList);

            //封装excel数据
            FileInputStream in = new FileInputStream(new File(this.getClass().getClassLoader().getResource("template/user/user.account.detail.xlsx").getPath()));
            Workbook workbook = new XLSTransformer().transformXLS(in,dataDto);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);

            //输出excel数据
            String filename = "用户账户流水.xls";
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
            logger.error("[导出用户账户流水]发生异常,异常信息：",e);
            Dto resultDto = new BaseDto("dcode", ConstantUtils.OPERATION_FAILURE);//返回数据对象
            resultDto.put("dmsg",e.getMessage());
            WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
        }
    }
}