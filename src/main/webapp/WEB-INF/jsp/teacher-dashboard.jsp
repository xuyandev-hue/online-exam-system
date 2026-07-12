<%@ page import="java.util.List" %>
<%@ page import="com.training.exam.model.ExamRecord" %>
<%@ page import="com.training.exam.model.Exam" %>
<%@ page import="com.training.exam.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%
    User user = (User) session.getAttribute("user");
    List<ExamRecord> records = (List<ExamRecord>) request.getAttribute("records");
    List<Exam> exams = (List<Exam>) request.getAttribute("exams");
    int submitted = records == null ? 0 : records.size();
    int sum = 0;
    int pass = 0;
    if (records != null) {
        for (ExamRecord r : records) {
            sum += r.getTotalScore();
            if (r.getTotalScore() >= 60) pass++;
        }
    }
%>
<!doctype html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>教师工作台</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/app.css">
</head>
<body>
<header class="topbar">
    <div>
        <strong>教师工作台</strong>
        <span>欢迎，<%=user.getRealName()%></span>
    </div>
    <nav>
        <a href="<%=request.getContextPath()%>/teacher/questions">题库管理</a>
        <a href="<%=request.getContextPath()%>/teacher/exams">组卷发布</a>
        <a href="<%=request.getContextPath()%>/teacher/export">导出成绩</a>
        <a href="<%=request.getContextPath()%>/logout">退出</a>
    </nav>
</header>
<main class="container">
    <section class="page-head">
        <div>
            <p class="eyebrow">Teacher Console</p>
            <h1>考试管理概览</h1>
        </div>
    </section>
    <section class="stats">
        <div><span>试卷数量</span><strong><%=exams == null ? 0 : exams.size()%></strong></div>
        <div><span>提交次数</span><strong><%=submitted%></strong></div>
        <div><span>平均分</span><strong><%=submitted == 0 ? 0 : sum / submitted%></strong></div>
        <div><span>及格率</span><strong><%=submitted == 0 ? 0 : pass * 100 / submitted%>%</strong></div>
    </section>
    <section class="panel">
        <div class="section-title">
            <h2>成绩统计与考试次数</h2>
            <a class="button secondary" href="<%=request.getContextPath()%>/teacher/export">导出 CSV</a>
        </div>
        <table>
            <thead><tr><th>考试</th><th>学生</th><th>班级</th><th>考试次数</th><th>分数</th><th>提交时间</th></tr></thead>
            <tbody>
            <% if (records != null && !records.isEmpty()) { for (ExamRecord r : records) { %>
            <tr>
                <td><%=r.getExamTitle()%></td>
                <td><%=r.getStudentName()%></td>
                <td><%=r.getClassName()%></td>
                <td>第 <%=r.getAttemptNo()%> 次</td>
                <td><strong><%=r.getTotalScore()%></strong></td>
                <td><%=r.getSubmittedAt()%></td>
            </tr>
            <% }} else { %>
            <tr><td colspan="6" class="empty">暂无学生提交记录</td></tr>
            <% } %>
            </tbody>
        </table>
    </section>
</main>
</body>
</html>
