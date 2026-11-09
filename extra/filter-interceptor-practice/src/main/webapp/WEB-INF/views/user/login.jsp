<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>로그인</title>
  <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<h1>로그인</h1>

<c:if test="${not empty error}"><p class="error">${error}</p></c:if>

<form method="post" action="/user/login">
  <label>아이디 <input name="id" value="user"></label>
  <label>비밀번호 <input name="password" type="password" value="1234"></label>
  <button>로그인</button>
</form>

<p class="hint">admin / 1234 (ADMIN) · user / 1234 (USER)</p>
<p><a href="/">처음으로</a></p>
</body>
</html>
