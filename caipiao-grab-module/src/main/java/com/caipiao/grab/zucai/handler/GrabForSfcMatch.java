package com.caipiao.grab.zucai.handler;

import com.alibaba.fastjson.JSON;
import com.caipiao.common.http.Grab;
import com.caipiao.common.util.StringUtil;
import com.caipiao.grab.vo.SfcMatchVo;
import com.caipiao.grab.vo.SfcPeriodVO;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 抓取官网胜负彩任九比赛对阵
 * Created by kouyi on 2017/11/10.
 */
@Component("grabForSfcMatch")
public class GrabForSfcMatch extends Grab<SfcMatchVo, String> {
    private static Logger logger = LoggerFactory.getLogger(GrabForSfcMatch.class);

    @Override
    public SfcMatchVo parse(String content, String param) {
        try{
            if(StringUtil.isEmpty(content)){
                logger.info("[官网胜负彩任九比赛对阵数据抓取] 页面数据为空!");
                return null;
            }
            List<SfcMatchVo> array = JSON.parseArray(content, SfcMatchVo.class);
            if (StringUtil.isNotEmpty(array)) {
                return array.get(0);
            }
        } catch (Exception ex) {
            logger.error("[官网胜负彩任九比赛对阵数据解析异常] 数据转换出错!");
        }
        return null;
    }

}
