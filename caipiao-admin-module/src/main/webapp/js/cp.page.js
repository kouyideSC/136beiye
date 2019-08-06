var sellStatusColorJson = {'-1':'label-primary','0':'label-failed','1':'label-success','2':'label-failed','3':'label-primary'};//销售状态对应的背景色
var stateStatusColorJson = {'0':'label-warning','1':'label-failed','2':'label-info','3':'label-danger','4':'label-success','5':'label-success','6':'label-success','7':'label-success','8':'label-success','9':'label-success','10':'label-success','11':'label-success','12':'label-success','13':'label-success','99':'label-primary'};//计奖状态对应的背景色
var userVipColorJson = {'1':'label-primary','2':'label-failed','3':'label-default','4':'label-info','5':'label-danger','6':'label-success','7':'label-warning'};
var mcode;//当前所在菜单编号
$(function ()
{
    //权限过滤-操作按钮
    $("[opauthority]").each(function()
    {
        if($(parent.pagemcodes).attr($(this).attr("opauthority")) == undefined)
        {
            $(this).remove();
        }
    });
    //权限过滤-下钻/链接
    $("[dtauthority]").each(function()
    {
        if($(parent.pagemcodes).attr($(this).attr("dtauthority")) == undefined)
        {
            $(this).click(function (event)
            {
                event.preventDefault();
            });
        }
    });
	//初始化表格显示列
	mcode = $("#navtabs_tabslist li[mcode].active",parent.document).attr("mcode");
	var hidelist = getCookie(mcode + "_table_hide_columns");
	var showlist = getCookie(mcode + "_table_show_columns");
	if($.trim(hidelist) != "")
	{
		//初始化设置窗体中隐藏的字段
		hidelist = hidelist.split(",");
		var hidehtml = '';
		$.each(hidelist,function(i,m)
		{
			m = m.split("-");
			$('th[scolumn="' + m[0] + '"]').hide();
			$('td[scolumn="' + m[0] + '"]').hide();
			hidehtml += '<li scolumn="' + m[0] + '">' + m[1] + '</li>';
			$('.list-box-show li[scolumn="' + m[0] + '"]').remove();
		});
		$('.list-box-hide').append(hidehtml);
	}
	//选中默认每页显示条数
    var userPageSize = getCookie("userPageSize");
    if (userPageSize != null && $.trim(userPageSize) != "")
    {
    	$(".pagination_page_size").val(userPageSize);
    }
    //初始化日期控件(年月日格式)
    $('.datepicker').daterangepicker({
        "showDropdowns": true,
        "singleDatePicker": true,
        format: 'YYYY-MM-DD',//时间格式:年月日 (完整格式:YYYY-MM-DD HH:mm:ss)
        locale: {applyLabel: '确定', cancelLabel: '取消', format: 'YYYY-MM-DD'}
    });
    $('.datepicker').after('<i class="glyphicon glyphicon-calendar fa fa-calendar"></i>');
    $('.datepicker').after('<b class="glyphicon glyphicon-remove-circle btn_date_clear"></b>');
    $('.datepicker').val("");

    //初始化日期控件(年月日 时分秒格式)
    $('.datetimepicker').daterangepicker({
        "showDropdowns": true,
        "singleDatePicker": true,
        timePicker: true,
        timePickerSeconds : true,//是否显示秒
        timePicker24Hour: true,
        autoApply: true,
        format: 'YYYY-MM-DD HH:mm:ss',//时间格式:年月日 时分 (完整格式:YYYY-MM-DD HH:mm:ss)
        applyClass: 'btn-small btn-primary blue',
        cancelClass: 'btn-small',
        locale: {applyLabel: '确定', cancelLabel: '取消', format: 'YYYY-MM-DD HH:mm:ss'}
    });
    $('.datetimepicker').after('<i class="glyphicon glyphicon-calendar fa fa-calendar"></i>');
    $('.datetimepicker').after('<b class="glyphicon glyphicon-remove-circle btn_date_clear"></b>');
    $('.datetimepicker').val("");
	$('.daterangepicker.dropdown-menu').css('min-width','365px');

    //初始化日期控件(年月格式)
    $('.datepickerym').daterangepicker({
        "showDropdowns": true,
        "singleDatePicker": true,
        format: 'YYYY-MM',//时间格式:年月
        locale: {applyLabel: '确定', cancelLabel: '取消', format: 'YYYY-MM'}
    });
    $('.datepickerym').after('<i class="glyphicon glyphicon-calendar fa fa-calendar"></i>');
    $('.datepickerym').after('<b class="glyphicon glyphicon-remove-circle btn_date_clear"></b>');
    $('.datepickerym').val("");

    //初始化日期控件(年月日格式)
    $('.datepickerymd').daterangepicker({
        "showDropdowns": true,
        "singleDatePicker": true,
        format: 'YYYYMMDD',//时间格式:年月日
        locale: {applyLabel: '确定', cancelLabel: '取消', format: 'YYYYMMDD'}
    });
    $('.datepickerymd').after('<i class="glyphicon glyphicon-calendar fa fa-calendar"></i>');
    $('.datepickerymd').after('<b class="glyphicon glyphicon-remove-circle btn_date_clear"></b>');
    $('.datepickerymd').val("");

    //清除日期
    $(document).off("click", ".btn_date_clear");
    $(document).on("click", '.btn_date_clear', function ()
    {
    	$(this).prev("input").val("");
    });
    //点击页码数(包括首尾页和上下页),如果该分页段的父元素有funcname属性,则优先调用funcname属性指定的函数名,默认调用querydatas方法
    $(document).off("click", ".pagelist_ul_cls li[page]");
    $(document).on("click", ".pagelist_ul_cls li[page]", function () {
        var navnode = $(this).parents(".pagelist_cls");
        if ($(this).css("cursor") != "not-allowed" && !$(this).hasClass("disabled")) {
            clickpage($(this), function () {
                if (navnode.attr("funcname") != undefined) {
                    var func = navnode.attr("funcname");
                    window[func](this.toString());
                }
                else {
                    querydatas(this.toString());
                }
            });
        }
    });
    //切换每页显示条数
    $(document).off("change", "#paginationPageSize,.pagination_page_size");
    $(document).on("change", "#paginationPageSize,.pagination_page_size", function () {
        setCookie("userPageSize", $(this).val());
        var navnode = $(this).parents(".pagelist_cls");
        if (navnode.attr("funcname") != undefined) {
            var func = navnode.attr("funcname");
            window[func](1);
        }
        else {
            querydatas(1);
        }
    });
    //卡片tab切换
    $(document).on('click','.card-tab-item',function(e){
    	var i = $(this).index();
    	$(this).addClass('active').siblings('').removeClass('active');
    	$(this).parents('.card-tab-list').siblings('.card-tab-con').eq(i).addClass('active').siblings('').removeClass('active');
    });
    //点击排序
    $(document).on("click", "[sortfield] .sorter-up,[sortfield] .sorter-down", function ()
    {
    	var fixtable = $(".fixtable_tableData");
    	var pnode = $(this).parent("div[sortfield]");
    	if ($(this).hasClass("active"))
    	{
    		$(this).removeClass("active");
    		$(this).parent("div").attr("sortstr", "");
    		if(fixtable.length > 0)
    		{
    			var fixsortnode = fixtable.find("div[sortfield='" + pnode.attr("sortfield") + "']");
    			fixsortnode.attr("sortstr","");
    			fixsortnode.find("." + $(this).attr("class")).removeClass("active");
    		}
    	}
        else
        {
            var tablenode = $(this).parents("table");
            if(tablenode.length == 0)
            {
            	tablenode = $(this).parents(".mytable_fixed");
            }
            tablenode.find("div[sortfield]").attr("sortstr", "").find("div").removeClass("active");
            if(fixtable.length > 0)
            {
            	fixtable.find("div[sortfield]").attr("sortstr", "").find("div").removeClass("active");
            }
            $(this).toggleClass("active", true);
            var pnode = $(this).parent("div");
            var fixsortnode = fixtable.find("div[sortfield='" + pnode.attr("sortfield") + "']");
            if($(this).hasClass("sorter-up"))
            {
            	pnode.attr("sortstr", pnode.attr("sortfield") + " asc");
            	if(fixtable.length > 0)
        		{
        			fixsortnode.attr("sortstr",pnode.attr("sortstr"));
        			fixsortnode.find(".sorter-up").toggleClass("active",true);
        			fixsortnode.find(".sorter-down").removeClass("active");
        		}
            }
            else if ($(this).hasClass("sorter-down"))
            {
            	pnode.attr("sortstr", pnode.attr("sortfield") + " desc");
            	if(fixtable.length > 0)
        		{
        			fixsortnode.attr("sortstr",pnode.attr("sortstr"));
        			fixsortnode.find(".sorter-down").toggleClass("active",true);
        			fixsortnode.find(".sorter-up").removeClass("active");
        		}
            }
            else
            {
            	if(fixtable.length > 0)
        		{
        			fixsortnode.removeAttr("sortstr");
        		}
            	pnode.removeAttr("sortstr");
            }
        }
        var funcname = $(this).parents("table").attr("funcname") || $(this).parents(".mytable_fixed").attr("funcname");
        if(funcname != undefined)
        {
        	window[funcname](1);
        }
        else
        {
        	querydatas(1);
        }
    });
    //关闭操作反馈提示层
    $(document).on('click', '.warn-alert-close', function ()
    {
    	var pnode = $(this).parent("warn-alert");
    	pnode.fadeOut(800);
    	pnode.remove();
    });
    //点击模态框的取消
    $(document).on('click', '.btn_modal_cancel', function ()
    {
        $(this).parents(".modal").first().modal('hide');
    });
    //给有固定列的表格绑定滚动监听
    $('.mytable_fixed').each(function()
    {
    	var fixtableNode = $(this);
    	var mytable_conentent_Dom = fixtableNode.find(".mytable_conentent")[0];
    	if(mytable_conentent_Dom != undefined)
    	{
    		var mytable_fixedColumn_Dom = fixtableNode.find(".mytable_fixedColumn")[0];
    		var mytable_header_left_Dom = fixtableNode.find(".mytable_header_left")[0];
    		mytable_conentent_Dom.addEventListener("scroll",function(event)
    		{
    			var target = event.target;
	    		//mytable_fixedColumn_Dom.scrollTop = target.scrollTop;
    			mytable_header_left_Dom.scrollLeft = target.scrollLeft;
    		},false);
    	}
    });
    //输入框校验
    $(document).off("blur","textarea[notEmpty],textarea[validRegex],input[notEmpty],input[validRegex],input[validFunc],input[validUrl]");
    $(document).on("blur","textarea[notEmpty],textarea[validRegex],input[notEmpty],input[validRegex],input[validFunc],input[validUrl]",function()
    {
        var node = $(this);
        //执行非空校验
        if(node.attr("notEmpty") != undefined)
        {
            if($.trim(node.val()) == "")
            {
                node.parents(".form-group").toggleClass("has-error",true);
                node = node.siblings("p");
                node = node.length > 0? node : $(this).parent("div").siblings("p");
                node.toggleClass("error-text",true).html("必填字段不能为空.").show();
                return false;
            }
        }
        //执行正则校验
        if(node.attr("validRegex") != undefined)
        {
            var regexp = new RegExp($(this).attr("validRegex"));
            var result = regexp.test(node.val());
            if(result == false)
            {
                node.parents(".form-group").toggleClass("has-error",true);
                node = node.siblings("p");
                node = node.length > 0? node : $(this).parent("div").siblings("p");
                node.toggleClass("error-text",true).html("内容格式或长度不符合要求.").show();
                return false;
            }
        }
        //执行自定义校验函数
        if(node.attr("validFunc") != undefined)
        {
            var json = window[node.attr("validFunc")]($(this));
            if(json.status != 1000)
            {
                node.parents(".form-group").toggleClass("has-error",true);
                node = node.siblings("p");
                node = node.length > 0? node : $(this).parent("div").siblings("p");
                node.toggleClass("error-text",true).html(json.message).show();
                return false;
            }
        }
        //执行给定URL的校验
        if(node.attr("validUrl") != undefined)
        {
            var data = new Object();
            data[node.attr("name")] = node.val();
            data.editId = $("input[editId]").val();
            var url = node.attr("validUrl");
            $.ajax({
                url		: url,
                type	: 'post',
                dataType: 'json',
                data 	: data,
                success : function(json)
                {
                    if(json.status != 1000)
                    {
                        node.parents(".form-group").toggleClass("has-error",true);
                        node.attr("validUrlfalse",1);
                        node = node.siblings("p");
                        node.toggleClass("error-text",true).html(json.message).show();
                    }
                }
            });
        }
    });
    //清除校验样式
    $(document).off("focus change","select[notEmpty],textarea[notEmpty],textarea[validRegex],input[notEmpty],input[validRegex],input[validFunc],input[validUrl]");
    $(document).on("focus change","select[notEmpty],textarea[notEmpty],textarea[validRegex],input[notEmpty],input[validRegex],input[validFunc],input[validUrl]",function()
	{
	    $(this).parents(".form-group").removeClass("has-error");
	    $(this).removeAttr("validUrlfalse");
	    var pNode = $(this).is("select")? $(this).parent().siblings("p") : $(this).siblings("p");
	    pNode = pNode.length > 0 ? pNode : $(this).parent("div").siblings("p");
	    pNode.html("").hide();
	});
    //页面点击事件
    $(document).on('click', function (e)
    {
        var qcnode = $(".quickclose_menu_div", window.parent.document);
        if (!qcnode.is(e.target) && qcnode.has(e.target).length === 0 && qcnode.find(".quickclose_list").is(":visible"))
        {
            qcnode.find("button").click();
        }
    });
});
$(document).on('mouseout','.col-inner',function(e){
	$('.tooltips').css('display','none');
	$('.tooltips').html('');
});
/**
 * 初始化页面插件(主要针对弹窗页面)
 * @param    select        弹窗装载体(jquery选择器,比如:#addUserModal,尽量避免传递诸如body之类的选项)
 * @param    callback    初始化完成后的回调函数,可传可不传
 */
