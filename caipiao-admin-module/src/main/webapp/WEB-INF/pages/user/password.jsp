<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editPasswordDialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">修改帐号密码</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="editPasswordForm" method="post">
				<div class="modal-bg col-sm-12">
					<div class="form-group">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>原密码</label>
						<div class="col-sm-7">
							<input type="password" class="form-control" placeholder="输入原密码" name="oldpassword" notEmpty="" id="edit_password_oldpassword">
							<p></p>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>新密码</label>
						<div class="col-sm-7">
							<input type="password" class="form-control" placeholder="输入新密码" name="newpassword" notEmpty="" id="edit_password_newpassword">
							<p></p>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>确认密码</label>
						<div class="col-sm-7">
							<input type="password" class="form-control" placeholder="再次确认新密码" notEmpty="" id="edit_password_resurepassword">
							<p></p>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="editPasswordSureBtn">确定</button>
		</div>
	</div>
</div>
<script>
var pbkey = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuFPdRBaE6OnqQsXvVim5mOmXhXidOa16gLIV9sI2dRZW4ZejlE+zGIsAlbsDYagO+RkjgVeZd4oZ+P7roYNlLtY8YujNkMnhMqHHKAOXUqK6HMBzrCsu/JaMFNsYW7EyEnXnd1Lq15U85O1irXLYfBbrGjKrYlKv5p+3xUYKRRc+PdeYDCKPKLKl9vgZwACVYnYEEuM2ErxdYWRARukSIEOtO69X6C1xlJL+MnQlFMTWLvg2m3pvAHqa/ZMJLwFE9mf+29LnjAJJvPWjMCUGroU0Hjoq+cPTJp5EfPM6jHlIhHzl8oZCHvNRFgOkXz5aFqNbmg5m0vs8vugBGXN61wIDAQAB';
$(function()
{
	//点击确定
	$('#editPasswordSureBtn').on('click',function()
	{
		if($.trim($('#edit_password_oldpassword').val()) == '')
		{
			showoplayer({dcode:-1000,dmsg:"原密码必须输入！"});
			return false;
		}
		else if($.trim($('#edit_password_newpassword').val()) == '')
		{
			showoplayer({dcode:-1000,dmsg:"新密码必须输入！"});
			return false;
		}
		else if($('#edit_password_resurepassword').val() != $('#edit_password_newpassword').val())
		{
			showoplayer({dcode:-1000,dmsg:"确认密码与新密码不匹配！请重新输入"});
			return false;
		}
		//<c:if test="${sessionScope.user_logged_session_skey.isSale != -1}">
		$('#edit_password_oldpassword').val(getRsaData($('#edit_password_oldpassword').val()));
		$('#edit_password_newpassword').val(getRsaData($('#edit_password_newpassword').val()));
		//</c:if>
		//发送请求
		$.ajax({
			url : '${pageContext.request.contextPath}/user/password/edit',
			type : 'post',
			dataType : 'json',
			data : $("#editPasswordForm").serializeArray(),
			success : function(json)
			{
				showoplayer(json);
				if(json.dcode == 1000)
				{
					$('#editPasswordDialog').parents('.modal').first().modal('hide');
				}
			}
		});
	});
});
//rsa加密
function getRsaData(source)
{
	var encrypt = new JSEncrypt();
	encrypt.setPublicKey(pbkey);
	var rsadata = encrypt.encrypt(source);
	return rsadata
}
</script>