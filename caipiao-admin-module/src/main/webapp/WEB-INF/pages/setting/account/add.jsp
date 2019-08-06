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
<div class="modal-dialog lg" id="addAccountDialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">添加帐户</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="addAccountForm" method="post">
				<div class="modal-bg col-sm-12">
					<div class="form-group">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>帐户名</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="输入帐户登录名" name="accountName" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>真实姓名</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="输入帐户对应的真实姓名" name="personalName" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">所属组织</label>
						<div class="col-sm-7 dropdown">
							<input class="form-control" readonly="readonly" name="organizationId" id="add_organizationDropdownMenu" data-toggle="dropdown" placeholder="选择帐户归属组织"
								   style="background: #fff;cursor: pointer;padding-right: 22px;">
							<span class="bs-caret"><span class="caret s-tree"></span></span>
							<ul class="dropdown-menu ztree select-tree" id="add_organizationId" role="menu" aria-labelledby="add_organizationDropdownMenu" style="margin-left: 0;"></ul>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">所属岗位</label>
						<div class="col-sm-7">
							<select class="form-control" name="jobTypeId" id="add_jobTypeIdSelect" title="选择帐户归属岗位" data-live-search="true" data-size="8" data-selected-text-format="count > 3" multiple></select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">所属角色</label>
						<div class="col-sm-7">
							<select class="form-control" name="roleIds" id="add_roleIdsSelect" title="选择帐户归属角色" data-live-search="true" data-size="8" data-selected-text-format="count > 3" multiple></select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">手机号</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="输入手机号码" name="mobile">
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">身份证号码</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="输入身份证号码" name="idcard">
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">邮箱</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="输入邮箱" name="email">
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">微信</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="输入微信" name="weixin">
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">QQ</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="输入QQ" name="qq">
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="addAccountSureBtn">确定</button>
		</div>
	</div>
</div>
<script>
$(function()
{
	//初始化页面插件
	initPagePlugins('#addAccountDialog',function()
	{
		var $dialogNode = $('#addAccountDialog');
		$dialogNode.find('.config-date input').css('padding-left','20px');
		$dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
	});
	//初始化岗位下拉
	$('#add_jobTypeIdSelect').fillSelectMenu({
		url: '${pageContext.request.contextPath}/setting/account/jobtype/get',
		id: 'id',
		name: 'jobName'
	});
	//初始化角色下拉
	$('#add_roleIdsSelect').fillSelectMenu({
		url: '${pageContext.request.contextPath}/setting/role/get',
		id: 'id',
		name: 'name'
	});
	//点击确定
	$('#addAccountSureBtn').on('click',function()
	{
		//发送请求
		$('#add_organizationDropdownMenu').val($('#add_organizationDropdownMenu').attr("ids"));
		$.ajax({
			url : '${pageContext.request.contextPath}/setting/account/add',
			type : 'post',
			dataType : 'json',
			data : $("#addAccountForm").serializeArray(),
			success : function(json)
			{
				$('#addAccountDialog').parents('.modal').first().modal('hide');
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
			$.fn.zTree.init($("#add_organizationId"),
			{
				view: {showLine: false, showIcon: false},
				check: {enable: true, chkStyle: 'radio',radioType: 'all'},
				callback:
				{
					beforeClick: function (id, node)
					{
						var auths = $.fn.zTree.getZTreeObj('add_organizationId');
						auths.checkNode(node, node.checked, null, true);
						return false;
					},
					onCheck: function (nodes)
					{
						var auths = $.fn.zTree.getZTreeObj('add_organizationId');
						var nodes = auths.getCheckedNodes(true);
						checked.apply(undefined, [nodes]);
					}
				},
				data: {simpleData: {enable: true}}
			}, json.datas.list);
		}
	});
	$('#add_organizationId').on('click',function (e)
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
	$('#add_organizationDropdownMenu').val(temps.join(','));
	$('#add_organizationDropdownMenu').attr('ids', ids.join(','));
}
</script>