function initPagePlugins(select, callback)
{
    var bodynode = $(select);
    if (bodynode.length > 0)
    {
    	//初始化日期控件(年月日格式)
        bodynode.find('.datepicker').each(function ()
        {
            var value = $(this).val();
            $(this).daterangepicker({
                "showDropdowns": true,
                "singleDatePicker": true,
                format: 'YYYY-MM-DD',//时间格式:年月日 (完整格式:YYYY-MM-DD HH:mm:ss)
                locale: {applyLabel: '确定', cancelLabel: '取消', format: 'YYYY-MM-DD'}
            });
            $(this).val(value);
            $(this).after('<i class="glyphicon glyphicon-calendar fa fa-calendar"></i>');
            $(this).after('<b class="glyphicon glyphicon-remove-circle btn_date_clear"></b>');
        });
        //初始化日期控件(年月日 时分秒格式)
        bodynode.find('.datetimepicker').each(function ()
        {
            var value = $(this).val();
            $(this).daterangepicker({
                "showDropdowns": true,
                "singleDatePicker": true,
                timePicker: true,
                timePickerSeconds : true,//是否显示秒
                timePicker24Hour: true,
                autoApply: true,
                format: 'YYYY-MM-DD HH:mm:ss',//时间格式:年月日 时分 (完整格式:YYYY-MM-DD HH:mm:ss)
                applyClass: 'btn-small btn-primary blue',
                cancelClass: 'btn-small',
                locale: {applyLabel: '确定', cancelLabel: '取消', format: 'YYYY-MM-DD HH:mm:ss'}
            });
            $(this).val(value);
            $(this).after('<i class="glyphicon glyphicon-calendar fa fa-calendar"></i>');
            $(this).after('<b class="glyphicon glyphicon-remove-circle btn_date_clear"></b>');
        });
        //初始化下拉框
        bodynode.find('select[id!="paginationPageSize"]').each(function ()
        {
        	if ($(this).find("option[value='']").length == 0)
            {
                var value = $(this).val();
                if (!$(this).attr("noDefault"))
                {
                    $(this).prepend('<option value="">--' + ($(this).attr('title') || '请选择') + '--</option>');
                }
                $(this).selectpicker('refresh').selectpicker('val', value);
            }
        });
        //过滤权限-操作按钮
        bodynode.find("[opauthority]").each(function()
        {
            if($(parent.pagemcodes).attr($(this).attr("opauthority")) == undefined)
            {
                $(this).remove();
            }
        });
        //过滤权限-下钻
        bodynode.find("[dtauthority]").each(function()
        {
            if($(parent.pagemcodes).attr($(this).attr("dtauthority")) == undefined)
            {
                $(this).click(function (event)
                {
                    event.preventDefault();
                });
            }
        });
        var userPageSize = getCookie("userPageSize");
        if (userPageSize != null && $.trim(userPageSize) != "")
        {
        	bodynode.find(".pagination_page_size").val(userPageSize);
        }
    }
    if (callback != undefined)
    {
    	callback.call();
    }
}
/**
 * 卡片页面初始化(主要针对卡片)
 */
