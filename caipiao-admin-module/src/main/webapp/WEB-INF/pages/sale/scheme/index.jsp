<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_xiaoshou");
    request.setAttribute("sidebar_mcode","menu_xiaoshou_userscheme");
%>
<html lang="en" class="app">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
<title>销售管理-用户方案</title>
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
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <select class="form-control" title="彩种" name="lotteryId" id="lotterySelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <select class="form-control selectpicker" title="方案类型" name="schemeType" id="schemeTypeSelect"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <select class="form-control selectpicker" title="方案状态" name="schemeStatus" id="schemeStatusSelect"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 180px;">
                    <input type="text" class="form-control" placeholder="方案编号" title="方案编号" name="schemeOrderId">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 165px;">
                    <input type="text" class="form-control" placeholder="编号/昵称/手机号" title="用户编号/用户昵称/用户手机号" name="userAttrs" value="${params.userId}">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link show-more">高级筛选</button>
                <button class="btn btn-link show-off" style="display: none;">收起</button>
                <button class="btn btn-link clear-condition" style="padding-left: 0;">清除</button>
            </div>
            <div class="clearfix advanced">
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px">
                    <select class="form-control selectpicker" title="客户端来源" name="clientSource" id="clientSourceSelect">
                        <option value="0">网站</option>
                        <option value="1">苹果</option>
                        <option value="2">安卓</option>
                        <option value="3">H5</option>
                        <option value="4">其它</option>
                    </select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <input type="text" class="form-control" title="最小方案金额" placeholder="最小方案金额" name="minSchemeMoney">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <input type="text" class="form-control" title="最大方案金额" placeholder="最大方案金额" name="maxSchemeMoney">
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 180px;">
                    <input type="text" class="form-control datetimepicker" title="方案最早发起时间" placeholder="方案最早发起时间" name="minCreateTime">
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 178px">
                    <input type="text" class="form-control datetimepicker" title="方案最晚发起时间" placeholder="方案最晚发起时间" name="maxCreateTime">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 133px;">
                    <input type="text" class="form-control" title="方案所属期次号" placeholder="期次号" name="period">
                </div>
            </div>
        </div>
        <div class="core clearfix">
            <div class="core-table pull-left" style="position: relative; width:100%">
                <a href="javascript:;" class="edit-item" data-toggle="modal" data-target="#edit-item" style=" z-index: 500;"><i class="plus-icon p-setting"></i></a>
                <table id="dataTables" style="" border="0" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th scolumn="schemeOrderId" style="max-width: 180px;">方案编号</th>
                        <th scolumn="nickName" style="max-width: 120px;">用户昵称</th>
                        <th scolumn="lotteryName" style="max-width: 120px;">彩种</th>
                        <th scolumn="period">期次</th>
                        <th scolumn="schemePlayType">玩法</th>
                        <th scolumn="schemeType">类型</th>
                        <th scolumn="schemeStatusDesc">状态</th>
                        <th scolumn="schemeMultiple">倍数</th>
                        <th scolumn="schemeMoney">金额</th>
                        <th scolumn="jjpj">计奖/派奖</th>
                        <th scolumn="createTime">下单时间</th>
                        <th scolumn="clientSource">来源</th>
                        <th scolumn="prizeTax">奖金</th>
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
                            <li scolumn="schemeOrderId">方案编号</li>
                            <li scolumn="nickName">用户昵称</li>
                            <li scolumn="lotteryName">彩种</li>
                            <li scolumn="period">期次</li>
                            <li scolumn="schemePlayType">玩法</li>
                            <li scolumn="schemeType">类型</li>
                            <li scolumn="schemeStatusDesc">状态</li>
                            <li scolumn="schemeMultiple">倍数</li>
                            <li scolumn="schemeMoney">金额</li>
                            <li scolumn="jjpj">计奖/派奖</li>
                            <li scolumn="createTime">下单时间</li>
                            <li scolumn="clientSource">来源</li>
                            <li scolumn="prizeTax">奖金</li>
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
<div class="card-wrap" id="detailCard"></div>
</body>
<script>
var schemeTypeJsons = new Object();
var schemeStatusColorJsons = {'-1':'label-failed','0':'label-danger','1':'label-success','2':'label-success','3':'label-success','4':'label-danger','5':'label-primary','6':'label-failed','7':'label-failed'};
var tbdatas = new Object();//方案表格数据对象
var zhtbdatas = new Object();//追号方案表格数据对象
$(function ()
{
    //彩种名称下拉
    $('#lotterySelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
        id: 'id',
        name: 'name'
    });
    //方案类型下拉
    $('#schemeTypeSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/user/scheme/getSchemeTypeCombo',
        id: 'value',
        name: 'desc',
        callback:function(json)
        {
            $.each(json,function(i,m)
            {
                schemeTypeJsons[$(m).attr('value')] = $(m).attr('desc');
            });
        }
    });
    //方案状态下拉
    $('#schemeStatusSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/user/scheme/getSchemeStatusCombo',
        id: 'value',
        name: 'desc',
        callback:function(json)
        {
            var html = '<option value="ddkj">等待开奖</option>';
            html += '<option value="wzj">未中奖</option>';
            html += '<option value="ypj">已派奖</option>';
            $('#schemeStatusSelect').append(html);
            $('#schemeStatusSelect').selectpicker("refresh");
        }
    });
    //客户端来源下拉
    $('#clientSourceSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/user/scheme/getClientSourceCombo',
        id: 'value',
        name: 'desc'
    });
    //点击方案编号(进入方案详细)
    $(document).on('click','.detail_scheme_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/sale/scheme/detail?id=' + datas['id']);
    });
    querydatas(1);
});
//获取列表数据
var querydatas = function (pnum)
{
    var data = $('.operate').getConditionValue();
    data.url = '${pageContext.request.contextPath}/sale/scheme/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 3;
    data.minSchemeStatus = 0;
    if(data.schemeStatus == 'ddkj')
    {
        data.openStatus = 0;
        delete data.schemeStatus;
    }
    else if(data.schemeStatus == 'wzj')
    {
        data.openStatus = 1;
        delete data.schemeStatus;
    }
    else if(data.schemeStatus == 'ypj')
    {
        data.prizeStatus = 2;
        delete data.schemeStatus;
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
                html += '<td scolumn="schemeOrderId" style="max-width: 180px;">' + n.schemeOrderId + '</td>';
                html += '<td scolumn="nickName" style="max-width: 120px;">' + n.nickName + '</td>';
                html += '<td scolumn="lotteryName" style="max-width: 120px;">'+ n.lotteryName + '</td>';
                html += '<td scolumn="period">' + n.period + '</td>';
                html += '<td scolumn="schemePlayType">' + n.schemePlayType + '</td>';
                html += '<td scolumn="schemeType">' + schemeTypeJsons[n.schemeType] + '</td>';

                //方案状态,如果是追号方案,则显示追号进度
                html += '<td scolumn="schemeStatusDesc">';
                if(n.schemeType == 1)
                {
                    html += '<span class="label ';
                    html += (n.donePeriod != n.periodSum)? ('label-danger">' + '追号中[' + n.donePeriod + '/' + n.periodSum + ']') : ('label-success">追号完成');
                    html == '</span>';
                }
                else
                {
                    html += '<span class="label ' + schemeStatusColorJsons[n.schemeStatus] + '">' + n.schemeStatusDesc + '</span>';
                }
                html += '</td>';
                html += '<td scolumn="schemeMultiple">' + n.schemeMultiple + '</td>';
                html += '<td scolumn="schemeMoney">' + n.schemeMoney + '</td>';

                //计奖/派奖
                html += '<td scolumn="jjpj">';
                if(n.schemeType == 1)
                {
                    html += '--';
                }
                else
                {
                    if(n.openStatus == 0)
                    {
                        html += n.drawNumber != ''? '未计奖' : (n.schemeStatus > 3 || n.schemeStatus < 1) ? '--' : '等待开奖';
                    }
                    else if(n.openStatus == 1)
                    {
                        html += '未中奖';
                    }
                    else if(n.openStatus == 2)
                    {
                        html += n.prizeStatus == 0? '未派奖' : (n.prizeStatus == 1? '派奖中' : (n.prizeStatus == 2? '已派奖' : ''));
                    }
                }
                html += '</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '<td scolumn="clientSourceName">' + n.clientSourceName + '</td>';
                html += '<td scolumn="prizeTax">' + n.prizeTax + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>