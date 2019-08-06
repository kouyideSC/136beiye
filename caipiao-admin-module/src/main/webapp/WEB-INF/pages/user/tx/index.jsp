<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_user");
    request.setAttribute("sidebar_mcode","menu_user_tx");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>用户-提现流水</title>
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
                <div class="dropdown pull-left" style="margin-right: 10px; width: 130px">
                    <input type="text" class="form-control" placeholder="按手机号" name="mobile">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 130px">
                    <input type="text" class="form-control" placeholder="按用户编号" name="userId" value="${params.userId}">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 158px">
                    <input type="text" class="form-control" placeholder="按用户昵称" name="nickName">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 158px">
                    <input type="text" class="form-control" placeholder="按真实姓名" name="realName">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link show-more">高级筛选</button>
                <button class="btn btn-link show-off" style="display: none;">收起</button>
                <button class="btn btn-link clear-condition">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button type="button" class="btn btn-success" id="export" style="margin-right:20px;">导出</button>
                </div>
            </div>
            <div class="clearfix advanced">
                <div class="dropdown pull-left" style="margin-right: 10px;width: 130px">
                    <select class="form-control selectpicker" title="订单状态" name="status">
                        <option value="-1">处理失败</option>
                        <option value="0">待处理</option>
                        <option value="1">等待重新处理</option>
                        <option value="2">处理中</option>
                        <option value="3">处理成功</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 130px">
                    <select class="form-control selectpicker" title="提现渠道" name="channelCode">
                        <option value="3100">线下人工转账</option>
                        <option value="3104">快付支付</option>
                        <option value="3105">kj412</option>
                        <option value="3106">ttpay</option>
                        <option value="3107">ypay</option>
                    </select>
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 156px">
                    <input type="text" class="form-control datepicker" placeholder="时间范围-开始" name="beginTime" id="beginTime">
                </div>
                <span class="pull-left" style="line-height: 2; margin-right: 5px;margin-left: -5px;">-</span>
                <div class="dropdown pull-left option-date" style="margin-right: 12px;width: 156px">
                    <input type="text" class="form-control datepicker" placeholder="时间范围-结束" name="endTime">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <input type="text" class="form-control" title="最小提现金额" placeholder="最小提现金额" name="minMoney">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <input type="text" class="form-control" title="最大提现金额" placeholder="最大提现金额" name="maxMoney">
                </div>
                <div class="dropdown pull-left" style="margin-left:2px;margin-right: 10px;width: 130px">
                    <select class="form-control selectpicker" title="客户端来源" name="clientFrom">
                        <option value="0">网站</option>
                        <option value="1">苹果</option>
                        <option value="2">安卓</option>
                        <option value="3">H5</option>
                        <option value="4">其它</option>
                    </select>
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
                        <th scolumn="userId">用户编号</th>
                        <th scolumn="nickName">用户昵称</th>
                        <th scolumn="mobile">手机号</th>
                        <th scolumn="realName">真实姓名</th>
                        <th scolumn="payId">平台流水号</th>
                        <th scolumn="money">金额</th>
                        <th scolumn="status">订单状态</th>
                        <th scolumn="bankInfo">提款银行</th>
                        <th scolumn="channelDesc">渠道</th>
                        <th scolumn="channelPayId">渠道流水号</th>
                        <th scolumn="createTime">
                            发起时间
                            <div class="sorter" sortfield="createTime">
                                <div class="sorter-up"></div>
                                <div class="sorter-down"></div>
                            </div>
                        </th>
                        <th scolumn="doneTime">处理完成时间</th>
                        <th scolumn="clientFrom">客户端来源</th>
                        <th scolumn="remark">交易备注</th>
                    </tr>
                    </thead>
                    <tbody id="dataTbody"></tbody>
                </table>
                <div class="pull-right pagelist_cls" style="width: 100%;">
                    <div class="pull-left" style="height: 38px;line-height: 45px;margin-left: 10px;">
                        <strong>[总计]</strong>
                        <span>金额(元)：</span><em scolumn="tmoney">--</em>
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
                            <li scolumn="userId">用户编号</li>
                            <li scolumn="nickName">用户昵称</li>
                            <li scolumn="mobile">手机号</li>
                            <li scolumn="realName">真实姓名</li>
                            <li scolumn="payId">平台流水号</li>
                            <li scolumn="money">金额</li>
                            <li scolumn="status">订单状态</li>
                            <li scolumn="bankInfo">付款银行</li>
                            <li scolumn="channelDesc">渠道</li>
                            <li scolumn="channelPayId">渠道流水号</li>
                            <li scolumn="createTime">发起时间</li>
                            <li scolumn="doneTime">处理完成时间</li>
                            <li scolumn="clientFrom">客户端来源</li>
                            <li scolumn="remark">交易备注</li>
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
var tbdatas = new Object();
var sdescJsons = {"-1":"处理失败","0":"待处理","1":"等待重新处理","2":"处理中","3":"处理成功"};
var statusColorJsons = {"-1":"label-failed","0":"label-warning","1":"label-danger","2":"label-info","3":"label-success"};
var clientFromJson = {"0":"网站","1":"苹果","2":"安卓","3":"H5","4":"其它"};
$(function ()
{
    //手动提现成功
    $(document).on('click','.award_confirm',function()
    {
        var $trNode = $(this).parents('tr').first();
        var $modalNode = $('#confirmModal');
        $modalNode.find('div.modal-body p').html('确定该笔提现申请已经付款成功了吗？');
        $modalNode.attr('editDatas',$trNode.attr('dataid')).modal('show');
    });

    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $modalNode = $(this).parents('div.modal').first();
        $.ajax({
            url : '${pageContext.request.contextPath}/user/tx/success',
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
    //点击用户昵称(进入用户详细)
    $(document).on('click','.detail_user_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/user/detail?id=' + datas['userId']);
    });
    //点击导出
    $(document).on('click','#export',function()
    {
        var data = $('.operate').getConditionValue();
        data.payType = 1;
        var params = '';
        for(var key in data)
        {
            params += "&" + key + "=" + data[key];
        }
        window.location.href = '${pageContext.request.contextPath}/user/tx/export?' + params.substring(1);
    });
    $('#beginTime').val('${params.minCreateTime}');
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
    data.url = '${pageContext.request.contextPath}/user/tx/get';
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
                html += '<td>';
                html += '<div class="btn-group clearfix">';
                if(n.status != 3){
                    html += '<a opauthority="btn_user_user_edit" href="javascript:;" class="label label-info mright10 award_confirm">提现成功</a>';
                }
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="userId"><a dtauthority="menu_user_user" href="javascript:;" class="detail_user_cls">' + n.userId + '</a></td>';
                html += '<td scolumn="nickName"><a dtauthority="menu_user_user" href="javascript:;" class="detail_user_cls">' + n.nickName + '</a></td>';
                html += '<td scolumn="mobile"><a dtauthority="menu_user_user" href="javascript:;" class="detail_user_cls">' + n.mobile + '</a></td>';
                html += '<td scolumn="realName"><a dtauthority="menu_user_user" href="javascript:;" class="detail_user_cls">' + n.realName + '</a></td>';
                html += '<td scolumn="payId">' + n.payId + '</td>';
                html += '<td scolumn="money">' + (n.money).toFixed(2) + '</td>';
                html += '<td scolumn="status"><span class="label ' + statusColorJsons[n.status] + '"> ' + sdescJsons[n.status] + '</span></td>';
                if(n.bankInfo != '')
                {
                    var bankInfo = $.parseJSON(n.bankInfo);
                    var bankStr = bankInfo['bankName'];
                    bankStr += bankInfo['subBankName'] != ''? ('-' + bankInfo['subBankName']) : '';
                    bankStr += " | ";
                    bankStr += bankInfo['bankProvince'] + " - " + bankInfo['bankCity'];
                    html += '<td scolumn="bankInfo">' + bankStr + '</td>';
                }
                else
                {
                    html += '<td scolumn="bankInfo"></td>';
                }
                html += '<td scolumn="channelDesc">' + n.channelDesc + '</td>';
                html += '<td scolumn="channelPayId">' + n.channelPayId + '</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '<td scolumn="doneTime">' + n.doneTime + '</td>';
                html += '<td scolumn="clientFrom">' + clientFromJson[n.clientFrom] + '</td>';
                html += '<td scolumn="remark">' + n.remark + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
        $('em[scolumn="tmoney"]').html(data.datas.tmoney || '--');
    });
}
</script>
</html>