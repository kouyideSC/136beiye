$(function () {
	//禁止点击空白处关闭模态框
	$("div.modal").attr("data-backdrop","static");
	//
    $('.tab-professional-status li').click(function () {
        $(this).addClass('active').siblings().removeClass('active');
    });
    //表格高级筛选
    $('.show-more').on('click', function () {
        $(this).hide();
        $('.show-off').show();
        $('.advanced').show();
        $(this).parents('div.whitebox').find('div.fixtable_tableData table').each(function()
        {
            $(this).fixTable();
        });
    });
    $('.show-off').on('click', function () {
        $(this).hide();
        $('.show-more').show();
        $('.advanced').hide();
        $(this).parents('div.whitebox').find('div.fixtable_tableData table').each(function()
        {
            $(this).fixTable();
        });
    });
    // 表格编辑(表格列显示/隐藏的设置)
	$(document).on('click','.list-box li',function () {
		$(this).addClass('active').siblings().removeClass('active');
	});
	$(document).on('click','.add',function () {
		var hidecon = $('.list-box-hide').children('.active');
		hidecon.appendTo($('.list-box-show'));
		$('.list-box-show').children().removeClass('active');
	});
	$(document).on('click','.subtract',function () {
		var showcon = $('.list-box-show').children('.active');
		showcon.appendTo($('.list-box-hide'));
		$('.list-box-hide').children().removeClass('active');
	});
	//保存表格列显示/隐藏的设置
	$(document).on('click','.save_scolumn_setting',function ()
	{
		var pnode = $(this).parents(".modal").first();
		pnode.modal('hide');
		var hidelist = "";
		var showlist = "";
		if($(".mytable_fixed").length > 0)
		{
			$('.list-box-hide li').each(function()
			{
				hidelist += "," + $(this).attr("scolumn") + "-" + $(this).text();
				$(".mytable_header div[scolumn='" + $(this).attr("scolumn") + "']").removeClass("headercol").hide();
				$(".mytable_conentent div[scolumn='" + $(this).attr("scolumn") + "']").hide();
			});
			$('.list-box-show li').each(function()
			{
				showlist += "," + $(this).attr("scolumn") + "-" + $(this).text();
				$(".mytable_header div[scolumn='" + $(this).attr("scolumn") + "']").toggleClass("headercol",true).show();
				$(".mytable_conentent div[scolumn='" + $(this).attr("scolumn") + "']").show();
			});
		}
		else if($(".fixtable_tableLayout").length > 0)
		{
			pnode.find(".list-box-hide li").each(function()
			{
				hidelist += "," + $(this).attr("scolumn") + "-" + $(this).text();
				$("th[scolumn='" + $(this).attr("scolumn") + "']").hide();
				$("td[scolumn='" + $(this).attr("scolumn") + "']").hide();
			});
			pnode.find(".list-box-show li").each(function()
			{
				showlist += "," + $(this).attr("scolumn") + "-" + $(this).text();
				$("th[scolumn='" + $(this).attr("scolumn") + "']").show();
				$("td[scolumn='" + $(this).attr("scolumn") + "']").show();
			});
		}
		else
		{
			$('.list-box-hide li').each(function()
			{
				hidelist += "," + $(this).attr("scolumn") + "-" + $(this).text();;
				$("th[scolumn='" + $(this).attr("scolumn") + "']").hide();
				$("td[scolumn='" + $(this).attr("scolumn") + "']").hide();
			});
			$('.list-box-show li').each(function()
			{
				showlist += "," + $(this).attr("scolumn") + "-" + $(this).text();;
				$("th[scolumn='" + $(this).attr("scolumn") + "']").show();
				$("td[scolumn='" + $(this).attr("scolumn") + "']").show();
			});
		}
		//在cookie中保存设置
		var mcode = $("#navtabs_tabslist li[mcode].active",parent.document).attr("mcode");
		setCookie((mcode + "_table_hide_columns"),hidelist.length >= 1? hidelist.substring(1) : "");
		setCookie((mcode + "_table_show_columns"),showlist.length >= 1? showlist.substring(1) : "");
		//FixTable(fixTableDatas);
        $('table[ufixtable_fixflag]').each(function()
        {
            $(this).fixTable({});
        });
	});
    //分页
    $('.paginationer li a').click(function () {
        $(this).parent().addClass('active').siblings().removeClass('active');
    });
    $('#saveSetting').click(function () {
        var j = $('.list-box-hide').children('li').length;
        for (var i = 0; i < j; i++) {
            var o = $('.list-box-hide').children('li').eq(i - 1).attr('class');
            $('.fht-fixed-body').find($("." + o)).hide();
            $('.fht-fixed-column').find($("." + o)).hide();

        }

    });
    $("[data-toggle='tooltip']").tooltip();
    $('.p-double-left').on('click', function () {
        $('#sidebar').addClass('short');
        $('#nav').addClass('short');
        $('.wrap').css('left', 60);
    });

    $('.p-double-right').on('click', function () {
        $('#sidebar').removeClass('short');
        $('#nav').removeClass('short');
        $('.wrap').css('left', 220);
    });

    // select
    $(document).on('click', '.dropdown-menu li', function () {
        var con = $(this).html();
        var opval = $(this).attr('opvalue');
        $(this).parent().siblings('.dropdown-toggle').children('.content').html(con);
        $(this).parent().siblings('.dropdown-toggle').attr('opvalue', opval);
    });
    //弹层模态框
    $("[data-toggle='popup']").on("click", function () {
        var datas = new Object();
        datas.dataurl = $(this).attr("data-url");
        window.parent.showPopupIframe(datas);
        //var target = $($(this).attr("data-target"));
        //$('#' + data.iframeid,window.parent.document).attr("src",$(this).attr("data-url"));
        //alert(target.prop('outerHTML'));
        //$('#' + data.iframeid,window.parent.document).html(target.prop('outerHTML'));
        //window.parent.showPopupIframe("#" + data.id);
        /*$('.modal-popup', window.parent.document).show();
         var targetID = $(this).attr('data-target');
         $(targetID, window.parent.document).show();

         $("[data-dismiss='popup']", window.parent.document).on("click",function(){
         $(".modal-popup", window.parent.document).hide();
         $(targetID, window.parent.document).hide();
         });*/
    });
    //自定义模态框-关闭
    $("[data-dismiss='popup']").on("click", function () {
        $("#popup_main_div", window.parent.document).hide();
        $("#popup_layer_div", window.parent.document).hide();
    });
    //当模态窗对用户可见时触发(渲染完毕)
    $(document).on("shown.bs.modal","div.modal",function(){
    	var dialogNode = $(this).find(".modal-dialog").slice(0,1);
    	dialogNode.appendTo($(this).find(".modal-backdrop").slice(0,1));
    });
    //模态窗调用hide实例时触发
    $(document).on("hide.bs.modal","div.modal",function()
    {
    	if($(this).attr("useStaticDialog") == "1")
    	{
    		var dialogNode = $(this).find(".modal-dialog").slice(0,1);
        	var that = $(this);
        	setTimeout(function(){
        		dialogNode.appendTo(that);
        	},300);
    	}
    });
    //表格hover效果
    $(document).on('mouseover','.myrow',function(){
		var i = $(this).index();
        $(this).addClass('active');
        $('.myfixedrow').eq(i).addClass('active');
	 });
    $(document).on('mouseout','.myrow',function(){
		var i = $(this).index();
        $(this).removeClass('active');
        $('.myfixedrow').eq(i).removeClass('active');
    })
   
    $(document).on('mouseover','.myfixedrow',function(){
		var i = $(this).index();
        $(this).addClass('active');
        $('.myrow').eq(i).addClass('active');
	 });
    $(document).on('mouseout','.myfixedrow',function(){
		var i = $(this).index();
        $(this).removeClass('active');
        $('.myrow').eq(i).removeClass('active');
    })
    
    $(document).on('mouseover','.mytable_conentent .col',function(){
        $(this).toggleClass('hovercol');
    });
    $(document).on('mouseout','.mytable_conentent .col',function(){
        $(this).toggleClass('hovercol');
    })
    
    // focus
    $(document).on("click",".card-info-item",function(e){
        $(this).find('.card-info-input').eq(0).focus();
    });
    $(document).on("focus",".card-info-input",function(e){
        $(this).addClass('card-info-input-focus');
    });
    $(document).on("blur",".card-info-input",function(e){
        $(this).removeClass('card-info-input-focus');
    });
    $(document).on("click",".listpreview",function(e){
        $(this).parent().addClass('packdown');
    });
    $(document).on("click",".packup",function(e){
        $(this).parent().removeClass('packdown');
    });
    
    //卡片关闭
    $(document).on("click",".card-close",function(e){
        var $parentNode = $(this).parents('.card-wrap').slice(0,1);
        $parentNode.removeClass('card-wrap-show');
        $parentNode.parent('.card-wrap-show').css('overflow-y','auto');
        if($parentNode.attr("callback") && $parentNode.attr("callback") != '')
        {
            window[$parentNode.attr("callback")]();
        }

    });
    //表格区域下拉操作按钮-样式自适应
    $(document).on("click","table [data-toggle='dropdown']",function()
    {
        var czheight = $(this).parents(".core-table").find(".pagelist_cls").offset().top - ($(this).offset().top + 40) - 17 - 2;//滚动条位置所在的top
    	var ulheight = $(this).next("ul.dropdown-menu").height();//ul菜单内容的高度
    	if(ulheight > czheight)
    	{
    		$(this).parents("div.btn-group").toggleClass("dropup",true);//当前操作按钮到滚动条的距离<ul菜单内容的高度,则ul菜单往上弹
    	}
    	else
    	{
    		$(this).parents("div.btn-group").removeClass("dropup");//当前操作按钮到滚动条的距离>ul菜单内容的高度,则ul菜单往下弹
    	}
    });
});
