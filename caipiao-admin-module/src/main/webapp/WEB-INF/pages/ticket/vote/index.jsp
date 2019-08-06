<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_chupiao");
    request.setAttribute("sidebar_mcode","menu_chupiao_vote");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>出票商-列表/出票商查询</title>
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
        <div class="clearfix status-options operate" callback="querydatas();">
            <div class="clearfix">
                <div class="dropdown pull-left" style="margin-right: 10px; width: 150px">
                    <input type="text" class="form-control" placeholder="出票商编号" name="voteId">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_chupiao_vote_add" type="button" class="btn btn-success" id="addTicketVote" style="margin-right:20px;">新增出票商</button>
                    <button opauthority="menu_chupiao_rule" type="button" class="btn btn-success" id="addTicketRule" style="margin-right:20px;">配置分票规则</button>
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
                        <th scolumn="voteId">出票商编号</th>
                        <th scolumn="voteName">出票商名称</th>
                        <th scolumn="status">状态</th>
                        <th scolumn="apiUrl">接口地址</th>
                        <th scolumn="key">秘钥</th>
                        <th scolumn="desc">描述</th>
                    </tr>
                    </thead>
                    <tbody id="dataTbody"></tbody>
                </table>
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
                            <li scolumn="voteId">出票商编号</li>
                            <li scolumn="voteName">出票商名称</li>
                            <li scolumn="status">状态</li>
                            <li scolumn="apiUrl">接口地址</li>
                            <li scolumn="key">秘钥</li>
                            <li scolumn="desc">描述</li>
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
var voteStatusJson = {'0':'关闭','1':'启用'};
$(function ()
{
    //点击删除
    $(document).on('click','.delete_vote',function()
    {
        var $trNode = $(this).parents('tr').first();
        var $modalNode = $('#confirmModal');
        $modalNode.find('div.modal-body p').html('确定删除出票商 ' + $trNode.attr('editName') + ' 吗？');
        $modalNode.attr('optype',1).attr('editDatas',$trNode.attr('editid')).attr('vid',$trNode.attr('voteId')).modal('show');
    });

    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $modalNode = $(this).parents('div.modal').first();
        $.ajax({
            url : '${pageContext.request.contextPath}/ticket/vote/delete',
            type : 'post',
            dataType : 'json',
            data : {id:$modalNode.attr('editDatas'),voteId:$modalNode.attr('voteId')},
            success : function (json)
            {
                showoplayer(json);
                querydatas();
            }
        });
    });

    //点击新增出票商
    $('#addTicketVote').on('click',function()
    {
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/ticket/vote/initAdd');
        $('#operatorModal').modal('show');
    });

    //点击配置分票规则
    $('#addTicketRule').on('click',function()
    {
        window.location = "${pageContext.request.contextPath}/ticket/rule/index";
    });

    //点击编辑
    $(document).on('click','.edit_vote',function()
    {
        var $trnode = $(this).parents('tr').first();
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/ticket/vote/detail?id=' + $trnode.attr('editid'));
        $('#operatorModal').modal('show');
    });

    //监听模态窗关闭
    $(document).on("hidden.bs.modal","#operatorModal",function()
    {
        querydatas(1);
    });
    querydatas(1);
});
//获取列表数据
var querydatas = function ()
{
    var data = $('.operate').getConditionValue();
    data.url = '${pageContext.request.contextPath}/ticket/vote/list';
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
                html += '<tr editid="' + n.id + '" editName="'+ n.voteName + '" voteId="'+ n.voteId + '">';
                html += '<td>';
                html += '<div class="btn-group clearfix">';
                html += '<a opauthority="btn_chupiao_vote_edit" href="javascript:;" class="label label-info mright10 edit_vote">编辑</a>';
                html += '<a opauthority="btn_chupiao_vote_delete" href="javascript:;" class="label label-info mright10 delete_vote">删除</a>';
                html += '</div>';
                html += '</td>';
                html += '<td scolumn="voteId"><a dtauthority="btn_chupiao_vote_edit" href="javascript:;" class="edit_vote">' + n.voteId + '</a></td>';
                html += '<td scolumn="voteName">' + n.voteName + '</td>';
                html += '<td scolumn="status">' + (voteStatusJson[n.status] || '未知') + '</td>';
                html += '<td scolumn="apiUrl">' + n.apiUrl + '</td>';
                html += '<td scolumn="key">' + n.key + '</td>';
                html += '<td scolumn="desc">' + n.desc + '</td>';
                html += '</tr>';
            });
            $('#dataTbody').html(html);
        }
    });
}
</script>
</html>