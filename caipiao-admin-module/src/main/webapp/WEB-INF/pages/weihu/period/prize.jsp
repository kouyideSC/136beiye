<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editPeriodDialog" style="min-width: 600px;">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">期次加奖</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editPeriodForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <input type="hidden" name="prizeGrade" id="prizeGrade">
                <div class="modal-bg col-sm-12 editBaseInfoDiv_cls">
                    <h2 class="modal-title">基础信息</h2>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">彩种</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.shortName}" disabled>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">开始加奖期次</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" id="startPeriod" name="startPeriod" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">结束加奖期次</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" id="endPeriod" name="endPeriod" />
                        </div>
                    </div>
                </div>
                <div class="modal-bg col-sm-12">
                    <h2 class="modal-title">设置奖级加奖</h2>
                    <div class="control-label">
                        <table class="table table-bordered table-money">
                            <thead>
                                <tr style="background: #f8f8f8;">
                                    <th style="width: 20%;">奖级名称</th>
                                    <th style="width: 28%;">加奖奖金</th>
                                </tr>
                            </thead>
                            <tbody id="editPeriodPrizeGradeTbody" style="background: #fff;"></tbody>
                        </table>
                    </div>
                </div>
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
                {}
                else
                {
                    html += '<tr>';
                    html += '<td>' + key + '</td>';
                    html += '<td><input class="form-control" value="' + prizeGrade[key]['加奖奖金'] + '"></td>';
                    html += '</tr>';
                }
            }
            $('#editPeriodPrizeGradeTbody').html(html);
            $('#editPeriodPrizeGradeTbody').parents('div.control-label').first().before(glhtml);
        }
        else
        {
            $('#editPeriodPrizeGradeTbody').html('<tr><td colspan="4" style="color:red;">无奖级信息</td></tr>');
        }
    }
    else
    {
        $('#editPeriodPrizeGradeTbody').html('<tr><td colspan="4" style="color:red;">无奖级信息</td></tr>');
    }

    //点击确定
    $('#editPeriodSureBtn').on('click',function()
    {
        //解析奖级信息
        var prizeGrade = new Object();
        $('#editPeriodPrizeGradeTbody').find('tr').each(function(i,tr)
        {
            var $tdnode = $(tr).find('td');
            if($tdnode.length >= 2)
            {
                var data = new Object();
                data['加奖奖金'] = $tdnode.slice(1,2).find('input').val();
                prizeGrade[$tdnode.first().text()] = data;
            }
        });
        if(!$.isEmptyObject(prizeGrade))
        {
            $('#prizeGrade').val(JSON.stringify(prizeGrade));
        }
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/period/addPrize',
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