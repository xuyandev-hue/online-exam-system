<%@ page import="java.util.List" %>
<%@ page import="com.training.exam.model.Exam" %>
<%@ page import="com.training.exam.model.Question" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%!
    boolean hasType(List<Question> questions, String type) {
        if (questions == null) return false;
        for (Question q : questions) if (type.equals(q.getType())) return true;
        return false;
    }
    String sectionTitle(String type, int sectionNo) {
        String prefix = sectionNo == 1 ? "一" : sectionNo == 2 ? "二" : sectionNo == 3 ? "三" : "四";
        if ("SINGLE".equals(type)) return prefix + "、单选题";
        if ("MULTIPLE".equals(type)) return prefix + "、多选题";
        if ("JUDGE".equals(type)) return prefix + "、判断题";
        if ("SUBJECTIVE".equals(type)) return prefix + "、主观题";
        return prefix + "、" + type;
    }
%>
<%
    Exam exam = (Exam) request.getAttribute("exam");
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    Long recordId = (Long) request.getAttribute("recordId");
    String[] types = {"SINGLE", "MULTIPLE", "JUDGE", "SUBJECTIVE"};
%>
<!doctype html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%=exam.getTitle()%></title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/app.css?v=20260713-2">
    <script defer src="<%=request.getContextPath()%>/static/js/exam.js?v=20260713-2"></script>
</head>
<body class="exam-page">
<header class="topbar exam-topbar">
    <div>
        <strong><%=exam.getTitle()%></strong>
        <span>共 <%=questions == null ? 0 : questions.size()%> 题，答题时长 <%=exam.getDurationMinutes()%> 分钟</span>
    </div>
    <button type="submit" form="examForm" class="button primary">提交试卷</button>
</header>
<main class="container exam-layout">
    <form id="examForm" class="exam-form" method="post" action="<%=request.getContextPath()%>/student/submit">
        <input type="hidden" name="examId" value="<%=exam.getId()%>">
        <input type="hidden" name="recordId" value="<%=recordId%>">
        <%
            int no = 1;
            int sectionNo = 1;
            if (questions != null) {
                for (String type : types) {
                    if (!hasType(questions, type)) continue;
        %>
        <div class="question-section-title"><%=sectionTitle(type, sectionNo++)%></div>
        <%
                    for (Question q : questions) {
                        if (!type.equals(q.getType())) continue;
        %>
        <section class="question" id="question-<%=no%>">
            <div class="question-head">
                <h3>第 <%=no%> 题</h3>
                <span class="badge">本题满分 <%=q.getScore()%> 分</span>
            </div>
            <p class="question-content"><%=q.getContent()%></p>
            <div class="options">
            <% if ("SINGLE".equals(q.getType())) { %>
                <label class="option"><input type="radio" name="q_<%=q.getId()%>" value="A"> <span>A. <%=q.getOptionA()%></span></label>
                <label class="option"><input type="radio" name="q_<%=q.getId()%>" value="B"> <span>B. <%=q.getOptionB()%></span></label>
                <label class="option"><input type="radio" name="q_<%=q.getId()%>" value="C"> <span>C. <%=q.getOptionC()%></span></label>
                <label class="option"><input type="radio" name="q_<%=q.getId()%>" value="D"> <span>D. <%=q.getOptionD()%></span></label>
            <% } else if ("MULTIPLE".equals(q.getType())) { %>
                <label class="option"><input type="checkbox" name="q_<%=q.getId()%>" value="A"> <span>A. <%=q.getOptionA()%></span></label>
                <label class="option"><input type="checkbox" name="q_<%=q.getId()%>" value="B"> <span>B. <%=q.getOptionB()%></span></label>
                <label class="option"><input type="checkbox" name="q_<%=q.getId()%>" value="C"> <span>C. <%=q.getOptionC()%></span></label>
                <label class="option"><input type="checkbox" name="q_<%=q.getId()%>" value="D"> <span>D. <%=q.getOptionD()%></span></label>
            <% } else if ("JUDGE".equals(q.getType())) { %>
                <label class="option"><input type="radio" name="q_<%=q.getId()%>" value="T"> <span>正确</span></label>
                <label class="option"><input type="radio" name="q_<%=q.getId()%>" value="F"> <span>错误</span></label>
            <% } else { %>
                <textarea name="q_<%=q.getId()%>" rows="6" placeholder="请输入主观题答案"></textarea>
            <% } %>
            </div>
        </section>
        <%
                        no++;
                    }
                }
            }
        %>
    </form>
    <aside class="exam-sidebar">
        <div class="timer-card">
            <span class="timer-label">剩余时间</span>
            <strong id="sidebarTimer" data-minutes="<%=exam.getDurationMinutes()%>" data-switch-limit="<%=exam.getSwitchLimit()%>">--:--</strong>
            <p class="hint">时间结束后系统会自动提交。前 <%=Math.max(0, exam.getSwitchLimit() - 1)%> 次切屏警告，第 <%=exam.getSwitchLimit()%> 次强制收卷。</p>
        </div>
        <div class="question-nav">
            <h3>题目导航</h3>
            <div class="nav-grid">
                <% if (questions != null) { for (int i = 1; i <= questions.size(); i++) { %>
                <a href="#question-<%=i%>"><%=i%></a>
                <% }} %>
            </div>
        </div>
    </aside>
</main>
</body>
</html>
