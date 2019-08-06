<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_activity");
    request.setAttribute("sidebar_mcode","menu_activity_channel");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>活动管理-列表/渠道合作信息维护</title>
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
                <div class="dropdown pull-left" style="margin-right: 10px; width: 130px">
                    <input type="text" class="form-control" placeholder="渠道编号" name="channelCode">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 120px">
                    <input type="text" class="form-control" placeholder="渠道联系人" name="contactMobile">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_activity_channel_add" type="button" class="btn btn-success" id="addChannel" style="margin-right:20px;">添加新渠道</button>
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
                        <th scolumn="channelName">渠道名称</th>
                        <th scolumn="channelCode">渠道编号</th>
                        <th scolumn="contactMobile">渠道手机号</th>
                        <th scolumn="authKey">加密KEY</th>
                        <th scolumn="outAccountUserId">出款账户编号</th>
                        <th scolumn="balance">出款账户余额</th>
                        <th scolumn="overstepAccount">透支金额</th>
                        <th scolumn="status">状态</th>
                        <th scolumn="notifyStatus">出票通知</th>
                        <th scolumn="notifyUrl">通知地址</th>
                        <th scolumn="ipLimit">IP白名单</th>
                        <th scolumn="beginTime">生效时间</th>
                        <th scolumn="endTime">结束时间</th>
                        <th scolumn="createTime">开通时间</th>
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
                            <li scolumn="channelName">渠道名称</li>
                            <li scolumn="channelCode">渠道编号</li>
                            <li scolumn="contactMobile">渠道手机号</li>
                            <li scolumn="authKey">加密KEY</li>
                            <li scolumn="outAccountUserId">出款账户编号</li>
                            <li scolumn="balance">出款账户余额</li>
                            <li scolumn="overstepAccount">透支金额</li>
                            <li scolumn="status">状态</li>
                            <li scolumn="notifyStatus">出票通知</li>
                            <li scolumn="notifyUrl">通知地址</li>
                            <li scolumn="ipLimit">IP白名单</li>
                            <li scolumn="beginTime">生效时间</li>
                            <li scolumn="endTime">结束时间</li>
                            <li scolumn="createTime">开通时间</li>
                            <li scolumn="updateTime">更新时间</li>
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
    //点击删除
    $(document).on('click','.delete_channel',function()
    {
        var $trNode = $(this).parents('tr').first();
        var $modalNode = $('#confirmModal');
        $modalNode.find('div.modal-body p').html('确定删除该渠道吗？');
        $modalNode.attr('optype',1).attr('editDatas',$trNode.attr('editid')).modal('show');
    });

    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $modalNode = $(this).parents('div.modal').first();
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/channel/delete',
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

    //添加新渠道
    $('#addChannel').on('click',function()
    {
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/channel/initAdd');
        $('#operatorModal').modal('show');
    });

    //点击编辑
    $(document).on('click','.edit_channel',function()
    {
        var $trnode = $(this).parents('tr').first();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/channel/detail?id=' + $trnode.attr('editid'));
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
    data.url = '${pageContext.request.contextPath}/weihu/channel/list';
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
                html += '<a opauthority="btn_activity_channel_edit" href="javascript:;" class="label label-info mright10 edit_channel">编辑</a>';
                html += '<a opauthority="btn_activity_channel_delete" href="javascript:;" class="label label-danger mright10 delete_channel">删除</a>';
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="channelName">' + n.channelName + '</td>';
                html += '<td scolumn="channelCode">' + n.channelCode + '</td>';
                html += '<td scolumn="contactMobile">' + n.contactMobile + '</td>';
                html += '<td scolumn="authKey">' + n.authKey + '</td>';
                html += '<td scolumn="outAccountUserId">' + (n.outAccountUserId==null||n.outAccountUserId==""?'未设置':n.outAccountUserId) + '</td>';
                html += '<td scolumn="balance">' + (n.balance==null||n.balance==""?0:n.balance) + '</td>';
                html += '<td scolumn="overstepAccount">' + n.overstepAccount + '</td>';
                html += '<td scolumn="status">' + (n.status==0?'停用':'启用') + '</td>';
                html += '<td scolumn="notifyStatus">' + (n.notifyStatus==0?'不通知':'通知') + '</td>';
                html += '<td scolumn="notifyUrl">' + n.notifyUrl + '</td>';
                html += '<td scolumn="ipLimit">' + n.ipLimit + '</td>';
                html += '<td scolumn="beginTime">' + n.beginTime + '</td>';
                html += '<td scolumn="endTime">' + n.endTime + '</td>';
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