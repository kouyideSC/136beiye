<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html>
<html lang="en" class="app">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content ="IE=edge,chrome=1" />
<title>${sessionScope.user_logged_session_skey.isSale == -1? "136彩票管理后台" : "136彩票代理平台"}</title>
<link href="${pageContext.request.contextPath}/favicon.png" rel="icon" type="image/png" sizes="18x18"/>
<style type="text/css">
.massage-warning{
	min-width: 350px;
	max-width:500px;
	padding: 20px 65px 20px 20px;
	background: #333a4b;
	position: fixed;
	bottom: 30px;
	right: 0;
	color: #fff;
	display: none;
	text-align: left;
	font-size:12px;
	box-shadow: 0 0 20px #ccc;
}
.massage-warning a{
	color: #03A9F3;
	margin-left:20px;
}
.massage-warning b{
	padding:0 3px;
	color: #ff6666;
	font-size:13px;
}
.tips-list{
	display: inline-block;
}
.tips-item{
	margin-bottom: 5px;
}
.tips-item span{
	display: inline-block;
}
.massage-warning .p-guanbi{
	position: absolute;
	top: 20px;
	right: 20px;
	font-size: 14px;
	cursor: pointer ;
	transform:rotate(-90deg);
	-ms-transform:rotate(-90deg);
	-moz-transform:rotate(-90deg);
	-webkit-transform:rotate(-90deg);
	-o-transform:rotate(-90deg);
	transition: transform 500ms;
}
.massage-warning .p-guanbi:hover{
	transform:rotate(90deg);
	-ms-transform:rotate(90deg);
	-moz-transform:rotate(90deg);
	-webkit-transform:rotate(90deg);
	-o-transform:rotate(90deg);
	transition: all 500ms;
}
.volume{
	position: absolute;
	top: 20px;
	right: 46px;
	font-size: 14px;
}
</style>
<meta charset="UTF-8">
<%@include file="base/inc.jsp"%>
</head>
<body>
	<aside id="sidebar">
		<jsp:include page="base/sidebar.jsp"></jsp:include>
	</aside>
	<nav id="nav" class="clearfix">
		<div class="content-left pull-left">
			<div class="arrow-wrapper pull-left">
				<span class="plus-icon p-arrow-left navtabs_tabslist_prev"></span>
			</div>
			<div class="tabs-wrapper pull-left" id="navtabs_tabsdiv">
				<ul class="tabs-ul" id="navtabs_tabslist">
					<li class="tabs active" mcode="menu_index" mname="主页">
						<span class="text">主页</span>
					</li>
				</ul>
			</div>
			<div class="arrow-wrapper pull-left" style="border-left:1px solid #efeff4;">
				<span class="plus-icon p-arrow-right navtabs_tabslist_next"></span>
			</div>
			<div class="close-wrapper dropdown pull-left quickclose_menu_div">
				<button class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
					快速关闭
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu quickclose_list" role="menu" aria-labelledby="levelMenu" id="ul_nav_tabsnav_quickclose">
					<li opvalue="1"><a href="javascript:;">关闭右侧全部</a></li>
					<li role="separator" class="divider"></li>
					<li opvalue="2"><a href="javascript:;">关闭左侧全部</a></li>
					<li role="separator" class="divider"></li>
					<li opvalue="3"><a href="javascript:;">关闭其它</a></li>
					<li role="separator" class="divider"></li>
					<li opvalue="4"><a href="javascript:;">关闭全部</a></li>
				</ul>
			</div>
		</div>
		<div class="content-right">
	    	<div class="search-wrapper" style="font-size: 14px;color: #26bf8c;margin-right: 60px;">
	    		<span><span id="left_sidbar_nowdesc"></span>，${sessionScope.user_logged_session_skey.personalName}</span>
	    	</div>
			<c:if test="${sessionScope.user_logged_session_skey.isSale == -1}">
				<div class="tips-wrapper" title="修改密码" style="font-size: 14px;" id="editPassword">
					<span class="plus-icon p-rename" style="color:#d4237a;"></span>
				</div>
			</c:if>
			<div class="tips-wrapper" style="font-size: 14px;color: #FF3366;margin-right: 18px;">
				<span class="plus-icon p-close" title="退出登录" onclick="logout();" style="cursor: pointer;"></span>
			</div>
	    </div>
	</nav>
	<div class="wrap" id="iframecontent">
		<iframe id="menu_index" name="menu_index" scrolling="no" frameborder="0" style="width:100%;height:100%;" src="${pageContext.request.contextPath}/home"></iframe>
	</div>
	<div class="modal fade" id="operatorModal"></div>
	<input type="hidden" id="current_loginuser_id" value="${sessionScope.user_logged_session_skey.id}">
	<!-- 收到推送消息展示区域 -->
	<div class="massage-warning" id="messageWarning">
		<div class="tips-inner massage_warning_desc"></div>
		<i class="plus-icon p-guanbi massage_warning_close"></i>
		<i class="plus-icon volume"></i>
	</div>
	<audio id="msgAudio" src="${pageContext.request.contextPath}/media/audio/msg3.mp3" type="audio/mpeg"></audio>
