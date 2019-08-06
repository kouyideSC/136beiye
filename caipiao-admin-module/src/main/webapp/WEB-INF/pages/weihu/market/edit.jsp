<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="modal-dialog lg" id="editMarketDialog">
	<div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">
                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">编辑版本信息</h4>
        </div>
        <div class="modal-body clearfix clearfix">
            <form class="form-horizontal" id="editMarketForm" method="post">
                <input type="hidden" name="id" value="${params.id}">
                <div class="modal-bg col-sm-12">
                    <div class="form-group addMarket_cls" id="addMarketName">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>选择发布到市场</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="marketId" id="marketNameSelect" data-live-search="true" data-size="8" data-selected-text-format="count > 3"></select>
                        </div>
                    </div>
                    <div class="form-group addMarket_cls" id="addClientType">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>安装客户端</label>
                        <div class="col-sm-7">
                            <select class="form-control selectpicker" id="edit_clientType" name="clientType" title="客户端" data-live-search="true" data-size="8" data-selected-text-format="count > 3">
                                <option value="1">安卓</option>
                                <option value="0">IOS</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group addMarket_cls" id="addPlayType">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>版本类型</label>
                        <div class="col-sm-7">
                            <select class="form-control selectpicker" id="edit_versionType" name="versionType" title="版本类型" data-live-search="true" data-size="8" data-selected-text-format="count > 3">
                                <option value="0">正式版</option>
                                <option value="1">资讯版</option>
                                <option value="2">企业版</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group addMarket_cls" id="addAppVersion">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>小版本号</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.appVersion}" name="appVersion" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addMarket_cls" id="addBuildVersion">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>大版本号</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" value="${params.buildVersion}" name="buildVersion" notEmpty="">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addMarket_cls" id="addStatus">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>是否提示用户更新</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="status" id="edit_status">
                                <option value="1">提示</option>
                                <option value="0">不提示</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group addMarket_cls" id="addIsForceUpdate">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>是否强制更新</label>
                        <div class="col-sm-7">
                            <select class="form-control" name="isForceUpdate" id="edit_isForceUpdate">
                                <option value="1">是</option>
                                <option value="0">否</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group" id="uploadApk">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>上传安装包</label>
                        <div class="col-sm-7">
                            <input type="file" name="uplogo" id="add_logoFile" style="display: none;"/>
                            <input type="hidden" name="downUrl" id="add_logo"/>
                            <button id="add_uploadLogoBtn" type="button" class="btn btn-default active col-sm-11 add-file-btn">
                                <span class="plus-icon p-add icon-cha"></span>
                                选择文件
                            </button>
                            <span class="label label-danger" style="padding-left: 0;position: fixed;margin-left: 10px;margin-top: 8px;cursor: pointer;" id="add_cleanLogoFile">&nbsp;&nbsp;清除</span>
                        </div>
                    </div>
                    <div class="form-group addMarket_cls" id="inputUrl" style="display: none;">
                        <label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>IOS下载地址</label>
                        <div class="col-sm-7">
                            <input type="text" class="form-control" placeholder="输入IOS企业版下载URL" name="downUrl" notEmpty="" id="inputDownUrl">
                            <p></p>
                        </div>
                    </div>
                    <div class="form-group addMarket_cls" id="addUpdateInfo">
                        <label class="col-sm-3 control-label"></span>更新内容</label>
                        <div class="col-sm-7">
                            <textarea class="form-control" name="updateInfo">${params.updateInfo}</textarea>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
            <button type="button" class="btn btn-save" id="editMarketSureBtn">确定</button>
        </div>
    </div>
</div>
<script>
$(function()
{
    //初始化页面插件
    initPagePlugins('#editMarketDialog',function()
    {
        var $dialogNode = $('#editVoteDialog');
        $dialogNode.find('.config-date input').css('padding-left','20px');
        $dialogNode.find('.daterangepicker.dropdown-menu').css('min-width','365px');
    });

    $('#edit_clientType').selectpicker("val", '${params.clientType}');
    $('#edit_versionType').selectpicker("val", '${params.versionType}');
    $('#edit_isForceUpdate').selectpicker("val", '${params.isForceUpdate}');
    $('#edit_status').selectpicker("val", '${params.status}');

    //市场名称下拉
    $('#marketNameSelect').fillSelectMenu({
        url: '${pageContext.request.contextPath}/weihu/market/getMarketList',
        id: 'id',
        name: 'marketName',
        selectedIds: ${params.marketId}
    });

    //点击确定
    $('#editMarketSureBtn').on('click',function()
    {
        //发送请求
        $.ajax({
            url : '${pageContext.request.contextPath}/weihu/market/edit',
            type : 'post',
            dataType : 'json',
            data : $("#editMarketForm").serializeArray(),
            success : function(data)
            {
                $('#editMarketDialog').parents('.modal').first().modal('hide');
                showoplayer(data);
            }
        });
    });

    //点击选择文件
    $("#add_uploadLogoBtn").click(function ()
    {
        $("#add_logoFile").click();
    });
    $('#add_logoFile').fileupload({
        url: '${pageContext.request.contextPath}/weihu/market/uploadApk',//上传地址
        dataType: 'json',
        autoUpload: true
    }).on('fileuploadadd', function (event, data)
    {
        $.each(data.files, function (index, file)
        {
            $("#add_uploadLogoBtn").html("<sapn>" + file.name + "</span>");
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
            $('#add_logo').val(data.result.datas.fpath);
        }
        else
        {
            showoplayer(data.result.dmsg);
        }
    });

    //清除logo
    $('#add_cleanLogoFile').on('click',function()
    {
        $('#add_uploadLogoBtn').text('选择文件');
        $('#add_logo').val('');
    });

    $('#edit_clientType').on('change', function()
    {
        var value = $(this).val();
        if(value == 0) {
            $('#inputUrl').css("display", "block");
            $('#uploadApk').css("display", "none");
            $('#inputDownUrl').val('${params.downUrl}');
        } else {
            $('#uploadApk').css("display", "block");
            $('#inputUrl').css("display", "none");
        }
    });
    if('${params.clientType}' == '0'){
        $('#inputUrl').css("display", "block");
        $('#uploadApk').css("display", "none");
        $('#inputDownUrl').val('${params.downUrl}');
    } else {
        $('#uploadApk').css("display", "block");
        $('#inputUrl').css("display", "none");
    }
});
</script>