package com.caipiao.admin.weihu.lottery.controller;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.base.BaseController;
import com.caipiao.admin.service.config.SysConfig;
import com.caipiao.admin.service.weihu.lottery.LotteryService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.SessionUtil;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import com.caipiao.memcache.MemCached;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 彩种-控制类
 */
@Controller
@RequestMapping("/weihu/lottery")
public class LotteryController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(LotteryController.class);
    private static final String lotteryPrefix = "lottery_";//彩种信息-前缀(缓存key)
    @Autowired
    private MemCached memcache;
    @Autowired
    private LotteryService lotteryService;

    /**
     * 显示彩种维护首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_weihu_lottery")
    public String index()
    {
        return "weihu/lottery/index";
    }

    /**
     * 显示彩种编辑页面
     * @author  mcdog
     */
    @RequestMapping("/initEdit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_lottery_edit")
    public String initEdit(ModelMap map, HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> lotteryList = lotteryService.queryLotterys(params);
        if(lotteryList != null && lotteryList.size() > 0)
        {
            map.addAttribute("params",lotteryList.get(0));
        }
        return "weihu/lottery/edit";
    }

    /**
     * 显示彩种新增页面
     * @author  mcdog
     */
    @RequestMapping("/initAdd")
    @ModuleAuthorityRequired(mcode = "btn_weihu_lottery_add")
    public String initAdd(HttpServletRequest request, HttpServletResponse response)
    {
        return "weihu/lottery/add";
    }

    /**
     * 显示彩种详细页面
     * @author  mcdog
     */
    @RequestMapping("/initDetail")
    @ModuleAuthorityRequired(mcode = "menu_weihu_lottery")
    public String initDetail(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> lotteryList = lotteryService.queryLotterys(params);
        if(lotteryList != null && lotteryList.size() > 0)
        {
            map.addAttribute("params",lotteryList.get(0));
        }
        return "weihu/lottery/detail";
    }

    /**
     * 查询彩种信息
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_weihu_lottery")
    public void getLotterys(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",replaceNullToEmpty(lotteryService.queryLotterys(params)));//查询彩种信息
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",lotteryService.queryLotterysCount(params));//查询总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询彩种发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 编辑彩种信息
     * @author  mcdog
     */
    @RequestMapping("/edit")
    @ModuleAuthorityRequired(mcode = "btn_weihu_lottery_edit")
    public void editLottery(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[编辑彩种信息]操作帐户=" + params.getAsString("opfullName") + ",接收原始参数:" + params.toString());
            if(lotteryService.editLottery(params) > 0)
            {
                memcache.delete(lotteryPrefix+params.getAsString("id"));//清除投注时检查彩种有效性的缓存
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
            logger.error("编辑彩种出错发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 编辑彩种销售状态
     * @author  mcdog
     */
    @RequestMapping("/editLotterySaleStatus")
    @ModuleAuthorityRequired(mcode = "btn_weihu_lottery_edit")
    public void editLotterySaleStatus(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            logger.info("[编辑彩种销售状态]操作帐户=" + SessionUtil.getCurrentAccount(request).getAsString("accountName") + ",接收原始参数:" + params.toString());
            if(lotteryService.editLotterySaleStatus(params) > 0)
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
            logger.error("编辑彩种销售状态发生异常,异常信息:",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 获取彩种下拉数据
     * @author  mcdog
     */
    @RequestMapping("/getLotterysCombo")
    public void getLotterysCombo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",lotteryService.queryLotterys(params));//查询彩种信息
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("查询彩种下拉出错，错误信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 上传彩种活动图片
     * @author  mcdog
     */
    @RequestMapping("/uploadActivityImg")
    public void uploadLogo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);
        try
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> mfileMap = multipartRequest.getFileMap();
            if(mfileMap != null && mfileMap.size() > 0 && mfileMap.get("upActivityImgFile") != null)
            {
                //初始化文件上传参数
                MultipartFile multipartFile = mfileMap.get("upActivityImgFile");
                String fileName = multipartFile.getOriginalFilename();//提取文件名
                String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);//提取文件后缀
                String staticPath = SysConfig.getString("static.file.path");//从配置文件中提取静态文件根路径
                String fpath = SysConfig.getString("weihu.lottery.activityimg.path");//从配置文件中提取活动logo上传路径

                //上传文件
                File file = new File(staticPath + fpath);
                if(!file.exists())
                {
                    file.mkdirs();
                }
                String fname = System.currentTimeMillis() + "." + fileSuffix;
                fpath = fpath + fname;
                multipartFile.transferTo(new File(staticPath + fpath));
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("datas",new BaseDto("fpath",fpath));//设置文件路径
            }
        }
        catch (Exception e)
        {
            resultDto.put("dmsg","上传活动图片发生异常!异常信息:" + e);
            logger.error("[上传彩种活动图片]发生异常!异常信息:" + e);
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}