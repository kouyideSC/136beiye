package com.caipiao.service.match;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.BeanUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.match.MatchFootBallMapper;
import com.caipiao.dao.match.MatchGyjMapper;
import com.caipiao.domain.base.MatchBean;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.match.GyjMatch;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.vo.JczqAwardInfo;
import com.caipiao.domain.vo.JczqMatchVo;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 冠亚军相关业务处理服务
 * Created by kouyi on 2017/11/07.
 */
@Service("gyjMatchService")
public class GyjMatchService {
    private static Logger logger = LoggerFactory.getLogger(GyjMatchService.class);
    @Autowired
    private MatchGyjMapper matchGyjMapper;

    /**
     * 根据参数-查询冠亚军对阵列表
     * @param gyjMatch
     * @return
     */
    public List<GyjMatch> queryMatchFootBallList(GyjMatch gyjMatch) throws ServiceException, Exception {
        try {
            return matchGyjMapper.queryGyjMatchList(gyjMatch);
        } catch (Exception e) {
            logger.error("[查询冠亚军对阵列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 保存或更新冠亚军对阵信息
     * @param gyjMatch
     */
    public boolean saveOrUpdateMatch(GyjMatch gyjMatch) throws ServiceException, Exception {
        try {
            GyjMatch match = matchGyjMapper.queryGyjMatchInfo(gyjMatch.getLotteryId(), gyjMatch.getMatchCode());
            //对阵更新
            if(StringUtil.isNotEmpty(match)) {
                //取消|截止|数据没变化|后台手工设置的场次不自动更新
                if(match.getStatus().intValue() == LotteryConstants.STATUS_EXPIRE
                        || match.getStatus().intValue() == LotteryConstants.STATUS_CANCEL
                        || match.getMatchInfo().equals(gyjMatch.getMatchInfo()) || match.getUpdateFlag()) {
                    return false;
                }
                match.setTeamImg(gyjMatch.getTeamImg());
                match.setTeamName(gyjMatch.getTeamName());
                match.setTeamId(gyjMatch.getTeamId());
                match.setGuestTeamId(gyjMatch.getGuestTeamId());
                match.setGuestTeamImg(gyjMatch.getGuestTeamImg());
                match.setGuestTeamName(gyjMatch.getGuestTeamName());
                match.setSp(gyjMatch.getSp());
                match.setProbability(gyjMatch.getProbability());
                match.setStatus(gyjMatch.getStatus());
                matchGyjMapper.updateGyjMatch(match);
            }
            //该场次第一次抓取 直接入库
            else {
                matchGyjMapper.insertGyjMatch(gyjMatch);
            }
            return true;
        } catch (Exception e){
            logger.error("[保存或更新冠亚军对阵异常] matchCode=" + gyjMatch.getMatchCode(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }
}
