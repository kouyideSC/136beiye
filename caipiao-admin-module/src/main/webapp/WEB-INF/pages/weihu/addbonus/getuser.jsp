<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editAddbonusDialog">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">活动参与人员名单</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <table id="dataTables" style="min-width:900px;" border="0" cellspacing="0" cellpadding="0">
                <thead>
                <tr>
                    <th scolumn="activityName">活动名称</th>
                    <th scolumn="nickName">用户昵称</th>
                    <th scolumn="createTime">参与时间</th>
                </tr>
                </thead>
                <tbody id="dataTbody">
                    <c:choose>
                        <c:when test="${empty params}">
                            <tr>
                                <td colspan="2" style="text-align: center;">暂无任何人参与该活动...</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${params}" var="p">
                                <tr>
                                    <td scolumn="activityName">${p.activityName}</td>
                                    <td scolumn="nickName">${p.nickName}</td>
                                    <td scolumn="createTime">${p.createTime}</td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>
<script>
$(function()
{
    //初始化页面插件
    initPagePlugins('#editAddbonusDialog',function()
    {
        var $dialogNode = $('#editVoteDialog');
        $dialogNode.find('.config-date input').css('padding-left','20px');
        $dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
    });
});
</script>