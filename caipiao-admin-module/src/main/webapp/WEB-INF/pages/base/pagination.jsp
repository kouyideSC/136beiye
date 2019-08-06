<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<ul class="paginationer clearfix pagelist_ul_cls" tpage="0" id="pagelist">
	<li page="p" class="disabled"><a class="plus-icon p-left"></a></li>
	<li page="1" class="active"><a >1</a></li>
	<li page="n" class="disabled"><a class="plus-icon p-right"></a></li>
	<li class="nobd">
		<select id="paginationPageSize" class="pagination_page_size">
			<option value="10">10</option>
			<option value="15">15</option>
			<option value="20">20</option>
			<option value="30">30</option>
			<option value="50">50</option>
		</select>
	</li>
	<li class="nobd total">共<span>0</span>条</li>
</ul>
