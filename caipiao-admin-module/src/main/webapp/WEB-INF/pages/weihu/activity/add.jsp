<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<style>
	.add-file-btn{
		display:block;white-space:nowrap; overflow:hidden; text-overflow:ellipsis;padding-top: 0;height: 31px;line-height: 31px;
	}
</style>
<div class="modal-dialog lg" id="addActivityDialog" style="display: block;width: 99%;min-width: 1150px;">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">新增活动</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="addActivityForm" method="post">
				<div class="modal-bg col-sm-12">
					<div class="form-group">
						<label class="col-sm-3 control-label">活动类型</label>
						<div class="col-sm-7">
							<select class="form-control" name="activityType" id="add_activityTypeSelect">
								<option value="0">彩票首页焦点图</option>
								<option value="1">资讯版首页焦点图</option>
								<option value="2">特定活动</option>
								<option value="3">公告</option>
								<option value="4">资讯/优惠信息</option>
								<option value="5">app欢迎页图片</option>
							</select>
						</div>
					</div>
					<div class="form-group" style="display: none;" id="add_settingIsbannerDivs">
						<label class="col-sm-3 control-label">附加选项</label>
						<div class="col-sm-7" style="margin-top: -3px;">
							<input type="hidden" name="isbanner" id="add_isbanner">
							<input type="checkbox" id="add_settingIsbanner">
							<span style="margin-top: 10px;position: fixed;">&nbsp;设置为焦点图</span>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">活动标题</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入标题" name="title">
							<p></p>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">活动名称
						</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入活动名称" name="activityName">
							<p></p>
						</div>
					</div>
					<div class="form-group add_coupons_cls" style="display: none;">
						<label class="col-sm-3 control-label">优惠券赠送类型</label>
						<div class="col-sm-7">
							<input type="hidden" name="czsCouponIds" id="add_czsCouponIds">
							<select class="form-control" name="couponType" id="add_couponTypeSelect">
								<option value="0">注册送</option>
								<option value="1">充值送</option>
							</select>
						</div>
					</div>
					<div class="form-group add_coupons_cls" style="display: none;">
						<label class="col-sm-3 control-label">优惠券赠送模式</label>
						<div class="col-sm-7">
							<select class="form-control" name="couponMode" id="add_couponModeSelect">
								<option value="0">固定模式</option>
								<option value="1">自定义模式</option>
							</select>
						</div>
					</div>
					<div class="form-group add_coupons_cls add_couponsMode_cls add_couponsMode_0_cls" style="display: none;">
						<label class="col-sm-3 control-label">选择优惠券</label>
						<div class="col-sm-7">
							<select class="form-control" name="couponIds" id="add_couponIdsSelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3" multiple></select>
						</div>
					</div>
					<div class="form-group add_coupons_cls add_couponsMode_cls add_couponsMode_1_cls" style="display: none;">
						<div class="col-sm-10">
							<input type="button" class="btn btn-danger col-sm-12" style="font-size: 12px;" id="add_couponConditionBtn" value="+ 添加一组金额范围和优惠券">
						</div>
					</div>
					<div class="form-group add_coupons_cls" style="display: none;">
						<label class="col-sm-3 control-label">优惠券过期时间</label>
						<div class="col-sm-7 config-date">
							<input class="form-control datetimepicker" type="text" name="couponExpireTime">
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">显示客户端</label>
						<div class="col-sm-7">
							<select class="form-control" name="clientType">
								<option value="1">app</option>
								<option value="2">h5</option>
								<option value="3">web</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">链接跳转方式</label>
						<div class="col-sm-7">
							<select class="form-control" name="build" id="add_BuildSelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3">
								<option value="0">原生详情页</option>
								<option value="1">H5页面</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">是否显示</label>
						<div class="col-sm-7">
							<select class="form-control" name="isShow">
								<option value="1">显示</option>
								<option value="0">不显示</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">详情链接地址
						</label>
						<div class="col-sm-7">
							<textarea class="form-control" placeholder="请输入点击活动所跳转的链接地址" name="linkUrl"></textarea>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">活动开始时间</label>
						<div class="col-sm-7 config-date">
							<input class="form-control datepicker" type="text" name="beginTime">
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">活动结束时间</label>
						<div class="col-sm-7 config-date">
							<input class="form-control datepicker" type="text" name="expireTime">
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">logo/banner</label>
						<div class="col-sm-7">
							<input type="file" name="uplogo" id="add_pictureFile" style="display: none;"/>
							<input type="hidden" name="pictureUrl" id="add_picture"/>
							<button id="add_pictureBtn" type="button" class="btn btn-default active col-sm-11 add-file-btn">
								<span style="font-size: 16px;">+</span> 选择文件
							</button>
							<span class="label label-danger" style="padding-left: 0;position: fixed;margin-left: 6px;margin-top: 9px;cursor: pointer;" id="add_pictureCleanBtn">&nbsp;&nbsp;清除</span>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">活动内容</label>
						<input type="hidden" name="content" id="add_content">
					</div>
					<div style="width: 1000px;">
						<script type="text/plain" id="add_activityEditor" style="width:1000px;min-height:300px;"></script>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="addActivitySureBtn">确定</button>
		</div>
	</div>
