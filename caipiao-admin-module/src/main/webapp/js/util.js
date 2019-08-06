/**
 * Created by Maki on 2016/12/30.
 */
$(function () {

    //筛选条件下拉框增加空选项
    $('.operate ul.dropdown-menu[noDefault!="true"]').prepend('<li role="presentation" opvalue=""><a role="menuitem" tabindex="-1" href="javascript:;">--请选择--</a></li>');

    // $('.dropdown-toggle[data-live-search="true"]').next().prepend('<div class="col-sm-9" style="padding-left: 0"><input id="searchBoxEdit" type="text" autocomplete="off" name="setLabels" class="form-control" placeholder="请输入"></div>');

    // $(document).on('keyup','.dropdown-menu input',function(){
    //     var that = this;
    //     var total = 0;
    //     var disabled = 0;
    //     $(this).parent().siblings('li').css('display','none');
    //     $(this).parent().siblings('a').filter(function(){
    //         total ++;
    //         if($(this).html().toLowerCase().indexOf($(that).val().toLowerCase())==-1){
    //             disabled ++;
    //             $(this).css('display','none');
    //         }else{
    //             $(this).css('display','');
    //         }
    //     })
    //     if(disabled == total){
    //         $(this).parent().siblings('li').css('display', '');
    //         $(this).parent().siblings('li').html('未找到 "' + $(that).val() + '"');
    //     }
    // })

    /**
     * 填充静态页
     * @returns {*|jQuery|HTMLElement}
     */
    $.fn.fillWithUrl = function () {
        var url;//链接
        var data;//参数
        if (arguments[0] instanceof Object) {
            url = arguments[0].url;
            data = arguments[0].data || {};
        } else {
            url = arguments[0];
            data = arguments[1] || {};
        }
        var that = $(this);
        $.ajax({
            url: url,
            async: false,
            type: 'GET',
            data: data,
            success: function (data) {
                that.html(data);
            }
        });
        return that;
    };

    /**
     * 填充select下拉框
     * @returns {*|jQuery|HTMLElement}
     */
    $.fn.fillSelectMenu = function () {
        var url;//链接
        var data;//参数
        var id;//value
        var name;//name
        var callback;//回调
        var selectedIds;//选中
        var noDefault;//取消默认的'--未选择--'
        var setEmptyOption = true;//默认增加'--未选择--'选项
        var noneSelectedText = '--请选择--';
        if (arguments[0] instanceof Object) {
            url = arguments[0].url;
            data = arguments[0].data || {};
            id = arguments[0].id || 'id';
            name = arguments[0].name || 'name';
            callback = arguments[0].callback;
            selectedIds = arguments[0].selectedIds;
            noDefault = arguments[0].noDefault;
            noneSelectedText = arguments[0].noneSelectedText;
        } else {
            url = arguments[0];
            data = arguments[1] || {};
            id = arguments[2] || 'id';
            name = arguments[3] || 'name';
            callback = arguments[4];
            selectedIds = arguments[5];
            noDefault = arguments[6];
        }
        if (noDefault && noDefault == true) {
            setEmptyOption = false;
        }
        var that = $(this);
        $.ajax({
            url: url,
            async: false,
            type: 'POST',
            data: data,
            success: function (data) {
                var json = $.parseJSON(data);
                if (!(json instanceof Array)) {
                    json = json.datas.list || []
                }
                var option = '';
                if (setEmptyOption && (that.prop("multiple") == undefined || that.prop("multiple") == false)) {
                    option += ('<option value="">' + (noneSelectedText || ('不限' + (that.attr('title')? that.attr('title') : '')) || '--请选择--') + '</option>');
                }
                if (name.charAt(0) === '`' && name.charAt(name.length - 1) === '`') { //匹配 "`contractNoStr（projectNames）`" 等格式化输出模板
                    $.each(json, function (i, n) {
                        option += ('<option value="' + n[id] + '">');
                        var content = name.substr(1, name.length - 2); //去掉 ``
                        $.each(n, function (k, v) {
                            if (content.indexOf(k) != -1) {
                                content = content.split(k).join(v);
                            }
                        });
                        option += (content + '</option>');
                    });
                } else { //不是模板取key值
                    $.each(json, function (i, n) {
                        option += ('<option value="' + n[id] + '">');
                        option += (n[name] + '</option>');
                    });
                }
                that.html(option);
                if (that.prop("multiple")) {
                    that.selectpicker({noneSelectedText: noneSelectedText || ('不限' + (that.attr('title')? that.attr('title') : '')) || '--请选择--'});
                }
                that.selectpicker('refresh');
                if (selectedIds && selectedIds != '') {
                    that.selectpicker('val', selectedIds.toString().split(',')).selectpicker('refresh');
                }
                if(noDefault)
                {
                    that.selectpicker('val', json[0].id).selectpicker('refresh');
                }
                if (callback) {
                    callback.call(null, json);
                }
            }
        });
        return that;
    };


    /**
     * 获取表单数据,数组默认使用逗号分隔
     * @returns {{}}
     */
    $.fn.getFormData = function () {
        var data = {};
        var element = $(this);
        var split = true;
        if (arguments[0] == false) {
            split = false;
        }
        var splitWith = arguments[1] || ',';
        element.find('input[name]').each(function () {
            var each = $(this);
            data[each.attr('name')] = each.val();
        });
        element.find('select[name]').each(function () {
            var each = $(this);
            var value = each.val();
            if (split && value instanceof Array) {
                data[each.attr('name')] = value.join(splitWith);
            } else {
                data[each.attr('name')] = value;
            }
        });
        element.find('textarea[name]').each(function () {
            var each = $(this);
            data[each.attr('name')] = each.val();
        });
        element.find('input[data-toggle="dropdown"]').each(function () {
            var each = $(this);
            if (each.val() != '') {
                data[each.attr('name')] = each.attr('ids');
            }
        });
        return data;
    }


    /**
     * 获取搜索区域条件
     * @returns {{}}
     */
    $.fn.getConditionValue = function () {
        var obj = {};
        var element = $(this);
        element.find('input').each(function () {
            var each = $(this);
            if (each.val() != '') {
                obj[each.attr('name')] = each.val();
            }
        });
        element.find('.dropdown-toggle[opvalue]').each(function () {
            var each = $(this);
            if (each.attr('opvalue') != '') {
                obj[each.attr('name')] = each.attr('opvalue');
            }
        });

        element.find('select').each(function () {
            var each = $(this);
            if (each.val() != '') {
                var temp = each.val();
                if (temp instanceof Array)
                    obj[each.attr('name')] = each.val().toString();
                else
                    obj[each.attr('name')] = each.val();
            }
        });
        element.find('input[type="checkbox"]').each(function () {
            if($(this).is(":checked")){
                obj[$(this).attr('name')] = 1;
            }
            else
            {
                delete obj[$(this).attr('name')];
            }
        });
        obj.queryName = $("#top_input_search", window.parent.document).val();
        return obj;
    }

    /**
     * 清除搜索区域条件
     */
    $('.clear-condition').on('click', function () {
        var element = $(this).parents('.operate');
        element.find('input[type!="radio"]').each(function () {
            $(this).val('');
        });
        element.find('.dropdown-toggle[opvalue]').each(function () {
            var each = $(this);
            each.attr('opvalue', '');
            each.find('span.content').html(each.attr('title'));
        });
        element.find('select').each(function () {
            var each = $(this);
            each.selectpicker("deselectAll").selectpicker("refresh");
        });


    });

    /**
     * 点击筛选
     */
    $('.do-condition').on('click', function () {
        var callback = $(this).parents('.operate').attr('callback');
        eval(callback);

    });

    /**
     * 填充搜索区域下拉框
     * @returns {*|jQuery|HTMLElement}
     */
    $.fn.fillDropdownMenu = function () {
        var url;//链接
        var data;//参数
        var id;//value
        var name;//name
        var callback;//回调
        if (arguments[0] instanceof Object) {
            url = arguments[0].url;
            data = arguments[0].data || {};
            id = arguments[0].id || 'id';
            name = arguments[0].name || 'name';
            callback = arguments[0].callback;
        } else {
            url = arguments[0];
            data = arguments[1] || {};
            id = arguments[2] || 'id';
            name = arguments[3] || 'name';
            callback = arguments[4];
        }
        var that = $(this);
        $.ajax({
            url: url,
            async: true,
            type: 'GET',
            data: data,
            success: function (data) {
                var json = $.parseJSON(data);
                var option = '<li role="presentation" opvalue=""><a role="menuitem" tabindex="-1" href="javascript:;">--请选择--</a></li>';

                // $.each(json.list, function (i, n) {
                //     option += ('<li role="presentation" opvalue="' + n[id] + '">');
                //     option += ('<a role="menuitem" tabindex="-1" href="javascript:;">' + n[name] + '</a>');
                //     option += ('</li>');
                // });
                if (name.charAt(0) === '`' && name.charAt(name.length - 1) === '`') { //匹配 "`contractNoStr（projectNames）`" 等格式化输出模板
                    $.each(json.list, function (i, n) {
                        option += ('<li role="presentation" opvalue="' + n[id] + '"><a role="menuitem" tabindex="-1" href="javascript:;">');
                        var content = name.substr(1, name.length - 2); //去掉 ``
                        $.each(n, function (k, v) {
                            if (content.indexOf(k) != -1) {
                                content = content.split(k).join(v);
                            }
                        });
                        option += (content + '</a>');
                    });
                } else { //不是模板取key值
                    $.each(json.list, function (i, n) {
                        option += ('<li role="presentation" opvalue="' + n[id] + '">');
                        option += ('<a role="menuitem" tabindex="-1" href="javascript:;">' + n[name] + '</a>');
                        option += ('</li>');
                    });
                }
                that.next().html(option);
                if (callback) {
                    callback.call(null, json);
                }
            }
        });
        return that;
    }

    /**
     * 设置Tabs数量
     * @returns {*|jQuery|HTMLElement}
     */
    $.fn.setTabsTitle = function () {
        var element = $(this);
        var url;//链接
        var data;//参数
        var statusKey;//状态key
        var countKey;//数量key
        var callback;//回调
        if (arguments[0] instanceof Object) {
            url = arguments[0].url;
            data = arguments[0].data || {};
            statusKey = arguments[0].statusKey || 'status';
            countKey = arguments[0].countKey || 'count';
            callback = arguments[0].callback;
        } else {
            url = arguments[0];
            data = arguments[1] || {};
            statusKey = arguments[2] || 'status';
            countKey = arguments[3] || 'count';
            callback = arguments[4];
        }
        var that = $(this);
        $.ajax({
            url: url,
            async: true,
            type: 'GET',
            data: data,
            success: function (data) {
                var json = $.parseJSON(data);
                var map = {};
                var total = 0;
                $.each(json.list, function (i, n) {
                    map[n[statusKey] + ''] = n[countKey];
                    total += parseInt(n[countKey]);
                });
                element.find('li[index]').each(function () {
                    var each = $(this);
                    if (map[each.attr('index')] != undefined) {
                        each.find('span.status-count').html('(' + map[each.attr('index')] + ')');
                    } else {
                        each.find('span.status-count').html('(0)');
                    }
                });
                element.find('li[index=""] span.status-count').html('(' + total + ')');
                if (callback) {
                    callback.call(null, json);
                }
            }
        });
        return that;
    };

    /**
     * 获取当前选中的Tabs的index
     * @returns {*|jQuery}
     */
    $.fn.getActiveTabIndex = function () {
        return $(this).find('li.active').attr('index');
    }

    /**
     * 初始化编辑界面
     * @returns {*|jQuery|HTMLElement}
     */
    $.fn.initEdit = function () {
        var url;//链接
        var data;//参数
        var callback;
        if (arguments[0] instanceof Object) {
            url = arguments[0].url;
            data = arguments[0].data || {};
            callback = arguments[0].callback;
        } else {
            url = arguments[0];
            data = arguments[1] || {};
            callback = arguments[2];
        }
        var element = $(this);
        $.ajax({
            url: url,
            async: false,
            type: 'GET',
            data: data,
            success: function (data) {
                var info = $.parseJSON(data);
                element.find('input[name],p[name],textarea[name]').each(function () {
                    var each = $(this);
                    if (each.attr('name') != '' && each.attr('type') != 'radio') {
                        if (info[each.attr('name')]) {
                            each.val(info[each.attr('name')]);
                        }
                    }
                });
                element.find('input').each(function () {
                    var each = $(this);
                    each.attr("title", each.val())
                });
                element.find('p[name]').each(function () {
                    var each = $(this);
                    if (each.attr('name') != '') {
                        if (info[each.attr('name')]) {
                            each.html(info[each.attr('name')]);
                        }
                    }
                });
                element.find('select[name]').each(function () {
                    var each = $(this);
                    if (each.attr('name') != '') {
                        if (info[each.attr('name')]) {
                            if (info[each.attr('name')].toString().indexOf(',') < 0) {
                                each.selectpicker('val', info[each.attr('name')].toString());
                            } else {
                                each.selectpicker('val', info[each.attr('name')].toString().split(','));
                            }
                        }
                    }
                });

                if (callback) {
                    callback.call(null, info);
                }
            }
        });
        return element;
    }
})
;

