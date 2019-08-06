<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<style>
.add-file-btn{
	display:block;white-space:nowrap; overflow:hidden; text-overflow:ellipsis;padding-top: 0;height: 31px;line-height: 31px;
}
</style>
<div class="modal-dialog lg" id="addArticleDialog" style="display: block;width: 99%;min-width: 1300px;">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">新增（发布）文章</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="addArticleForm" method="post">
				<div class="modal-bg col-sm-12">
					<div class="form-group">
						<label class="col-sm-2 control-label">文章类型</label>
						<div class="col-sm-9">
							<select class="form-control selectpicker" name="articleType">
								<option value="0">推荐</option>
								<option value="1">预测</option>
								<option value="2">情报</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label">关联彩种</label>
						<div class="col-sm-9">
							<select class="form-control" name="lotteryId" id="add_lotteryIdSelect" title="彩种" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label">关联场次/期次</label>
						<div class="col-sm-9">
							<input type="text" class="form-control" placeholder="输入场次号或期次号" name="mcode">
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label">标题</label>
						<div class="col-sm-9">
							<textarea class="form-control" name="title" placeholder="给文章起一个响亮的标题，不要超过50个字" notEmpty></textarea>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label">文章标签</label>
						<div class="col-sm-9">
							<textarea class="form-control" name="tags" placeholder="给文章设置一些标签，多个用 、隔开，不要超过100个字"></textarea>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label">发布者</label>
						<div class="col-sm-9">
							<input type="text" class="form-control" placeholder="输入小编的名字" name="author">
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label">置顶标识</label>
						<div class="col-sm-9" style="position: relative;top: -3px;">
							<input type="hidden" name="iszd" id="add_iszdFlag">
							<input type="checkbox" id="add_iszdCheckbox">&nbsp;设置置顶&nbsp;&nbsp;
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label">热门标识</label>
						<div class="col-sm-9" style="position: relative;top: -3px;">
							<input type="hidden" name="ishot" id="add_ishotFlag">
							<input type="checkbox" id="add_ishotCheckbox">&nbsp;标记为热门&nbsp;&nbsp;
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label">焦点图</label>
						<div class="col-sm-9">
							<input type="file" name="uplogo" id="add_logoFile" style="display: none;"/>
							<input type="hidden" name="logo" id="add_logo"/>
							<button id="add_uploadLogoBtn" type="button" class="btn btn-default active col-sm-11 add-file-btn">
								<span class="plus-icon p-add icon-cha"></span>
								选择文件
							</button>
							<span class="label label-danger" style="padding-left: 0;position: fixed;margin-left: 10px;margin-top: 8px;cursor: pointer;" id="add_cleanLogoFile">&nbsp;&nbsp;清除</span>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-2 control-label">文章内容</label>
						<input type="hidden" name="contents" id="add_contents">
					</div>
					<div style="width: 1150px;">
						<script type="text/plain" id="add_articleEditor" style="width:1150px;min-height:300px;"></script>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="add_articleBtn">确定</button>
		</div>
	</div>
</div>
<script>
$(function()
{
	//初始化页面插件
	initPagePlugins('#addArticleDialog',function()
	{
	});
	//初始化彩种下拉
	$('#add_lotteryIdSelect').fillSelectMenu({
		url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo',
		id: 'id',
		name: 'name'
	});
	//点击确定
	$('#add_articleBtn').on('click',function()
	{
		$('#add_iszdFlag').val($('#add_iszdCheckbox').is(':checked')? 1 : 0);
		$('#add_ishotFlag').val($('#add_ishotCheckbox').is(':checked')? 1 : 0);
		$('#add_contents').val(UM.getEditor('add_articleEditor').getContent());
		var data = $("#addArticleForm").serializeArray();
		//发送请求
		$.ajax({
			url : '${pageContext.request.contextPath}/zxgl/article/add',
			type : 'post',
			dataType : 'json',
			data : data,
			success : function(json)
			{
				$('#addArticleDialog').parents('.modal').first().modal('hide');
                showoplayer(json);
			}
		});
	});
	//点击选择文件
	$("#add_uploadLogoBtn").click(function ()
	{
		$("#add_logoFile").click();
	});
	var accept = /(\.|\/)(gif|jpe?g|png|ico?n)$/i;
	$('#add_logoFile').fileupload({
		url: '${pageContext.request.contextPath}/zxgl/article/uploadLogo',//上传地址
		dataType: 'json',
		acceptFileTypes: accept,
		autoUpload: true,
		maxFileSize: 9999999
	}).on('fileuploadadd', function (event, data)
	{
		$.each(data.files, function (index, file)
		{
			$("#add_uploadLogoBtn").html("<sapn>" + file.name + "</span>");
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
			$('#add_logo').val(data.result.datas.fpath);
		}
		else
		{
			showoplayer(data.result.dmsg);
		}
	});
	//清除logo
	$('#add_cleanLogoFile').on('click',function()
	{
		$('#add_uploadLogoBtn').text('选择文件');
		$('#add_logo').val('');
	});
	//初始化umeditor
	var width = $('#add_articleEditor').width();
	var addUmeditor = UM.getEditor('add_articleEditor');
	if(addUmeditor.$body)
	{
		addUmeditor.destroy();
		$('#add_articleEditor').width(width);
		addUmeditor = UM.getEditor('add_articleEditor');
	}
});
</script>