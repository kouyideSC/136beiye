
$(function() {		
	
	// 中间部分的列表移入移出
	$(document).on('mouseenter','.formcanvas-item',function(event) {
		$(this).addClass('formcanvas-item-hover');
	});
	$(document).on('mouseleave','.formcanvas-item',function(event) {
		$(this).removeClass('formcanvas-item-hover');
	});
	// 列表移除
	$(document).on('click','.formcanvas-item-close',function(event) {
		$(this).parents('.formcanvas-item').remove();
		if ($('.formcanvas-item').length == 0) {
			$('.formcanvas-main').addClass('empty');
		}
		if ($('.formcanvas-item-active').length == 0) {
			$('.formsettings-list').addClass('active').siblings().removeClass('active');
			$('.tabitem-form').addClass('active').siblings().removeClass('active');
		}
	});
	// 初始化表单
	$( ".widgetsitem" ).draggable({
      appendTo: "body",
      helper: "clone"
    });
    $( ".formcanvas-main" ).droppable({
        accept: ":not(.ui-sortable-helper)",
        drop: function( event, ui ) {
	        $( this ).removeClass('empty');
	        var clone = ui.draggable.clone();
	        if($(clone).attr('data-reactid')=='single-line-input'){
	            var source = "<div class='formcanvas-item' data-reactid='single-line-input' titles='单行输入框' mytips='请输入...'>";
	            source += "<label class='formcanvas-title componentview-label'>单行输入框</label>";
	            source += "<span class='formcanvas-content componentview-placeholder'>请输入...</span>";
	            source += "<a class='formcanvas-item-close'>&#10005</a>";
	            source += "</div>";
	            $(source).appendTo( this );
	        }else if ($(clone).attr('data-reactid')=='mtext') {
	        	var source = "<div class='formcanvas-item' data-reactid='mtext' titles='多行输入框' mytips='请输入...'>";
	            source += "<label class='formcanvas-title componentview-label'>多行输入框</label>";
	            source += "<span class='mtext-formcanvas-content componentview-placeholder'>请输入...</span>";
			    source += "<a class='formcanvas-item-close'>&#10005</a>";
			    source += "</div>";
			    $(source).appendTo( this );
			}else if ($(clone).attr('data-reactid')=='number-box') {
				var source = "<div class='formcanvas-item' data-reactid='number-box' titles='数字输入框' mytips='请输入...' unit=''>";
			    source += "<label class='formcanvas-title'><span class='componentview-label'>数字输入框</span><span class='label-unit'></span></label>";
			    source += "<span class='formcanvas-content componentview-placeholder'>请输入...</span>";
			    source += "<a class='formcanvas-item-close'>&#10005</a>";
			    source += "</div>";
			    $(source).appendTo( this );
			}else if ($(clone).attr('data-reactid')=='radio-box') {
				var source = "<div class='formcanvas-item' data-reactid='radio-box' titles='单选框' mytips='请选择' options='选项1,选项2,选项3'>";
			    source += "<label class='formcanvas-title componentview-label'>单选框</label>";
			    source += "<span class='radio-box-content componentview-placeholder p-r-10'>请选择</span>";
			    source += "<span class='arrow'>&#155</span>";
			    source += "<a class='formcanvas-item-close'>&#10005</a>";
			    source += "</div>";
			    $(source).appendTo( this );
			}else if($(clone).attr('data-reactid')=='multiple-select-box'){
				var source = "<div class='formcanvas-item' data-reactid='multiple-select-box' titles='多选框' options='选项1,选项2,选项3'>";
			    source += "<label class='formcanvas-title componentview-label'>多选框</label>";
			    source += "<span class='radio-box-content componentview-placeholder p-r-10'>请选择</span>";
			    source += "<span class='arrow'>&#155</span>";
			    source += "<a class='formcanvas-item-close'>&#10005</a>";
			    source += "</div>";
			    $(source).appendTo( this );
			}else if($(clone).attr('data-reactid')=='widgetsettings-date'){
				var source = "<div class='formcanvas-item' data-reactid='widgetsettings-date' titles='日期' date='年-月-日 时:分'>";
			    source += "<label class='formcanvas-title componentview-label'>日期</label>";
			    source += "<span class='radio-box-content componentview-placeholder p-r-10'>请选择</span>";
			    source += "<span class='arrow'>&#155</span>";
			    source += "<a class='formcanvas-item-close'>&#10005</a>";
			    source += "</div>";
			    $(source).appendTo( this );
			}else if($(clone).attr('data-reactid')=='range-date'){
				var source = "<div class='formcanvas-item' data-reactid='range-date' starttime='开始时间' endtime='结束时间' date='年-月-日 时:分' mytips='请选择'>";
				source += "<span class='formcanvas-item-row'>";
			    source += "<label class='formcanvas-title componentview-start-time'>开始时间</label>";
			    source += "<span class='radio-box-content componentview-placeholder p-r-10'>请选择</span>";
			    source += "<span class='arrow'>&#155</span>";
			    source += "</span>";
			    source += "<span class='formcanvas-item-row'>";
			    source += "<label class='formcanvas-title componentview-end-time'>结束时间</label>";
			    source += "<span class='radio-box-content componentview-placeholder p-r-10'>请选择</span>";
			    source += "<span class='arrow'>&#155</span>";
			    source += "</span>";
			    source += "<a class='formcanvas-item-close'>&#10005</a>";
			    source += "</div>";
			    $(source).appendTo( this );
			}else if($(clone).attr('data-reactid')=='widgetsettings-picture'){
				var source = "<div class='formcanvas-item' data-reactid='widgetsettings-picture' titles='图片'>";
			    source += "<label class='formcanvas-title componentview-label'>图片</label>";
			    source += "<span class='radio-box-content componentview-placeholder'><span class='plus-icon p-xiangji'></span></span>";
			    source += "<a class='formcanvas-item-close'>&#10005</a>";
			    source += "</div>";
			    $(source).appendTo( this );
			}else if($(clone).attr('data-reactid')=='widgetsettings-detail'){
				var source = "<div class='formcanvas-item' data-reactid='widgetsettings-detail' titles='明细' mytips='增加明细'>";
			    source += "<label class='formcanvas-title componentview-label'>明细</label>";
			    source += "<div class='componentview-area empty'>";
			    source += "<p class='emptytip'>可拖出多个组件（不包含明细组件）</p>";
			    source += "</div>";
			    source += "<div class='componentview-adddetail'><span class='plus-icon p-add'></span><span class='componentview-placeholder'>增加明细</span></div>";
			    source += "<a class='formcanvas-item-close'>&#10005</a>";
			    source += "</div>";
			    $(source).appendTo( this );
			}else if ($(clone).attr('data-reactid')=='widgetsettings-captioned') {
				var source = "<div class='formcanvas-item' data-reactid='widgetsettings-captioned' titles='请输入说明文字'>";
			    source += "<label class='formcanvas-title componentview-label explain'>请输入说明文字</label>";
			    source += "<a class='formcanvas-item-close'>&#10005</a>";
			    source += "</div>";
			    $(source).appendTo( this );
			}else if ($(clone).attr('data-reactid')=='widgetsettings-sum') {
				var source = "<div class='formcanvas-item' data-reactid='widgetsettings-sum' titles='金额（元）' mytips='请输入'>";
			    source += "<label class='formcanvas-title m-b-10 componentview-label'>金额（元）</label>";
			    source += "<span class='formcanvas-content componentview-placeholder'>请输入...</span>";
			    source += "<p class='note'>大写：壹万元整（示例）</p>";
			    source += "<a class='formcanvas-item-close'>&#10005</a>";
			    source += "</div>";
			    $(source).appendTo( this );
			}else if($(clone).attr('data-reactid')=='widgetsettings-attachment'){
				var source = "<div class='formcanvas-item' data-reactid='widgetsettings-attachment' titles='附件'>";
			    source += "<label class='formcanvas-title componentview-label'>附件</label>";
			    source += "<span class='radio-box-content componentview-placeholder'><span class='plus-icon p-fujian'></span></span>";
			    source += "<a class='formcanvas-item-close'>&#10005</a>";
			    source += "</div>";
			    $(source).appendTo( this );
			}else if($(clone).attr('data-reactid')=='widgetsettings-linkman'){
				var source = "<div class='formcanvas-item' data-reactid='widgetsettings-linkman' titles='联系人' contacter='只能选择一人' channel='0'>";
			    source += "<label class='formcanvas-title componentview-label'>联系人</label>";
			    source += "<span class='radio-box-content componentview-placeholder p-r-10'>请选择</span>";
			    source += "<span class='arrow'>&#155</span>";
			    source += "<a class='formcanvas-item-close'>&#10005</a>";
			    source += "</div>";
			    $(source).appendTo( this );
			}
			$('.formcanvas-item:last').addClass('formcanvas-item-active').siblings().removeClass('formcanvas-item-active');
			interflow();

			if ($('.formcanvas-item-active').attr("data-reactid") == "radio-box") {
                var ochannel = $('.radio-box .selected').val();
				$(".formcanvas-item-active").attr("channel",ochannel);
				optionData();
			}else if ($('.formcanvas-item-active').attr("data-reactid") == "multiple-select-box") {
                var ochannel = $('.multiple-select-box .selected').val();
                $(".formcanvas-item-active").attr("channel",ochannel);
				optionDataM();
			}



        }
    }).sortable({
        items: "div:not(.placeholder)"
        // placeholder: "ui-state-highlight"
    }).disableSelection();
    // 多选框选项添加、删除
    $(document).on('click','.action-del',function(){
		var itemLength = $('.setting-option-list').find('.option-item').length;
		// console.log(itemLength);
		if(itemLength > 1){
			$(this).parents('.option-item').remove();
		}else{
			$('.action-del').css('color','#ccc');
		}
		formOption();
	});
	$(document).on('click','.action-add',function(){
		var newOptionItem = '<div class="option-item">';
		newOptionItem += '<input type="text" value="选项"  class="option-input">';
		newOptionItem += '<a class="action action-del">-</a>';
		newOptionItem += '<a class="action action-add">+</a>';
		newOptionItem += '</div>';
		$(this).parents('.option-item').after(newOptionItem);
		var itemLength = $('.setting-option-list').find('.option-item').length;
		if(itemLength > 1){
			$('.action-del').css('color','#666');
		}
		formOption();
	});
	$(document).on('click','.action-del',function(){
		var itemLength = $('.setting-option-list-m').find('.option-item-m').length;
		// console.log(itemLength);
		if(itemLength > 1){
			$(this).parents('.option-item-m').remove();
		}else{
			$('.action-del').css('color','#ccc');
		}
		formOptionM();
	});
	$(document).on('click','.action-add',function(){
		var newOptionItem = '<div class="option-item-m">';
		newOptionItem += '<input type="text" value="选项"  class="option-input-m">';
		newOptionItem += '<a class="action action-del">-</a>';
		newOptionItem += '<a class="action action-add">+</a>';
		newOptionItem += '</div>';
		$(this).parents('.option-item-m').after(newOptionItem);
		var itemLength = $('.setting-option-list-m').find('.option-item-m').length;
		if(itemLength > 1){
			$('.action-del').css('color','#666');
		}
		formOptionM();
	});
    // 右侧tab切换
    $('.tabitem').on('click',function(){
		$(this).addClass('active').siblings().removeClass('active');
		var index = $(this).index() + 1;
		$('.widget-edit').children().eq(index).addClass('active').siblings().removeClass('active');
	});
    // 表单设置图标选项
	$('.iconitem').on('click',function(){
		$(this).addClass('active').siblings().removeClass('active');
	});
	// 展示部分的列表获取焦点
	$(document).on('mousedown','.formcanvas-item',function(event) {
		$(this).addClass('formcanvas-item-active').siblings().removeClass('formcanvas-item-active');
		interflow();
		if ($('.formcanvas-item-active').attr("data-reactid") == "radio-box") {
			optionData();
		}else if ($('.formcanvas-item-active').attr("data-reactid") == "multiple-select-box") {
			optionDataM();
		}
	});
	// 公用双向绑定初始化
	function interflow(){
		var dataReactid = $(".formcanvas-item-active").attr("data-reactid");
		$("." + dataReactid).parent('.widgetsettings-list').addClass('active').siblings().removeClass('active');
		$("." + dataReactid).show().siblings().hide();
		$('.tabitem-widget').addClass('active').siblings().removeClass('active');
		var otitles = $(".formcanvas-item-active").attr('titles');
		$('.form-title').val(otitles);
		var otips = $(".formcanvas-item-active").attr('myTips');
		$('.form-placeholder').val(otips);
		var ounit = $(".formcanvas-item-active").attr('unit');
		$('.unit').val(ounit);
		var ostart = $(".formcanvas-item-active").attr('startTime');
		$('.start-time').val(ostart);
		var oend = $(".formcanvas-item-active").attr('endTime');
		$('.end-time').val(oend);
		$('.checkbox-must').removeProp('checked');
		var omust = $(".formcanvas-item-active").attr('must');
		if (omust == "必填") {
			$('.checkbox-must').prop('checked',true);
		}else{
			$('.checkbox-must').prop('checked',false);
		}
		var odate = $(".formcanvas-item-active").attr('date');
		$('.type-date').find('input[type=radio]').removeProp('checked');
		if (odate == "年-月-日 时:分") {
			$('.type-date').children('p').eq(0).children('input[type=radio]').prop('checked',true);
			$('.type-date').children('p').eq(1).children('input[type=radio]').prop('checked',false);
			$('.type-date').children('p').eq(2).children('input[type=radio]').prop('checked',true);
			$('.type-date').children('p').eq(3).children('input[type=radio]').prop('checked',false);
		}else{
			$('.type-date').children('p').eq(1).children('input[type=radio]').prop('checked',true);
			$('.type-date').children('p').eq(0).children('input[type=radio]').prop('checked',false);
			$('.type-date').children('p').eq(3).children('input[type=radio]').prop('checked',true);
			$('.type-date').children('p').eq(2).children('input[type=radio]').prop('checked',false);
		}
		var contacts = $(".formcanvas-item-active").attr('contacts');
		$('.contacter').children('input[type=radio]').removeProp('checked');
		if (contacts == "只能选择一人") {
			$('.contacter').eq(0).children('input[type=radio]').prop('checked',false);
			$('.contacter').eq(1).children('input[type=radio]').prop('checked',true);
		}else{
			$('.contacter').eq(1).children('input[type=radio]').prop('checked',false);
			$('.contacter').eq(0).children('input[type=radio]').prop('checked',true);
		}


	}
    // 监听单选项变化
    function formOption(){
        var optionLength = $('.setting-option-list').find('.option-item').length;
        var arr = [];
        for (var i = 0; i < optionLength; i++) {
            var options = $('.setting-option-list').children('.option-item').eq(i).children('.option-input').val();
            arr.push(options);
        }
        $('.formcanvas-item-active').attr("options",arr);
    }
    $(document).on('input propertychange', '.option-input', function(event) {
        event.preventDefault();
        formOption();
    });
    // 监听多选项变化
    function formOptionM(){
        var optionLength = $('.setting-option-list-m').find('.option-item-m').length;
        var arr = [];
        for (var i = 0; i < optionLength; i++) {
            var options = $('.setting-option-list-m').children('.option-item-m').eq(i).children('.option-input-m').val();
            arr.push(options);
        }
        $('.formcanvas-item-active').attr("options",arr);
    }
    $(document).on('input propertychange', '.option-input-m', function(event) {
        event.preventDefault();
        formOptionM();
    });
	// 单选项双向绑定
	function optionData(){
        $('.contact-channel').children('option.selected').prop('selected', true);
        $(".setting-option-list").show();
        var selected = $(".radio-box .selected").attr('value');
        var ochannel = $(".formcanvas-item-active").attr('channel');
        if(ochannel == selected){
            var ooption = $(".formcanvas-item-active").attr('options');
            ooption = ooption.split(",");
            $('.setting-option-list').children().remove();
            var newOptionItem = '<div class="option-item">';
            newOptionItem += '<input type="text" value="选项"  class="option-input">';
            newOptionItem += '<a class="action action-del">-</a>';
            newOptionItem += '<a class="action action-add">+</a>';
            newOptionItem += '</div>';
            for (var j = 0; j < ooption.length; j++) {
                $('.setting-option-list').append(newOptionItem);
                $('.option-item').eq(j).children('.option-input').val(ooption[j]);
            }
		}else{
            $('.contact-channel').children("option[value='" + ochannel +"']").prop('selected', true);
            console.log($('option[value=ochannel]').text());
            $(".formcanvas-item-active").attr('options','');
            $(".setting-option-list").hide();
		}
	}


	// 多选项双向绑定
	function optionDataM(){
        $('.contact-channel:visible').children('option.selected').prop('selected', true);
        $(".setting-option-list-m").show();
        var selected = $(".multiple-select-box .selected").attr('value');
        var ochannel = $(".formcanvas-item-active").attr('channel');
        if(ochannel == selected){
            var ooption = $(".formcanvas-item-active").attr('options');
            ooption = ooption.split(",");
            $('.setting-option-list-m').children().remove();
            var newOptionItem = '<div class="option-item-m">';
            	newOptionItem += '<input type="text" value="选项"  class="option-input-m">';
            	newOptionItem += '<a class="action action-del">-</a>';
            	newOptionItem += '<a class="action action-add">+</a>';
            	newOptionItem += '</div>';
            for (var j = 0; j < ooption.length; j++) {
            	$('.setting-option-list-m').append(newOptionItem);
            	$('.option-item-m').eq(j).children('.option-input-m').val(ooption[j]);
            }
        }else{
            $('.contact-channel:visible').children("option[value='" + ochannel +"']").prop('selected', true);
            $(".formcanvas-item-active").attr('options','');
            $(".setting-option-list-m").hide();
        }
	}

	// 监听标题变化
    $(document).on('input propertychange','.form-title',function(){
		var formTitle = $(this).val();
		$('.formcanvas-item-active').attr('titles',formTitle);
		$('.componentview-label').html(function(){
			return $(this).parents(".formcanvas-item").attr('titles');
		});
	});
	// 监听提示变化
	$(document).on('input propertychange','.form-placeholder',function(){
		var formPlaceholder = $(this).val();
		$('.formcanvas-item-active').attr('myTips',formPlaceholder);
		$('.componentview-placeholder').html(function(){
			return $(this).parents(".formcanvas-item").attr('myTips');
		});
	});
	// 数字输入框单位
	$(document).on('input propertychange','.number-box .unit',function(){
		var unit = $(this).val();
		$('.formcanvas-item-active').attr('unit',unit);
		$('.label-unit').html(function(){
			return "（" + $(this).parents(".formcanvas-item").attr('unit') + "）";
		})
		
	});

	// 监听日期区间
	$(document).on('input propertychange','.start-time',function(){
		var startTime = $(this).val();
		$('.formcanvas-item-active').attr('startTime',startTime);
		$('.componentview-start-time').html(function(){
			return $(this).parents('.formcanvas-item').attr('startTime');
		})
	});
	$(document).on('input propertychange','.end-time',function(){
		var endTime = $(this).val();
		$('.formcanvas-item-active').attr('endTime',endTime);
		$('.componentview-end-time').html(function(){
			return $(this).parents('.formcanvas-item').attr('endTime');
		});
	});
	// 日期类型
	$('.type-date').find("input[type=radio]").on('click',function(event) {
		$(this).attr('checked','checked').parent('p').siblings().find('input[type=radio]').removeAttr('checked');
		$('.formcanvas-item-active').attr("date",$(this).parent().text());
	});
	// 联系人
	$('.contacter').children("input[type=radio]").on('click',function(event) {
		$(this).attr('checked','checked').parent('.contacter').siblings().find('input[type=radio]').removeAttr('checked');
		$('.formcanvas-item-active').attr("contacts",$(this).parent().text());
	});
	// 必填项
	$('.checkbox-must').on('click',function(){
		if ($(this).prop("checked") == true) {
			$(this).attr("checked",true);
			$('.formcanvas-item-active .componentview-placeholder').after("<span class='placeholder-must'>（必填）</span>");
			$('.formcanvas-item-active').attr("must","必填");
		}else{
			$(this).attr("checked",false);
			$('.formcanvas-item-active .componentview-placeholder').next().remove();
			$('.formcanvas-item-active').removeAttr('must');
		}
	});

	// 联系人渠道
	$(".contact-channel").change(function(){
	    var channelVal = $(this).val();
        var selected = $(".selected").attr('value');
        if(channelVal == selected){
            $(this).siblings(".setting-option-list").show();
            $(this).siblings(".setting-option-list-m").show();
            $(".formcanvas-item-active").attr('options','选项1,选项2,选项3');
            var ooption = $(".formcanvas-item-active").attr('options');
            ooption = ooption.split(",");
        	if($(this).next().attr("class") == "setting-option-list"){
                $('.setting-option-list').children().remove();
                var newOptionItem = '<div class="option-item">';
                newOptionItem += '<input type="text" value="选项"  class="option-input">';
                newOptionItem += '<a class="action action-del">-</a>';
                newOptionItem += '<a class="action action-add">+</a>';
                newOptionItem += '</div>';
                for (var j = 0; j < ooption.length; j++) {
                    $('.setting-option-list').append(newOptionItem);
                    $('.option-item').eq(j).children('.option-input').val(ooption[j]);
                }
			}else if($(this).next().attr("class") == "setting-option-list-m"){
                $('.setting-option-list-m').children().remove();
                var newOptionItem = '<div class="option-item-m">';
                	newOptionItem += '<input type="text" value="选项"  class="option-input-m">';
                	newOptionItem += '<a class="action action-del">-</a>';
                	newOptionItem += '<a class="action action-add">+</a>';
                	newOptionItem += '</div>';
                for (var k = 0; k < ooption.length; k++) {
                	$('.setting-option-list-m').append(newOptionItem);
                	$('.option-item-m').eq(k).children('.option-input-m').val(ooption[k]);
                }
			}
            $('.formcanvas-item-active').attr("channel",channelVal);
		}else{
            $(this).siblings(".setting-option-list").hide();
            $(this).siblings(".setting-option-list-m").hide();
            $('.formcanvas-item-active').attr("channel",channelVal);
            $(".formcanvas-item-active").attr('options','');
		}

	});
});
