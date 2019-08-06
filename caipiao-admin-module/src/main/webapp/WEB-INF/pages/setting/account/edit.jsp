<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css"/>
<script src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.core.js"></script>
<script src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.excheck.js"></script>
<style type="text/css">
.dropdown-menu.select-tree {
    width: -moz-calc(100% - 30px);
    width: -webkit-calc(100% - 30px);
    width: calc(100% - 30px);
    left: 15px;
}
ul.ztree {
    height: 200px;
    overflow-y: scroll;
    overflow-x: auto;
    padding: 5px;
}
</style>
<div class="modal-dialog lg" id="editAccountDialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">修改帐户</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editAccountForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <div class="modal-bg col-sm-12">
                    <div class="form-group">
                        <label class="col-sm-3 control-label">帐户名</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="输入帐户登录名" notEmpty="" value="${params.accountName}" disabled>
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>真实姓名</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="输入帐户对应的真实姓名" name="personalName" notEmpty="" value="${params.personalName}">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">所属组织</label>
                        <div class="col-sm-7 dropdown">
                            <input class="form-control" readonly="readonly" name="organizationId" id="edit_organizationDropdownMenu" data-toggle="dropdown" placeholder="选择帐户归属组织"
                                   style="background: #fff;cursor: pointer;padding-right: 22px;">
                            <span class="bs-caret"><span class="caret s-tree"></span></span>
                            <ul class="dropdown-menu ztree select-tree" id="edit_organizationId" role="menu" aria-labelledby="edit_organizationDropdownMenu" style="margin-left: 0;"></ul>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">所属岗位</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="jobTypeId" id="edit_jobTypeIdSelect" title="选择帐户归属岗位" data-live-search="true" data-size="8" data-selected-text-format="count > 3" multiple></select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">所属角色</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="roleIds" id="edit_roleIdsSelect" title="选择帐户归属角色" data-live-search="true" data-size="8" data-selected-text-format="count > 3" multiple></select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">手机号</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="输入手机号码" name="mobile" value="${params.mobile}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">身份证号码</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="输入身份证号码" name="idcard" value="${params.idcard}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">邮箱</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="输入邮箱" name="email" value="${params.email}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">微信</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="输入微信" name="weixin" value="${params.weixin}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">QQ</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="输入QQ" name="qq" value="${params.qq}">
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editAccountSureBtn">确定</button>
        </div>
    </div>
</div>
<script>
$(function()
{
    //初始化页面插件
    initPagePlugins('#editAccountDialog',function()
    {
        var $dialogNode = $('#editAccountDialog');
        $dialogNode.find('.config-date input').css('padding-left','20px');
        $dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
    });
    //初始化岗位下拉
    $('#edit_jobTypeIdSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/setting/account/jobtype/get',
        id: 'id',
        name: 'jobName',
        callback : function(json)
        {
            $('#edit_jobTypeIdSelect').selectpicker('val','${params.jobTypeId}');
        }
    });
    //初始化角色下拉
    $('#edit_roleIdsSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/setting/role/get',
        id: 'id',
        name: 'name',
        callback : function(json)
        {
            $('#edit_roleIdsSelect').selectpicker('val','${params.roleIds}'.split(','));
        }
    });
    //点击确定
    $('#editAccountSureBtn').on('click',function()
    {
        //发送请求
        $('#edit_organizationDropdownMenu').val($('#edit_organizationDropdownMenu').attr("ids"));
        $.ajax({
            url : '${pageContext.request.contextPath}/setting/account/edit',
            type : 'post',
            dataType : 'json',
            data : $("#editAccountForm").serializeArray(),
            success : function(json)
            {
                showoplayer(json);
                if(json.dcode == 1000)
                {
                    $('#editAccountDialog').parents('.modal').first().modal('hide');
                }
            }
        });
    });
    //初始组织树
    $.ajax({
        url: '${pageContext.request.contextPath}/setting/account/otree/get',
        async: true,
        dataType : 'json',
        success: function (json)
        {
            $.fn.zTree.init($("#edit_organizationId"),
            {
                view: {showLine: false, showIcon: false},
                check: {enable: true, chkStyle: 'radio',radioType: 'all'},
                callback:
                {
                    beforeClick: function (id, node)
                    {
                        var auths = $.fn.zTree.getZTreeObj('edit_organizationId');
                        auths.checkNode(node, node.checked, null, true);
                        return false;
                    },
                    onCheck: function (nodes)
                    {
                        var auths = $.fn.zTree.getZTreeObj('edit_organizationId');
                        var nodes = auths.getCheckedNodes(true);
                        checked.apply(undefined, [nodes]);
                    }
                },
                data: {simpleData: {enable: true}}
            }, json.datas.list);

            //初始化帐户当前的归属组织
            var organizationId = "${params.organizationId}";
            var organizationName = "${params.organizationName}";
            $('#edit_organizationDropdownMenu').attr('ids',$.trim(organizationId) == ''? '' : organizationId);
            $('#edit_organizationDropdownMenu').val($.trim(organizationName) == ''? '' : organizationName);
            var ztree = $.fn.zTree.getZTreeObj('edit_organizationId');
            if($.trim(organizationId) != '')
            {
                organizationId = organizationId.split(",");
                $.each(organizationId,function(i,m)
                {
                    ztree.getNodeByParam("id",m,null).checked = true;
                });
            }
        }
    });
    $('#edit_organizationId').on('click',function (e)
    {
        e.stopPropagation();
    })
});
function checked(nodes)
{
    var temps = [];
    var ids = [];
    for (var i in nodes)
    {
        temps.push(nodes[i].name);
        ids.push(nodes[i].id);
    }
    $('#edit_organizationDropdownMenu').val(temps.join(','));
    $('#edit_organizationDropdownMenu').attr('ids', ids.join(','));
}
</script>