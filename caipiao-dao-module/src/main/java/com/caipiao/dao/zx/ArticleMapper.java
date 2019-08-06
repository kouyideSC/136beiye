package com.caipiao.dao.zx;

import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章-数据库访问接口
 * @author  mcdog
 */
public interface ArticleMapper
{
    /**
     * 查询文章(管理后台)
     * @author	sjq
     */
    List<Dto> queryArticles(Dto params);
    /**
     * 查询文章总记录条数(管理后台)
     * @author	sjq
     */
    int queryArticlesCount(Dto params);
    /**
     * 新增(发布)文章(管理后台)
     * @author	sjq
     */
    int saveArticle(Dto params);
    /**
     * 编辑(更新)文章(管理后台)
     * @author	sjq
     */
    int updateArticle(Dto params);
    /**
     * 删除文章(管理后台)
     * @author	sjq
     */
    int deleteArticle(Dto params);
    /**
     * 文章设置置顶(管理后台)
     * @author	sjq
     */
    int updateArticleForZd(Dto params);
    /**
     * 文章设置热门(管理后台)
     * @author	sjq
     */
    int updateArticleForHot(Dto params);
}