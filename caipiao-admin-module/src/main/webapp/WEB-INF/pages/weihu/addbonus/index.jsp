<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_activity");
    request.setAttribute("sidebar_mcode","menu_activity_addbonus");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>加奖活动-列表/加奖活动查询</title>
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
        <div class="clearfix status-options operate" callback="querydatas();">
            <div class="clearfix">
                <button class="btn btn-info do-condition">重新加载</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_activity_addbonus_add" type="button" class="btn btn-success" id="joinAddbonus" style="margin-right:20px;">领取活动资格</button>
                    <button opauthority="btn_activity_addbonus_add" type="button" class="btn btn-success" id="addAddbonus" style="margin-right:20px;">添加加奖活动</button>
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
                        <th scolumn="activityName">活动名称</th>
                        <th scolumn="maxMoney">活动额度</th>
                        <th scolumn="balance">已使用额度</th>
                        <th scolumn="outAccountUserId">出款账户编号</th>
                        <th scolumn="userDayLimit">用户单日额度</th>
                        <th scolumn="lotteryLimit">彩种限制</th>
                        <th scolumn="leagueNameLimit">赛事限制</th>
                        <th scolumn="matchCode">场次号限制</th>
                        <th scolumn="passType">串关方式</th>
                        <th scolumn="schemeMoneyLimit">方案金额限制</th>
                        <th scolumn="isWithDraw">是否提现</th>
                        <th scolumn="addBonusRate">加奖比例</th>
                        <th scolumn="status">状态</th>
                        <th scolumn="weekLimit">星期限制</th>
                        <th scolumn="beginTime">生效时间</th>
                        <th scolumn="endTime">结束时间</th>
                        <th scolumn="createTime">创建时间</th>
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
                            <li scolumn="activityName">活动名称</li>
                            <li scolumn="maxMoney">活动额度</li>
                            <li scolumn="balance">已使用额度</li>
                            <li scolumn="outAccountUserId">出款账户编号</li>
                            <li scolumn="userDayLimit">用户单日额度</li>
                            <li scolumn="lotteryLimit">彩种限制</li>
                            <li scolumn="leagueNameLimit">赛事限制</li>
                            <li scolumn="matchCode">场次号限制</li>
                            <li scolumn="passType">串关方式</li>
                            <li scolumn="schemeMoneyLimit">方案金额限制</li>
                            <li scolumn="isWithDraw">是否提现</li>
                            <li scolumn="addBonusRate">加奖比例</li>
                            <li scolumn="status">状态</li>
                            <li scolumn="weekLimit">星期限制</li>
                            <li scolumn="beginTime">生效时间</li>
                            <li scolumn="endTime">结束时间</li>
                            <li scolumn="createTime">创建时间</li>
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
<div class="modal fade" id="dt_joinModal" useStaticDialog="1" style="z-index:1055;">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title">您正在进行活动资格领取操作</h4>
            </div>
            <div class="modal-body clearfix">
                <div class="form-group">
                    <label class="col-sm-3 control-label">活动编号</label>
                    <div class="col-sm-7" id="activityId">
                    </div>
                </div><br/>
                <div class="form-group">
                    <label class="col-sm-3 control-label">用户编号</label>
                    <div class="col-sm-7" id="userId">
                        <input type="text" class="form-control" placeholder="请输入用户编号" name="sgid" notEmpty="" id="sgid"/>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-save alertwarn btn_modal_cancel" data-dismiss="modal" id="confirmModalSureBtnActivity">确定</button>
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
    //点击删除
    $(document).on('click','.delete_addbonus',function()
    {
        var $trNode = $(this).parents('tr').first();
        var $modalNode = $('#confirmModal');
        $modalNode.find('div.modal-body p').html('确定删除该加奖活动吗？');
        $modalNode.attr('optype',1).attr('editDatas',$trNode.attr('editid')).modal('show');
    });

    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $modalNode = $(this).parents('div.modal').first();
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/addbonus/delete',
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

    //添加新渠道
    $('#addAddbonus').on('click',function()
    {
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/addbonus/initAdd');
        $('#operatorModal').modal('show');
    });

    //点击编辑
    $(document).on('click','.edit_addbonus',function()
    {
        var $trnode = $(this).parents('tr').first();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/addbonus/detail?id=' + $trnode.attr('editid'));
        $('#operatorModal').modal('show');
    });

    //点击参与人数
    $(document).on('click','.query_joinuser',function()
    {
        var $trnode = $(this).parents('tr').first();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/weihu/addbonus/getuser?activityId=' + $trnode.attr('editid'));
        $('#operatorModal').modal('show');
    });

    //用户头衔设置
    $('#joinAddbonus').on('click',function(){
        //发送请求
        $.ajax({
            url: '${pageContext.request.contextPath}/weihu/addbonus/list',
            type:'post',
            dataType:'json',
            success : function(data)
            {
                var html = '<select class="form-control" name="aid" id="aid" data-live-search="true" data-size="8" data-selected-text-format="count > 3">';
                $.each(data.datas.list, function(i,m)
                {
                    html += '<option value="'+m.id+'">' + m.activityName + '</option>';
                });
                html += '</select>';
                $('#activityId').html(html);
                $('#dt_joinModal').modal('show');
            }
        });
    });

    //点击确定
    $('#confirmModalSureBtnActivity').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/addbonus/binding',
            type : 'post',
            dataType : 'json',
            data : {activityId:$("#aid").val(),userId:$("#sgid").val()},
            success : function(json)
            {
                showoplayer(json);
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
    data.url = '${pageContext.request.contextPath}/weihu/addbonus/list';
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
                html += '<tr editid="' + n.id + '">';
                html += '<td>';
                html += '<div class="btn-group clearfix">';
                html += '<a opauthority="btn_activity_addbonus_ckcyry" href="javascript:;" class="label label-info mright10 query_joinuser">参与用户</a>';
                html += '<a opauthority="btn_activity_addbonus_edit" href="javascript:;" class="label label-info mright10 edit_addbonus">编辑</a>';
                html += '<a opauthority="btn_activity_addbonus_delete" href="javascript:;" class="label label-danger mright10 delete_addbonus">删除</a>';
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="activityName">' + n.activityName + '</td>';
                html += '<td scolumn="maxMoney">' + n.maxMoney + '</td>';
                html += '<td scolumn="balance">' + n.balance + '</td>';
                html += '<td scolumn="outAccountUserId">' + n.outAccountUserId + '</td>';
                html += '<td scolumn="userDayLimit">' + n.userDayLimit + '</td>';
                html += '<td scolumn="lotteryLimit">' + n.lotteryLimit + '</td>';
                html += '<td scolumn="leagueNameLimit">' + n.leagueNameLimit + '</td>';
                html += '<td scolumn="matchCode">' + n.matchCode + '</td>';
                html += '<td scolumn="passType">' + n.passType + '</td>';
                html += '<td scolumn="schemeMoneyLimit">' + n.schemeMoneyLimit + '</td>';
                html += '<td scolumn="isWithDraw">' + (n.isWithDraw?'可提现':'不可提现') + '</td>';
                html += '<td scolumn="addBonusRate">' + n.addBonusRate + '</td>';
                html += '<td scolumn="status">' + (n.status==0?'停用':'启用') + '</td>';
                html += '<td scolumn="weekLimit">' + n.weekLimit + '</td>';
                html += '<td scolumn="beginTime">' + n.beginTime + '</td>';
                html += '<td scolumn="endTime">' + n.endTime + '</td>';
                html += '<td scolumn="createTime">' + n.createTime + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>