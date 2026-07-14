<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>在线考试系统登录</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/app.css?v=20260713-2">
</head>
<body class="login-page">
<main class="login-panel">
    <div class="brand-mark">EXAM</div>
    <h1>在线考试与自动评阅系统</h1>
    <p class="muted">教师组卷、学生考试、自动判分、AI 主观题评阅</p>
    <form method="post" action="<%=request.getContextPath()%>/login" autocomplete="off">
        <label>用户名
            <input name="username" required placeholder="请输入用户名" autocomplete="off">
        </label>
        <label>密码
            <input name="password" type="password" required placeholder="请输入密码" autocomplete="new-password">
        </label>
        <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%=request.getAttribute("error")%></p>
        <% } %>
        <button type="submit" class="button primary full">登录系统</button>
    </form>
</main>
<script>
    window.addEventListener('pageshow', function () {
        document.querySelectorAll('input[name="username"], input[name="password"]').forEach(function (input) {
            input.value = '';
        });
    });
</script>
</body>
</html>
