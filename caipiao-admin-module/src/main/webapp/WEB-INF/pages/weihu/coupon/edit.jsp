<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<style>
    .add-file-btn{
        display:block;white-space:nowrap; overflow:hidden; text-overflow:ellipsis;padding-top: 0;height: 31px;line-height: 31px;
    }
</style>
<div class="modal-dialog lg" id="editCouponDialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">编辑优惠券</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editCouponForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <div class="modal-bg col-sm-12">
                    <div class="form-group">
                        <label class="col-sm-3 control-label">优惠券使用类型</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="useType" id="edit_useTypeSelect">
                                <option value="0">直减券</option>
                                <option value="1">满减券</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">优惠券名称</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="请输入优惠券名称" name="name" value="${params.name}">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">面额
                        </label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="请输入面额" name="money" value="${params.money}">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group limitMoney_div_cls" style="display: none;">
                        <label class="col-sm-3 control-label">金额限制
                        </label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="请输入限制金额(适用于满减券)" name="limitMoney" value="${params.limitMoney}">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">适用彩种</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="lotteryId" id="edit_lotteryIdSelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">状态</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="status">
                                <option value="0">下架</option>
                                <option value="1">上架</option>
                            </select>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editCouponSureBtn">确定</button>
        </div>
    </div>
</div>
<script>
    //初始化页面插件
    initPagePlugins('#editCouponDialog',function()
    {
        var $dialogNode = $('#editCouponDialog');
        $dialogNode.find('.config-date input').css('padding-left','20px');
        $dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
        $('#edit_useTypeSelect').selectpicker('val','${params.useType}');
        $('#edit_lotteryIdSelect').selectpicker('val','${params.lotteryId}');
        if('${params.useType}' == '1')
        {
            $('#editCouponDialog .limitMoney_div_cls').show();
        }
    });
    $(function ()
    {
        //彩种名称下拉
        $('#edit_lotteryIdSelect').fillSelectMenu({
            url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
            id: 'id',
            name: 'name'
        });
        //优惠券类型变更
        $('#edit_useTypeSelect').on('change',function ()
        {
            if($(this).val() == 1)
            {
                $('#editCouponDialog .limitMoney_div_cls').show();
            }
            else
            {
                $('#editCouponDialog .limitMoney_div_cls').hide();
            }
        });
        //点击确定
        $('#editCouponSureBtn').on('click',function()
        {
            //发送请求
            $.ajax({
                url : '${pageContext.request.contextPath}/weihu/coupon/edit',
                type : 'post',
                dataType : 'json',
                data : $("#editCouponForm").serializeArray(),
                success : function(data)
                {
                    $('#editCouponDialog').parents('.modal').first().modal('hide');
                    showoplayer(data);
                }
            });
        });
    });
</script>