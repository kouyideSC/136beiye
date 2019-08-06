<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/font/iconfont.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/side-nav-bar.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/defaultTheme.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/myTheme.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/datepicker/daterangepicker.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/zTree/css/zTreeStyle/zTreeStyle.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-select.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/plugins/viewer/viewer.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/worktab.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css?t=<%=Math.random()%>">
<script src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<script>
//监听按钮点击
$(document).on('click','.btn-save',function()
{
	$(this).attr('disabled','disabled').attr('ajaxSendingFlag',1).addClass('disabled');
	var that = $(this);
	setTimeout(function(){
		that.removeAttr('disabled').removeAttr('ajaxSendingFlag').removeClass('disabled');
	},4 * 1000);
});
//扩展startWith方法
String.prototype.startWith = function(str)
{
	var reg = new RegExp("^" + str);
	return reg.test(this);
};
//设置ajax全局配置
/*var loadingHtml = '<div class="modal-loading" id="modalLoadingDiv"><div class="overlay"></div><div class="ajaxLoading showbox clearfix"><div class="loadingword"><span class="loadinggif" style="background-size:50%;"></span><span class="loadingtext">处理中，请稍候...&nbsp;</span><a href="javascript:;" class="plus-icon p-guanbi" onclick="$(this).parents(\'.modal-loading\').remove();" style="position:absolute;top:-12px;right:6px;font-size:12px;color:#ff6699"></a></div></div></div>';
$.ajaxSetup({
	beforeSend : function(data)
	{
		var url = this.url;
		url = url.substring(url.indexOf("?") + 1);
		var methodReg = new RegExp("(^|&)method=([^&]*)(&|$)"); 
		var method = url.match(methodReg);
		if(method != null)
		{
			method = unescape(method[2]);
			if(method != null && method != '')
			{
				//拦截增删改方法
				if(method.startWith("add") || method.startWith("insert") || method.startWith("save") 
					|| method.startWith("edit") || method.startWith("update")  
					|| method.startWith("delete") || method.startWith("remove"))
				{
					$('body').append(loadingHtml);
					$("#modalLoadingDiv .overlay").css({'display':'block','opacity':'0.8'});
					$("#modalLoadingDiv .showbox").stop(true).animate({'opacity':'1'},200);
                    $("#modalLoadingDiv .showbox").css({opacity:1});
				}
			}
		}
	},
	complete : function(data)
	{
		$('#modalLoadingDiv').remove();
		$('button[ajaxSendingFlag]').removeAttr('disabled').removeAttr('ajaxSendingFlag').removeClass('disabled');
        var json = data.responseText;
        try 
        {
            json = $.parseJSON(json);
            if (json.status && json.status < 0) 
            {
            	if(-1001 == json.status)
            	{
            		showoplayer({message: '您的会话已失效，请重新登录'});
            		setTimeout(function()
            		{
            			window.location.href = '${pageContext.request.contextPath}';
            		},2 * 1000);
            	}
            	else
            	{
            		showoplayer({message: (json.message || json.msg || json.desc || '操作错误，请与管理员联系')});
                    return false;
            	}
            }
        }catch (err){
        }
	},
	error : function()
	{
		$('#modalLoadingDiv').remove();
		$('button[ajaxSendingFlag]').removeAttr('disabled').removeAttr('ajaxSendingFlag').removeClass('disabled');
	}
});*/
</script>
<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/datepicker/moment.js"></script>
<script src="${pageContext.request.contextPath}/js/datepicker/daterangepicker.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.fixedheadertable.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-select.js"></script>
<script src="${pageContext.request.contextPath}/js/util.js?t=<%=Math.random()%>"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-select.js"></script>
<script src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all.js"></script>
<script src="${pageContext.request.contextPath}/js/jQueryuploadfile/jquery.ui.widget.js"></script>
<script src="${pageContext.request.contextPath}/js/jQueryuploadfile/jquery.iframe-transport.js"></script>
<script src="${pageContext.request.contextPath}/js/jQueryuploadfile/jquery.fileupload.js"></script>
<script src="${pageContext.request.contextPath}/js/common.js?t=<%=Math.random()%>"></script>
<script src="${pageContext.request.contextPath}/js/cp.page.js?t=<%=Math.random()%>"></script>
<script src="${pageContext.request.contextPath}/js/cp.fixtable.js?t=<%=Math.random()%>"></script>