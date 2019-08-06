<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal-dialog lg" id="auditMatchDialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">赛果审核</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="auditMatchForm" method="post">
				<input type="hidden" name="id" value="${params.id}">
				<input type="hidden" name="lotteryId" value="${params.lotteryId}">
				<div class="modal-bg col-sm-12">
					<h2 class="modal-title">对阵信息</h2>
					<div class="form-group">
						<label class="col-sm-3 control-label">期次</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="${params.period}" disabled>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">赛事</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="${params.leagueName}" disabled>
						</div>
					</div>
					<div class="form-group edit_sellStatus_hostName_cls">
						<label class="col-sm-3 control-label">主队名</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="${params.hostName}" disabled>
						</div>
					</div>
					<div class="form-group edit_sellStatus_guestName_cls">
						<label class="col-sm-3 control-label">客队名</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="${params.guestName}" disabled>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">比赛时间</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="${params.matchTime}" disabled>
						</div>
					</div>
				</div>
				<div class="modal-bg col-sm-12">
					<h2 class="modal-title">审核内容</h2>
					<div class="form-group">
						<label class="col-sm-3 control-label">销售状态</label>
						<div class="col-sm-7">
							<select class="form-control" name="status" id="auditMatchStatusSelect">
								<option value="-1">取消</option>
								<option value="0">停售</option>
								<option value="1">开售</option>
								<option value="2">截止</option>
							</select>
						</div>
					</div>
					<div class="form-group edit_sellStatus_halfScore_cls">
						<label class="col-sm-3 control-label">半场比分 </label>
						<div class="col-sm-7">
							<input type="text" class="form-control" name="halfScore" value="${params.halfScore}" id="audit_halfScore">
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">全场比分</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" name="score" value="${params.score}" id="audit_score">
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="auditMatchSureBtn">确定</button>
		</div>
	</div>
</div>
<script>
$(function()
{
	//根据彩种切换主客队显示顺序
	if('${params.lotteryId}' == '1710')
	{
		$('#auditMatchDialog .edit_sellStatus_guestName_cls').insertBefore($('#auditMatchDialog .edit_sellStatus_hostName_cls'));
		$('#auditMatchDialog .edit_sellStatus_halfScore_cls').remove();
	}
	//初始化销售状态下拉
	$('#auditMatchStatusSelect').selectpicker({});
	if('${params.status}' == '-1' || '${params.status}' == '2')
	{
		$('#auditMatchStatusSelect option').slice(1,3).remove();
		$('#auditMatchStatusSelect').selectpicker('refresh');
	}
	$('#auditMatchStatusSelect').selectpicker('val','${params.status}');

	//点击确定
	$('#auditMatchSureBtn').on('click',function()
	{
		var halfScore = $('#audit_halfScore').val();
		var score = $('#audit_score').val();
		if($('#auditMatchStatusSelect').val() != -1)
		{
			if($.trim(score) == '')
			{
				showoplayer({'dcode':'-1000','dmsg':'比分不能为空!'});
				return false;
			}
			if('${params.lotteryId}' == '1700' && $.trim(halfScore) == '')
			{
				showoplayer({'dcode':'-1000','dmsg':'半场比分不能为空!'});
				return false;
			}
			if(score.indexOf(':') < 0 || score.indexOf(';') > -1 || score.split(':').length != 2)
			{
				showoplayer({'dcode':'-1000','dmsg':'比分格式错误!'});
				return false;
			}
			if('${params.lotteryId}' == '1700' && (halfScore.indexOf(':') < 0 || halfScore.indexOf(';') > -1 || halfScore.split(':').length != 2))
			{
				showoplayer({'dcode':'-1000','dmsg':'半场比分格式错误!'});
				return false;
			}
		}
		//发送请求
		$.ajax({
			url : '${pageContext.request.contextPath}/weihu/match/audit',
			type : 'post',
			dataType : 'json',
			data : $("#auditMatchForm").serializeArray(),
			success : function(data)
			{
				$('#auditMatchDialog').parents('.modal').first().modal('hide');
				showoplayer(data);
			}
		});
	});
});
</script>