function initPageCards()
{
	//页面滚动头部固定
	$(".card-wrap-show").scroll(function() {
    	var cardScrollTop = $(this).scrollTop();
    	$(this).find('.card-header').css('top',cardScrollTop);
    });
	//设置高度
	$('.card-header').each(function(){
		var cardHeaderHeight = $(this).height();
		$(this).next('.card-main').css('padding-top',cardHeaderHeight + 10);
	});
	//过滤权限
	$(".card-wrap-show").each(function()
	{
		//过滤权限-操作按钮
		$(this).find("[opauthority]").each(function()
	    {
	    	if($(parent.pagemcodes).attr($(this).attr("opauthority")) == undefined)
			{
	    		$(this).remove();
			}
	    });
		//过滤权限-下钻
		$(this).find("[dtauthority]").each(function()
		{
			if($(parent.pagemcodes).attr($(this).attr("dtauthority")) == undefined)
			{
                $(this).click(function (event)
                {
                    event.preventDefault();
                });
			}
		});
	});
}
/**
 * 初始化动态创建的节点内容
 * @param    select      动态内容装载体(jquery选择器,比如:#addUserModal,尽量避免传递诸如body之类的选项)
 * @param    callback    初始化完成后的回调函数,可传可不传
 */
function initCreatedNode(select, callback)
{
    var bodynode = select? $(select) : $('body');
    if (bodynode.length > 0)
    {
        //权限过滤-操作按钮
        bodynode.find("[opauthority]").each(function()
        {
            if($(parent.pagemcodes).attr($(this).attr("opauthority")) == undefined)
            {
                $(this).remove();
            }
        });
        //权限过滤-下钻/链接
        bodynode.find("[dtauthority]").each(function()
        {
            if($(parent.pagemcodes).attr($(this).attr("dtauthority")) == undefined)
            {
                $(this).click(function (event)
                {
                    event.preventDefault();
                });
            }
        });
    }
    if (callback != undefined)
    {
    	callback.call();
    }
}
/**
 * 获取ajax数据(分页列表)
 * @param    data    数据参数
 * @param    fn      回调函数
 */
