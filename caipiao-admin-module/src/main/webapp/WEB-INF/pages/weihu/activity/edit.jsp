<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<style>
    .add-file-btn{
        display:block;white-space:nowrap; overflow:hidden; text-overflow:ellipsis;padding-top: 0;height: 31px;line-height: 31px;
    }
</style>
<div class="modal-dialog lg" id="editActivityDialog" style="display: block;width: 99%;min-width: 1150px;">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">编辑活动</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editActivityForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <div class="modal-bg col-sm-12">
                    <div class="form-group">
                        <label class="col-sm-3 control-label">活动类型</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="activityType" id="edit_activityTypeSelect">
                                <option value="0">彩票首页焦点图</option>
                                <option value="1">资讯版首页焦点图</option>
                                <option value="2">特定活动</option>
                                <option value="3">公告</option>
                                <option value="4">资讯/优惠信息</option>
                                <option value="5">app欢迎页图片</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group" style="display: none;" id="edit_settingIsbannerDivs">
                        <label class="col-sm-3 control-label">附加选项</label>
                        <div class="col-sm-7" style="margin-top: -3px;">
                            <input type="hidden" name="isbanner" id="edit_isbanner">
                            <input type="checkbox" id="edit_settingIsbanner">
                            <span style="margin-top: 10px;position: fixed;">&nbsp;设置为焦点图</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">活动标题</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="请输入标题" name="title" value="${params.title}">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">活动名称
                        </label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="请输入活动名称" name="activityName" value="${params.activityName}">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group edit_coupons_cls" style="display: none;">
                        <label class="col-sm-3 control-label">优惠券赠送类型</label>
                        <div class="col-sm-7">
                            <input type="hidden" name="czsCouponIds" id="edit_czsCouponIds">
                            <select class="form-control" name="couponType" id="edit_couponTypeSelect">
                                <option value="0">注册送</option>
                                <option value="1">充值送</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group edit_coupons_cls" style="display: none;">
                        <label class="col-sm-3 control-label">优惠券赠送模式</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="couponMode" id="edit_couponModeSelect">
                                <option value="0">固定模式</option>
                                <option value="1">自定义模式</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group edit_coupons_cls edit_couponsMode_cls edit_couponsMode_0_cls" style="display: none;" id="edit_couponIdsDiv">
                        <label class="col-sm-3 control-label">选择优惠券</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="couponIds" id="edit_couponIdsSelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3" multiple></select>
                        </div>
                    </div>
                    <div class="form-group edit_coupons_cls edit_couponsMode_cls edit_couponsMode_1_cls" style="display: none;">
                        <div class="col-sm-10">
                            <input type="button" class="btn btn-danger col-sm-12" style="font-size: 12px;" id="edit_couponConditionBtn" value="+ 添加一组金额范围和优惠券">
                        </div>
                    </div>
                    <div class="form-group edit_coupons_cls" style="display: none;">
                        <label class="col-sm-3 control-label">优惠券过期时间</label>
                        <div class="col-sm-7 config-date">
                            <input class="form-control datetimepicker" type="text" name="couponExpireTime" id="edit_couponExpireTime">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">显示客户端</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="clientType" id="edit_clientTypeSelect">
                                <option value="1">app</option>
                                <option value="2">h5</option>
                                <option value="3">web</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">链接跳转方式</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="build" id="edit_buildSelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3">
                                <option value="0">原生详情页</option>
                                <option value="1">H5页面</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">是否显示</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="isShow" id="edit_isShowSelect">
                                <option value="0">不显示</option>
                                <option value="1">显示</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">详情链接地址
                        </label>
                        <div class="col-sm-7">
                            <textarea class="form-control" placeholder="请输入点击活动所跳转的链接地址" name="linkUrl">${params.linkUrl}</textarea>
                        </div>
                    </div>
                    <div class="form-group addPeriod_periodRange_cls">
                        <label class="col-sm-3 control-label">活动开始时间</label>
                        <div class="col-sm-7 config-date">
                            <input class="form-control datepicker" type="text" name="beginTime" value="<fmt:formatDate value="${params.beginTime}" pattern="yyyy-MM-dd"/>">
                        </div>
                    </div>
                    <div class="form-group addPeriod_periodRange_cls">
                        <label class="col-sm-3 control-label">活动结束时间</label>
                        <div class="col-sm-7 config-date">
                            <input class="form-control datepicker" type="text" name="expireTime" value="<fmt:formatDate value="${params.expireTime}" pattern="yyyy-MM-dd"/>">
                        </div>
                    </div>
                    <c:if test="${params.pictureUrl != null && params.pictureUrl != ''}">
                        <div class="form-group">
                            <label class="col-sm-3 control-label">logo/banner</label>
                            <div class="col-sm-7">
                                <img src="${params.pictureLink}">
                            </div>
                        </div>
                    </c:if>
                    <div class="form-group">
                        <label class="col-sm-3 control-label"><c:if test="${params.pictureUrl == null || params.pictureUrl == ''}">logo/banner</c:if></label>
                        <div class="col-sm-7">
                            <input type="file" name="uplogo" id="edit_logoFile" style="display: none;"/>
                            <input type="hidden" name="pictureUrl" id="edit_logo" value="${params.pictureUrl}"/>
                            <button id="edit_uploadLogoBtn" type="button" class="btn btn-default active col-sm-12 add-file-btn">
                                <span class="plus-icon p-add icon-cha"></span>
                                <c:choose>
                                    <c:when test="${params.pictureUrl == null || params.pictureUrl == ''}">
                                        选择图片
                                    </c:when>
                                    <c:otherwise>
                                        更改图片
                                    </c:otherwise>
                                </c:choose>
                            </button>
                            <span class="label label-danger" style="padding-left: 0;position: fixed;margin-left: 10px;margin-top: 8px;cursor: pointer;" id="edit_cleanLogoFile">&nbsp;&nbsp;清除</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">活动内容</label>
                        <input type="hidden" name="content" id="edit_content">
                    </div>
                    <div style="width: 1000px;">
                        <script type="text/plain" id="edit_activityEditor" style="width:1000px;min-height:300px;"></script>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editActivitySureBtn">确定</button>
        </div>
    </div>
