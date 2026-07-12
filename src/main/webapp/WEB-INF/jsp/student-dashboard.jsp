<%@ page import="java.util.List" %>
<%@ page import="com.training.exam.model.Exam" %>
<%@ page import="com.training.exam.model.ExamRecord" %>
<%@ page import="com.training.exam.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%
    User user = (User) session.getAttribute("user");
    List<Exam> exams = (List<Exam>) request.getAttribute("exams");
    List<ExamRecord> records = (List<ExamRecord>) request.getAttribute("records");
    String notice = request.getParameter("notice");
%>
<!doctype html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>学生工作台</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/app.css">
</head>
<body>
<header class="topbar">
    <div>
        <strong>学生工作台</strong>
        <span>欢迎，<%=user.getRealName()%></span>
    </div>
    <nav><a href="<%=request.getContextPath()%>/logout">退出</a></nav>
</header>
<main class="container">
    <section class="page-head">
        <div>
            <p class="eyebrow">Student Console</p>
            <h1>我的考试</h1>
        </div>
    </section>
    <section class="panel">
        <div class="section-title"><h2>可参加考试</h2></div>
        <div class="grid">
            <% if (exams != null && !exams.isEmpty()) { for (Exam exam : exams) { %>
            <article class="item exam-card">
                <h3><%=exam.getTitle()%></h3>
                <p>考试时间：<%=exam.getStartTime()%> 至 <%=exam.getEndTime()%></p>
                <p>答题时长：<strong><%=exam.getDurationMinutes()%></strong> 分钟；允许次数：<strong><%=exam.getMaxAttempts()%></strong>；总分：<strong><%=exam.getTotalScore()%></strong></p>
                <a class="button primary" href="<%=request.getContextPath()%>/student/take?examId=<%=exam.getId()%>">开始考试</a>
            </article>
            <% }} else { %>
            <p class="empty">暂无已发布考试</p>
            <% } %>
        </div>
    </section>
    <section class="panel">
        <div class="section-title"><h2>个人最高成绩</h2><span class="muted">多次考试时取最高分</span></div>
        <table>
            <thead><tr><th>考试</th><th>状态</th><th>最高分</th><th>对应次数</th><th>提交时间</th></tr></thead>
            <tbody>
            <% if (records != null && !records.isEmpty()) { for (ExamRecord r : records) { %>
            <tr>
                <td><%=r.getExamTitle()%></td>
                <td><span class="badge"><%=r.getStatus()%></span></td>
                <td><strong><%=r.getTotalScore()%></strong></td>
                <td>第 <%=r.getAttemptNo()%> 次</td>
                <td><%=r.getSubmittedAt()%></td>
            </tr>
            <% }} else { %>
            <tr><td colspan="5" class="empty">暂无成绩记录</td></tr>
            <% } %>
            </tbody>
        </table>
    </section>
</main>
<% if ("attempts-used".equals(notice)) { %>
<script>alert("考试次数已耗尽，无法再次进入答题。");</script>
<% } else if ("not-started".equals(notice)) { %>
<script>alert("考试尚未开始，请在规定时间进入答题。");</script>
<% } else if ("ended".equals(notice)) { %>
<script>alert("考试已结束，无法进入答题。");</script>
<% } %>
</body>
</html>