function pageAjax(datas, fn)
{
	//获取分页参数
	var pagelistNode;
	var tableNode = $(datas.select? datas.select : 'table');
	var fixTableNode = tableNode.parents(".fixtable_tableLayout").first();
	if(fixTableNode.length > 0)
	{
		pagelistNode = fixTableNode.siblings('.pagelist_cls').find('.pagelist_ul_cls');
	}
	else
	{
		pagelistNode = tableNode.siblings('.pagelist_cls').find('.pagelist_ul_cls');
	}
	if(pagelistNode.length > 0)
	{
		datas.page = datas.page? datas.page : pagelistNode.find('li[page][class*="active"]').attr("page");
		datas.psize = datas.psize? datas.psize : pagelistNode.find('.pagination_page_size').val();
	}
    datas.needpage = datas.psize == undefined ? undefined : 1;
    datas.page = datas.page == undefined ? 1 : datas.page;
    datas.pstart = (datas.page * 1 - 1) * datas.psize;
    datas.sorts = datas.notsorts == 1 ? datas.sorts : getsortstr(datas.select);
    $.ajax({
        url: datas.url,
        type: 'post',
        dataType: 'json',
        data: datas,
        async: datas.async != undefined ? datas.async : true,
        success: function (json) 
        {
        	json.page = datas.page;
        	json.psize = datas.psize;
        	$.extend(datas,json);
        	if(datas.needpage == 1) 
        	{
        		var tpage = 1;
                var tsize = 0;
                if(json.dcode == 1000)
                {
					tsize = json.datas.tsize;
                    tpage = (tsize % datas.psize) != 0 ? (parseInt(tsize / datas.psize) + 1) : (tsize / datas.psize);
                }
                datas.tpage = tpage;
                datas.tsize = tsize;
                createPageList(datas);
            }
            fn.call(datas);
            if (!checkPageDatas(json, datas))
            {
            	datas.tpage = 0;
                datas.tsize = 0;
                createPageList(datas);
            }
            datas.mcode = $("#navtabs_tabslist li[mcode].active",parent.document).attr("mcode");
            checkAuthority(datas);
            $(datas.select? datas.select : 'table').fixTable({fixcmber:datas.fixcmber},datas);
        },
        error: function (json) {
        	$.extend(datas,json);
        	datas.list = null;
            if (datas.needpage == 1) {
                datas.tpage = 0;
                datas.tsize = 0;
                createPageList(datas);
            }
            fn.call(datas);
        }
    });
}
/**
 * 获取分页html
 * @param    datas.page    当前页码数
 * @param    datas.size    当前每页显示条数
 * @param    datas.tpage    总页数
 * @param    datas.toal    总记录条数
 */
