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
    <title>销售自己月提成-列表</title>
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
        .ui-datepicker-calendar {
            display: none;
        }
    </style>
    <%@include file="../../base/inc.jsp" %>
</head>
<body class="modal-open1">
<div class="main-content">
    <div class="whitebox">
        <div class="clearfix status-options operate" callback="querydatas(1);">
            <div class="clearfix">
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 200px">
                    <input type="text" class="form-control" placeholder="输入年月格式如:2018-04" name="month">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
            </div>
        </div>
        <div class="core clearfix">
            <div class="core-table pull-left" style="position: relative; width:100%">
                <a href="javascript:;" class="edit-item" data-toggle="modal" data-target="#edit-item" style=" z-index: 500;"><i class="plus-icon p-setting"></i></a>
                <table id="dataTables" style="min-width:1200px;" border="0" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th scolumn="month">月份</th>
                        <th scolumn="nickName">用户昵称</th>
                        <th scolumn="totalMoney">总销量</th>
                        <th scolumn="totalMoneySellTc">总销量提成</th>
                        <th scolumn="saleZjMoney">自购销量</th>
                        <th scolumn="saleZjMoneyTc">自购销量提成</th>
                        <th scolumn="userMoney">下级用户销量</th>
                        <th scolumn="userMoneyTc">下级用户销量提成</th>
                        <th scolumn="proxyMoney">下级代理销量</th>
                        <th scolumn="proxyMoneyTc">下级代理销量提成</th>
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
                            <li scolumn="month">月份</li>
                            <li scolumn="nickName">用户昵称</li>
                            <li scolumn="totalMoney">总销量</li>
                            <li scolumn="totalMoneySellTc">总销量提成</li>
                            <li scolumn="saleZjMoney">自购销量</li>
                            <li scolumn="saleZjMoneyTc">自购销量提成</li>
                            <li scolumn="userMoney">下级用户销量</li>
                            <li scolumn="userMoneyTc">下级用户销量提成</li>
                            <li scolumn="proxyMoney">下级代理销量</li>
                            <li scolumn="proxyMoneyTc">下级代理销量提成</li>
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
var uid;
$(function ()
{
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
    data.url = '${pageContext.request.contextPath}/sale/monthmy/list';
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
                html += '<tr>';
                html += '<td scolumn="month">' + n.month + '</td>';
                html += '<td scolumn="nickName">' + n.nickName + '</td>';
                html += '<td scolumn="totalMoney">' + n.totalMoney + '</td>';
                html += '<td scolumn="totalMoneySellTc">' + n.totalMoneySellTc + '</td>';
                html += '<td scolumn="saleZjMoney">' + n.saleZjMoney + '</td>';
                html += '<td scolumn="saleZjMoneyTc">' + n.saleZjMoneyTc + '</td>';
                html += '<td scolumn="userMoney">' + n.userMoney + '</td>';
                html += '<td scolumn="userMoneySellTc">' + n.userMoneySellTc + '</td>';
                html += '<td scolumn="proxyMoney">' + n.proxyMoney + '</td>';
                html += '<td scolumn="proxyMoneySellTc">' + n.proxyMoneySellTc + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>