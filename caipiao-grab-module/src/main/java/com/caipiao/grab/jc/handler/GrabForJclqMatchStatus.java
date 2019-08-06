package com.caipiao.grab.jc.handler;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.http.Grab;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.match.MatchBasketBall;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.grab.util.JcUtil;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 抓取竞彩篮球对阵对阵开停售状态
 * Created by kouyi on 2018/07/10.
 */
@Component("grabForJclqMatchStatus")
public class GrabForJclqMatchStatus extends Grab<Map<String, MatchBasketBall>, String> {
    private static Logger logger = LoggerFactory.getLogger(GrabForJclqMatchStatus.class);

    @Override
    public Map<String, MatchBasketBall> parse(String content, String param) {
        try{
            if(StringUtil.isEmpty(content)){
                logger.info("[竞彩篮球对阵开停售状态抓取] 页面数据为空!");
                return null;
            }
            String[] datas = content.split("\\;");
            if(StringUtil.isEmpty(datas) || datas.length != 4) {
                logger.info("[竞彩篮球对阵开停售状态抓取] 数据格式不正确!");
                return null;
            }
            JSONArray arrayData = JSONArray.fromObject(datas[2].replace("var data=", ""));
            Map<String, MatchBasketBall> matchMap = new HashMap<>();
            for(int i = 0; i < arrayData.size(); i++) {
                JSONArray data = arrayData.getJSONArray(i);
                String weekStr = data.getJSONArray(0).getString(0);
                String time = data.getJSONArray(0).getString(4);
                Date matchTime = DateUtil.dateDefaultFormat("20" + time + ":00");
                if (matchTime.getTime() < new Date().getTime()) {//比赛时间小于当前时间则不再抓取
                    continue;
                }
                String period = JcUtil.getPeriodDay(weekStr.substring(0, 2), matchTime);
                MatchBasketBall match = new MatchBasketBall();
                match.setMatchCode(period + weekStr.substring(2, 5));
                match.setSfStatus(data.getJSONArray(1).getString(2).equals("1") ? LotteryConstants.STATUS_SELL : LotteryConstants.STATUS_STOP);
                match.setRfsfStatus(data.getJSONArray(2).getString(3).equals("1") ? LotteryConstants.STATUS_SELL : LotteryConstants.STATUS_STOP);
                match.setDxfStatus(data.getJSONArray(3).getString(3).equals("1") ? LotteryConstants.STATUS_SELL : LotteryConstants.STATUS_STOP);
                match.setSfcStatus(data.getJSONArray(4).getString(13).equals("1") ? LotteryConstants.STATUS_SELL : LotteryConstants.STATUS_STOP);
                matchMap.put(match.getMatchCode(), match);
            }
            return matchMap;
        } catch (Exception e){
            logger.error("[竞彩篮球对阵开停售状态抓取] 解析对阵数据异常", e);
            return null;
        }
    }

}
