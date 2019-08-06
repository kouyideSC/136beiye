<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal-dialog lg" id="edit_dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">修改充值方式</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="edit_form" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <div class="modal-bg col-sm-12">
                    <div class="form-group">
                        <label class="col-sm-3 control-label">名称</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" name="payName" value="${params.payName}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">简称</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" name="payShort" value="${params.payShort}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">客户端类型</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="clientTypes" id="edit_clientTypeSelect" multiple>
                                <option value="-1">所有</option>
                                <option value="0">web</option>
                                <option value="1">ios</option>
                                <option value="2">android</option>
                                <option value="3">h5</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">启用状态</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="status" id="edit_statusSelect">
                                <option value="0">停用</option>
                                <option value="1">开启</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">排序顺序</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" name="orderValue" value="${params.orderValue}">
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="edit_sureBtn">确定</button>
        </div>
    </div>
</div>
<script>
$(function()
{
    //初始化页面插件
    initPagePlugins('#edit_dialog',function()
    {
        var $dialogNode = $('#edit_dialog');
        $dialogNode.find('.config-date input').css('padding-left','20px');
        $dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');

        //初始化下拉框默认选中
        var clientTypes = '${params.clientTypes}';
        clientTypes = clientTypes != ''? clientTypes.split(',') : clientTypes;
        $('#edit_clientTypeSelect').selectpicker('val',clientTypes);
        $('#edit_statusSelect').selectpicker('val','${params.status}');
    });
    //点击确定
    $('#edit_sureBtn').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/czfs/edit',
            type : 'post',
            dataType : 'json',
            data : $("#edit_form").serializeArray(),
            success : function(data)
            {
                $('#edit_dialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        });
    });
});
</script>