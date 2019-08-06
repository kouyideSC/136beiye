<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editLotteryDialog">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">编辑彩种</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editLotteryForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <input type="hidden" name="prizeGrade" id="editLotteryPrizeGrade">
                <div class="modal-bg col-sm-12">
                    <h2 class="modal-title">基础信息</h2>
                    <div class="form-group">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>彩种名称
                        </label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.name}" placeholder="请输入彩种名称" name="name" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>彩种简称</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.shortName}" placeholder="请输入彩种简称" name="shortName" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">app销售状态</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="appStatus">
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">网站销售状态</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="webStatus">
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">H5销售状态</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="h5Status">
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">后台销售状态</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="consoleStatus">
                                <option value="0">停售</option>
                                <option value="1">开售</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">排序号</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.orderValue}" placeholder="请输入彩种显示顺序号" name="orderValue">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">彩种说明</label>
                        <div class="col-sm-7">
                            <textarea class="form-control" placeholder="请输入彩种说明" name="message">${params.message}</textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">说明是否显示背景</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="backGround">
                                <option value="0">不显示</option>
                                <option value="1">显示</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">是否显示在首页</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="showInHome">
                                <option value="0">不显示</option>
                                <option value="1">显示</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">是否限制单方案最大倍数</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="xzMaxSellMultiple" noDefault=true>
                                <option value="0">不限</option>
                                <option value="1">限制</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">单方案最大倍数</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.maxSellMultiple}" placeholder="请输入彩种单方案最大倍数" name="maxSellMultiple">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">是否限制单方案最大金额</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="xzMaxSellMoney" noDefault=true>
                                <option value="0">不限</option>
                                <option value="1">限制</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">单方案最大金额</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.maxSellMoney}" placeholder="请输入彩种单方案最大金额" name="maxSellMoney">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">是否限制单方案最小倍数</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="xzMinSellMultiple" noDefault=true>
                                <option value="0">不限</option>
                                <option value="1">限制</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">单方案最小倍数</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.minSellMultiple}" placeholder="请输入彩种单方案最小倍数" name="minSellMultiple">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">是否限制单方案最小金额</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="xzMinSellMoney" noDefault=true>
                                <option value="0">不限</option>
                                <option value="1">限制</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">单方案最小金额</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.minSellMoney}" placeholder="请输入彩种单方案最小金额" name="minSellMoney">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">支持过关方式</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="ggfsFlag" noDefault=true>
                                <option value="0">单个过关方式</option>
                                <option value="1">不限</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">单方案最多追号期数</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.maxZhNum}" placeholder="请输入单个追号方案的最多追号期数" name="maxZhNum">
                        </div>
                    </div>
                    <c:if test="${params.activityImg != null && params.activityImg != ''}">
                        <div class="form-group">
                            <label class="col-sm-3 control-label">活动图片</label>
                            <div class="col-sm-7">
                                <img src="${params.activityImgLink}">
                            </div>
                        </div>
                    </c:if>
                    <div class="form-group">
                        <label class="col-sm-3 control-label"><c:if test="${params.activityImg == null || params.activityImg == ''}">活动图片</c:if></label>
                        <div class="col-sm-7">
                            <input type="file" name="upActivityImgFile" id="edit_activityImgFile" style="display: none;"/>
                            <input type="hidden" name="activityImg" id="edit_activityImg" value="${params.activityImg}"/>
                            <button id="edit_activityImgBtn" type="button" class="btn btn-default active col-sm-11 add-file-btn">
                                <c:choose>
                                    <c:when test="${params.activityImg == null || params.activityImg == ''}">
                                        选择活动图片
                                    </c:when>
                                    <c:otherwise>
                                        更改活动图片
                                    </c:otherwise>
                                </c:choose>
                            </button>
                            <span class="label label-danger" style="padding-left: 0;position: fixed;margin-left: 6px;margin-top: 9px;cursor: pointer;" id="edit_activityImgCleanBtn">&nbsp;&nbsp;清除</span>
                        </div>
                    </div>
                </div>
                <div class="modal-bg col-sm-12">
                    <h2 class="modal-title">奖级信息</h2>
                    <div class="control-label">
                        <table class="table table-bordered table-money">
                            <thead>
                                <tr style="background: #f8f8f8;">
                                    <th style="width: 20%;">奖级名称</th>
                                    <th style="width: 28%;">单注奖金</th>
                                    <th style="width: 28%;">加奖奖金</th>
                                    <th style="width: 28%;">中奖注数</th>
                                </tr>
                            </thead>
                            <tbody id="editLotteryPrizeGradeTbody" style="background: #fff;"></tbody>
                        </table>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editLotterySureBtn">确定</button>
        </div>
    </div>
