<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_chupiao");
    request.setAttribute("sidebar_mcode","menu_shopchupiao_change");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>实体店切票管理-列表/票查询</title>
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
                <input type="hidden" name="ticketStatus" value="-1">
                <input type="hidden" name="change" value="1">
                <div class="dropdown pull-left" style="margin-right: 10px; width: 150px">
                    <input type="text" class="form-control" placeholder="方案号" name="schemeId">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 150px">
                    <input type="text" class="form-control" placeholder="方案票号" name="ticketId">
                </div>
                <button class="btn btn-info do-condition">筛选</button>&nbsp;
                <button opauthority="btn_shopchupiao_change_qp" class="btn btn-info do-condition" id="changeTicket">批量切票</button>
            </div>
        </div>
        <div class="core clearfix">
            <div class="core-table pull-left" style="position: relative; width:100%">
                <a href="javascript:;" class="edit-item" data-toggle="modal" data-target="#edit-item" style=" z-index: 500;"><i class="plus-icon p-setting"></i></a>
                <table id="dataTables" style="min-width:1200px;" border="0" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th style="min-width: 30px;max-width: 30px;"><input type="checkbox" class="multi_checkbox_allcls"></th>
                        <th scolumn="schemeId">方案号</th>
                        <th scolumn="playTypeId">彩种玩法</th>
                        <th scolumn="period">期次</th>
                        <th scolumn="voteName">出票商</th>
                        <th scolumn="multiple">倍数</th>
                        <th scolum="money">金额</th>
                        <th scolumn="ticketStatus">出票状态</th>
                        <th scolumn="ticketId">方案票号</th>
                        <th scolumn="endTime">截止时间</th>
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
                            <li>操作</li>
                            <li scolumn="schemeId">方案号</li>
                            <li scolumn="playTypeId">彩种玩法</li>
                            <li scolumn="period">期次</li>
                            <li scolumn="voteName">出票商</li>
                            <li scolumn="multiple">倍数</li>
                            <li scolum="money">金额</li>
                            <li scolumn="ticketStatus">出票状态</li>
                            <li scolumn="ticketId">方案票号</li>
                            <li scolumn="endTime">截止时间</li>
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
                <h4 class="modal-title">您正在进行批量切票操作</h4>
            </div>
            <div class="modal-body clearfix">
                <div class="form-group">
                    <label class="col-sm-3 control-label"></span>切票至新出票商</label>
                    <div class="col-sm-7">
                        <select class="form-control" id="toVoteDiv" name="toVoteDiv" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                        <p></p>
                    </div>
                </div>
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

    //切票
    $('#changeTicket').on('click',function(){
        var arrays = new Array();
        $(".fixtable_tableColumnClone .multi_checkbox_cls:checked").each(function()
        {
            var $trNode = $(this).parents("tr[editid]").first();
            arrays.push({id:$trNode.attr('editid')});
        });
        if(arrays.length > 0)
        {
            //发送请求
            $.ajax({
                url: '${pageContext.request.contextPath}/ticket/shopvote/list',
                type:'post',
                dataType:'json',
                success : function(data)
                {
                    var html = '';
                    $.each(data.datas.list, function(i,m)
                    {
                        html += '<option value="' + $(m).attr('voteId') + '">'+$(m).attr('voteName')+'</option>';
                    });
                    $('#toVoteDiv').html(html);
                    var $modalNode = $('#confirmModal');
                    $modalNode.attr('optype',2).attr('editDatas',JSON.stringify(arrays)).modal('show');
                }
            });
        }
        else
        {
            showoplayer({dcode:-1000,dmsg:'请至少选择一张票进行切票！'});
        }
    });

    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $modalNode = $(this).parents('div.modal').first();
        $.ajax({
            url : '${pageContext.request.contextPath}/ticket/shopchange/qiepiao',
            type : 'post',
            dataType : 'json',
            data : {datas:$modalNode.attr('editDatas'),voteId:$('#toVoteDiv').val()},
            success : function (json)
            {
                showoplayer(json);
                querydatas(1);
            }
        });
    });

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
    $.ajax({
        url: '${pageContext.request.contextPath}/ticket/getSchemeStatus?module=1',
        async: false,
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            $.each(data.datas.list, function(i,m)
            {
                ticketStatusJson[$(m).attr('id')] = $(m).attr('value');
            });
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
                html += '<tr editid="' + n.id + '">';
                html += '<td style="min-width: 30px;max-width: 30px;"><input type="checkbox" class="multi_checkbox_cls"></td>';
                html += '<td scolumn="schemeId"><a href="javascript:;" class="">' + n.schemeId + '</a></td>';
                html += '<td scolumn="playTypeId">' + (playNameJson[n.playTypeId] || LotteryJson[n.playTypeId]) + '</td>';
                html += '<td scolumn="period">' + n.period + '</td>';
                html += '<td scolumn="voteName">' + n.voteName + '</td>';
                html += '<td scolumn="multiple">' + n.multiple + '</td>';
                html += '<td scolumn="money">' + n.money + '</td>';
                html += '<td scolumn="ticketStatus">' + (ticketStatusJson[n.ticketStatus] || '未知') + '</td>';
                html += '<td scolumn="ticketId">' + n.ticketId + '</td>';
                html += '<td scolumn="endTime">' + n.endTime + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>