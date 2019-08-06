<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editPeriodDialog" style="min-width: 1000px;">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">编辑期次</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editPeriodForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <input type="hidden" name="lotteryId" value="${params.lotteryId}">
                <input type="hidden" name="period" value="${params.period}">
                <input type="hidden" name="prizeGrade" id="editPeriodPrizeGrade">
                <input type="hidden" name="updateFlag" id="editPeriodUpdateFlag">
                <input type="hidden" name="matches" id="editPeriodMatches">
                <div class="modal-bg col-sm-12 editBaseInfoDiv_cls">
                    <h2 class="modal-title">基础信息</h2>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">彩种</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.lotteryName}" disabled>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">期次</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.period}" disabled>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">销售状态</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="sellStatus" id="editPeriodSellStatusSelect">
                                <option value="-1">已取消</option>
                                <option value="0">已停售</option>
                                <option value="1">销售中</option>
                                <option value="2">已截止</option>
                                <option value="3">未开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">开始销售时间</label>
                        <div class="col-sm-7 config-date">
                            <input class="form-control datetimepicker" type="text" name="sellStartTime" value="${params.sellStartTime}" notEmpty>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">截止销售时间</label>
                        <div class="col-sm-7 config-date">
                            <input class="form-control datetimepicker" type="text" name="sellEndTime" value="${params.sellEndTime}" notEmpty>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">官方截止时间</label>
                        <div class="col-sm-7 config-date">
                            <input class="form-control datetimepicker" type="text" name="authorityEndTime" value="${params.authorityEndTime}" notEmpty>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">覆盖更新标记</label>
                        <div class="col-sm-9" style="position: relative;top: -3px;">
                            <input type="checkbox" id="editPeriodUpdateFlagCheckbox">&nbsp;标记为不覆盖更新&nbsp;&nbsp;
                        </div>
                    </div>
                </div>
                <div class="modal-bg col-sm-12">
                    <h2 class="modal-title">奖级信息</h2>
                    <div class="control-label">
                        <table class="table table-bordered table-money">
                            <thead>
                                <tr class="font-bold" style="background: #f8f8f8;">
                                    <th width="80">奖级名称</th>
                                    <c:choose>
                                        <c:when test="${params.lotteryId == 1500}">
                                            <th>单注奖金</th>
                                            <th>追加奖金</th>
                                            <th>加奖奖金</th>
                                            <th>追加加奖奖金</th>
                                            <th>中奖注数</th>
                                            <th>追加注数</th>
                                        </c:when>
                                        <c:otherwise>
                                            <th>单注奖金</th>
                                            <th>加奖奖金</th>
                                            <th>中奖注数</th>
                                        </c:otherwise>
                                    </c:choose>
                                </tr>
                            </thead>
                            <tbody id="editPeriodPrizeGradeTbody" style="background: #fff;"></tbody>
                        </table>
                    </div>
                </div>
                <c:if test="${params.lotteryId == 1800 || params.lotteryId == 1810 || params.lotteryId == 1820 || params.lotteryId == 1830}">
                    <div class="modal-bg col-sm-12">
                        <h2 class="modal-title">对阵信息</h2>
                        <div class="control-label">
                            <table class="table table-bordered table-money">
                                <thead>
                                <tr class="font-bold" style="background: #f8f8f8;">
                                    <th style="width: 6%;">编号</th>
                                    <th style="width: 10%;">赛事</th>
                                    <th style="width: 10%;">主队</th>
                                    <th style="width: 10%;">客队</th>
                                    <th style="width: 10%;">胜赔</th>
                                    <th style="width: 10%;">平赔</th>
                                    <th style="width: 10%;">负赔</th>
                                    <th style="width: 22%;">比赛时间</th>
                                    <th style="width: 10%;">比分</th>
                                </tr>
                                </thead>
                                <tbody id="editPeriodMatchsTbody" style="background: #fff;"></tbody>
                            </table>
                        </div>
                    </div>
                </c:if>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editPeriodSureBtn">确定</button>
        </div>
    </div>
