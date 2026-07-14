package com.training.exam.servlet;

import com.training.exam.dao.ExamDao;
import com.training.exam.dao.RecordDao;
import com.training.exam.model.User;
import com.training.exam.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Shanghai");
    private final ExamDao examDao = new ExamDao();
    private final RecordDao recordDao = new RecordDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.requireLogin(request, response)) {
            return;
        }
        try {
            User user = WebUtil.currentUser(request);
            if (user.isTeacher()) {
                request.setAttribute("records", recordDao.findSubmitted());
                request.setAttribute("exams", examDao.findAll(false));
                request.getRequestDispatcher("/WEB-INF/jsp/teacher-dashboard.jsp").forward(request, response);
            } else {
                var exams = examDao.findAll(true);
                LocalDateTime now = LocalDateTime.now(APP_ZONE);
                for (var exam : exams) {
                    int used = recordDao.countAttempts(exam.getId(), user.getId());
                    exam.setRemainingAttempts(Math.max(0, exam.getMaxAttempts() - used));
                    if (exam.getStartTime() != null && now.isBefore(exam.getStartTime().toLocalDateTime())) {
                        exam.setAvailabilityStatus("NOT_STARTED");
                    } else if (exam.getEndTime() != null && now.isAfter(exam.getEndTime().toLocalDateTime())) {
                        exam.setAvailabilityStatus("ENDED");
                    } else if (exam.getRemainingAttempts() == 0) {
                        exam.setAvailabilityStatus("EXHAUSTED");
                    } else {
                        exam.setAvailabilityStatus("AVAILABLE");
                    }
                }
                request.setAttribute("exams", exams);
                request.setAttribute("records", recordDao.findBestByStudent(user.getId()));
                request.getRequestDispatcher("/WEB-INF/jsp/student-dashboard.jsp").forward(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
