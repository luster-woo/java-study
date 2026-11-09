<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>user</title></head>
<body>
<%@ include file="../fragments/header.jsp" %>
<h1>USER 페이지 (/secured/user/**)</h1>
<p>hasRole("USER") — RoleHierarchy(ADMIN ⊃ USER) 덕분에 ADMIN도 들어올 수 있음</p>
</body>
</html>
