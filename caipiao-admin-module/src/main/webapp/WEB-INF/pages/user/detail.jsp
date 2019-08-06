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
<div class="card-header" id="dt_user_card">
    <div class="planflom-header">
        <div class="card-header-title clearfix">
            <button type="button" class="card-close plus-icon p-guanbi"></button>
            <div class="pull-left clearfix" style="margin-top: 6px" id="dtInfoTabs">
                <button type="button" class="btn btn-scheme-detail-active" tbsvalue="base">基本信息</button>
                <c:if test="${params.isSale == 0}">
                    <button opauthority="btn_user_user_szfd" type="button" class="btn" style="margin-left: -2px;" tbsvalue="yhdetail">用户返点设置</button>
                    <button opauthority="btn_user_user_bdsjgs" type="button" class="btn" style="margin-left: -2px;" tbsvalue="yhcome">绑定用户归属</button>
                </c:if>
                <c:if test="${params.isSale == 1}">
                    <button opauthority="btn_user_user_szfd" type="button" class="btn" style="margin-left: -2px;" tbsvalue="yhdetail">销售返点上限</button>
                    <button type="button" class="btn" tbsvalue="loweruser">下级用户</button>
                    <button type="button" class="btn" tbsvalue="lowerproxy">下级代理</button>
                    <button opauthority="btn_user_user_bdsjgs" type="button" class="btn" tbsvalue="yhout">下级用户转出</button>
                </c:if>
                <c:if test="${params.isSale == 2}">
                    <button opauthority="btn_user_user_szfd" type="button" class="btn" style="margin-left: -2px;" tbsvalue="yhdetail">代理返点设置</button>
                    <button opauthority="btn_user_user_bdsjgs" type="button" class="btn" style="margin-left: -2px;" tbsvalue="yhcome">绑定用户归属</button>
                </c:if>
                <button opauthority="menu_order_scheme" type="button" class="btn" style="margin-left: -2px;" tbsvalue="fadetail">方案记录</button>
                <button opauthority="menu_user_recharge" type="button" class="btn" style="margin-left: -2px;" tbsvalue="czdetail">充值流水</button>
                <button opauthority="menu_user_tx" type="button" class="btn" style="margin-left: -2px;" tbsvalue="txdetail">提现流水</button>
                <button opauthority="menu_user_account" type="button" class="btn" style="margin-left: -2px;" tbsvalue="zhdetail">账户流水</button>
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
                    <span class="abstract-label">手机号(IP)</span>
                    <span class="abstract-value">${params.mobile}(${params.registerIp})</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">是否为管理员</span>
                    <span class="abstract-value">${params.isAdmin == 0? '非管理员' : '管理员'}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">用户头衔</span>
                    <span class="abstract-value" id="isale" dv="${params.isSale}">${is}</span>
                    <span class="abstract-value"><a opauthority="btn_user_user_edit" href="javascript:" class="set_user_issale">修改</a></span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">用户昵称</span>
                    <span class="abstract-value" id="nickName">${params.nickName}&nbsp;(已更改:${params.updateUserNameNum}次)</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">身份证号</span>
                    <span class="abstract-value">${params.idCard!=null?params.idCard:'未实名'}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">用户状态</span>
                    <span class="abstract-value" id="yhzt" dv="${params.status}">${us}</span>
                    <span class="abstract-value"><a opauthority="btn_user_user_edit" href="javascript:" class="set_user_status">修改</a></span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">用户等级(安全等级:${secrityLevel}级)</span>
                    <span class="abstract-value" id="dvip">${vp}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">注册时间</span>
                    <span class="abstract-value"><fmt:formatDate value="${params.registerTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">黑白名单</span>
                    <span class="abstract-value" id="bmd" dv="${params.isWhite}">${params.isWhite=='1'?"白名单":params.isWhite=='2'?"黑名单":"普通"}</span>
                    <span class="abstract-value"><a opauthority="btn_user_user_edit" href="javascript:" class="set_user_iswhite" id="bmdValue">${params.isWhite=='1'?"设置黑名单":"设置白名单"}</a></span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">银行卡号</span>
                    <span class="abstract-value">${params.bankInfo != null? (params.bankInfo.bankCard != null? params.bankInfo.bankCard : '') : '无'}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">绑定信息</span>
                    <span class="abstract-value">${isBank=='0'?'未绑定':bankInfo}</span>
                </li>
                <c:if test="${params.isSale > 0}">
                <li class="abstract-item">
                    <span class="abstract-label"><b>邀请码</b></span>
                    <span class="abstract-value"><b>${params.qq}</b></span>
                </li>
                </c:if>
                <li class="abstract-item" style="height: 43px;border-bottom: 1px solid #e8ebee;width: 97%;color:#3e4359;font-size: 14px;font-weight: bold;">账户信息</li>
                <li class="abstract-item">
                    <span class="abstract-label">账户余额</span>
                    <span class="abstract-value">
                        ${account.balance!=null?account.balance:0.0}&nbsp;元&nbsp;&nbsp;&nbsp;&nbsp;
                        <a opauthority="btn_user_user_jkkk" href="javascript:;" class="label label-danger dt_acchangeBtn" optype="0">加款</a>&nbsp;&nbsp;
                        <a opauthority="btn_user_user_jkkk" href="javascript:;" class="label label-warning dt_acchangeBtn" optype="1">扣款</a>
                    </span>
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
                <div class="col-sm-12" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <form class="form-horizontal" id="saveRebate" method="post">
                        <table class="col-sm-12 table table-bordered">
                            <thead>
                            <tr class="font-bold" style="background: #FAEBD7;">
                                <th>彩种</th>
                                <th>设置返点</th>
                                <th>设置时间</th>
                                <th></th>
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
                                    <td rowspan="${rebate_size}" style="vertical-align: middle;text-align: center;">
                                        <%--<a href="javascript:;" class="label label-info" id="save">立即保存</a>--%>
                                        <button opauthority="btn_user_user_szfd" type="button" class="btn btn-info" id="save">立即保存</button>
                                    </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </form>
                </div>
            </ul>

            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_yhcome" style="display: none;border: none;">
                <div class="col-sm-9" style="margin-left:-10px;overflow-y:auto;word-break:break-all;">
                    <form class="form-horizontal" id="userSetCome" method="post">
                        <table class="col-sm-9 table table-bordered">
                            <thead>
                            <tr style="background: #FAEBD7;">
                                <th colspan="3" style="color: red;">注意：该功能旨在给没有标签归属的用户设置上级代理或上级销售员.一旦绑定将无法更改,请谨慎操作</th>
                            </tr>
                            </thead>
                            <tbody style="background: #fff;">
                            <tr>
                                <input type="hidden" name="uid" id="uid" value="${params.id}">
                                <td valign="middle">绑定到目标代理或销售员手机号为</td>
                                <td><input type="text" class="form-control" placeholder="手机号" name="smobile" id="smobile"/></td>
                                <td valign="middle"><a opauthority="btn_user_user_bdsjgs" href="javascript:;" class="label label-info" id="quedingcome">确定绑定</a></td>
                            </tr>
                            </tbody>
                        </table>
                    </form>
                </div>
            </ul>

            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_yhout" style="display: none;border: none;">
                <div class="col-sm-9" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <form class="form-horizontal" id="userOutRebate" method="post">
                        <table class="col-sm-9 table table-bordered">
                            <thead>
                            <tr style="background: #FAEBD7;">
                                <th colspan="3" style="color: red;">注意：该功能旨在将销售员下属的所有代理和用户转到另一名有效的销售员名下 请谨慎操作</th>
                            </tr>
                            </thead>
                            <tbody style="background: #fff;">
                            <tr>
                                <input type="hidden" name="saleId" id="saleId" value="${params.id}">
                                <td align="right" width="180"><div style="margin-top: 8px;">转到目标销售员手机号为</div></td>
                                <td><input type="text" class="form-control" placeholder="手机号" name="mobile" id="mobile"/></td>
                                <td>
                                    <div style="margin-top: 8px;"><a opauthority="btn_user_user_bdsjgs" href="javascript:;" class="label label-info" id="quedingsave">确定转出</a></div>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </form>
                </div>
            </ul>

            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_loweruser" style="display: none;border: none;">
                <div class="col-sm-12" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <form class="form-horizontal" id="lowerUserForm" method="post">
                        <table class="col-sm-12 table table-bordered">
                            <thead>
                            <tr class="font-bold" style="background: #FAEBD7;">
                                <th scolumn="nickName">昵称</th>
                                <th scolumn="balance">余额</th>
                                <th scolumn="totalConsume">购彩/中奖金额</th>
                                <th scolumn="totalRecharge">充值/提现金额</th>
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

            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_lowerproxy" style="display: none;border: none;">
                <div class="col-sm-12" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <form class="form-horizontal" id="lowerProxyForm" method="post">
                        <table class="col-sm-12 table table-bordered">
                            <thead>
                            <tr class="font-bold" style="background: #FAEBD7;">
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
            <!-- 用户方案记录 -->
            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_fadetail" style="display: none;border: none;">
                <div class="col-sm-12" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <table class="col-sm-12 table table-bordered">
                        <thead>
                        <tr class="font-bold" style="background: #FAEBD7;">
                            <th>方案编号</th>
                            <th>彩种</th>
                            <th>期次</th>
                            <th>玩法</th>
                            <th>类型</th>
                            <th>状态</th>
                            <th>倍数</th>
                            <th>金额</th>
                            <th>计奖/派奖</th>
                            <th>下单时间</th>
                            <th>来源</th>
                            <th>奖金</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${schemeList}" var="scheme">
                        <tr style="background: #fff;">
                            <td>${scheme.schemeOrderId}</td>
                            <td>${scheme.lotteryName}</td>
                            <td>${scheme.period}</td>
                            <td>${scheme.schemePlayType}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${scheme.schemeType == 0}">普通</c:when>
                                    <c:when test="${scheme.schemeType == 1}">追号</c:when>
                                    <c:when test="${scheme.schemeType == 2}">优化</c:when>
                                    <c:when test="${scheme.schemeType == 3}">跟单</c:when>
                                    <c:when test="${scheme.schemeType == 4}">神单</c:when>
                                </c:choose>
                            </td>
                            <td>${scheme.schemeStatusDesc}</td>
                            <td>${scheme.schemeMultiple}</td>
                            <td>${scheme.schemeMoney}</td>
                            <td>${scheme.jjpj}</td>
                            <td>${scheme.createTime}</td>
                            <td>${scheme.clientSourceName}</td>
                            <td>${scheme.prizeTax}</td>
                        </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <li class="abstract-item col-sm-12" style="margin-top: -20px;float: right;width: 100px;border: none;margin-right: 7px">
                    <a href="javascript:;" class="label label-success dt_showmore" userId="${params.id}" forMenuCode="menu_order_scheme">查看更多</a>
                </li>
            </ul>
            <!-- 用户充值流水 -->
            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_czdetail" style="display: none;border: none;">
                <div class="col-sm-12" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <table class="col-sm-12 table table-bordered">
                        <thead>
                        <tr class="font-bold" style="background: #FAEBD7;">
                            <th>平台流水号</th>
                            <th>金额</th>
                            <th>订单状态</th>
                            <th>付款银行</th>
                            <th>充值渠道</th>
                            <th>渠道流水号</th>
                            <th>发起时间</th>
                            <th>处理完成时间</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${rechargeList}" var="recharge">
                            <tr style="background: #fff;">
                                <td>${recharge.payId}</td>
                                <td>${recharge.money}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${recharge.status == -1}"><span class="label label-failed">处理失败</span></c:when>
                                        <c:when test="${recharge.status == 0}"><span class="label label-warning">待处理</span></c:when>
                                        <c:when test="${recharge.status == 1}"><span class="label label-danger">等待重新处理</span></c:when>
                                        <c:when test="${recharge.status == 2}"><span class="label label-info">处理中</span></c:when>
                                        <c:when test="${recharge.status == 3}"><span class="label label-success">处理成功</span></c:when>
                                    </c:choose>
                                </td>
                                <td>${recharge.bankTypeDesc}</td>
                                <td>${recharge.channelDesc}</td>
                                <td>${recharge.channelPayId}</td>
                                <td>${recharge.createTime}</td>
                                <td>${recharge.doneTime}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <li class="abstract-item col-sm-12" style="margin-top: -20px;float: right;width: 100px;border: none;margin-right: 7px">
                    <a href="javascript:;" class="label label-success dt_showmore" userId="${params.id}" forMenuCode="menu_user_recharge">查看更多</a>
                </li>
            </ul>
            <!-- 用户提现流水 -->
            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_txdetail" style="display: none;border: none;">
                <div class="col-sm-12" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <table class="col-sm-12 table table-bordered">
                        <thead>
                        <tr class="font-bold" style="background: #FAEBD7;">
                            <th>平台流水号</th>
                            <th>金额</th>
                            <th>订单状态</th>
                            <th style="max-width: 300px;">提款银行</th>
                            <th>渠道</th>
                            <th>渠道流水号</th>
                            <th>发起时间</th>
                            <th>处理完成时间</th>
                            <th>交易备注</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${txList}" var="tx">
                            <tr style="background: #fff;">
                                <td>${tx.payId}</td>
                                <td>${tx.money}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${tx.status == -1}"><span class="label label-failed">处理失败</span></c:when>
                                        <c:when test="${tx.status == 0}"><span class="label label-warning">待处理</span></c:when>
                                        <c:when test="${tx.status == 1}"><span class="label label-danger">等待重新处理</span></c:when>
                                        <c:when test="${tx.status == 2}"><span class="label label-info">处理中</span></c:when>
                                        <c:when test="${tx.status == 3}"><span class="label label-success">处理成功</span></c:when>
                                    </c:choose>
                                </td>
                                <td style="max-width: 300px;">
                                    ${tx.bankInfo.bankName} ${tx.bankInfo.subBankName}｜${tx.bankInfo.bankProvince} - ${tx.bankInfo.bankCity}
                                </td>
                                <td>${tx.channelDesc}</td>
                                <td>${tx.channelPayId}</td>
                                <td>${tx.createTime}</td>
                                <td>${tx.doneTime}</td>
                                <td>${tx.remark}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <li class="abstract-item col-sm-12" style="margin-top: -20px;float: right;width: 100px;border: none;margin-right: 7px">
                    <a href="javascript:;" class="label label-success dt_showmore" userId="${params.id}" forMenuCode="menu_user_tx">查看更多</a>
                </li>
            </ul>
            <!-- 用户账户流水 -->
            <ul class="abstract-list clearfix dtInfo_cls" id="dtInfo_zhdetail" style="display: none;border: none;">
                <div class="col-sm-12" style="margin-left:-15px;overflow-y:auto;word-break:break-all;">
                    <table class="col-sm-12 table table-bordered">
                        <thead>
                        <tr class="font-bold" style="background: #FAEBD7;">
                            <th>交易类型</th>
                            <th>交易渠道</th>
                            <th>交易金额</th>
                            <th>交易前余额</th>
                            <th>交易后余额</th>
                            <th>交易时间</th>
                            <th>状态</th>
                            <th>备注</th>
                            <th>业务关联编号</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${detailList}" var="acdetail">
                            <tr style="background: #fff;">
                                <td>
                                    <c:choose>
                                        <c:when test="${acdetail.inType == false}"><span class="label label-success">进账</span></c:when>
                                        <c:when test="${acdetail.inType}"><span class="label label-danger">出账</span></c:when>
                                    </c:choose>
                                </td>
                                <td>${acdetail.channelDesc}</td>
                                <td>${acdetail.money}</td>
                                <td>${acdetail.lastBalance}</td>
                                <td>${acdetail.balance}</td>
                                <td>${acdetail.createTime}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${acdetail.status == -1}"><span class="label label-failed">无效</span></c:when>
                                        <c:when test="${acdetail.status == 0}"><span class="label label-info">处理中</span></c:when>
                                        <c:when test="${acdetail.status == 1}"><span class="label label-success">已完成（有效）</span></c:when>
                                    </c:choose>
                                </td>
                                <td>${acdetail.remark}</td>
                                <td>${acdetail.businessId}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <li class="abstract-item col-sm-12" style="margin-top: -20px;float: right;width: 100px;border: none;margin-right: 7px">
                    <a href="javascript:;" class="label label-success dt_showmore" userId="${params.id}" forMenuCode="menu_user_account">查看更多</a>
                </li>
            </ul>
        </div>
    </div>
