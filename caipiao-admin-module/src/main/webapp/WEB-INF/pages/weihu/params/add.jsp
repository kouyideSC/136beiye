<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<style>
.radio_Params{
	margin-right:30px;
}
.radio_Params input[type="radio"]{
	position: relative;
	top:1px;
	margin-right:5px;
}
</style>
<div class="modal-dialog lg" id="addParamsDialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">新增系统参数</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="addParamsForm" method="post">
				<div class="modal-bg col-sm-12">
					<div class="form-group addParams_cls" id="addParamsRate">
						<label class="col-sm-3 control-label"></span>参数名</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="系统参数名" name="pmKey" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group addParams_cls" id="addParamsReceiveTime">
						<label class="col-sm-3 control-label">参数值</label>
						<div class="col-sm-7">
							<textarea class="form-control" placeholder="系统参数值" name="pmValue" notEmpty=""></textarea>
							<p></p>
						</div>
					</div>
					<div class="form-group addMarket_cls" id="addPmDescribe">
						<label class="col-sm-3 control-label"></span>描述</label>
						<div class="col-sm-7">
							<textarea class="form-control" name="pmDescribe" rows="5"></textarea>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="addParamsSureBtn">确定</button>
		</div>
	</div>
</div>
<script>
$(function()
{
	//初始化页面插件
	initPagePlugins('#addParamsDialog',function()
	{
		var $dialogNode = $('#addParamsDialog');
		$dialogNode.find('.config-date input').css('padding-left','20px');
		$dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
	});

	//点击确定
	$('#addParamsSureBtn').on('click',function()
	{
		//发送请求
		$.ajax({
			url : '${pageContext.request.contextPath}/weihu/params/add',
			type : 'post',
			dataType : 'json',
			data : $("#addParamsForm").serializeArray(),
			success : function(data)
			{
				$('#addParamsDialog').parents('.modal').first().modal('hide');
				showoplayer(data);
			}
		});
	});
});
</script>