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
    <title>实体店出票查询-列表/出票查询</title>
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
                    <select class="form-control" name="lotteryId" id="lotterySelect" title="彩种" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 130px">
                    <select class="form-control" name="ticketStatus" id="schemeStatus" title="出票状态" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 150px">
                    <input type="text" class="form-control" placeholder="方案号" name="schemeId" id="shopschemeId">
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 150px">
                    <input type="text" class="form-control datetimepicker" placeholder="截止时间-开始" name="tbeginTime" id="tbeginTime">
                </div>
                <span class="pull-left" style="line-height: 2; margin-right: 5px;margin-left: -5px;">-</span>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 150px">
                    <input type="text" class="form-control datetimepicker" placeholder="截止时间-结束" name="tendTime">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button opauthority="menu_shopchupiao_query" class="btn btn-info do-condition" id="szcxcpBtn">批量成功</button>
                <button opauthority="menu_shopchupiao_query" class="btn btn-info do-condition" id="szsbcpBtn">批量失败</button>
                <button class="btn btn-link show-more">高级筛选</button>
                <button class="btn btn-link show-off" style="display: none;">收起</button>
            </div>
            <div class="clearfix advanced">
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 170px">
                    <input type="text" class="form-control datetimepicker" placeholder="推送时间-开始" name="beginTime" id="beginTime">
                </div>
                <span class="pull-left" style="line-height: 2; margin-right: 5px;margin-left: -5px;">-</span>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 170px">
                    <input type="text" class="form-control datetimepicker" placeholder="推送时间-结束" name="endTime" id="endTime">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 150px">
                    <input type="text" class="form-control" placeholder="方案票号" name="ticketId">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 125px">
                    <input type="text" class="form-control" placeholder="期次编号" name="period">
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
                        <th style="min-width: 30px;max-width: 30px;"><input type="checkbox" class="multi_checkbox_allcls"></th>
                        <th>操作</th>
                        <th scolumn="schemeId">方案号</th>
                        <th scolumn="playTypeId">彩种玩法</th>
                        <th scolumn="period">期次</th>
                        <th scolumn="voteName">出票商</th>
                        <th scolumn="multiple">倍数</th>
                        <th scolum="money">金额</th>
                        <th scolumn="ticketStatus">出票状态</th>
                        <th scolumn="endTime">截止时间</th>
                        <th scolumn="createTime">推送时间</th>
                        <th scolumn="ticketId">方案票号</th>
                        <th scolumn="ticketPrizeTax">网站奖金</th>
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
                            <li scolumn="endTime">截止时间</li>
                            <li scolumn="createTime">推送时间</li>
                            <li scolumn="ticketId">方案票号</li>
                            <li scolumn="ticketPrizeTax">网站奖金</li>
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

    //出票状态下拉
    $('#schemeStatus').fillSelectMenu({
        url: '${pageContext.request.contextPath}/ticket/getSchemeStatus?module=1',
        id: 'id',
        name: 'value',
        //noDefault : true,
        //selectedIds : '0',
        callback : function(json)
        {
            $.each(json, function(i,m)
            {
                ticketStatusJson[$(m).attr('id')] = $(m).attr('value');
            });
        }
    });

    //点击详情(进入用户详情)
    $(document).on('click','.set_ticket_detail',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/ticket/shopquery/detail?id=' + $trNode.attr('editid'));
    });

    //点击打票成功
    $(document).on('click','.dpcg_scheme_cls',function()
    {
        var id = $(this).parents('tr').first().attr('editid');
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('确定已成功打票了吗？');
        var arrays = new Array();
        arrays.push({id:id, optype:1});
        $confirmModal.attr('opdatas',JSON.stringify(arrays)).modal('show');
    });

    //点击打票失败
    $(document).on('click','.dpsb_scheme_cls',function()
    {
        var id = $(this).parents('tr').first().attr('editid');
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('确定该票出票失败吗？');
        var arrays = new Array();
        arrays.push({id:id, optype:2});
        $confirmModal.attr('opdatas',JSON.stringify(arrays)).modal('show');
    });

    //点击确认操作
    $(document).on('click','#confirmModalSureBtn',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        //根据操作类型获取url
        var url = '${pageContext.request.contextPath}/ticket/shopquery/setTicketStatus';
        //发送请求
        if($.trim(url) != '')
        {
            $.ajax({
                url : url,
                type : 'post',
                data : {'datas': $confirmModal.attr('opdatas')},
                dataType : 'json',
                success : function(json)
                {
                    showoplayer(json);
                    querydatas(1);
                }
            });
        }
    });

    //点击批量出票成功
    $('#szcxcpBtn').on('click',function()
    {
        var schemeId = $('#shopschemeId').val();
        if(schemeId == '') {
            showoplayer({dcode:-1000,dmsg:'必须先通过方案号搜索后,才能使用批量功能!'});
            return;
        }
        var arrays = new Array();
        var count = 0;
        $(".fixtable_tableColumnClone .multi_checkbox_cls:checked").each(function()
        {
            var id = $(this).parents('tr').first().attr('editid');
            arrays.push({id:id, optype:1});
            var schid = $(this).parents('tr').first().attr('schid');
            if(schemeId == schid) {
                count++;
            }
        });
        if(arrays.length > 0) {
            if (arrays.length != count) {
                showoplayer({dcode: -1000, dmsg: '批量操作票方案号必须与搜索框中输入的方案号保持一致!'});
                return;
            }
            var $modalNode = $('#confirmModal');
            $modalNode.find('div.modal-body p').html('确定将所选的票进行批量出票成功吗？');
            $modalNode.attr('opdatas', JSON.stringify(arrays)).modal('show');
        }
        else
        {
            showoplayer({dcode:-1000,dmsg:'请至少选择一张票进行批量操作！'});
        }
    });

    //点击批量出票失败
    $('#szsbcpBtn').on('click',function()
    {
        var schemeId = $('#shopschemeId').val();
        if(schemeId == '') {
            showoplayer({dcode:-1000,dmsg:'必须先通过方案号搜索后,才能使用批量功能!'});
            return;
        }
        var arrays = new Array();
        var count = 0;
        $(".fixtable_tableColumnClone .multi_checkbox_cls:checked").each(function()
        {
            var id = $(this).parents('tr').first().attr('editid');
            arrays.push({id:id, optype:2});
            var schid = $(this).parents('tr').first().attr('schid');
            if(schemeId == schid) {
                count++;
            }
        });
        if(arrays.length > 0) {
            if (arrays.length != count) {
                showoplayer({dcode: -1000, dmsg: '批量操作票方案号必须与搜索框中输入的方案号保持一致!'});
                return;
            }
            var $modalNode = $('#confirmModal');
            $modalNode.find('div.modal-body p').html('确定将所选的票进行批量出票失败吗？');
            $modalNode.attr('opdatas', JSON.stringify(arrays)).modal('show');
        }
        else
        {
            showoplayer({dcode:-1000,dmsg:'请至少选择一张票进行批量操作！'});
        }
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

    //全选/全不选
    $(document).on("change",".fixtable_tableFixClone .multi_checkbox_allcls",function()
    {
        if($(this).is(":checked"))
        {
            $("#dataTables .multi_checkbox_allcls").prop("checked","checked");
            $(".fixtable_tableColumn .multi_checkbox_cls").prop("checked","checked");
            $("#dataTables .multi_checkbox_cls").prop("checked","checked");
        }
        else
        {
            $("#dataTables .multi_checkbox_allcls").prop("checked",false);
            $(".fixtable_tableColumn .multi_checkbox_cls").prop("checked",false);
            $("#dataTables .multi_checkbox_cls").prop("checked",false);
        }
    });

    //单选
    $(document).on("change",".fixtable_tableColumnClone .multi_checkbox_cls",function()
    {
        if($(this).is(":checked"))
        {
            $("#dataTables tr[editid='" + $(this).parents("tr[editid]").attr("editid") + "'] .multi_checkbox_cls").prop("checked",false);
            $(this).prop("checked","checked");
        }
        else
        {
            $("#dataTables tr[editid='" + $(this).parents("tr[editid]").attr("editid") + "'] .multi_checkbox_cls").prop("checked",false);
            $(this).prop("checked",false);
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
    data.url = '${pageContext.request.contextPath}/ticket/shopquery/list';
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
                html += '<tr editid="' + n.id + '" schid="' + n.schemeId + '">';
                html += '<td style="min-width: 30px;max-width: 30px;"><input type="checkbox" class="multi_checkbox_cls"></td>';
                html += '<td scolumn="detail"><a opauthority="menu_shopchupiao_query" href="javascript:;" class="label label-info mright10 set_ticket_detail">详情</a>';
                if(n.ticketStatus == 0) {
                    html += '<a opauthority="menu_shopchupiao_query" href="javascript:;" class="label label-info mright10 dpcg_scheme_cls">打票成功</a>';
                    html += '<a opauthority="menu_shopchupiao_query" href="javascript:;" class="label label-info mright10 dpsb_scheme_cls">打票失败</a>';
                }
                html += '<td scolumn="schemeId">' + n.schemeId + '</td>';
                html += '<td scolumn="playTypeId">' + (playNameJson[n.playTypeId] || LotteryJson[n.playTypeId]) + '</td>';
                html += '<td scolumn="period">' + n.period + '</td>';
                html += '<td scolumn="voteName">' + n.voteName + '</td>';
                html += '<td scolumn="multiple">' + n.multiple + '</td>';
                html += '<td scolumn="money">' + n.money + '</td>';
                html += '<td scolumn="ticketStatus">' + (((ticketStatusJson[n.ticketStatus])=='待提票' ? '等待出票' : ticketStatusJson[n.ticketStatus]) || '未知') + '</td>';
                html += '<td scolumn="endTime">' + n.endTime + '</a></td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '<td scolumn="voteTicketId">' + n.ticketId + '</td>';
                html += '<td scolumn="ticketPrizeTax">' + (n.ticketPrizeTax+n.ticketSubjoinPrizeTax) + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>