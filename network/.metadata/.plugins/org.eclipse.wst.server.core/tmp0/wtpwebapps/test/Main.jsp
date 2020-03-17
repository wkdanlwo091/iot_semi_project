<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>
	<h1>Admin Page</h1>
	<form action="webappservlet" method="get">
		IP<input type = "text" id = "ip"><br>
		PORT<input type = "text" name = "port"><br>
		TXT<input type = "text" name = "txt"><br>
		<input type = "submit" value = "send">
	</form>
</body>
</html>