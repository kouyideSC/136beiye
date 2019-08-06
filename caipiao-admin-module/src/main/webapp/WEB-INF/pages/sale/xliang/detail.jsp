<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<%@include file="../../base/inc.jsp" %>
<div class="card-wrap card-wrap-show" id="detailCard">
<div class="card-header">
    <div class="planflom-header">
        <div class="card-header-title clearfix">
            <div class="pull-left clearfix" style="margin-top: 6px" id="dtInfoTabs">
                <button type="button" class="btn btn-scheme-detail-active" tbsvalue="base">基本信息</button>
                <button type="button" class="btn" style="margin-left: -2px;" tbsvalue="yhdetail">我的返点上限</button>
                <button type="button" class="btn" tbsvalue="loweruser">下级用户</button>
                <button type="button" class="btn" tbsvalue="lowerproxy">下级代理</button>
                <button type="button" class="btn" tbsvalue="xiaodetail">本月销量明细</button>
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
                    <span class="abstract-value" id="nickName">${params.nickName}&nbsp;</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">用户状态</span>
                    <span class="abstract-value" id="yhzt" dv="${params.status}">${us}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">上次登录时间</span>
                    <span class="abstract-value"><fmt:formatDate value="${params.lastLoginTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">白名单</span>
                    <span class="abstract-value" id="bmd" dv="${params.isWhite}">${params.isWhite=='1'?"是":"否"}</span>
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
                    <span class="abstract-label">返利账户余额</span>
                    <span class="abstract-value">${account.balanceBack!=null?account.balanceBack:0.0}&nbsp;元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">总返利金额</span>
                    <span class="abstract-value">${account.totalBack!=null?account.totalBack:0.0}&nbsp;元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">冻结金额</span>
                    <span class="abstract-value">${account.frozen!=null?account.frozen:0.0}&nbsp;元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label"><b>累计总销量</b></span>
                    <span class="abstract-value">${salemoney.zjSaleMoney+salemoney.zjSaleZhuiHaoMoney+salemoney.dlSaleMoney+salemoney.dlZhuiHaoSaleMoney+salemoney.dlUserSaleMoney+salemoney.dlUserZhuiHaoSaleMoney+salemoney.yhSaleMoney+salemoney.yhZhuiHaoSaleMoney}&nbsp;元</span>
                </li>
                <li class="abstract-item" style="width: 64.5%;">
                    <span class="abstract-label"><b>累计推广用户数</b></span>
                    <span class="abstract-value">${salemoney.tgUserNumber}人</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label"><b>当月销量</b></span>
                    <span class="abstract-value">${salemoney.localMonthZjSaleMoney+salemoney.localMonthZjSaleZhuiHaoMoney+salemoney.localMonthDlSaleMoney+salemoney.localMonthDlZhuiHaoSaleMoney+salemoney.localMonthDlUserSaleMoney+salemoney.localMonthDlUserZhuiHaoSaleMoney+salemoney.localMonthYhSaleMoney+salemoney.localMonthYhZhuiHaoSaleMoney}&nbsp;元</span>
                </li>
                <li class="abstract-item" style="width: 64.5%;">
                    <span class="abstract-label"><b>当月推广用户数</b></span>
                    <span class="abstract-value">${salemoney.localMonthTgUserNumber}人</span>
                </li>

            </ul>

            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_yhdetail" style="display: none;border: none;">
                <div class="col-sm-7" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <form class="form-horizontal" id="saveRebate" method="post">
                    <table class="col-sm-7 table table-bordered">
                        <thead>
                        <tr style="background: #FAEBD7;">
                            <th>彩种</th>
                            <th>设置返点</th>
                            <th>设置时间</th>
                        </tr>
                        </thead>
                        <tbody style="background: #fff;">
                        <c:forEach items="${rebate}" var="rb" varStatus="status">
                            <tr>
                                <input type="hidden" name="lotteryId" id="lotteryId" value="${rb.lotteryId}">
                                <input type="hidden" name="userId" id="userId" value="${params.id}">
                                <input type="hidden" name="id" id="id" value="${rb.id}">
                                <td>${rb.shortName}</td>
                                <td>${rb.rate}</td>
                                <td>${rb.createTime}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    </form>
                </div>
            </ul>

            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_loweruser" style="display: none;border: none;">
                <div class="col-sm-12" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <table class="col-sm-12 table table-bordered">
                        <thead>
                            <tr style="background: #FAEBD7;">
                                <th scolumn="nickName">昵称</th>
                                <th scolumn="balance">余额</th>
                                <th scolumn="totalConsume">购彩/中奖金额</th>
                                <th scolumn="totalRecharge">充值/提现金额</th>
                                <th scolumn="sumMoney">用户总销量</th>
                                <th scolumn="registerTime">注册时间</th>
                                <th scolumn="bankIsBind">银行绑定</th>
                                <th scolumn="isWhite">白名单</th>
                                <th scolumn="loginDegree">登录</th>
                                <th scolumn="lastLoginTime">上次登录</th>
                            </tr>
                        </thead>
                        <tbody style="background: #fff;">
                            <c:forEach items="${loweruser}" var="dt">
                                <tr>
                                    <td>${dt.nickName}</td>
                                    <td>${dt.balance}</td>
                                    <td>${dt.totalConsume}/${dt.totalAward}</td>
                                    <td>${dt.totalRecharge}/${dt.totalWithDraw}</td>
                                    <td>${dt.yhSaleMoney+dt.yhSaleZhuiHaoMoney}元</td>
                                    <td>${dt.registerTime}</td>
                                    <td>${dt.bankIsBind==false?'未绑定':'已绑定'}</td>
                                    <td>${dt.isWhite==0?'未开通':'已开通'}</td>
                                    <td>${dt.loginDegree}次</td>
                                    <td>${dt.lastLoginTime}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </ul>

            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_lowerproxy" style="display: none;border: none;">
                <div class="col-sm-12" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <form class="form-horizontal" id="lowerProxyForm" method="post">
                        <table class="col-sm-12 table table-bordered">
                            <thead>
                            <tr style="background: #FAEBD7;">
                                <th scolumn="nickName">昵称</th>
                                <th scolumn="balance">余额</th>
                                <th scolumn="totalConsume">购彩/中奖金额</th>
                                <th scolumn="totalRecharge">充值/提现金额</th>
                                <th scolumn="tgUserNumber">推广用户</th>
                                <th scolumn="sumMoney">代理总销量</th>
                                <th scolumn="registerTime">注册时间</th>
                                <th scolumn="bankIsBind">银行绑定</th>
                                <th scolumn="isWhite">白名单</th>
                                <th scolumn="loginDegree">登录</th>
                                <th scolumn="lastLoginTime">上次登录</th>
                            </tr>
                            </thead>
                            <tbody style="background: #fff;">
                            <c:forEach items="${lowerproxy}" var="dt">
                                <tr>
                                    <td>${dt.nickName}</td>
                                    <td>${dt.balance}</td>
                                    <td>${dt.totalConsume}/${dt.totalAward}</td>
                                    <td>${dt.totalRecharge}/${dt.totalWithDraw}</td>
                                    <td>${dt.tgUserNumber}人</td>
                                    <td>${dt.dlSaleMoney+dt.dlSaleZhuiHaoMoney+dt.dlUserSaleMoney+dt.dlUserZhuiHaoSaleMoney}元</td>
                                    <td>${dt.registerTime}</td>
                                    <td>${dt.bankIsBind==false?'未绑定':'已绑定'}</td>
                                    <td>${dt.isWhite==0?'未开通':'已开通'}</td>
                                    <td>${dt.loginDegree}次</td>
                                    <td>${dt.lastLoginTime}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </form>
                </div>
            </ul>

            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_backdetail" style="display: none;border: none;">
                <div class="col-sm-12" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <table class="col-sm-12 table table-bordered">
                        <thead>
                            <tr style="background: #FAEBD7;">
                                <th scolumn="type">类型</th>
                                <th scolumn="lotteryName">彩种名</th>
                                <th scolumn="nickName">购彩用户</th>
                                <th scolumn="schemeOrderId">订单号</th>
                                <th scolumn="schemeMoney">订单金额</th>
                                <th scolumn="rate">返点比</th>
                                <th scolumn="currentRebateMoney">应返</th>
                                <th scolumn="lastBalanceRebate">账变前金额</th>
                                <th scolumn="balanceRebate">账变后金额</th>
                                <th scolumn="createTime">返利时间</th>
                            </tr>
                        </thead>
                        <tbody style="background: #fff;">
                            <c:forEach items="${backdetail}" var="dt">
                                <tr>
                                    <td>${dt.type=='0'?'进账':'提取'}</td>
                                    <td>${dt.shortName}</td>
                                    <td>${dt.nickName}</td>
                                    <td>${dt.schemeOrderId}</td>
                                    <td>${dt.schemeMoney}</td>
                                    <td>${dt.rate}</td>
                                    <td>${dt.currentRebateMoney}</td>
                                    <td>${dt.lastBalanceRebate}</td>
                                    <td>${dt.balanceRebate}</td>
                                    <td>${dt.createTime}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </ul>

            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_xiaodetail" style="display: none;border: none;">
                <div class="col-sm-12" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <table class="col-sm-12 table table-bordered">
                        <thead>
                        <tr style="background: #FAEBD7;">
                            <th scolumn="nickName">昵称</th>
                            <th scolumn="isSale">头衔</th>
                            <th scolumn="localMoney">本月销量</th>
                            <th scolumn="registerTime">注册时间</th>
                            <th scolumn="loginDegree">登录次数</th>
                            <th scolumn="lastLoginTime">上次登录</th>
                        </tr>
                        </thead>
                        <tbody style="background: #fff;">
                        <c:forEach items="${saledetail}" var="dt">
                            <tr>
                                <td>${dt.nickName}</td>
                                <td>${dt.isSale=='0'?'会员':dt.isSale=='1'?'销售员':'代理员'}</td>
                                <td>${dt.saleMoney}</td>
                                <td>${dt.registerTime}</td>
                                <td>${dt.loginDegree}</td>
                                <td>${dt.lastLoginTime}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </ul>
        </div>
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
    //点击用户返点tabs
    $('#dtInfoTabs button[tbsvalue]').on('click',function()
    {
        $('#dtInfoTabs button[tbsvalue]').removeClass('btn-scheme-detail-active');
        $(this).addClass('btn-scheme-detail-active');
        $('.dtInfo_cls').hide();
        $('#dtInfo_' + $(this).attr('tbsvalue')).show();
    });

})
</script>