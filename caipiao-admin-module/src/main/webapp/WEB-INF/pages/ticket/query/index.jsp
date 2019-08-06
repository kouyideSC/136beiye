<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_chupiao");
    request.setAttribute("sidebar_mcode","menu_chupiao_query");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>出票查询-列表/出票查询</title>
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
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 110px">
                    <select class="form-control" name="voteId" id="voteSelect" title="出票商" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 110px">
                    <select class="form-control" name="lotteryId" id="lotterySelect" title="彩种" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 150px">
                    <select class="form-control" name="ticketStatus" id="schemeStatus" title="出票状态" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 150px">
                    <input type="text" class="form-control" placeholder="方案号" name="schemeId">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 125px">
                    <input type="text" class="form-control" placeholder="期次编号" name="period">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 150px">
                    <input type="text" class="form-control" placeholder="出票商票号" name="voteTicketId">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link show-more">高级筛选</button>
                <button class="btn btn-link show-off" style="display: none;">收起</button>
                <button class="btn btn-link clear-condition">清除</button>
            </div>
            <div class="clearfix advanced">
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 170px">
                    <input type="text" class="form-control datetimepicker" placeholder="拆票时间-开始" name="beginTime" id="beginTime">
                </div>
                <span class="pull-left" style="line-height: 2; margin-right: 5px;margin-left: -5px;">-</span>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 170px">
                    <input type="text" class="form-control datetimepicker" placeholder="拆票时间-结束" name="endTime" id="endTime">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 150px">
                    <input type="text" class="form-control" placeholder="方案票号" name="ticketId">
                </div>
                <div class="dropdown pull-left">
                    <input type="checkbox" name="isZhuiHao">&nbsp;追号方案&nbsp;&nbsp;
                </div>
                <div class="dropdown pull-left">
                    <input type="checkbox" name="isWin">&nbsp;中奖&nbsp;&nbsp;
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
                        <th scolumn="schemeId">方案号</th>
                        <th scolumn="playTypeId">彩种玩法</th>
                        <th scolumn="period">期次</th>
                        <th scolumn="voteName">出票商</th>
                        <th scolumn="multiple">倍数</th>
                        <th scolum="money">金额</th>
                        <th scolumn="ticketStatus">出票状态</th>
                        <th scolumn="isZhuiHao">追号</th>
                        <th scolumn="createTime">拆票时间</th>
                        <th scolumn="sendTicketTime">提票时间</th>
                        <th scolumn="outTicketTime">出票时间</th>
                        <th scolumn="ticketId">方案票号</th>
                        <th scolumn="voteTicketId">出票商票号</th>
                        <th scolumn="votePrizeTax">出票商奖金</th>
                        <th scolumn="ticketPrizeTax">网站奖金</th>
                        <th scolumn="awardTime">兑奖时间</th>
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
                            <li scolumn="schemeId">方案号</li>
                            <li scolumn="playTypeId">彩种玩法</li>
                            <li scolumn="period">期次</li>
                            <li scolumn="voteName">出票商</li>
                            <li scolumn="multiple">倍数</li>
                            <li scolum="money">金额</li>
                            <li scolumn="ticketStatus">出票状态</li>
                            <li scolumn="isZhuiHao">追号</li>
                            <li scolumn="createTime">拆票时间</li>
                            <li scolumn="sendTicketTime">提票时间</li>
                            <li scolumn="outTicketTime">出票时间</li>
                            <li scolumn="ticketId">方案票号</li>
                            <li scolumn="voteTicketId">出票商票号</li>
                            <li scolumn="votePrizeTax">出票商奖金</li>
                            <li scolumn="ticketPrizeTax">网站奖金</li>
                            <li scolumn="awardTime">兑奖时间</li>
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
var playNameJson = new Object();
var ticketStatusJson = new Object();
var LotteryJson = new Object();
$(function ()
{
    //彩种名称下拉
    $('#lotterySelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
        id: 'id',
        name: 'shortName',
        callback : function(json)
        {
            $.each(json, function(i,m)
            {
                LotteryJson[$(m).attr('id')] = $(m).attr('shortName');
            });
        }
    });

    //出票商名称下拉
    $('#voteSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/ticket/vote/list',
        id: 'voteId',
        name: 'voteName'
    });

    //出票状态下拉
    $('#schemeStatus').fillSelectMenu({
        url: '${pageContext.request.contextPath}/ticket/getSchemeStatus?module=1',
        id: 'id',
        name: 'value',
        callback : function(json)
        {
            $.each(json, function(i,m)
            {
                ticketStatusJson[$(m).attr('id')] = $(m).attr('value');
            });
        }
    });

    //点击用户昵称(进入用户详情)
    $(document).on('click','.set_ticket_detail',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/ticket/query/detail?id=' + $trNode.attr('editid'));
    });

    //监听模态窗关闭
    $(document).on("hidden.bs.modal","#operatorModal",function()
    {
        querydatas(1);
    });

    //玩法状态
    $.ajax({
        url: '${pageContext.request.contextPath}/ticket/getSchemeStatus?module=2',
        async: false,
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            $.each(data.datas.list, function(i,m)
            {
                playNameJson[$(m).attr('id')] = $(m).attr('value');
            });
        }
    });
    $('#beginTime').val('${params.beginTime}');
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
    data.url = '${pageContext.request.contextPath}/ticket/query/list';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 5;
    pageAjax(data, function ()
    {
        $(data.select).parents('.fixtable_tableLayout:first').find('.multi_checkbox_allcls').prop("checked",false);
        var html = "";
        if(data.datas.list && data.datas.list.length > 0)
        {
            $.each(data.datas.list, function (i, n)
            {
                html += '<tr editid="' + n.id + '">';
                html += '<td scolumn="detail"><a opauthority="menu_chupiao_query" href="javascript:;" class="label label-info mright10 set_ticket_detail">详情</a></td>';
                html += '<td scolumn="schemeId">' + n.schemeId + '</td>';
                html += '<td scolumn="playTypeId">' + (playNameJson[n.playTypeId] || LotteryJson[n.playTypeId]) + '</td>';
                html += '<td scolumn="period">' + n.period + '</td>';
                html += '<td scolumn="voteName">' + n.voteName + '</td>';
                html += '<td scolumn="multiple">' + n.multiple + '</td>';
                html += '<td scolumn="money">' + n.money + '</td>';
                html += '<td scolumn="ticketStatus">' + (ticketStatusJson[n.ticketStatus] || '未知') + '</td>';
                html += '<td scolumn="isZhuiHao">' + (n.isZhuiHao == 1 ? '是':'否') + '</a></td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '<td scolumn="sendTicketTime">' + n.sendTicketTime + '</td>';
                html += '<td scolumn="outTicketTime">' + n.outTicketTime + '</td>';
                html += '<td scolumn="voteTicketId">' + n.ticketId + '</td>';
                html += '<td scolumn="voteTicketId">' + n.voteTicketId + '</td>';
                html += '<td scolumn="votePrizeTax">' + n.votePrizeTax + '</td>';
                html += '<td scolumn="ticketPrizeTax">' + (n.ticketPrizeTax+n.ticketSubjoinPrizeTax) + '</td>';
                html += '<td scolumn="awardTime">' + n.awardTime + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>