var pagesizeHtml = '<li class="nobd"><select id="paginationPageSize" class="pagination_page_size">';
pagesizeHtml += '<option value="10">10</option>';
pagesizeHtml += '<option value="15">15</option>';
pagesizeHtml += '<option value="20">20</option>';
pagesizeHtml += '<option value="25">25</option>';
pagesizeHtml += '<option value="30">30</option>';
pagesizeHtml += '<option value="50">50</option>';
pagesizeHtml += '</select></li>';
function getPageListHtml(datas)
{
    var page = datas.page * 1;
    var psize = datas.psize * 1;
    var tpage = datas.tpage * 1;
    var tsize = datas.tsize * 1;
    var pagehtml = '';
    var maxshow = 8;
    if (tsize > 0) {
        pagehtml += '<li page="p"><a class="plus-icon p-left"></a></li>';
        if (page >= 9) {
            pagehtml += '<li page="1"><a href="javascript:;">1</a></li>';
            pagehtml += '<li class="plus-icon p-dots nobd"></li>';
        }
        var min = 0;
        var max = 0;
        if (tpage > maxshow) {
            if (page >= 9) {
                if ((page + maxshow) > tpage) {
                    min = (tpage - maxshow) + 1;
                }
                else if ((page + maxshow) == tpage) {
                    min = (tpage - maxshow) - 1;
                }
                else {
                    if ((page - maxshow) > 0) {
                        min = page - (maxshow - 2);
                    }
                    else {
                        min = 1;
                    }
                }
            }
            else {
                min = 1;
            }
            max = min + maxshow - 1;
        }
        else {
            min = 1;
            max = tpage;
        }
        for (var i = min; i < max + 1; i++) {
            pagehtml += '<li page="' + i + '"><a href="javascript:;">' + i + '</a></li>';
        }
        if (tpage > maxshow && page < (tpage - 1) && tpage != max) {
            pagehtml += '<li class="plus-icon p-dots nobd"></li>';
            pagehtml += '<li page="' + tpage + '"><a href="javascript:;">' + tpage + '</a></li>';
        }
        pagehtml += '<li title="下一页" page="n"><a href="javascript:;" class="plus-icon p-right"></a></li>';
        pagehtml += pagesizeHtml;
        pagehtml += '<li class="nobd total">共<span>' + tsize + '</span>条</li>';
    }
    else {
        pagehtml += '<li class="disabled" page="p"><a href="javascript:;" class="plus-icon p-left"></a></li>';
        pagehtml += '<li class="active" page="1"><a href="javascript:;">1</a></li>';
        pagehtml += '<li class="disabled" page="n"><a href="javascript:;" class="plus-icon p-right"></a></li>';
        pagehtml += pagesizeHtml;
        pagehtml += '<li class="nobd total">共<span>0</span>条</li>';
    }
    return pagehtml;
}
/**
 * 生成分页html节点,并选中当前页
 * @param    datas.page    当前页码数
 * @param    datas.psize    当前每页显示条数
 * @param    datas.tpage    总页数
 * @param    datas.total    总记录条数
 */
function createPageList(datas)
{
    var pagelistNode;
    var tableNode = $(datas.select);
	var fixTableNode = tableNode.parents(".fixtable_tableLayout").first();
	if(fixTableNode.length > 0)
	{
		pagelistNode = fixTableNode.siblings('.pagelist_cls').find('.pagelist_ul_cls');
	}
	else
	{
		pagelistNode = tableNode.siblings('.pagelist_cls').find('.pagelist_ul_cls');
	}
	if(pagelistNode != undefined && pagelistNode.length > 0)
	{
		pagelistNode.attr("tpage", datas.tpage);
		pagelistNode.html(getPageListHtml(datas));
		pagelistNode.find("li[page='" + datas.page + "']").toggleClass("active", true);
		if(datas.page == 1)
		{
			pagelistNode.find("li[page='p']").toggleClass("disabled", true);
		}
		if(datas.page == datas.tpage) 
		{
			pagelistNode.find("li[page='n']").toggleClass("disabled", true);
		}
		pagelistNode.find(".pagination_page_size").val(datas.psize);
	}
}
/**
 * 点击页码(包括首尾页和上下页)
 * @param    linode    当前选中的节点
 * @param    fn        回调函数
 */
