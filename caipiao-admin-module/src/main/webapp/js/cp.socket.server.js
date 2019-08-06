//初始化
module.paths.push('/web/node-v10.13.0-linux-x64/lib/node_modules');
var express = require('express');
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var bodyParser = require("body-parser");
app.use(bodyParser.urlencoded({extended: true}));
var socketUsers = {};//用来存储客户端连接
var duplicateKey = "_m_";//重复用户时的重命名标识

//启动监听
http.listen(9999,function()
{
	console.log("cp.socket.server开启监听端口:9999");
});
//定义访问路径-推送消息(get方式)
app.get('/cpadmin/sendmsg',function(request,response)
{
	try
	{
		var ptype = request.query.ptype;
		if(ptype == 1)
		{
			//给所有的客户端发消息
			for(var key in socketUsers)
			{
				if(socketUsers[key] != undefined)
				{
					socketUsers[key].emit('refreshMsg',request.query);
				}
			}
			response.end();
		}
		else
		{
			var aid = request.query.aid;
			if(aid == undefined || aid == "")
			{
				response.end();
			}
			else
			{
				//给对应的人发送消息
				for(var key in socketUsers)
				{
					if(key == aid || (key.indexOf(aid) == 0 && key.indexOf(duplicateKey) > -1))
					{
						if(socketUsers[key] != undefined)
						{
							socketUsers[key].emit('refreshMsg',request.query);
						}
					}
				}
				response.end();
			}
		}
	}
	catch(e)
	{
		console.log("****GET:/cpadmin/refreshMsg发生异常,异常信息:" + e);
		try 
		{
			response.end(e.stack);
		}
		catch(e){}
	}
});
//定义访问路径-推送消息(post方式)
app.post('/cpadmin/sendmsg',function(request,response)
{
	try
	{
		var ptype = request.body.ptype;
		if(ptype == 1)
		{
			for(var key in socketUsers)
			{
				if(socketUsers[key] != undefined)
				{
					socketUsers[key].emit('refreshMsg',request.body);
				}
			}
		}
		else
		{
			var aid = request.body.aid;
			if(aid == undefined || aid == "")
			{
				response.end();
			}
			else
			{
				//给对应的人发送消息
				for(var key in socketUsers)
				{
					if(key == aid || (key.indexOf(aid) == 0 && key.indexOf(duplicateKey) > -1))
					{
						if(socketUsers[key] != undefined)
						{
							socketUsers[key].emit('refreshMsg',request.body);
						}
					}
				}
			}
		}
		response.end();
	}
	catch(e)
	{
		console.log("****POST:/cpadmin/refreshMsg发生异常,异常信息:" + e);
		try 
		{
			response.end(e.stack);
		}
		catch(e){}
	}
});
//新的客户端连接
io.sockets.on("connection",function(socket)
{
	//存储用户
	socket.on('pushuser', function(data)
	{
		try
		{
			if(socketUsers[data.aid] != undefined && socketUsers[data.aid].id != socket.id)
			{
				console.log("同帐户多处登录连接,aid:" + data.aid);
				for(var i = 0; i < 10; i ++)
				{
					var newkey = data.aid + duplicateKey + i;
					if(socketUsers[newkey] == undefined)
					{
						console.log("同帐户多处登录连接,aid:" + data.aid + ",newkey:" + newkey);
						socketUsers[newkey] = socket;
						break;
					}
				}
			}
			else
			{
				console.log("新帐户连接,aid:" + data.aid);
				socketUsers[data.aid] = socket;
			}
		}
		catch(e)
		{
			console.log("****pushuser发生异常,异常信息:" + e);
		}
	});
	//客户断开连接
	socket.on('disconnect',function()
	{
		try
		{
			for(var key in socketUsers)
			{
				var socketuser = socketUsers[key];
				if(socketuser.id == socket.id)
				{
					console.log("帐户断开连接,aid:" + key);
					delete socketUsers[key];
				}
			}
		}
		catch(e)
		{
			console.log("****disconnect发生异常,异常信息:" + e);
		}
	});
});
//产生指定位数的随机数
var getRands = function(num)
{
	var rand = "";
	for(var i = 0; i < num; i ++)
	{
		rand += Math.floor(Math.random() * 10);
	}
};