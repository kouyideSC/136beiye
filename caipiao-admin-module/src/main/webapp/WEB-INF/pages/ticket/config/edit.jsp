<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editConfigDialog">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">编辑控制参数</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editConfigForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <div class="modal-bg col-sm-12">
                    <div class="form-group addVote_cls" id="editLotteryId">
                        <label class="col-sm-3 control-label"></span>彩种名称</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.lotteryName}" name="lotteryName" notEmpty="" disabled>
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="editPlayType">
                        <label class="col-sm-3 control-label"></span>玩法名称</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.playType}" name="playType" notEmpty="" disabled>
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="editMaxMultiple">
                        <label class="col-sm-3 control-label"></span>最低倍数</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.maxMultiple}" name="maxMultiple" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="editMaxMoney">
                        <label class="col-sm-3 control-label"></span>最低金额</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.maxMoney}" name="maxMoney" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="editMaxPrize">
                        <label class="col-sm-3 control-label"></span>最高金额</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.maxPrize}" name="maxPrize" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="editMaxPassType">
                        <label class="col-sm-3 control-label"></span>竞彩最大串关</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.maxPassType}" name="maxPassType" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editConfigSureBtn">确定</button>
        </div>
    </div>
</div>
<script>
$(function()
{
    //初始化页面插件
    initPagePlugins('#editVoteDialog',function()
    {
        var $dialogNode = $('#editVoteDialog');
        $dialogNode.find('.config-date input').css('padding-left','20px');
        $dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
    });

    //点击确定
    $('#editConfigSureBtn').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/ticket/config/edit',
            type : 'post',
            dataType : 'json',
            data : $("#editConfigForm").serializeArray(),
            success : function(data)
            {
                $('#editConfigDialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        });
    });
});
</script>