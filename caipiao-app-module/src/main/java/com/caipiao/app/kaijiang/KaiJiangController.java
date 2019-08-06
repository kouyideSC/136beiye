package com.caipiao.app.kaijiang;

import com.caipiao.app.base.BaseController;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.vo.KaiJiangVo;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.kaijiang.KaiJiangService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 开奖接口控制类
 */
@Controller
@RequestMapping("/kj")
public class KaiJiangController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(KaiJiangController.class);

    @Autowired
    private KaiJiangService kaiJiangService;

    /**
     * 获取开奖信息
     * @param
     */
    @RequestMapping(value="/get")
    public void getAllKj(Map<String,String> params,HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            List<KaiJiangVo> kjList = kaiJiangService.getLatestKjInfos();
            result.setData(kjList == null? new ArrayList<KaiJiangVo>() : kjList);
        }
        catch (ServiceException e1)
        {
            logger.error("[获取开奖信息]服务异常,异常信息：" + e1);
            result.setErrorCode(ErrorCode.SERVER_ERROR);
        }
        catch (Exception e)
        {
            logger.error("[获取开奖信息]系统异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,Arrays.asList(new String[]{"xh"}),response);
    }
}