<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_weihu");
    request.setAttribute("sidebar_mcode","menu_weihu_txqd");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>维护-提现渠道维护</title>
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
                <div class="dropdown pull-left" style="margin-right: 10px; width: 100px">
                    <select class="form-control selectpicker" title="启用状态" name="status">
                        <option value="0">已停用</option>
                        <option value="1">使用中</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 180px;">
                    <input type="text" class="form-control" placeholder="渠道名称" title="渠道名称" name="name">
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
                        <th scolumn="name">渠道名称</th>
                        <th scolumn="channelDesc">渠道描述</th>
                        <th scolumn="status">启用状态</th>
                        <th scolumn="model">启用模式</th>
                        <th scolumn="timeRange">时间段</th>
                        <th scolumn="timeCharacter">时间特征</th>
                        <th scolumn="rate">使用权重</th>
                        <th scolumn="minMoney">单笔最小金额</th>
                        <th scolumn="maxMoney">单笔最大金额</th>
                        <th scolumn="updateTime">更新时间</th>
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
                            <li scolumn="name">渠道名称</li>
                            <li scolumn="channelDesc">渠道描述</li>
                            <li scolumn="status">启用状态</li>
                            <li scolumn="model">启用模式</li>
                            <li scolumn="timeRange">时间段</li>
                            <li scolumn="timeCharacter">时间特征</li>
                            <li scolumn="rate">使用权重</li>
                            <li scolumn="minMoney">单笔最小金额</li>
                            <li scolumn="maxMoney">单笔最大金额</li>
                            <li scolumn="updateTime">更新时间</li>
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
var statusColorJsons = {'0':'label-failed','1':'label-success'};//渠道启用状态颜色样式
$(function ()
{
    //点击启用/停用
    $(document).on('click','.status_txqd_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $modalNode = $('#confirmModal');
        $modalNode.find('div.modal-body p').html('确定' + (datas['status'] == 0? '启用' : '停用') + '提现渠道-' + datas['name'] + '吗？');
        $modalNode.attr('optype',1).attr('opdatas',JSON.stringify({id:datas['id'],status:(datas['status'] == 0? 1 : 0)})).modal('show');
    });
    //点击设置规则
    $(document).on('click','.rule_txqd_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $modalNode = $('#operatorModal');
        $modalNode.fillWithUrl('${pageContext.request.contextPath}/weihu/txqd/initRule?id=' + datas['id']);
        $modalNode.modal('show');
    });
    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        var optype = $confirmModal.attr('optype');//获取操作类型 1-启用/停用提现渠道
        var opdatas = JSON.parse($confirmModal.attr('opdatas'));

        //根据操作类型获取url
        var url = '';
        if(optype == 1)
        {
            url = '${pageContext.request.contextPath}/weihu/txqd/editStatus';//修改提现渠道启用状态
        }
        //发送请求
        if($.trim(url) != '')
        {
            $.ajax({
                url: url,
                type: 'post',
                data: opdatas,
                dataType: 'json',
                success: function (json)
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
        querydatas();
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
    data.url = '${pageContext.request.contextPath}/weihu/txqd/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 2;
    pageAjax(data, function ()
    {
        var html = "";
        $.each(data.datas.list, function (i, n)
        {
            tbdatas[n.id] = n;
            html += '<tr dataid="' + n.id + '">';
            html += '<td>';
            html += '<div class="btn-group clearfix">';
            html += '<a opauthority="btn_weihu_txqd_edit" href="javascript:;" class="label label-info mright10 ' + (n.status == 1? 'label-danger' : '') + ' status_txqd_cls">' + (n.status == 0? '启用' : '停用') + '</a>';
            html += '<a opauthority="btn_weihu_txqd_edit" href="javascript:;" class="label label-warning mright10 rule_txqd_cls">设置规则</a>';
            html += '</div>';
            html += '</td>';
            html += '<td scolumn="name">' + n.name + '</td>';
            html += '<td scolumn="channelDesc">' + n.channelDesc + '</td>';
            html += '<td scolumn="status"><span class="label ' + statusColorJsons[n.status] + '">' + (n.status == 0? '已停用' : '使用中') + '</span></td>';
            html += '<td scolumn="model">' + (n.model == 0? '默认模式' : (n.model == 1? '时间段' : '时间特征')) + '</td>';
            html += '<td scolumn="timeRange">' + ((n.timeRangeStart != ''? (n.timeRangeStart + ' ~ ') : '--') + (n.timeRangeEnd != ''? n.timeRangeEnd : '--')) + '</td>';
            html += '<td scolumn="timeCharacter">' + n.timeCharacter + '</td>';
            html += '<td scolumn="rate">' + n.rate + '</td>';
            html += '<td scolumn="minMoney">' + (n.minMoney || '--') + '</td>';
            html += '<td scolumn="maxMoney">' + (n.maxMoney || '--') + '</td>';
            html += '<td scolumn="updateTime">' + n.updateTime + '</td>';
        });
        $('#dataTbody').html(html);
    });
};
</script>
</html>