package com.caipiao.admin.index.controller;

import com.caipiao.admin.service.login.LoginService;
import com.caipiao.admin.util.ConstantUtils;
import com.caipiao.admin.util.SessionUtil;
import com.caipiao.admin.util.WebUtils;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController
{
	private static Logger logger = LoggerFactory.getLogger(LoginService.class);

	@Autowired
	private LoginService loginService;

	/**
	 * 跳转至框架首页
	 */
	@RequestMapping("/index")
	public String index(HttpServletRequest request, HttpServletResponse response)
	{
		return "index";
	}

	/**
	 * 跳转至主页
	 */
	@RequestMapping("/home")
	public String home(HttpServletRequest request, HttpServletResponse response)
	{
		return "home";
	}
}