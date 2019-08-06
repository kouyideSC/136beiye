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
    <title>销售下级用户-列表</title>
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
                <div class="dropdown pull-left" style="margin-right: 10px; width: 125px">
                    <input type="text" class="form-control" placeholder="昵称搜索" name="nickName">
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 170px">
                    <input type="text" class="form-control datetimepicker" placeholder="查询销量开始时间" name="beginTime">
                </div>
                <span class="pull-left" style="line-height: 2; margin-right: 5px;margin-left: -5px;">-</span>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 170px">
                    <input type="text" class="form-control datetimepicker" placeholder="查询销量结束时间" name="endTime">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
            </div>
        </div>
        <div class="core clearfix">
            <div class="core-table pull-left" style="position: relative; width:100%">
                <a href="javascript:;" class="edit-item" data-toggle="modal" data-target="#edit-item" style=" z-index: 500;"><i class="plus-icon p-setting"></i></a>
                <table id="dataTables" style="min-width:1200px;" border="0" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th>操作</th>
                        <th scolumn="nickName">昵称</th>
                        <th scolumn="balance">余额</th>
                        <th scolumn="totalConsume">购彩/中奖金额</th>
                        <th scolumn="totalRecharge">充值/提现金额</th>
                        <th scolumn="sumMoney">用户总销量</th>
                        <th scolumn="registerTime">注册时间</th>
                        <th scolumn="bankIsBind">银行绑定</th>
                        <th scolumn="marketFrom">市场来源</th>
                        <th scolumn="loginDegree">登录</th>
                        <th scolumn="lastLoginTime">上次登录</th>
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
                            <li scolumn="nickName">昵称</li>
                            <li scolumn="balance">余额</li>
                            <li scolumn="totalConsume">购彩/中奖金额</li>
                            <li scolumn="totalRecharge">充值/提现金额</li>
                            <li scolumn="sumMoney">用户总销量</li>
                            <li scolumn="registerTime">注册时间</li>
                            <li scolumn="bankIsBind">银行绑定</li>
                            <li scolumn="marketFrom">市场来源</li>
                            <li scolumn="loginDegree">登录</li>
                            <li scolumn="lastLoginTime">上次登录</li>
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
var uid,code;
$(function ()
{
    //点击用户昵称(进入用户详情)
    $(document).on('click','.set_user_detail',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/sale/lower/detail?id=' + $trNode.attr('editid'));
    });

    //点击设置代理
    $(document).on('click','.edit_vote',function()
    {
        var $trNode = $(this).parents('tr').first();
        $('#confirmModal').find('.modal-body p').first().html('确定将用户('+ $trNode.attr('nname') +')升级为代理吗？');
        $('#confirmModal').modal('show');
        uid = $trNode.attr('editid');
        code = $trNode.attr('code');
    });

    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $modalNode = $(this).parents('tr').first();
        $.ajax({
            url : '${pageContext.request.contextPath}/sale/lower/setproxy?id=' + uid + '&code=' + code,
            type : 'get',
            dataType : 'json',
            success : function (json)
            {
                showoplayer(json);
                querydatas();
            }
        });
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
    data.url = '${pageContext.request.contextPath}/sale/lower/list';
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
                html += '<tr editid="' + n.id + '" nname="' + n.nickName + '" code="' + n.qq + '">';
                html += '<td>';
                html += '<div class="btn-group clearfix">';
                if(n.tsale == 1) {
                    html += '<a href="javascript:;" class="label label-info mright10 edit_vote">设为代理</a>';
                } else {
                    html += '--';
                }
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="nickName"><a href="javascript:;" class="set_user_detail">' + n.nickName + '</a></td>';
                html += '<td scolumn="balance">' + n.balance + '</td>';
                html += '<td scolumn="totalConsume">' + (n.totalConsume + '/' + n.totalAward) + '</td>';
                html += '<td scolumn="totalRecharge">' + (n.totalRecharge + '/' + n.totalWithDraw) + '</td>';
                html += '<td scolumn="sumMoney">' + (n.yhSaleMoney+n.yhSaleZhuiHaoMoney) + '元</td>';
                html += '<td scolumn="registerTime">' + n.registerTime + '</td>';
                html += '<td scolumn="bankIsBind">' + (n.bankIsBind==0?'未绑定':'已绑定') + '</a></td>';
                html += '<td scolumn="marketFrom">' + n.marketFrom + '</td>';
                html += '<td scolumn="loginDegree">' + n.loginDegree + '次</td>';
                html += '<td scolumn="lastLoginTime">' + n.lastLoginTime + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>