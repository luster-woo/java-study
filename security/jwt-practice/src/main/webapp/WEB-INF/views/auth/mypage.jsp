<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>mypage</title></head>
<body>
<%@ include file="../fragments/header.jsp" %>
<h1>마이페이지 (/auth/**) — 로그인만 하면 접근 가능</h1>
<%-- 컨트롤러에서 @AuthenticationPrincipal로 받아 model에 담은 값 --%>
<ul>
    <li>username : ${user.username}</li>
    <li>authorities : ${user.authorities}</li>
    <li>password : ${user.password} (인증 후에는 credential이 지워져 null)</li>
</ul>
</body>
</html>
