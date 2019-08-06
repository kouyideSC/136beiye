package com.caipiao.grab.jc.handler;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.http.Grab;
import com.caipiao.common.lottery.JczqUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.grab.util.GrabForJcUtil;
import com.caipiao.grab.util.JcUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 抓取竞彩足球对阵对阵开停售状态
 * Created by kouyi on 2018/07/10.
 */
@Component("grabForJczqMatchStatus")
public class GrabForJczqMatchStatus extends Grab<Map<String, MatchFootBall>, String> {
    private static Logger logger = LoggerFactory.getLogger(GrabForJczqMatchStatus.class);

    @Override
    public Map<String, MatchFootBall> parse(String content, String param) {
        try{
            if(StringUtil.isEmpty(content)){
                logger.info("[竞彩足球对阵开停售状态抓取] 页面数据为空!");
                return null;
            }
            String[] datas = content.split("\\;");
            if(StringUtil.isEmpty(datas) || datas.length != 4) {
                logger.info("[竞彩足球对阵开停售状态抓取] 数据格式不正确!");
                return null;
            }
            JSONArray arrayData = JSONArray.fromObject(datas[2].replace("var data=", ""));
            Map<String, MatchFootBall> matchMap = new HashMap<>();
            for(int i = 0; i < arrayData.size(); i++) {
                JSONArray data = arrayData.getJSONArray(i);
                String weekStr = data.getJSONArray(0).getString(0);
                String time = data.getJSONArray(0).getString(3);
                Date matchTime = DateUtil.dateDefaultFormat("20" + time + ":00");
                if (matchTime.getTime() < new Date().getTime()) {//比赛时间小于当前时间则不再抓取
                    continue;
                }
                String period = JcUtil.getPeriodDay(weekStr.substring(0, 2), matchTime);
                MatchFootBall match = new MatchFootBall();
                match.setMatchCode(period + weekStr.substring(2, 5));
                match.setRqspfStatus(data.getJSONArray(1).getInt(3) == 1 ? LotteryConstants.STATUS_SELL : LotteryConstants.STATUS_STOP);
                match.setBfStatus(data.getJSONArray(2).getInt(32) == 1 ? LotteryConstants.STATUS_SELL : LotteryConstants.STATUS_STOP);
                match.setZjqStatus(data.getJSONArray(3).getInt(8) == 1 ? LotteryConstants.STATUS_SELL : LotteryConstants.STATUS_STOP);
                match.setBqcStatus(data.getJSONArray(4).getInt(9) == 1 ? LotteryConstants.STATUS_SELL : LotteryConstants.STATUS_STOP);
                match.setSpfStatus(data.getJSONArray(5).getInt(3) == 1 ? LotteryConstants.STATUS_SELL : LotteryConstants.STATUS_STOP);
                matchMap.put(match.getMatchCode(), match);
            }
            return matchMap;
        } catch (Exception e){
            logger.error("[竞彩足球对阵开停售状态抓取] 解析对阵数据异常", e);
            return null;
        }
    }

}
