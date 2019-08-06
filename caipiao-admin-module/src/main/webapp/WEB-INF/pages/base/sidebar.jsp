<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="title-wrapper clearfix">
	<span id="left_sidbar_homelogo">
		<img src="${pageContext.request.contextPath}/favicon.png" style="position: fixed;margin-top: 11px;width: 25px;height: 25px;">
		<span style="font-size: 13px;letter-spacing: 1px;margin-left: 28px;position: fixed;">
            <c:choose>
                <c:when test="${sessionScope.user_logged_session_skey.isSale == -1}">
                    136彩票管理后台
                </c:when>
                <c:otherwise>
                    136彩票代理平台
                </c:otherwise>
            </c:choose>
        </span>
	</span>
	<span class="plus-icon p-double-left p-list" data-toggle="tooltip" data-placement="right" title="收起导航"></span>
	<span class="plus-icon p-double-right p-list" data-toggle="tooltip" data-placement="right" title="展开导航"></span>
</div>
<ul class="menu-wrapper" id="left_sidbar_menulist">
	${sessionScope.session_menu_html_skey}
</ul>
<%--<div class="bottom">
	<div class="info-wrapper">
		&lt;%&ndash;<div class="info-img-wrapper">
			<img id="sidebar_userinfo_img" src="${pageContext.request.contextPath}/personal.do?method=viewPhoto&fileName=${sessionScope.user_logged_session_skey.imageFileName}" alt="" class="info-img">
		</div>&ndash;%&gt;
		<div class="info-desc" style="color: yellow;">
			<p class="info-name"><span id="left_sidbar_nowdesc"></span>！${sessionScope.user_logged_session_skey.personalName}</p>
			&lt;%&ndash;<p class="job">${sessionScope.user_logged_session_skey.jobName}</p>&ndash;%&gt;
		</div>
	</div>
</div>--%>
<script type="text/javascript">
window.onload = function()
{
	//收起/展开左侧菜单
	$('.p-double-left').on('click', function()
	{
		$('#sidebar').addClass('short');
		$('#nav').addClass('short');
		$('#left_sidbar_homelogo').hide();
	});
	$('.p-double-right').on('click', function()
	{
		$('#sidebar').removeClass('short');
		$('#nav').removeClass('short');
		$('#left_sidbar_homelogo').show();
	});
	//点击导航菜单项
	$("#left_sidbar_menulist li[mcode][links],#left_sidbar_settinglist li[mcode][links],#indexing_dict").on("click",function()
	{
		//提取当前菜单信息
		var data = new Object();
		data.mcode = $(this).attr("mcode");
		data.mname = $(this).attr("mname");
		data.url = $(this).attr("links");
		var targetFrame = $('iframe[name="' + $(this).attr("mcode") + '"]');
		if(targetFrame.length == 0)
		{
			//创建nav-tabs选项并选中
			var tabnodes = $("#navtabs_tabslist li[mcode]");
			tabnodes.removeClass("active");
			tabnodes.show();
			var navtabsHtml = '<li class="tabs active" mname="' + data.mname + '" mcode="' + data.mcode + '">';
			navtabsHtml += '<span class="text">' + data.mname + '</span>';
			navtabsHtml += '<span class="plus-icon p-cha icon-cha navtabs_tabs_close"></span>';
			navtabsHtml += '</li>';
			$("#navtabs_tabslist").append(navtabsHtml);
			$("#top_input_search").val("");//重置搜索输入框

			//创建nav-tabs选项所对应的iframe
			$("#iframecontent iframe").hide();
			var iframeHtml = createFrame(data);
			$("#iframecontent").append(iframeHtml);
			
			//判断nav-tabs选项是否窜行
			var navheight = $("#navtabs_tabslist").height();
			if(navheight > 50) {
				var maxwidth = $("#navtabs_tabsdiv").width();//nav-tabs区域容器宽度
				var maxtabs = parseInt(maxwidth / 90);//最多能显示全的nav-tabs个数
				var prevtabs = $("#navtabs_tabslist li.active").prevAll("li[mcode]");//当前nav-tabs前面已有的nav-tabs
				if(prevtabs.length >= maxtabs)
				{
					var hidecount = Math.abs(prevtabs.length - maxtabs) + 2;
					tabnodes.slice(1,(1 + hidecount)).hide();
				}
				$("#navtabs_tabslist li[mcode]:visible").slice(1,2).hide();
			}
		}
		else
		{
			//如果菜单已有对应的iframe,则切换至该菜单
			$("#navtabs_tabslist li[mcode='" + data.mcode + "']").click();
			targetFrame.attr("src",data.url);
		}
		//选中二级菜单
		$("#left_sidbar_menulist li[mcode]").removeClass("active");
		var plinode = $(this).parents("li[mcode]");
		if(plinode.length > 0)
		{
			plinode.slice(0,1).toggleClass("active",true);
		}
		else
		{
			$(this).toggleClass("active",true);
		}
		//更改tabs(上一个/下一个)箭头样式
		changeTabsNavCls();
		$("#left_sidbar_settinglist").removeClass("active");
	});
	//点击设置
	$('#sidebar .p-setting').on('click',function(e)
	{
		e.stopPropagation();
		var settingnode = $('.setting-list');
		if(settingnode.hasClass("active")) {
			settingnode.removeClass("active");
		}
		else
		{
			settingnode.addClass('active');
		}
    });
};
//创建iframe
function createFrame(data)
{
	var iframeHtml = '<iframe id="' + data.mcode + '" name="' + data.mcode + '" scrolling="auto" frameborder="0" src="' + data.url + '" style="width:100%;height:100%;"></iframe>';
	return iframeHtml;
}
//更改tabs上一个/下一个操作箭头的样式
function changeTabsNavCls()
{
	var tabsnode = $("#navtabs_tabslist li[mcode]");
	if(tabsnode.length == 1)
	{
		$("#nav .navtabs_tabslist_prev").removeClass("active");
		$("#nav .navtabs_tabslist_next").removeClass("active");
	}
	else
	{
		if(tabsnode.slice(0,1).hasClass("active"))
		{
			$("#nav .navtabs_tabslist_prev").removeClass("active");
		}
		else
		{
			$("#nav .navtabs_tabslist_prev").toggleClass("active",true);
		}
		if(tabsnode.slice(-1).hasClass("active"))
		{
			$("#nav .navtabs_tabslist_next").removeClass("active");
		}
		else
		{
			$("#nav .navtabs_tabslist_next").toggleClass("active",true);
		}
	}
}
</script>