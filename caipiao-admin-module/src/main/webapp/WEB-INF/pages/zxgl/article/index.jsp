<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_zxgl");
    request.setAttribute("sidebar_mcode","menu_zxgl_article");
%>
<html lang="en" class="app">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
<title>资讯管理-文章管理</title>
<meta name="description" content="">
<meta name="keywords" content="">
<meta name="author" content="PLUS">
<meta charset="UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/font/iconfont.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/side-nav-bar.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/defaultTheme.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/myTheme.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/datepicker/daterangepicker.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-select.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css?t=<%=Math.random()%>">
<script src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/umeditor/themes/default/css/umeditor.css">
<style type="text/css">
    body {
        background-color: #efeff4;
    }
    .table thead tr {
        color: #666;
        font-size: 15px;
        background-color: #f9f9fb;
    }
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/datepicker/moment.js"></script>
<script src="${pageContext.request.contextPath}/js/datepicker/daterangepicker.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.fixedheadertable.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-select.js"></script>
<script src="${pageContext.request.contextPath}/js/util.js?t=<%=Math.random()%>"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-select.js"></script>
<script src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all.js"></script>
<script src="${pageContext.request.contextPath}/js/common.js?t=<%=Math.random()%>"></script>
<script src="${pageContext.request.contextPath}/js/cp.page.js?t=<%=Math.random()%>"></script>
<script src="${pageContext.request.contextPath}/js/cp.fixtable.js?t=<%=Math.random()%>"></script>
<script src="${pageContext.request.contextPath}/js/jQueryuploadfile/jquery.ui.widget.js"></script>
<script src="${pageContext.request.contextPath}/js/jQueryuploadfile/jquery.iframe-transport.js"></script>
<script src="${pageContext.request.contextPath}/js/jQueryuploadfile/jquery.fileupload.js"></script>
<script src="${pageContext.request.contextPath}/js/jQueryuploadfile/cors/jquery.xdr-transport.js"></script>
<script src="${pageContext.request.contextPath}/js/timeUtils.js"></script>
</head>
<body class="modal-open1">
<div class="main-content">
    <div class="whitebox">
        <div class="clearfix status-options operate" callback="querydatas(1);">
            <div class="clearfix">
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <select class="form-control" title="关联彩种" name="lotteryId" id="lotterySelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <select class="form-control selectpicker" title="文章类型" name="articleType" id="articleTypeSelect">
                        <option value="0">推荐</option>
                        <option value="1">预测</option>
                        <option value="2">情报</option>
                    </select>
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 180px;">
                    <input type="text" class="form-control datetimepicker" title="最早发布时间" placeholder="最早发布时间" name="minCreateTime">
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 180px">
                    <input type="text" class="form-control datetimepicker" title="最晚发布时间" placeholder="最晚发布时间" name="maxCreateTime">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition" style="padding-left: 0;">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_zxgl_article_add" type="button" class="btn btn-success" id="addArticleBtn" style="margin-right:20px;">发表文章</button>
                </div>
            </div>
        </div>
        <div class="core clearfix">
            <div class="core-table pull-left" style="position: relative; width:100%">
                <a href="javascript:;" class="edit-item" data-toggle="modal" data-target="#edit-item" style=" z-index: 500;"><i class="plus-icon p-setting"></i></a>
                <table id="dataTables" style="" border="0" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th>操作</th>
                        <th scolumn="id">编号</th>
                        <th scolumn="articleType">类型</th>
                        <th scolumn="lotteryName">关联彩种</th>
                        <th scolumn="mcode">关联场次/期次</th>
                        <th scolumn="title" style="max-width: 200px;">标题</th>
                        <th scolumn="ishot">是否热门</th>
                        <th scolumn="iszd">是否置顶</th>
                        <%--<th scolumn="contents" style="max-width: 250px;">内容</th>--%>
                        <th scolumn="tags" style="max-width: 120px;">标签</th>
                        <th scolumn="author">作者</th>
                        <th scolumn="createTime">发布时间</th>
                    </tr>
                    </thead>
                    <tbody id="dataTbody"></tbody>
                </table>
                <div class="pull-right pagelist_cls" funcname="querydatas">
                    <jsp:include page="../../base/pagination.jsp"></jsp:include>
                </div>
            </div>
        </div>
    </div>
