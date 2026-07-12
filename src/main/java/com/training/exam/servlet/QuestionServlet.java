package com.training.exam.servlet;

import com.training.exam.dao.QuestionDao;
import com.training.exam.model.Question;
import com.training.exam.model.User;
import com.training.exam.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/teacher/questions")
public class QuestionServlet extends HttpServlet {
    private final QuestionDao questionDao = new QuestionDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.requireTeacher(request, response)) {
            return;
        }
        try {
            String deleteId = request.getParameter("delete");
            if (deleteId != null) {
                questionDao.delete(Long.parseLong(deleteId));
                response.sendRedirect(request.getContextPath() + "/teacher/questions");
                return;
            }
            String editId = request.getParameter("edit");
            if (editId != null) {
                request.setAttribute("editQuestion", questionDao.findById(Long.parseLong(editId)));
            }
            request.setAttribute("questions", questionDao.findAll());
            request.getRequestDispatcher("/WEB-INF/jsp/questions.jsp").forward(request, response);
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
            Question q = new Question();
            String questionId = WebUtil.param(request, "questionId");
            q.setType(WebUtil.param(request, "type"));
            q.setContent(WebUtil.param(request, "content"));
            q.setOptionA(WebUtil.param(request, "optionA"));
            q.setOptionB(WebUtil.param(request, "optionB"));
            q.setOptionC(WebUtil.param(request, "optionC"));
            q.setOptionD(WebUtil.param(request, "optionD"));
            q.setAnswer(WebUtil.param(request, "answer"));
            q.setAnalysis(WebUtil.param(request, "analysis"));
            String score = WebUtil.param(request, "score");
            q.setScore(score.isBlank() ? 0 : Integer.parseInt(score));
            if (questionId.isBlank()) {
                questionDao.save(q, user.getId());
            } else {
                q.setId(Long.parseLong(questionId));
                questionDao.update(q);
            }
            response.sendRedirect(request.getContextPath() + "/teacher/questions");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
