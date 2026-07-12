package com.training.exam.servlet;

import com.training.exam.dao.UserDao;
import com.training.exam.model.User;
import com.training.exam.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            User user = userDao.login(WebUtil.param(request, "username"), WebUtil.param(request, "password"));
            if (user == null) {
                request.setAttribute("error", "用户名或密码错误");
                doGet(request, response);
                return;
            }
            request.getSession().setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
