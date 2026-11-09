<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- spring-security-taglibs : JSP에서 인증 정보 사용 --%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="root" value="${pageContext.request.contextPath}"/>
<style>
    body { font-family: sans-serif; max-width: 720px; margin: 24px auto; padding: 0 16px; }
    nav a { margin-right: 12px; }
    .status { background: #f3f4f6; padding: 8px 12px; border-radius: 6px; margin: 12px 0; }
    form.inline { display: inline; }
</style>
<nav>
    <a href="${root}/">home</a>
    <a href="${root}/auth/mypage">auth/mypage</a>
    <a href="${root}/secured/user">secured/user</a>
    <a href="${root}/secured/admin">secured/admin</a>
    <a href="${root}/jwt.html">jwt 실습</a>
</nav>
<div class="status">
    <%-- 로그인 안 한 사용자 --%>
    <sec:authorize access="isAnonymous()">
        로그인 전입니다. <a href="${root}/login">로그인</a>
    </sec:authorize>
    <%-- 로그인 한 사용자 --%>
    <sec:authorize access="isAuthenticated()">
        <b><sec:authentication property="principal.username"/></b>
        (<sec:authentication property="principal.authorities"/>) 님 로그인 중
        <%-- csrf가 켜져 있으므로 logout은 POST + csrf 토큰 --%>
        <form class="inline" method="post" action="${root}/logout">
            <sec:csrfInput/>
            <button type="submit">로그아웃</button>
        </form>
    </sec:authorize>
</div>
