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
    <title>维护-赛事/赛果维护</title>
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
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 100px">
                    <select class="form-control" name="lotteryId" id="lotteryIdSelect">
                        <option value="1700">竞彩足球</option>
                        <option value="1710">竞彩篮球</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 100px">
                    <select class="form-control" title="销售状态" name="status" id="statusSelect" multiple>
                        <option value="-1">已截止</option>
                        <option value="0">未开售</option>
                        <option value="1">销售中</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 100px">
                    <select class="form-control" name="state" id="stateSelect" title="计奖状态" data-live-search="true" data-size="8" data-selected-text-format="count > 3" multiple></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 160px">
                    <input type="text" class="form-control" placeholder="输入期次号或场次号" name="attrValue" value="${params.currentPeriod}">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_weihu_match_xgxszt" type="button" class="btn btn-success" id="multiEditMatchSellStatusBtn">批量修改销售状态</button>
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
                        <th scolumn="matchCode" style="max-width: 250px;">场次号</th>
                        <th scolumn="leagueName" style="max-width: 150px;">赛事名称</th>
                        <th scolumn="hostName">主队名</th>
                        <th scolumn="guestName">客队名</th>
                        <th scolumn="isHot">是否热门</th>
                        <th scolumn="status">销售状态</th>
                        <th scolumn="state">计奖状态</th>
                        <th scolum="halfScore">半场比分</th>
                        <th scolumn="score">全场比分</th>
                        <th scolumn="matchTime">比赛时间</th>
                        <th scolumn="period" style="max-width: 250px;">期次</th>
                        <th scolumn="weekday">竞彩编号</th>
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
                            <li scolumn="matchCode">场次号</li>
                            <li scolumn="leagueName">赛事名称</li>
                            <li scolumn="hostName">主队名</li>
                            <li scolumn="guestName">客队名</li>
                            <li scolumn="isHot">是否热门</li>
                            <li scolumn="status">销售状态</li>
                            <li scolumn="state">计奖状态</li>
                            <li scolum="halfScore">半场比分</li>
                            <li scolumn="score">全场比分</li>
                            <li scolumn="matchTime">比赛时间</li>
                            <li scolumn="period">期次</li>
                            <li scolumn="weekday">竞彩编号</li>
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
$(function ()
{
    //初始化下拉筛选
    $('#lotteryIdSelect').selectpicker({});
    $('#statusSelect').selectpicker({});

    //对阵场次处理状态下拉
    $('#stateSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/match/getMatchJjStatesCombo',
        id: 'state',
        name: 'description'
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
    //点击修改销售状态
    $(document).on('click','.edit_sellStatus_cls',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/match/initSellStatus?id=' + $trNode.attr('editid') + '&lotteryId=' + $trNode.attr('lotteryId'));
        $('#operatorModal').modal('show');
    });
    //点击批量修改销售状态
    $('#multiEditMatchSellStatusBtn').on('click',function()
    {
        var arrays = new Array();
        var lotteryId = '';
        var count = 0;
        $(".fixtable_tableColumnClone .multi_checkbox_cls:checked").each(function()
        {
            var $trNode = $(this).parents("tr[editid]").first();
            arrays.push($trNode.attr('editid'));
            lotteryId = $trNode.attr('lotteryId');
            if($trNode.attr('status') != -1 && $trNode.attr('status') != 2)
            {
                count ++;
            }
        });
        if(arrays == 0)
        {
            showoplayer({dcode:-1000,dmsg:'请至少选择一条记录进行修改！'});
        }
        /*else if(count == 0)
        {
            showoplayer({dcode:-1000,dmsg:'所选择场次中没有可以修改销售状态的记录！'});
        }*/
        else
        {
            var $trNode = $(this).parents('tr').first();
            $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/match/initSellStatus?ids=' + arrays.join(',') + '&lotteryId=' + lotteryId);
            $('#operatorModal').modal('show');
        }
    });
    //点击场次号(进入赛事详细)
    $(document).on('click','.detail_match_cls',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/weihu/match/initDetail?id=' + $trNode.attr('editid') + '&lotteryId=' + $trNode.attr('lotteryId'));
    });
    //点击审核赛果
    $(document).on('click','.audit_match_cls',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/match/initAudit?id=' + $trNode.attr('editid') + '&lotteryId=' + $trNode.attr('lotteryId'));
        $('#operatorModal').modal('show');
    });
    //点击编辑sp
    $(document).on('click','.edit_sp_cls',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/match/initSp?id=' + $trNode.attr('editid') + '&lotteryId=' + $trNode.attr('lotteryId'));
        $('#operatorModal').modal('show');
    });
    //点击修改玩法
    $(document).on('click','.edit_playSellStatus_cls',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/match/initPlaySellStatus?id=' + $trNode.attr('editid') + '&lotteryId=' + $trNode.attr('lotteryId'));
        $('#operatorModal').modal('show');
    });
    //点击更改是否热门
    $(document).on('click','.edit_ishot_cls',function()
    {
        var $trNode = $(this).parents('tr').first();
        var $modalNode = $('#confirmModal');
        var confirmMsg = '确定将 ';
        if($trNode.attr('lotteryId') == '1700')
        {
            confirmMsg += $trNode.attr('hostName') + ' VS ' + $trNode.attr('guestName');
        }
        else if($trNode.attr('lotteryId') == '1710')
        {
            confirmMsg += $trNode.attr('guestName') + ' VS ' + $trNode.attr('hostName');
        }
        if($trNode.attr('isHot') == 0)
        {
            confirmMsg += ' 设置为热门比赛吗？';
        }
        else
        {
            confirmMsg += ' 取消热门吗？';
        }
        $modalNode.find('div.modal-body p').html(confirmMsg);
        var data = {id:$trNode.attr('editid'),lotteryId:$trNode.attr('lotteryId'),isHot:($trNode.attr('isHot') == 0? 1 : 0)};
        $modalNode.attr('optype',1).attr('editDatas',JSON.stringify(data)).modal('show');
    });
    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $modalNode = $(this).parents('div.modal').first();
        var data = new Object();
        var url = '';
        if($modalNode.attr('optype') == 1)
        {
            data = $.parseJSON($modalNode.attr('editDatas'));
            url = '${pageContext.request.contextPath}/weihu/match/editHot'
        }
        $.ajax({
            url : url,
            type : 'post',
            dataType : 'json',
            data : data,
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
var jjstateJson = {0:'待处理',1:'自动撤单中',2:'赛果获取中',3:'已有赛果待审核',4:'赛果人工审核成功',5:'系统审核成功',6:'计算奖金成功',7:'奖金汇总成功',8:'奖金核对成功',9:'自动派奖成功',10:'过关统计完成',11:'战绩统计完成',12:'派送返点成功',99:'场次处理结束'};
var sellStatusJson = {'-1':'已取消','0':'已停售','1':'销售中','2':'已截止'};
var querydatas = function (pnum)
{
    //根据选择的彩种切换显示主客队列的位置
    var $tdNodes = $('#dataTables th[scolumn="hostName"],#dataTables th[scolumn="guestName"]');
    if($tdNodes.length == 2)
    {
        var $firstTdNode = $tdNodes.slice(0,1);
        if($('#lotteryIdSelect').val() == '1700')
        {
            if($firstTdNode.attr('scolumn') == 'guestName')
            {
                $firstTdNode.insertAfter($tdNodes.slice(1,2))
            }
        }
        else if($('#lotteryIdSelect').val() == '1710')
        {
            if($firstTdNode.attr('scolumn') == 'hostName')
            {
                $firstTdNode.insertAfter($tdNodes.slice(1,2));
            }
        }
    }
    if (typeof pnum == 'undefined')
    {
        pnum = $("#pagelist li[page][class*='active']").attr("page");
    }
    var data = $('.operate').getConditionValue();
    data.url = '${pageContext.request.contextPath}/weihu/match/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 4;
    pageAjax(data, function ()
    {
        $(data.select).parents('.fixtable_tableLayout:first').find('.multi_checkbox_allcls').prop("checked",false);
        var html = "";
        if(data.datas.list && data.datas.list.length > 0)
        {
            var lotteryId = $('#lotteryIdSelect').val();
            $.each(data.datas.list, function (i, n)
            {
                html += '<tr editid="' + n.id + '" period="' + n.period + '" hostName="' + n.hostName
                        + '" guestName="' + n.guestName + '" lotteryId="' + lotteryId
                        + '" isHot="' + (n.isHot == true? 1 : 0) + '" status="' + n.status + '">';
                html += '<td style="min-width: 30px;max-width: 30px;"><input type="checkbox" class="multi_checkbox_cls"></td>';
                html += '<td>';
                html += '<div class="btn-group clearfix">';
                if(n.state == 2 || n.state == 3 || n.state == 4)//赛果获取中或已有赛果待审核才允许审核比赛
                {
                    html += '<a opauthority="btn_weihu_match_audit" href="javascript:;" class="label label-warning mright10 audit_match_cls">赛果审核</button>';
                }
                html += '<a opauthority="btn_weihu_match_xgwfxszt" href="javascript:;" class="label label-info mright5 edit_playSellStatus_cls">修改玩法</a>';
                if(n.status != -1 && n.status != 2)//未取消且未截止的比赛才允许修改销售状态
                {
                    html += '<a opauthority="btn_weihu_match_xgxszt" href="javascript:;" class="label label-info mright10 edit_sellStatus_cls">修改状态</button>';
                }
                //html += '<a opauthority="btn_weihu_match_xgsp" href="javascript:;" class="label label-info mright10 edit_sp_cls">修改sp</a>';
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="matchCode" style="max-width: 250px;">' + (n.matchCode || '--') + '</td>';
                html += '<td scolumn="leagueName" style="max-width: 150px;"><span class="label label-success" style="background-color:' + n.leagueColor + ';">' + (n.leagueName || '') + '</span></td>';

                //判断选择的彩种显示相应的差异列
                if(lotteryId == '1700')
                {
                    html += '<td scolumn="hostName">' + n.hostName + '</td>';
                    html += '<td scolumn="guestName">' + n.guestName + '</td>';
                }
                else
                {
                    html += '<td scolumn="guestName">' + n.guestName + '</td>';
                    html += '<td scolumn="hostName">' + n.hostName + '</td>';
                }
                html += '<td scolumn="isHot"><a dtauthority="btn_weihu_match_szrm" href="javascript:;" class="edit_ishot_cls">' + (n.isHot? '是' : '否') + '</a></td>';
                html += '<td scolumn="status"><span class="label ' + sellStatusColorJson[n.status] + '">' + (sellStatusJson[n.status]) + '</span></td>';
                html += '<td scolumn="state"><span class="label ' + stateStatusColorJson[n.state] + '">' + (jjstateJson[n.state]) + '</span></td>';
                html += '<td scolumn="halfScore">' + (n.halfScore || '--') + '</td>';
                html += '<td scolumn="score">' + (n.score || '--') + '</td>';
                html += '<td scolumn="matchTime">' + (n.matchTime || '--') + '</td>';
                html += '<td scolumn="period" style="max-width: 250px;">' + n.period + '</td>';
                html += '<td scolumn="weekday" style="max-width: 150px;">'+ (n.weekday + n.jcId) + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>