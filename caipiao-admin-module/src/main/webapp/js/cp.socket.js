$(function()
{
	var socket = io.connect("47.101.36.76:9999");
	socket.emit("pushuser",{"aid":$("#current_loginuser_id").val()});
	socket.on('refreshMsg',function(data)
	{
		var readText = '';
		var html = '<div class="tips-list">';
		if(data['cptotal'] != undefined)
		{
			//$("#left_sidbar_menulist li[mcode='menu_shopchupiao_query']").click();
			//$('#messageWarning').hide();
			html += '<div class="tips-item clearfix">';
			if(data.cptotal != "")
			{
				html += '<span class="pull-left">收到<b class="text-red">' + data.cptotal + "</b>条" + '待办任务</span>';
				readText += '收到' + data.cptotal +'条待办任务';
			}
			else
			{
				html += '<span class="pull-left">收到新待办任务</span>';
				readText += '收到新待办任务';
			}
			html += '<a href="javascript:;" onclick="showCpMsg()" class="pull-right">查看</a>';
			html += '</div>';
		}
		html += '</div>';
		$("#messageWarning .massage_warning_desc").html(html);
		if(checkWindowMinStatus())
		{
			if(window.Notification)
			{
				if(Notification.permission == 'granted')
				{
					showNotification(data,readText);
				}
				else
				{
					Notification.requestPermission().then(function(status)
					{
						//status-授权状态,'granted'表示允许
						if(status == 'granted')
						{
							showNotification(data,readText);
						}
						else
						{
							$('#messageWarning').slideDown();
						}
					});
				}
			}
			else
			{
				$('#messageWarning').slideDown();
			}
		}
		else
		{
			$('#messageWarning').slideDown();
		}
		playAudio(readText);
	});
});