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
                <button type="button" class="btn" style="margin-left: -2px;" tbsvalue="tzdetail">投注详情</button>
            </div>
        </div>
        <div class="card-header-con">
            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_base">
                <li class="abstract-item">
                    <span class="abstract-label">用户昵称</span>
                    <span class="abstract-value">${params.nickName}（用户编号：${params.userId}）</span>
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
                                    + 优惠券（${params.coupon.cName}，面额：${params.coupon.cMoney}元）
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${params.couponId != null && params.couponId != ''}">
                                        优惠券(${params.coupon.cName},面额:${params.coupon.cMoney})
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
                <li class="abstract-item" style="width: 96%;">
                    <span class="abstract-label">投注内容</span>
                    <span class="abstract-value">
                        ${params.schemeContent.length() > 80? params.schemeContent.substring(0,80) : params.schemeContent}
                        ${params.schemeContent.length() > 80? " ..." : ""}
                    </span>
                    <span class="abstract-value"><a href="javascript:;" class="dt_tzcontent_detail_cls">详细</a></span>
                </li>
                <li class="abstract-item" style="width: 96%;">
                    <span class="abstract-label">赔率内容</span>
                    <c:if test="${params.schemeSpContent != null && params.schemeSpContent != ''}">
                        <span class="abstract-value">
                            ${params.schemeSpContent.length() > 80? params.schemeSpContent.substring(0,80) : params.schemeSpContent}
                            ${params.schemeSpContent.length() > 80? " ..." : ""}
                        </span>
                        <span class="abstract-value"><a href="javascript:;" class="dt_tzcontent_detail_cls">详细</a></span>
                    </c:if>
                    <c:if test="${params.schemeSpContent == null || params.schemeSpContent == ''}">
                        <span class="abstract-value">--</span>
                    </c:if>
                </li>
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
                <li class="abstract-item" style="width: 95%;">
                    <span class="abstract-label">中奖明细</span>
                    <span class="abstract-value">
                        ${params.openStatus == 2? ((params.prizeDetail == null || params.prizeDetail == "")? "--" : params.prizeDetail) : "--"}
                    </span>
                </li>
                <c:if test="${params.openStatus == 2 && params.prizeDetail != null && params.prizeDetail != ''}">
                    <li class="abstract-item" style="width: 95%;">
                        <span class="abstract-label"></span>
                        <span class="abstract-value"></span>
                    </li>
                    <li class="abstract-item" style="width: 95%;">
                        <span class="abstract-label"></span>
                        <span class="abstract-value"></span>
                    </li>
                </c:if>
                <li class="abstract-item col-sm-12" style="margin-left:-10px;border-bottom:none;margin-bottom: -6px;">
                    <span class="abstract-label">原始投注串</span>
                </li>
                <div class="col-sm-12" style="margin-left:-10px;max-height: 150px;overflow-y:auto;word-break:break-all;">
                    ${params.schemeContent}
                </div>
                <!-- 竞彩(包含冠亚军) start -->
                <c:if test="${params.schemeOrderId.indexOf('JC') == 0 || params.schemeOrderId.indexOf('YH') == 0}">
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
                <c:choose>
                    <c:when test="${params.lotteryId == '1980' || params.lotteryId == '1990'}">
                        <div class="col-sm-12" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                            <table class="col-sm-12 table table-bordered">
                                <tr style="background: #FAEBD7;">
                                    <th>选项</th>
                                    <th>赔率</th>
                                </tr>
                                <c:forEach items="${params.tzxxs}" var="tzxx">
                                    <tr style="background: #fff;">
                                        <td>${tzxx.xx}</td>
                                        <td>${tzxx.sp}</td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="col-sm-12" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                            <table class="col-sm-12 table table-bordered">
                                <c:forEach items="${params.tzxxs}" var="tzxx">
                                    <tr style="background: #FAEBD7;">
                                        <td>${tzxx.week}<span style="color: red;">${tzxx.isd == 1? "（胆）" : ""}</span></td>
                                        <td>
                                            <table class="col-sm-12" style="margin-bottom:0;font-size: 13px;">
                                                <tr style="background: #FAEBD7;">
                                                    <td class="col-sm-4" style="position: relative;left: -13px;">
                                                        对阵：
                                                        ${params.jctype == 1? tzxx.hname : tzxx.gname}${params.jctype == 1? "（主）" : ""}
                                                        vs
                                                        ${params.jctype == 1? tzxx.gname : tzxx.hname}${params.jctype == 2? "（主）" : ""}
                                                    </td>
                                                    <td class="col-sm-2">赛事：${tzxx.lname}</td>
                                                    <td class="col-sm-3">
                                                        比分：${tzxx.score}
                                                    </td>
                                                    <td class="col-sm-3">比赛时间：${tzxx.mtime}</td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <c:forEach items="${tzxx.ccxxs}" var="ccxx">
                                        <tr style="background: #fff;">
                                            <td>${ccxx.wfname}</td>
                                            <td>${ccxx.xxs}</td>
                                        </tr>
                                    </c:forEach>
                                </c:forEach>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
                <!-- 奖金优化 -->
                <c:if test="${params.schemeType == 2}">
                <li class="abstract-item col-sm-12" style="margin-left:-10px;border-bottom:none;margin-bottom: -6px;margin-top: -10px;">
                    <span class="abstract-label">优化明细</span>
                </li>
                <div class="col-sm-12" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                    <table class="col-sm-12 table table-bordered">
                        <tr style="background: #FAEBD7;">
                            <td>选项</td>
                            <td>过关方式</td>
                            <td>倍数</td>
                            <td>理论奖金</td>
                        </tr>
                        <c:forEach items="${params.yhinfos}" var="yhinfo">
                            <tr style="background: #fff;">
                                <td>${yhinfo.xxs}</td>
                                <td style="vertical-align:middle">${yhinfo.ggfs}</td>
                                <td style="vertical-align:middle">${yhinfo.smultiple}</td>
                                <td style="vertical-align:middle">${yhinfo.lprize}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                </c:if>
                </c:if>
                <!-- 竞彩(包含冠亚军) end -->
                <!-- 足彩 start -->
                <c:if test="${params.schemeOrderId.indexOf('ZC') == 0}">
                <li class="abstract-item col-sm-12" style="margin-left:-10px;border-bottom:none;padding-top: 6px;">
                    <span class="abstract-label">详细（<span style="color: #ff3366;">开奖号：${(params.drawNumber == null || params.drawNumber == "")? "--" : params.drawNumber}</span>）</span>
                </li>
                <div class="col-sm-12" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                    <table class="col-sm-12 table table-bordered">
                        <tr style="background: #FAEBD7;">
                            <th>序号</th>
                            <th>赛事</th>
                            <th>比赛时间</th>
                            <th>对阵</th>
                            <th>选项</th>
                            <th>比分</th>
                        </tr>
                        <c:forEach items="${params.tzxxs}" var="tzxx">
                            <tr>
                                <td>${tzxx.xh}</td>
                                <td>${tzxx.mname}</td>
                                <td>${tzxx.mtime}</td>
                                <td>
                                    <span class="dt-scheme-dzinfo">${tzxx.hname}</span>
                                    <span class="dt-scheme-dzinfo">vs</span>
                                    <span class="dt-scheme-dzinfo">${tzxx.gname}</span>
                                </td>
                                <td>${tzxx.ccxxs}</td>
                                <td>${tzxx.score}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                </c:if>
                <!-- 足彩 end -->
                <!-- 数字彩 start -->
                <c:if test="${params.schemeOrderId.indexOf('MP') == 0 || params.schemeOrderId.indexOf('KP') == 0 || params.schemeOrderId.indexOf('ZH') == 0}">
                    <li class="abstract-item col-sm-12" style="margin-left:-10px;border-bottom:none;padding-top:6px;">
                        <span class="abstract-label">详细（<span style="color: #ff3366;">开奖号：${(params.drawNumber == null || params.drawNumber == "")? "--" : params.drawNumber}</span>）</span>
                    </span>
                    </li>
                    <div class="col-sm-12" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                        <table class="col-sm-12 table table-bordered">
                            <tr style="background: #FAEBD7;">
                                <td class="col-sm-2">玩法名称</td>
                                <td>投注选项</td>
                            </tr>
                            <c:forEach items="${params.tzxxs}" var="tzxx">
                                <tr>
                                    <td>${tzxx.wfname}</td>
                                    <td>${tzxx.xxs}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                    <c:if test="${params.schemeType == 1}">
                        <li class="abstract-item col-sm-12" style="margin-left:-10px;border-bottom:none;margin-bottom: -6px;margin-top: -10px;">
                            <span class="abstract-label">追号详细</span>
                        </li>
                        <div class="col-sm-12" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                            <table class="col-sm-12 table table-bordered">
                                <tr style="background: #FAEBD7;">
                                    <td>期次号</td>
                                    <td>方案编号</td>
                                    <td>倍数</td>
                                    <td>金额</td>
                                    <td>完成情况</td>
                                    <td>开奖时间</td>
                                    <td>开奖号</td>
                                    <td>中奖状态</td>
                                    <td>派奖状态</td>
                                    <td>派奖时间</td>
                                </tr>
                                <c:forEach items="${params.zhinfos}" var="zhinfo">
                                    <tr>
                                        <td>${zhinfo.pid}</td>
                                        <td>${zhinfo.scode}</td>
                                        <td>${zhinfo.multiple}</td>
                                        <td>${zhinfo.money}</td>
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
                            </table>
                        </div>
                    </c:if>
                    <!-- 数字彩 end -->
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