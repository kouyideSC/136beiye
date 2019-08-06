<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_weihu");
    request.setAttribute("sidebar_mcode","menu_weihu_market");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>版本控制-列表/市场版本维护</title>
    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta name="author" content="PLUS">
    <meta charset="UTF-8">
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
    <%@include file="../../base/inc.jsp" %>
</head>
<body class="modal-open1">
<div class="main-content">
    <div class="whitebox">
        <div class="clearfix status-options operate" callback="querydatas();">
            <div class="clearfix">
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 100px">
                    <select class="form-control" name="marketId" id="marketSelect" title="市场名称" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 100px">
                    <select class="form-control selectpicker" name="clientType" id="client" title="客户端" data-live-search="true" data-size="8" data-selected-text-format="count > 3">
                        <option value="1">安卓</option>
                        <option value="0">IOS</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 100px">
                    <select class="form-control selectpicker" name="versionType" title="版本类型" data-live-search="true" data-size="8" data-selected-text-format="count > 3">
                        <option value="0">正式版</option>
                        <option value="1">资讯版</option>
                        <option value="2">企业版</option>
                    </select>
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_weihu_market_fbxbb" type="button" class="btn btn-success" id="addVersion" style="margin-right:20px;">新版本发布</button>
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
                        <th scolumn="marketName">市场名称</th>
                        <th scolumn="clientType">客户端</th>
                        <th scolumn="versionType">版本类型</th>
                        <th scolumn="appVersion">小版本号</th>
                        <th scolumn="buildVersion">大版本号</th>
                        <th scolumn="status">是否提示更新</th>
                        <th scolumn="isForceUpdate">强更</th>
                        <th scolumn="downUrl">下载地址</th>
                        <th scolumn="updateInfo">更新内容</th>
                        <th scolumn="createTime">发布时间</th>
                        <th scolumn="updateTime">更新时间</th>
                    </tr>
                    </thead>
                    <tbody id="dataTbody"></tbody>
                </table>
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
                            <li scolumn="marketName">市场名称</li>
                            <li scolumn="clientType">客户端</li>
                            <li scolumn="versionType">版本类型</li>
                            <li scolumn="appVersion">小版本号</li>
                            <li scolumn="buildVersion">大版本号</li>
                            <li scolumn="status">是否提示更新</li>
                            <li scolumn="isForceUpdate">强更</li>
                            <li scolumn="downUrl">下载地址</li>
                            <li scolumn="updateInfo">更新内容</li>
                            <li scolumn="createTime">发布时间</li>
                            <th scolumn="updateTime">更新时间</th>
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
<div class="card-wrap" id="detailCard" callback="querydatas"></div>
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
$(function ()
{
    //市场名称下拉
    $('#marketSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/market/getMarketList',
        id: 'id',
        name: 'marketName'
    });

    //点击删除
    $(document).on('click','.delete_version',function()
    {
        var $trNode = $(this).parents('tr').first();
        var $modalNode = $('#confirmModal');
        $modalNode.find('div.modal-body p').html('确定删除该条版本数据吗？');
        $modalNode.attr('optype',1).attr('editDatas',$trNode.attr('editid')).modal('show');
    });

    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $modalNode = $(this).parents('div.modal').first();
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/market/delete',
            type : 'post',
            dataType : 'json',
            data : {id:$modalNode.attr('editDatas')},
            success : function (json)
            {
                showoplayer(json);
                querydatas();
            }
        });
    });

    //新版本发布
    $('#addVersion').on('click',function()
    {
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/market/initAdd');
        $('#operatorModal').modal('show');
    });

    //点击编辑
    $(document).on('click','.edit_version',function()
    {
        var $trnode = $(this).parents('tr').first();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/market/detail?id=' + $trnode.attr('editid'));
        $('#operatorModal').modal('show');
    });

    //监听模态窗关闭
    $(document).on("hidden.bs.modal","#operatorModal",function()
    {
        querydatas(1);
    });
    querydatas(1);
});
//获取列表数据
var querydatas = function ()
{
    var data = $('.operate').getConditionValue();
    data.url = '${pageContext.request.contextPath}/weihu/market/list';
    data.select = '#dataTables';
    data.fixcmber = 3;
    pageAjax(data, function ()
    {
        $(data.select).parents('.fixtable_tableLayout:first').find('.multi_checkbox_allcls').prop("checked",false);
        var html = "";
        if(data.datas.list && data.datas.list.length > 0)
        {
            $.each(data.datas.list, function (i, n)
            {
                html += '<tr editid="' + n.id + '">';
                html += '<td>';
                html += '<div class="btn-group clearfix">';
                html += '<a opauthority="btn_weihu_market_edit" href="javascript:;" class="label label-info mright10 edit_version">编辑</a>';
                html += '<a opauthority="btn_weihu_market_delete" href="javascript:;" class="label label-danger mright10 delete_version">删除</a>';
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="marketName">' + n.appName + '</td>';
                html += '<td scolumn="clientType">' + (n.clientType==0?'IOS':'安卓') + '</td>';
                html += '<td scolumn="versionType">' + (n.versionType==0?'正式版':n.versionType==1?'资讯版':'企业版') + '</td>';
                html += '<td scolumn="appVersion">' + n.appVersion + '</td>';
                html += '<td scolumn="buildVersion">' + n.buildVersion + '</td>';
                html += '<td scolumn="status">' + (n.status==1?'提示':'不提示') + '</td>';
                html += '<td scolumn="isForceUpdate">' + (n.isForceUpdate==1?'是':'否') + '</td>';
                html += '<td scolumn="downUrl">' + n.downUrl + '</td>';
                html += '<td scolumn="updateInfo">' + n.updateInfo + '</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '<td scolumn="updateTime">' + n.updateTime + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>