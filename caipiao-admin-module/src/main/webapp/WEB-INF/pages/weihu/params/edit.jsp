<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editParamsDialog">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">编辑系统参数</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editParamsForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <div class="modal-bg col-sm-12">
                    <div class="form-group addVote_cls" id="addPmKey">
                        <label class="col-sm-3 control-label"></span>参数名</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.pmKey}" name="pmKey" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="addPmValue">
                        <label class="col-sm-3 control-label">参数值</label>
                        <div class="col-sm-7">
                            <textarea class="form-control" name="pmValue" notEmpty="">${params.pmValue}</textarea>
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addMarket_cls" id="addPmDescribe">
                        <label class="col-sm-3 control-label"></span>描述</label>
                        <div class="col-sm-7">
                            <textarea class="form-control" name="pmDescribe" rows="5">${params.pmDescribe}</textarea>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editParamsSureBtn">确定</button>
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
    $('#editParamsSureBtn').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/params/edit',
            type : 'post',
            dataType : 'json',
            data : $("#editParamsForm").serializeArray(),
            success : function(data)
            {
                $('#editParamsDialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        });
    });
});
</script>