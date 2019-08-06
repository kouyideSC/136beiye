<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_order");
    request.setAttribute("sidebar_mcode","menu_order_schemepjsh");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>用户管理-方案派奖审核</title>
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
                    <select class="form-control selectpicker" title="派奖状态" name="prizeStatus">
                        <option value="0" selected>未派奖</option>
                        <option value="1">派奖中</option>
                        <option value="2">已派奖</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 125px;">
                    <input type="text" class="form-control" placeholder="期次号" title="方案所属期次号" name="period">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 185px;">
                    <input type="text" class="form-control" placeholder="方案编号" title="方案编号" name="schemeOrderId">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 160px;">
                    <input type="text" class="form-control" placeholder="编号/昵称/手机号" title="用户编号/用户昵称/用户手机号" name="userAttrs">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link show-more">高级筛选</button>
                <button class="btn btn-link show-off" style="display: none;">收起</button>
                <button class="btn btn-link clear-condition" style="padding-left: 0;">清除</button>
            </div>
            <div class="clearfix advanced">
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <input type="text" class="form-control" placeholder="最小方案奖金" name="minPrizeTax">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <input type="text" class="form-control" placeholder="最大方案奖金" name="maxPrizeTax">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 125px;">
                    <select class="form-control selectpicker" title="出票商" name="ticketVoteId" id="ticketVoteSelect"></select>
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 185px;">
                    <input type="text" class="form-control datepickerymd" title="方案最早下单日期" placeholder="方案最早下单日期" name="minCreateDate" id="minCreateDate">
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 160px">
                    <input type="text" class="form-control datepickerymd" title="方案最晚下单日期" placeholder="方案最晚下单日期" name="maxCreateDate">
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 180px;">
                    <input type="text" class="form-control datetimepicker" title="方案最早计奖时间" placeholder="方案最早计奖时间" name="minOpenTime" id="minOpenTime">
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 178px">
                    <input type="text" class="form-control datetimepicker" title="方案最晚计奖时间" placeholder="方案最晚计奖时间" name="maxOpenTime">
                </div>
            </div>
        </div>
        <div class="core clearfix">
            <div class="core-table pull-left" style="position: relative; width:100%">
                <a href="javascript:;" class="edit-item" data-toggle="modal" data-target="#edit-item" style=" z-index: 500;"><i class="plus-icon p-setting"></i></a>
                <table id="dataTables" style="" border="0" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th>操作</th>
                        <th scolumn="lotteryName">彩种</th>
                        <th scolumn="period">期次</th>
                        <th scolumn="schemeOrderId">方案编号</th>
                        <th scolumn="nickName">用户昵称</th>
                        <th scolumn="prizeStatus">派奖状态</th>
                        <th scolumn="schemeType">方案类型</th>
                        <th scolumn="schemeMultiple">倍数</th>
                        <th scolumn="schemeMoney">金额</th>
                        <th scolumn="prizeTax">中奖奖金</th>
                        <%--<th scolumn="jiajiangPrize">加奖奖金</th>--%>
                        <th scolumn="prizeTime">派奖时间</th>
                        <th scolumn="createTime">下单时间</th>
                        <th scolumn="ticketVoteName">出票商</th>
                    </tr>
                    </thead>
                    <tbody id="dataTbody"></tbody>
                </table>
                <div class="pull-right pagelist_cls" style="width: 100%;">
                    <div class="pull-left" style="height: 38px;line-height: 45px;margin-left: 10px;">
                        <strong>[总计]</strong>
                        <span>方案金额(元)：</span><em scolumn="tmoney">--</em>
                        <span style="padding-left: 10px;">中奖奖金(元)：</span><em scolumn="tprize">--</em>
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
                            <li scolumn="prizeStatus">派奖状态</li>
                            <li scolumn="schemeType">方案类型</li>
                            <li scolumn="schemeMultiple">倍数</li>
                            <li scolumn="schemeMoney">金额</li>
                            <li scolumn="prizeTax">中奖奖金</li>
                            <%--<li scolumn="jiajiangPrize">加奖奖金</li>--%>
                            <li scolumn="prizeTime">派奖时间</li>
                            <li scolumn="createTime">下单时间</li>
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
        name: 'name'
    });
    //点击确认派奖
    $(document).on('click','.qrpj_scheme_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('确定对方案' + datas['schemeOrderId'] + '进行派奖吗？');
        $confirmModal.attr('optype',1);//设置操作类型
        $confirmModal.attr('opdatas',JSON.stringify({id:datas['id'],iszh:(datas['schemeType'] == 1? '1' : '')}));
        $confirmModal.modal('show');
    });
    //出票商下拉
    $('#ticketVoteSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/ticket/vote/list',
        id: 'voteId',
        name: 'voteName'
    });
    //点击确认操作
    $(document).on('click','#confirmModalSureBtn',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        var optype = $confirmModal.attr('optype');//获取操作类型 1-确认派奖
        var opdatas = JSON.parse($confirmModal.attr('opdatas'));

        //根据操作类型获取url
        var url = '';
        if(optype == 1)
        {
            url = '${pageContext.request.contextPath}/user/scheme/qrpj';//确认派奖
        }
        //发送请求
        if($.trim(url) != '')
        {
            $.ajax({
                url : url,
                type : 'post',
                data : opdatas,
                dataType : 'json',
                success : function(json)
                {
                    showoplayer(json);
                    querydatas();
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
    $('#minOpenTime').val('${params.minOpenTime}');
    $('#minCreateDate').val('${params.minCreateDate}');
    querydatas(1);
});
//获取列表数据
var querydatas = function (pnum)
{
    var data = $('.operate').getConditionValue();
    data.url = '${pageContext.request.contextPath}/user/scheme/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 6;
    data.minOpenStatus = 1;//只查询已计奖的方案
    data.schemeStatus = 3;//只查询出票成功的方案
    data.containsZh = 1;
    data.notsorts = 1;
    data.sorts = 'openTime desc,createTime desc';
    data.openStatus = 2;
    pageAjax(data, function ()
    {
        var html = "";
        if(data.datas.list && data.datas.list.length > 0)
        {
            $.each(data.datas.list, function (i, n)
            {
                tbdatas[n.id] = n;
                html += '<tr dataid="' + n.id + '">';
                html += '<td>';
                html += '<div class="btn-group clearfix">';

                //确认派奖的条件:已中奖且未派奖
                if(n.openStatus == 2)
                {
                    if(n.prizeStatus == 0)
                    {
                        html += '<a opauthority="btn_order_schemepjsh_qrpj" href="javascript:;" class="label label-info mright5 qrpj_scheme_cls">确认派奖</a>';
                    }
                    else
                    {
                        html += '<span class="label label-primary">' + (n.prizeStatus == 1? '正在派奖' : '已派奖') + '</span>';
                    }
                }
                else if(n.openStatus == 1)
                {
                    html += '<span class="label label-primary">未中奖</span>';
                }
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="lotteryName">'+ n.lotteryName + '</td>';
                html += '<td scolumn="period">' + n.period + '</td>';
                html += '<td scolumn="schemeOrderId"><a dtauthority="menu_order_scheme" href="javascript:;" class="detail_scheme_cls">' + n.schemeOrderId + '</a></td>';
                html += '<td scolumn="nickName"><a dtauthority="menu_user_user" href="javascript:;" class="detail_user_cls">' + n.nickName + '</a></td>';
                html += '<td scolumn="prizeStatus">';
                if(n.openStatus == 1)
                {
                    html += '未中奖';
                }
                else
                {
                    html += n.prizeStatus == 0? '未派奖' : (n.prizeStatus == 1? '派奖中' : (n.prizeStatus == 2? '已派奖' : ''));
                }
                html += '</td>';
                html += '<td scolumn="schemeType">' + (n.schemeType == 1? '<span class="label label-danger">追号</span>' : '<span class="label label-success">普通</span>') + '</td>';
                html += '<td scolumn="schemeMultiple">' + n.schemeMultiple + '</td>';
                html += '<td scolumn="schemeMoney">' + (n.schemeMoney).toFixed(2) + '</td>';
                var prizedesc = n.prizeTax ;//奖金描述
                var jiajiangPrize = n.prizeSubjoinTax + n.prizeSubjoinSiteTax;
                var rewardPrize = n.rewardPrize;
                var flag = jiajiangPrize > 0 || (rewardPrize != null && rewardPrize > 0);
                prizedesc += flag? "（" : "";
                prizedesc += jiajiangPrize > 0? "加奖" + jiajiangPrize : "";
                prizedesc += ((rewardPrize != null && rewardPrize > 0)? ("+" + (n.schemeType == 3? ("支付赏金" + rewardPrize) : ("收取赏金" + rewardPrize))) : "");
                prizedesc += flag? "）" : "";
                html += '<td scolumn="prizeTax">' + prizedesc + '</td>';
                html += '<td scolumn="prizeTime">' + n.prizeTime + '</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '<td scolumn="ticketVoteName">' + n.ticketVoteName + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
        $('em[scolumn="tmoney"]').html(data.datas.tmoney || '--');
        $('em[scolumn="tprize"]').html(data.datas.tprize || '--');
    });
}
</script>
</html>