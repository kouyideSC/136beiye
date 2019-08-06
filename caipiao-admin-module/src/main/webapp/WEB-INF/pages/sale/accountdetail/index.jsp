<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_xiaoshou");
    request.setAttribute("sidebar_mcode","menu_xiaoshou_accountdetail");
%>
<html lang="en" class="app">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
<title>销售管理-用户账户流水</title>
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
                <div class="dropdown pull-left" style="margin-right: 10px; width: 130px">
                    <input type="text" class="form-control" placeholder="按用户编号" name="userId" value="${params.userId}">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 130px">
                    <input type="text" class="form-control" placeholder="按手机号" name="mobile">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 130px">
                    <input type="text" class="form-control" placeholder="按用户昵称" name="nickName">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 130px">
                    <input type="text" class="form-control" placeholder="按真实姓名" name="realName">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link show-more">高级筛选</button>
                <button class="btn btn-link show-off" style="display: none;">收起</button>
                <button class="btn btn-link clear-condition">清除</button>
            </div>
            <div class="clearfix advanced">
                <div class="dropdown pull-left" style="margin-right: 10px;width: 130px">
                    <select class="form-control selectpicker" title="交易类型" name="inType">
                        <option value="0">进账</option>
                        <option value="1">出账</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 130px">
                    <select class="form-control selectpicker" title="业务渠道" name="channelCode">
                        <option value="300">余额支付[购彩]</option>
                        <option value="301">优惠券支付[购彩]</option>
                        <option value="3100">线下人工转账提现</option>
                        <option value="3101">万两支付提现</option>
                        <option value="306">管理后台扣款</option>
                        <option value="307">佣金提现</option>
                        <option value="308">打赏扣除</option>
                        <option value="400">中奖金额</option>
                        <option value="4100">微信充值</option>
                        <option value="4101">微信H5充值</option>
                        <option value="4102">支付宝充值</option>
                        <option value="4103">支付宝H5充值</option>
                        <option value="4104">QQ钱包充值</option>
                        <option value="4105">京东钱包充值</option>
                        <option value="4106">银联充值</option>
                        <option value="406">管理后台加款</option>
                        <option value="407">预约失败退款</option>
                        <option value="408">盛付通提现失败退款</option>
                        <option value="412">优惠券退回</option>
                        <option value="413">佣金转入</option>
                        <option value="414">收获打赏</option>
                        <option value="415">注册送彩金</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 130px">
                    <select class="form-control selectpicker" title="状态" name="status">
                        <option value="-1">无效</option>
                        <option value="0">处理中</option>
                        <option value="1" selected>有效（已完成）</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 130px">
                    <input type="text" class="form-control" placeholder="金额范围-最小" name="minMoney">
                </div>
                <span class="pull-left" style="line-height: 2; margin-right: 5px;margin-left: -5px;">-</span>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 130px">
                    <input type="text" class="form-control" placeholder="金额范围-最大" name="maxMoney">
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 136px">
                    <input type="text" class="form-control datepicker" placeholder="时间范围-开始" name="minCreateTime">
                </div>
                <span class="pull-left" style="line-height: 2; margin-right: 5px;margin-left: -5px;">-</span>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 136px">
                    <input type="text" class="form-control datepicker" placeholder="时间范围-结束" name="maxCreateTime">
                </div>
            </div>
        </div>
        <div class="core clearfix">
            <div class="core-table pull-left" style="position: relative; width:100%">
                <a href="javascript:;" class="edit-item" data-toggle="modal" data-target="#edit-item" style=" z-index: 500;"><i class="plus-icon p-setting"></i></a>
                <table id="dataTables" style="min-width:1200px;" border="0" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th scolumn="userId">用户编号</th>
                        <th scolumn="nickName">用户昵称</th>
                        <th scolumn="mobile">手机号</th>
                        <th scolumn="realName">真实姓名</th>
                        <th scolumn="inType">交易类型</th>
                        <th scolumn="channelDesc">交易渠道</th>
                        <th scolumn="money">交易金额</th>
                        <th scolumn="lastBalance">交易前余额</th>
                        <th scolumn="balance">交易后余额</th>
                        <th scolumn="createTime">交易时间</th>
                        <th scolumn="status">状态</th>
                        <th scolumn="businessId">业务关联编号</th>
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
                            <li scolumn="userId">用户编号</li>
                            <li scolumn="nickName">用户昵称</li>
                            <li scolumn="mobile">手机号</li>
                            <li scolumn="realName">真实姓名</li>
                            <li scolumn="inType">交易类型</li>
                            <li scolumn="channelDesc">交易渠道</li>
                            <li scolumn="money">交易金额</li>
                            <li scolumn="lastBalance">交易前余额</li>
                            <li scolumn="balance">交易后余额</li>
                            <li scolumn="createTime">交易时间</li>
                            <li scolumn="status">状态</li>
                            <li scolumn="businessId">业务关联编号</li>
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
var tbdatas = new Object();
var inTypeJsons = {"0":"进账","1":"出账"};
var inTypeColorJsons = {"0":"label-success","1":"label-danger"};
var statusJsons = {"-1":"无效","0":"处理中","1":"有效（已完成）"};
var statusColorJsons = {"-1":"label-failed","0":"label-info","1":"label-success"};
$(function ()
{
    //点击业务关联编号(购彩)
    $(document).on('click','.detail_scheme_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/sale/scheme/detail?id=' + datas['businessId']);
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
    data.url = '${pageContext.request.contextPath}/sale/account/detail/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 5;
    data.payType = 1;
    pageAjax(data, function ()
    {
        var html = "";
        if(data.datas.list && data.datas.list.length > 0)
        {
            $.each(data.datas.list, function (i, n)
            {
                tbdatas[n.id] = n;
                html += '<tr dataid="' + n.id + '">';
                html += '<td scolumn="userId">' + n.userId + '</td>';
                html += '<td scolumn="nickName">' + n.nickName + '</td>';
                html += '<td scolumn="mobile">' + n.mobile + '</td>';
                html += '<td scolumn="realName">' + n.realName + '</td>';
                html += '<td scolumn="inType"><span class="label ' + (n.inType? inTypeColorJsons['1'] : inTypeColorJsons['0']) + '">' + (n.inType? inTypeJsons['1'] : inTypeJsons['0']) + '</span></td>';
                html += '<td scolumn="channelDesc">' + n.channelDesc + '</td>';
                html += '<td scolumn="money">' + n.money + '</td>';
                html += '<td scolumn="lastBalance">' + n.lastBalance + '</td>';
                html += '<td scolumn="balance">' + n.balance + '</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '<td scolumn="status"><span class="label ' + statusColorJsons[n.status] + '">' + statusJsons[n.status] + '</span></td>';
                if(n.channelCode == 300 || n.channelCode == 301 || n.channelCode == 400 || n.channelCode == 407)
                {
                    html += '<td scolumn="businessId"><a dtauthority="menu_order_scheme" href="javascript:;" class="detail_scheme_cls">' + n.businessId + '</a></td>';
                }
                else
                {
                    html += '<td scolumn="businessId">' + n.businessId + '</td>';
                }
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>