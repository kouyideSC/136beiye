<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="card-header">
    <div class="planflom-header">
        <div class="card-header-title clearfix">
            <button type="button" class="card-close plus-icon p-guanbi"></button>
            <div class="pull-left clearfix" style="margin-top: 6px">
                <input type="hidden" id="tid" value="${params.id}">
                <%--<button opauthority="menu_order_scheme" class="btn btn-info do-condition">方案详情</button>
                <button opauthority="menu_user_user" class="btn btn-info do-condition" style="margin-left: 10px;">用户中心</button>--%>
                <c:if test="${params.ticketStatus == 0}">
                    <button opauthority="menu_shopchupiao_query" class="btn btn-info do-condition" id="outsuccess">打票成功</button>
                    <button opauthority="menu_shopchupiao_query" class="btn btn-info do-condition" style="margin-left: 10px;" id="outfail">打票失败</button>
                </c:if>
            </div>
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
                    <span class="abstract-label">推送时间</span>
                    <span class="abstract-value">${params.createTime}</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">出票时间</span>
                    <span class="abstract-value">${params.outTicketTime}</span>
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
                    <span class="abstract-label">网站计奖税前奖金</span>
                    <span class="abstract-value">${sq}元</span>
                </li>
                <li class="abstract-item">
                    <span class="abstract-label">网站计奖税后奖金</span>
                    <span class="abstract-value">${sh}元</span>
                </li>
                <li class="abstract-item" style="width: 40%;">
                    <span class="abstract-label">方案票号</span>
                    <span class="abstract-value">${params.ticketId}</span>
                </li>
                <li class="abstract-item" style="width: 55%;">
                    <span class="abstract-label">出票商票号</span>
                    <span class="abstract-value">${params.voteTicketId}</span>
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
                <!--竞彩详情-->
                <c:if test="${isJc==true}">
                    <li class="abstract-item" style="width: 99%;">
                        <span class="abstract-label"><b style="color: #b92c28"></b></span>
                        <span class="abstract-value"><b style="color: #b92c28">票面玩法【${params.choose.wfType}】，&nbsp;串关方式【${params.choose.ggfs}】，&nbsp;投注倍数【${params.multiple}倍】，&nbsp;投注总金额【${params.money}元】，&nbsp;截止时间【${params.endTime}】</b></span>
                    </li>
                            <div class="col-sm-12" style="width: 94%;">
                                <table class="col-sm-12 table table-bordered">
                                    <c:forEach items="${params.choose.match}" var="tzxx">
                                        <tr style="background: #FAEBD7;">
                                            <td>竞彩编号：${tzxx.week}</td>
                                            <td>
                                                <table class="col-sm-12" style="margin-bottom:0;font-size: 13px;">
                                                    <tr style="background: #FAEBD7;">
                                                        <td class="col-sm-4" style="position: relative;left: -13px;">
                                                            对阵：
                                                                ${params.choose.jctype == 1? tzxx.hname : tzxx.gname}${params.choose.jctype == 1? "（主）" : ""}
                                                            vs
                                                                ${params.choose.jctype == 1? tzxx.gname : tzxx.hname}${params.choose.jctype == 2? "（主）" : ""}
                                                        </td>
                                                        <td class="col-sm-2">赛事：${tzxx.lname}</td>
                                                        <td class="col-sm-3">比赛时间：${tzxx.mtime}</td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>
                                        <tr style="background: #fff;">
                                            <td>${tzxx.xwf}</td>
                                            <td>${tzxx.ccxxs}</td>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </div>
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

                <!--老足彩详情-->
                <c:if test="${isZc==true}">
                    <li class="abstract-item" style="width: 94%;">
                        <span class="abstract-label"><b style="color: #b92c28">该票实体店打票信息</b></span>
                        <span class="abstract-value"><b style="color: #b92c28">票面彩种【${lts=='' ? params.shortName : lts}】，&nbsp;投注倍数【${params.multiple}倍】，&nbsp;投注总金额【${params.money}元】</b></span>
                    </li>
                    <div class="col-sm-12" style="width: 85%;">
                        <table class="col-sm-12 table table-bordered">
                            <tr style="background: #FAEBD7;">
                                <th>期次号</th>
                                <th>投注串【另注：#号=未选，3=胜，1=平，0=负】</th>
                                <%--<th>序号</th>
                                <th>赛事</th>
                                <th>对阵</th>
                                <th>比赛时间</th>
                                <th>投注项</th>--%>
                            </tr>
                            <tr style="background: #FAEBD7;">
                                <td>${params.period}</td>
                                <td>${params.choose.match}</td>
                            </tr>
                            <%--<c:forEach items="${params.choose.match}" var="tzxx">
                                <tr style="background: #FAEBD7;">
                                    <td>${tzxx.index}</td>
                                    <td>${tzxx.league}</td>
                                    <td>${tzxx.teamName}</td>
                                    <td>${tzxx.matchTime}</td>
                                    <td>${tzxx.ccxxs}</td>
                                </tr>
                            </c:forEach>--%>
                        </table>
                    </div>
                </c:if>

                <!--数字彩详情-->
                <c:if test="${isSzc==true}">
                    <li class="abstract-item" style="width: 94%;">
                        <span class="abstract-label"><b style="color: #b92c28">该票实体店打票信息</b></span>
                        <span class="abstract-value"><b style="color: #b92c28">票面彩种【${lts=='' ? params.shortName : lts}】，&nbsp;投注倍数【${params.multiple}倍】，&nbsp;投注总金额【${params.money}元】</b></span>
                    </li>
                    <div class="col-sm-12" style="width: 75%;">
                        <table class="col-sm-12 table table-bordered">
                            <tr style="background: #FAEBD7;">
                                <th>期次号</th>
                                <th>玩法</th>
                                <th>投注串</th>
                            </tr>
                            <tr style="background: #FAEBD7;">
                                <td>${params.period}</td>
                                <td>${params.choose.wfType}</td>
                                <td>${params.choose.match}</td>
                            </tr>
                        </table>
                    </div>
                </c:if>

                <!--冠亚军详情-->
                <c:if test="${isGyj==true}">
                    <li class="abstract-item" style="width: 94%;">
                        <span class="abstract-label"><b style="color: #b92c28">该票实体店打票信息</b></span>
                        <span class="abstract-value"><b style="color: #b92c28">票面彩种【${lts=='' ? params.shortName : lts}】，&nbsp;投注倍数【${params.multiple}倍】，&nbsp;投注总金额【${params.money}元】</b></span>
                    </li>
                    <div class="col-sm-12" style="width: 85%;">
                        <table class="col-sm-12 table table-bordered">
                            <tr style="background: #FAEBD7;">
                                <th>彩种</th>
                                <th>球队编号</th>
                                <th>球队中文翻译</th>
                            </tr>
                            <tr style="background: #FAEBD7;">
                                <td>${lts}</td>
                                <td>${params.choose.ttxxcode}</td>
                                <td>${params.choose.ttxx}</td>
                            </tr>
                        </table>
                    </div>
                </c:if>
            </ul>
        </div>
    </div>
