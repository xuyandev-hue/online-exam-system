<%@ page import="java.util.List" %>
<%@ page import="com.training.exam.model.Question" %>
<%@ page import="com.training.exam.model.Exam" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%!
    boolean hasType(List<Question> questions, String type) {
        if (questions == null) return false;
        for (Question q : questions) if (type.equals(q.getType())) return true;
        return false;
    }
    String typeName(String type) {
        if ("SINGLE".equals(type)) return "单选题";
        if ("MULTIPLE".equals(type)) return "多选题";
        if ("JUDGE".equals(type)) return "判断题";
        if ("SUBJECTIVE".equals(type)) return "主观题";
        return type;
    }
%>
<%
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    List<Exam> exams = (List<Exam>) request.getAttribute("exams");
    String[] types = {"SINGLE", "MULTIPLE", "JUDGE", "SUBJECTIVE"};
%>
<!doctype html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>组卷发布</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/app.css">
</head>
<body>
<header class="topbar">
    <div><strong>组卷发布</strong><span>按题型筛选题目并发布考试</span></div>
    <nav>
        <a href="<%=request.getContextPath()%>/dashboard">工作台</a>
        <a href="<%=request.getContextPath()%>/teacher/questions">题库管理</a>
        <a href="<%=request.getContextPath()%>/logout">退出</a>
    </nav>
</header>
<main class="container two-column exam-manage-layout">
    <section class="panel">
        <div class="section-title"><h2>创建试卷</h2></div>
        <% if (request.getAttribute("error") != null) { %><p class="error"><%=request.getAttribute("error")%></p><% } %>
        <form method="post" class="stack-form">
            <label>试卷标题 <input name="title" required placeholder="例如：Java Web 基础测试"></label>
            <label>考试开始时间 <input name="startTime" type="datetime-local" required></label>
            <label>考试结束时间 <input name="endTime" type="datetime-local" required></label>
            <div class="form-grid">
                <label>答题时长（分钟） <input name="durationMinutes" type="number" min="1" value="30" required></label>
                <label>允许考试次数 <input name="maxAttempts" type="number" min="1" value="1" required></label>
            </div>
            <label>试卷总分 <input name="totalScore" type="number" min="1" value="100" required></label>
            <label class="inline switch"><input name="published" type="checkbox" checked> 发布考试</label>

            <div class="type-tabs" role="tablist">
                <button type="button" class="type-tab active" data-type="ALL">全部</button>
                <button type="button" class="type-tab" data-type="SINGLE">单选题</button>
                <button type="button" class="type-tab" data-type="MULTIPLE">多选题</button>
                <button type="button" class="type-tab" data-type="JUDGE">判断题</button>
                <button type="button" class="type-tab" data-type="SUBJECTIVE">主观题</button>
            </div>

            <div class="question-picker grouped-picker">
                <% if (questions != null && !questions.isEmpty()) { %>
                    <% for (String type : types) { if (hasType(questions, type)) { %>
                    <div class="picker-group" data-type="<%=type%>">
                        <h3><%=typeName(type)%></h3>
                        <% for (Question q : questions) { if (type.equals(q.getType())) { %>
                        <label class="inline picker-row">
                            <input type="checkbox" name="questionIds" value="<%=q.getId()%>">
                            <span>#<%=q.getId()%> <%=q.getContent()%>（<%=q.getScore() > 0 ? q.getScore() + " 分，按总分比例换算" : "自动分配"%>）</span>
                        </label>
                        <% }} %>
                    </div>
                    <% }} %>
                <% } else { %>
                    <p class="empty">暂无可选题目</p>
                <% } %>
            </div>
            <button type="submit" class="button primary">保存试卷</button>
        </form>
    </section>
    <section class="panel">
        <div class="section-title"><h2>试卷列表</h2><span class="muted"><%=exams == null ? 0 : exams.size()%> 份</span></div>
        <% if (exams != null && !exams.isEmpty()) { for (Exam exam : exams) { %>
        <article class="item exam-card">
            <h3><%=exam.getTitle()%></h3>
            <p>考试时间：<%=exam.getStartTime()%> 至 <%=exam.getEndTime()%></p>
            <p>答题时长：<strong><%=exam.getDurationMinutes()%></strong> 分钟；次数：<strong><%=exam.getMaxAttempts()%></strong>；总分：<strong><%=exam.getTotalScore()%></strong></p>
            <span class="badge"><%=exam.isPublished() ? "已发布" : "未发布"%></span>
        </article>
        <% }} else { %>
        <p class="empty">暂无试卷</p>
        <% } %>
    </section>
</main>
<script src="<%=request.getContextPath()%>/static/js/exam-builder.js"></script>
</body>
</html>
