<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<style>
.btn-czqd-detail-active{
    background: #26bf8c;
    color: #fff;
}
.btn-czqd-detail-active:hover{
    background: #26bf8c;
    color: #fff;
}
.btn-czqd-detail-active:link{
    background: #26bf8c;
    color: #fff;
}
.btn-czqd-detail-active:focus{
    background: #26bf8c;
    color: #fff;
}
</style>
<div class="card-header">
    <div class="planflom-header">
        <div class="card-header-title clearfix">
            <button type="button" class="card-close plus-icon p-guanbi"></button>
            <div class="pull-left clearfix" style="margin-top: 6px" id="dtInfoTabs">
                <button type="button" class="btn btn-czqd-detail-active" tbsvalue="base">基本信息</button>
            </div>
        </div>
        <div class="card-header-con">
            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_base">
                <li class="abstract-item">
                    <span class="abstract-label">渠道名称</span>
                    <span class="abstract-value">${params.name}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">渠道简称</span>
                    <span class="abstract-value">${params.shortName}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">渠道描述</span>
                    <span class="abstract-value">${params.channelDesc}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">启用状态</span>
                    <span class="abstract-value" style="    margin-top: 15px;position: fixed;">${params.status == 1? "<label class='label label-success'>启用中</label>" : "<label class='label label-failed'>已停用</label>"}</span>
                </li>
                <li class="abstract-item" style="margin-top:6px;border-bottom:none;margin-bottom: -6px;width: 100%;">
                    <span class="abstract-label">充值方式配置信息</span>
                </li>
                <div class="col-sm-12" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                    <table class="col-sm-12 table table-bordered">
                        <tr style="background: #FAEBD7;font-weight: bold;">
                            <th>商户号</th>
                            <th>充值方式</th>
                            <th>开放客户端</th>
                            <th>启用状态</th>
                            <th>启用模式</th>
                            <th>时间段</th>
                            <th>时间特征</th>
                            <th>权重</th>
                            <th>限额</th>
                            <th style="text-align: center;">操作</th>
                        </tr>
                        <c:choose>
                            <c:when test="${params.czfslist != null && params.czfslist.size() > 0}">
                                <c:forEach items="${params.czfslist}" var="czfs">
                                    <tr dataid="${czfs.id}" datastatus="${czfs.status}" datapayName="${czfs.payName}">
                                        <td>${czfs.merchantNo}</td>
                                        <td>${czfs.payName}</td>
                                        <td>${czfs.clientTypes}</td>
                                        <td>${czfs.status == 1? "<label class='label label-success'>启用中</label>" : "<label class='label label-failed'>已停用</label>"}</td>
                                        <td>${czfs.model == 0? "默认模式" : (czfs.model == 1? "时间段" : "时间特征")}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${czfs.model == 1}">
                                                    ${czfs.timeRangeStart}~${czfs.timeRangeEnd}
                                                </c:when>
                                                <c:otherwise>--</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <th>
                                            <c:choose>
                                                <c:when test="${czfs.model == 2}">
                                                    ${czfs.timeCharacter}
                                                </c:when>
                                                <c:otherwise>--</c:otherwise>
                                            </c:choose>
                                        </th>
                                        <td>${czfs.weight}</td>
                                        <td>${czfs.minMoney} ~ ${czfs.maxMoney}</td>
                                        <td align="center">
                                            <a opauthority="btn_weihu_czqd_edit" href="javascript:;" class="label label-info mright10 edit_qdczfs_cls">编辑</a>
                                            <c:choose>
                                                <c:when test="${czfs.status == 1}">
                                                    <a opauthority="btn_weihu_czqd_edit" href="javascript:;" class="label label-danger mright10 status_qdczfs_cls">停用</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a opauthority="btn_weihu_czqd_edit" href="javascript:;" class="label label-success mright10 status_qdczfs_cls">启用</a>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="8" style="color: red;">该渠道尚未配置任何充值方式</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </table>
                </div>
            </ul>
        </div>
    </div>
</div>
<div class="modal fade" id="dt_operatorModal"></div>
<div class="modal fade" id="dt_confirmModal" useStaticDialog="1" style="z-index:1050;">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title">操作确认</h4>
            </div>
            <div class="modal-body clearfix">
                <p style="margin-top: 10px;"></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-save alertwarn btn_modal_cancel" data-dismiss="modal" id="dt_confirmModalSureBtn">确定
                </button>
            </div>
        </div>
    </div>
</div>
<script>
$(function()
{
    //点击编辑
    $('.edit_qdczfs_cls').on('click',function()
    {
        var dataid = $(this).parents('tr').first().attr('dataid');
        var $modalNode = $('#dt_operatorModal');
        $modalNode.fillWithUrl('${pageContext.request.contextPath}/weihu/czqd/czfs/initEdit?id=' + dataid);
        $modalNode.modal('show');
    });
    //点击启用/停用
    $('.status_qdczfs_cls').on('click',function()
    {
        var $trnode = $(this).parents('tr').first();
        var $modalNode = $('#dt_confirmModal');
        $modalNode.find('div.modal-body p').html('确定' + ($trnode.attr('datastatus') == 0? '启用' : '停用') + '${params.name}-' + $trnode.attr('datapayName') + '充值方式吗？');
        $modalNode.attr('uptype',0).attr('optype',1).attr('opdatas',JSON.stringify({id:$trnode.attr('dataid'),status:($trnode.attr('datastatus') == 0? 1 : 0)})).modal('show');
    });
    //点击操作确认-确定
    $('#dt_confirmModalSureBtn').on('click',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        var uptype = $confirmModal.attr('uptype');//获取更新类型 0-更新 1-新增/删除
        var optype = $confirmModal.attr('optype');//获取操作类型 1-启用/关闭渠道充值方式
        var opdatas = JSON.parse($confirmModal.attr('opdatas'));

        //根据操作类型获取url
        var url = '';
        if(optype == 1)
        {
            url = '${pageContext.request.contextPath}/weihu/czqd/czfs/status/edit';//修改渠道充值方式启用状态
        }
        //发送请求
        if($.trim(url) != '')
        {
            $.ajax({
                url: url,
                type: 'post',
                data: opdatas,
                dataType: 'json',
                success: function (json)
                {
                    showoplayer(json);
                }
            });
        }
    });
});
</script>