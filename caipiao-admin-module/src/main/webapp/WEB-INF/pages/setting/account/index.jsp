<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    request.setAttribute("sidebar_parent_mcode","menu_setting");
    request.setAttribute("sidebar_mcode","menu_setting_role");
%>
<html lang="en" class="app">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="EmulateIE9" >
    <title>设置-帐户管理</title>
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
                    <input type="text" class="form-control" placeholder="按帐户名称查找" title="帐户名称" name="accountName">
                </div>
                <div class="dropdown pull-left" style="margin-right: 10px; max-width: 150px">
                    <input type="text" class="form-control" placeholder="按真实姓名查找" title="真实姓名" name="personalName">
                </div>
                <button class="btn btn-info do-condition">筛选</button>
                <button class="btn btn-link clear-condition">清除</button>
                <div class="btn-group pull-right clearfix">
                    <button opauthority="btn_setting_account_add" type="button" class="btn btn-success" id="addAccountBtn" style="margin-right:20px;">新增帐户</button>
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
                        <th scolumn="accountName">帐户名称</th>
                        <th scolumn="personalName">真实姓名</th>
                        <th scolumn="mobile">手机号</th>
                        <th scolumn="organizationName">所在组织</th>
                        <th scolumn="jobName">所在岗位</th>
                        <th scolumn="roleNames">关联角色</th>
                        <th scolumn="workStatus">工作状态</th>
                        <th scolumn="isLock">帐户状态</th>
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
                            <li scolumn="accountName">帐户名称</li>
                            <li scolumn="personalName">真实姓名</li>
                            <li scolumn="mobile">手机号</li>
                            <li scolumn="organizationName">所在组织</li>
                            <li scolumn="jobName">所在岗位</li>
                            <li scolumn="roleNames">关联角色</li>
                            <li scolumn="workStatus">工作状态</li>
                            <li scolumn="isLock">帐户状态</li>
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
    //点击新增帐户
    $(document).on('click','#addAccountBtn',function()
    {
        $('#operatorModal').attr('uptype',1);
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/setting/account/initAdd');
        $('#operatorModal').modal('show');
    });
    //点击编辑帐户
    $(document).on('click','.edit_account_cls',function()
    {
        $('#operatorModal').removeAttr('uptype');
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        $('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/setting/account/initEdit?id=' + datas['id']);
        $('#operatorModal').modal('show');
    });
    //点击重置密码
    $(document).on('click','.reset_pwd_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.removeAttr('uptype');
        $confirmModal.find('.modal-body p').html('确定将帐户 ' + datas['accountName'] + ' 的密码重置吗？');
        var arrays = new Array();
        arrays.push({id:datas['id']});
        $confirmModal.attr('optype',1).attr('editdatas',JSON.stringify(arrays)).modal('show');
    });
    //点击锁定/解锁帐户
    $(document).on('click','.lock_account_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.removeAttr('uptype');
        $confirmModal.find('.modal-body p').html('确定将帐户 ' + datas['accountName'] + (datas['isLock'] == 0? ' 锁定' : ' 解锁') + '吗？');
        var arrays = new Array();
        arrays.push({id:datas['id'],isLock:(datas['isLock'] == 0? 1 : 0)});
        $confirmModal.attr('optype',2).attr('editdatas',JSON.stringify(arrays)).modal('show');
    });
    //点击离职/复职帐户
    $(document).on('click','.workstatus_account_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.removeAttr('uptype');
        $confirmModal.find('.modal-body p').html('确定将帐户 ' + datas['accountName'] + ' 的工作状态设为' + (datas['workStatus'] == 1? '离职' : '复职') + '吗？');
        var arrays = new Array();
        arrays.push({id:datas['id'],workStatus:(datas['workStatus'] == 1? 2 : 1)});
        $confirmModal.attr('optype',3).attr('editdatas',JSON.stringify(arrays)).modal('show');
    });
    //点击删除帐户
    $(document).on('click','.delete_account_cls',function()
    {
        var datas = tbdatas[$(this).parents('tr').first().attr('dataid')];
        var $confirmModal = $('#confirmModal');
        $confirmModal.attr('utype',1);
        $confirmModal.find('.modal-body p').html('确定删除帐户 ' + datas['accountName'] + ' 吗？');
        var arrays = new Array();
        arrays.push({id:datas['id']});
        $confirmModal.attr('optype',4).attr('editdatas',JSON.stringify(arrays)).modal('show');
    });
    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        var datas = JSON.parse($confirmModal.attr('editdatas'));
        var optype = $confirmModal.attr('optype');
        var uptype = $confirmModal.attr('uptype');
        var url = '';
        if(optype == 1)
        {
            url = '${pageContext.request.contextPath}/setting/account/resetpwd';//重置密码
            datas = datas[0];
        }
        if(optype == 2)
        {
            url = '${pageContext.request.contextPath}/setting/account/lock';//锁定/解锁帐户
            datas = datas[0];
        }
        if(optype == 3)
        {
            url = '${pageContext.request.contextPath}/setting/account/setworkstatus';//离职/复职帐户
            datas = datas[0];
        }
        else if(optype == 4)
        {
            url = '${pageContext.request.contextPath}/setting/account/delete';//删除帐户
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
                    if(uptype == 1)
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
        if($(this).attr('uptype') == 1)
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
    data.url = '${pageContext.request.contextPath}/setting/account/get';
    data.page = pnum;
    data.select = '#dataTables';
    data.fixcmber = 4;
    pageAjax(data, function ()
    {
        var html = "";
        $.each(data.datas.list, function (i, n)
        {
            tbdatas[n.id] = n;
            html += '<tr dataid="' + n.id + '">';
            html += '<td>';
            html += '<a opauthority="btn_setting_account_edit" href="javascript:;" class="label label-info mright5 edit_account_cls">编辑</a>';
            html += '<a opauthority="btn_setting_account_resetpwd" href="javascript:;" class="label label-warning mright5 reset_pwd_cls">重置密码</a>';
            html += '<a opauthority="btn_setting_account_lock" href="javascript:;" class="label label-danger mright5 lock_account_cls">' + (n.isLock == 0? '锁定' : '解锁') + '</a>';
            html += '<a opauthority="btn_setting_account_workstatus" href="javascript:;" class="label label-danger mright5 workstatus_account_cls">' + (n.workStatus == 1? '离职' : '复职') + '</a>';
            html += '<a opauthority="btn_setting_account_delete" href="javascript:;" class="label label-danger mright5 delete_account_cls">删除</a>';
            html += '</td>';
            html += '<td scolumn="accountName">' + n.accountName + '</td>';
            html += '<td scolumn="personalName">'+ n.personalName + '</td>';
            html += '<td scolumn="mobile">' + n.mobile + '</td>';
            html += '<td scolumn="organizationName">' + n.organizationName + '</td>';
            html += '<td scolumn="jobName">' + n.jobName + '</td>';
            html += '<td scolumn="roleNames">' + n.roleNames + '</td>';
            html += '<td scolumn="workStatus"><span class="label ' + (n.workStatus == 1? 'label-success' : 'label-failed') + '">' + (n.workStatus == 1? '在职' : '离职') + '</span></td>';
            html += '<td scolumn="isLock"><span class="label ' + (n.isLock == 0? 'label-success' : 'label-failed') + '">' + (n.isLock == 0? '正常' : '锁定') + '</span></td>';
            html += '</tr>';
        });
        $('#dataTbody').html(html);
    });
};
</script>
</html>