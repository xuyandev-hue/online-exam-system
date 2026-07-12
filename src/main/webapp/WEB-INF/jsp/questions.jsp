<%@ page import="java.util.List" %>
<%@ page import="com.training.exam.model.Question" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%!
    String val(String value) { return value == null ? "" : value; }
    String selected(Question q, String type) { return q != null && type.equals(q.getType()) ? "selected" : ""; }
%>
<%
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    Question edit = (Question) request.getAttribute("editQuestion");
    boolean editing = edit != null;
%>
<!doctype html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>题库管理</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/app.css">
</head>
<body class="question-manage-page">
<header class="topbar">
    <div><strong>题库管理</strong><span>维护考试题目与评分参考</span></div>
    <nav>
        <a href="<%=request.getContextPath()%>/dashboard">工作台</a>
        <a href="<%=request.getContextPath()%>/teacher/exams">组卷发布</a>
        <a href="<%=request.getContextPath()%>/logout">退出</a>
    </nav>
</header>
<main class="container two-column question-manage-layout">
    <section class="panel question-form-panel">
        <div class="section-title">
            <h2><%=editing ? "修改题目" : "新增题目"%></h2>
            <% if (editing) { %><a class="button secondary" href="<%=request.getContextPath()%>/teacher/questions">取消修改</a><% } %>
        </div>
        <form method="post" class="stack-form question-form-scroll">
            <% if (editing) { %><input type="hidden" name="questionId" value="<%=edit.getId()%>"><% } %>
            <label>题型
                <select name="type" required>
                    <option value="SINGLE" <%=selected(edit, "SINGLE")%>>单选题</option>
                    <option value="MULTIPLE" <%=selected(edit, "MULTIPLE")%>>多选题</option>
                    <option value="JUDGE" <%=selected(edit, "JUDGE")%>>判断题</option>
                    <option value="SUBJECTIVE" <%=selected(edit, "SUBJECTIVE")%>>主观题</option>
                </select>
            </label>
            <label>题干<textarea name="content" rows="4" required><%=editing ? val(edit.getContent()) : ""%></textarea></label>
            <div class="form-grid">
                <label>A <input name="optionA" value="<%=editing ? val(edit.getOptionA()) : ""%>"></label>
                <label>B <input name="optionB" value="<%=editing ? val(edit.getOptionB()) : ""%>"></label>
                <label>C <input name="optionC" value="<%=editing ? val(edit.getOptionC()) : ""%>"></label>
                <label>D <input name="optionD" value="<%=editing ? val(edit.getOptionD()) : ""%>"></label>
            </div>
            <label>标准答案
                <input name="answer" required value="<%=editing ? val(edit.getAnswer()) : ""%>" placeholder="单选 A；多选 A,B；判断 T/F；主观题写参考答案摘要">
            </label>
            <label>解析/评分参考<textarea name="analysis" rows="3"><%=editing ? val(edit.getAnalysis()) : ""%></textarea></label>
            <label>分值 <input name="score" type="number" min="0" value="<%=editing && edit.getScore() > 0 ? edit.getScore() : ""%>" placeholder="留空则组卷时自动平均分配"></label>
            <button type="submit" class="button primary"><%=editing ? "保存修改" : "保存题目"%></button>
        </form>
    </section>
    <section class="panel question-list-panel">
        <div class="section-title">
            <h2>题目列表</h2>
            <span class="muted"><%=questions == null ? 0 : questions.size()%> 道题</span>
        </div>
        <div class="question-list-scroll">
            <% if (questions != null && !questions.isEmpty()) { for (Question q : questions) { %>
            <article class="item question-item">
                <div class="row">
                    <strong>#<%=q.getId()%> <span class="badge"><%=q.getType()%></span> <%=q.getScore() > 0 ? q.getScore() + " 分" : "自动分配"%></strong>
                    <span>
                        <a href="<%=request.getContextPath()%>/teacher/questions?edit=<%=q.getId()%>">修改</a>
                        <a class="danger-link" href="<%=request.getContextPath()%>/teacher/questions?delete=<%=q.getId()%>" onclick="return confirm('确定删除这道题？')">删除</a>
                    </span>
                </div>
                <p><%=q.getContent()%></p>
                <p class="hint">答案：<%=q.getAnswer()%></p>
            </article>
            <% }} else { %>
            <p class="empty">暂无题目</p>
            <% } %>
        </div>
    </section>
</main>
</body>
</html>
