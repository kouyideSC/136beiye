<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editSpDialog">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">sp编辑</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editSpForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <input type="hidden" name="period" value="${params.period}">
                <input type="hidden" name="lotteryId" value="${params.period}">
                <div class="modal-bg col-sm-12">
                    <c:if test="${params.lotteryId == 1700}"><!-- 竞彩足球 -->
                    <div class="form-group">
                        <label class="col-sm-3 control-label">胜</label>
                        <input class="form-control" type="text" name="sheng" value="${params.sheng}">
                    </div>
                    </c:if>
                    <c:if test="${params.lotteryId == 1710}"><!-- 竞彩篮球 -->
                    <div class="form-group">
                        <label class="col-sm-3 control-label">胜</label>
                        <input class="form-control" type="text" name="sheng" value="${params.sheng}">
                    </div>
                    </c:if>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editSpSureBtn">确定</button>
        </div>
    </div>
</div>
<script>
$(function()
{
    //初始化页面插件
    initPagePlugins('#editSpDialog');

    //点击确定
    $('#editSpSureBtn').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/match/spEdit',
            type : 'post',
            dataType : 'json',
            data : $("#editSpForm").serializeArray(),
            success : function(data)
            {
                $('#editSpDialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        })
    });
});
</script>