<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>게시판</title>
  <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<h1>게시판</h1>
<p>
  ${sessionScope.loginUser.name} (${sessionScope.loginUser.role})
  · <a href="/user/logout">로그아웃</a> · <a href="/">처음으로</a>
</p>

<table>
  <tr><th>번호</th><th>제목</th><th>삭제 (관리자만)</th></tr>
  <c:forEach var="b" items="${boards}">
    <tr>
      <td>${b.key}</td>
      <td>${b.value}</td>
      <td>
        <form method="post" action="/board/delete/${b.key}">
          <button>삭제</button>
        </form>
      </td>
    </tr>
  </c:forEach>
</table>

<p class="hint">USER로 삭제 → AdminInterceptor가 403 / ADMIN으로 삭제 → 삭제 후 목록으로</p>
</body>
</html>
