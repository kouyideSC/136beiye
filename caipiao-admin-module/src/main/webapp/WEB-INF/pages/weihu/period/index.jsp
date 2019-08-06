<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_weihu");
    request.setAttribute("sidebar_mcode","menu_weihu_period");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>维护-期次维护</title>
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
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 100px">
                    <select class="form-control" title="选择彩种" name="lotteryId" id="lotterySelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 100px">
                    <select class="form-control" title="销售状态" name="sellStatus" id="saleStatusSelect" multiple>
                        <option value="-1">已取消</option>
                        <option value="0">已停售</option>
                        <option value="1">销售中</option>
                        <option value="2">已截止</option>
                        <option value="3">未开售</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 100px">
                    <select class="form-control" title="期次状态" name="state" id="periodStateSelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3" multiple></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 100px">
                    <select class="form-control selectpicker" title="期次范围" name="periodRange" id="periodRangeSelect">
                        <option value="15" selected>最近15期</option>
                        <option value="30">最近30期</option>
                        <option value="50">最近50期</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 160px">
                    <input type="text" class="form-control" placeholder="输入期次号" name="period">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_weihu_period_add" type="button" class="btn btn-success" id="addPeriodBtn" style="margin-right:20px;">新增期次</button>
                    <button opauthority="btn_weihu_period_szjj" type="button" class="btn btn-success" id="setAddPrize" style="margin-right:20px;">设置加奖</button>
                    <button opauthority="btn_weihu_period_delete" type="button" class="btn btn-success" id="multiDeletePeriodBtn">批量删除</button>
                </div>
            </div>
        </div>
        <div class="core clearfix">
            <div class="core-table pull-left" style="position: relative; width:100%">
                <a href="javascript:;" class="edit-item" data-toggle="modal" data-target="#edit-item" style=" z-index: 500;"><i class="plus-icon p-setting"></i></a>
                <table id="dataTables" style="" border="0" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th style="min-width: 30px;max-width: 30px;"><input type="checkbox" class="multi_checkbox_allcls"></th>
                        <th>操作</th>
                        <th scolumn="lotteryName" style="max-width: 250px;">彩种</th>
                        <th scolumn="period" style="max-width: 250px;">期次</th>
                        <th scolumn="drawNumber" style="max-width: 150px;">开奖号码</th>
                        <th scolumn="state">计奖状态</th>
                        <th scolumn="sellStatus">销售状态</th>
                        <th scolumn="sellStartTime">销售开售时间</th>
                        <th scolumn="sellEndTime">销售截止时间</th>
                        <th scolumn="authorityEndTime">官方截止时间</th>
                        <th scolumn="updateFlag">覆盖标记</th>
                        <th scolumn="createTime">期次生成时间</th>
                        <th scolumn="stateTime">计奖状态变更时间</th>
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
                            <li scolumn="drawNumber">开奖号码</li>
                            <li scolumn="state">计奖状态</li>
                            <li scolumn="sellStatus">销售状态</li>
                            <li scolumn="sellStartTime">销售开售时间</li>
                            <li scolumn="sellEndTime">销售截止时间</li>
                            <li scolumn="authorityEndTime">官方截止时间</li>
                            <li scolumn="updateFlag">覆盖标记</li>
                            <li scolumn="createTime">期次生成时间</li>
                            <li scolumn="stateTime">计奖状态变更时间</li>
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
<div class="card-wrap" id="detailCard"></div>
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
var stateJson = new Object();
$(function ()
{
    //彩种名称下拉
    $('#lotterySelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
        id: 'id',
        name: 'name',
        noDefault : true
    });
    //销售状态下拉
    $('#saleStatusSelect').selectpicker({});
    //期次状态下拉
    $('#periodStateSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/period/getPeriodStatesCombo',
        id: 'state',
        name: 'description',
        callback : function(data)
        {
            $.each(data,function(i,m)
            {
                stateJson[$(m).attr('state')] = $(m).attr('description');
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
    //切换彩种
    $('#lotterySelect').on('change',function()
    {
        var id = $('#lotterySelect').val();
        if(id=='1700' || id=='1710') {
            $('#setAddPrize').hide();
        } else {
            $('#setAddPrize').show();
        }
    });
    //点击设置加奖
    $('#setAddPrize').on('click',function()
    {
        var lotteryId = $('#lotterySelect').val();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/period/setAddPrize?id=' + lotteryId);
        $('#operatorModal').modal('show');
    });
    //点击新增期次
    $('#addPeriodBtn').on('click',function()
    {
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/period/initAdd');
        $('#operatorModal').modal('show');
    });
    //点击编辑
    $(document).on('click','.edit_period_cls',function()
    {
        var $trnode = $(this).parents('tr').first();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/period/initEdit?id=' + $trnode.attr('editid') + "&lotteryId=" + $trnode.attr('lotteryId'));
        $('#operatorModal').modal('show');
    });
    //点击期次号(进入期次详细)
    $(document).on('click','.detail_period_cls',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/weihu/period/initDetail?id=' + $trNode.attr('editid') + "&lotteryId=" + $trNode.attr('lotteryId'));
    });
    //点击彩种(进入彩种详细)
    $(document).on('click','.detail_lottery_cls',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/weihu/lottery/initDetail?id=' + $trNode.attr('editid') + "&lotteryId=" + $trNode.attr('lotteryId'));
    });
    //点击审核
    $(document).on('click','.audit_period_cls',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/period/initAudit?id=' + $trNode.attr('editid') + "&lotteryId=" + $trNode.attr('lotteryId'));
        $('#operatorModal').modal('show');
    });
    //点击删除
    $(document).on('click','.delete_period_cls',function()
    {
        var $trNode = $(this).parents('tr').first();
        var $modalNode = $('#confirmModal');
        $modalNode.find('div.modal-body p').html('确定删除期次' + $trNode.attr('period') + '吗？');
        var arrays = new Array();
        arrays.push({id:$trNode.attr('editid'),lotteryId:$trNode.attr('lotteryId'),period:$trNode.attr('period')});
        $modalNode.attr('optype',1).attr('editDatas',JSON.stringify(arrays)).modal('show');
    });
    //点击批量删除
    $('#multiDeletePeriodBtn').on('click',function()
    {
        var arrays = new Array();
        $(".fixtable_tableColumnClone .multi_checkbox_cls:checked").each(function()
        {
            var $trNode = $(this).parents("tr[editid]").first();
            arrays.push({id:$trNode.attr('editid'),lotteryId:$trNode.attr('lotteryId'),period:$trNode.attr('period')});
        });
        if(arrays.length > 0)
        {
            var $modalNode = $('#confirmModal');
            $modalNode.find('div.modal-body p').html('确定删除所选择的期次吗？');
            $modalNode.attr('optype',2).attr('editDatas',JSON.stringify(arrays)).modal('show');
        }
        else
        {
            showoplayer({dcode:-1000,dmsg:'请至少选择一条记录进行删除！'});
        }
    });
    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $modalNode = $(this).parents('div.modal').first();
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/period/delete',
            type : 'post',
            dataType : 'json',
            data : {datas:$modalNode.attr('editDatas')},
            success : function (json)
            {
                showoplayer(json);
                querydatas(1);
            }
        });
    });
    //监听模态窗关闭
    $(document).on("hidden.bs.modal","#operatorModal",function()
    {
        querydatas();
    });
    querydatas(1);
});
//获取列表数据
//var stateJson = {0:'待处理',1:'自动撤单中',2:'开奖号获取中',3:'开奖号待审核',4:'开奖号审核成功',5:'开奖号同步成功',6:'中奖匹配成功',7:'计算奖金成功',8:'奖金汇总成功',9:'奖金核对成功',10:'自动派奖成功',11:'过关统计完成',12:'战绩统计完成',13:'派送返点成功',99:'期次处理结束'};
var querydatas = function (pnum)
{
    if (typeof pnum == 'undefined')
    {
        pnum = $("#pagelist li[page][class*='active']").attr("page");
    }
    var data = $('.operate').getConditionValue();
    data.url = '${pageContext.request.contextPath}/weihu/period/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 4;
    if(data.periodRange)
    {
        data.psize = 50;
    }
    pageAjax(data, function ()
    {
        $(data.select).parents('.fixtable_tableLayout:first').find('.multi_checkbox_allcls').prop("checked",false);
        var html = "";
        if(data.datas.list && data.datas.list.length > 0)
        {
            $.each(data.datas.list, function (i, n)
            {
                html += '<tr editid="' + n.id + '" period="' + n.period + '" lotteryId="' + n.lotteryId + '">';
                html += '<td style="min-width: 30px;max-width: 30px;"><input type="checkbox" class="multi_checkbox_cls"></td>';
                html += '<td style="min-width: 130px;">';
                html += '<div class="btn-group clearfix">';
                html += '<a opauthority="btn_weihu_period_edit" href="javascript:;" class="label label-info mright10 edit_period_cls">编辑</button>';
                html += '<a opauthority="btn_weihu_period_delete" href="javascript:;" class="label label-danger mright10 delete_period_cls">删除</a>';
                if(n.state == 2 || n.state == 3 || n.state == 4)
                {
                    html += '<a opauthority="btn_weihu_period_audit" href="javascript:;" class="label label-warning mright10 audit_period_cls">审核</a>';
                }
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="lotteryName" style="max-width: 250px;">' + n.lotteryName + '</td>';
                html += '<td scolumn="period" style="max-width: 250px;">' + n.period + '</td>';
                html += '<td scolumn="drawNumber" style="max-width: 150px;">'+ (n.drawNumber || '--') + '</td>';
                html += '<td scolumn="state"><span class="label ' + stateStatusColorJson[n.state] + '">' + stateJson[n.state] + '</span></td>';
                html += '<td scolumn="sellStatus"><span class="label ' + sellStatusColorJson[n.sellStatus] + '">' + (n.sellStatus == -1? '已取消' : (n.sellStatus == 0? '已停售' : (n.sellStatus == 1? '销售中' : (n.sellStatus == 2? '已截止' : '未开售')))) + '</span></td>';
                html += '<td scolumn="sellStartTime">' + (n.sellStartTime || '--') + '</td>';
                html += '<td scolumn="sellEndTime">' + (n.sellEndTime || '--') + '</td>';
                html += '<td scolumn="authorityEndTime">' + (n.authorityEndTime || '--') + '</td>';
                html += '<td scolumn="updateFlag">' + (n.updateFlag == 0? '覆盖' : '不覆盖') + '</td>';
                html += '<td scolumn="createTime">' + (n.createTime || '--') + '</td>';
                html += '<td scolumn="stateTime">' + (n.stateTime || '--') + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>