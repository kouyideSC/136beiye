<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_activity");
    request.setAttribute("sidebar_mcode","menu_activity_coupon");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>活动管理-优惠券</title>
    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta name="author" content="">
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
                <div class="dropdown pull-left" style="margin-right: 10px;width: 150px;">
                    <input type="text" class="form-control" placeholder="按优惠券名称" title="优惠券名称" name="name">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 130px;">
                    <select class="form-control selectpicker" title="优惠券使用类型" name="useType">
                        <option value="0">直减券</option>
                        <option value="1">满减券</option>
                    </select>
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 150px;">
                    <input type="text" class="form-control datepicker" title="优惠券最早发行时间" placeholder="最早发行时间" name="minCreateTime">
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 150px">
                    <input type="text" class="form-control datepicker" title="优惠券最晚发行时间" placeholder="最晚发行时间" name="maxCreateTime">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_activity_coupon_add" type="button" class="btn btn-success" id="addCouponBtn" style="margin-right:20px;">新增优惠券</button>
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
                        <th scolumn="name">名称</th>
                        <th scolumn="money">面额</th>
                        <th scolumn="useType">使用类型</th>
                        <th scolumn="limitMoney">金额限制</th>
                        <th scolumn="lotteryName">适用彩种</th>
                        <th scolumn="status">状态</th>
                        <th scolumn="createTime">发行时间</th>
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
                            <li scolumn="name">名称</li>
                            <li scolumn="money">面额</li>
                            <li scolumn="useType">使用类型</li>
                            <li scolumn="limitMoney">金额限制</li>
                            <li scolumn="lotteryName">适用彩种</li>
                            <li scolumn="status">状态</li>
                            <li scolumn="createTime">发行时间</li>
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
var tbdatas = new Object();//表格数据对象
var useTypeJsons = {"0":"直减券","1":"满减券"};//优惠券使用类型名称映射
var statusJsons = {"0":"下架","1":"正常"};//优惠券状态映射
$(function ()
{
    //点击新增优惠券活动
    $(document).on('click','#addCouponBtn',function()
    {
        $('#operatorModal').attr('optype',1);
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/coupon/initAdd');
        $('#operatorModal').modal('show');
    });
    //点击编辑优惠券
    $(document).on('click','.edit_coupon_cls',function()
    {
        $('#operatorModal').removeAttr('optype');
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/coupon/initEdit?id=' + datas['id']);
        $('#operatorModal').modal('show');
    });
    //点击下架优惠券
    $(document).on('click','.xj_coupon_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('确定将优惠券' + datas['name'] + '下架吗？');
        $confirmModal.attr('optype',1);//设置操作类型
        $confirmModal.attr('opdatas',JSON.stringify({id:datas['id']}));
        $confirmModal.modal('show');
    });
    //点击确认操作
    $(document).on('click','#confirmModalSureBtn',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        var optype = $confirmModal.attr('optype');//获取操作类型 1-下架
        var opdatas = JSON.parse($confirmModal.attr('opdatas'));

        //根据操作类型获取url
        var url = '';
        if(optype == 1)
        {
            url = '${pageContext.request.contextPath}/weihu/coupon/xj';//下架
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
    //监听模态窗关闭
    $(document).on("hidden.bs.modal","#operatorModal",function()
    {
        if($(this).attr('optype') == 1)
        {
            querydatas(1);
        }
        else
        {
            querydatas();
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
    data.url = '${pageContext.request.contextPath}/weihu/coupon/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 2;
    pageAjax(data, function ()
    {
        var html = "";
        if(data.datas.list && data.datas.list.length > 0)
        {
            var lotteryId = $('#lotteryIdSelect').val();
            $.each(data.datas.list, function (i, n)
            {
                tbdatas[n.id] = n;
                html += '<tr dataid="' + n.id + '">';
                html += '<td>';
                html += '<a opauthority="btn_activity_coupon_edit" href="javascript:;" class="label label-info mright5 edit_coupon_cls">编辑</a>';
                if(n.status == 1)
                {
                    html += '<a opauthority="btn_activity_coupon_edit" href="javascript:;" class="label label-danger mright5 xj_coupon_cls">下架</a>';
                }
                html += '</td>';
                html += '<td scolumn="name">' + n.name + '</td>';
                html += '<td scolumn="money">' + n.money + '</td>';
                html += '<td scolumn="useType">' + useTypeJsons[n.useType] + '</td>';
                html += '<td scolumn="limitMoney">' + n.limitMoney + '</td>';
                html += '<td scolumn="lotteryName">' + n.lotteryName + '</td>';
                html += '<td scolumn="status">' + statusJsons[n.status] + '</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>