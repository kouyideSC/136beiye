<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editVoteDialog">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">编辑出票商</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editVoteForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <div class="modal-bg col-sm-12 editBaseInfoDiv_cls">
                    <h2 class="modal-title"></h2>
                    <div class="form-group addVote_cls" id="addVoteId">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>出票商编号</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" name="voteId" value="${params.voteId}">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="addVoteName">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>出票商名称</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" name="voteName" value="${params.voteName}">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">状态</label>
                        <div class="col-sm-7" id="vote_status">
                            <span class="radio_vote"><input type="radio" name="status" value="1">启用</span>
                            <span class="radio_vote"><input type="radio" name="status" value="0">关闭</span>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="addVoteApiUrl">
                        <label class="col-sm-3 control-label"></span>出票商服务接口</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" name="apiUrl"  value="${params.apiUrl}">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="addVoteKey">
                        <label class="col-sm-3 control-label">出票商接口秘钥</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" name="key" value="${params.key}">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addVote_cls" id="addVoteDesc">
                        <label class="col-sm-3 control-label"></span>出票商说明</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" name="desc" value="${params.desc}">
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editVoteSureBtn">确定</button>
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

    //初始化下拉框默认选中
    $('#vote_status input[name="status"][value="${params.status}"]').attr("checked","checked");

    //点击确定
    $('#editVoteSureBtn').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/ticket/vote/edit',
            type : 'post',
            dataType : 'json',
            data : $("#editVoteForm").serializeArray(),
            success : function(data)
            {
                $('#editVoteDialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        });
    });
});
</script>