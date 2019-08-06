package com.caipiao.admin.user;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.base.BaseController;
import com.caipiao.admin.service.user.CzTxService;
import com.caipiao.admin.service.user.SchemeService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
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
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

/**
 * 用户充值流水-控制类
 */
@Controller
@RequestMapping("/user/recharge")
public class RechargeController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(RechargeController.class);

    @Autowired
    private CzTxService czTxService;

    /**
     * 显示用户充值流水首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_user_recharge")
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
        return "user/recharge/index";
    }

    /**
     * 查询用户充值流水
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_user_recharge")
    public void getUserRecharge(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode", ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",czTxService.queryUserPayInfos(params));//查询数据记录
            Dto countDto = czTxService.querUserPayInfoCount(params);//查询总计数据
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",countDto.get("tsize"));//设置总记录条数
            }
            dataDto.put("tmoney",countDto.get("tmoney"));//设置总计金额
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[查询用户充值流水]发生异常,异常信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 导出用户充值流水
     * @author  mcdog
     */
    @RequestMapping("/export")
    public void exportUserRecharge(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        try
        {
            //查询数据
            params.remove("psize");
            params.remove("pnum");
            Dto dataDto = new BaseDto();
            List<Dto> dataList = czTxService.queryUserPayInfos(params);
            if(dataList != null && dataList.size() > 0)
            {
                for(Dto dto : dataList)
                {
                    int status = dto.getAsInteger("status");
                    dto.put("sdesc",status == -1? "处理失败" : (status == 0? "待处理" : (status == 1? "等待重新处理" : (status == 2? "处理中" :(status == 3? "处理成功" : "未知状态")))));
                }
            }
            dataDto.put("list",dataList);

            //封装excel数据
            FileInputStream in = new FileInputStream(new File(this.getClass().getClassLoader().getResource("template/user/user.recharge.xlsx").getPath()));
            Workbook workbook = new XLSTransformer().transformXLS(in,dataDto);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);

            //输出excel数据
            String filename = "用户充值流水.xls";
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
            logger.error("[导出用户充值流水]发生异常,异常信息：",e);
            Dto resultDto = new BaseDto("dcode", ConstantUtils.OPERATION_FAILURE);//返回数据对象
            resultDto.put("dmsg",e.getMessage());
            WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
        }
    }
}