</div>
<div class="tooltips"></div>
<div class="modal fade" id="operatorModal"></div>
<div class="card-wrap" id="detailCard" callback="querydatas"></div>
<div class="modal fade" id="confirmModalDetail" useStaticDialog="1" style="z-index:1050;">
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
                <button type="button" class="btn btn-save alertwarn btn_modal_cancel" data-dismiss="modal" id="confirmModalSureBtnDetail">确定
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
    //点击打票成功
    $('#outsuccess').bind('click',function()
    {
        var id = $("#tid").val();
        var $confirmModal = $('#confirmModalDetail');
        $confirmModal.find('.modal-body p').html('确定已成功打票了吗？');
        var arrays = new Array();
        arrays.push({id:id, optype:1});
        $confirmModal.attr('opdatas',JSON.stringify(arrays)).modal('show');
    });

    //点击打票失败
    $('#outfail').bind('click',function()
    {
        var id = $("#tid").val();
        var $confirmModal = $('#confirmModalDetail');
        $confirmModal.find('.modal-body p').html('确定该票出票失败吗？');
        var arrays = new Array();
        arrays.push({id:id, optype:2});
        $confirmModal.attr('opdatas',JSON.stringify(arrays)).modal('show');
    });

    //点击确认操作
    $('#confirmModalSureBtnDetail').bind('click',function()
    {
        var $confirmModal = $(this).parents('div.modal').first();
        //根据操作类型获取urlc
        var url = '${pageContext.request.contextPath}/ticket/shopquery/setTicketStatus';
        //发送请求
        if($.trim(url) != '')
        {
            $.ajax({
                url : url,
                type : 'post',
                data : {'datas': $confirmModal.attr('opdatas')},
                dataType : 'json',
                success : function(json)
                {
                    var $parentNode = $(".card-close").parents('.card-wrap').slice(0,1);
                    $parentNode.removeClass('card-wrap-show');
                    $parentNode.parent('.card-wrap-show').css('overflow-y','auto');
                    if($parentNode.attr("callback") && $parentNode.attr("callback") != '')
                    {
                        showoplayer(json);
                        window[$parentNode.attr("callback")]();
                    }
                }
            });
        }
    });
</script>