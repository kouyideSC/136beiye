package com.caipiao.admin.service.weihu.cqfs;

import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.PayWayMapper;
import com.caipiao.domain.cpadmin.Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 充值方式-服务类
 */
@Service("czfsService")
public class CzfsService
{
    @Autowired
    private PayWayMapper payWayMapper;

    /**
     * 查询充值方式信息
     * @author	sjq
     */
    public List<Dto> queryPayWayInfos(Dto params)
    {
        return payWayMapper.queryPayWayInfos(params);
    }

    /**
     * 查询充值方式总记录条数
     * @author	sjq
     */
    public int queryPaymentWayInfoCount(Dto params)
    {
        return payWayMapper.queryPayWayInfosCount(params);
    }

    /**
     * 编辑方式渠道
     * @author	sjq
     */
    public int editPayway(Dto params) throws Exception
    {
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","缺少必要参数id!");
            return -1;
        }
        return payWayMapper.editPayway(params);
    }
}