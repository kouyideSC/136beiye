<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal-dialog lg" id="rule_dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">设置渠道规则</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="rule_form" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <div class="modal-bg col-sm-12">
                    <h2 class="modal-title">基础信息</h2>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">渠道名称</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.name}" disabled>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">渠道描述</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.channelDesc}" disabled>
                        </div>
                    </div>
                </div>
                <div class="modal-bg col-sm-12">
                    <h2 class="modal-title">设置规则</h2>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">使用权重</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="比如3%，则填写3" name="rate" value="${params.rate}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">启用状态</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="status" id="rule_statusSelect">
                                <option value="0">停用</option>
                                <option value="1">开启</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">启用模式</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="model" id="rule_modelSelect">
                                <option value="0">默认模式</option>
                                <option value="1">时间段</option>
                                <option value="2">时间特征</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group rule_timeRange_cls rule_timeRange_1_cls" style="display:none;">
                        <label class="col-sm-3 control-label">起始时间段</label>
                        <div class="col-sm-7 config-date">
                            <input type="text" name="timeRangeStart" class="form-control datetimepicker" style="padding-left: 20px;" value="${params.timeRangeStart}" placeholder="时间段-起始时间">
                        </div>
                    </div>
                    <div class="form-group rule_timeRange_cls rule_timeRange_1_cls" style="display:none;">
                        <label class="col-sm-3 control-label">终止时间段</label>
                        <div class="col-sm-7 config-date">
                            <input type="text" name="timeRangeEnd" class="form-control datetimepicker" style="padding-left: 20px;" value="${params.timeRangeEnd}" placeholder="时间段-终止时间">
                        </div>
                    </div>
                    <div class="form-group rule_timeRange_cls rule_timeRange_2_cls" style="display: none;">
                        <label class="col-sm-3 control-label">时间特征</label>
                        <div class="col-sm-7">
                            <input type="text" name="timeCharacter" value="${params.timeCharacter}" class="form-control" placeholder="比如03:00~05:00表示3点到5点才使用，多个特征用;连接">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">单笔最小金额</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="单笔最小金额限制" name="minMoney" value="${params.minMoney}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">单笔最大金额</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="单笔最大金额限制" name="maxMoney" value="${params.maxMoney}">
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="rule_sureBtn">确定</button>
        </div>
    </div>
</div>
<script>
$(function()
{
    $('#rule_form .rule_timeRange_${params.model}_cls').show();

    //初始化页面插件
    initPagePlugins('#rule_dialog',function()
    {
        var $dialogNode = $('#rule_dialog');
        $dialogNode.find('.config-date input').css('padding-left','20px');
        $dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');

        //初始化下拉框默认选中
        $('#rule_statusSelect').selectpicker('val','${params.status}');
        $('#rule_modelSelect').selectpicker('val','${params.model}');
    });
    //启用模式变更
    $('#rule_modelSelect').on('change',function()
    {
        $('#rule_form .rule_timeRange_cls').hide();
        $('#rule_form .rule_timeRange_' + $(this).val() + '_cls').show();
    });
    //点击确定
    $('#rule_sureBtn').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/txqd/setrule',
            type : 'post',
            dataType : 'json',
            data : $("#rule_form").serializeArray(),
            success : function(data)
            {
                $('#rule_dialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        })
    });
});
</script>