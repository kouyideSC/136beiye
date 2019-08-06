package com.caipiao.dao.lottery;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.vo.KaiJiangVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 资讯-数据库访问接口
 * @author  mcdog
 */
public interface ZxMapper
{
    /**
     * 查询推荐
     * @author  mcdog
     */
    List<Dto> queryTj(Dto params);
    /**
     * 查询推荐总记录条数
     * @author  mcdog
     */
    long queryTjCount(Dto params);

    /**
     * 查询热门比赛
     * @author  mcdog
     */
    List<Dto> queryHotMatch(Dto params);
}