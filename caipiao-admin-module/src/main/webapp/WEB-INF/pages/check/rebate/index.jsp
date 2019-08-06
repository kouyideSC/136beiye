<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_report");
    request.setAttribute("sidebar_mcode","menu_report_checkrebate");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>报表查询-列表/用户返利查询</title>
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
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 170px">
                    <input type="text" class="form-control datepicker" placeholder="日期-开始" name="beginTime">
                </div>
                <span class="pull-left" style="line-height: 2; margin-right: 5px;margin-left: -5px;">-</span>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 170px">
                    <input type="text" class="form-control datepicker" placeholder="日期-结束" name="endTime">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button type="button" class="btn btn-success" id="export" style="margin-right:20px;">导出</button>
                </div>
            </div>
        </div>
        <div class="core clearfix">
            <div class="core-table pull-left" style="position: relative; width:100%">
                <a href="javascript:;" class="edit-item" data-toggle="modal" data-target="#edit-item" style=" z-index: 500;"><i class="plus-icon p-setting"></i></a>
                <table id="dataTables" style="min-width:1200px;" border="0" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th scolumn="dateStr">日期</th>
                        <th scolumn="difference">差异额</th>
                        <th scolumn="beginRebate">返利账户初余额</th>
                        <th scolumn="endRebate">返利账户末余额</th>
                        <th scolumn="getRebate">用户获得返利金额</th>
                        <th scolumn="outRebate">提取返利金额</th>
                        <th scolumn="calErrorNumber">用户返利金额计算错误笔数</th>
                        <th scolumn="createTime">生成时间</th>
                        <th scolumn="updateTime">变更时间</th>
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
                            <li scolumn="dateStr">日期</li>
                            <li scolumn="difference">差异额</li>
                            <li scolumn="beginRebate">返利账户初余额</li>
                            <li scolumn="endRebate">返利账户末余额</li>
                            <li scolumn="getRebate">用户获得返利金额</li>
                            <li scolumn="outRebate">提取返利金额</li>
                            <li scolumn="calErrorNumber">用户返利金额计算错误笔数</li>
                            <li scolumn="createTime">生成时间</li>
                            <li scolumn="updateTime">变更时间</li>
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
$(function ()
{
    //点击导出
    $(document).on('click','#export',function()
    {
        var data = $('.operate').getConditionValue();
        var params = '';
        for(var key in data)
        {
            params += "&" + key + "=" + data[key];
        }
        window.location.href = '${pageContext.request.contextPath}/check/rebate/export?' + params.substring(1);
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
    data.url = '${pageContext.request.contextPath}/check/rebate/list';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 5;
    pageAjax(data, function ()
    {
        $(data.select).parents('.fixtable_tableLayout:first').find('.multi_checkbox_allcls').prop("checked",false);
        var html = "";
        if(data.datas.list && data.datas.list.length > 0)
        {
            $.each(data.datas.list, function (i, n)
            {
                html += '<tr editid="' + n.dataStr + '">';
                html += '<td scolumn="dateStr">' + n.dateStr + '</td>';
                html += '<td scolumn="difference">' + n.difference + '元</td>';
                html += '<td scolumn="beginRebate">' + n.beginRebate + '元</td>';
                html += '<td scolumn="endRebate">' + n.endRebate + '元</td>';
                html += '<td scolumn="getRebate">' + n.getRebate + '元</td>';
                html += '<td scolumn="outRebate">' + n.outRebate + '元</td>';
                html += '<td scolumn="calErrorNumber">' + n.calErrorNumber + '笔</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '<td scolumn="updateTime">' + n.updateTime + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>