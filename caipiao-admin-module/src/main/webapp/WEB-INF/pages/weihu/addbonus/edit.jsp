<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editAddbonusDialog">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">编辑活动信息</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editAddbonusForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <div class="modal-bg col-sm-12">
                    <div class="form-group addAddbonus_cls" id="addActivityName">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>活动名称</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.activityName}" name="activityName" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addMaxMoney">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>活动额度</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.maxMoney}" name="maxMoney" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addBalance">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>已使用额度</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.balance}" name="balance" notEmpty="" disabled>
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addOutAccountUserId">
                        <label class="col-sm-3 control-label"><span class="check-star"></span>出款账户编号</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.outAccountUserId}" name="outAccountUserId">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addUserDayLimit">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>用户单日额度</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.userDayLimit}" name="userDayLimit" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addLotteryLimit">
                        <label class="col-sm-3 control-label">彩种限制</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.lotteryLimit}" name="lotteryLimit" notEmpty="">多个彩种用逗号隔开
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addLeagueNameLimit">
                        <label class="col-sm-3 control-label">赛事限制</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.leagueNameLimit}" name="leagueNameLimit" notEmpty="">多个赛事用逗号隔开
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addMatchCode">
                        <label class="col-sm-3 control-label">场次号限制</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.matchCode}" name="matchCode" notEmpty="">只能设置单场,针对单关加奖
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addPassType">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>串关方式</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.passType}" name="passType" notEmpty="">仅支持单个串关方式
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="schemeMoneyLimit">
                        <label class="col-sm-3 control-label">方案金额限制</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.schemeMoneyLimit}" name="schemeMoneyLimit" notEmpty="">方案最低金额限制
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addIsWithDraw">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>是否支持提现</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="isWithDraw" id="edit_IsWithDraw">
                                <option value="0">不可提现</option>
                                <option value="1">可提现</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addStatus">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>状态</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="status" id="edit_status">
                                <option value="0">停用</option>
                                <option value="1">启用</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addBonusRate">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>加奖比例</label>
                        <div class="col-sm-7">
                            <textarea class="form-control" name="addBonusRate" notEmpty="">${params.addBonusRate}</textarea>举例如下配置</br>
                            1$2000$0.05/2001$5000$0.08/5001$*$0.1</br>该配置表示奖金范围</br>1~2000元加奖5%</br>2001~5000元加奖8%</br>5000以上加奖10%
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addWeekLimit">
                        <label class="col-sm-3 control-label">星期限制</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.weekLimit}" name="weekLimit" notEmpty="">周一~周六分别使用数字1~6,周日使用0
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addBeginTime">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>生效时间</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control datetimepicker" value="${params.beginTime}" name="beginTime" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addAddbonus_cls" id="addEndTime">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>结束时间</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control datetimepicker" value="${params.endTime}" name="endTime" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editAddbonusSureBtn">确定</button>
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

    $('#edit_IsWithDraw').selectpicker("val", '${params.isWithDraw}');
    $('#edit_status').selectpicker("val", '${params.status}');

    //点击确定
    $('#editAddbonusSureBtn').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/addbonus/edit',
            type : 'post',
            dataType : 'json',
            data : $("#editAddbonusForm").serializeArray(),
            success : function(data)
            {
                $('#editAddbonusDialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        });
    });
});
</script>