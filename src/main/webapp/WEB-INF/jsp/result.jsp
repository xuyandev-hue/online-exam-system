<%@ page import="java.util.List" %>
<%@ page import="com.training.exam.model.Answer" %>
<%@ page import="com.training.exam.model.ExamRecord" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%
    List<Answer> answers = (List<Answer>) request.getAttribute("answers");
    ExamRecord record = (ExamRecord) request.getAttribute("record");
    int correct = 0;
    int total = answers == null ? 0 : answers.size();
    if (answers != null) {
        for (Answer a : answers) {
            if (a.getScore() > 0) correct++;
        }
    }
%>
<!doctype html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>考试结果</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/app.css">
</head>
<body>
<header class="topbar">
    <div><strong>考试结果</strong><span>查看总分、答对数量与每题评语</span></div>
    <nav><a href="<%=request.getContextPath()%>/dashboard">返回工作台</a></nav>
</header>
<main class="container">
    <section class="stats">
        <div><span>总分</span><strong><%=record == null ? 0 : record.getTotalScore()%></strong></div>
        <div><span>答对题数</span><strong><%=correct%>/<%=total%></strong></div>
        <div><span>考试次数</span><strong>第 <%=record == null ? 1 : record.getAttemptNo()%> 次</strong></div>
        <div><span>状态</span><strong><%=record == null ? "" : record.getStatus()%></strong></div>
    </section>
    <section class="panel">
        <div class="section-title"><h2>答题明细</h2></div>
        <table>
            <thead><tr><th>题目 ID</th><th>你的答案</th><th>得分</th><th>评语</th></tr></thead>
            <tbody>
            <% if (answers != null && !answers.isEmpty()) { for (Answer a : answers) { %>
            <tr>
                <td>#<%=a.getQuestionId()%></td>
                <td><%=a.getAnswerText()%></td>
                <td><strong><%=a.getScore()%></strong></td>
                <td><%=a.getCommentText()%></td>
            </tr>
            <% }} else { %>
            <tr><td colspan="4" class="empty">暂无答题明细</td></tr>
            <% } %>
            </tbody>
        </table>
    </section>
</main>
</body>
</html>
