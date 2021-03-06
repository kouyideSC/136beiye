<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_order");
    request.setAttribute("sidebar_mcode","menu_order_scheme");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>用户管理-用户方案</title>
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
                <%--<div class="btn-group pull-right clearfix">
                    <button type="button" class="btn btn-success" id="exportSchemeBtn" style="margin-right:20px;">导出方案</button>
                </div>--%>
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
                    <input type="text" class="form-control datetimepicker" title="方案最早发起时间" placeholder="方案最早发起时间" name="minCreateTime" id="minCreateTime">
                </div>
                <div class="dropdown pull-left option-date" style="margin-right: 10px;width: 178px">
                    <input type="text" class="form-control datetimepicker" title="方案最晚发起时间" placeholder="方案最晚发起时间" name="maxCreateTime">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 133px;">
                    <input type="text" class="form-control" title="方案所属期次号" placeholder="期次号" name="period">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <select class="form-control selectpicker" title="用户类型" name="userType" id="userTypeSelect"></select>
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px;width: 110px;">
                    <select class="form-control selectpicker" title="出票商" name="ticketVoteId" id="ticketVoteSelect"></select>
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
                        <th scolumn="schemeOrderId" style="max-width: 180px;">方案编号</th>
                        <th scolumn="nickName" style="max-width: 120px;">用户昵称</th>
                        <th scolumn="lotteryName" style="max-width: 120px;">彩种</th>
                        <th scolumn="period">期次</th>
                        <th scolumn="schemePlayType">玩法</th>
                        <th scolumn="ggfs" style="max-width: 100px;">过关方式</th>
                        <th scolumn="schemeStatusDesc">状态</th>
                        <th scolumn="schemeMultiple">
                            倍数
                            <div class="sorter" sortfield="schemeMultiple">
                                <div class="sorter-up"></div>
                                <div class="sorter-down"></div>
                            </div>
                        </th>
                        <th scolumn="schemeMoney">
                            金额
                            <div class="sorter" sortfield="schemeMoney">
                                <div class="sorter-up"></div>
                                <div class="sorter-down"></div>
                            </div>
                        </th>
                        <th scolumn="schemeType">类型</th>
                        <th scolumn="jjpj">计/派奖</th>
                        <th scolumn="ticketVoteName">出票商</th>
                        <th scolumn="prizeTax">中奖奖金</th>
                        <th scolumn="theoryPrize">
                            理论奖金
                            <div class="sorter" sortfield="theoryPrize">
                                <div class="sorter-up"></div>
                                <div class="sorter-down"></div>
                            </div>
                        </th>
                        <th scolumn="createTime">下单时间</th>
                        <th scolumn="endTime">方案截止时间</th>
                        <th scolumn="clientSource">来源</th>
                    </tr>
                    </thead>
                    <tbody id="dataTbody"></tbody>
                </table>
                <div class="pull-right pagelist_cls" style="width: 100%;">
                    <div class="pull-left" style="height: 38px;line-height: 45px;margin-left: 10px;">
                        <strong>[总计]</strong>
                        <span>方案金额(元)：</span><em scolumn="tmoney">--</em>
                        <span style="padding-left: 10px;">方案实际支付金额(元)：</span><em scolumn="tpaymoney">--</em>
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
                            <li scolumn="schemeOrderId">方案编号</li>
                            <li scolumn="nickName">用户昵称</li>
                            <li scolumn="lotteryName">彩种</li>
                            <li scolumn="period">期次</li>
                            <li scolumn="schemePlayType">玩法</li>
                            <li scolumn="ggfs">过关方式</li>
                            <li scolumn="schemeStatusDesc">状态</li>
                            <li scolumn="schemeMultiple">倍数</li>
                            <li scolumn="schemeMoney">金额</li>
                            <li scolumn="schemeType">类型</li>
                            <li scolumn="jjpj">计/派奖</li>
                            <li scolumn="ticketVoteName">出票商</li>
                            <li scolumn="prizeTax">中奖奖金</li>
                            <li scolumn="theoryPrize">理论奖金</li>
                            <li scolumn="createTime">下单时间</li>
                            <li scolumn="endTime">方案截止时间</li>
                            <li scolumn="clientSource">来源</li>
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
<div class="modal fade" id="confirmInfoModal" useStaticDialog="1" style="z-index:1049;">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title">追号方案撤单</h4>
            </div>
            <div class="modal-body clearfix">
                <table class="col-sm-12 table table-bordered" id="zhinfoTables" border="0" cellspacing="0" cellpadding="0">
                    <tr style="background: #FAEBD7;">
                        <th>期次号</th>
                        <th>方案编号</th>
                        <th>方案倍数</th>
                        <th>方案金额</th>
                        <th>状态</th>
                        <th>操作</th>
                    </tr>
                    <tbody id="zhinfoTbody"></tbody>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            </div>
        </div>
    </div>