$.fn.initZtree = function (url, id) {
    var that = this;
    that.after('<span class="bs-caret"><span class="caret s-tree"></span></span>' +
        '<ul class="dropdown-menu ztree select-tree" id="organizationTree" role="menu" aria-labelledby="organizationId"></ul>')
    $.ajax({
        url: url,
        async: true,
        success: function (data) {
            var json = $.parseJSON(data);
            $.each(json.list, function (i, n) {
                if (n.id == id) {
                    n.checked = true;
                    $('#organizationId').val(n.name);
                    $('#organizationId').attr('ids', id);
                }
            })
            $.fn.zTree.init($('#organizationTree'), {
                view: {
                    showLine: false,
                    showIcon: false
                },
                check: {
                    enable: true,
                    chkStyle: 'radio',
                    radioType: 'all'
                },
                callback: {
                    beforeClick: function (id, node) {
                        var auths = $.fn.zTree.getZTreeObj('organizationTree');
                        auths.checkNode(node, node.checked, null, true);
                        return false;
                    },
                    onCheck: function (nodes) {
                        var auths = $.fn.zTree.getZTreeObj('organizationTree');
                        var nodes = auths.getCheckedNodes(true);
                        checked.apply(undefined, [nodes]);
                    }
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                }
            }, json.list);

            $.fn.zTree.getZTreeObj('organizationTree').expandAll(true);

            // context
            function checked(nodes) {
                var temps = [];
                var ids = [];
                for (var i in nodes) {
                    temps.push(nodes[i].name);
                    ids.push(nodes[i].id);
                }
                that.val(temps.join(','))
                that.attr('ids', ids.join(','))
            }

            $('#organizationTree').on('click', function (e) {
                e.stopPropagation();
            })
        }
    })
};

