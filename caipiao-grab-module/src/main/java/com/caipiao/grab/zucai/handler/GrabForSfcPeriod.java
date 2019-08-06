package com.caipiao.grab.zucai.handler;

import com.alibaba.fastjson.JSON;
import com.caipiao.common.http.Grab;
import com.caipiao.common.util.StringUtil;
import com.caipiao.grab.vo.SfcPeriodVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 抓取胜负彩任九期次
 * Created by kouyi on 2017/11/10.
 */
@Component("grabForSfcPeriod")
public class GrabForSfcPeriod extends Grab<SfcPeriodVO, String> {
    private static Logger logger = LoggerFactory.getLogger(GrabForSfcPeriod.class);

    @Override
    public SfcPeriodVO parse(String content, String param) {
        try{
            if(StringUtil.isEmpty(content)){
                logger.info("[胜负彩任九期次数据抓取] 页面数据为空!");
                return null;
            }
            List<SfcPeriodVO> array = JSON.parseArray(content, SfcPeriodVO.class);
            if (StringUtil.isNotEmpty(array)) {
                return array.get(0);
            }
        } catch (Exception ex) {
            logger.error("[胜负彩任九期次数据解析异常] 数据转换出错!");
        }
        return null;
    }

}
