<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>index</title></head>
<body>
<%@ include file="fragments/header.jsp" %>
<h1>Spring Security 실습</h1>

<table border="1" cellpadding="6">
    <tr><th>경로</th><th>접근 조건</th></tr>
    <tr><td>/, /member/**</td><td>누구나</td></tr>
    <tr><td>/api/** (JWT chain)</td><td>/api/v1/etc/**, /api/v1/auth/** 는 누구나, 나머지는 access token 필요 → <a href="${root}/jwt.html">jwt 실습 페이지</a></td></tr>
    <tr><td>/auth/**</td><td>로그인한 사용자</td></tr>
    <tr><td>/secured/user/**</td><td>USER role (ADMIN 포함)</td></tr>
    <tr><td>/secured/admin/**</td><td>ADMIN role</td></tr>
</table>

<h3>테스트 계정</h3>
<ul>
    <li>admin@ssafy.com / 1234 (ADMIN)</li>
    <li>user@ssafy.com / 1234 (USER)</li>
</ul>

<%-- role에 따라 다른 내용 보여주기 --%>
<sec:authorize access="hasRole('ADMIN')">
    <p>관리자에게만 보이는 문장</p>
</sec:authorize>
<sec:authorize access="hasRole('USER')">
    <p>USER 이상에게 보이는 문장 (RoleHierarchy 때문에 ADMIN에게도 보임)</p>
</sec:authorize>
</body>
</html>
