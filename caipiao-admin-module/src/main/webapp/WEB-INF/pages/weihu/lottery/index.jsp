<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_weihu");
    request.setAttribute("sidebar_mcode","menu_weihu_lottery");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>维护-彩种维护</title>
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
                    <select class="form-control" name="id" id="lotterySelect" title="彩种名称" data-live-search="true" data-size="8" data-selected-text-format="count > 3" multiple></select>
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
                        <th scolumn="id" style="max-width: 100px;">彩种ID</th>
                        <th scolumn="name" style="max-width: 250px;">
                            彩种名称
                            <div class="sorter" sortfield="name">
                                <div class="sorter-up"></div>
                                <div class="sorter-down"></div>
                            </div>
                        </th>
                        <th scolumn="shortName" style="max-width: 250px;">
                            彩种简称
                        </th>
                        <%--<th scolumn="prizeGrade" style="max-width:250px;">奖级信息</th>--%>
                        <th scolumn="appStatus">app销售状态</th>
                        <th scolumn="consoleStatus">后台打开状态</th>
                        <th scolumn="orderValue">排序号</th>
                        <th scolumn="message">彩种说明</th>
                        <th scolumn="backGround">说明是否显示背景</th>
                        <th scolumn="showInHome">是否显示在首页</th>
                        <th scolumn="maxSellMultiple">单方案最大倍数</th>
                        <th scolumn="minSellMultiple">单方案最小倍数</th>
                        <th scolumn="ggfsFlag">支持过关方式</th>
                        <th scolumn="maxZhNum">单方案最多追号期数</th>
                        <th scolumn="maxSellMoney">单方案最大金额</th>
                        <th scolumn="minSellMoney">单方案最小金额</th>
                        <th scolumn="updateTime">最后更新时间</th>
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
                            <li scolumn="shortName">彩种简称</li>
                            <li scolumn="appStatus">app销售状态</li>
                            <li scolumn="consoleStatus">后台打开状态</li>
                            <li scolumn="orderValue">排序号</li>
                            <li scolumn="message">彩种说明</li>
                            <li scolumn="backGround">说明是否显示背景</li>
                            <li scolumn="showInHome">是否显示在首页</li>
                            <li scolumn="maxSellMultiple">单方案最大倍数</li>
                            <li scolumn="minSellMultiple">单方案最小倍数</li>
                            <li scolumn="ggfsFlag">支持过关方式</li>
                            <li scolumn="maxZhNum">单方案最多追号期数</li>
                            <li scolumn="maxSellMoney">单方案最大金额</li>
                            <li scolumn="minSellMoney">单方案最小金额</li>
                            <li scolumn="updateTime">最后更新时间</li>
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
var optypeJsons = {1:'app',2:'网站',3:'H5',4:'后台'};
var operatorData;//操作数据对象
$(function ()
{
    //彩种名称下拉
    $('#lotterySelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo',
        id: 'id',
        name: 'shortName'
    });
    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/lottery/editLotterySaleStatus',
            type : 'post',
            dataType : 'json',
            data : operatorData,
            success : function(data)
            {
                showoplayer(data);
                querydatas();
            }
        })
    });
    //监听模态窗关闭
    $(document).on("hidden.bs.modal","#operatorModal",function()
    {
        querydatas();
    });
    querydatas(1);
});
//点击编辑彩种
function editLottery(id)
{
    $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/lottery/initEdit?id=' + id);
}
//点击彩种详细
function showLotteryDetail(id)
{
    $('#detailCard').addClass('card-wrap-show');
    $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/weihu/lottery/initDetail?id=' + id);
}
//app/网站/h5/后台 停/开售操作(optype,操作类型,1/2/3/4分别表示app/网站/h5/后台)
function editLotterySalesStatus(id,lotteryName,currentStatus,optype)
{
    $('#confirmModal').find('.modal-body p').first().html('确认' + (currentStatus == 0? '开售' : '停售') + lotteryName + '（' + optypeJsons[optype] + '）吗？');
    $('#confirmModal').modal('show');
    operatorData = new Object();
    operatorData.id = id;
    if(optype == 1)
    {
        operatorData.appStatus = currentStatus == 0? 1 : 0;
    }
    else if(optype == 2)
    {
        operatorData.webStatus = currentStatus == 0? 1 : 0;
    }
    else if(optype == 3)
    {
        operatorData.h5Status = currentStatus == 0? 1 : 0;
    }
    else if(optype == 4)
    {
        operatorData.consoleStatus = currentStatus == 0? 1 : 0;
    }
}
//获取列表数据
var querydatas = function (pnum)
{
    if (typeof pnum == 'undefined')
    {
        pnum = $("#pagelist li[page][class*='active']").attr("page");
    }
    var data = $('.operate').getConditionValue();
    data.url = '${pageContext.request.contextPath}/weihu/lottery/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 3;
    pageAjax(data, function ()
    {
        var html = "";
        $.each(data.datas.list, function (i, n)
        {
            html += '<tr>';
            html += '<td>';
            html += '<div class="btn-group clearfix">';
            html += '<a opauthority="btn_weihu_lottery_edit" href="javascript:;" class="label label-info mright10" onclick="editLottery(\'' + n.id + '\')" data-toggle="modal" data-target="#operatorModal">编辑</a>';
            html += '</div>';
            html += '</td>';
            html += '<td scolumn="id">' + n.id + '</td>';
            html += '<td scolumn="name">' + n.name + '</td>';
            html += '<td scolumn="shortName">'+ (n.shortName || '--') + '</td>';
            //html += '<td scolumn="prizeGrade" style="max-width:250px;"><div class="col-inner">' + (n.prizeGrade || '') + '</div></td>';
            html += '<td scolumn="appStatus"><a dtauthority="btn_weihu_lottery_edit" class="label ' + sellStatusColorJson[n.appStatus] + '" href="javascript:;" onclick=\"editLotterySalesStatus(\'' + n.id + '\',\'' + n.name + '\',' + n.appStatus + ',1)\">' + (n.appStatus == 0? '已停售' : '销售中') + '</a></td>';
            html += '<td scolumn="consoleStatus"><a dtauthority="btn_weihu_lottery_edit" class="label ' + sellStatusColorJson[n.consoleStatus] + '" href="javascript:;" onclick=\"editLotterySalesStatus(\'' + n.id + '\',\'' + n.name + '\',' + n.consoleStatus + ',4)\">' + (n.consoleStatus == 0? '已关闭' : '已开启') + '</a></td>';
            html += '<td scolumn="orderValue">'+ n.orderValue + '</td>';
            html += '<td scolumn="message">'+ (n.message  || '--')+ '</td>';
            html += '<td scolumn="backGround">'+ (n.backGround == 0? '不显示' : '显示') + '</td>';
            html += '<td scolumn="showInHome">'+ (n.showInHome == 0? '不显示' : '显示') + '</td>';
            html += '<td scolumn="maxSellMultiple">'+ (n.xzMaxSellMultiple == 0? '不限' : (n.maxSellMultiple  || '--'))+ '</td>';
            html += '<td scolumn="minSellMultiple">'+ (n.xzMinSellMultiple == 0? '不限' : (n.minSellMultiple  || '--'))+ '</td>';
            html += '<td scolumn="ggfsFlag">'+ (n.ggfsFlag == 0? '单个过关方式' : '不限') + '</td>';
            html += '<td scolumn="maxZhNum">'+ (n.maxZhNum  || '--')+ '</td>';
            html += '<td scolumn="maxSellMoney">'+ (n.xzMaxSellMoney == 0? '不限' : (n.maxSellMoney  || '--'))+ '</td>';
            html += '<td scolumn="minSellMoney">'+ (n.xzMinSellMoney == 0? '不限' : (n.minSellMoney  || '--'))+ '</td>';
            html += '<td scolumn="updateTime">'+ (n.updateTime || '--') + '</td>';
            html += '</tr>';
        });
        $('#dataTbody').html(html);
    });
};
</script>
</html>