</div>
<!--表格列编辑-->
<div class="modal fade" id="edit-item" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" useStaticDialog="1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4 class="modal-title" id="myModalLabel">编辑显示字段</h4>
            </div>
            <div class="modal-body clearfix clearfix">
                <div class="word-list pull-left">
                    <p class="word-list-title">隐藏字段</p>
                    <div class="list-box">
                        <ul class="list-box-hide">
                        </ul>
                    </div>
                </div>
                <div class="move-button pull-left">
                    <button class="add plus-icon p-right"></button>
                    <button class="subtract plus-icon p-left"></button>
                </div>
                <div class="word-list-show pull-left">
                    <p class="word-list-title">显示字段</p>
                    <div class="list-box">
                        <ul class="list-box-show">
                            <li scolumn="id">编号</li>
                            <li scolumn="articleType">类型</li>
                            <li scolumn="lotteryName">关联彩种</li>
                            <li scolumn="mcode">关联场次/期次</li>
                            <li scolumn="title">标题</li>
                            <li scolumn="ishot">是否热门</li>
                            <li scolumn="iszd">是否置顶</li>
                            <%--<li scolumn="contents">内容</li>--%>
                            <li scolumn="tags">标签</li>
                            <li scolumn="author">作者</li>
                            <li scolumn="createTime">发布时间</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-save save_scolumn_setting">保存设置</button>
            </div>
        </div>
    </div>
</div>
<div class="tooltips"></div>
<div class="modal fade" id="operatorModal"></div>
<div class="card-wrap" id="detailCard"></div>
<div class="modal fade" id="confirmModal" useStaticDialog="1" style="z-index:1050;">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title">操作确认</h4>
            </div>
            <div class="modal-body clearfix">
                <p style="margin-top: 10px;"></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-save alertwarn btn_modal_cancel" data-dismiss="modal" id="confirmModalSureBtn">确定</button>
            </div>
        </div>
    </div>
