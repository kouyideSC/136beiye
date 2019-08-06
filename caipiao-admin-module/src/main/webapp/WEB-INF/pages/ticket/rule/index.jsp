<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_chupiao");
    request.setAttribute("sidebar_mcode","menu_chupiao_rule");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>分票规则-列表/分票规则查询</title>
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
                    <select class="form-control" name="lotteryId" id="lotterySelect" title="彩种" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 100px">
                    <select class="form-control" name="voteId" id="VoteSelect" title="出票商" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_chupiao_vote_add" type="button" class="btn btn-success" id="addTicketVote" style="margin-right:20px;">新增出票商</button>
                    <button opauthority="btn_chupiao_rule_add" type="button" class="btn btn-success" id="addTicketRule" style="margin-right:20px;">新增分票规则</button>
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
                        <th scolumn="voteName">出票商名称</th>
                        <th scolumn="lotteryName">彩种名称</th>
                        <th scolumn="playName">玩法名称</th>
                        <th scolumn="rate">分票比例</th>
                        <th scolumn="receiveTime">收票时间段</th>
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
                            <li scolumn="voteName">出票商名称</li>
                            <li scolumn="lotteryName">彩种名称</li>
                            <li scolumn="playName">玩法名称</li>
                            <li scolumn="rate">分票比例</li>
                            <li scolumn="receiveTime">收票时间段</li>
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
    //彩种名称下拉
    $('#lotterySelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
        id: 'id',
        name: 'shortName'
    });

    //出票商名称下拉
    $('#VoteSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/ticket/vote/list',
        id: 'voteId',
        name: 'voteName'
    });

    //点击删除
    $(document).on('click','.delete_rule',function()
    {
        var $trNode = $(this).parents('tr').first();
        var $modalNode = $('#confirmModal');
        $modalNode.find('div.modal-body p').html('确定删除该条分票规则吗？');
        $modalNode.attr('optype',1).attr('editDatas',$trNode.attr('editid')).modal('show');
    });

    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $modalNode = $(this).parents('div.modal').first();
        $.ajax({
            url : '${pageContext.request.contextPath}/ticket/rule/delete',
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

    //点击新增分票规则
    $('#addTicketRule').on('click',function()
    {
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/ticket/rule/initAdd');
        $('#operatorModal').modal('show');
    });

    //点击新增出票商
    $('#addTicketVote').on('click',function()
    {
        window.location = "${pageContext.request.contextPath}/ticket/vote/index";
    });

    //点击编辑
    $(document).on('click','.edit_rule',function()
    {
        var $trnode = $(this).parents('tr').first();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/ticket/rule/detail?id=' + $trnode.attr('editid'));
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
    data.url = '${pageContext.request.contextPath}/ticket/rule/list';
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
                html += '<tr editid="' + n.id + '" editName="'+ n.ruleName + '">';
                html += '<td>';
                html += '<div class="btn-group clearfix">';
                html += '<a opauthority="btn_chupiao_rule_edit" href="javascript:;" class="label label-info mright10 edit_rule">编辑</a>';
                html += '<a opauthority="btn_chupiao_rule_delete" href="javascript:;" class="label label-info mright10 delete_rule">删除</a>';
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="voteName">' + n.voteName + '</td>';
                html += '<td scolumn="lotteryName">' + n.lotteryName + '</td>';
                html += '<td dtauthority="btn_chupiao_rule_edit" scolumn="playType"><a href="javascript:;" class="edit_rule">' + n.playName + '</a></td>';
                html += '<td scolumn="rate">' + n.rate + '%</td>';
                html += '<td scolumn="receiveTime">' + n.receiveTime + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>