</div>
<script>
$(function()
{
    //初始化页面插件
    initPagePlugins('#editPeriodDialog',function()
    {
        var $dialogNode = $('#editPeriodDialog');
        $dialogNode.find('.config-date input').css('padding-left','20px');
        $dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
        if('${params.sellStatus}' == '-1' || '${params.sellStatus}' == '2')
        {
            $dialogNode.find(".editBaseInfoDiv_cls input,.editBaseInfoDiv_cls select").attr("disabled","disabled");
        }
    });
    //初始化期次销售状态下拉框默认选中
    $('#editPeriodSellStatusSelect').selectpicker('val','${params.sellStatus}');

    //初始化覆盖更新标记复选框默认选中
    if('${params.updateFlag}' == 'true')
    {
        $('#editPeriodUpdateFlagCheckbox').attr('checked','checked');
    }
    //初始化奖级信息
    var prizeGrade = '${params.prizeGrade}';
    if(prizeGrade != '' && prizeGrade != 'null')
    {
        prizeGrade = $.parseJSON(prizeGrade);
        if(!$.isEmptyObject(prizeGrade))
        {
            var html = '';
            var glhtml = '';
            for(var key in prizeGrade)
            {
                if(prizeGrade[key] == null || isStringOrNumber(prizeGrade[key]))
                {
                    glhtml += '<div class="form-group editPeriod_prizeGradeFormGroup">';
                    glhtml += '<label class="col-sm-3 control-label">' + key + '</label>';
                    glhtml += '<div class="col-sm-7">';
                    glhtml += '<input type="text" class="form-control" value="' + (prizeGrade[key] || (prizeGrade[key] == 0? 0 : '')) + '">';
                    glhtml += '</div></div>';
                }
                else
                {
                    html += '<tr>';
                    html += '<td>' + key + '</td>';
                    html += '<td><input class="form-control" value="' + prizeGrade[key]['单注奖金'] + '"></td>';
                    <c:if test="${params.lotteryId == 1500}">
                    if(prizeGrade[key]['追加奖金'] == undefined)
                    {
                        html += '<td><input class="form-control" value="--" disabled></td>';
                    }
                    else
                    {
                        html += '<td><input class="form-control" value="' + prizeGrade[key]['追加奖金'] + '"></td>';
                    }
                    </c:if>
                    html += '<td><input class="form-control" value="' + prizeGrade[key]['加奖奖金'] + '"></td>';
                    <c:if test="${params.lotteryId == 1500}">
                    html += '<td><input class="form-control" value="' + (prizeGrade[key]['追加加奖奖金'] || '0') + '"></td>';
                    </c:if>
                    if(prizeGrade[key]['中奖注数'] == undefined)
                    {
                        html += '<td><input class="form-control" value="--" disabled></td>';
                    }
                    else
                    {
                        html += '<td><input class="form-control" value="' + prizeGrade[key]['中奖注数'] + '"></td>';
                    }
                    <c:if test="${params.lotteryId == 1500}">
                    if(prizeGrade[key]['追加注数'] == undefined)
                    {
                        html += '<td><input class="form-control" value="--" disabled></td>';
                    }
                    else
                    {
                        html += '<td><input class="form-control" value="' + prizeGrade[key]['追加注数'] + '"></td>';
                    }
                    </c:if>
                    html += '</tr>';
                }
            }
            $('#editPeriodPrizeGradeTbody').html(html);
            $('#editPeriodPrizeGradeTbody').parents('div.control-label').first().before(glhtml);
        }
        else
        {
            $('#editPeriodPrizeGradeTbody').html('<tr><td colspan="5" style="color:red;">无奖级信息</td></tr>');
        }
    }
    else
    {
        $('#editPeriodPrizeGradeTbody').html('<tr><td colspan="5" style="color:red;">无奖级信息</td></tr>');
    }
    //初始化赛事对阵信息
    var matches = '${params.matches}';
    if(matches != '' && matches != 'null')
    {
        matches = $.parseJSON(matches);
        if(!$.isEmptyObject(matches))
        {
            var html = '';
            for(var key in matches)
            {
                html += '<tr>';
                html += '<td>' + key + '</td>';
                html += '<td><input class="form-control" value="' + matches[key]['matchname'] + '"></td>';
                html += '<td><input class="form-control" value="' + matches[key]['homeTeamView'] + '"></td>';
                html += '<td><input class="form-control" value="' + matches[key]['awayTeamView'] + '"></td>';
                html += '<td><input class="form-control" value="' + matches[key]['sheng'] + '"></td>';
                html += '<td><input class="form-control" value="' + matches[key]['ping'] + '"></td>';
                html += '<td><input class="form-control" value="' + matches[key]['fu'] + '"></td>';
                html += '<td class="config-date"><input class="form-control datetimepicker" value="' + matches[key]['matchTime'] + '"></td>';
                html += '<td><input class="form-control" value="' + matches[key]['score'] + '"></td>';
                html += '</tr>';
            }
            $('#editPeriodMatchsTbody').html(html);
            initPagePlugins('#editPeriodMatchsTbody',function()
            {
                var $dialogNode = $('#editPeriodDialog');
                $dialogNode.find('.config-date input').css('padding-left','20px');
                $dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
                $('#editPeriodMatchsTbody').find('.config-date b,.config-date i').remove();
            });
        }
        else
        {
            $('#editPeriodMatchsTbody').html('<tr><td colspan="9" style="color:red;">无赛事对阵信息</td></tr>');
        }
    }
    else
    {
        $('#editPeriodMatchsTbody').html('<tr><td colspan="9" style="color:red;">无赛事对阵信息</td></tr>');
    }
    //点击确定
    $('#editPeriodSureBtn').on('click',function()
    {
        //解析奖级信息
        var prizeGrade = new Object();
        $('#editPeriodPrizeGradeTbody').find('tr').each(function(i,tr)
        {
            var $tdnode = $(tr).find('td');
            if($tdnode.length >= 4)
            {
                var data = new Object();
                <c:choose>
                <c:when test="${params.lotteryId == 1500}">
                data['单注奖金'] = $tdnode.slice(1,2).find('input').val();
                var zjjj = $tdnode.slice(2,3).find('input').val();//追加奖金
                if(zjjj != '--')
                {
                    data['追加奖金'] = zjjj;
                }
                var jjjj = $tdnode.slice(3,4).find('input').val();//加奖奖金
                if(jjjj != '--')
                {
                    data['加奖奖金'] = jjjj;
                }
                var zjjjjj = $tdnode.slice(4,5).find('input').val();//追加加奖奖金
                if(zjjjjj != '--')
                {
                    data['追加加奖奖金'] = zjjjjj;
                }
                zjzs = $tdnode.slice(5,6).find('input').val()//中奖注数
                if(zjzs != '--')
                {
                    data['中奖注数'] = zjzs;
                }
                zjzs = $tdnode.slice(6,7).find('input').val();//追加注数
                if(zjzs != '--')
                {
                    data['追加注数'] = zjzs;
                }
                </c:when>
                <c:otherwise>
                data['单注奖金'] = $tdnode.slice(1,2).find('input').val();
                data['加奖奖金'] = $tdnode.slice(2,3).find('input').val();
                var zjzs = $tdnode.slice(3,4).find('input').val();
                if(zjzs != '--')
                {
                    data['中奖注数'] = zjzs;
                }
                </c:otherwise>
                </c:choose>
                prizeGrade[$tdnode.first().text()] = data;
            }
        });
        $('.editPeriod_prizeGradeFormGroup').each(function (i,m)
        {
            var $groupNode = $(m);
            prizeGrade[$groupNode.find('label').text()] = $groupNode.find('input').val();
        });
        if(!$.isEmptyObject(prizeGrade))
        {
            $('#editPeriodPrizeGrade').val(JSON.stringify(prizeGrade));
        }
        //解析赛事对阵信息
        var matches = new Object();
        $('#editPeriodMatchsTbody').find('tr').each(function(i,tr)
        {
            var $tdnode = $(tr).find('td');
            if($tdnode.length >= 6)
            {
                var data = new Object();
                data['index'] = $tdnode.first().text();
                data['matchname'] = $tdnode.slice(1,2).find('input').val();
                data['homeTeamView'] = $tdnode.slice(2,3).find('input').val();
                data['awayTeamView'] = $tdnode.slice(3,4).find('input').val();
                data['sheng'] = $tdnode.slice(4,5).find('input').val();
                data['ping'] = $tdnode.slice(5,6).find('input').val();
                data['fu'] = $tdnode.slice(6,7).find('input').val();
                data['matchTime'] = $tdnode.slice(7,8).find('input').val();
                data['score'] = $tdnode.slice(8,9).find('input').val();
                matches[$tdnode.first().text()] = data;
            }
        });
        if(!$.isEmptyObject(matches))
        {
            $('#editPeriodMatches').val(JSON.stringify(matches));
        }
        //设置不覆盖更新标记
        $('#editPeriodUpdateFlag').val($('#editPeriodUpdateFlagCheckbox').is(':checked')? 1 : 0);

        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/period/edit',
            type : 'post',
            dataType : 'json',
            data : $("#editPeriodForm").serializeArray(),
            success : function(data)
            {
                $('#editPeriodDialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        });
    });
});
</script>