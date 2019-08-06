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
    <title>用户返利-列表/返利查询</title>
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
    <%@include file="../../base/inc.jsp" %>
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
                    <input type="text" class="form-control" placeholder="用户编号" name="userId">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 125px">
                    <input type="text" class="form-control" placeholder="方案编号" name="schemeOrderId">
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
                        <th scolumn="type">返点类型</th>
                        <th scolumn="userId">账变用户</th>
                        <th scolumn="lotteryId">彩种</th>
                        <th scolumn="schemeOrderId">订单号</th>
                        <th scolumn="schemeMoney">订单金额</th>
                        <th scolumn="schemeUserId">下单用户</th>
                        <th scolum="rate">返点比例</th>
                        <th scolumn="currentRebateMoney">方案返点金额</th>
                        <th scolumn="lastBalanceRebate">操作前返点账户余额</th>
                        <th scolumn="balanceRebate">操作后返点账户余额</th>
                        <th scolumn="createTime">返点时间</th>
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
                            <li scolumn="type">返点类型</li>
                            <li scolumn="userId">账变用户</li>
                            <li scolumn="lotteryId">彩种</li>
                            <li scolumn="schemeOrderId">订单号</li>
                            <li scolumn="schemeMoney">订单金额</li>
                            <li scolumn="schemeUserId">下单用户</li>
                            <li scolum="rate">返点比例</li>
                            <li scolumn="currentRebateMoney">方案返点金额</li>
                            <li scolumn="lastBalanceRebate">操作前返点账户余额</li>
                            <li scolumn="balanceRebate">操作后返点账户余额</li>
                            <li scolumn="createTime">返点时间</li>
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
    //点击方案编号(进入方案详细)
    $(document).on('click','.detail_scheme_cls',function()
    {
        var id = $(this).parents('tr').first().attr('editid');
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/user/scheme/detail?id=' + id);
    });
    //点击导出
    $(document).on('click','#export',function()
    {
        var data = $('.operate').getConditionValue();
        var params = '';
        for(var key in data)
        {
            params += "&" + key + "=" + data[key];
        }
        window.location.href = '${pageContext.request.contextPath}/user/fanli/export?' + params.substring(1);
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
    data.url = '${pageContext.request.contextPath}/user/fanli/list';
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
                html += '<td scolumn="type">' + (n.type==0?'收获':'提取') + '</td>';
                html += '<td scolumn="nickName">' + n.nickName + '</td>';
                html += '<td scolumn="shortName">' + n.shortName + '</td>';
                html += '<td scolumn="schemeOrderId">' + n.schemeOrderId + '</td>';
                html += '<td scolumn="schemeMoney">' + (n.schemeMoney).toFixed(2) + '元</td>';
                html += '<td scolumn="schemeUserId">' + n.schemeUserName + '</td>';
                html += '<td scolumn="rate">' + n.rate + '</td>';
                html += '<td scolumn="currentRebateMoney">' + (n.currentRebateMoney).toFixed(2) + '元</td>';
                html += '<td scolumn="lastBalanceRebate">' + (n.lastBalanceRebate).toFixed(2) + '元</td>';
                html += '<td scolumn="balanceRebate">' + (n.balanceRebate).toFixed(2) + '元</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>