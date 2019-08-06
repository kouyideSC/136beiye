package com.caipiao.admin.zxgl.article;

import com.caipiao.admin.annotation.ModuleAuthorityRequired;
import com.caipiao.admin.base.BaseController;
import com.caipiao.admin.service.config.SysConfig;
import com.caipiao.admin.service.zx.article.ArticleService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 文章管理-控制类
 */
@Controller
@RequestMapping("/zxgl/article")
public class ArticleController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;

    /**
     * 显示文章发布-首页
     * @author  mcdog
     */
    @RequestMapping("/index")
    @ModuleAuthorityRequired(mcode = "menu_zxgl_article")
    public String initIndex(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        map.put("params",new BaseDto("staticHost", SysConfig.getHostStatic()));//获取静态文件域名
        return "zxgl/article/index";
    }

    /**
     * 显示编辑文章页面
     * @author  mcdog
     */
    @RequestMapping("/initEdit")
    @ModuleAuthorityRequired(mcode = "btn_zxgl_article_edit")
    public String initEdit(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> articleList = articleService.queryArticles(params);
        if(articleList != null && articleList.size() > 0)
        {
            Dto dataDto = articleList.get(0);
            dataDto.putAll(articleList.get(0));
            if(StringUtil.isNotEmpty(dataDto.get("logo")))
            {
                dataDto.put("logo",SysConfig.getHostStatic() + dataDto.getAsString("logo"));
            }
            map.addAttribute("params",dataDto);
        }
        return "zxgl/article/edit";
    }

    /**
     * 显示发布文章页面
     * @author  mcdog
     */
    @RequestMapping("/initAdd")
    @ModuleAuthorityRequired(mcode = "btn_zxgl_article_add")
    public String initAdd(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        return "zxgl/article/add";
    }

    /**
     * 显示文章详细页
     * @author  mcdog
     */
    @RequestMapping("/detail")
    @ModuleAuthorityRequired(mcode = "menu_zxgl_article")
    public String detail(ModelMap map,HttpServletRequest request, HttpServletResponse response)
    {
        BaseDto params = WebUtils.getPraramsAsDto(request);
        List<Dto> articleList = articleService.queryArticles(params);
        if(articleList != null && articleList.size() > 0)
        {
            Dto dataDto = articleList.get(0);
            dataDto.putAll(articleList.get(0));
            if(StringUtil.isNotEmpty(dataDto.get("logo")))
            {
                dataDto.put("logo",SysConfig.getHostStatic() + dataDto.getAsString("logo"));
            }
            map.addAttribute("params",dataDto);
        }
        return "zxgl/article/detail";
    }


    /**
     * 查询文章
     * @author  mcdog
     */
    @RequestMapping("/get")
    @ModuleAuthorityRequired(mcode = "menu_zxgl_article")
    public void getArticles(HttpServletRequest request, HttpServletResponse response)
    {
        Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
        Dto resultDto = new BaseDto("dcode", ConstantUtils.OPERATION_FAILURE);//返回数据对象
        try
        {
            Dto dataDto = new BaseDto("list",articleService.queryArticles(params));//查询文章
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                dataDto.put("tsize",articleService.queryArticlesCount(params));//查询文章总记录条数
            }
            resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
            resultDto.put("datas",dataDto);
        }
        catch(Exception e)
        {
            logger.error("[查询文章]发生异常,异常信息：",e);
            resultDto.put("dmsg",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 新增(发布)文章
     * @author  mcdog
     */
    @RequestMapping("/add")
    @ModuleAuthorityRequired(mcode = "btn_zxgl_article_add")
    public void addArticle(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
            logger.info("[发布文章]操作帐户=" + params.getAsString("current_login_personal") + ",接收原始参数:" + params.toString());
            if(articleService.addArticle(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","发布成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[发布文章]发生异常!异常信息:" + e);
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 编辑(更新)文章
     * @author  mcdog
     */
    @RequestMapping("/edit")
    @ModuleAuthorityRequired(mcode = "btn_zxgl_article_edit")
    public void editArticle(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
            logger.info("[编辑文章]操作帐户=" + params.getAsString("current_login_personal" + ",接收原始参数:" + params.toString()));
            if(articleService.updateArticle(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","编辑成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[编辑文章]发生异常!异常信息:" + e);
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 删除文章
     * @author  mcdog
     */
    @RequestMapping("/delete")
    @ModuleAuthorityRequired(mcode = "btn_zxgl_article_delete")
    public void deleteArticle(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
            logger.info("[删除文章]操作帐户=" + params.getAsString("current_login_personal" + ",接收原始参数:" + params.toString()));
            if(articleService.deleteArticle(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","删除成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[删除文章]发生异常!异常信息:" + e);
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 文章设置置顶
     * @author  mcdog
     */
    @RequestMapping("/setzd")
    @ModuleAuthorityRequired(mcode = "btn_zxgl_article_edit")
    public void settingArticleZd(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
            logger.info("[文章设置置顶]操作帐户=" + params.getAsString("current_login_personal" + ",接收原始参数:" + params.toString()));
            if(articleService.updateArticleForZd(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","文章置顶成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[文章设置置顶]发生异常!异常信息:" + e);
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 文章设置热门
     * @author  mcdog
     */
    @RequestMapping("/sethot")
    @ModuleAuthorityRequired(mcode = "btn_zxgl_article_edit")
    public void settingArticleHot(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);//返回数据对象;
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//初始化请求参数
            logger.info("[文章设置热门]操作帐户=" + params.getAsString("current_login_personal" + ",接收原始参数:" + params.toString()));
            if(articleService.updateArticleForHot(params) > 0)
            {
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("dmsg","设置热门成功");
            }
            else
            {
                resultDto.put("dmsg",params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[文章设置热门]发生异常!异常信息:" + e);
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 上传logo
     * @author  mcdog
     * @return  fpath   logo上传后的文件路径(包含文件名)
     */
    @RequestMapping("/uploadLogo")
    private void uploadLogo(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("dcode",ConstantUtils.OPERATION_FAILURE);
        try
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> mfileMap = multipartRequest.getFileMap();
            if(mfileMap != null && mfileMap.size() > 0 && mfileMap.get("uplogo") != null)
            {
                //初始化文件上传参数
                MultipartFile multipartFile = mfileMap.get("uplogo");
                String fileName = multipartFile.getOriginalFilename();//提取文件名
                String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);//提取文件后缀
                String staticPath = SysConfig.getString("static.file.path");//从配置文件中提取静态文件根路径
                String fpath = SysConfig.getString("zx.article.logo.path");//从配置文件中提取文章logo上传路径

                //上传文件
                File file = new File(staticPath + fpath);
                if(!file.exists())
                {
                    file.mkdirs();
                }
                String fname = System.currentTimeMillis() + "." + fileSuffix;
                fpath = fpath + fname;
                multipartFile.transferTo(new File(staticPath + fpath));
                resultDto.put("dcode",ConstantUtils.OPERATION_SUCCESS);
                resultDto.put("datas",new BaseDto("fpath",fpath));//设置文件路径
            }
        }
        catch (Exception e)
        {
            resultDto.put("dmsg","上传logo发生异常,异常信息:" + e);
            logger.error("[上传logo]发生异常,异常信息:" + e);
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 文件上传
     * @author  mcdog
     */
    @RequestMapping("/upload")
    public void uploadFile(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto();
        try
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> mfileMap = multipartRequest.getFileMap();
            if(mfileMap != null && mfileMap.size() > 0 && mfileMap.get("upfile") != null)
            {
                //初始化文件上传参数
                MultipartFile multipartFile = mfileMap.get("upfile");
                String fileName = multipartFile.getOriginalFilename();//提取文件名
                String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);//提取文件后缀
                String staticPath = SysConfig.getString("static.file.path");//从配置文件中提取静态文件根路径
                String fpath = SysConfig.getString("zx.article.content.path");//从配置文件中提取文章内容图片上传路径

                //上传文件
                File file = new File(staticPath + fpath);
                if(!file.exists())
                {
                    file.mkdirs();
                }
                String fname = System.currentTimeMillis() + "." + fileSuffix;
                fpath = fpath + fname;
                long size = multipartFile.getSize();
                multipartFile.transferTo(new File(staticPath + fpath));

                //设置返回结果
                resultDto.put("name",fname);//设置上传文件名
                resultDto.put("original",fileName);//设置源文件名
                resultDto.put("size",size);//设置文件大小
                resultDto.put("state","SUCCESS");//设置文件上传状态
                resultDto.put("type",("." + fileSuffix));//设置文件后缀
                resultDto.put("url",fpath);//设置文件路径
            }
        }
        catch (Exception e)
        {
            logger.error("[文章发布]上传文件发生异常,异常信息:" + e);
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}
