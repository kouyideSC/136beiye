<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<style>
.radio_vote{
	margin-right:30px;
}
.radio_vote input[type="radio"]{
	position: relative;
	top:1px;
	margin-right:5px;
}
</style>
<div class="modal-dialog lg" id="addVoteDialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">新增出票商</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="addVoteForm" method="post">
				<div class="modal-bg col-sm-12">
					<div class="form-group addVote_cls" id="addVoteId">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>出票商编号</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入出票商编号" name="voteId" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group addVote_cls" id="addVoteName">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>出票商名称</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入出票商名称" name="voteName" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">状态</label>
						<div class="col-sm-7">
							<span class="radio_vote"><input type="radio" name="status" value="1" checked>启用</span>
							<span class="radio_vote"><input type="radio" name="status" value="0">关闭</span>
						</div>
					</div>
					<div class="form-group addVote_cls" id="addVoteApiUrl">
						<label class="col-sm-3 control-label"></span>出票商服务接口</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入出票商服务接口地址" name="apiUrl" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group addVote_cls" id="addVoteKey">
						<label class="col-sm-3 control-label">出票商接口秘钥</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入出票商接口秘钥Key" name="key" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group addVote_cls" id="addVoteDesc">
						<label class="col-sm-3 control-label"></span>出票商说明</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入出票商说明信息" name="desc" notEmpty="">
							<p></p>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="addVoteSureBtn">确定</button>
		</div>
	</div>
</div>
<script>
$(function()
{
	//初始化页面插件
	initPagePlugins('#addVoteDialog',function()
	{
		var $dialogNode = $('#addVoteDialog');
		$dialogNode.find('.config-date input').css('padding-left','20px');
		$dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
	});

	//点击确定
	$('#addVoteSureBtn').on('click',function()
	{
		//发送请求
		$.ajax({
			url : '${pageContext.request.contextPath}/ticket/vote/add',
			type : 'post',
			dataType : 'json',
			data : $("#addVoteForm").serializeArray(),
			success : function(data)
			{
				$('#addVoteDialog').parents('.modal').first().modal('hide');
				showoplayer(data);
			}
		});
	});
});
</script>