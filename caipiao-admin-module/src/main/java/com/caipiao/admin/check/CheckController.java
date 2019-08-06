package com.caipiao.admin.check;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.service.check.CheckService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.DoubleUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.lang.StringUtils;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表控制类
 * Created by Kouyi on 2017/12/01.
 */
@Controller
@RequestMapping("/check")
public class CheckController
{
    private static final Logger logger = LoggerFactory.getLogger(CheckController.class);
    @Autowired
    private CheckService checkService;

    /**
     * 显示资金报表首页
     * @author  kouyi
     */
    @ModuleAuthorityRequired(mcode = "menu_report_checkcapital")
    @RequestMapping("/capital/index")
    public String capitalIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "check/capital/index";
    }

    /**
     * 显示方案票报表首页
     * @author  kouyi
     */
    @RequestMapping("/orderticket/index")
    @ModuleAuthorityRequired(mcode = "menu_report_checkorderticket")
    public String orderticketIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "check/orderticket/index";
    }

    /**
     * 显示兑奖报表首页
     * @author  kouyi
     */
    @RequestMapping("/prize/index")
    @ModuleAuthorityRequired(mcode = "menu_report_checkprize")
    public String prizeIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "check/prize/index";
    }

    /**
     * 显示用户返利报表首页
     * @author  kouyi
     */
    @RequestMapping("/rebate/index")
    @ModuleAuthorityRequired(mcode = "menu_report_checkrebate")
    public String rebateIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "check/rebate/index";
    }

    /**
     * 系统兑奖报表首页
     * @author  kouyi
     */
    @RequestMapping("/award/index")
    @ModuleAuthorityRequired(mcode = "menu_report_checkaward")
    public String awardIndex(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        return "check/award/index";
    }

    /**
     * 查询平台资金对账列表
     * @author kouyi
     */
    @RequestMapping("/capital/list")
    @ModuleAuthorityRequired(mcode = "menu_report_checkcapital")
    public void queryPlatFormCapital(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = checkService.queryPlatFormCapital(params);//查询出票商信息
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",checkService.queryPlatFormCapitalCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询平台资金对账列表，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询平台方案和票报表
     * @author kouyi
     */
    @RequestMapping("/orderticket/list")
    @ModuleAuthorityRequired(mcode = "menu_report_checkorderticket")
    public void querySchemeTicket(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = checkService.querySchemeTicket(params);//查询出票商信息
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",checkService.querySchemeTicketCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询平台方案和票列表，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询平台兑奖计奖报表
     * @author kouyi
     */
    @RequestMapping("/prize/list")
    @ModuleAuthorityRequired(mcode = "menu_report_checkprize")
    public void queryVoteSitePrize(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = checkService.queryVoteSitePrize(params);//查询出票商信息
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",checkService.queryVoteSitePrizeCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询平台兑奖计奖报表列表，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询平台用户返利报表
     * @author kouyi
     */
    @RequestMapping("/rebate/list")
    @ModuleAuthorityRequired(mcode = "menu_report_checkrebate")
    public void queryUserRebate(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            List<Dto> dataList = checkService.queryUserRebate(params);//查询出票商信息
            Dto dataDto = new BaseDto("list",dataList);
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",checkService.queryUserRebateCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询平台用户返利报表列表，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询系统兑奖报表
     * @author kouyi
     */
    @RequestMapping("/award/list")
    @ModuleAuthorityRequired(mcode = "menu_report_checkaward")
    public void queryVoteSiteAward(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            String beginTime = params.getAsString("beginTime");
            if(StringUtil.isEmpty(beginTime)) {//默认上一天
                beginTime = DateUtil.dateFormat(DateUtil.addDay(new Date(), -1), DateUtil.DEFAULT_DATE);
            }
            params.put("beginTime", beginTime);
            List<Dto> dataList = checkService.queryVoteSiteAward(params);//查询出票商信息
            Dto dataDto = new BaseDto("list",dataList);
            //查询加奖金额
            Double subjoin = checkService.queryDateSchemeSubjoin(params);
            dataDto.put("subjoin", subjoin);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询平台兑奖报表列表，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 导出平台资金对账
     * @author  mcdog
     */
    @RequestMapping("/capital/export")
    public void exportDaystatis(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        try
        {
            //查询数据
            params.remove("psize");
            params.remove("pnum");
            Dto dataDto = new BaseDto();
            List<Dto> dataList = checkService.queryPlatFormCapital(params);
            dataDto.put("list",dataList);

            //封装excel数据
            FileInputStream in = new FileInputStream(new File(this.getClass().getClassLoader().getResource("template/user/capital.xlsx").getPath()));
            Workbook workbook = new XLSTransformer().transformXLS(in,dataDto);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);

            //输出excel数据
            String filename = "平台资金对账.xls";
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
            logger.error("[导出平台资金对账]发生异常,异常信息：",e);
            Dto resultDto = new BaseDto("dcode", ConstantUtils.OPERATION_FAILURE);//返回数据对象
            resultDto.put("dmsg",e.getMessage());
            WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
        }
    }

    /**
     * 导出订单和票对账
     * @author  mcdog
     */
    @RequestMapping("/orderticket/export")
    public void exportOrderticket(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        try
        {
            //查询数据
            params.remove("psize");
            params.remove("pnum");
            Dto dataDto = new BaseDto();
            List<Dto> dataList = checkService.querySchemeTicket(params);
            dataDto.put("list",dataList);

            //封装excel数据
            FileInputStream in = new FileInputStream(new File(this.getClass().getClassLoader().getResource("template/user/orderticket.xlsx").getPath()));
            Workbook workbook = new XLSTransformer().transformXLS(in,dataDto);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);

            //输出excel数据
            String filename = "订单和票对账.xls";
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
            logger.error("[导出订单和票对账]发生异常,异常信息：",e);
            Dto resultDto = new BaseDto("dcode", ConstantUtils.OPERATION_FAILURE);//返回数据对象
            resultDto.put("dmsg",e.getMessage());
            WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
        }
    }

    /**
     * 导出计奖兑奖对账
     * @author  mcdog
     */
    @RequestMapping("/prize/export")
    public void exportPrize(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        try
        {
            //查询数据
            params.remove("psize");
            params.remove("pnum");
            Dto dataDto = new BaseDto();
            List<Dto> dataList = checkService.queryVoteSitePrize(params);
            dataDto.put("list",dataList);

            //封装excel数据
            FileInputStream in = new FileInputStream(new File(this.getClass().getClassLoader().getResource("template/user/prize.xlsx").getPath()));
            Workbook workbook = new XLSTransformer().transformXLS(in,dataDto);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);

            //输出excel数据
            String filename = "计奖兑奖对账.xls";
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
            logger.error("[导出计奖兑奖对账]发生异常,异常信息：",e);
            Dto resultDto = new BaseDto("dcode", ConstantUtils.OPERATION_FAILURE);//返回数据对象
            resultDto.put("dmsg",e.getMessage());
            WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
        }
    }

    /**
     * 导出用户返利对账
     * @author  mcdog
     */
    @RequestMapping("/rebate/export")
    public void exportRebate(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        try
        {
            //查询数据
            params.remove("psize");
            params.remove("pnum");
            Dto dataDto = new BaseDto();
            List<Dto> dataList = checkService.queryUserRebate(params);
            dataDto.put("list",dataList);

            //封装excel数据
            FileInputStream in = new FileInputStream(new File(this.getClass().getClassLoader().getResource("template/user/rebate.xlsx").getPath()));
            Workbook workbook = new XLSTransformer().transformXLS(in,dataDto);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);

            //输出excel数据
            String filename = "用户返利对账.xls";
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
            logger.error("[导出用户返利对账]发生异常,异常信息：",e);
            Dto resultDto = new BaseDto("dcode", ConstantUtils.OPERATION_FAILURE);//返回数据对象
            resultDto.put("dmsg",e.getMessage());
            WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
        }
    }
}