function clickpage(linode, fn)
{
    var pageno = linode.attr("page");
    var pagelistNode = linode.parents(".pagelist_ul_cls");
    if (pageno == "f")          //首页
    {
        pageno = 1;
    }
    else if (pageno == "l")     //尾页
    {
        pageno = pagelistNode.attr("tpage") * 1;
    }
    else if (pageno == "p")     //上一页
    {
        var current = pagelistNode.find("li[class*='active']").attr("page") * 1;
        if (current == 1) {
            return;
        }
        pageno = current - 1;
    }
    else if (pageno == "n")     //下一页
    {
        var current = pagelistNode.find("li[class*='active']").attr("page") * 1;
        var zpageno = pagelistNode.attr("tpage") * 1;
        if (current == zpageno) {
            return;
        }
        pageno = current + 1;
    }
    pagelistNode.find("li[page]").removeClass("active");
    pagelistNode.find("li[page='" + pageno + "']").addClass("active");
    fn.call(pageno + "");
}
/**
 * 表单提交验证
 * @param    select    选择器(默认取body)
 */
function validForm(select)
{
    var formnode = select == undefined ? $("body") : $(select);
    if (formnode.length > 0) {
        //执行非空校验
        var ftotal = 0;
        formnode.find("select[notEmpty]:visible").each(function () {

            var node = $(this);
            if (node.attr("notEmpty") != undefined && node.parents(".form-group").hasClass("has-error")) {
                ftotal++;
                return false;
            }

            if ($.trim($(this).val()) == "") {
                $(this).parents(".form-group").toggleClass("has-error",true);
                var pNode = $(this).parent().siblings("p");
                pNode.toggleClass("error-text",true).html("请至少选择一项.").show();
                ftotal++;
            }
        });
        formnode.find("input[notEmpty]:visible").each(function () {

            var node = $(this);
            if (node.attr("notEmpty") != undefined && node.parents(".form-group").hasClass("has-error")) {
                ftotal++;
                return false;
            }

            if ($.trim($(this).val()) == "") {
                $(this).parents(".form-group").toggleClass("has-error",true);
                var pNode = $(this).siblings("p");
                pNode = pNode.length > 0 ? pNode : $(this).parent("div").siblings("p");
                pNode.toggleClass("error-text",true).html("必填字段不能为空.").show();
                ftotal++;
            }
        });
        formnode.find("textarea[notEmpty]:visible").each(function () {

            var node = $(this);
            if (node.attr("notEmpty") != undefined && node.parents(".form-group").hasClass("has-error")) {
                ftotal++;
                return false;
            }

            if ($.trim($(this).val()) == "") {
                $(this).parents(".form-group").toggleClass("has-error",true);
                var pNode = $(this).siblings("p");
                pNode.toggleClass("error-text",true).html("必填字段不能为空.").show();
                ftotal++;
            }
        });
        //执行正则校验
        formnode.find("input[validRegex]:visible").each(function () {

            var node = $(this);
            if (node.attr("validRegex") != undefined && node.parents(".form-group").hasClass("has-error")) {
                ftotal++;
                return false;
            }

            var regexp = new RegExp($(this).attr("validRegex"));
            var result = regexp.test($(this).val());
            if (result == false) {
                $(this).parents(".form-group").toggleClass("has-error",true);
                var pNode = $(this).siblings("p");
                pNode = pNode.length > 0? pNode : $(this).parent("div").siblings("p");
                pNode.toggleClass("error-text",true).html("内容格式或长度不符合要求.").show();
                ftotal++;
            }
        });
        formnode.find("textarea[validRegex]:visible").each(function () {

            var node = $(this);
            if (node.attr("validRegex") != undefined && node.parents(".form-group").hasClass("has-error")) {
                ftotal++;
                return false;
            }

            var regexp = new RegExp($(this).attr("validRegex"));
            var result = regexp.test($(this).val());
            if (result == false) {
                $(this).parents(".form-group").toggleClass("has-error",true);
                var pNode = $(this).siblings("p");
                pNode.toggleClass("error-text",true).html("内容格式或长度不符合要求.").show();
                ftotal++;
            }
        });
        //执行自定义校验函数
        formnode.find("input[validFunc]:visible").each(function () {

            var node = $(this);
            if (node.attr("validFunc") != undefined && node.parents(".form-group").hasClass("has-error")) {
                ftotal++;
                return false;
            }

            var json = window[$(this).attr("validFunc")]($(this));
            if (json.dcode != 1000) {
                $(this).parents(".form-group").toggleClass("has-error",true);
                var node = $(this).siblings("p");
                node = node.length > 0? node : $(this).parent("div").siblings("p");
                node.toggleClass("error-text",true).html(json.dmsg).show();
                ftotal++;
            }
        });
        //执行给定URL的校验
        formnode.find("input[validUrl]:visible").each(function () {
            var node = $(this);
            if (node.attr("notEmpty") != undefined && node.parents(".form-group").hasClass("has-error") && node.attr("validUrlfalse") == undefined) {
                ftotal++;
                return false;
            }
            var data = new Object();
            data[node.attr("name")] = node.val();
            data.editId = formnode.find("input[editId]").val();
            var url = node.attr("validUrl");
            $.ajax({
                url: url,
                async: false,
                type: 'post',
                dataType: 'json',
                data: data,
                success: function (json) {
                    if (json.dcode != 1000) {
                        node.parents(".form-group").toggleClass("has-error",true);
                        node.attr("validUrlfalse",1);
                        node = node.siblings("p");
                        node.toggleClass("error-text",true).html(json.dmsg).show();
                        ftotal++;
                    }
                }
            });
        });
        //判断验证是否通过
        return ftotal > 0 ? false : true;
    }
    return true;
}
/**
 * 获取表格排序字段及排序方式
 * @param    select    选择器(默认为table标签下有sorter样式的div)
 * @return    sortstr    排序字符串
 */
