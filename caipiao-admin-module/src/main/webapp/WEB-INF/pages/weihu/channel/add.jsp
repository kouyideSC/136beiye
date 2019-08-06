<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal-dialog lg" id="addChannelDialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">添加渠道</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="addChannelForm" method="post">
				<div class="modal-bg col-sm-12">
					<div class="form-group addChannel_cls" id="addChannelName">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>渠道名称</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="" name="channelName" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group addChannel_cls" id="addChannelCode">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>渠道编号</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="" name="channelCode" notEmpty="">
							<p></p>
						</div>
					</div>
                    <div class="form-group addChannel_cls" id="addContactMobile">
                        <label class="col-sm-3 control-label"><span class="check-star"></span>渠道手机号</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="" name="contactMobile">
                            <p></p>
                        </div>
                    </div>
					<div class="form-group addChannel_cls" id="addAuthKey">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>加密KEY</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="" name="authKey" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group addChannel_cls" id="addOutAccountUserId">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>出款账户编号</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="" name="outAccountUserId" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group addChannel_cls" id="addOverstepAccount">
						<label class="col-sm-3 control-label">透支金额</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="" name="overstepAccount">
							<p></p>
						</div>
					</div>
					<div class="form-group addChannel_cls" id="addStatus">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>状态</label>
						<div class="col-sm-7">
							<select class="form-control" name="status" id="status">
								<option value="0">停用</option>
								<option value="1">启用</option>
							</select>
						</div>
					</div>
					<div class="form-group addChannel_cls" id="addNotifyStatus">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>出票通知</label>
						<div class="col-sm-7">
							<select class="form-control" name="notifyStatus" id="notifyStatus">
								<option value="0">不通知</option>
								<option value="1">通知</option>
							</select>
						</div>
					</div>
					<div class="form-group addChannel_cls" id="addIpLimit">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>IP白名单</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" value="" name="ipLimit" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group addChannel_cls" id="addBeginTime">
						<label class="col-sm-3 control-label">生效时间</label>
						<div class="col-sm-7">
							<input type="text" class="form-control datetimepicker" value="" name="beginTime">
							<p></p>
						</div>
					</div>
					<div class="form-group addChannel_cls" id="addEndTime">
						<label class="col-sm-3 control-label">结束时间</label>
						<div class="col-sm-7">
							<input type="text" class="form-control datetimepicker" value="" name="endTime">
							<p></p>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="addChannelSureBtn">确定</button>
		</div>
	</div>
</div>
<script>
$(function()
{
	//初始化页面插件
	initPagePlugins('#addChannelDialog',function()
	{
		var $dialogNode = $('#addChannelDialog');
		$dialogNode.find('.config-date input').css('padding-left','20px');
		$dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
	});

	//点击确定
	$('#addChannelSureBtn').on('click',function()
	{
		//发送请求
		$.ajax({
			url : '${pageContext.request.contextPath}/weihu/channel/add',
			type : 'post',
			dataType : 'json',
			data : $("#addChannelForm").serializeArray(),
			success : function(data)
			{
				$('#addChannelDialog').parents('.modal').first().modal('hide');
				showoplayer(data);
			}
		});
	});

});
</script>