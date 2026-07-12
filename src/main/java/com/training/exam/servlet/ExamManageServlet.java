package com.training.exam.servlet;

import com.training.exam.dao.ExamDao;
import com.training.exam.dao.QuestionDao;
import com.training.exam.model.Exam;
import com.training.exam.model.User;
import com.training.exam.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;

@WebServlet("/teacher/exams")
public class ExamManageServlet extends HttpServlet {
    private final ExamDao examDao = new ExamDao();
    private final QuestionDao questionDao = new QuestionDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.requireTeacher(request, response)) {
            return;
        }
        try {
            request.setAttribute("questions", questionDao.findAll());
            request.setAttribute("exams", examDao.findAll(false));
            request.getRequestDispatcher("/WEB-INF/jsp/exams.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.requireTeacher(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");
        try {
            User user = WebUtil.currentUser(request);
            Exam exam = new Exam();
            exam.setTitle(WebUtil.param(request, "title"));
            exam.setDurationMinutes(Integer.parseInt(WebUtil.param(request, "durationMinutes")));
            exam.setStartTime(parseTimestamp(WebUtil.param(request, "startTime")));
            exam.setEndTime(parseTimestamp(WebUtil.param(request, "endTime")));
            exam.setMaxAttempts(parseInt(WebUtil.param(request, "maxAttempts"), 1));
            exam.setTotalScore(parseInt(WebUtil.param(request, "totalScore"), 100));
            exam.setPublished("on".equals(request.getParameter("published")));

            String[] selected = request.getParameterValues("questionIds");
            long[] questionIds = selected == null ? new long[0] : Arrays.stream(selected).mapToLong(Long::parseLong).toArray();
            if (questionIds.length == 0) {
                request.setAttribute("error", "至少选择一道题目");
                doGet(request, response);
                return;
            }
            examDao.save(exam, questionIds, user.getId());
            response.sendRedirect(request.getContextPath() + "/teacher/exams");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private Timestamp parseTimestamp(String value) {
        return value == null || value.isBlank() ? null : Timestamp.valueOf(LocalDateTime.parse(value));
    }

    private int parseInt(String value, int defaultValue) {
        return value == null || value.isBlank() ? defaultValue : Integer.parseInt(value);
    }
}
