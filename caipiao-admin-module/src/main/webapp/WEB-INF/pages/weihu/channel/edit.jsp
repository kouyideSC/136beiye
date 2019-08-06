<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editChannelDialog">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">修改渠道信息</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editChannelForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <div class="modal-bg col-sm-12">
                    <div class="form-group addChannel_cls" id="addChannelName">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>渠道名称</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.channelName}" name="channelName" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addChannel_cls" id="addChannelCode">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>渠道编号</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.channelCode}" name="channelCode" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addChannel_cls" id="addContactMobile">
                        <label class="col-sm-3 control-label"><span class="check-star"></span>渠道手机号</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.contactMobile}" name="contactMobile">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addChannel_cls" id="addAuthKey">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>加密KEY</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.authKey}" name="authKey" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addChannel_cls" id="addOutAccountUserId">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>出款账户编号</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.outAccountUserId}" name="outAccountUserId" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addChannel_cls" id="addOverstepAccount">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>透支金额</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.overstepAccount}" name="overstepAccount" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addChannel_cls" id="addStatus">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>状态</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="status" id="edit_status">
                                <option value="0">停用</option>
                                <option value="1">启用</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group addChannel_cls" id="addNotifyStatus">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>出票通知</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="notifyStatus" id="edit_notifyStatus">
                                <option value="0">不通知</option>
                                <option value="1">通知</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group addChannel_cls" id="addNotifyUrl">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>商户通知地址</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.notifyUrl}" name="notifyUrl" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addChannel_cls" id="addIpLimit">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>IP白名单</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.ipLimit}" name="ipLimit" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addChannel_cls" id="addBeginTime">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>生效时间</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control datetimepicker" value="${params.beginTime}" name="beginTime" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addChannel_cls" id="addEndTime">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>结束时间</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control datetimepicker" value="${params.endTime}" name="endTime" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editChannelSureBtn">确定</button>
        </div>
    </div>
</div>
<script>
$(function()
{
    //初始化页面插件
    initPagePlugins('#editChannelDialog',function()
    {
        var $dialogNode = $('#editVoteDialog');
        $dialogNode.find('.config-date input').css('padding-left','20px');
        $dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
    });

    $('#edit_notifyStatus').selectpicker("val", '${params.notifyStatus}');
    $('#edit_status').selectpicker("val", '${params.status}');

    //点击确定
    $('#editChannelSureBtn').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/channel/edit',
            type : 'post',
            dataType : 'json',
            data : $("#editChannelForm").serializeArray(),
            success : function(data)
            {
                $('#editChannelDialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        });
    });
});
</script>