</div>
<script>
$(function()
{
    //初始化页面插件
    initPagePlugins('#editLotteryDialog',function ()
    {
        //初始化下拉框默认选中
        $('#editLotteryForm select[name="appStatus"]').selectpicker('val','${params.appStatus}');
        $('#editLotteryForm select[name="webStatus"]').selectpicker('val','${params.webStatus}');
        $('#editLotteryForm select[name="h5Status"]').selectpicker('val','${params.h5Status}');
        $('#editLotteryForm select[name="consoleStatus"]').selectpicker('val','${params.consoleStatus}');
        $('#editLotteryForm select[name="showInHome"]').selectpicker('val','${params.showInHome}');
        $('#editLotteryForm select[name="backGround"]').selectpicker('val','${params.backGround == false? 0 : 1}');
        $('#editLotteryForm select[name="xzMaxSellMultiple"]').selectpicker('val','${params.xzMaxSellMultiple}');
        $('#editLotteryForm select[name="xzMaxSellMoney"]').selectpicker('val','${params.xzMaxSellMoney}');
        $('#editLotteryForm select[name="xzMinSellMultiple"]').selectpicker('val','${params.xzMinSellMultiple}');
        $('#editLotteryForm select[name="xzMinSellMoney"]').selectpicker('val','${params.xzMinSellMoney}');
        $('#editLotteryForm select[name="ggfsFlag"]').selectpicker('val','${params.ggfsFlag}');
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
                    glhtml += '<div class="form-group editLottery_prizeGradeFormGroup">';
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
                    html += '<td><input class="form-control" value="' + prizeGrade[key]['加奖奖金'] + '"></td>';
                    html += '<td><input class="form-control" value="' + prizeGrade[key]['中奖注数'] + '"></td>';
                    html += '</tr>';
                }
            }
            $('#editLotteryPrizeGradeTbody').html(html);
            $('#editLotteryPrizeGradeTbody').parents('div.control-label').first().before(glhtml);
        }
        else
        {
            $('#editLotteryPrizeGradeTbody').html('<tr><td colspan="4" style="color:red;">无奖级信息</td></tr>');
        }
    }
    else
    {
        $('#editLotteryPrizeGradeTbody').html('<tr><td colspan="4" style="color:red;">无奖级信息</td></tr>');
    }
    //点击选择文件
    $("#edit_activityImgBtn").click(function ()
    {
        $("#edit_activityImgFile").click();
    });
    var accept = /(\.|\/)(gif|jpe?g|png|ico?n)$/i;
    $('#edit_activityImgFile').fileupload({
        url: '${pageContext.request.contextPath}/weihu/lottery/uploadActivityImg',//上传地址
        dataType: 'json',
        acceptFileTypes: accept,
        autoUpload: true,
        maxFileSize: 9999999
    }).on('fileuploadadd', function (event, data)
    {
        $.each(data.files, function (index, file)
        {
            $("#edit_activityImgBtn").html("<sapn>" + file.name + "</span>");
        });
    }).on('fileuploadprogress', function (event, data)
    {
        var progress = parseInt(data.loaded / data.total * 100, 10);//上传进度
        //$('<div class="process"><div class="process-bar"></div></div>').appendTo('body');
        //$('.process-bar').css('width',progress + '%');
    }).on('fileuploaddone', function (event, data)
    {
        if(data.result.dcode == 1000)
        {
            $('#edit_activityImg').val(data.result.datas.fpath);
        }
        else
        {
            showoplayer(data.result.dmsg);
        }
    });
    //清除logo
    $('#edit_activityImgCleanBtn').on('click',function()
    {
        $('#edit_activityImgBtn').text('选择文件');
        $('#edit_activityImg').val('');
    });
    //点击确定
    $('#editLotterySureBtn').on('click',function()
    {
        //解析奖级信息
        var prizeGrade = new Object();
        $('#editLotteryPrizeGradeTbody').find('tr').each(function(i,tr)
        {
            var $tdnode = $(tr).find('td');
            if($tdnode.length >= 4)
            {
                var data = new Object();
                data['单注奖金'] = $tdnode.slice(1,2).find('input').val();
                data['加奖奖金'] = $tdnode.slice(2,3).find('input').val();
                data['中奖注数'] = $tdnode.slice(3,4).find('input').val();
                prizeGrade[$tdnode.first().text()] = data;
            }
        });
        $('.editLottery_prizeGradeFormGroup').each(function (i,m)
        {
            var $groupNode = $(m);
            prizeGrade[$groupNode.find('label').text()] = $groupNode.find('input').val();
        });
        if(!$.isEmptyObject(prizeGrade))
        {
            $('#editLotteryPrizeGrade').val(JSON.stringify(prizeGrade));
        }
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/lottery/edit',
            type : 'post',
            dataType : 'json',
            data : $("#editLotteryForm").serializeArray(),
            success : function(data)
            {
                $('#editLotteryDialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        })
    });
});
</script>