function getsortstr(select)
{
	var sortnodes;
	if($("#fixtable_tableHeadClone").length > 0)
	{
		sortnodes = $("#fixtable_tableHeadClone").find("div.sorter");
	}
	else
	{
		if(select == undefined || $.trim(select) == "")
		{
            sortnodes = $("table div.sorter");
		}
		else
		{
            sortnodes = $(select).find("div.sorter");
		}
	}
    if (sortnodes.length > 0)
    {
    	var sortstr = '';
    	$.each(sortnodes, function ()
    	{
    		if ($.trim($(this).attr("sortstr")) != "")
    		{
    			sortstr += "," + $(this).attr("sortstr");
    		}
    	});
    	return sortstr.substring(1);
    }
    return null;
}
/**
 * 显示操作消息提示层
 * @param    json    待显示的数据对象(dcode:操作状态,1000表示操作成功 dmsg:提示消息)
 */
var alerthtml = '<div class="alert alert-dismissible warn-alert" role="alert" style="width:280px;">';
alerthtml += '<button type="button" class="close warn-alert-close" data-dismiss="alert">';
alerthtml += '<span aria-hidden="true" class="warn-alert-close">&times;</span><span class="sr-only">Close</span></button>';
alerthtml += '<strong class="warn-alert-message"></strong></div>';
function showoplayer(json)
{
	var node = $(alerthtml);
	if (json != undefined)
	{
		if (json.dmsg != undefined)
		{
			node.find(".warn-alert-message").html(json.dmsg);
		}
		if (json.dcode != 1000)
		{
			node.addClass("alert-danger");
		}
		else
		{
			node.addClass("alert-success");
		}
	}
	$('body').append(node);
	var showtime = json.showtime || 400;
	var delaytime = json.delaytime || 5000;
	var hidetime = json.hidetime || 400;
	node.fadeIn(showtime).delay(delaytime).fadeOut(hidetime, function ()
	{
		$(this).remove();
	});
}
/**
 * 检测服务器返回的json分页数据状态及内容,如果状态为未登录则跳至登录页,如果数据为空,则显示默认提示语
 * @param	json    待检测的json数据
 * @param	datas	json形式的参数对象(如:datas.select,选择器(默认取table标签,强烈建议传递该值以避免出现同一页面多个table时的数据混乱))
 * @return	boolean	布尔值,true表示检测通过,false表示检测不通过
 */
function checkPageDatas(json, datas)
{
    if (json != undefined && json.dcode == -1001)
    {
    	var pathname = window.document.location.pathname;
    	pathname = pathname.substring(1, pathname.substr(1).indexOf('/') + 1);
    	location.href = '/' + pathname + "/login.jsp";
    	return false;
    }
    if($(".mytable_fixed:visible").length > 0 && $(".modal.in").length == 0)
    {
    	if (json.datas.list == undefined || json.datas.list == null || json.datas.list.length == 0)
    	{
        	if($(".mytable_fixedHeaderColumn:visible div.col").length > 0)
        	{
        		$(".mytable_fixedColumn:visible").find("div.myfixedrow").remove();
        		$(".mytable_fixedColumn:visible").append('<div class="myfixedrow"><div class="col fixedcol"></div></div>');
        	}
        	$(".mytable_conentent:visible .table-right").find("div.myrow").remove();
        	$(".mytable_conentent:visible .table-right").append('<div class="myrow"><div class="col" style="color:red;font-weight:bold;font-size:13px;">' + (datas.dcode == -1002? datas.dmsg : '无符合条件的记录.') + '</div></div>');
        	return false;
    	}
    }
    else if($(".fixtable_tableData:visible").length > 0 && $(".modal.in").length == 0)
    {
    	if (json.datas.list == undefined || json.datas.list == null || json.datas.list.length == 0)
    	{
    		var tbnode = $(".fixtable_tableData:visible");
    		var trline = tbnode.find("th").length;
    		var html = '<tr>';
    		for(var i = 0; i < trline; i ++)
    		{
    			if(i == 0)
    			{
    				html += '<td align="center" style="color:red;font-weight:bold;font-size:13px;">无符合条件的记录.</td>';
    			}
    			else
    			{
    				html += '<td></td>';
    			}
    		}
    		html += '</tr>';
    		tbnode.find("tbody").html(html);
    		return false;
    	}
    }
    else
    {
    	var tbnode = datas.select == undefined ? (datas.pagelistNode == undefined ? $("table") : $(datas.pagelistNode).parent("nav").prev("table")) : $(datas.select);
        if (tbnode.length > 0)
        {
        	if (json.datas.list == undefined || json.datas.list == null || json.datas.list.length == 0)
        	{
        		var trline = tbnode.find("th").length;
        		var html = '<tr>';
        		for(var i = 0; i < trline; i ++)
        		{
        			if(i == 0)
        			{
        				html += '<td align="center" style="color:red;font-weight:bold;font-size:13px;">无符合条件的记录.</td>';
        			}
        			else
        			{
        				html += '<td></td>';
        			}
        		}
        		html += '</tr>';
        		tbnode.find("tbody").html(html);
        	}
        }
    }
    return true;
}
/**
 * 检测权限
 * @param	datas
 */
