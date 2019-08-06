<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_order");
    request.setAttribute("sidebar_mcode","menu_order_schemecd");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>用户管理-方案撤单</title>
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
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <select class="form-control" title="彩种" name="lotteryId" id="lotterySelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <select class="form-control selectpicker" title="退款状态" name="schemeStatus" id="schemeStatusSelect">
                        <option value="4" selected>待撤单</option>
                        <option value="5">已撤单</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 125px;">
                    <input type="text" class="form-control" placeholder="期次号" title="方案所属期次号" name="period">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 185px;">
                    <input type="text" class="form-control" placeholder="方案编号" title="方案编号" name="schemeOrderId">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 170px;">
                    <input type="text" class="form-control" placeholder="编号/昵称/手机号" title="用户编号/用户昵称/用户手机号" name="userAttrs">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link show-more">高级筛选</button>
                <button class="btn btn-link show-off" style="display: none;">收起</button>
                <button class="btn btn-link clear-condition" style="padding-left: 0;">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_order_schemecd_cd" type="button" class="btn btn-success" id="cdBtn" style="margin-right:20px;">批量撤单</button>
                    <button opauthority="btn_order_schemecd_szcpcg" type="button" class="btn btn-success" id="szcpcgBtn" style="margin-right:20px;">批量出票成功</button>
                    <button opauthority="btn_order_schemecd_szcxcp" type="button" class="btn btn-success" id="szcxcpBtn">批量重新出票</button>
                </div>
            </div>
            <div class="clearfix advanced">
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <select class="form-control selectpicker" title="出票商" name="ticketVoteId" id="ticketVoteSelect"></select>
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
                        <th scolumn="lotteryName">彩种</th>
                        <th scolumn="period">期次</th>
                        <th scolumn="schemeOrderId">方案编号</th>
                        <th scolumn="nickName">用户昵称</th>
                        <th scolumn="schemeStatusDesc">方案状态</th>
                        <th scolumn="schemeMultiple">倍数</th>
                        <th scolumn="schemeMoney">金额</th>
                        <th scolumn="schemePayMoney">支付金额</th>
                        <th scolumn="theoryPrize">理论奖金</th>
                        <th scolumn="createTime">下单时间</th>
                        <th scolumn="endTime">方案截止时间</th>
                        <th scolumn="prizeTime">撤单时间</th>
                        <th scolumn="ticketVoteName">出票商</th>
                    </tr>
                    </thead>
                    <tbody id="dataTbody"></tbody>
                </table>
                <div class="pull-right pagelist_cls" style="width: 100%;">
                    <div class="pull-left" style="height: 38px;line-height: 45px;margin-left: 10px;">
                        <strong>[总计]</strong>
                        <span>撤单方案金额(元)：</span><em scolumn="tmoney">--</em>
                        <span style="padding-left: 10px;">撤单方案实际支付金额(元)：</span><em scolumn="tpaymoney">--</em>
                    </div>
                    <div class="pull-right" funcname="querydatas">
                        <jsp:include page="../../base/pagination.jsp"></jsp:include>
                    </div>
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
                            <li scolumn="lotteryName">彩种</li>
                            <li scolumn="period">期次</li>
                            <li scolumn="schemeOrderId">方案编号</li>
                            <li scolumn="nickName">用户昵称</li>
                            <li scolumn="schemeStatusDesc">方案状态</li>
                            <li scolumn="schemeMultiple">倍数</li>
                            <li scolumn="schemeMoney">金额</li>
                            <li scolumn="schemePayMoney">支付金额</li>
                            <li scolumn="theoryPrize">理论奖金</li>
                            <li scolumn="createTime">下单时间</li>
                            <li scolumn="endTime">方案截止时间</li>
                            <li scolumn="prizeTime">撤单时间</li>
                            <li scolumn="ticketVoteName">出票商</li>
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
                <button type="button" class="btn btn-save alertwarn btn_modal_cancel" data-dismiss="modal" id="confirmModalSureBtn">确定</button>
            </div>
        </div>
    </div>