//div 拖拽
$.fn.dragDiv = function (divWrap) {
    return this.each(function () {
        var $divMove = $(this);//鼠标可拖拽区域
        var $divWrap = divWrap ? $divMove.parents(divWrap) : $divMove;//整个移动区域
        var mX = 0, mY = 0;//定义鼠标X轴Y轴
        var dX = 0, dY = 0;//定义div左、上位置
        var isDown = false;//mousedown标记
        if (document.attachEvent) {//ie的事件监听，拖拽div时禁止选中内容，firefox与chrome已在css中设置过-moz-user-select: none; -webkit-user-select: none;
            $divMove[0].attachEvent('onselectstart', function () {
                return false;
            });
        }
        $divMove.mousedown(function (event) {
            var event = event || window.event;
            mX = event.clientX;
            mY = event.clientY;
            dX = $divWrap.offset().left;
            dY = $divWrap.offset().top;
            isDown = true;//鼠标拖拽启动
        });
        $(document).mousemove(function (event) {
            var event = event || window.event;
            var x = event.clientX;//鼠标滑动时的X轴
            var y = event.clientY;//鼠标滑动时的Y轴
            if (isDown) {
                $divWrap.css({"left": x - mX + dX, "top": y - mY + dY});//div动态位置赋值
            }
        });
        $divMove.mouseup(function () {
            isDown = false;//鼠标拖拽结束
        });
    });
};
//判断对象是否是字符串
function isString(obj)
{
    return Object.prototype.toString.call(obj) === "[object String]";
}
//判断对象是否是数值
function isNumber(obj)
{
    return Object.prototype.toString.call(obj) === "[object Number]";
}
//判断对象是否是字符串或数值
function isStringOrNumber(obj)
{
    return isString(obj) || isNumber(obj);
}