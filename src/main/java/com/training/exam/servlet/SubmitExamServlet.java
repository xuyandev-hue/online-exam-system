package com.training.exam.servlet;

import com.training.exam.dao.QuestionDao;
import com.training.exam.dao.RecordDao;
import com.training.exam.model.Answer;
import com.training.exam.model.Question;
import com.training.exam.service.GradingService;
import com.training.exam.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/student/submit")
public class SubmitExamServlet extends HttpServlet {
    private final QuestionDao questionDao = new QuestionDao();
    private final RecordDao recordDao = new RecordDao();
    private final GradingService gradingService = new GradingService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.requireLogin(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");
        try {
            long examId = Long.parseLong(WebUtil.param(request, "examId"));
            long recordId = Long.parseLong(WebUtil.param(request, "recordId"));
            List<Answer> answers = new ArrayList<>();
            int totalScore = 0;
            for (Question question : questionDao.findByExam(examId)) {
                String name = "q_" + question.getId();
                String rawAnswer;
                if ("MULTIPLE".equals(question.getType())) {
                    String[] values = request.getParameterValues(name);
                    rawAnswer = values == null ? "" : String.join(",", values);
                } else {
                    rawAnswer = WebUtil.param(request, name);
                }
                Answer answer = gradingService.grade(question, rawAnswer);
                totalScore += answer.getScore();
                answers.add(answer);
            }
            recordDao.submit(recordId, answers, totalScore);
            response.sendRedirect(request.getContextPath() + "/student/result?recordId=" + recordId);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
