package com.caipiao.taskcenter.code.util;


import com.caipiao.domain.lottery.Period;

/**
 * 抓取开奖号码接口
 * Created by kouyi on 2017/11/17.
 */
public interface GrabDrawCode {
    /**
     * 官网开奖号码抓取接口
     * @param period
     */
    public void GrabGuanWang(Period period);

    /**
     * 彩经网开奖号码抓取接口
     * @param period
     */
    public void GrabCaiJingWang(Period period);

    /**
     * 360开奖号码抓取接口
     * @param period
     */
    public void Grab360(Period period);

    /**
     * 网易开奖号码抓取接口
     * @param period
     */
    public void GrabWangYi(Period period);

}
