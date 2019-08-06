<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="card-header">
    <div class="planflom-header">
        <div class="card-header-title clearfix">
            <button type="button" class="card-close plus-icon p-guanbi"></button>
            <%--<div class="pull-left clearfix" style="margin-top: 6px">
                <button opauthority="menu_order_scheme" class="btn btn-info do-condition">方案详情</button>
                <button opauthority="menu_user_user" class="btn btn-info do-condition" style="margin-left: 10px;">用户中心</button>
            </div>--%>
        </div>
        <div class="card-header-con">
            <ul class="abstract-list clearfix">
                <li class="abstract-item">
                    <span class="abstract-label">方案号</span>
                    <span class="abstract-value">${params.schemeId}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">彩种玩法</span>
                    <span class="abstract-value" id="lotid">${lts=='' ? params.shortName : lts}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">期次</span>
                    <span class="abstract-value">${params.period}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">票倍数</span>
                    <span class="abstract-value">${params.multiple}&nbsp;倍</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">票金额</span>
                    <span class="abstract-value">${params.money}&nbsp;元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">是否追期</span>
                    <span class="abstract-value">${params.zhuiHao == true ? '是' : '否'}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">拆票时间</span>
                    <span class="abstract-value">${params.createTime}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">提票时间</span>
                    <span class="abstract-value">${params.sendTicketTime}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">出票时间</span>
                    <span class="abstract-value">${params.outTicketTime}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">兑奖时间</span>
                    <span class="abstract-value">${params.awardTime==''?'未兑':params.awardTime}</span>
                </li>
                <li class="abstract-item" style="width: 64.5%;">
                    <span class="abstract-label">出票状态</span>
                    <span class="abstract-value"><span class="label label-success">${ts}</span>&nbsp;(描述：${params.ticketDesc})</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">网站计奖状态</span>
                    <span class="abstract-value label label-info">${bs}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">网站计奖时间</span>
                    <span class="abstract-value">${params.bonusStateTime}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">网站中奖状态</span>
                    <span class="abstract-value label label-danger">${iw}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">出票商税后奖金</span>
                    <span class="abstract-value">${params.votePrizeTax==null?'0.00':params.votePrizeTax}元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">网站计奖税前奖金</span>
                    <span class="abstract-value">${sq}元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">网站计奖税后奖金</span>
                    <span class="abstract-value">${sh}元</span>
                </li>
                <%--<li class="abstract-item">
                    <span class="abstract-label">出票商</span>
                    <span class="abstract-value" id="voteId">${params.voteName}</span>
                </li>--%>
                <li class="abstract-item" style="width: 40%;">
                    <span class="abstract-label">方案票号</span>
                    <span class="abstract-value">${params.ticketId}</span>
                </li>
                <li class="abstract-item" style="width: 55%;">
                    <span class="abstract-label">出票商票号</span>
                    <span class="abstract-value">${params.voteTicketId}</span>
                </li>
                <li class="abstract-item" style="width: 64.5%;">
                    <span class="abstract-label">中奖描述</span>
                    <span class="abstract-value">${params.bonusInfo}</span>
                </li>
                <c:if test="${params.drawNumber != null && params.drawNumber != ''}">
                    <li class="abstract-item" style="width: 40%;">
                        <span class="abstract-label">乐善号码</span>
                        <span class="abstract-value">${params.drawNumber}</span>
                    </li>
                    <li class="abstract-item" style="width: 55%;">
                        <span class="abstract-label">乐善奖中奖描述</span>
                        <span class="abstract-value">${params.numberBonusInfo}</span>
                    </li>
                </c:if>
                <li class="abstract-item" style="width: 95%;">
                    <span class="abstract-label">票面内容</span>
                    <span class="abstract-value" style="word-break: break-all;">${params.codes}</span>
                </li>
                <li class="abstract-item" style="width: 95%;">
                    <span class="abstract-label"></span>
                    <span class="abstract-value"></span>
                </li>

                <c:if test="${isJc==true}">

                    <li class="abstract-item" style="width: 95%;">
                        <span class="abstract-label">出票赔率</span>
                        <span class="abstract-value" style="word-break: break-all;">${params.codesSp}</span>
                    </li>
                    <li class="abstract-item" style="width: 95%;">
                        <span class="abstract-label"></span>
                        <span class="abstract-value"></span>
                    </li>
                    <li class="abstract-item" style="width: 95%;">
                        <span class="abstract-label"></span>
                        <span class="abstract-value"></span>
                    </li>
                </c:if>
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