function checkAuthority(datas)
{
	var parentNode = $(datas.select);
	if(datas.select == undefined)
	{
		var fixtableNode = $(".mytable_fixed:visible");
		if(datas.pagelistNode == undefined)
		{
			parentNode = fixtableNode.length > 0? fixtableNode : $("table");
		}
		else
		{
			parentNode = fixtableNode.length > 0? $(datas.pagelistNode).parents(".mytable_fixed:visible") : $(datas.pagelistNode).parent("nav").prev("table");
		}
	}
	//校验表格区域-操作权限
	parentNode.find("[opauthority]").each(function()
	{
		if($(parent.pagemcodes).attr($(this).attr("opauthority")) == undefined)
		{
			$(this).remove();
		}
	});
	//校验表格区域-下钻权限
	parentNode.find("[dtauthority]").each(function()
	{
		if($(parent.pagemcodes).attr($(this).attr("dtauthority")) == undefined)
		{
            $(this).click(function (event)
            {
                event.preventDefault();
            });
		}
	});
	//移除多余的col-inner
	parentNode.find("div.col-inner").each(function()
	{
		if($.trim($(this).html()) == "")
		{
			$(this).removeClass("col-inner");
		}
	});
}
/**
 * 将形如{aa=3,bb=4}的字符串转换为json对象
 * @param    jsonobj    待转换的json字符串
 */
function parseYxJson(jsonobj)
{
    var json = new Object();
    if (jsonobj != undefined && jsonobj != "") {
        jsonobj = jsonobj.replace("{", "").replace("}", "");
        jsonobj = jsonobj.split(",");
        var jsonstr = "";
        $.each(jsonobj, function () {
            jsonstr += ",'" + $.trim(this.substring(0, this.indexOf("="))) + "':'" + this.substring(this.indexOf("=") + 1) + "'";
        });
        jsonstr = "{" + jsonstr.substring(1) + "}";
        json = eval('(' + jsonstr + ')');
    }
    return json;
}
/**
 * 分割日期范围字符串为数组
 * @param    datestr    待分隔的日期字符串
 */
function splitdate(datestr)
{
    return $.trim(datestr).split("至");
}
/**
 * 获取本月第一天的日期字符串
 * @param    date    日期对象
 */
function getMonthFirstDay(date)
{
    var datestr = date.getFullYear();
    var month = date.getMonth() + 1;
    datestr += "-" + (month > 9 ? month : ("0" + month));
    datestr += "-01";
    return datestr;
}
/**
 * 获取当前的日期字符串
 * @param    date    日期对象
 */
function getDateStr(date)
{
    var datestr = date.getFullYear();
    var month = date.getMonth() + 1;
    datestr += "-" + (month > 9 ? month : ("0" + month));
    var day = date.getDate() - 1;
    datestr += "-" + (day < 10 ? ("0" + day) : day);
    return datestr;
}
/**
 * 获取昨天的日期字符串
 * @param    date    日期对象
 */
function getLastDateStr(date)
{
    var datestr = date.getFullYear();
    var month = date.getMonth() + 1;
    datestr += "-" + (month > 9 ? month : ("0" + month));
    var day = date.getDate() - 1;
    datestr += "-" + (day < 10 ? ("0" + day) : day);
    return datestr;
}
/**
 * 千分位格式化
 * @param    value    待格式化的值
 */
function toThousands(value)
{
    return (value || 0).toString().replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,');
}
/**
 * 把数字转化为金钱(千分位 + 小数点(两位小数))
 * @param    value    待格式化的值
 */
function toMoney(value)
{
    value = isNaN(value) ? value : value.toFixed(2);
    return (value || 0).toString().replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,');
}
/**
 * 设置cookie
 * @param    name    存入cookie中的key
 * @param    value    存入cookie中的值
 */
function setCookie(name, value, option)
{
    var Days = 30;
    var exp = new Date();
    if (option != undefined && option.expires != undefined) {
        exp.setTime(exp.getTime() + option.expires);
        document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
    }
    else {
        document.cookie = name + "=" + escape(value);
    }
}
/**
 * 读取cookie
 * @param    name    cookie中的key
 */
function getCookie(name)
{
    var keys = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1, c.length);
        }
        if (c.indexOf(keys) == 0) {
            return unescape(c.substring(keys.length, c.length));
        }
    }
    return "";
}
/**
 * 删除指定cookie
 */
function removeCookie(name)
{
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cvalue = getCookie(name);
    if (cvalue != null && cvalue != undefined) {
        document.cookie = name + "=" + cvalue + ";expires=" + exp.toGMTString();
    }
}
//时间格式转换
function dateformat(time,format)
{
	var t = new Date(time);
	var tf = function(i){return (i < 10 ? '0' : '') + i};
	return format.replace(/yyyy|MM|dd|hh|mm|ss/g, function(a)
	{
		switch(a)
		{
		case 'yyyy':
			return tf(t.getFullYear());
			break;
		case 'MM':
			return tf(t.getMonth() + 1);
			break;
		case 'mm':
			return tf(t.getMinutes());
			break;
		case 'dd':
			return tf(t.getDate());
			break;
		case 'hh':
			return tf(t.getHours());
			break;
		case 'ss':
			return tf(t.getSeconds());
			break;
		}
	})
}
/**
 * 有固定列的列表设置
 */
$(window).resize(function(event)
{
	$('table[ufixtable_fixflag]').each(function()
	{
		$(this).fixTable({});
	});
});
