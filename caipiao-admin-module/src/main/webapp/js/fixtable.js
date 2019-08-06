/**
 * 固定列表格效果 version 1.0.0
 * @author 	sjq
 */
!(function($){
	//定义固定列表格对象
	var FixTable = function(ele,opt,datas)
	{
		this.$element = ele;//选择器
		//默认参数
		this.defaults = {
			fixType : 1,//固定类型(1-动态模式,适用于表格的高度/宽度非固定值;2-弹窗模式  3-卡片模式)
		};
		this.options = $.extend({},this.defaults,eval('(' + this.$element.attr('fixoption') + ')'),opt);//合并参数项
		this.options.fixcmber = this.options.fixcmber? this.options.fixcmber : (datas? datas.fixcmber : undefined);
		this.datas = datas;//数据项
		this.$element.attr('fixoption',JSON.stringify(this.options));
		this.pmcode = $("#navtabs_tabslist li[mcode].active",parent.document).attr("mcode");//当前菜单项
		this.hidelist = getCookie(this.pmcode + "_table_hide_columns");//获取表格隐藏的列
		if($.trim(this.hidelist) != "")
		{
			$.each(this.hidelist.split(","),function(i,m)
			{
				m = m.split("-");
				ele.find('th[scolumn="' + m[0] + '"]').hide();
				ele.find('td[scolumn="' + m[0] + '"]').hide();
			});
		}
	};
	//扩展固定列表格对象属性及方法
	FixTable.prototype =
	{
		//初始化
		init : function() 
		{
			//固定类型-动态模式
			var fixflag = new Date().getTime();
			if(this.options.fixType == 1)
			{
				var that = this;
				this.$whiteboxNode = $('.whitebox:visible');//页面整个区域
				this.$whiteboxNode = this.$whiteboxNode.length > 0? this.$whiteboxNode : $('body');
				this.$statusOptionsNode = $('.status-options:visible');//筛选条件区域
				this.$coreTableNode = this.$element.parents('.core-table').first();//表格区域
				this.$coreNavNode = $('.core-nav:visible');
				if(this.$coreTableNode.length > 0)
				{
					//初始化固定列表格的外层div
					if(this.$element.attr("ufixtable_fixflag") != undefined)
				    {
						fixflag = this.$element.attr("ufixtable_fixflag");
						this.$fixTableLayout = $("#fixtable_tableLayout_" + fixflag);
						this.$fixTableLayout.before(this.$element);
						this.$fixTableLayout.empty();
					} 
				    else
				    {
				    	this.$fixTableLayout = $('<div class="fixtable_tableLayout" id="fixtable_tableLayout_' + fixflag + '"></div>');
				    	this.$element.after(this.$fixTableLayout);
				    	this.$element.addClass('ufixtable_init_cls').attr('ufixtable_fixflag',fixflag);//给原始表格添加fix标识
				    	this.isFirstInit = 1;//标识为第一次初始化
				    }
					
					//获取表格区域的实际宽度和高度
					this.$tableDataAmountNode = this.$fixTableLayout.next('.table-data-amount');
					var width = this.$coreTableNode.width() + 17;
					var height = this.$whiteboxNode.height() - 58 
								- (this.$statusOptionsNode.length > 0? this.$statusOptionsNode.height() + 36 : 0) 
								- 50
								- (this.$tableDataAmountNode.length > 0? this.$tableDataAmountNode.height() : 0);
					//初始化固定列表格
					var coreTableWidth = this.$coreTableNode.width();
					this.$element.css('min-width',coreTableWidth);
				    this.$fixTableFix = $('<div class="fixtable_tableFix fixtable_tableFix_' + fixflag + '"></div>');//固定列表头区域
				    this.$fixTableHead = $('<div class="fixtable_tableHead" id="fixtable_tableHead_' + fixflag + '"></div>');//非固定列表头区域
				    this.$fixTableColumn = $('<div class="fixtable_tableColumn" id="fixtable_tableColumn_' + fixflag + '"></div>');//固定列数据区域
				    this.$fixTableData = $('<div class="fixtable_tableData" id="fixtable_tableData_' + fixflag + '"></div>');//非固定列数据区域
				    this.$fixTableLayout.append(this.$fixTableFix).append(this.$fixTableHead).append(this.$fixTableColumn).append(this.$fixTableData);
				    this.$fixTableLayout.height(height);//设置固定列表格的外层div高度
				    
				    //渲染原始表格
				    this.$element.find('.blankCol').remove();//移除已有的空白行
					var tableConHeight = height - 52 - (this.$fixTableData.width() < this.$element.width()? 17 : 0);
					var lineNum = parseInt(tableConHeight / 40);//现有表格的行高
					var myrowLength = this.$element.find('tbody').children('tr').length;//现有表格的行数
					var mycolumnNum = this.$element.find('tbody').children('tr').eq(0).children('td:visible').length;//现有表格的列数
					var blankLength = lineNum - myrowLength;//现有表格实际剩余空白高度
					var lastHeight = tableConHeight % 40;
					var html = '';
					for(var i = 0; i < blankLength + 1; i ++)
					{
						html += '<tr class="blankCol" style="height:40px;">';
						for(var j = 0; j < mycolumnNum; j ++)
			 	        {
							html += '<td></td>';
						}
						html += '</tr>';
					}
					this.$element.find('tbody').append(html);//追加空白行
					
					//设置原始表格的样式
					this.$element.find('tbody').find('tr').last().height(lastHeight);
					this.$element.find('tbody').find('tr').children('td').css('height','40px');
					this.$element.find('tbody').find('tr').last().children('td').height(lastHeight);
				    
				    //初始化固定列表格的固定列区域表头
				    var oldtable = this.$element;
				    var tableFixClone = oldtable.clone(true);
				    tableFixClone.attr('id','fixtable_tableFixClone_' + fixflag).addClass('fixtable_tableFixClone').removeAttr('ufixtable_fixflag').find('tbody').removeAttr('id');
				    this.$fixTableFix.append(tableFixClone);
				    
				    //初始化固定列表格的非固定列区域表头
				    var tableHeadClone = oldtable.clone(true);
				    tableHeadClone.attr('id','fixtable_tableHeadClone_' + fixflag).addClass('fixtable_tableHeadClone').removeAttr('ufixtable_fixflag').find('tbody').removeAttr('id');
				    this.$fixTableHead.append(tableHeadClone);
				    
				    //初始化固定列表格的固定列区域数据
				    var tableColumnClone = oldtable.clone(true);
				    tableColumnClone.attr('id','fixtable_tableColumnClone_' + fixflag).addClass('fixtable_tableColumnClone').removeAttr("ufixtable_fixflag").find('tbody').removeAttr('id');
				    this.$fixTableColumn.append(tableColumnClone);
				    
				    //初始化固定列表格的非固定列区域数据
				    this.$fixTableData.append(oldtable);
				    this.$fixTableLayout.find('table').each(function ()
				    {
				    	$(this).css('margin','0');
				    });
				    
				    //设置固定列表格样式
				    var headHeight = this.$fixTableHead.find("thead").height() + 2;
				    this.$fixTableHead.css('height',headHeight);
				    this.$fixTableFix.css('height',headHeight);
				    var columnsWidth = 0;
				    var columnsNumber = 0;
				    if(this.options.fixcmber)
				    {
				    	this.$fixTableColumn.find('tr:last td:lt(' + this.options.fixcmber + ')').each(function ()
				    	{
				    		columnsWidth += $(this).outerWidth(true);
				    		columnsNumber ++;
				    	});
				    }
				    columnsWidth += 2;
				    this.$fixTableColumn.css('width',columnsWidth);
				    this.$fixTableFix.css('width',columnsWidth);
				    
				    this.$fixTableLayout.css({width:width - 17,height:height,overflow:'hidden'});
				    this.$fixTableFix.css({'overflow':'hidden','position':'relative','z-index':'50','background-color':'#fafdff'});
				    this.$fixTableHead.css({'overflow':'hidden','width':(width - 17),'position':'relative','z-index':'45','background-color':'#fafdff'});
				    this.$fixTableColumn.css({'overflow':'hidden','height':(height - 17),'position':'relative','z-index':'40','background-color':'#fff'});
				    this.$fixTableColumn.find('td').css({'border-right':'1px solid #e8ebee','box-sizing':'border-box','height':'40px'});
				    this.$fixTableData.css({'overflow':'auto','width':width,'height':height,'position':'relative','z-index':'35','border-bottom':'1px solid #e8ebee'});
				    if(this.$fixTableColumn.height() > this.$fixTableColumn.find("table").height())
				    {
				    	this.$fixTableColumn.css('height',this.$fixTableColumn.find('table').height());
				    	this.$fixTableData.css('height',this.$fixTableColumn.find('table').height() + 17);
				    }
				    if(this.$fixTableData.width() > this.$fixTableLayout.width()) 
				    {
				    	this.$fixTableData.css('width',this.$fixTableLayout.width());
				    	this.$fixTableHead.css('width',this.$fixTableLayout.width() - 17);
				    }
				    if(this.$fixTableData.find('table').height() > this.$fixTableData.height())
				    {
				    	this.$coreTableNode.find('.edit-item').css('right','17px');
				    }
				    else
				    {
				    	this.$coreTableNode.find('.edit-item').css('right',0);
				    }
				    this.$fixTableFix.offset(this.$fixTableLayout.offset());
				    this.$fixTableHead.offset(this.$fixTableLayout.offset());
				    this.$fixTableColumn.offset(this.$fixTableLayout.offset());
				    this.$fixTableData.offset(this.$fixTableLayout.offset());
				    
				    //固定列表格滚动
				    this.$fixTableData.scroll(function()
			    	{
				    	that.$fixTableHead.scrollLeft(that.$fixTableData.scrollLeft());
				    	that.$fixTableColumn.scrollTop(that.$fixTableData.scrollTop());
				    });
			    	//鼠标划入划出效果
				    this.$fixTableColumn.find('tr').mouseover(function(event)
				    {
				    	var i = $(this).index();
				    	$(this).addClass('active');
				    	that.$element.find('tr').eq(i + 1).addClass('active');
				    }).mouseout(function(event) 
				    {
				    	var i = $(this).index();
				    	$(this).removeClass('active');
				    	that.$element.find('tr').eq(i + 1).removeClass('active');
				    });
				    this.$element.find('tr').mouseover(function(event)
				    {
				    	var i = $(this).index();
				    	$(this).addClass('active');
				    	that.$fixTableColumn.find('tr').eq(i + 1).addClass('active');
				    }).mouseout(function(event)
				    {
				    	var i = $(this).index();
				    	$(this).removeClass('active');
				    	that.$fixTableColumn.find('tr').eq(i + 1).removeClass('active');
				    });
				    //设置固定列表格的单元格显示更多的效果
				    this.$fixTableLayout.find('.col-inner').on('mouseover',function(e)
				    {
				    	var t = e.target;
				    	var X = t.parentNode.offsetLeft;
				    	var Y = t.parentNode.offsetTop;
				    	var W = t.parentNode.offsetWidth;
				    	var optionsH = that.$statusOptionsNode.length > 0? (that.$statusOptionsNode.height() + 36) : 0;
					    var tooltipsNode = $('.tooltips');
					    var tableH = that.$fixTableData.height();
					    var coreNavH = 160;
				    	tooltipsNode.html(t.innerHTML).stop().fadeIn(500).css({
				    		'top':Y + 58 +15 + 40 + optionsH,
				    		'left':X + 15 + coreNavH,
				    		'width': W
				    	});
				    	var tipH = $('.tooltips:visible').height() + 40;
				    	var tolH = tipH + Y + 50;
				    	if(tolH > tableH)
				    	{
				    		tooltipsNode.css({'top': Y + 58 +15 - tipH + optionsH});
				    		tooltipsNode.addClass('bottomTips');
				    	}
				    	else
				    	{
				    		tooltipsNode.removeClass('bottomTips');
				    	}
				    });
                    //设置固定列表格的单元格显示更多的效果
                    this.$fixTableLayout.find('.triangular-mark').on('mouseover',function(e)
                    {
                        var t = e.target;
                        var X = t.offsetLeft;
                        var Y = t.offsetTop;
                        var W = t.offsetWidth;
                        var optionsH = that.$statusOptionsNode.length > 0? (that.$statusOptionsNode.height() + 36) : 0;
                        var tooltipsNode = $('.mark-tips');
                        var tableH = that.$fixTableData.height();
                        var coreNavH = that.$coreNavNode.length > 0? 300 : 0;
                        var con = $(this).attr("floatvalue");
                        var html = con;
                        if(html == undefined || html == ''){
                            html = '无开票和回款信息';
                        }
                        tooltipsNode.html(html).stop().fadeIn(500).css({
                            'top':Y + 58 +15 + 40 + optionsH,
                            'left':X + 15 + coreNavH,
                            'width': W
                        });
                        var tipH = $('.tooltips:visible').height() + 40;
                        var tolH = tipH + Y + 50;
                        if(tolH > tableH)
                        {
                            tooltipsNode.css({'top': Y + 58 +15 - tipH + optionsH});
                            tooltipsNode.addClass('bottomTips');
                        }
                        else
                        {
                            tooltipsNode.removeClass('bottomTips');
                        }
                    });
				    //设置固定列表格的非固定列区域的滚动效果
				    this.$fixTableData.on('scroll',function()
				    {
				    	var scrollT = $(this).scrollTop();
				    	var scrollL = $(this).scrollLeft();
				    	var tooltipsNode = $('.tooltips');
				    	var tableH = that.$fixTableData.height();
				    	var optionsH = that.$statusOptionsNode.length > 0? (that.$statusOptionsNode.height() + 36) : 0;
				    	var coreNavH = that.$coreNavNode.length > 0? 300 : 0;
				    	that.$fixTableLayout.find('.col-inner').on('mouseover',function(e)
						{
				    		var t = e.target;
				    		var X = t.parentNode.offsetLeft;
				    		var Y = t.parentNode.offsetTop;
				    		var W = t.parentNode.offsetWidth;
				    		tooltipsNode.html(t.innerHTML).stop().fadeIn(500).css({
				    			'top':Y + 58 +15 + 40 + optionsH - scrollT,
				    			'left':X + 15 - scrollL + coreNavH,
				    			'width': W
							});
				    		var tipH = $('.tooltips:visible').height() + 40;
							var tolH = tipH + Y;
							if(tolH > tableH)
							{
								$('.tooltips').css({'top': Y + 58 +15 - tipH + optionsH - scrollT});
								$('.tooltips').addClass('bottomTips');
							}
							else
							{
								$('.tooltips').removeClass('bottomTips');
							}
						});
				    });
                    this.$fixTableData.on('scroll',function()
                    {
                        var scrollT = $(this).scrollTop();
                        var scrollL = $(this).scrollLeft();
                        var tooltipsNode = $('.mark-tips');
                        var tableH = that.$fixTableData.height();
                        var optionsH = that.$statusOptionsNode.length > 0? (that.$statusOptionsNode.height() + 36) : 0;
                        var coreNavH = 160;
                        that.$fixTableLayout.find('.triangular-mark').on('mouseover',function(e)
                        {
                            var t = e.target;
                            var X = t.offsetLeft;
                            var Y = t.offsetTop;
                            var W = t.offsetWidth;
                            var con = $(this).attr("floatvalue");
                            var html = con;
                            tooltipsNode.html(html).stop().fadeIn(500).css({
                                'top':Y + 58 +15 + 40 + optionsH - scrollT,
                                'left':X + 15 - scrollL + coreNavH,
                                'width': W
                            });
                            var tipH = $('.tooltips:visible').height() + 40;
                            var tolH = tipH + Y;
                            if(tolH > tableH)
                            {
                                $('.tooltips').css({'top': Y + 58 +15 - tipH + optionsH - scrollT});
                                $('.tooltips').addClass('bottomTips');
                            }
                            else
                            {
                                $('.tooltips').removeClass('bottomTips');
                            }
                        });
                    });
                    this.$fixTableLayout.find('.triangular-mark').on('mouseout',function(e)
					{
                    	$(".mark-tips").empty().hide();
                    });
				}
			}
			//固定类型-静态模式
			else if(this.options.fixType == 2)
			{
			}
			return this;
		},
		//渲染原始表格
		renderSourceTable : function()
        {
        	//设置原始表格的空白行
		},
		//渲染固定列表格
		render : function()
		{
			
		},
        //获取固定列表格对象
        getFixtable : function()
        {
        	return this;
        },
        //获取原始表格对象
        getSourceTable : function()
        {
        	return this;
        },
        //销毁
        destroy : function()
        {
        	return this;
        }
    };
	//给jQuery对象添加fixTable方法
	$.fn.fixTable = function(options,datas)
	{
		var fixTable = new FixTable(this,options,datas);
		if(fixTable.$element.length > 0)
		{
			return fixTable.init();
		}
	}
})(jQuery);