</div>
</body>
<script>
var schemeTypeJsons = new Object();
var schemeStatusColorJsons = {'-1':'label-failed','0':'label-danger','1':'label-success','2':'label-success','3':'label-success','4':'label-danger','5':'label-primary'};
var tbdatas = new Object();
$(function ()
{
    //彩种名称下拉
    $('#lotterySelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
        id: 'id',
        name: 'name',
        callback:function(json)
        {
            $('#lotterySelect').selectpicker('val',1700);
        }
    });
    //出票商下拉
    $('#ticketVoteSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/ticket/vote/list',
        id: 'voteId',
        name: 'voteName'
    });
    //点击撤单
    $(document).on('click','.cancel_scheme_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('确定对方案' + datas['schemeOrderId'] + '进行撤单吗？');
        $confirmModal.attr('optype',1);//设置操作类型
        var arrays = new Array();
        arrays.push({id:datas['id'],iszh:(datas['schemeType'] == 1? '1' : '')});
        $confirmModal.attr('opdatas',JSON.stringify(arrays)).modal('show');
    });
    //点击批量撤单
    $('#cdBtn').on('click',function()
    {
        var arrays = new Array();
        var count = 0;
        $(".fixtable_tableColumnClone .multi_checkbox_cls:checked").each(function()
        {
            var datas = tbdatas[$(this).parents("tr[dataid]").first().attr('dataid')];
            arrays.push({id:datas['id'],iszh:(datas['schemeType'] == 1? '1' : '')});
            count += (datas['schemeStatus'] == 1 || datas['schemeStatus'] == 2 || datas['schemeStatus'] == 4
                    || datas['schemeStatus'] == 6 || datas['schemeStatus'] == 7)? 1 : 0;
        });
        if(arrays.length > 0)
        {
            if(count == 0)
            {
                showoplayer({dcode:-1000,dmsg:'所选择的方案中没有满足撤单条件的方案！请重新选择'});
            }
            else
            {
                var $modalNode = $('#confirmModal');
                $modalNode.find('div.modal-body p').html('确定将所选的方案进行撤单操作吗？');
                $modalNode.attr('optype',1).attr('opdatas',JSON.stringify(arrays)).modal('show');
            }
        }
        else
        {
            showoplayer({dcode:-1000,dmsg:'请至少选择一条方案记录进行撤单操作！'});
        }
    });
    //点击出票成功
    $(document).on('click','.cpcg_scheme_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('确定将方案' + datas['schemeOrderId'] + '设置为出票成功吗？');
        $confirmModal.attr('optype',2);//设置操作类型
        var arrays = new Array();
        arrays.push({id:datas['id'],iszh:(datas['schemeType'] == 1? '1' : '')});
        $confirmModal.attr('opdatas',JSON.stringify(arrays)).modal('show');
    });
    //点击批量出票成功
    $('#szcpcgBtn').on('click',function()
    {
        var arrays = new Array();
        var count = 0;
        $(".fixtable_tableColumnClone .multi_checkbox_cls:checked").each(function()
        {
            var datas = tbdatas[$(this).parents("tr[dataid]").first().attr('dataid')];
            arrays.push({id:datas['id'],iszh:(datas['schemeType'] == 1? '1' : '')});
            count += (datas['schemeStatus'] == 1 || datas['schemeStatus'] == 2 || datas['schemeStatus'] == 4
            || datas['schemeStatus'] == 6 || datas['schemeStatus'] == 7)? 1 : 0;
        });
        if(arrays.length > 0)
        {
            if(count == 0)
            {
                showoplayer({dcode:-1000,dmsg:'所选择的方案中没有满足出票成功条件的方案！请重新选择'});
            }
            else
            {
                var $modalNode = $('#confirmModal');
                $modalNode.find('div.modal-body p').html('确定将所选的方案进行出票成功操作吗？');
                $modalNode.attr('optype',2).attr('opdatas',JSON.stringify(arrays)).modal('show');
            }
        }
        else
        {
            showoplayer({dcode:-1000,dmsg:'请至少选择一条方案记录进行出票成功操作！'});
        }
    });
    //点击重新出票
    $(document).on('click','.cxcp_scheme_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('确定对方案' + datas['schemeOrderId'] + '进行重新出票吗？');
        $confirmModal.attr('optype',3);//设置操作类型
        var arrays = new Array();
        arrays.push({id:datas['id'],iszh:(datas['schemeType'] == 1? '1' : '')});
        $confirmModal.attr('opdatas',JSON.stringify(arrays)).modal('show');
    });
    //点击批量重新出票
    $('#szcxcpBtn').on('click',function()
    {
        var arrays = new Array();
        var count = 0;
        $(".fixtable_tableColumnClone .multi_checkbox_cls:checked").each(function()
        {
            var datas = tbdatas[$(this).parents("tr[dataid]").first().attr('dataid')];
            arrays.push({id:datas['id'],iszh:(datas['schemeType'] == 1? '1' : '')});
            count += (datas['schemeStatus'] == 4 || datas['schemeStatus'] == 6 || datas['schemeStatus'] == 7)? 1 : 0;
        });
        if(arrays.length > 0)
        {
            if(count == 0)
            {
                showoplayer({dcode:-1000,dmsg:'所选择的方案中没有满足重新出票条件的方案！请重新选择'});
            }
            else
            {
                var $modalNode = $('#confirmModal');
                $modalNode.find('div.modal-body p').html('确定将所选的方案进行重新出票操作吗？');
                $modalNode.attr('optype',3).attr('opdatas',JSON.stringify(arrays)).modal('show');
            }
        }
        else
        {
            showoplayer({dcode:-1000,dmsg:'请至少选择一条方案记录进行重新出票操作！'});
        }
    });
    //点击确认操作
    $(document).on('click','#confirmModalSureBtn',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        var optype = $confirmModal.attr('optype');//获取操作类型 1-撤单 2-出票成功 3-重新出票
        var opdatas = JSON.parse($confirmModal.attr('opdatas'));

        //根据操作类型获取url
        var url = '';
        if(optype == 1)
        {
            url = '${pageContext.request.contextPath}/user/scheme/cancel';//撤单
        }
        else if(optype == 2)
        {
            url = '${pageContext.request.contextPath}/user/scheme/cpcg';//出票成功
        }
        else if(optype == 3)
        {
            url = '${pageContext.request.contextPath}/user/scheme/cxcp';//重新出票
        }
        //发送请求
        if($.trim(url) != '')
        {
            $.ajax({
                url : url,
                type : 'post',
                data : {'cdinfos': $confirmModal.attr('opdatas')},
                dataType : 'json',
                success : function(json)
                {
                    if((optype == 1 || optype == 2 || optype == 3) && json.dcode == 1001)
                    {
                        $.each(opdatas,function(i,m)
                        {
                            opdatas[i].iscontinue = 1;
                        });
                        $confirmModal.attr('opdatas',JSON.stringify(opdatas));
                        var html = '<span style="color:red">' + json.dmsg + '<br/>是否继续进行' + (optype == 1? "撤单" : "出票成功") + '操作？</span>';
                        $confirmModal.find('.modal-body p').first().html(html);
                        setTimeout(function(){
                            $confirmModal.modal('show');
                        },500)
                    }
                    else
                    {
                        showoplayer(json);
                        querydatas();
                    }
                }
            });
        }
    });
    //点击方案编号(进入方案详细)
    $(document).on('click','.detail_scheme_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#detailCard').addClass('card-wrap-show');
        var url = '${pageContext.request.contextPath}/user/scheme/detail?id=' + datas['id'];//onlyZh
        if(datas['schemeOrderId'].indexOf('ZH') >= 0)
        {
            url += '&onlyZh=1';
        }
        $('#detailCard').fillWithUrl(url);
    });
    //点击用户昵称(进入用户详细)
    $(document).on('click','.detail_user_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/user/detail?id=' + datas['userId']);
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
    querydatas(1);
});
//获取列表数据
var querydatas = function (pnum)
{
    var data = $('.operate').getConditionValue();
    data.url = '${pageContext.request.contextPath}/user/scheme/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 3;
    data.containsZh = 1;
    data.notsorts = 1;
    data.sorts = 'schemeStatus desc,outTicketTime desc,createTime desc';
    if(data.schemeStatus == 4)
    {
        delete data.schemeStatus;
        data.isdcd = 1;
    }
    pageAjax(data, function ()
    {
        var html = "";
        if(data.datas.list && data.datas.list.length > 0)
        {
            $.each(data.datas.list, function (i, n)
            {
                tbdatas[n.id] = n;
                html += '<tr dataid="' + n.id + '">';
                html += '<td style="min-width: 30px;max-width: 30px;"><input type="checkbox" class="multi_checkbox_cls"></td>';
                html += '<td>';
                html += '<div class="btn-group clearfix">';

                //撤单/出票成功的条件:方案状态为支付成功/出票中/出票失败/出票失败待撤单/截止未出票待撤单
                if(n.schemeStatus == 1 || n.schemeStatus == 2 || n.schemeStatus == 4 || n.schemeStatus == 6 || n.schemeStatus == 7)
                {
                    html += '<a opauthority="btn_order_schemecd_cd" href="javascript:;" class="label label-danger mright5 cancel_scheme_cls">撤单</a>';
                    html += '<a opauthority="btn_order_schemecd_szcpcg" href="javascript:;" class="label label-info mright5 cpcg_scheme_cls">出票成功</a>';
                }
                //重新出票的条件:方案状态为出票失败/出票失败待撤单/截止未出票待撤单
                if(n.schemeStatus == 4 || n.schemeStatus == 6 || n.schemeStatus == 7)
                {
                    html += '<a opauthority="btn_order_schemecd_szcxcp" href="javascript:;" class="label label-warning mright5 cxcp_scheme_cls">重新出票</a>';
                }
                if(n.schemeStatus == 5)
                {
                    html += '<span class="label label-primary">已撤单</span>';
                }
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="lotteryName">'+ n.lotteryName + '</td>';
                html += '<td scolumn="period">' + n.period + '</td>';
                html += '<td scolumn="schemeOrderId"><a dtauthority="menu_order_scheme" href="javascript:;" class="detail_scheme_cls">' + n.schemeOrderId + '</a></td>';
                html += '<td scolumn="nickName"><a dtauthority="menu_user_user" href="javascript:;" class="detail_user_cls">' + n.nickName + '</a></td>';
                html += '<td scolumn="schemeStatusDesc">' + n.schemeStatusDesc + '</td>';
                html += '<td scolumn="schemeMultiple">' + n.schemeMultiple + '</td>';
                html += '<td scolumn="schemeMoney">' + (n.schemeMoney).toFixed(2) + '</td>';
                html += '<td scolumn="schemePayMoney">' + (n.schemeMoney).toFixed(2) + '</td>';
                html += '<td scolumn="theoryPrize">' + (n.theoryPrize || '--') + '</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '<td scolumn="endTime">' + n.endTime + '</td>';
                html += '<td scolumn="prizeTime">' + n.prizeTime + '</td>';
                html += '<td scolumn="ticketVoteName">' + n.ticketVoteName + '</td>';
            });
            $('#dataTbody').html(html);
        }
        $('em[scolumn="tmoney"]').html(data.datas.tmoney || '--');
        $('em[scolumn="tpaymoney"]').html(data.datas.tpaymoney || '--');
    });
}
</script>
</html>