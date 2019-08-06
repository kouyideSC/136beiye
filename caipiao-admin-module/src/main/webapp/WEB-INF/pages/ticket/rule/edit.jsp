<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editRuleDialog">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">编辑分票规则</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editRuleForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <input type="hidden" name="lotteryName" id="lotteryName" value="${params.lotteryName}">
                <input type="hidden" name="playName" id="playName" value="${params.playType}">
                <div class="modal-bg col-sm-12">
                    <div class="form-group addVote_cls" id="addVoteName">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>选择出票商</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="voteId" id="addRuleVoteSelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="addLotteryId">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>选择彩种</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="lotteryId" id="addRuleLotterySelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="addPlayType">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>选择玩法</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="playType" id="addRulePlayTypeSelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="addRuleRate">
                        <label class="col-sm-3 control-label"></span>分票比例</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.rate}" name="rate" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="addRuleReceiveTime">
                        <label class="col-sm-3 control-label">出票商接口秘钥</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.receiveTime}" name="receiveTime" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editRuleSureBtn">确定</button>
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

    //彩种名称下拉
    $('#addRuleLotterySelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/lottery/getLotterysCombo?consoleStatus=1',
        id: 'id',
        name: 'shortName',
        selectedIds: ${params.lotteryId}
    });

    //出票商名称下拉
    $('#addRuleVoteSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/ticket/vote/list',
        id: 'voteId',
        name: 'voteName',
        selectedIds: ${params.voteId}
    });

    //玩法类型下拉
    $('#addRulePlayTypeSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/ticket/rule/getPlayType?lotteryId=' + ${params.lotteryId},
        id: 'id',
        name: 'value',
        selectedIds: '${params.playType}'
    });

    //根据彩种获取玩法
    $('#addRuleLotterySelect').on('change',function()
    {
        var lotteryId = $(this).val();
        $('#addRulePlayTypeSelect').fillSelectMenu({
            url: '${pageContext.request.contextPath}/ticket/rule/getPlayType?lotteryId=' + lotteryId,
            id: 'id',
            name: 'value',
            noDefault : true
        });
    });

    //点击确定
    $('#editRuleSureBtn').on('click',function()
    {
        $('#lotteryName').val($('#addRuleLotterySelect').find("option:selected").text());
        $('#playName').val($('#addRulePlayTypeSelect').find("option:selected").text());
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/ticket/rule/edit',
            type : 'post',
            dataType : 'json',
            data : $("#editRuleForm").serializeArray(),
            success : function(data)
            {
                $('#editRuleDialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        });
    });
});
</script>