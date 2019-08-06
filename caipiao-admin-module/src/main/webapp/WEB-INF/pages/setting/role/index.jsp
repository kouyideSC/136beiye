<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_setting");
    request.setAttribute("sidebar_mcode","menu_setting_role");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>设置-角色管理</title>
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
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 150px">
                    <input type="text" class="form-control" placeholder="按角色名称查找" title="角色名称" name="name">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_setting_role_add" type="button" class="btn btn-success" id="addRoleBtn" style="margin-right:20px;">新增角色</button>
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
                        <th scolumn="name">角色名称</th>
                        <th scolumn="description">角色描述</th>
                        <th scolumn="createTime">创建时间</th>
                        <th scolumn="modifiedTime">修改时间</th>
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
                            <li scolumn="name">角色名称</li>
                            <li scolumn="description">角色描述</li>
                            <li scolumn="createTime">创建时间</li>
                            <li scolumn="modifiedTime">修改时间</li>
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
$(function ()
{
    //点击新增角色
    $(document).on('click','#addRoleBtn',function()
    {
        $('#operatorModal').attr('optype',1);
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/setting/role/initAdd');
        $('#operatorModal').modal('show');
    });
    //点击编辑角色
    $(document).on('click','.edit_role_cls',function()
    {
        $('#operatorModal').removeAttr('optype');
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/setting/role/initEdit?id=' + datas['id']);
        $('#operatorModal').modal('show');
    });
    //点击删除角色
    $(document).on('click','.delete_role_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.find('.modal-body p').html('确认删除角色 ' + datas['name'] + ' 吗？');
        var arrays = new Array();
        arrays.push({id:datas['id']});
        $confirmModal.attr('optype',1).attr('editdatas',JSON.stringify(arrays)).modal('show');
    });
    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        var datas = JSON.parse($confirmModal.attr('editdatas'));
        var optype = $confirmModal.attr('optype');
        var url = '';
        if(optype == 1)
        {
            url = '${pageContext.request.contextPath}/setting/role/delete';//删除角色
            datas = datas[0];
        }
        $.ajax({
            url : url,
            type : 'post',
            dataType : 'json',
            data : datas,
            success : function (json)
            {
                showoplayer(json);
                if(json.dcode == 1000)
                {
                    if(optype == 1)
                    {
                        querydatas(1);
                    }
                    else
                    {
                        querydatas();
                    }
                }
            }
        });
    });
    //监听模态窗关闭
    $(document).on("hidden.bs.modal","#operatorModal,#confirmModal",function()
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
    data.url = '${pageContext.request.contextPath}/setting/role/get';
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
            html += '<a opauthority="btn_setting_role_edit" href="javascript:;" class="label label-info mright5 edit_role_cls">编辑</a>';
            html += '<a opauthority="btn_setting_role_delete" href="javascript:;" class="label label-danger mright5 delete_role_cls">删除</a>';
            html += '</td>';
            html += '<td scolumn="name">' + n.name + '</td>';
            html += '<td scolumn="description">'+ n.description + '</td>';
            html += '<td scolumn="createTime">' + n.createTime + '</td>';
            html += '<td scolumn="modifiedTime">' + n.modifiedTime + '</td>';
            html += '</tr>';
        });
        $('#dataTbody').html(html);
    });
};
</script>
</html>