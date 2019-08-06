<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_chupiao");
    request.setAttribute("sidebar_mcode","menu_chupiao_query");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>回退重新计奖</title>
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
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 110px">
                    <select class="form-control" name="lotteryId" id="lotterySelect" title="彩种" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; width: 500px">
                    <input type="text" class="form-control" placeholder="竞彩场次编号[如足球:20190404003]或数字彩期次号[如大乐透:2019038]" name="matchCode" id="matchCode">
                </div>
                <button class="btn btn-info do-condition" id="reback">执行回退</button>
                <button class="btn btn-link clear-condition">清除</button>
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
var playNameJson = new Object();
var ticketStatusJson = new Object();
var LotteryJson = new Object();
$(function ()
{
    //彩种名称下拉
    $('#lotterySelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
        id: 'id',
        name: 'shortName',
        callback : function(json)
        {
            $.each(json, function(i,m)
            {
                LotteryJson[$(m).attr('id')] = $(m).attr('shortName');
            });
        }
    });

    //点击执行回退
    $(document).on('click','#reback',function()
    {
        var data = $('.operate').getConditionValue();
        if(data.lotteryId == null) {
            showoplayer({dcode:-1000,dmsg:'请先选择一个彩种!'});
            return;
        }
        if(data.matchCode == null) {
            showoplayer({dcode:-1000,dmsg:'请填写彩种对应的 竞彩场次编号或数字彩期次号!'});
            return;
        }
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('<font color="red">重要提示：请先后台手动操作用户错误派奖金额,再执行审核回退</font><br/>您确定已经按照正确步骤处理完成,并继续执行审核回退吗？');
        $confirmModal.attr('opdatas',JSON.stringify(data)).modal('show');
    });

    //点击确认操作
    $(document).on('click','#confirmModalSureBtn',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        //根据操作类型获取url
        var url = '${pageContext.request.contextPath}/weihu/erroraward/reaward';
        //发送请求
        if($.trim(url) != '')
        {
            $.ajax({
                url : url,
                type : 'post',
                data : {'datas': $confirmModal.attr('opdatas')},
                dataType : 'json',
                success : function(json)
                {
                    showoplayer(json);
                }
            });
        }
    });

});
</script>
</html>