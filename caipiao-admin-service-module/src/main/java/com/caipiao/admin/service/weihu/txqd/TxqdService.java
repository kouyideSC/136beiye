package com.caipiao.admin.service.weihu.txqd;

import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.CouponMapper;
import com.caipiao.dao.common.PaymentWayMapper;
import com.caipiao.domain.common.PaymentWay;
import com.caipiao.domain.cpadmin.Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 提现渠道-服务类
 */
@Service("txqdService")
public class TxqdService
{
    @Autowired
    private PaymentWayMapper paymentWayMapper;

    /**
     * 查询提现渠道信息
     * @author	sjq
     */
    public List<Dto> queryPaymentWayInfo(Dto params)
    {
        return paymentWayMapper.queryPaymentWayInfo(params);
    }

    /**
     * 查询提现渠道总记录条数
     * @author	sjq
     */
    public int queryPaymentWayInfoCount(Dto params)
    {
        return paymentWayMapper.queryPaymentWayInfoCount(params);
    }

    /**
     * 编辑提现渠道
     * @author	sjq
     */
    public int editTxqd(Dto params) throws Exception
    {
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","缺少必要参数id!");
            return -1;
        }
        return paymentWayMapper.editTxqd(params);
    }
}