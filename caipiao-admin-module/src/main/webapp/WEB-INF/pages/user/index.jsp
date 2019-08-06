<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_user");
    request.setAttribute("sidebar_mcode","menu_user_user");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>用户-列表/用户查询</title>
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
            background-color: #f9f9fb;
        }
    </style>
    <%@include file="../base/inc.jsp" %>
</head>
<body class="modal-open1">
<div class="main-content">
    <div class="whitebox">
        <div class="clearfix status-options operate" callback="querydatas(1);">
            <div class="clearfix">
                <div class="dropdown pull-left" style="margin-right: 10px; width: 130px">
                    <input type="text" class="form-control" placeholder="输入手机号" name="mobile">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 120px">
                    <input type="text" class="form-control" placeholder="用户编号" name="id">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 125px">
                    <input type="text" class="form-control" placeholder="用户昵称" name="nickName">
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 170px">
                    <input type="text" class="form-control datetimepicker" placeholder="注册时间-开始" name="beginTime">
                </div>
                <span class="pull-left" style="line-height: 2; margin-right: 5px;margin-left: -5px;">-</span>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 170px">
                    <input type="text" class="form-control datetimepicker" placeholder="注册时间-结束" name="endTime">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link show-more">高级筛选</button>
                <button class="btn btn-link show-off" style="display: none;">收起</button>
                <button class="btn btn-link clear-condition">清除</button>
            </div>
            <div class="clearfix advanced">
                <div class="dropdown pull-left" style="margin-right: 10px;width: 130px">
                    <select class="form-control selectpicker" title="用户状态" name="status" id="status"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 120px">
                    <select class="form-control selectpicker" title="代理头衔" name="isSale" id="isSale"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 125px">
                    <select class="form-control selectpicker" title="客户端" name="registerFrom" id="registerFrom"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 15px;width: 170px">
                    <select class="form-control selectpicker" title="用户类型" name="userType" id="userType"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 170px">
                    <select class="form-control selectpicker" title="用户等级" name="vipLevel" id="vipLevel"></select>
                </div>
                <div class="dropdown pull-left">
                    <input type="checkbox" name="isWhite">&nbsp;是否开通白名单&nbsp;&nbsp;
                </div>
            </div>

        </div>
        <div class="core clearfix">
            <div class="core-table pull-left" style="position: relative; width:100%">
                <a href="javascript:;" class="edit-item" data-toggle="modal" data-target="#edit-item" style=" z-index: 500;"><i class="plus-icon p-setting"></i></a>
                <table id="dataTables" style="min-width:1200px;" border="0" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th scolumn="nickName">用户昵称</th>
                        <th scolumn="balance">账户余额</th>
                        <th scolumn="consume">总消费</th>
                        <th scolumn="mobile">手机号</th>
                        <th scolumn="status">状态</th>
                        <th scolumn="loginDegree">登录数</th>
                        <th scolumn="userType">用户类型</th>
                        <th scolum="registerFrom">客户端</th>
                        <th scolumn="higherUid">上级归属</th>
                        <th scolumn="isSale">头衔</th>
                        <th scolumn="marketFrom">市场来源</th>
                        <th scolumn="registerTime">注册时间</th>
                        <th scolumn="lastLoginTime">上次登录时间</th>
                        <th scolumn="isAdmin">是否为管理员</th>
                    </tr>
                    </thead>
                    <tbody id="dataTbody"></tbody>
                </table>
                <div class="pull-right pagelist_cls" funcname="querydatas">
                    <jsp:include page="../base/pagination.jsp"></jsp:include>
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
                            <li scolumn="nickName">用户昵称</li>
                            <li scolumn="balance">账户余额</li>
                            <li scolumn="consume">总消费</li>
                            <li scolumn="mobile">手机号</li>
                            <li scolumn="status">状态</li>
                            <li scolumn="loginDegree">登录数</li>
                            <li scolumn="userType">用户类型</li>
                            <li scolum="registerFrom">客户端</li>
                            <li scolumn="higherUid">上级归属</li>
                            <li scolumn="isSale">头衔</li>
                            <li scolumn="marketFrom">市场来源</li>
                            <li scolumn="registerTime">注册时间</li>
                            <li scolumn="lastLoginTime">上次登录时间</li>
                            <li scolumn="isAdmin">是否为管理员</li>
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
var userStatusJson = new Object();
var vipLevelJson = new Object();
var regFromJson = new Object();
var userTypeJson = new Object();
var saleJson = new Object();
$(function ()
{
    //用户状态下拉
    $('#status').fillSelectMenu({
        url: '${pageContext.request.contextPath}/user/getUserModuleDown?module=1',
        id: 'id',
        name: 'value',
        callback : function(data)
        {
            $.each(data, function(i,m)
            {
                userStatusJson[$(m).attr('id')] = $(m).attr('value');
            });
        }
    });
    //用户等级下拉
    $('#vipLevel').fillSelectMenu({
        url: '${pageContext.request.contextPath}/user/getUserModuleDown?module=2',
        id: 'id',
        name: 'value',
        callback : function(data)
        {
            $.each(data, function(i,m)
            {
                vipLevelJson[$(m).attr('id')] = $(m).attr('value');
            });
        }
    });
    //客户端下拉
    $('#registerFrom').fillSelectMenu({
        url: '${pageContext.request.contextPath}/user/getUserModuleDown?module=3',
        id: 'id',
        name: 'value',
        callback : function(data)
        {
            $.each(data, function(i,m)
            {
                regFromJson[$(m).attr('id')] = $(m).attr('value');
            });
        }
    });
    //用户类型下拉
    $('#userType').fillSelectMenu({
        url: '${pageContext.request.contextPath}/user/getUserModuleDown?module=4',
        id: 'id',
        name: 'value',
        callback : function(data)
        {
            $.each(data, function(i,m)
            {
                userTypeJson[$(m).attr('id')] = $(m).attr('value');
            });
        }
    });
    //代理头衔下拉
    $('#isSale').fillSelectMenu({
        url: '${pageContext.request.contextPath}/user/getUserModuleDown?module=5',
        id: 'id',
        name: 'value',
        callback : function(data)
        {
            $.each(data, function(i,m)
            {
                saleJson[$(m).attr('id')] = $(m).attr('value');
            });
        }
    });
    //点击用户昵称(进入用户详情)
    $(document).on('click','.set_user_detail',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/user/detail?id=' + $trNode.attr('editid'));
    });

    //监听模态窗关闭
    $(document).on("hidden.bs.modal","#operatorModal",function()
    {
        querydatas(1);
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
    data.url = '${pageContext.request.contextPath}/user/list';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 1;
    pageAjax(data, function ()
    {
        $(data.select).parents('.fixtable_tableLayout:first').find('.multi_checkbox_allcls').prop("checked",false);
        var html = "";
        if(data.datas.list && data.datas.list.length > 0)
        {
            $.each(data.datas.list, function (i, n)
            {
                html += '<tr editid="' + n.id + '">';
                html += '<td scolumn="nickName"><a dtauthority="menu_user_user" href="javascript:;" class="set_user_detail">' + n.nickName + '</a></td>';
                //html += '<td scolumn="vipLevel"><span class="label ' + userVipColorJson[n.vipLevel] + '">' + (vipLevelJson[n.vipLevel]) + '</span></td>';
                html += '<td scolumn="balance">' + (n.balance).toFixed(2) + '</td>';
                html += '<td scolumn="consume">' + (n.consume).toFixed(2) + '</td>';
                html += '<td scolumn="mobile">' + n.mobile + '</td>';
                html += '<td scolumn="status">' + (userStatusJson[n.status] || '未知') + '</td>';
                html += '<td scolumn="loginDegree">' + n.loginDegree + '</td>';
                html += '<td scolumn="userType">' + (userTypeJson[n.userType] || '未知') + '</td>';
                html += '<td scolumn="registerFrom">' + (regFromJson[n.registerFrom] || '未知') + '</td>';
                html += '<td scolumn="higherUid">' + (n.higherUid==''?'无':n.higherUid) + '</td>';
                html += '<td scolumn="isSale"><a href="javascript:;" class="set_user_detail">' + (saleJson[n.isSale] || '未知') + '</a></td>';
                html += '<td scolumn="marketFrom">' + n.marketFrom + '</td>';
                html += '<td scolumn="registerTime">' + n.registerTime + '</td>';
                html += '<td scolumn="lastLoginTime">' + n.lastLoginTime + '</td>';
                html += '<td scolumn="isAdmin">' + (n.isAdmin == 0? '非管理员' : '管理员') + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>