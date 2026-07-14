package com.training.exam.servlet;

import com.training.exam.dao.RecordDao;
import com.training.exam.model.ExamRecord;
import com.training.exam.model.User;
import com.training.exam.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/student/result", "/exam/view"})
public class ResultServlet extends HttpServlet {
    private final RecordDao recordDao = new RecordDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.requireLogin(request, response)) {
            return;
        }
        try {
            long recordId = Long.parseLong(WebUtil.param(request, "recordId"));
            ExamRecord record = recordDao.findById(recordId);
            User user = WebUtil.currentUser(request);
            if (record == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (user.isStudent() && record.getStudentId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            request.setAttribute("record", record);
            request.setAttribute("answers", recordDao.findAnswers(recordId));
            request.getRequestDispatcher("/WEB-INF/jsp/result.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
