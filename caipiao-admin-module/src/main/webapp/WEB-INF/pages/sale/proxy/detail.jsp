<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<style>
    .btn-scheme-detail-active{
        background: #26bf8c;
        color: #fff;
    }
    .btn-scheme-detail-active:hover{
        background: #26bf8c;
        color: #fff;
    }
    .btn-scheme-detail-active:link{
        background: #26bf8c;
        color: #fff;
    }
    .btn-scheme-detail-active:focus{
        background: #26bf8c;
        color: #fff;
    }
    .dt-scheme-dzinfo{
        margin-right:8px;
    }
</style>
<div class="card-header">
    <div class="planflom-header">
        <div class="card-header-title clearfix">
            <button type="button" class="card-close plus-icon p-guanbi"></button>
            <div class="pull-left clearfix" style="margin-top: 6px" id="dtInfoTabs">
                <button type="button" class="btn btn-scheme-detail-active" tbsvalue="base">基本信息</button>
                <c:if test="${params.isSale == 2}">
                    <button type="button" class="btn" style="margin-left: -2px;" tbsvalue="yhdetail">代理返点设置</button>
                </c:if>
            </div>
            <%--<button type="button" class="card-close plus-icon p-guanbi"></button>
            <div class="pull-left clearfix" style="margin-top: 6px">
                <button class="btn btn-info do-condition">购彩记录</button>
                <button class="btn btn-info do-condition" style="margin-left: 10px;">充值记录</button>
                <button class="btn btn-info do-condition" style="margin-left: 10px;">提现记录</button>
                <button class="btn btn-info do-condition" style="margin-left: 10px;">账户流水</button>
            </div>--%>
        </div>
        <div class="card-header-con">
            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_base">
                <li class="abstract-item">
                    <span class="abstract-label">用户编号(姓名)</span>
                    <span class="abstract-value">${params.id}&nbsp;(${params.realName!=null&&params.realName!=''?params.realName:'未实名'})</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">用户昵称</span>
                    <span class="abstract-value" id="nickName">${params.nickName}&nbsp;(已更改:${params.updateUserNameNum}次)</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">用户头衔</span>
                    <span class="abstract-value" id="isale" dv="${params.isSale}">${is}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">用户状态</span>
                    <span class="abstract-value" id="yhzt" dv="${params.status}">${us}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">银行绑定</span>
                    <span class="abstract-value">${isBank=='0'?'未绑定':'已绑定'}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label"><b>邀请码</b></span>
                    <span class="abstract-value"><b>${params.qq}</b></span>
                </li>
                <li class="abstract-item" style="height: 43px;border-bottom: 1px solid #e8ebee;width: 97%;color:#3e4359;font-size: 14px;font-weight: bold;">账户信息</li>
                <li class="abstract-item">
                    <span class="abstract-label">账户余额</span>
                    <span class="abstract-value">${account.balance!=null?account.balance:0.0}&nbsp;元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">可提现金额</span>
                    <span class="abstract-value">${account.withDraw!=null?account.withDraw:0.0}&nbsp;元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">不可提现金额</span>
                    <span class="abstract-value">${account.unWithDraw!=null?account.unWithDraw:0.0}&nbsp;元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">累计消费</span>
                    <span class="abstract-value">${account.totalConsume!=null?account.totalConsume:0.0}&nbsp;元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">累计充值</span>
                    <span class="abstract-value">${account.totalRecharge!=null?account.totalRecharge:0.0}&nbsp;元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">冻结金额</span>
                    <span class="abstract-value">${account.frozen!=null?account.frozen:0.0}&nbsp;元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">累计中奖</span>
                    <span class="abstract-value">${account.totalAward!=null?account.totalAward:0.0}&nbsp;元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">累计提现</span>
                    <span class="abstract-value">${account.totalWithDraw!=null?account.totalWithDraw:0.0}&nbsp;元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">累计返现</span>
                    <span class="abstract-value">${account.totalBack!=null?account.totalBack:0.0}&nbsp;元</span>
                </li>
            </ul>

            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_yhdetail" style="display: none;border: none;">
                <div class="col-sm-9" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <form class="form-horizontal" id="saveRebate" method="post">
                    <table class="col-sm-9 table table-bordered">
                        <thead>
                        <tr style="background: #FAEBD7;">
                            <th>彩种</th>
                            <th>设置返点</th>
                            <th>设置时间</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody style="background: #fff;">
                        <c:forEach items="${rebate}" var="rb" varStatus="status">
                            <tr>
                                <input type="hidden" name="lotteryId" id="lotteryId" value="${rb.lotteryId}">
                                <input type="hidden" name="userId" id="userId" value="${params.id}">
                                <input type="hidden" name="id" id="id" value="${rb.id}">
                                <td>${rb.shortName}</td>
                                <td><input type="text" class="form-control" placeholder="未设置" name="rate" value="${rb.rate}"></td>
                                <td>${rb.createTime}</td>
                                <c:if test="${status.first==true}" >
                                    <td rowspan="${rebate_size}"><a href="javascript:;" class="label label-info" id="save">立即保存</a></td>
                                </c:if>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    </form>
                </div>
            </ul>
        </div>
    </div>
</div>
<style>
    .user_detail_radio{
        margin-right:30px;
    }
    .user_detail_radio input[type="radio"]{
        position: relative;
        top:1px;
        margin-right:5px;
    }
</style>
<script>
$(function(){
    var vipLevelJson = new Object();
    $('#vipLevel').fillSelectMenu({
        url: '${pageContext.request.contextPath}/user/getUserModuleDown?module=2',
        id: 'id',
        name: 'value',
        callback : function(data)
        {
            $.each(data, function(i,m)
            {
                vipLevelJson[$(m).attr('id')] = $(m).attr('value');
            });
            $('#dvip').html('<span class="label ' + userVipColorJson[${params.vipLevel}] + '">${vp}</span>');
        }
    });

    //点击用户返点tabs
    $('#dtInfoTabs button[tbsvalue]').on('click',function()
    {
        $('#dtInfoTabs button[tbsvalue]').removeClass('btn-scheme-detail-active');
        $(this).addClass('btn-scheme-detail-active');
        $('.dtInfo_cls').hide();
        $('#dtInfo_' + $(this).attr('tbsvalue')).show();
    });

    //点击保存
    $('#save').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/sale/lower/save',
            type : 'post',
            dataType : 'json',
            data : $("#saveRebate").serializeArray(),
            success : function(data)
            {
                showoplayer(data);
            }
        });
    });
})
</script>