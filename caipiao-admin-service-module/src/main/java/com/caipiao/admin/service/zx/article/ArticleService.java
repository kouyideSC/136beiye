package com.caipiao.admin.service.zx.article;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.scheme.SchemeUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.lottery.LotteryMapper;
import com.caipiao.dao.lottery.PeriodMapper;
import com.caipiao.dao.scheme.SchemeMapper;
import com.caipiao.dao.ticket.TicketMapper;
import com.caipiao.dao.user.UserAccountMapper;
import com.caipiao.dao.user.UserCouponMapper;
import com.caipiao.dao.user.UserDetailMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.dao.zx.ArticleMapper;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.scheme.SchemeZhuiHao;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.domain.user.UserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import java.text.MessageFormat;
import java.util.*;

/**
 * 文章管理-服务类
 */
@Service("articleService")
@Transactional
public class ArticleService
{
    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private LotteryMapper lotteryMapper;


    /**
     * 查询文章
     * @author	sjq
     */
    public List<Dto> queryArticles(Dto params)
    {
        return articleMapper.queryArticles(params);
    }

    /**
     * 查询文章总记录条数
     * @author	sjq
     */
    public int queryArticlesCount(Dto params)
    {
        return articleMapper.queryArticlesCount(params);
    }

    /**
     * 新增(发布)文章
     * @author	sjq
     */
    public int addArticle(Dto params)
    {
        if(StringUtil.isEmpty(params.get("title"))
                || StringUtil.isEmpty(params.getAsString("contents")))
        {
            params.put("dmsg","必要参数不能为空！");
            return 0;
        }
        if(StringUtil.isNotEmpty(params.get("lotteryId")))
        {
            Lottery lottery = lotteryMapper.queryLotteryInfo(params.getAsString("lotteryId"));
            if(lottery != null)
            {
                params.put("lotteryName",lottery.getName());//设置彩种名称
            }
        }
        return articleMapper.saveArticle(params);
    }

    /**
     * 编辑(更新)文章
     * @author	sjq
     */
    public int updateArticle(Dto params)
    {
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","必要参数id不能为空！");
            return 0;
        }
        return articleMapper.updateArticle(params);
    }

    /**
     * 删除文章(管理后台)
     * @author	sjq
     */
    public int deleteArticle(Dto params)
    {
        if(StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","必要参数id不能为空！");
            return 0;
        }
        return articleMapper.deleteArticle(params);
    }

    /**
     * 文章设置置顶(管理后台)
     * @author	sjq
     */
    public int updateArticleForZd(Dto params)
    {
        if(StringUtil.isEmpty(params.get("id")) || StringUtil.isEmpty(params.get("iszd")))
        {
            params.put("dmsg","必要参数不能为空!");
            return 0;
        }
        else if(!"0".equals(params.getAsString("iszd")) && !"1".equals(params.getAsString("iszd")))
        {
            params.put("dmsg","参数值iszd非法!");
            return 0;
        }
        return articleMapper.updateArticleForZd(params);
    }

    /**
     * 文章设置热门(管理后台)
     * @author	sjq
     */
    public int updateArticleForHot(Dto params)
    {
        if(StringUtil.isEmpty(params.get("id")) || StringUtil.isEmpty(params.get("ishot")))
        {
            params.put("dmsg","必要参数不能为空!");
            return 0;
        }
        else if(!"0".equals(params.getAsString("ishot")) && !"1".equals(params.getAsString("ishot")))
        {
            params.put("dmsg","参数值ishot非法!");
            return 0;
        }
        return articleMapper.updateArticleForHot(params);
    }
}