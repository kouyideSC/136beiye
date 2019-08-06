<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<style>
.btn-scheme-detail-active{
    background: #26bf8c;
    color: #fff;
}
.btn-scheme-detail-active:hover{
    background: #26bf8c;
    color: #fff;
}
.btn-scheme-detail-active:link{
    background: #26bf8c;
    color: #fff;
}
.btn-scheme-detail-active:focus{
    background: #26bf8c;
    color: #fff;
}
.dt-scheme-dzinfo{
    margin-right:8px;
}
</style>
<div class="card-header" id="dt_article_card">
    <div class="planflom-header">
        <div class="card-header-title clearfix">
            <button type="button" class="card-close plus-icon p-guanbi"></button>
            <div class="pull-left clearfix" style="margin-top: 6px" id="dtInfoTabs">
                <button type="button" class="btn btn-scheme-detail-active" tbsvalue="base">基本信息</button>
                <button type="button" class="btn" style="margin-left: -2px;" tbsvalue="detail">文章预览</button>
                <button opauthority="btn_zxgl_article_edit" type="button" class="btn btn-danger" id="dt_editArticleBtns">编辑</button>
            </div>
        </div>
        <div class="card-header-con">
            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_base">
                <li class="abstract-item">
                    <span class="abstract-label">文章类型</span>
                    <span class="abstract-value">${params.articleType == 0? "推荐" : (params.articleType == 1? "预测" : "情报")}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">关联彩种</span>
                    <span class="abstract-value">${params.lotteryName}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">关联场次/期次</span>
                    <span class="abstract-value">${params.mcode}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">是否置顶</span>
                    <c:choose>
                        <c:when test="${params.iszd == 1}">
                            <span class="abstract-value label label-success">已置顶</span>
                        </c:when>
                        <c:otherwise>
                            <span class="abstract-value label label-primary">否</span>
                        </c:otherwise>
                    </c:choose>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">是否热门</span>
                    <c:choose>
                        <c:when test="${params.ishot == 1}">
                            <span class="abstract-value label label-success">热门</span>
                        </c:when>
                        <c:otherwise>
                            <span class="abstract-value label label-primary">否</span>
                        </c:otherwise>
                    </c:choose>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">作者</span>
                    <span class="abstract-value">${params.author}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">发布时间</span>
                    <span class="abstract-value">${params.createTime}</span>
                </li>
                <li class="abstract-item" style="width: 64%;">
                    <span class="abstract-label">标题</span>
                    <span class="abstract-value">${params.title}</span>
                </li>
                <li class="abstract-item" style="width: 96%;">
                    <span class="abstract-label">标签</span>
                    <span class="abstract-value">${params.tags}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">logo</span>
                </li>
                <div class="col-sm-12" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                    <img src="${params.logo}">
                </div>
            </ul>
            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_detail" style="display: none;border-bottom: none;">
                <div class="col-sm-12" style="margin-top:8px;" id="dt_articleContents"></div>
            </ul>
        </div>
    </div>
</div>
<div class="modal fade" id="dt_operatorModal" aria-hidden="true" data-backdrop="static"></div>
<script>
$(function()
{
    initCreatedNode('#dt_article_card');
    //点击tabs
    $('#dtInfoTabs button[tbsvalue]').on('click',function()
    {
        $('#dtInfoTabs button[tbsvalue]').removeClass('btn-scheme-detail-active');
        $(this).addClass('btn-scheme-detail-active');
        $('.dtInfo_cls').hide();
        $('#dtInfo_' + $(this).attr('tbsvalue')).show();
    });
    //点击编辑
    $('#dt_editArticleBtns').on('click',function()
    {
        $('#dt_operatorModal').fillWithUrl('${pageContext.request.contextPath}/zxgl/article/initEdit?id=${params.id}');
        $('#dt_operatorModal').modal('show');
    });
    //初始化文章内容
    $('#dt_articleContents').html('${params.contents}');
});
</script>