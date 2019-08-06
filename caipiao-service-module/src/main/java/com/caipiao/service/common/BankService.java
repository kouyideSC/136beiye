package com.caipiao.service.common;

import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.BankMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.Bank;
import com.caipiao.domain.common.BankSub;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 银行服务类
 * @author  mcdog
 */
@Service("bankService")
public class BankService
{
    private static Logger logger = LoggerFactory.getLogger(BankService.class);

    @Autowired
    private BankMapper bankMapper;

    /**
     * 获取银行
     * @author  mcdog
     * @param   params  查询参数对象
     * @param   result  处理结果对象
     */
    public void getBanks(Dto params,ResultBean result) throws ServiceException,Exception
    {
        params.put("status",1);//只查询状态为正常的银行
        List<Bank> dataList = bankMapper.queryBanks(params);//查询银行信息
        List<Dto> bankList = new ArrayList<Dto>();
        if(dataList != null && dataList.size() > 0)
        {
            //重新封装银行信息
            Dto bankDto = null;
            for(Bank bank : dataList)
            {
                bankDto = new BaseDto();
                bankDto.put("name",bank.getBankName());//设置银行名称
                bankDto.put("bcode",bank.getBankCode());//设置银行编码
                bankDto.put("logo", SysConfig.getHostStatic() + bank.getLogo());//设置银行logo
                bankDto.put("needsub",bank.getNeedSub() == 1? 1 : 0);//转账是否需要支行(0-不需要 1-需要)
                bankList.add(bankDto);
            }
        }
        result.setErrorCode(ErrorCode_API.SUCCESS);
        result.setData(bankList);
    }

    /**
     * 获取支行
     * @author  mcdog
     * @param   params  查询参数对象
     * @param   result  处理结果对象
     */
    public void getBankSubs(Dto params,ResultBean result) throws ServiceException,Exception
    {
        /**
         * 校验参数
         */
        if(StringUtil.isEmpty(params.get("bcode"))
                || StringUtil.isEmpty(params.get("acode")))
        {
            logger.error("[获取支行]参数校验不通过!确实必要参数,接收原始参数:" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        params.put("status",1);//只查询状态为正常的银行
        List<BankSub> dataList = bankMapper.queryBankSubs(params);//查询银行支行信息
        List<Dto> banksubList = new ArrayList<Dto>();
        if(dataList != null && dataList.size() > 0)
        {
            //重新封装银行支行信息
            Dto banksubDto = null;
            for(BankSub bankSub : dataList)
            {
                banksubDto = new BaseDto();
                banksubDto.put("subname",bankSub.getSubBankName());//设置银行支行名称
                banksubDto.put("bcode",bankSub.getBankCode());//设置支行所属银行编号
                banksubDto.put("acode",bankSub.getCityCode());//设置支行所属城市编号
                banksubList.add(banksubDto);
            }
        }
        result.setErrorCode(ErrorCode_API.SUCCESS);
        result.setData(banksubList);
    }
}