</div>
<script>
$(function ()
{
	//初始化页面插件
	initPagePlugins('#addActivityDialog',function()
	{
		var $dialogNode = $('#addActivityDialog');
		$dialogNode.find('.config-date input').css('padding-left','20px');
		$dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
	});
	//初始化优惠券下拉
	$('#add_couponIdsSelect').fillSelectMenu({
		url: '${pageContext.request.contextPath}/weihu/coupon/get?status=1',
		id: 'id',
		name: '`name（money）`'
	});
	//活动类型变更
	$('#add_activityTypeSelect').on('change',function()
	{
		if($(this).val() == 2)
		{
			$('.add_coupons_cls').show();
			$('.add_couponsMode_cls').hide();
			if($('#add_couponTypeSelect').val() == 0)
			{
				$('#add_couponModeSelect').selectpicker('val',0);
				$('#add_couponModeSelect').attr('disabled','disabled');
				$('.add_couponsMode_0_cls').show();
			}
			else
			{
				$('.add_couponsMode_' + $('#add_couponModeSelect').val() + '_cls').show();
			}
		}
		else
		{
			$('.add_coupons_cls').hide();
		}
		if($(this).val() != 0 && $(this).val() != 1)
		{
			$('#add_settingIsbannerDivs').show();
		}
		else
		{
			$('#add_settingIsbannerDivs').hide();
		}
	});
	//优惠券赠送类型变更
	$('#add_couponTypeSelect').on('change',function()
	{
		if($(this).val() == 0)
		{
			$('#add_couponModeSelect').selectpicker('val',0);
			$('#add_couponModeSelect').attr('disabled','disabled');
			$('.add_couponsMode_0_cls').show();
		}
		else
		{
			$('#add_couponModeSelect').removeAttr('disabled');
		}
	});
	//优惠券赠送模式变更
	$('#add_couponModeSelect').on('change',function()
	{
		$('.add_couponsMode_cls').hide();
		$('.add_couponsMode_' + $(this).val() + '_cls').show();
	});
	//点击添加一组金额范围和优惠券
	$('#add_couponConditionBtn').on('click',function()
	{
		var $conditionNode = $('<div class="form-group add_coupons_cls add_couponsMode_cls add_couponsMode_1_cls add_couponCondition_cls"></div>');

		//初始化金额范围节点
		var html = '<div class="col-sm-6">';
		html += '<a class="add_couponCondition_del_cls" href="javascript:;" style="position: fixed;margin-top: 7px;color: red;font-weight: bold;">X</a>';
		html += '<span style="margin-left: 18px;">充值金额范围：</span>';
		html += '<input type="text" class="form-control mincz" placeholder="最小金额" style="width:90px;display: inline;">';
		html += '&nbsp;到&nbsp;';
		html += '<input type="text" class="form-control maxcz" placeholder="最大金额" style="width:90px;display: inline;">';
		html += '<span style="margin-left:30px;">选择优惠券：</span></div>';
		$conditionNode.append(html);

		//初始化选择优惠券节点
		var $couponNode = $('<div class="col-sm-5" style="margin-left: -86px"></div>');
		var $couponSelectClone = $('#add_couponIdsSelect').clone();
		$couponSelectClone.appendTo($couponNode);
		$conditionNode.append($couponNode);
		$couponSelectClone.removeAttr('id').removeAttr('name');
		$couponSelectClone.css({'width':'130px','display':'inline'});
		$couponSelectClone.addClass('couponIds');
		$couponSelectClone.selectpicker('refresh');

		//追加条件节点
		var $lastNode = $('.add_couponCondition_cls').slice(-1);
		if($lastNode.length == 0)
		{
			$(this).parents('.form-group').first().after($conditionNode);
		}
		else
		{
			$lastNode.after($conditionNode);
		}
		//点击删除金额范围和优惠券
		$conditionNode.find('.add_couponCondition_del_cls').click(function()
		{
			$(this).parents('div.add_couponCondition_cls').first().remove();
		});
	});
	//详情显示模式下拉初始化
	$.ajax({
		url : '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
		dataType : 'json',
		success : function(json)
		{
			if(json && json.datas && json.datas.list)
			{
				var html = '';
				$.each(json.datas.list,function(i,m)
				{
					html += '<option value="' + m.id + '">' + m.name + '</option>';
				});
				$('#add_BuildSelect').append(html);
				$('#add_BuildSelect').selectpicker('refresh');
			}
		}
	});
	//点击选择文件
	$("#add_pictureBtn").click(function ()
	{
		$("#add_pictureFile").click();
	});
	var accept = /(\.|\/)(gif|jpe?g|png|ico?n)$/i;
	$('#add_pictureFile').fileupload({
		url: '${pageContext.request.contextPath}/weihu/activity/uploadLogo',//上传地址
		dataType: 'json',
		acceptFileTypes: accept,
		autoUpload: true,
		maxFileSize: 9999999
	}).on('fileuploadadd', function (event, data)
	{
		$.each(data.files, function (index, file)
		{
			$("#add_pictureBtn").html("<sapn>" + file.name + "</span>");
		});
	}).on('fileuploadprogress', function (event, data)
	{
		var progress = parseInt(data.loaded / data.total * 100, 10);//上传进度
		//$('<div class="process"><div class="process-bar"></div></div>').appendTo('body');
		//$('.process-bar').css('width',progress + '%');
	}).on('fileuploaddone', function (event, data)
	{
		if(data.result.dcode == 1000)
		{
			$('#add_picture').val(data.result.datas.fpath);
		}
		else
		{
			showoplayer(data.result.dmsg);
		}
	});
	//清除logo
	$('#add_pictureCleanBtn').on('click',function()
	{
		$('#add_pictureBtn').text('选择文件');
		$('#add_picture').val('');
	});
	//点击确定
	$('#addActivitySureBtn').on('click',function()
	{
		$('#add_content').val(UM.getEditor('add_activityEditor').getContent());
		$('#add_isbanner').val($('#add_activityTypeSelect').val() > 1? ($('#add_settingIsbanner').is(":checked")? 1 : 0) : 0);
		if($('#add_couponTypeSelect').val() == 1 && $('#add_couponModeSelect').val() == 1)
		{
			var czsCouponIds = new Array();
			$('.add_couponCondition_cls').each(function(i,m)
			{
				debugger;
				var conditionData = new Object();
				conditionData.mincz = $(m).find('.mincz').val();
				conditionData.maxcz = $(m).find('.maxcz').val();
				conditionData.couponIds = $(m).find('select.couponIds').val().join(',');
				czsCouponIds.push(conditionData);
			});
			$('#add_czsCouponIds').val(JSON.stringify(czsCouponIds));
		}
		//发送请求
		$.ajax({
			url : '${pageContext.request.contextPath}/weihu/activity/add',
			type : 'post',
			dataType : 'json',
			data : $("#addActivityForm").serializeArray(),
			success : function(data)
			{
				$('#addActivityDialog').parents('.modal').first().modal('hide');
				showoplayer(data);
			}
		});
	});
	//初始化umeditor
	var width = $('#add_activityEditor').width();
	var addUmeditor = UM.getEditor('add_activityEditor');
	if(addUmeditor.$body)
	{
		addUmeditor.destroy();
		$('#add_activityEditor').width(width);
		addUmeditor = UM.getEditor('add_activityEditor');
	}
});
</script>