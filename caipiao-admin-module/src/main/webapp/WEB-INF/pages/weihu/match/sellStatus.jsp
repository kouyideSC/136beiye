<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editSellStatusDialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">修改销售状态</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="editSellStatusForm" method="post">
				<input type="hidden" name="id" value="${params.id}">
				<input type="hidden" name="ids" value="${params.ids}">
				<input type="hidden" name="lotteryId" value="${params.lotteryId}">
				<div class="modal-bg col-sm-12">
					<c:if test="${params.ids == null || params.ids == ''}">
					<div class="form-group">
						<label class="col-sm-3 control-label">期次</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="${params.period}" disabled>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">场次</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="${params.matchCode}" disabled>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">赛事</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="${params.leagueName}" disabled>
						</div>
					</div>
					<div class="form-group edit_sellStatus_hostName_cls">
						<label class="col-sm-3 control-label">主队</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="${params.hostName}" disabled>
						</div>
					</div>
					<div class="form-group edit_sellStatus_guestName_cls">
						<label class="col-sm-3 control-label">客队</label>
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
					</c:if>
					<div class="form-group">
						<label class="col-sm-3 control-label">销售状态</label>
						<div class="col-sm-7">
							<select class="form-control" name="status" id="editSellStatusStatusSelect">
								<option value="-1">取消</option>
								<option value="0">停售</option>
								<option value="1">开售</option>
								<option value="2">截止</option>
							</select>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="editSellStatusSureBtn">确定</button>
		</div>
	</div>
</div>
<script>
$(function()
{
	//根据彩种切换主客队显示顺序
	if('${params.lotteryId}' == '1710')
	{
		$('#editSellStatusDialog .edit_sellStatus_guestName_cls').insertBefore($('#editSellStatusDialog .edit_sellStatus_hostName_cls'));
	}
	//初始化销售状态下拉
	$('#editSellStatusStatusSelect').selectpicker({});
	if('${params.status}' == '-1' || '${params.status}' == '2')
	{
		$('#editSellStatusStatusSelect option').slice(1,3).remove();
		$('#editSellStatusStatusSelect').selectpicker('refresh');
	}
	$('#editSellStatusStatusSelect').selectpicker('val','${params.status}');

	//点击确定
	$('#editSellStatusSureBtn').on('click',function()
	{
		//发送请求
		$.ajax({
			url : '${pageContext.request.contextPath}/weihu/match/editSellStatus',
			type : 'post',
			dataType : 'json',
			data : $("#editSellStatusForm").serializeArray(),
			success : function(data)
			{
				$('#editSellStatusDialog').parents('.modal').first().modal('hide');
				showoplayer(data);
			}
		});
	});
});
</script>