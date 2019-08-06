package com.caipiao.admin.service.weihu.market;

import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.AppMarketMapper;
import com.caipiao.dao.ticket.TicketMapper;
import com.caipiao.dao.ticket.TicketVoteMapper;
import com.caipiao.domain.common.AppMarket;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 版本控制相关服务
 * Created by kouyi on 2018/01/04.
 */
@Service("marketService")
public class MarketService
{
    @Autowired
    private AppMarketMapper appMarketMapper;

    /**
     * 发布市场版本
     * @param params
     * @return
     */
    public int insertMarketVersion(Dto params) {
        AppMarket market = new AppMarket();
        market.setAppName(params.getAsString("appName"));
        market.setClientType(params.getAsInteger("clientType"));
        market.setVersionType(params.getAsInteger("versionType"));
        market.setMarketId(params.getAsInteger("marketId"));
        market.setAppVersion(params.getAsString("appVersion"));
        market.setBuildVersion(params.getAsString("buildVersion"));
        market.setDownUrl(params.getAsString("downUrl"));
        if(market.getClientType() == 0) {
            market.setDownUrl(market.getDownUrl().substring(1));
        } else {
            market.setDownUrl(market.getDownUrl().substring(0, market.getDownUrl().length()-1));
        }
        market.setStatus(params.getAsInteger("status"));
        market.setIsForceUpdate(params.getAsInteger("isForceUpdate"));
        market.setUpdateInfo(params.getAsString("updateInfo"));
        return appMarketMapper.insertMarketVersion(market);
    }

    /**
     * 删除市场版本
     * @param params
     * @return
     */
    public int deleteMarketVersion(Dto params) {
        if(StringUtil.isEmpty(params.getAsInteger("id"))) {
            return 0;
        }
        return appMarketMapper.deleteMarketVersion(params.getAsInteger("id"));
    }

    /**
     * 更新市场版本
     * @param params
     * @return
     */
    public int updateMarketVersion(Dto params) {
        if(StringUtil.isEmpty(params.getAsInteger("id"))) {
            return 0;
        }
        if(StringUtil.isNotEmpty(params.getAsString("downUrl"))) {
            if (params.getAsInteger("clientType") == 0) {
                params.put("downUrl", params.getAsString("downUrl").substring(1));
            } else {
                params.put("downUrl", params.getAsString("downUrl").substring(0, params.getAsString("downUrl").length() - 1));
            }
        }
        return appMarketMapper.updateMarketVersion(params);
    }

    /**
     * 查询市场版本列表
     * @param params
     * @return
     */
    public List<AppMarket> queryMarketVersionList(Dto params) {
        return appMarketMapper.queryMarketVersionList(params);
    }

    /**
     * 查询市场版本列表
     * @return
     */
    public List<Dto> queryMarketList() {
        return appMarketMapper.queryMarketList();
    }
}
