package com.caipiao.admin.service.weihu.erroraward;

import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.CouponMapper;
import com.caipiao.dao.lottery.PeriodMapper;
import com.caipiao.dao.match.MatchBasketBallMapper;
import com.caipiao.dao.match.MatchFootBallMapper;
import com.caipiao.dao.scheme.SchemeMapper;
import com.caipiao.domain.cpadmin.Dto;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 重新计奖-服务类
 */
@Service("errorAwardService")
public class ErrorAwardService
{
    @Autowired
    private SchemeMapper schemeMapper;
    @Autowired
    private MatchFootBallMapper matchFootBallMapper;
    @Autowired
    private MatchBasketBallMapper matchBasketBallMapper;
    @Autowired
    private PeriodMapper periodMapper;

    /**
     * 审核错误回退重新计奖
     * @author	sjq
     */
    public int updateErrorReAward(Dto params) throws Exception
    {
        int result = 0;
        if(StringUtil.isEmpty(params.getAsString("datas")))
        {
            return result;
        }
        JSONObject jsonObject = JSONObject.fromObject(params.getAsString("datas"));//解析参数串
        //彩种不能为空
        String lotteryId = jsonObject.getString("lotteryId");
        if(StringUtil.isEmpty(params)) {
            return result;
        }
        params.put("lotteryId", lotteryId);
        //场次号不能为空
        String matchCode = jsonObject.getString("matchCode");
        if(StringUtil.isEmpty(matchCode)) {
            return result;
        }
        params.put("matchCode", matchCode);
        params.remove("datas");

        if(LotteryUtils.isJc(lotteryId)) {
            //比赛状态回退
            if(LotteryUtils.isJczq(lotteryId)) {
                result = matchFootBallMapper.updateMatchRebackState(params);
            } else {
                result = matchBasketBallMapper.updateMatchRebackState(params);
            }

            //方案数据回退
            result = schemeMapper.updateErrorReAwardSchemeJc(params);
            //票数据回退
            result = schemeMapper.updateErrorReAwardTicketJc(params);
        } else {
            //期次状态回退
            result = periodMapper.updatePeriodRebackState(params);

            //普通方案数据回退
            result = schemeMapper.updateErrorReAwardSchemeSzc(params);
            //追号方案数据回退
            result = schemeMapper.updateErrorReAwardSchemeSzcZhuiHao(params);

            //普通票数据回退
            result = schemeMapper.updateErrorReAwardTicketSzc(params);
            //追号票数据回退
            result = schemeMapper.updateErrorReAwardTicketSzcZhuiHao(params);
        }
        return result;
    }
}