</div>
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
    //用户类型下拉
    $('#userTypeSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/user/getUserModuleDown?module=4',
        id: 'id',
        name: 'value',
        callback : function(data)
        {
            $('#userTypeSelect').selectpicker('val',0);//默认选中第一个
        }
    });
    //出票商下拉
    $('#ticketVoteSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/ticket/vote/list',
        id: 'voteId',
        name: 'voteName'
    });
    //点击撤单(追号方案)
    $(document).on('click','.cancel_scheme_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        if(datas['schemeType'] == 1)
        {
            //查询方案追号信息
            $.ajax({
                url : '${pageContext.request.contextPath}/user/scheme/zh/get',
                type : 'post',
                dataType : 'json',
                data : {schemeId:datas['id']},
                success : function(json)
                {
                    var html = '';
                    $.each(json.datas.list,function(i,n)
                    {
                        zhtbdatas[n.id] = n;
                        html += '<tr dataid="' + n.id + '">';
                        html += '<td>' + n.period + '</td>';
                        html += '<td>' + n.schemeOrderId + '</td>';
                        html += '<td>' + n.schemeMultiple + '</td>';
                        html += '<td>' + n.schemeMoney + '</td>';
                        html += '<td>' + n.schemeStatusDesc + '</td>';
                        //撤单的条件:订单状态为支付成功/出票中/出票失败/出票失败待撤单/截止未出票待撤单
                        html += '<td>';
                        if(n.schemeStatus == 1 || n.schemeStatus == 2
                                || n.schemeStatus == 4 || n.schemeStatus == 6 || n.schemeStatus == 7)
                        {
                            html += '<a href="javascript:;" class="label label-info mright5 cancel_schemezh_cls">撤单</a>';
                        }
                        else
                        {
                            html += '<span class="label label-primary">不允许撤单</span>';
                        }
                        html += '</td>';
                        html += '</tr>';
                    });
                    $('#zhinfoTbody').html(html);
                    $('#confirmInfoModal').modal('show');
                },
                error : function()
                {
                    showoplayer({dcode:-1000,dmsg:'查询追号信息出错'});
                }
            });
        }
        else
        {
            var $confirmModal = $('#confirmModal');
            $confirmModal.find('.modal-body p').first().html('确定对方案' + datas['schemeOrderId'] + '进行撤单吗？');
            $confirmModal.attr('optype',1);//设置操作类型
            $confirmModal.attr('opdatas',JSON.stringify({id:datas['id']}));
            $confirmModal.modal('show');
        }
    });
    //追号方案撤单
    $(document).on('click','.cancel_schemezh_cls',function()
    {
        var datas = zhtbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').first().html('确定对追号方案' + datas['schemeOrderId'] + '进行撤单吗？');
        $confirmModal.attr('optype',3);//设置操作类型
        $confirmModal.attr('opdatas',JSON.stringify({id:datas['id'],schemeId:datas['schemeId']}));
        $confirmModal.modal('show');
    });
    //点击审核大单
    $(document).on('click','.audit_bigorder_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('确定对方案' + datas['schemeOrderId'] + '进行大单审核通过吗？');
        $confirmModal.attr('optype',2);//设置操作类型
        $confirmModal.attr('opdatas',JSON.stringify({id:datas['id']}));
        $confirmModal.modal('show');
    });
    //点击确认操作
    $(document).on('click','#confirmModalSureBtn',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        var optype = $confirmModal.attr('optype');//获取操作类型 1-撤单 2-大单审核
        var opdatas = JSON.parse($confirmModal.attr('opdatas'));

        //根据操作类型获取url
        var url = '';
        if(optype == 1)
        {
            var arrays = new Array();
            arrays.push({id:opdatas['id']});
            opdatas = {'cdinfos':JSON.stringify(arrays)};
            url = '${pageContext.request.contextPath}/user/scheme/cancel';//撤单
        }
        else if(optype == 2)
        {
            url = '${pageContext.request.contextPath}/user/scheme/auditBigOrder';//大单审核
        }
        else if(optype == 3)
        {
            url = '${pageContext.request.contextPath}/user/scheme/zh/cancel';//追号方案撤单
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
                    if(optype == 1 && json.dcode == 1001)
                    {
                        opdatas.iscontinue = 1;
                        $confirmModal.attr('opdatas',JSON.stringify(opdatas));
                        $confirmModal.find('.modal-body p').first().html(json.dmsg + '<br/>是否继续进行撤单操作？');
                    }
                    else if(optype == 3)
                    {
                        //重新加载方案追号信息
                        $.ajax({
                            url : '${pageContext.request.contextPath}/user/scheme/zh/get',
                            type : 'post',
                            dataType : 'json',
                            data : {schemeId:opdatas['schemeId']},
                            success : function(json)
                            {
                                var html = '';
                                $.each(json.datas.list,function(i,n)
                                {
                                    zhtbdatas[n.id] = n;
                                    html += '<tr dataid="' + n.id + '">';
                                    html += '<td>' + n.period + '</td>';
                                    html += '<td>' + n.schemeOrderId + '</td>';
                                    html += '<td>' + n.schemeStatusDesc + '</td>';
                                    //撤单的条件:订单状态为支付成功、出票中、出票失败
                                    html += '<td>';
                                    if(n.schemeStatus == 1 || n.schemeStatus == 2 || n.schemeStatus == 4)
                                    {
                                        html += '<a href="javascript:;" class="label label-info mright5 cancel_schemezh_cls">撤单</a>';
                                    }
                                    else
                                    {
                                        html += '<span class="label label-primary">不允许撤单</span>';
                                    }
                                    html += '</td>';
                                    html += '</tr>';
                                });
                                $('#zhinfoTbody').html(html);
                            },
                            error : function()
                            {
                                showoplayer({dcode:-1000,dmsg:'查询追号信息出错'});
                            }
                        });
                    }
                    else
                    {
                        querydatas();
                    }
                }
            });
        }
    });
    //点击方案编号(进入方案详细)
    $(document).on('click','.detail_scheme_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/user/scheme/detail?id=' + datas['id']);
    });
    //点击用户昵称(进入用户详细)
    $(document).on('click','.detail_user_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#detailCard').addClass('card-wrap-show');
        $('#detailCard').fillWithUrl('${pageContext.request.contextPath}/user/detail?id=' + datas['userId']);
    });
    $('#minCreateTime').val('${params.minCreateTime}');
    querydatas(1);
});
//获取列表数据
var querydatas = function (pnum)
{
    var data = $('.operate').getConditionValue();
    data.url = '${pageContext.request.contextPath}/user/scheme/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 4;
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
                html += '<td>';
                html += '<div class="btn-group clearfix">';

                //撤单的条件:1.非追号订单且订单状态为支付成功/出票中/出票失败/出票失败待撤单/截止未出票待撤单 2.追号订单
                if(((n.schemeStatus == 1 || n.schemeStatus == 2 || n.schemeStatus == 4 || n.schemeStatus == 6 || n.schemeStatus == 7) && n.schemeType != 1)
                        || (n.schemeType == 1 && n.donePeriod != n.periodSum))
                {
                    html += '<a opauthority="btn_order_scheme_cd" class="label label-danger mright5 cancel_scheme_cls">撤单</a>';
                }
                else
                {
                    html += '<a opauthority="menu_order_scheme" class="label label-info mright5 detail_scheme_cls">详情</a>';
                }
                //审核大单的条件:大单且未审核
                if(n.bigOrderStatus == 2 && n.schemeStatus == 1)
                {
                    html += '<a opauthority="btn_order_scheme_shdd" class="label label-danger mright5 audit_bigorder_cls">审核大单</a>';
                }
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="schemeOrderId" style="max-width: 180px;"><a dtauthority="menu_order_scheme" href="javascript:;" class="detail_scheme_cls">' + n.schemeOrderId + '</a></td>';
                html += '<td scolumn="nickName" style="max-width: 120px;"><a dtauthority="menu_user_user" href="javascript:;" class="detail_user_cls">' + n.nickName + '</a></td>';
                html += '<td scolumn="lotteryName" style="max-width: 120px;">'+ n.lotteryName + '</td>';
                html += '<td scolumn="period">' + n.period + '</td>';
                html += '<td scolumn="schemePlayType">' + n.schemePlayType + '</td>';

                //过关方式
                if((n.schemeOrderId.indexOf('JC') > -1 || n.schemeOrderId.indexOf('YH') > -1) && n.lotteryId != 1980 && n.lotteryId != 1990)
                {
                    var ggfs = '';
                    var chuans = n.schemeContent.split('|')[2].split(',');
                    $.each(chuans, function(i,m)
                    {
                        ggfs += ',' + m.replace('1*1','单关').replace('*','串');
                    });
                    ggfs = ggfs.substring(1);
                    html += '<td scolumn="ggfs" style="max-width: 100px;"><div class="col-inner">' + ggfs + '</div></td>';
                }
                else
                {
                    html += '<td scolumn="ggfs">--</td>';
                }

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
                html += '<td scolumn="schemeMoney">' + (n.schemeMoney).toFixed(2) + '</td>';
                //方案类型
                html += '<td scolumn="schemeType">' + schemeTypeJsons[n.schemeType] + '</td>';
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
                html += '<td scolumn="ticketVoteName">' + n.ticketVoteName + '</td>';
                html += '<td scolumn="prizeTax">' + (n.prizeTax).toFixed(2) + '</td>';
                html += '<td scolumn="theoryPrize">' + (n.theoryPrize || '--') + '</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '<td scolumn="endTime">' + n.endTime + '</td>';
                html += '<td scolumn="clientSourceName">' + n.clientSourceName + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
        $('em[scolumn="tmoney"]').html(data.datas.tmoney || '--');
        $('em[scolumn="tpaymoney"]').html(data.datas.tpaymoney || '--');
    });
}
</script>
</html>