</div>
<script>
$(function ()
{
    if("${params.activityType}" != 0 && "${params.activityType}" != 1)
    {
        $('#edit_settingIsbannerDivs').show();
        if("${params.isbanner}" == 1)
        {
            $('#edit_settingIsbanner').attr('checked',"checked");
        }
    }
    //初始化页面插件
    initPagePlugins('#editActivityDialog',function()
    {
        var $dialogNode = $('#editActivityDialog');
        $dialogNode.find('.config-date input').css('padding-left','20px');
        $dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');

        //初始化下拉框默认选中
        $('#edit_couponTypeSelect').selectpicker('val','${params.couponType}');
        $('#edit_couponModeSelect').selectpicker('val','${params.couponMode}');
        if("${params.activityType}" == 2)
        {
            $('.edit_coupons_cls').show();
            $('.edit_couponsMode_cls').hide();
            if("${params.couponType}" == 0)
            {
                $('#edit_couponModeSelect').selectpicker('val',0);
                $('#edit_couponModeSelect').attr('disabled','disabled');
                $('.edit_couponsMode_0_cls').show();
            }
            else
            {
                $('.edit_couponsMode_${params.couponMode}' + '_cls').show();
            }
            if("${params.couponExpireTime}" != "")
            {
                $('#edit_couponExpireTime').val("${data.couponExpireTime}");
            }
        }
    });
    //初始化优惠券下拉
    $('#edit_couponIdsSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/coupon/get?status=1',
        id: 'id',
        name: '`name（money）`',
        callback : function()
        {
            //如果活动类型为特定活动,则初始化活动优惠券默认选择
            if("${params.activityType}" == 2 && couponIds != "")
            {
                //如果优惠券赠送模式为固定模式,则直接赋值
                if("${params.couponMode}" == 0)
                {
                    var couponIds = '${params.couponIds}';
                    $('#edit_couponIdsSelect').selectpicker("val",couponIds.split(","));
                }
                //如果优惠券赠送模式为自定义模式,则读取分组条件再赋值
                else if("${params.couponMode}" == 1)
                {
                    $('#edit_czsCouponIds').val('${params.couponIds}');
                    var couponInfoArray = eval('${params.couponIds}');
                    if(couponInfoArray != null && couponInfoArray.length > 0)
                    {
                        for(var i = 0; i < couponInfoArray.length; i ++)
                        {
                            var $conditionNode = $('<div class="form-group edit_coupons_cls edit_couponsMode_cls edit_couponsMode_1_cls edit_couponCondition_cls"></div>');

                            //初始化金额范围节点
                            var html = '<div class="col-sm-6">';
                            html += '<a style="position: fixed;margin-top: 7px;color: red;font-weight: bold;">X</a>';
                            html += '<span style="margin-left: 18px;">充值金额范围：</span>';
                            html += '<input type="text" class="form-control mincz" placeholder="最小金额" value="' + couponInfoArray[i].mincz + '" style="width:90px;display: inline;">';
                            html += '&nbsp;到&nbsp;';
                            html += '<input type="text" class="form-control maxcz" placeholder="最大金额" value="' + couponInfoArray[i].maxcz + '" style="width:90px;display: inline;">';
                            html += '<span style="margin-left:30px;">选择优惠券：</span></div>';
                            $conditionNode.append(html);

                            //初始化选择优惠券节点
                            var $couponNode = $('<div class="col-sm-5" style="margin-left: -86px"></div>');
                            var $couponSelectClone = $('#edit_couponIdsSelect').clone();
                            $couponSelectClone.appendTo($couponNode);
                            $conditionNode.append($couponNode);
                            $couponSelectClone.removeAttr('id').removeAttr('name');
                            $couponSelectClone.css({'width':'130px','display':'inline'});
                            $couponSelectClone.addClass('couponIds');
                            $couponSelectClone.selectpicker('refresh');
                            $couponSelectClone.selectpicker('val',couponInfoArray[i].couponIds.split(','));

                            //追加条件节点
                            var $lastNode = $('.edit_couponCondition_cls').slice(-1);
                            if($lastNode.length == 0)
                            {
                                $('#edit_couponConditionBtn').parents('.form-group').first().after($conditionNode);
                            }
                            else
                            {
                                $lastNode.after($conditionNode);
                            }
                            //点击删除金额范围和优惠券
                            $conditionNode.find('.edit_couponCondition_del_cls').click(function()
                            {
                                $(this).parents('div.edit_couponCondition_cls').first().remove();
                            });
                        }
                    }
                }
            }
        }
    });
    //活动类型变更
    $('#edit_activityTypeSelect').on('change',function()
    {
        if($(this).val() == 2)
        {
            $('.edit_coupons_cls').show();
            $('.edit_couponsMode_cls').hide();
            if($('#edit_couponTypeSelect').val() == 0)
            {
                $('#edit_couponModeSelect').selectpicker('val',0);
                $('#edit_couponModeSelect').attr('disabled','disabled');
                $('.edit_couponsMode_0_cls').show();
            }
            else
            {
                $('.edit_couponsMode_' + $('#edit_couponModeSelect').val() + '_cls').show();
            }
        }
        else
        {
            $('.edit_coupons_cls').hide();
        }
        if($(this).val() != 0 && $(this).val() != 1)
        {
            $('#edit_settingIsbannerDivs').show();
        }
        else
        {
            $('#edit_settingIsbannerDivs').hide();
        }
    });
    //优惠券赠送类型变更
    $('#edit_couponTypeSelect').on('change',function()
    {
        if($(this).val() == 0)
        {
            $('#edit_couponModeSelect').selectpicker('val',0);
            $('#edit_couponModeSelect').attr('disabled','disabled');
            $('.edit_couponsMode_0_cls').show();
        }
        else
        {
            $('#edit_couponModeSelect').removeAttr('disabled');
        }
    });
    //优惠券赠送模式变更
    $('#edit_couponModeSelect').on('change',function()
    {
        $('.edit_couponsMode_cls').hide();
        $('.edit_couponsMode_' + $(this).val() + '_cls').show();
    });
    //点击添加一组金额范围和优惠券
    $('#edit_couponConditionBtn').on('click',function()
    {
        var $conditionNode = $('<div class="form-group edit_coupons_cls edit_couponsMode_cls edit_couponsMode_1_cls edit_couponCondition_cls"></div>');

        //初始化金额范围节点
        var html = '<div class="col-sm-6">';
        html += '<a style="position: fixed;margin-top: 7px;color: red;font-weight: bold;">X</a>';
        html += '<span style="margin-left: 18px;">充值金额范围：</span>';
        html += '<input type="text" class="form-control mincz" placeholder="最小金额" style="width:90px;display: inline;">';
        html += '&nbsp;到&nbsp;';
        html += '<input type="text" class="form-control maxcz" placeholder="最大金额" style="width:90px;display: inline;">';
        html += '<span style="margin-left:30px;">选择优惠券：</span></div>';
        $conditionNode.append(html);

        //初始化选择优惠券节点
        var $couponNode = $('<div class="col-sm-5" style="margin-left: -86px"></div>');
        var $couponSelectClone = $('#edit_couponIdsSelect').clone();
        $couponSelectClone.appendTo($couponNode);
        $conditionNode.append($couponNode);
        $couponSelectClone.removeAttr('id').removeAttr('name');
        $couponSelectClone.css({'width':'130px','display':'inline'});
        $couponSelectClone.addClass('couponIds');
        $couponSelectClone.selectpicker('refresh');

        //追加条件节点
        var $lastNode = $('.edit_couponCondition_cls').slice(-1);
        if($lastNode.length == 0)
        {
            $(this).parents('.form-group').first().after($conditionNode);
        }
        else
        {
            $lastNode.after($conditionNode);
        }
    });
    //详情显示模式下拉初始化
    $.ajax({
        url : '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
        dataType : 'json',
        success : function(json)
        {
            if(json && json.datas && json.datas.list)
            {
                var html = '';
                $.each(json.datas.list,function(i,m)
                {
                    html += '<option value="' + m.id + '">' + m.name + '</option>';
                });
                $('#edit_buildSelect').append(html);
                $('#edit_buildSelect').selectpicker('refresh');
                $('#edit_buildSelect').selectpicker('val','${params.build}');
            }
        }
    });
    $('#edit_activityTypeSelect').selectpicker('val','${params.activityType}');
    $('#edit_clientTypeSelect').selectpicker('val','${params.clientType}');
    $('#edit_isShowSelect').selectpicker('val','${params.isShow}');

    //点击选择文件
    $("#edit_uploadLogoBtn").click(function ()
    {
        $("#edit_logoFile").click();
    });
    var accept = /(\.|\/)(gif|jpe?g|png|ico?n)$/i;
    $('#edit_logoFile').fileupload({
        url: '${pageContext.request.contextPath}/weihu/activity/uploadLogo',//上传地址
        dataType: 'json',
        acceptFileTypes: accept,
        autoUpload: true,
        maxFileSize: 9999999
    }).on('fileuploadadd', function (event, data)
    {
        $.each(data.files, function (index, file)
        {
            $("#edit_uploadLogoBtn").html("<sapn>" + file.name + "</span>");
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
            $('#edit_logo').val(data.result.datas.fpath);
        }
        else
        {
            showoplayer(data.result.dmsg);
        }
    });
    //清除logo
    $('#edit_cleanLogoFile').on('click',function()
    {
        $('#edit_uploadLogoBtn').text('选择文件');
        $('#edit_logo').val('');
    });
    //点击确定
    $('#editActivitySureBtn').on('click',function()
    {
        $('#edit_content').val(UM.getEditor('edit_activityEditor').getContent());
        $('#edit_isbanner').val($('#edit_activityTypeSelect').val() > 1? ($('#edit_settingIsbanner').is(":checked")? 1 : 0) : 0);
        if($('#edit_couponTypeSelect').val() == 1 && $('#edit_couponModeSelect').val() == 1)
        {
            var czsCouponIds = new Array();
            $('.edit_couponCondition_cls').each(function(i,m)
            {
                var conditionData = new Object();
                conditionData.mincz = $(m).find('.mincz').val();
                conditionData.maxcz = $(m).find('.maxcz').val();
                conditionData.couponIds = $(m).find('select.couponIds').val().join(',');
                czsCouponIds.push(conditionData);
            });
            $('#edit_czsCouponIds').val(JSON.stringify(czsCouponIds));
        }
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/activity/edit',
            type : 'post',
            dataType : 'json',
            data : $("#editActivityForm").serializeArray(),
            success : function(data)
            {
                $('#editActivityDialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        });
    });
    //初始化umeditor
    var width = $('#edit_activityEditor').width();
    var editUmeditor = UM.getEditor('edit_activityEditor');
    if(editUmeditor.$body)
    {
        editUmeditor.destroy();
        $('#edit_activityEditor').width(width);
        editUmeditor = UM.getEditor('edit_activityEditor');
    }
    setTimeout(function()
    {
        editUmeditor.setContent('${params.content}');
    },200);
});
</script>