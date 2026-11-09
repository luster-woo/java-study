<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Filter · Interceptor 실습</title>
  <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<h1>Filter · Interceptor 실습</h1>

<p>
  <c:choose>
    <c:when test="${not empty sessionScope.loginUser}">
      로그인: <b>${sessionScope.loginUser.name}</b> (${sessionScope.loginUser.role})
      · <a href="/user/logout">로그아웃</a>
    </c:when>
    <c:otherwise>로그인 안 함 · <a href="/user/login">로그인</a></c:otherwise>
  </c:choose>
</p>

<h2>요청해 보고 IntelliJ 콘솔 확인</h2>
<ul>
  <li><a href="/">/</a> — Filter A·B·C + LoggingInterceptor</li>
  <li><a href="/api/users">/api/users</a> — LoggingFilter까지 (/api/* 만)</li>
  <li><a href="/css/style.css">/css/style.css</a> — 필터는 걸리고 LoggingInterceptor는 제외</li>
  <li><a href="/board/list">/board/list</a> — LoginInterceptor (비로그인이면 로그인 페이지로)</li>
</ul>
</body>
</html>
