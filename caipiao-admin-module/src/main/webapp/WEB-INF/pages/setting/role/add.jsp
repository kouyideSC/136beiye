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
<div class="modal-dialog lg" id="addRoleDialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">添加角色</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="addRoleForm" method="post">
				<div class="modal-bg col-sm-12">
					<div class="form-group">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>角色名称</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="输入角色的名称" name="name" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">角色描述</label>
						<div class="col-sm-7">
							<textarea class="form-control" placeholder="输入角色描述" name="description"></textarea>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">功能模块</label>
						<div class="col-sm-7 dropdown">
							<input class="form-control" readonly="readonly" name="moduleIds" id="add_moduleDropdownMenu" data-toggle="dropdown" placeholder="请选择功能模块"
								   style="background: #fff;cursor: pointer;padding-right: 22px;">
							<span class="bs-caret"><span class="caret s-tree"></span></span>
							<ul class="dropdown-menu ztree select-tree" id="add_moduleIds" role="menu" aria-labelledby="add_moduleDropdownMenu" style="margin-left: 0;"></ul>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="addRoleSureBtn">确定</button>
		</div>
	</div>
</div>
<script>
$(function()
{
	//初始化页面插件
	initPagePlugins('#addRoleDialog',function()
	{
		var $dialogNode = $('#addRoleDialog');
		$dialogNode.find('.config-date input').css('padding-left','20px');
		$dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
	});
	//点击确定
	$('#addRoleSureBtn').on('click',function()
	{
		//发送请求
		$('#add_moduleDropdownMenu').val($('#add_moduleDropdownMenu').attr("ids"));
		$.ajax({
			url : '${pageContext.request.contextPath}/setting/role/add',
			type : 'post',
			dataType : 'json',
			data : $("#addRoleForm").serializeArray(),
			success : function(json)
			{
				$('#addRoleDialog').parents('.modal').first().modal('hide');
			}
		});
	});
	//初始功能模块树
	$.ajax({
		url: '${pageContext.request.contextPath}/setting/role/mtree/get',
		async: true,
		dataType : 'json',
		success: function (json)
		{
			$.fn.zTree.init($("#add_moduleIds"),
			{
				view: {showLine: false, showIcon: false},
				check: {enable: true},
				callback:
				{
					beforeClick: function (id, node)
					{
						var auths = $.fn.zTree.getZTreeObj('add_moduleIds');
						auths.checkNode(node, node.checked, null, true);
						return false;
					},
					onCheck: function (nodes)
					{
						var auths = $.fn.zTree.getZTreeObj('add_moduleIds');
						var nodes = auths.getCheckedNodes(true);
						checked.apply(undefined, [nodes]);
					}
				},
				data: {simpleData: {enable: true}}
			}, json.datas.list);
		}
	});
	$('#add_moduleIds').on('click',function (e)
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
	$('#add_moduleDropdownMenu').val(temps.join(','));
	$('#add_moduleDropdownMenu').attr('ids', ids.join(','));
}
</script>