package com.training.exam.servlet;

import com.training.exam.dao.ExamDao;
import com.training.exam.dao.QuestionDao;
import com.training.exam.dao.RecordDao;
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
import java.time.ZoneId;

@WebServlet("/student/take")
public class TakeExamServlet extends HttpServlet {
    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Shanghai");
    private final ExamDao examDao = new ExamDao();
    private final QuestionDao questionDao = new QuestionDao();
    private final RecordDao recordDao = new RecordDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.requireLogin(request, response)) {
            return;
        }
        User user = WebUtil.currentUser(request);
        if (!user.isStudent()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            long examId = Long.parseLong(WebUtil.param(request, "examId"));
            Exam exam = examDao.findById(examId);
            LocalDateTime now = LocalDateTime.now(APP_ZONE);
            if (exam.getStartTime() != null && now.isBefore(exam.getStartTime().toLocalDateTime())) {
                response.sendRedirect(request.getContextPath() + "/dashboard?notice=not-started");
                return;
            }
            if (exam.getEndTime() != null && now.isAfter(exam.getEndTime().toLocalDateTime())) {
                response.sendRedirect(request.getContextPath() + "/dashboard?notice=ended");
                return;
            }
            if (recordDao.countAttempts(examId, user.getId()) >= exam.getMaxAttempts()) {
                response.sendRedirect(request.getContextPath() + "/dashboard?notice=attempts-used");
                return;
            }
            long recordId = recordDao.startRecord(examId, user.getId());
            request.setAttribute("recordId", recordId);
            request.setAttribute("exam", exam);
            request.setAttribute("questions", questionDao.findByExam(examId));
            request.getRequestDispatcher("/WEB-INF/jsp/take-exam.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