</body>
<script>
var pagemcodes = parseYxJson('${sessionScope.user_logged_session_funcstrskey}');//功能权限code集合
$(function()
{
	//初始化消息提示
	$("[data-toggle='tooltip']").tooltip();
	
	//点击nav-tabs(切换页面)
	$(document).on("click","#navtabs_tabslist li[mcode]",function()
	{
		//显示tabs
		var tabnodes = $("#navtabs_tabslist li[mcode]");
		tabnodes.removeClass("active");
		tabnodes.show();
		$(this).addClass("active");
		
		//判断nav-tabs选项是否窜行
		var navheight = $("#navtabs_tabslist").height();
		if(navheight > 50)
		{
			var maxwidth = $("#navtabs_tabsdiv").width();//nav-tabs区域容器宽度
			var maxtabs = parseInt(maxwidth / 90);//最多能显示全的nav-tabs个数
			var prevtabs = $(this).prevAll("li[mcode]");//当前nav-tabs前面已有的nav-tabs
			if(prevtabs.length >= maxtabs)
			{
				var hidecount = Math.abs(prevtabs.length - maxtabs) + 2;
				tabnodes.slice(1,(1 + hidecount)).hide();
			}
		}
		//显示tabs对应的iframe
		var mcode = $(this).attr("mcode");
		$("#iframecontent iframe").hide();
		$("#" + mcode).show();
		
		//选中二级菜单
		$("#left_sidbar_menulist li[mcode]").removeClass("active");
		var linode = $("#left_sidbar_menulist li[mcode='" + mcode + "']");
		var plinode = linode.parents("li[mcode]");
		if(plinode.length > 0)
		{
			plinode.slice(0,1).toggleClass("active",true);
		}
		else
		{
			linode.toggleClass("active",true);
		}
		//初始化tabs对应的搜索框
		parent.changeTabsNavCls();
	});
	
	//关闭nav-tabs(关闭页面)
	$(document).on("click","#navtabs_tabslist li[mcode] span.navtabs_tabs_close",function(e)
	{
		var plinode = $(this).parent("li[mcode]");//待关闭的tabs
		var prevlinode = plinode.prev("li[mcode]");//待关闭的tabs的前一个tabs
		var nextlinode = plinode.next("li[mcode]");//待关闭的tabs的后一个tabs
		
		//判断,如果待关闭的tab为当前显示的tab,则需要按规则显示一个tab
		var activeMcode = '';
		if(plinode.hasClass("active"))
		{
			//判断,如果待关闭的tab是最后一个tab,则在关闭tab后显示前一个tab
			if(nextlinode.length == 0)
			{
				if(prevlinode.length > 0)
				{
					activeMcode = prevlinode.attr("mcode");
					prevlinode.toggleClass("active",true);
					prevlinode.show();
					$("#" + activeMcode).show();
				}
			}
			else
			{
				//如果待关闭的tab不是最后一个tab,则在关闭tab后显示后一个tab
				activeMcode = nextlinode.attr("mcode");
				nextlinode.toggleClass("active",true);
				nextlinode.show();
				$("#" + activeMcode).show();
			}
		}
		//删除待关闭的tabs
		$("#" + plinode.attr("mcode")).remove();
		plinode.remove();
		$("#left_sidbar_menulist li[mcode]").removeClass("active");
		var linode = $("#left_sidbar_menulist li[mcode='" + activeMcode + "']");
		var plinode = linode.parents("li[mcode]");
		if(plinode.length > 0)
		{
			plinode.slice(0,1).toggleClass("active",true);
		}
		else
		{
			linode.toggleClass("active",true);
		}
		e.stopPropagation();
	});
	//点击显示上一个nav-tabs箭头
	$("#nav .navtabs_tabslist_prev").on("click",function()
	{
		$("#navtabs_tabslist li.active").prev("li[mcode]").click();
	});
	//点击显示下一个nav-tabs箭头
	$("#nav .navtabs_tabslist_next").on("click",function()
	{
		$("#navtabs_tabslist li.active").next("li[mcode]").click();
	});
	//点击快速关闭菜单
	$(document).on("click","#ul_nav_tabsnav_quickclose li",function()
	{
		var currentNavnode = $("#navtabs_tabslist li.active[mcode]");
		var closenodes;
		if("1" == $(this).attr("opvalue"))
		{
			//关闭右侧全部
			closenodes = currentNavnode.nextAll("li[mcode]");
		}
		if("2" == $(this).attr("opvalue"))
		{
			//关闭左侧全部
			closenodes = currentNavnode.prevAll("li[mcode!='menu_workbench']");
		}
		if("3" == $(this).attr("opvalue"))
		{
			//关闭其它
			closenodes = currentNavnode.siblings("li[mcode!='menu_workbench']");
		}
		if("4" == $(this).attr("opvalue"))
		{
			//关闭全部
			closenodes = $("#navtabs_tabslist li[mcode!='menu_workbench']");
		}
		//关闭待关闭的tabs及相对应的iframe
		closenodes.each(function()
		{
			var mcode = $(this).attr("mcode");
			$("iframe[id='" + mcode + "']").remove();
			$(this).remove();
		});
		if($("#navtabs_tabslist li.active").length == 0)
		{
			$("#navtabs_tabslist li[mcode='menu_workbench']").click();
		}
		$("#left_sidbar_menulist li[mcode]").removeClass("active");
		var linode = $("#left_sidbar_menulist li[mcode='" + ($("#navtabs_tabslist li[mcode].active").attr("mcode")) + "']");
		var plinode = linode.parents("li[mcode]");
		if(plinode.length > 0)
		{
			plinode.slice(0,1).toggleClass("active",true);
		}
		else
		{
			linode.toggleClass("active",true);
		}
	});
	//点击修改密码
	$(document).on("click","#editPassword",function()
	{
		$('#operatorModal').fillWithUrl('${pageContext.request.contextPath}/user/password/initEdit').modal('show');
	});
	//点击系统提示声音设置
	$(document).on('click','#messageWarning .volume',function()
	{
		if($(this).hasClass("p-yinliang"))
		{
			$(this).removeClass("p-yinliang").addClass("p-jingyin");
			setCookie("messageWarningVolumeSetting","p-jingyin");
		}
		else
		{
			$(this).removeClass("p-jingyin").addClass("p-yinliang");
			setCookie("messageWarningVolumeSetting","p-yinliang");
		}
	});
	//关闭推送消息提醒
	$(".massage_warning_close").on("click",function()
	{
		$(this).parent('.massage-warning').slideUp();
		stopAudio();
	});
	//设置时间描述
	var date = new Date();
	var nowhour = date.getHours();
	$('#left_sidbar_nowdesc').html(nowhour < 12? '上午好' : (nowhour == 12? '中午好' : (nowhour < 19? '下午好' : '晚上好')));
	$("#left_sidbar_menulist li[mcode='menu_workbench']").addClass("active");
});
//出票消息推送-点击查看
function showCpMsg()
{
	$("#left_sidbar_menulist li[mcode='menu_shopchupiao_query']").click();
	$('#messageWarning').hide();
}
//播放新消息/待办任务提示音
var messageWarningVolumeSetting = getCookie("messageWarningVolumeSetting");//获取用户设置声音模式
messageWarningVolumeSetting = messageWarningVolumeSetting && messageWarningVolumeSetting != ''? messageWarningVolumeSetting : 'p-yinliang';
$("#messageWarning").find(".volume").addClass(messageWarningVolumeSetting);
var ttsurl = 'http://tts.baidu.com/text2audio?idx=1&cuid=baidu_speech_demo&cod=2&lan=zh&ctp=1&pdt=1&per=4&vol=5&pit=6';
function playAudio(readText)
{
	if($("#messageWarning").find(".volume").hasClass("p-yinliang"))
	{
		var msgAudio = $("#msgAudio")[0];
		msgAudio.pause();
		msgAudio.currentTime = 0;
		msgAudio.play();
		/*setTimeout(function()
		{
			if(readText && readText != "")
			{
				$('#ttmsgAudio').remove();
				$('body').append('<audio id="ttmsgAudio" autoplay="autoplay" src="' + (ttsurl + '&tex=' + readText) + '"></audio>');
			}
		},1000);*/
	}
}
//停止播放新消息/待办任务提示音
function stopAudio()
{
	var msgAudio = $("#msgAudio")[0].pause();
	$('#ttmsgAudio').remove();
}
//显示通知
function showNotification(data,text)
{
	var notification = new Notification('136彩票管理后台',{icon:'${pageContext.request.contextPath}/favicon.png',body:(text),tag:'notification',renotify:true,requireInteraction:true});
	notification.onclick = function()
	{
		window.focus();
		if(data.cptotal && data.cptotal > 0)
		{
			showCpMsg();
		}
		notification.close();
	};
}
//检测窗口是否为最小化状态
function checkWindowMinStatus()
{
	var ismin = false;
	if(window.outerWidth != undefined) 
	{
		ismin = window.outerWidth <= 160 && window.outerHeight <= 27;
	}
	else 
	{
		ismin = window.screenTop < -30000 && window.screenLeft < -30000;
	}
	return ismin;
}
//退出登录
function logout()
{
	window.location.href = '${pageContext.request.contextPath}/logout.do';
}
</script>
<script src="${pageContext.request.contextPath}/js/socket.io.js"></script>
<script src="${pageContext.request.contextPath}/js/cp.socket.js"></script>
</html>
