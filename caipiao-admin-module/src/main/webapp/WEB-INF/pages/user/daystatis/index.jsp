<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_report");
    request.setAttribute("sidebar_mcode","menu_report_daystatis");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>用户-日报表/数据统计</title>
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
        <div class="clearfix status-options operate" callback="querydatas(1);">
            <div class="clearfix">
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 170px">
                    <input type="text" class="form-control datepicker" placeholder="日期-开始" name="beginTime">
                </div>
                <span class="pull-left" style="line-height: 2; margin-right: 5px;margin-left: -5px;">-</span>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 170px">
                    <input type="text" class="form-control datepicker" placeholder="日期-结束" name="endTime">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button type="button" class="btn btn-success" id="export" style="margin-right:20px;">导出</button>
                </div>
            </div>
        </div>
        <div class="core clearfix">
            <div class="core-table pull-left" style="position: relative; width:100%">
                <a href="javascript:;" class="edit-item" data-toggle="modal" data-target="#edit-item" style=" z-index: 500;"><i class="plus-icon p-setting"></i></a>
                <table id="dataTables" style="min-width:1200px;" border="0" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th scolumn="date" rowspan="2">日期</th>
                        <th scolumn="loginNumber" colspan="2">登录人数</th>
                        <th scolumn="registerNumber" colspan="2">注册人数</th>
                        <th scolumn="orderNumber" colspan="2">购彩方案数</th>
                        <th scolumn="orderMoney" colspan="2">购彩总金额</th>
                        <th scolumn="rechargeNumber" colspan="2">充值笔数</th>
                        <th scolum="rechargeMoney" colspan="2">充值总金额</th>
                        <th scolumn="withdrawNumber" rowspan="2">提现笔数</th>
                        <th scolumn="withdrawMoney" rowspan="2">提现总金额</th>
                        <th scolumn="winNumber" rowspan="2">中奖笔数</th>
                        <th scolumn="winMoney" rowspan="2">中奖总金额</th>
                        <th scolumn="createTime" rowspan="2">统计时间</th>
                    </tr>
                    <tr>
                        <th scolumn="loginNumber_A">安卓</th>
                        <th scolumn="loginNumber_I">苹果</th>
                        <th scolumn="registerNumber_A">安卓</th>
                        <th scolumn="registerNumber_I">苹果</th>
                        <th scolumn="orderNumber_A">安卓</th>
                        <th scolumn="orderNumber_I">苹果</th>
                        <th scolumn="orderMoney_A">安卓</th>
                        <th scolumn="orderMoney_I">苹果</th>
                        <th scolumn="rechargeNumber_A">安卓</th>
                        <th scolumn="rechargeNumber_I">苹果</th>
                        <th scolum="rechargeMoney_A">安卓</th>
                        <th scolum="rechargeMoney_I">苹果</th>
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
                            <li scolumn="date" rowspan="2">日期</li>
                            <li scolumn="loginNumber" colspan="2">登录人数</li>
                            <li scolumn="registerNumber" colspan="2">注册人数</li>
                            <li scolumn="orderNumber" colspan="2">购彩方案数</li>
                            <li scolumn="orderMoney" colspan="2">购彩总金额</li>
                            <li scolumn="rechargeNumber" colspan="2">充值笔数</li>
                            <li scolum="rechargeMoney" colspan="2">充值总金额</li>
                            <li scolumn="withdrawNumber" rowspan="2">提现笔数</li>
                            <li scolumn="withdrawMoney" rowspan="2">提现总金额</li>
                            <li scolumn="winNumber" rowspan="2">中奖笔数</li>
                            <li scolumn="winMoney" rowspan="2">中奖总金额</li>
                            <li scolumn="createTime" rowspan="2">统计时间</li>
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
</body>
<script>
$(function ()
{
    //点击导出
    $(document).on('click','#export',function()
    {
        var data = $('.operate').getConditionValue();
        var params = '';
        for(var key in data)
        {
            params += "&" + key + "=" + data[key];
        }
        window.location.href = '${pageContext.request.contextPath}/user/daystatis/export?' + params.substring(1);
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
    data.url = '${pageContext.request.contextPath}/user/daystatis/list';
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
                html += '<tr editid="' + n.date + '">';
                html += '<td scolumn="date">' + n.date + '</td>';
                html += '<td scolumn="loginNumber_A">' + n.loginNumber_A + '</td>';
                html += '<td scolumn="loginNumber_I">' + n.loginNumber_I + '</td>';
                html += '<td scolumn="registerNumber_A">' + n.registerNumber_A + '</td>';
                html += '<td scolumn="registerNumber_I">' + n.registerNumber_I + '</td>';
                html += '<td scolumn="orderNumber_A">' + n.orderNumber_A + '</td>';
                html += '<td scolumn="orderNumber_I">' + n.orderNumber_I + '</td>';
                html += '<td scolumn="orderMoney_A">' + n.orderMoney_A + '元</td>';
                html += '<td scolumn="orderMoney_I">' + n.orderMoney_I + '元</td>';
                html += '<td scolumn="rechargeNumber_A">' + n.rechargeNumber_A + '</td>';
                html += '<td scolumn="rechargeNumber_I">' + n.rechargeNumber_I + '</td>';
                html += '<td scolumn="rechargeMoney_A">' + n.rechargeMoney_A + '元</td>';
                html += '<td scolumn="rechargeMoney_I">' + n.rechargeMoney_I + '元</td>';
                html += '<td scolumn="withdrawNumber">' + n.withdrawNumber + '</td>';
                html += '<td scolumn="withdrawMoney">' + n.withdrawMoney + '元</td>';
                html += '<td scolumn="winNumber">' + n.winNumber + '</td>';
                html += '<td scolumn="winMoney">' + n.winMoney + '元</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>