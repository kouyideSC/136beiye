package com.caipiao.service.common;

import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.ActivityMapper;
import com.caipiao.dao.common.AppMarketMapper;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.Activity;
import com.caipiao.domain.common.AppMarket;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 市场版本控制服务
 * Created by kouyi on 2018/1/5.
 */
@Service("marketVersionService")
public class MarketVersionService {
    private static Logger logger = LoggerFactory.getLogger(MarketVersionService.class);
    @Autowired
    private AppMarketMapper appMarketMapper;

    /**
     * 查询市场版本信息列表
     * @param params
     * @return
     */
    public void queryMarketVersionList(Dto params, ResultBean result) throws ServiceException {
        try {
            List<AppMarket> list = appMarketMapper.queryMarketVersionList(params);
            if(StringUtil.isNotEmpty(list)) {
                AppMarket market = list.get(0);
                params.remove("buildVersion");
                List<AppMarket> newList = appMarketMapper.queryMarketVersionList(params);
                if(StringUtil.isNotEmpty(newList)) {
                    AppMarket market2 = newList.get(0);
                    if(market2.getClientType()==1) {
                        market.setDownUrl(SysConfig.getHostStatic() + market2.getDownUrl());
                    } else {
                        market.setDownUrl(market2.getDownUrl());
                    }
                    market.setAppVersion(market2.getAppVersion());
                    market.setBuildVersion(market2.getBuildVersion());
                    market.setIsForceUpdate(market2.getIsForceUpdate());
                    market.setUpdateInfo(market2.getUpdateInfo());
                    market.setStatus(market2.getStatus());
                }
                result.setData(market);
            }
        } catch (Exception e) {
            logger.error("[查询市场版本信息列表] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

}
