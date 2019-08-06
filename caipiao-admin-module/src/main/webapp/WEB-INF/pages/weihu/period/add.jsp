<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<style>
.periodRange_radio{
	margin-right:30px;
}
.periodRange_radio input[type="radio"]{
	position: relative;
	top:1px;
	margin-right:5px;
}
</style>
<div class="modal-dialog lg" id="addPeriodDialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">新增期次</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="addPeriodForm" method="post">
				<div class="modal-bg col-sm-12">
					<div class="form-group">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>选择彩种</label>
						<div class="col-sm-7">
							<select class="form-control" name="lotteryId" id="addPeriodLotterySelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">选择生成模式</label>
						<div class="col-sm-7">
							<span class="periodRange_radio"><input type="radio" name="periodRange" value="1" checked>指定年份</span>
							<span class="periodRange_radio"><input type="radio" name="periodRange" value="2">指定期数</span>
							<span class="periodRange_radio"><input type="radio" name="periodRange" value="3">手动录入</span>
						</div>
					</div>
					<div class="form-group addPeriod_periodRange_cls" id="addPeriodPeriodOfYearDiv">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>输入年份</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入要生成期次的年份" name="periodYear" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group addPeriod_periodRange_cls" id="addPeriodPeriodNumDiv" style="display: none;">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>生成期次数</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入要生成的期次数" name="periodNum" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group addPeriod_periodRange_cls addPeriod_periodInfo" style="display: none;">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>期次号</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入期次号" name="period" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group addPeriod_periodRange_cls addPeriod_periodInfo" style="display: none;">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>销售状态</label>
						<div class="col-sm-7">
							<select class="form-control" name="sellStatus">
								<option value="-1">已截止</option>
								<option value="0">未开售</option>
								<option value="1">销售中</option>
							</select>
						</div>
					</div>
					<div class="form-group addPeriod_periodRange_cls addPeriod_periodInfo" style="display: none;">
						<label class="col-sm-3 control-label">开始销售时间</label>
						<div class="col-sm-7 config-date">
							<input class="form-control datetimepicker" type="text" name="sellStartTime" notEmpty>
						</div>
					</div>
					<div class="form-group addPeriod_periodRange_cls addPeriod_periodInfo" style="display: none;">
						<label class="col-sm-3 control-label">截止销售时间</label>
						<div class="col-sm-7 config-date">
							<input class="form-control datetimepicker" type="text" name="sellEndTime" notEmpty>
						</div>
					</div>
					<div class="form-group addPeriod_periodRange_cls addPeriod_periodInfo" style="display: none;">
						<label class="col-sm-3 control-label">官方截止时间</label>
						<div class="col-sm-7 config-date">
							<input class="form-control datetimepicker" type="text" name="authorityEndTime" notEmpty>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="addPeriodSureBtn">确定</button>
		</div>
	</div>
</div>
<script>
$(function()
{
	//初始化页面插件
	initPagePlugins('#addPeriodDialog',function()
	{
		var $dialogNode = $('#addPeriodDialog');
		$dialogNode.find('.config-date input').css('padding-left','20px');
		$dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
	});

	//初始化彩种下拉
	$('#addPeriodLotterySelect').fillSelectMenu({
		url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo',
		id: 'id',
		name: 'name'
	});
	//彩种变更
	$('#addPeriodLotterySelect').on('change',function()
	{
		var periodRangeNode = $('#addPeriodForm .periodRange_radio');
		if($(this).val() == '04')
		{
			periodRangeNode.slice(1,2).hide();
			periodRangeNode.slice(0,1).attr('checked','checked');
		}
		else
		{
			periodRangeNode.slice(1,2).show();
		}
	});
	//生成期次范围变更
	$('#addPeriodDialog input[name="periodRange"]').on('change',function()
	{
		$('#addPeriodDialog .addPeriod_periodRange_cls').hide();
		if($(this).val() == '1')
		{
			$('#addPeriodPeriodOfYearDiv').show();
		}
		else if($(this).val() == '2')
		{
			$('#addPeriodPeriodNumDiv').show();
		}
		else if($(this).val() == '3')
		{
			$('#addPeriodDialog .addPeriod_periodInfo').show();
		}
	});
	//点击确定
	$('#addPeriodSureBtn').on('click',function()
	{
		//发送请求
		$.ajax({
			url : '${pageContext.request.contextPath}/weihu/period/add',
			type : 'post',
			dataType : 'json',
			data : $("#addPeriodForm").serializeArray(),
			success : function(data)
			{
				$('#addPeriodDialog').parents('.modal').first().modal('hide');
				showoplayer(data);
			}
		});
	});
});
</script>