<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal-dialog lg" id="edit_qdczfs_dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">渠道充值方式配置</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="edit_qdczfs_form" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <div class="modal-bg col-sm-12">
                    <h2 class="modal-title">基础信息</h2>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">渠道名称</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.channelName}" disabled>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">商户号</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.merchantNo}" disabled>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">充值方式</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.payName}" disabled>
                        </div>
                    </div>
                </div>
                <div class="modal-bg col-sm-12">
                    <h2 class="modal-title">配置信息</h2>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">启用状态</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="status" id="edit_qdczfs_statusSelect">
                                <option value="0">停用</option>
                                <option value="1">开启</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">启用模式</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="model" id="edit_qdczfs_modelSelect">
                                <option value="0">默认模式</option>
                                <option value="1">时间段</option>
                                <option value="2">时间特征</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group edit_qdczfs_timerange_cls" style="display: none;">
                        <label class="col-sm-3 control-label">启用时间段(开始)</label>
                        <div class="col-sm-7 config-date">
                            <input class="form-control datetimepicker" type="text" placeholder="启用时间段-开始时间" name="timeRangeStart" value="${params.timeRangeStart}">
                        </div>
                    </div>
                    <div class="form-group edit_qdczfs_timerange_cls" style="display: none;">
                        <label class="col-sm-3 control-label">启用时间段(结束)</label>
                        <div class="col-sm-7 config-date">
                            <input class="form-control datetimepicker" type="text" placeholder="启用时间段-结束时间" name="timeRangeEnd" value="${params.timeRangeEnd}">
                        </div>
                    </div>
                    <div class="form-group edit_qdczfs_timecharacter_cls" style="display: none;">
                        <label class="col-sm-3 control-label">启用时间特征</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="时分模式，比如08:00~12:00，多个用;连接" name="timeCharacter" value="${params.timeCharacter}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">开放客户端</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="clientTypes" id="edit_qdczfs_clientTypesSelect" multiple>
                                <option value="-1">所有</option>
                                <option value="0">web</option>
                                <option value="1">ios</option>
                                <option value="2">android</option>
                                <option value="3">h5</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">权重</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" name="weight" value="${params.weight}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">单笔最小限额</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" name="minMoney" value="${params.minMoney}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">单笔最大限额</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" name="maxMoney" value="${params.maxMoney}">
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="edit_qdczfs_sureBtn">确定</button>
        </div>
    </div>
</div>
<script>
$(function()
{
    //初始化页面插件
    initPagePlugins('#edit_qdczfs_dialog',function()
    {
        var $dialogNode = $('#edit_qdczfs_dialog');
        $dialogNode.find('.config-date input').css('padding-left','20px');
        $dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');

        //初始化下拉框默认选中
        $('#edit_qdczfs_statusSelect').selectpicker('val','${params.status}');//启用状态下拉
        $('#edit_qdczfs_modelSelect').selectpicker('val','${params.model}');//启用模式下拉
        var clientTypes = '${params.clientTypes}';
        clientTypes = clientTypes != ''? clientTypes.split(',') : clientTypes;
        $('#edit_qdczfs_clientTypesSelect').selectpicker('val',clientTypes);//开放客户端下拉

        //初始化启用模式显示元素
        if("${params.model}" == 1)
        {
            $dialogNode.find('.edit_qdczfs_timerange_cls').show();
        }
        else if("${params.model}" == 2)
        {
            $dialogNode.find('.edit_qdczfs_timecharacter_cls').show();
        }
        //启用模式变更
        $('#edit_qdczfs_modelSelect').on('change',function()
        {
            if($(this).val() == 0)
            {
                $dialogNode.find('.edit_qdczfs_timerange_cls').hide();
                $dialogNode.find('.edit_qdczfs_timecharacter_cls').hide();
            }
            else if($(this).val() == 1)
            {
                $dialogNode.find('.edit_qdczfs_timerange_cls').show();
                $dialogNode.find('.edit_qdczfs_timecharacter_cls').hide();
            }
            else if($(this).val() == 2)
            {
                $dialogNode.find('.edit_qdczfs_timecharacter_cls').show();
                $dialogNode.find('.edit_qdczfs_timerange_cls').hide();
            }
        });
    });
    //点击确定
    $('#edit_qdczfs_sureBtn').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/czqd/czfs/edit',
            type : 'post',
            dataType : 'json',
            data : $("#edit_qdczfs_form").serializeArray(),
            success : function(json)
            {
                $('#edit_dialog').parents('.modal').first().modal('hide');
                showoplayer(json);
                if(json.dcode == 1000)
                {
                    $('#edit_qdczfs_dialog').parents('.modal').first().modal('hide');
                }
            }
        });
    });
});
</script>