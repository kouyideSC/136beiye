<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_activity");
    request.setAttribute("sidebar_mcode","menu_activity_activity");
%>
<html lang="en" class="app">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
<title>活动管理-活动维护</title>
<meta name="description" content="">
<meta name="keywords" content="">
<meta name="author" content="">
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
                <button class="btn btn-info do-condition">重新加载</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_activity_activity_add" type="button" class="btn btn-success" id="addActivityBtn" style="margin-right:20px;">新增活动</button>
                </div>
            </div>
        </div>
        <div class="core clearfix">
            <div class="core-table pull-left" style="position: relative; width:100%">
                <a href="javascript:;" class="edit-item" data-toggle="modal" data-target="#edit-item" style=" z-index: 500;"><i class="plus-icon p-setting"></i></a>
                <table id="dataTables" style="min-width:1200px;" border="0" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th>操作</th>
                        <th scolumn="activityName">活动名称</th>
                        <th scolumn="title" style="max-width: 250px;">标题</th>
                        <th scolumn="activityType">活动类型</th>
                        <th scolumn="build">链接跳转方式</th>
                        <th scolumn="linkUrl">链接地址</th>
                        <th scolumn="pictureLink">显示图片</th>
                        <th scolumn="clientType">显示客户端</th>
                        <th scolumn="isShow">是否显示</th>
                        <th scolum="beginTime">开始时间</th>
                        <th scolumn="expireTime">结束时间</th>
                        <th scolumn="createTime">创建时间</th>
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
                            <li scolumn="activityName">活动名称</li>
                            <li scolumn="title" style="max-width: 250px;">标题</li>
                            <li scolumn="activityType">活动类型</li>
                            <li scolumn="build">链接跳转方式</li>
                            <li scolumn="linkUrl">链接地址</li>
                            <li scolumn="pictureLink">显示图片</li>
                            <li scolumn="clientType">显示客户端</li>
                            <li scolumn="isShow">是否显示</li>
                            <li scolum="beginTime">开始时间</li>
                            <li scolumn="expireTime">结束时间</li>
                            <li scolumn="createTime">创建时间</li>
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
                <button type="button" class="btn btn-save alertwarn btn_modal_cancel" data-dismiss="modal" id="confirmModalSureBtn">确定
                </button>
            </div>
        </div>
    </div>
</div>
</body>
<script>
var tbdatas = new Object();//表格数据对象
//活动类型名称映射(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
var activityTypeJsons = {"0":"彩票首页焦点图","1":"资讯版首页焦点图","2":"特定活动","3":"公告","4":"资讯/优惠信息","5":"app欢迎页图片"};
//活动详情显示模式映射(0-活动模式 1-公告模式 2-资讯模式 1010-双色球购买页 其它彩种号表示跳转到响应的彩种购买页)
var buildJsons = {"0":"原生详情页","1":"H5页面"};
//显示客户端映射(1-app 2-h5 3-web)
clientTypeJsons = {"1":"app","2":"h5","3":"web"};
$(function ()
{
    //点击新增活动
    $(document).on('click','#addActivityBtn',function()
    {
        $('#operatorModal').attr('optype',1);
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/activity/initAdd');
        $('#operatorModal').modal('show');
    });
    //点击编辑活动
    $(document).on('click','.edit_activity_cls',function()
    {
        $('#operatorModal').removeAttr('optype');
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/activity/initEdit?id=' + datas['id']);
        $('#operatorModal').modal('show');
    });
    //点击删除活动
    $(document).on('click','.delete_activity_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('确认删除活动 ' + datas['activityName'] + ' 吗？');
        var arrays = new Array();
        arrays.push({id:datas['id']});
        $confirmModal.attr('optype',1).attr('editdatas',JSON.stringify(arrays)).modal('show');
    });
    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        var datas = JSON.parse($confirmModal.attr('editdatas'));
        var optype = $confirmModal.attr('optype');
        var url = '';
        if(optype == 1)
        {
            url = '${pageContext.request.contextPath}/weihu/activity/delete';//删除活动
            datas = datas[0];
        }
        $.ajax({
            url : url,
            type : 'post',
            dataType : 'json',
            data : datas,
            success : function (json)
            {
                showoplayer(json);
                if(json.dcode == 1000)
                {
                    if(optype == 1)
                    {
                        querydatas(1);
                    }
                    else
                    {
                        querydatas();
                    }
                }
            }
        });
    });
    //点击活动名称(进入活动详细)
    $(document).on('click','.detail_activity_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/weihu/activity/initDetail?id=' + datas['id']);
    });
    //监听模态窗关闭
    $(document).on("hidden.bs.modal","#operatorModal,#confirmModal",function()
    {
        if($(this).attr('optype') == 1)
        {
            querydatas(1);
        }
        else
        {
            querydatas();
        }
    });
    $.ajax({
        url : '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
        type : 'post',
        dataType : 'json',
        success : function (json)
        {
            if(json.dcode == 1000)
            {
                $.each(json.datas.list,function(i,m)
                {
                    buildJsons[m.id] = m.name + '购彩页';
                });
            }
        }
    });
    querydatas(1);
});
//获取列表数据
var querydatas = function (pnum)
{
    if (typeof pnum == 'undefined')
    {
        pnum = $("#pagelist li[page][class*='active']").attr("page");
    }
    var data = $('.operate').getConditionValue();
    data.url = '${pageContext.request.contextPath}/weihu/activity/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 3;
    pageAjax(data, function ()
    {
        var html = "";
        if(data.datas.list && data.datas.list.length > 0)
        {
            var lotteryId = $('#lotteryIdSelect').val();
            $.each(data.datas.list, function (i, n)
            {
                tbdatas[n.id] = n;
                html += '<tr dataid="' + n.id + '">';
                html += '<td>';
                html += '<a opauthority="btn_activity_activity_edit" href="javascript:;" class="label label-info mright5 edit_activity_cls">编辑</a>';
                html += '<a opauthority="btn_activity_activity_delete" href="javascript:;" class="label label-danger mright5 delete_activity_cls">删除</a>';
                html += '</td>';
                html += '<td scolumn="activityName">' + n.activityName + '</td>';
                html += '<td scolumn="title" style="max-width: 250px;"><div class="col-inner">' + n.title + '</div></td>';
                html += '<td scolumn="activityType">' + activityTypeJsons[n.activityType] + '</td>';
                html += '<td scolumn="build">' + buildJsons[n.build] + '</td>';
                html += '<td scolumn="linkUrl">' + n.linkUrl + '</td>';
                html += '<td scolumn="pictureUrl"><img src="' + n.pictureLink + '" style="width: 200px;height: 38px;"></td>';
                html += '<td scolumn="clientType">' + clientTypeJsons[n.clientType] + '</td>';
                html += '<td scolumn="isShow">' + (n.isShow == 1? "显示" : "隐藏") + '</td>';
                html += '<td scolumn="beginTime">' + n.beginTime + '</td>';
                html += '<td scolumn="expireTime">' + n.expireTime + '</td>';
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
window.umeditorImageUploadUrl = '${pageContext.request.contextPath}/weihu/activity/upload';//初始化umeditor图片上传路径
window.umeditorImagePath = '${params.staticHost}';//初始化umeditor图片预览路径
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/umeditor/umeditor.config.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/umeditor/umeditor.js"></script>
</html>