</div>
</body>
<script>
var typeJsons = {0:'推荐',1:'预测',2:'情报'};
var tbdatas = new Object();//表格数据对象
$(function ()
{
    //彩种名称下拉
    $('#lotterySelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
        id: 'id',
        name: 'name'
    });
    //点击发布(新增)
    $(document).on('click','#addArticleBtn',function()
    {
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/zxgl/article/initAdd');
        $('#operatorModal').attr('optype',1).modal('show');
    });
    //点击编辑
    $(document).on('click','.edit_article_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/zxgl/article/initEdit?id=' + datas['id']);
        $('#operatorModal').attr('optype',2).modal('show');
    });
    //设置置顶
    $(document).on('click','.zd_article_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').first().html('确定将文章【' + datas['title'] + '】' + (datas['iszd'] == 1? '取消置顶' : '置顶') + '吗？');
        $confirmModal.attr('optype',1);//设置操作类型
        $confirmModal.attr('opdatas',JSON.stringify({id:datas['id'],iszd:datas['iszd'] == 1? 0 : 1}));
        $confirmModal.modal('show');
    });
    //设置热门
    $(document).on('click','.hot_article_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('确定将文章【' + datas['title'] + '】' + (datas['ishot'] == 1? '取消热门' : '设为热门') + '吗？');
        $confirmModal.attr('optype',2);//设置操作类型
        $confirmModal.attr('opdatas',JSON.stringify({id:datas['id'],ishot:datas['ishot'] == 1? 0 : 1}));
        $confirmModal.modal('show');
    });
    //删除
    $(document).on('click','.delete_article_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('确定将文章【' + datas['title'] + '】删除吗？');
        $confirmModal.attr('optype',3);//设置操作类型
        $confirmModal.attr('opdatas',JSON.stringify({id:datas['id']}));
        $confirmModal.modal('show');
    });
    //点击确认操作
    $(document).on('click','#confirmModalSureBtn',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        var optype = $confirmModal.attr('optype');//获取操作类型
        var opdatas = JSON.parse($confirmModal.attr('opdatas'));

        //根据操作类型获取url
        var url = '';
        if(optype == 1)
        {
            url = '${pageContext.request.contextPath}/zxgl/article/setzd';//文章置顶
        }
        else if(optype == 2)
        {
            url = '${pageContext.request.contextPath}/zxgl/article/sethot';//设置文章为热门
        }
        else if(optype == 3)
        {
            url = '${pageContext.request.contextPath}/zxgl/article/delete';//删除文章
        }
        //发送请求
        if($.trim(url) != '')
        {
            $.ajax({
                url : url,
                type : 'post',
                data : opdatas,
                dataType : 'json',
                success : function(json)
                {
                    showoplayer(json);
                    querydatas();
                }
            });
        }
    });
    //点击文章标题(进入文章详细)
    $(document).on('click','.detail_article_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/zxgl/article/detail?id=' + datas['id']);
    });
    //监听模态窗关闭
    $(document).on("hidden.bs.modal","#operatorModal",function()
    {
        if($(this).attr('id') == 'operatorModal2')
        {
            querydatas(1);
        }
        else
        {
            querydatas();
        }
    });
    querydatas(1);
});
//获取列表数据
var querydatas = function (pnum)
{
    var data = $('.operate').getConditionValue();
    data.url = '${pageContext.request.contextPath}/zxgl/article/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 6;
    pageAjax(data, function ()
    {
        var html = "";
        if(data.datas.list && data.datas.list.length > 0)
        {
            $.each(data.datas.list, function (i, n)
            {
                tbdatas[n.id] = n;
                html += '<tr dataid="' + n.id + '">';
                html += '<td>';
                html += '<div class="btn-group clearfix">';
                html += '<a class="label label-info mright10 edit_article_cls">编辑</a>';
                //设置置顶
                if(n.iszd != 1)
                {
                    html += '<a opauthority="btn_zxgl_article_edit" class="label label-info mright10 zd_article_cls">置顶</a>';
                }
                else
                {
                    html += '<a opauthority="btn_zxgl_article_edit" class="label label-info mright10 zd_article_cls">取消置顶</a>';
                }
                //设置热门
                if(n.ishot != 1)
                {
                    html += '<a opauthority="btn_zxgl_article_edit" class="label label-info mright10 hot_article_cls">热门</a>';
                }
                else
                {
                    html += '<a opauthority="btn_zxgl_article_edit" class="label label-info mright10 hot_article_cls">取消热门</a>';
                }
                html += '<a opauthority="btn_zxgl_article_delete" class="label label-danger mright10 delete_article_cls">删除</a>';
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="id">' + n.id + '</td>';
                html += '<td scolumn="articleType">' + typeJsons[n.articleType] + '</td>';
                html += '<td scolumn="lotteryName">' + n.lotteryName + '</td>';
                html += '<td scolumn="mcode">'+ n.mcode + '</td>';
                html += '<td scolumn="title" style="max-width: 200px;"><a dtauthority="menu_zxgl_article" href="javascript:;" class="detail_article_cls">' + n.title + '</a></td>';
                html += '<td scolumn="ishot">' + (n.ishot == 1? '<span class="label label-success">是</span>' : '<span class="label label-primary">否</span>') + '</td>';
                html += '<td scolumn="iszd">' + (n.iszd == 1? '<span class="label label-success">置顶</span>' : '<span class="label label-primary">否</span>') + '</td>';
                /*html += '<td scolumn="contents" style="max-width: 250px;"><div class="col-inner">' + n.contents + '</div></td>';*/
                html += '<td scolumn="tags" style="max-width: 120px;"><div class="col-inner">' + n.tags + '</div></td>';
                html += '<td scolumn="author">' + n.author + '</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
<script>
window.umeditorHomeUrl = '${pageContext.request.contextPath}/js/umeditor/';//初始化umeditor服务器根路径
window.umeditorImageUploadUrl = '${pageContext.request.contextPath}/zxgl/article/upload';//初始化umeditor图片上传路径
window.umeditorImagePath = '${params.staticHost}';//初始化umeditor图片预览路径
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/umeditor/umeditor.config.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/umeditor/umeditor.js"></script>
</html>