package com.caipiao.grab.jc.handler;

import com.caipiao.common.http.Grab;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.match.MatchFootBallSp;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 抓取竞彩足球对阵赔率
 * Created by kouyi on 2017/11/08.
 */
@Component("grabForJczqMatchSp")
public class GrabForJczqMatchSp extends Grab<MatchFootBallSp, String> {
    private static Logger logger = LoggerFactory.getLogger(GrabForJczqMatchSp.class);

    @Override
    public MatchFootBallSp parse(String content, String matchCode) {
        try{
            if(StringUtil.isEmpty(content)){
                logger.info("[竞彩足球赔率抓取] 场次号="+matchCode+"数据为空!");
                return null;
            }
            if(content.startsWith("pool_prcess(")) {
                content = content.replaceFirst("pool_prcess\\(","");
            }
            if(content.endsWith(");")) {
                content = content.substring(0, content.length()-2);
            }
            JSONObject jsonObj = JSONObject.fromObject(content);
            String code = jsonObj.getJSONObject("status").get("code").toString();
            if (!code.equals("0")) {
                logger.info("[竞彩足球赔率抓取] 场次号="+matchCode+"无数据(code=" + code + ")!");
                return null;
            }
            MatchFootBallSp matchSp = new MatchFootBallSp();
            //解析胜平负赔率
            try {
                JSONArray spfArray = jsonObj.getJSONObject("result").getJSONObject("odds_list").getJSONObject("had").getJSONArray("odds");
                if (StringUtil.isNotEmpty(spfArray)) {
                    JSONObject spfObject = spfArray.getJSONObject(spfArray.size() - 1);
                    matchSp.setSheng(spfObject.getDouble("h"));
                    matchSp.setPing(spfObject.getDouble("d"));
                    matchSp.setFu(spfObject.getDouble("a"));
                }
            } catch (Exception e) {
                matchSp.setSheng(0d);
                matchSp.setPing(0d);
                matchSp.setFu(0d);
                logger.info("[竞彩足球赔率抓取] 场次号matchCode="+matchCode+"胜平负玩法未开售");
            }

            //解析让球胜平负赔率
            try {
                JSONArray rqspfArray = jsonObj.getJSONObject("result").getJSONObject("odds_list").getJSONObject("hhad").getJSONArray("odds");
                if (StringUtil.isNotEmpty(rqspfArray)) {
                    JSONObject rqspfObject = rqspfArray.getJSONObject(rqspfArray.size() - 1);
                    matchSp.setLose(rqspfObject.getInt("goalline"));
                    matchSp.setrSheng(rqspfObject.getDouble("h"));
                    matchSp.setrPing(rqspfObject.getDouble("d"));
                    matchSp.setRfu(rqspfObject.getDouble("a"));
                }
            } catch (Exception e) {
                matchSp.setLose(0);
                matchSp.setrSheng(0d);
                matchSp.setrPing(0d);
                matchSp.setRfu(0d);
                logger.info("[竞彩足球赔率抓取] 场次号matchCode="+matchCode+"让球胜平负玩法未开售");
            }

            //解析总进球赔率
            try {
                JSONArray zjqArray = jsonObj.getJSONObject("result").getJSONObject("odds_list").getJSONObject("ttg").getJSONArray("odds");
                if (StringUtil.isNotEmpty(zjqArray)) {
                    JSONObject zjqObject = zjqArray.getJSONObject(zjqArray.size() - 1);
                    matchSp.setT0(zjqObject.getDouble("s0"));
                    matchSp.setT1(zjqObject.getDouble("s1"));
                    matchSp.setT2(zjqObject.getDouble("s2"));
                    matchSp.setT3(zjqObject.getDouble("s3"));
                    matchSp.setT4(zjqObject.getDouble("s4"));
                    matchSp.setT5(zjqObject.getDouble("s5"));
                    matchSp.setT6(zjqObject.getDouble("s6"));
                    matchSp.setT7(zjqObject.getDouble("s7"));
                }
            } catch (Exception e) {
                logger.info("[竞彩足球赔率抓取] 场次号matchCode="+matchCode+"总进球玩法未开售");
            }

            //解析半全场赔率
            try {
                JSONArray bqcArray = jsonObj.getJSONObject("result").getJSONObject("odds_list").getJSONObject("hafu").getJSONArray("odds");
                if (StringUtil.isNotEmpty(bqcArray)) {
                    JSONObject bqcObject = bqcArray.getJSONObject(bqcArray.size() - 1);
                    matchSp.setSs(bqcObject.getDouble("hh"));
                    matchSp.setSp(bqcObject.getDouble("hd"));
                    matchSp.setSf(bqcObject.getDouble("ha"));
                    matchSp.setPs(bqcObject.getDouble("dh"));
                    matchSp.setPp(bqcObject.getDouble("dd"));
                    matchSp.setPf(bqcObject.getDouble("da"));
                    matchSp.setFs(bqcObject.getDouble("ah"));
                    matchSp.setFp(bqcObject.getDouble("ad"));
                    matchSp.setFf(bqcObject.getDouble("aa"));
                }
            } catch (Exception e) {
                logger.info("[竞彩足球赔率抓取] 场次号matchCode="+matchCode+"半全场玩法未开售");
            }

            //解析比分赔率
            try {
                JSONArray bfArray = jsonObj.getJSONObject("result").getJSONObject("odds_list").getJSONObject("crs").getJSONArray("odds");
                if (StringUtil.isNotEmpty(bfArray)) {
                    JSONObject bfObject = bfArray.getJSONObject(bfArray.size() - 1);
                    matchSp.setS10(bfObject.getDouble("0100"));
                    matchSp.setS20(bfObject.getDouble("0200"));
                    matchSp.setS21(bfObject.getDouble("0201"));
                    matchSp.setS30(bfObject.getDouble("0300"));
                    matchSp.setS31(bfObject.getDouble("0301"));
                    matchSp.setS32(bfObject.getDouble("0302"));
                    matchSp.setS40(bfObject.getDouble("0400"));
                    matchSp.setS41(bfObject.getDouble("0401"));
                    matchSp.setS42(bfObject.getDouble("0402"));
                    matchSp.setS50(bfObject.getDouble("0500"));
                    matchSp.setS51(bfObject.getDouble("0501"));
                    matchSp.setS52(bfObject.getDouble("0502"));
                    matchSp.setsOther(bfObject.getDouble("-1-h"));
                    matchSp.setP00(bfObject.getDouble("0000"));
                    matchSp.setP11(bfObject.getDouble("0101"));
                    matchSp.setP22(bfObject.getDouble("0202"));
                    matchSp.setP33(bfObject.getDouble("0303"));
                    matchSp.setpOther(bfObject.getDouble("-1-d"));
                    matchSp.setF01(bfObject.getDouble("0001"));
                    matchSp.setF02(bfObject.getDouble("0002"));
                    matchSp.setF12(bfObject.getDouble("0102"));
                    matchSp.setF03(bfObject.getDouble("0003"));
                    matchSp.setF13(bfObject.getDouble("0103"));
                    matchSp.setF23(bfObject.getDouble("0203"));
                    matchSp.setF04(bfObject.getDouble("0004"));
                    matchSp.setF14(bfObject.getDouble("0104"));
                    matchSp.setF24(bfObject.getDouble("0204"));
                    matchSp.setF05(bfObject.getDouble("0005"));
                    matchSp.setF15(bfObject.getDouble("0105"));
                    matchSp.setF25(bfObject.getDouble("0205"));
                    matchSp.setfOther(bfObject.getDouble("-1-a"));
                }
            } catch (Exception e) {
                logger.info("[竞彩足球赔率抓取] 场次号matchCode="+matchCode+"比分玩法未开售");
            }
            return matchSp;
        } catch (Exception e){
            logger.error("[竞彩足球赔率抓取] 解析对阵(matchCode=" + matchCode + ")数据异常[content=" + content + "]", e);
            return null;
        }
    }

}
