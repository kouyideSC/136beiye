<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal-dialog lg" id="addLotteryDialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">新增彩种</h4>
		</div>
		<div class="modal-body clearfix clearfix">
			<form class="form-horizontal" id="editLotteryForm" method="post">
				<div class="modal-bg col-sm-12">
					<div class="form-group">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>彩种名称
						</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入彩种名称" name="name" notEmpty="">
							<p></p>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label"><span class="check-star"><b>*</b></span>彩种简称</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" placeholder="请输入彩种简称" name="shortName" notEmpty="">
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
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-cancel btn_modal_cancel" data-dismiss="modal">取消</button>
			<button type="button" class="btn btn-save" id="addBtn">确定</button>
		</div>
	</div>
</div>
<script>

</script>