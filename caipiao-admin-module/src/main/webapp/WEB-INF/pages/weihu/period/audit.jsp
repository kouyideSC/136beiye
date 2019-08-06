<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="auditPeriodDialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">期次审核</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="auditPeriodForm" method="post">
				<input type="hidden" name="id" value="${params.id}">
				<input type="hidden" name="lotteryId" value="${params.lotteryId}">
				<input type="hidden" name="period" value="${params.period}">
				<input type="hidden" name="prizeGrade" id="auditPeriodPrizeGrade">
				<input type="hidden" name="matches" id="auditPeriodMatches">
				<div class="modal-bg col-sm-12">
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
							<select class="form-control" id="auditPeriodSellStatusSelect" disabled>
								<option value="-1">已截止</option>
								<option value="0">未开售</option>
								<option value="1">销售中</option>
							</select>
						</div>
					</div>
					<%--<div class="form-group">
						<label class="col-sm-3 control-label">开始销售时间</label>
						<div class="col-sm-7">
							<input class="form-control" type="text" value="${params.sellStartTime}" disabled>
						</div>
					</div>--%>
					<div class="form-group">
						<label class="col-sm-3 control-label">截止销售时间</label>
						<div class="col-sm-7">
							<input class="form-control" type="text" value="${params.sellEndTime}" disabled>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">官方截止时间</label>
						<div class="col-sm-7">
							<input class="form-control" type="text" value="${params.authorityEndTime}" disabled>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">期次状态</label>
						<div class="col-sm-7">
							<select class="form-control" id="auditPeriodStateSelect" disabled></select>
						</div>
					</div>
				</div>
				<div class="modal-bg col-sm-12">
					<h2 class="modal-title">开奖/奖级信息</h2>
					<div class="form-group">
						<label class="col-sm-3 control-label">开奖号码</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请填写开奖号码" name="drawNumber" value="${params.drawNumber}">
						</div>
					</div>
					<div class="control-label">
						<table class="table table-bordered table-money">
							<thead>
							<tr style="background: #f8f8f8;">
								<th style="width: 15%;">奖级名称</th>
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
							<tbody id="auditPeriodPrizeGradeTbody" style="background: #fff;"></tbody>
						</table>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="auditPeriodSureBtn">确定</button>
		</div>
	</div>
</div>
<script>
$(function()
{
	initPagePlugins('#auditPeriodDialog');//初始化页面插件

	//初始化下拉框默认选中
	$('#auditPeriodSellStatusSelect').selectpicker('val','${params.sellStatus}');
	$('#auditPeriodStateSelect').fillSelectMenu({
		url: '${pageContext.request.contextPath}/weihu/period/getPeriodStatesCombo',
		id: 'state',
		name: 'description',
		callback : function ()
		{
			$('#auditPeriodStateSelect').selectpicker('val','${params.state}');
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
				{
					glhtml += '<div class="form-group auditPeriod_prizeGradeFormGroup">';
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
			$('#auditPeriodPrizeGradeTbody').html(html);
			$('#auditPeriodPrizeGradeTbody').parents('div.control-label').first().before(glhtml);
		}
		else
		{
			$('#auditPeriodPrizeGradeTbody').html('<tr><td colspan="4" style="color:red;">无奖级信息</td></tr>');
		}
	}
	else
	{
		$('#auditPeriodPrizeGradeTbody').html('<tr><td colspan="4" style="color:red;">无奖级信息</td></tr>');
	}
	//点击确定
	$('#auditPeriodSureBtn').on('click',function()
	{
		//解析奖级信息
		var prizeGrade = new Object();
		$('#auditPeriodPrizeGradeTbody').find('tr').each(function(i,tr)
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
		$('.auditPeriod_prizeGradeFormGroup').each(function (i,m)
		{
			var $groupNode = $(m);
			prizeGrade[$groupNode.find('label').text()] = $groupNode.find('input').val();
		});
		if(!$.isEmptyObject(prizeGrade))
		{
			$('#auditPeriodPrizeGrade').val(JSON.stringify(prizeGrade));
		}
		//发送请求
		$.ajax({
			url : '${pageContext.request.contextPath}/weihu/period/audit',
			type : 'post',
			dataType : 'json',
			data : $("#auditPeriodForm").serializeArray(),
			success : function(data)
			{
				$('#auditPeriodDialog').parents('.modal').first().modal('hide');
				showoplayer(data);
			}
		})
	});
});
</script>