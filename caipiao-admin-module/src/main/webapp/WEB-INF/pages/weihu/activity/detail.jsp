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
                <button type="button" class="btn btn-scheme-detail-active" tbsvalue="base">活动信息</button>
                <button type="button" class="btn" style="margin-left: -2px;" tbsvalue="tzdetail">参与用户</button>
            </div>
        </div>
        <div class="card-header-con">
            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_base">
                <li class="abstract-item">
                    <span class="abstract-label">用户昵称</span>
                    <span class="abstract-value">${params.nickName}（编号：${params.userId}）</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">投注来源</span>
                    <span class="abstract-value">${params.clientSourceName}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">彩种名称</span>
                    <span class="abstract-value">${params.lotteryName}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">方案期次</span>
                    <span class="abstract-value">${params.period}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">方案编号</span>
                    <span class="abstract-value">${params.schemeOrderId}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">发起时间</span>
                    <span class="abstract-value">${params.createTime}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">方案类型</span>
                    <span class="abstract-value">
                        ${params.schemeTypeDesc}
                        <c:if test="${params.schemeType == 1}">
                            [${params.donePeriod}/${params.periodSum}]&nbsp;（中奖后${params.isPrizeStop == 0? "不" : ""}停止）
                        </c:if>
                    </span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">玩法类型</span>
                    <span class="abstract-value">${params.schemePlayType}</span>
                </li>

                <li class="abstract-item">
                    <span class="abstract-label">方案倍数</span>
                    <span class="abstract-value">${params.schemeMultiple}倍</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">方案注数</span>
                    <span class="abstract-value">${params.schemeZs}注</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">理论奖金</span>
                    <span class="abstract-value">${params.theoryPrize == null? '--' : params.theoryPrize}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">支付渠道</span>
                    <span class="abstract-value">${params.schemeStatus == 0? "--" : params.channelDesc}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">方案金额</span>
                    <span class="abstract-value">${params.schemeMoney}元</span>
                </li>
                <li class="abstract-item" style="width: 64%">
                    <span class="abstract-label">支付金额</span>
                    <span class="abstract-value">
                    <c:if test="${params.schemeStatus <= 0}">
                        --
                    </c:if>
                    <c:if test="${params.schemeStatus > 0}">
                        <c:choose>
                            <c:when test="${(params.schemePayMoney != null && params.schemePayMoney != '')}">
                                ${params.schemePayMoney}元
                                <c:if test="${params.couponId != null && params.couponId != ''}">
                                    + 优惠券（${params.coupon.name}，面额：${params.coupon.money}元）
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${params.couponId != null && params.couponId != ''}">
                                        优惠券(${params.coupon.name},面额:${params.coupon.money})
                                    </c:when>
                                    <c:otherwise>--</c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                    </span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">方案状态</span>
                    <span class="abstract-value">${params.schemeStatusDesc}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">出票时间</span>
                    <span class="abstract-value">${params.schemeStatus < 3? "--" : params.outTicketTime}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">开奖号码</span>
                    <span class="abstract-value">
                        ${(params.drawNumber == null || params.drawNumber == "")? "--" : params.drawNumber}
                    </span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">计奖状态</span>
                    <span class="abstract-value">${params.openStatus == 0? "未计奖" : "已计奖"}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">计奖时间</span>
                    <span class="abstract-value">${params.openStatus == 0? "--" : params.openTime}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">中奖状态</span>
                    <span class="abstract-value">
                        ${params.openStatus == 0? "--" : (params.openStatus == 1? "未中奖" : (params.openStatus == 2? "已中奖" : ""))}
                    </span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">税后总奖金</span>
                    <span class="abstract-value">
                        ${params.openStatus == 2? params.prizeTax : "--"}
                    </span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">派奖状态</span>
                    <span class="abstract-value">
                        ${params.openStatus == 2? (params.prizeStatus == 0? "未派奖" : (params.prizeStatus == 1? "派奖中" : (params.prizeStatus == 2? "已派奖" : "--"))) : "--"}
                    </span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">派奖时间</span>
                    <span class="abstract-value">
                        ${(params.prizeTime != null && params.prizeTime != "")? params.prizeTime : "--"}
                    </span>
                </li>
                <li class="abstract-item" style="width: 96%;">
                    <span class="abstract-label">中奖明细</span>
                    <span class="abstract-value">
                        ${params.openStatus == 2? ((params.prizeDetail == null || params.prizeDetail == "")? "--" : params.prizeDetail) : "--"}
                    </span>
                </li>
                <li class="abstract-item" style="width: 96%;">
                    <span class="abstract-label">投注内容</span>
                    <span class="abstract-value">
                        ${params.schemeContent.length() > 80? params.schemeContent.substring(0,80) : params.schemeContent}
                        ${params.schemeContent.length() > 80? " ..." : ""}
                    </span>
                    <span class="abstract-value"><a href="javascript:;" class="dt_tzcontent_detail_cls">详细</a></span>
                    <%--<span class="abstract-value" style="margin-left: 10px;"><a href="javascript:;" class="dt_cpvalue_cls" cpvalue="${params.schemeContent}">复制</a></span>--%>
                </li>
                <li class="abstract-item" style="width: 96%;">
                    <span class="abstract-label">赔率内容</span>
                    <c:if test="${params.schemeSpContent != null && params.schemeSpContent != ''}">
                        <span class="abstract-value">
                            ${params.schemeSpContent.length() > 80? params.schemeSpContent.substring(0,80) : params.schemeSpContent}
                            ${params.schemeSpContent.length() > 80? " ..." : ""}
                        </span>
                        <span class="abstract-value"><a href="javascript:;" class="dt_tzcontent_detail_cls">详细</a></span>
                        <%--<span class="abstract-value" style="margin-left: 10px;"><a href="javascript:;" class="dt_cpvalue_cls" cpvalue="${params.schemeSpContent}">复制</a></span>--%>
                    </c:if>
                    <c:if test="${params.schemeSpContent == null || params.schemeSpContent == ''}">
                        <span class="abstract-value">--</span>
                    </c:if>
                </li>
                <%--<li class="abstract-item">
                    <span class="abstract-label">官方税后加奖</span>
                    <span class="abstract-value">
                    <c:if test="${params.openStatus == 2}">
                        ${params.prizeSubjoinTax == null? 0.00 : params.prizeSubjoinTax}元
                    </c:if>
                    <c:if test="${params.openStatus != 2}">
                        --
                    </c:if>
                    </span>
                </li>--%>
                <%--<li class="abstract-item">
                    <span class="abstract-label">网站税后加奖</span>
                    <span class="abstract-value">
                    <c:if test="${params.openStatus == 2}">
                        ${params.prizeSubjoinSiteTax == null? 0.00 : params.prizeSubjoinSiteTax}元
                    </c:if>
                    <c:if test="${params.openStatus != 2}">
                        --
                    </c:if>
                    </span>
                </li>--%>

            </ul>
            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_tzdetail" style="display: none;border-bottom: none;">
                <li class="abstract-item">
                    <span class="abstract-label">方案倍数</span>
                    <span class="abstract-value">${params.schemeMultiple}倍</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">方案注数</span>
                    <span class="abstract-value">${params.schemeZs}注</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">方案金额</span>
                    <span class="abstract-value">${params.schemeMoney}元</span>
                </li>
                <li class="abstract-item col-sm-12" style="margin-left:-10px;border-bottom:none;margin-bottom: -6px;">
                    <span class="abstract-label">原始投注串</span>
                </li>
                <div class="col-sm-12" style="margin-left:-10px;max-height: 150px;overflow-y:auto;word-break:break-all;">
                    ${params.schemeContent}
                </div>
                <c:if test="${params.schemeOrderId.indexOf('JC') == 0}">
                <li class="abstract-item col-sm-12" style="margin-left:-10px;border-bottom:none;margin-bottom: -6px;">
                    <span class="abstract-label">赔率内容</span>
                </li>
                <div class="col-sm-12" style="margin-left:-10px;max-height: 150px;overflow-y:auto;word-break:break-all;">
                        ${params.schemeSpContent}
                </div>
                <li class="abstract-item col-sm-12" style="margin-left:-10px;border-bottom:none;margin-bottom: -6px;">
                    <span class="abstract-label">过关方式</span>
                </li>
                <div class="col-sm-12" style="margin-left:-10px;max-height: 150px;overflow-y:auto;word-break:break-all;">
                    ${params.ggfs}
                </div>
                <li class="abstract-item col-sm-12" style="margin-left:-10px;border-bottom:none;margin-bottom: -6px;">
                    <span class="abstract-label">详细</span>
                </li>
                <div class="col-sm-12" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                    <table class="col-sm-12 table table-bordered">
                        <c:forEach items="${params.tzxxs}" var="tzxx">
                            <tr style="background: #FAEBD7;">
                                <th>
                                    ${tzxx.week}
                                    <span style="color: red;">${tzxx.isd == 1? "（胆）" : ""}</span>
                                </th>
                                <th>
                                    <table class="col-sm-12" style="margin-bottom:0;font-size: 13px;">
                                        <tr style="background: #FAEBD7;">
                                            <th class="col-sm-4" style="position: relative;left: -13px;">
                                                对阵：
                                                ${params.jctype == 1? tzxx.hname : tzxx.gname}${params.jctype == 1? "（主）" : ""}
                                                vs
                                                ${params.jctype == 1? tzxx.gname : tzxx.hname}${params.jctype == 2? "（主）" : ""}
                                            </th>
                                            <th class="col-sm-2">赛事：${tzxx.lname}</th>
                                            <th class="col-sm-3">
                                                比分：
                                                <c:if test="${tzxx.isend == 1}">
                                                    ${tzxx.score}(半场${tzxx.hscore})
                                                </c:if>
                                                <c:if test="${tzxx.isend != 1}">
                                                    --
                                                </c:if>
                                            </th>
                                            <th class="col-sm-3">比赛时间：${tzxx.mtime}</th>
                                        </tr>
                                    </table>
                                </th>
                            </tr>
                            <c:forEach items="${tzxx.ccxxs}" var="ccxx">
                                <tr style="background: #fff;">
                                    <td>${ccxx.wfname}</td>
                                    <td>
                                        <c:forEach items="${ccxx.xxs}" var="xx">
                                            <c:if test="${xx.zstatus == 0}">
                                            <span class="dt-scheme-dzinfo">${xx.xname}</span>
                                            </c:if>
                                            <c:if test="${xx.zstatus == 1}">
                                                <span class="dt-scheme-dzinfo" style="color:red">${xx.xname} </span>
                                            </c:if>
                                        </c:forEach>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:forEach>
                    </table>
                </div>
                </c:if>
                <c:if test="${params.schemeOrderId.indexOf('ZC') == 0}">
                <li class="abstract-item col-sm-12" style="margin-left:-10px;border-bottom:none;padding-top: 6px;">
                    <span class="abstract-label">详细（<span style="color: #ff3366;">开奖号：${(params.drawNumber == null || params.drawNumber == "")? "--" : params.drawNumber}</span>）</span>
                </li>
                <div class="col-sm-12" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                    <table class="col-sm-12 table table-bordered">
                        <thead>
                            <tr style="background: #FAEBD7;">
                                <th>序号</th>
                                <th>赛事</th>
                                <th>比赛时间</th>
                                <th>对阵</th>
                                <th>比分</th>
                                <th>选项</th>
                                <th>是否命中</th>
                            </tr>
                        </thead>
                        <tbody style="background: #fff;">
                        <c:forEach items="${params.tzxxs}" var="tzxx">
                            <tr>
                                <td>${tzxx.xh}</td>
                                <td>${tzxx.mname}</td>
                                <td>${tzxx.mtime}</td>
                                <td>
                                    <span class="dt-scheme-dzinfo">${tzxx.hname}</span>
                                    <span class="dt-scheme-dzinfo">vs</span>
                                    <span class="dt-scheme-dzinfo">${tzxx.gname}</span></td>
                                <td>${tzxx.isend == 0? "--" : (tzxx.isend == 1? tzxx.score : "--")}</td>
                                <td>
                                    <c:set var="zstatus" value="0"></c:set>
                                    <c:forEach items="${tzxx.ccxxs}" var="ccxx">
                                        <span>${ccxx.xname}</span>
                                        <span class="dt-scheme-dzinfo" style="color: red;">${ccxx.dtdesc}</span>
                                        <c:set var="zstatus" value="${ccxx.zstatus}"></c:set>
                                    </c:forEach>
                                </td>
                                <td>
                                    <c:if test="${zstatus == 1}">
                                        <span style="color: red;">√</span>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                </c:if>
                <c:if test="${params.schemeOrderId.indexOf('MP') == 0 || params.schemeOrderId.indexOf('KP') == 0}">
                    <li class="abstract-item col-sm-12" style="margin-left:-10px;border-bottom:none;padding-top:6px;">
                        <span class="abstract-label">详细（<span style="color: #ff3366;">开奖号：${(params.drawNumber == null || params.drawNumber == "")? "--" : params.drawNumber}</span>）</span>
                    </span>
                    </li>
                    <div class="col-sm-12" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                        <table class="col-sm-12 table table-bordered">
                            <thead>
                            <tr style="background: #FAEBD7;">
                                <th class="col-sm-2">玩法名称</th>
                                <th>投注选项</th>
                            </tr>
                            </thead>
                            <tbody style="background: #fff;">
                            <c:forEach items="${params.tzxxs}" var="tzxx">
                                <tr>
                                    <td>${tzxx.wfname}</td>
                                    <td>
                                        <c:if test="${params.xhxt == 1}">
                                            <c:forEach items="${tzxx.qdxxs}" var="xx" varStatus="status">
                                                <c:if test="${status.first}">（</c:if>
                                                <c:if test="${xx.zstatus == 0}">
                                                    <span class="${status.last? "" : "dt-scheme-dzinfo"}">${xx.xname}</span>
                                                </c:if>
                                                <c:if test="${xx.zstatus == 1}">
                                                    <span class="${status.last? "" : "dt-scheme-dzinfo"}" style="color: red;;">${xx.xname}</span>
                                                </c:if>
                                                <c:if test="${status.last}">）</c:if>
                                            </c:forEach>
                                            <c:forEach items="${tzxx.qtxxs}" var="xx">
                                                <c:if test="${xx.zstatus == 0}">
                                                    <span class="dt-scheme-dzinfo">${xx.xname}</span>
                                                </c:if>
                                                <c:if test="${xx.zstatus == 1}">
                                                    <span class="dt-scheme-dzinfo" style="color: red;;">${xx.xname}</span>
                                                </c:if>
                                            </c:forEach>
                                            <span class="dt-scheme-dzinfo">|</span>
                                            <c:forEach items="${tzxx.hdxxs}" var="xx" varStatus="status">
                                                <c:if test="${status.first}">（</c:if>
                                                <c:if test="${xx.zstatus == 0}">
                                                    <span class="${status.last? "" : "dt-scheme-dzinfo"}">${xx.xname}</span>
                                                </c:if>
                                                <c:if test="${xx.zstatus == 1}">
                                                    <span class="${status.last? "" : "dt-scheme-dzinfo"}" style="color: red;;">${xx.xname}</span>
                                                </c:if>
                                                <c:if test="${status.last}">）</c:if>
                                            </c:forEach>
                                            <c:forEach items="${tzxx.htxxs}" var="xx">
                                                <c:if test="${xx.zstatus == 0}">
                                                    <span class="dt-scheme-dzinfo">${xx.xname}</span>
                                                </c:if>
                                                <c:if test="${xx.zstatus == 1}">
                                                    <span class="dt-scheme-dzinfo" style="color: red;;">${xx.xname}</span>
                                                </c:if>
                                            </c:forEach>
                                        </c:if>
                                        <c:if test="${params.xhxt != 1}">
                                            <c:forEach items="${tzxx.xxs}" var="xx">
                                                <c:if test="${xx.zstatus == 0}">
                                                    <span class="dt-scheme-dzinfo">${xx.xname}</span>
                                                </c:if>
                                                <c:if test="${xx.zstatus == 1}">
                                                    <span class="dt-scheme-dzinfo" style="color: red;;">${xx.xname}</span>
                                                </c:if>
                                            </c:forEach>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${params.schemeType == 1}">
                        <li class="abstract-item col-sm-12" style="margin-left:-10px;border-bottom:none;margin-bottom: -6px;margin-top: -10px;">
                            <span class="abstract-label">追号详细</span>
                        </li>
                        <div class="col-sm-12" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                            <table class="col-sm-12 table table-bordered">
                                <thead>
                                <tr style="background: #FAEBD7;">
                                    <th>期次号</th>
                                    <th>完成情况</th>
                                    <th>开奖时间</th>
                                    <th>开奖号</th>
                                    <th>中奖状态</th>
                                    <th>派奖状态</th>
                                    <th>派奖时间</th>
                                </tr>
                                </thead>
                                <tbody style="background: #fff;">
                                    <c:forEach items="${params.zhinfos}" var="zhinfo">
                                        <tr>
                                            <td>${zhinfo.pid}</td>
                                            <td>
                                                <c:if test="${zhinfo.status < 1}">
                                                    尚未开始
                                                </c:if>
                                                <c:if test="${zhinfo.status == 1}">
                                                    等待出票
                                                </c:if>
                                                <c:if test="${zhinfo.status == 2}">
                                                    出票中
                                                </c:if>
                                                <c:if test="${zhinfo.status == 3}">
                                                    <c:if test="${zhinfo.zstatus == 0}">
                                                        等待开奖
                                                    </c:if>
                                                    <c:if test="${zhinfo.zstatus != 0}">
                                                        <c:if test="${zhinfo.zstatus == 1}">已完成</c:if>
                                                        <c:if test="${zhinfo.zstatus == 2}">
                                                            <c:if test="${zhinfo.pstatus < 1}">等待派奖</c:if>
                                                            <c:if test="${zhinfo.pstatus == 1}">派奖中</c:if>
                                                            <c:if test="${zhinfo.pstatus == 2}">已完成</c:if>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                                <c:if test="${zhinfo.status >= 4}">
                                                    ${zhinfo.zdesc}
                                                </c:if>
                                            </td>
                                            <td>${zhinfo.ktime}</td>
                                            <td>${zhinfo.zstatus == 0? "--" : zhinfo.kcode}</td>
                                            <td>${zhinfo.zstatus == 0? "--" : (zhinfo.zstatus == 1? "未中奖" : (zhinfo.zstatus == 2? "已中奖" : ""))}</td>
                                            <td>${zhinfo.zstatus == 2? (zhinfo.pstatus == 1? "未派奖" : (zhinfo.pstatus == 1? "派奖中" : (zhinfo.pstatus == 2? "已派奖" : "--"))) : "--"}</td>
                                            <td>${zhinfo.pstatus == 2? zhinfo.ptime : "--"}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                </c:if>
            </ul>
        </div>
    </div>
</div>
<script>
$(function()
{
    //点击方案信息tabs
    $('#dtInfoTabs button[tbsvalue]').on('click',function()
    {
        $('#dtInfoTabs button[tbsvalue]').removeClass('btn-scheme-detail-active');
        $(this).addClass('btn-scheme-detail-active');
        $('.dtInfo_cls').hide();
        $('#dtInfo_' + $(this).attr('tbsvalue')).show();
    });
    //点击详细
    $('.dt_tzcontent_detail_cls').on('click',function()
    {
        $('#dtInfoTabs button[tbsvalue="tzdetail"]').click();
    });
});
</script>