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
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/app.css?v=20260713-2">
</head>
<body class="dashboard-page student-dashboard-page">
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
            <article class="item exam-card availability-<%=exam.getAvailabilityStatus().toLowerCase()%>">
                <div class="exam-card-head">
                    <span class="exam-icon">卷</span>
                    <div><h3><%=exam.getTitle()%></h3><span class="badge"><%=exam.getTotalScore()%> 分制</span></div>
                </div>
                <div class="exam-meta">
                    <p><span>考试时间</span><strong><%=exam.getStartTime()%><br>至 <%=exam.getEndTime()%></strong></p>
                    <p><span>答题时长</span><strong><%=exam.getDurationMinutes()%> 分钟</strong></p>
                    <p><span>剩余次数</span><strong><%=exam.getRemainingAttempts()%> / <%=exam.getMaxAttempts()%></strong></p>
                </div>
                <% if (exam.isAvailable()) { %>
                <a class="button primary full" href="<%=request.getContextPath()%>/student/take?examId=<%=exam.getId()%>">开始考试</a>
                <% } else if ("NOT_STARTED".equals(exam.getAvailabilityStatus())) { %>
                <span class="button disabled full">考试未开始</span>
                <% } else if ("ENDED".equals(exam.getAvailabilityStatus())) { %>
                <span class="button disabled full">考试已结束</span>
                <% } else { %>
                <span class="button disabled full">次数已用尽</span>
                <% } %>
            </article>
            <% }} else { %>
            <p class="empty">暂无已发布考试</p>
            <% } %>
        </div>
    </section>
    <section class="panel">
        <div class="section-title"><h2>个人最高成绩</h2><span class="muted">多次考试时取最高分</span></div>
        <div class="table-wrap"><table>
            <thead><tr><th>考试</th><th>状态</th><th>最高分</th><th>对应次数</th><th>提交时间</th><th>操作</th></tr></thead>
            <tbody>
            <% if (records != null && !records.isEmpty()) { for (ExamRecord r : records) { %>
            <tr>
                <td><%=r.getExamTitle()%></td>
                <td><span class="badge"><%=r.getStatus()%></span></td>
                <td><strong class="score-text"><%=r.getTotalScore()%> / <%=r.getExamTotalScore()%></strong></td>
                <td>第 <%=r.getAttemptNo()%> 次</td>
                <td><%=r.getSubmittedAt()%></td>
                <td><a class="button compact secondary" href="<%=request.getContextPath()%>/exam/view?recordId=<%=r.getId()%>">查看试卷</a></td>
            </tr>
            <% }} else { %>
            <tr><td colspan="6" class="empty">暂无成绩记录</td></tr>
            <% } %>
            </tbody>
        </table></div>
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
