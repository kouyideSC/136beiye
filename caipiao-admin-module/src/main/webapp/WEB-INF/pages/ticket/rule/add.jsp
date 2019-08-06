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
<div class="modal-dialog lg" id="addRuleDialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">新增分票规则</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="addRuleForm" method="post">
				<input type="hidden" name="lotteryName" id="lotteryName" value="">
				<input type="hidden" name="playName" id="playName" value="">
				<div class="modal-bg col-sm-12">
					<div class="form-group addVote_cls" id="addVoteName">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>选择出票商</label>
						<div class="col-sm-7">
							<select class="form-control" name="voteId" id="addRuleVoteSelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
						</div>
					</div>
					<div class="form-group addVote_cls" id="addLotteryId">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>选择彩种</label>
						<div class="col-sm-7">
							<select class="form-control" name="lotteryId" id="addRuleLotterySelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
						</div>
					</div>
					<div class="form-group addVote_cls" id="addPlayType">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>选择玩法</label>
						<div class="col-sm-7">
							<select class="form-control" name="playType" id="addRulePlayTypeSelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
						</div>
					</div>
					<div class="form-group addVote_cls" id="addRuleRate">
						<label class="col-sm-3 control-label"></span>分票比例</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入分票比例 如填写80 表示80%" name="rate" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group addVote_cls" id="addRuleReceiveTime">
						<label class="col-sm-3 control-label">出票商收票时间段</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入出票商收票时间段[*$*表示不限制]" name="receiveTime" notEmpty="">
							<p></p>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="addRuleSureBtn">确定</button>
		</div>
	</div>
</div>
<script>
$(function()
{
	//彩种名称下拉
	$('#addRuleLotterySelect').fillSelectMenu({
		url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
		id: 'id',
		name: 'shortName',
		noDefault: true
	});

	//出票商名称下拉
	$('#addRuleVoteSelect').fillSelectMenu({
		url: '${pageContext.request.contextPath}/ticket/vote/list',
		id: 'voteId',
		name: 'voteName',
		noDefault: true
	});

	//根据彩种获取玩法
	$('#addRuleLotterySelect').on('change',function()
	{
		var lotteryId = $(this).val();
		$('#addRulePlayTypeSelect').fillSelectMenu({
			url: '${pageContext.request.contextPath}/ticket/rule/getPlayType?lotteryId=' + lotteryId,
			id: 'id',
			name: 'value',
			noDefault : true
		});
	});

	//初始化页面插件
	initPagePlugins('#addRuleDialog',function()
	{
		var $dialogNode = $('#addRuleDialog');
		$dialogNode.find('.config-date input').css('padding-left','20px');
		$dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
	});

	//点击确定
	$('#addRuleSureBtn').on('click',function()
	{
		$('#lotteryName').val($('#addRuleLotterySelect').find("option:selected").text());
		$('#playName').val($('#addRulePlayTypeSelect').find("option:selected").text());

		//发送请求
		$.ajax({
			url : '${pageContext.request.contextPath}/ticket/rule/add',
			type : 'post',
			dataType : 'json',
			data : $("#addRuleForm").serializeArray(),
			success : function(data)
			{
				$('#addRuleDialog').parents('.modal').first().modal('hide');
				showoplayer(data);
			}
		});
	});
});
</script>