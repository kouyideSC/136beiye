<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editPlaySellStatusDialog">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">玩法销售状态编辑</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editPlaySellStatusForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <input type="hidden" name="period" value="${params.period}">
                <input type="hidden" name="lotteryId" value="${params.lotteryId}">
                <div class="modal-bg col-sm-12">
                    <h2 class="modal-title">单关</h2>
                    <c:if test="${params.lotteryId == 1700}"><!-- 竞彩足球 -->
                    <div class="form-group">
                        <label class="col-sm-3 control-label">胜平负</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="singleSpfStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">让球胜平负</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="singleRqspfStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">总进球</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="singleZjqStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">比分</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="singleBfStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">半全场</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="singleBqcStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    </c:if>
                    <c:if test="${params.lotteryId == 1710}"><!-- 竞彩篮球 -->
                    <div class="form-group">
                        <label class="col-sm-3 control-label">胜负</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="singleSfStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">让分胜负</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="singleRfsfStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">胜分差</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="singleSfcStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">大小分</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="singleDxfStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    </c:if>
                </div>
                <div class="modal-bg col-sm-12">
                    <h2 class="modal-title">过关</h2>
                    <c:if test="${params.lotteryId == 1700}">
                    <div class="form-group">
                        <label class="col-sm-3 control-label">胜平负</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="spfStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">让球胜平负</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="rqspfStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">总进球</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="zjqStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">比分</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="bfStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">半全场</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="bqcStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    </c:if>
                    <c:if test="${params.lotteryId == 1710}"><!-- 竞彩篮球 -->
                    <div class="form-group">
                        <label class="col-sm-3 control-label">胜负</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="sfStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">让分胜负</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="rfsfStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">胜分差</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="sfcStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">大小分</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="dxfStatus">
                                <option value="-1">未开</option>
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    </c:if>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editPlaySellStatusSureBtn">确定</button>
        </div>
    </div>
</div>
<script>
$(function()
{
    //初始化下拉选择
    $('#editPlaySellStatusDialog select').selectpicker({});
    if('${params.lotteryId}' == '1700')
    {
        $('#editPlaySellStatusDialog select[name="singleSpfStatus"]').selectpicker('val','${params.singleSpfStatus}');
        $('#editPlaySellStatusDialog select[name="singleRqspfStatus"]').selectpicker('val','${params.singleRqspfStatus}');
        $('#editPlaySellStatusDialog select[name="singleZjqStatus"]').selectpicker('val','${params.singleZjqStatus}');
        $('#editPlaySellStatusDialog select[name="singleBfStatus"]').selectpicker('val','${params.singleBfStatus}');
        $('#editPlaySellStatusDialog select[name="singleBqcStatus"]').selectpicker('val','${params.singleBqcStatus}');
        $('#editPlaySellStatusDialog select[name="spfStatus"]').selectpicker('val','${params.spfStatus}');
        $('#editPlaySellStatusDialog select[name="rqspfStatus"]').selectpicker('val','${params.rqspfStatus}');
        $('#editPlaySellStatusDialog select[name="zjqStatus"]').selectpicker('val','${params.zjqStatus}');
        $('#editPlaySellStatusDialog select[name="bfStatus"]').selectpicker('val','${params.bfStatus}');
        $('#editPlaySellStatusDialog select[name="bqcStatus"]').selectpicker('val','${params.bqcStatus}');
    }
    else if('${params.lotteryId}' == '1710')
    {
        $('#editPlaySellStatusDialog select[name="singleSfStatus"]').selectpicker('val','${params.singleSfStatus}');
        $('#editPlaySellStatusDialog select[name="singleRfsfStatus"]').selectpicker('val','${params.singleRfsfStatus}');
        $('#editPlaySellStatusDialog select[name="singleSfcStatus"]').selectpicker('val','${params.singleSfcStatus}');
        $('#editPlaySellStatusDialog select[name="singleDxfStatus"]').selectpicker('val','${params.singleDxfStatus}');
        $('#editPlaySellStatusDialog select[name="sfStatus"]').selectpicker('val','${params.sfStatus}');
        $('#editPlaySellStatusDialog select[name="rfsfStatus"]').selectpicker('val','${params.rfsfStatus}');
        $('#editPlaySellStatusDialog select[name="sfcStatus"]').selectpicker('val','${params.sfcStatus}');
        $('#editPlaySellStatusDialog select[name="dxfStatus"]').selectpicker('val','${params.dxfStatus}');
    }
    //点击确定
    $('#editPlaySellStatusSureBtn').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/match/editPlaySellStatus',
            type : 'post',
            dataType : 'json',
            data : $("#editPlaySellStatusForm").serializeArray(),
            success : function(data)
            {
                $('#editPlaySellStatusDialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        })
    });
});
</script>