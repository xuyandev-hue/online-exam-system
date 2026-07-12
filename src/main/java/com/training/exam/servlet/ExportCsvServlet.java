package com.training.exam.servlet;

import com.training.exam.dao.RecordDao;
import com.training.exam.model.ExamRecord;
import com.training.exam.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/teacher/export")
public class ExportCsvServlet extends HttpServlet {
    private final RecordDao recordDao = new RecordDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebUtil.requireTeacher(request, response)) {
            return;
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=exam-scores.csv");
        try {
            response.getWriter().print('\uFEFF');
            response.getWriter().println("考试,姓名,班级,考试次数,分数,提交时间");
            for (ExamRecord r : recordDao.findSubmitted()) {
                response.getWriter().printf("%s,%s,%s,%d,%d,%s%n",
                        csv(r.getExamTitle()), csv(r.getStudentName()), csv(r.getClassName()), r.getAttemptNo(), r.getTotalScore(), r.getSubmittedAt());
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