</div>
<div class="modal fade" id="dt_UserDetailModal" useStaticDialog="1" style="z-index:1055;">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title">设置或取消用户白名单操作</h4>
            </div>
            <div class="modal-body clearfix">
                <p style="margin-top: 10px;"></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-save alertwarn btn_modal_cancel" data-dismiss="modal" id="confirmModalSureBtn">确定
                </button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="dt_StatusModal" useStaticDialog="1" style="z-index:1055;">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title">您正在进行用户状态操作</h4>
            </div>
            <div class="modal-body clearfix">
                <div class="form-group">
                    <label class="col-sm-3 control-label">用户状态变更</label>
                    <div class="col-sm-7" id="dt_userStatusDiv">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-save alertwarn btn_modal_cancel" data-dismiss="modal" id="confirmModalSureBtnStatus">确定
                </button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="dt_SaleModal" useStaticDialog="1" style="z-index:1055;">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title">您正在进行用户头衔操作</h4>
            </div>
            <div class="modal-body clearfix">
                <div class="form-group">
                    <label class="col-sm-3 control-label">用户头衔变更</label>
                    <div class="col-sm-7" id="dt_userSaleDiv">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-save alertwarn btn_modal_cancel" data-dismiss="modal" id="confirmModalSureBtnSale">确定
                </button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="dt_acchangeOperatorModal" useStaticDialog="1" style="z-index:1055;" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title">用户<span class="dt_acchange_desc"></span></h4>
            </div>
            <div class="modal-body clearfix">
                <form class="form-horizontal" id="dt_acchangeForm" method="post">
                    <input type="hidden" name="userId" value="${params.id}">
                    <input type="hidden" name="inType">
                    <div class="modal-bg col-sm-12">
                        <div class="form-group">
                            <label class="col-sm-2 control-label"><span class="dt_acchange_desc"></span>金额</label>
                            <div class="col-sm-8">
                                <input type="text" class="form-control" placeholder="请输入金额" name="money">
                            </div>
                        </div>
                        <div class="form-group dt_acchangeTxtype_cls" style="display: none;">
                            <label class="col-sm-2 control-label">提现方式</label>
                            <div class="col-sm-8">
                                <select class="form-control" name="txtype">
                                    <option value="0">全部可提现</option>
                                    <option value="1">全部不可提现</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-2 control-label">备注</label>
                            <div class="col-sm-8">
                                <textarea class="form-control" placeholder="请输入备注(说明)" name="remark"></textarea>
                            </div>
                        </div>
                    </div>
                 </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-save alertwarn btn_modal_cancel" data-dismiss="modal" id="dt_acchangeSureBtn">确定
                </button>
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
    initCreatedNode('#dt_user_card');
    var userStatusJson = new Object();
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

    //用户白名单设置
    $('.set_user_iswhite').on('click',function(){
        var $modalNode = $('#dt_UserDetailModal');
        var confirmMsg = "确定将用户 ";
        confirmMsg += $('#nickName').html();
        confirmMsg += $('#bmd').attr('dv')=='1' ? " 设置黑名单吗？" : " 添加到白名单吗？";
        $modalNode.find('div.modal-body p').html(confirmMsg);
        var data = {id:${params.id}, isWhite:($("#bmd").attr('dv')=='0' || $("#bmd").attr('dv')=='2') ? 1 : 2};
        $modalNode.attr('optype',1).attr('editDatas',JSON.stringify(data)).modal('show');
    });

    //点击操作确认-确定
    $('#confirmModalSureBtn').on('click',function()
    {
        var $modalNode = $(this).parents('div.modal').first();
        var data = new Object();
        var url = '';
        if($modalNode.attr('optype') == 1)
        {
            data = $.parseJSON($modalNode.attr('editDatas'));
            url = '${pageContext.request.contextPath}/user/edit'
        }
        $.ajax({
            url : url,
            type : 'post',
            dataType : 'json',
            data : data,
            success : function (json)
            {
                showoplayer(json);
                $("#bmd").attr('dv',json.bmdValue);
                $("#bmd").html(json.bmdValue=='1'?"白名单":json.bmdValue=='2'?'黑名单':'普通');
                $("#bmdValue").html(json.bmdValue=='1'?"设置黑名单":"设置白名单");
            }
        });
    });

    //用户状态设置
    $('.set_user_status').on('click',function(){
        //发送请求
        $.ajax({
            url: '${pageContext.request.contextPath}/user/getUserModuleDown?module=1',
            type:'post',
            dataType:'json',
            success : function(data)
            {
                var html = '';
                $.each(data.datas.list, function(i,m)
                {
                    userStatusJson[$(m).attr('id')] = $(m).attr('value');
                    html += '<span class="user_detail_radio"><input type="radio" name="status_radio" value="' + $(m).attr('id') + '">'+$(m).attr('value')+'</span>';
                });
                $('#dt_userStatusDiv').html(html);
                $('#dt_userStatusDiv input[name="status_radio"][value="'+($("#yhzt").attr('dv'))+'"]').attr("checked","checked");
                $('#dt_StatusModal').modal('show');
            }
        });
    });
    //点击确定
    $('#confirmModalSureBtnStatus').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/user/edit',
            type : 'post',
            dataType : 'json',
            data : {status:$("input[name='status_radio']:checked").val(),id:${params.id}},
            success : function(json)
            {
                showoplayer(json);
                $("#yhzt").attr('dv',json.statusValue);
                $("#yhzt").html(userStatusJson[json.statusValue]);
            }
        });
    });

    //用户头衔设置
    $('.set_user_issale').on('click',function(){
        //发送请求
        $.ajax({
            url: '${pageContext.request.contextPath}/user/getUserModuleDown?module=5',
            type:'post',
            dataType:'json',
            success : function(data)
            {
                var html = '';
                $.each(data.datas.list, function(i,m)
                {
                    userStatusJson[$(m).attr('id')] = $(m).attr('value');
                    html += '<span class="user_detail_radio"><input type="radio" name="sale_radio" value="' + $(m).attr('id') + '">'+$(m).attr('value')+'</span>';
                });
                $('#dt_userSaleDiv').html(html);
                $('#dt_userSaleDiv input[name="sale_radio"][value="'+($("#isale").attr('dv'))+'"]').attr("checked","checked");
                $('#dt_SaleModal').modal('show');
            }
        });
    });

    //点击确定
    $('#confirmModalSureBtnSale').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/user/edit',
            type : 'post',
            dataType : 'json',
            data : {isSale:$("input[name='sale_radio']:checked").val(),code:'${params.qq}',id:${params.id}},
            success : function(json)
            {
                showoplayer(json);
                $("#isale").attr('dv',json.saleValue);
                $("#isale").html(userStatusJson[json.saleValue]);
            }
        });
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
            url : '${pageContext.request.contextPath}/user/saveRebate',
            type : 'post',
            dataType : 'json',
            data : $("#saveRebate").serializeArray(),
            success : function(data)
            {
                showoplayer(data);
            }
        });
    });

    //点击转出
    $('#quedingsave').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/sale/lower/change',
            type : 'post',
            dataType : 'json',
            data : $("#userOutRebate").serializeArray(),
            success : function(data)
            {
                showoplayer(data);
            }
        });
    });

    //点击绑定上级归属
    $('#quedingcome').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/user/come',
            type : 'post',
            dataType : 'json',
            data : $("#userSetCome").serializeArray(),
            success : function(data)
            {
                showoplayer(data);
            }
        });
    });

    //点击查看更多
    $('.dt_showmore').on('click',function()
    {
        var targetMenuNode = $('#left_sidbar_menulist li[mcode="' + $(this).attr('forMenuCode') + '"]',window.parent.document);
        var links = targetMenuNode.attr('links');
        var newlinks = links + (links.indexOf('?') > 0? ('&userId=' + $(this).attr('userId')) : ("?userId=" + $(this).attr('userId')));
        targetMenuNode.attr('links',newlinks);
        targetMenuNode.click();
        targetMenuNode.attr('links',links);
    });
    //点击加款/扣款
    $('.dt_acchangeBtn').on('click',function()
    {
        var optype = $(this).attr('optype');
        $('#dt_acchangeOperatorModal').attr('optype',optype);
        $('#dt_acchangeForm input[name="inType"]').val(optype);
        if(optype == 0)
        {
            $('#dt_acchangeOperatorModal .dt_acchange_desc').html("加款");
            $('#dt_acchangeForm .dt_acchangeTxtype_cls').show();
        }
        else if(optype == 1)
        {
            $('#dt_acchangeOperatorModal .dt_acchange_desc').html("扣款");
            $('#dt_acchangeForm .dt_acchangeTxtype_cls').hide();
        }
        $('#dt_acchangeForm')[0].reset();
        $('#dt_acchangeOperatorModal').modal('show');
    });
    //点击确定加款/扣款
    $('#dt_acchangeSureBtn').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/user/account/detail/change',
            type : 'post',
            dataType : 'json',
            data : $("#dt_acchangeForm").serializeArray(),
            success : function(data)
            {
                showoplayer(data);
            }
        });
    });
    $('#dt_acchangeForm select').selectpicker();
})
</script>