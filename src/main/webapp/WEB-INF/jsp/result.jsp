<%@ page import="java.util.List" %>
<%@ page import="com.training.exam.model.Answer" %>
<%@ page import="com.training.exam.model.ExamRecord" %>
<%@ page import="com.training.exam.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%!
    String safe(String value) { return value == null || value.isBlank() ? "未作答" : value; }
    String detail(String value) { return value == null || value.isBlank() ? "暂无解析" : value; }
    String typeName(String type) {
        if ("SINGLE".equals(type)) return "单选题";
        if ("MULTIPLE".equals(type)) return "多选题";
        if ("JUDGE".equals(type)) return "判断题";
        if ("SUBJECTIVE".equals(type)) return "主观题";
        return type;
    }
%>
<%
    List<Answer> answers = (List<Answer>) request.getAttribute("answers");
    ExamRecord record = (ExamRecord) request.getAttribute("record");
    User user = (User) session.getAttribute("user");
    boolean teacher = user != null && user.isTeacher();
    int fullScoreQuestions = 0;
    int total = answers == null ? 0 : answers.size();
    if (answers != null) {
        for (Answer a : answers) if (a.getMaxScore() > 0 && a.getScore() == a.getMaxScore()) fullScoreQuestions++;
    }
%>
<!doctype html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>查看试卷 - <%=record.getExamTitle()%></title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/app.css?v=20260713-2">
</head>
<body class="paper-review-page">
<header class="topbar">
    <div>
        <strong>试卷详情</strong>
        <span><%=record.getExamTitle()%><%=teacher ? " · " + record.getStudentName() : ""%></span>
    </div>
    <nav><a class="button secondary compact" href="<%=request.getContextPath()%>/dashboard">返回工作台</a></nav>
</header>
<main class="container review-container">
    <section class="review-hero">
        <div>
            <p class="eyebrow"><%=teacher ? "TEACHER REVIEW" : "MY EXAM PAPER"%></p>
            <h1><%=record.getExamTitle()%></h1>
            <p class="muted"><%=teacher ? record.getStudentName() + " · " + record.getClassName() + " · " : ""%>第 <%=record.getAttemptNo()%> 次作答 · <%=record.getSubmittedAt()%></p>
        </div>
        <div class="hero-score"><span>总得分</span><strong><%=record.getTotalScore()%><small> / <%=record.getExamTotalScore()%></small></strong></div>
    </section>

    <section class="stats review-stats">
        <div><span>总得分</span><strong><%=record.getTotalScore()%> / <%=record.getExamTotalScore()%></strong></div>
        <div><span>满分题数</span><strong><%=fullScoreQuestions%> / <%=total%></strong></div>
        <div><span>考试次数</span><strong>第 <%=record.getAttemptNo()%> 次</strong></div>
        <div><span>状态</span><strong class="status-success">已提交</strong></div>
    </section>

    <section class="review-list">
        <% if (answers != null && !answers.isEmpty()) { int no = 1; for (Answer a : answers) { %>
        <article class="review-question <%=a.getScore() == a.getMaxScore() ? "is-correct" : "needs-review"%>">
            <div class="review-question-head">
                <div><span class="question-number"><%=no++%></span><span class="badge"><%=typeName(a.getQuestionType())%></span></div>
                <strong class="question-score"><%=a.getScore()%> / <%=a.getMaxScore()%> 分</strong>
            </div>
            <h2><%=a.getQuestionContent()%></h2>
            <% if (!"SUBJECTIVE".equals(a.getQuestionType()) && !"JUDGE".equals(a.getQuestionType())) { %>
            <div class="review-options">
                <% if (a.getOptionA() != null) { %><span>A. <%=a.getOptionA()%></span><% } %>
                <% if (a.getOptionB() != null) { %><span>B. <%=a.getOptionB()%></span><% } %>
                <% if (a.getOptionC() != null) { %><span>C. <%=a.getOptionC()%></span><% } %>
                <% if (a.getOptionD() != null) { %><span>D. <%=a.getOptionD()%></span><% } %>
            </div>
            <% } %>
            <div class="answer-comparison">
                <div class="answer-box mine"><span>我的答案</span><strong><%=safe(a.getAnswerText())%></strong></div>
                <div class="answer-box correct"><span>正确答案</span><strong><%=safe(a.getCorrectAnswer())%></strong></div>
            </div>
            <div class="analysis-box">
                <span>解析</span>
                <p><%=detail(a.getAnalysis())%></p>
                <% if (a.getCommentText() != null && !a.getCommentText().isBlank()) { %><p class="grading-comment"><strong>评分说明：</strong><%=a.getCommentText()%></p><% } %>
            </div>
        </article>
        <% }} else { %>
        <div class="panel empty">暂无答题明细</div>
        <% } %>
    </section>
</main>
</body>
</html>
