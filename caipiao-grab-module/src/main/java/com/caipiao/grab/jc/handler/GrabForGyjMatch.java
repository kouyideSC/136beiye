package com.caipiao.grab.jc.handler;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.http.Grab;
import com.caipiao.common.lottery.JczqUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.match.GyjMatch;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.grab.util.GrabForJcUtil;
import com.caipiao.grab.util.JcUtil;
import net.sf.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 抓取冠亚军对阵
 * Created by kouyi on 2018/04/04.
 */
@Component("grabForGyjMatch")
public class GrabForGyjMatch extends Grab<List<GyjMatch>, String> {
    private static Logger logger = LoggerFactory.getLogger(GrabForGyjMatch.class);

    @Override
    public List<GyjMatch> parse(String content, String param) {
        try{
            if(StringUtil.isEmpty(content)){
                logger.info("[冠亚军对阵抓取] 页面数据为空!");
                return null;
            }
            if(content.startsWith("showFnlList(")) {
                content = content.replaceFirst("showFnlList\\(","");
            }
            if(content.endsWith(");")) {
                content = content.substring(0, content.length()-2);
            }
            String jsonObj = JSONObject.fromObject(content).getJSONArray("data").getJSONObject(0).getString("data");
            if (StringUtil.isEmpty(jsonObj)) {
                logger.info("[冠亚军对阵抓取] 无对阵数据");
                return null;
            }

            String[] nodes = jsonObj.split("\\|");
            List<GyjMatch> matchs = new ArrayList<>();
            for(int i = 0; i < nodes.length; i++) {
                String[] values = nodes[i].split("\\-");
                GyjMatch match = new GyjMatch();
                match.setLotteryId(LotteryConstants.GYJ);
                match.setPeriod("2018002");
                match.setMatchCode(values[0]);
                match.setLeagueName("亚洲杯冠亚军");
                String[] name = values[1].split("\\—");
                match.setTeamName(name[0]);
                match.setTeamId(Long.parseLong(values[10].split("\\—")[0]));
                match.setTeamImg(values[11].split("\\—")[0]);
                if(name.length == 2) {
                    match.setGuestTeamName(name[1]);
                    match.setGuestTeamId(Long.parseLong(values[10].split("\\—")[1]));
                    match.setGuestTeamImg(values[11].split("\\—")[1]);
                }
                match.setSp(StringUtil.parseDouble(values[3]));
                match.setProbability(values[5]);
                match.setStatus(values[2].equals("开售")?LotteryConstants.STATUS_SELL:values[2].equals("停售")?LotteryConstants.STATUS_STOP:LotteryConstants.STATUS_EXPIRE);
                matchs.add(match);
            }
            return matchs;
        } catch (Exception e){
            logger.error("[冠亚军对阵抓取] 解析对阵数据异常", e);
            return null;
        }
    }
}
