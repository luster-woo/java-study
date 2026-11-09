<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>login</title></head>
<body>
<%@ include file="../fragments/header.jsp" %>
<h1>로그인</h1>
<%--
    지금은 SecurityConfig에서 formLogin(Customizer.withDefaults())로 기본 로그인 화면(/login)을 사용.
    이 화면을 쓰려면 formLogin().loginPage("/member/login-form")로 바꿀 것.
    - 파라미터 이름 기본값 : username, password
    - action : POST /login (UsernamePasswordAuthenticationFilter가 처리)
--%>
<c:if test="${param.error != null}">
    <p style="color: red">아이디 또는 비밀번호가 올바르지 않습니다.</p>
</c:if>
<form method="post" action="${root}/login">
    <sec:csrfInput/>
    <div><label>email <input type="text" name="username" value="user@ssafy.com"></label></div>
    <div><label>password <input type="password" name="password" value="1234"></label></div>
    <button type="submit">로그인</button>
</form>
</body>
</html>
