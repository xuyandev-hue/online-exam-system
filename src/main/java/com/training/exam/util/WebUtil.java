package com.training.exam.util;

import com.training.exam.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class WebUtil {
    private WebUtil() {}

    public static User currentUser(HttpServletRequest request) {
        Object user = request.getSession().getAttribute("user");
        return user instanceof User ? (User) user : null;
    }

    public static boolean requireLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (currentUser(request) == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    public static boolean requireTeacher(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = currentUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        if (!user.isTeacher()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    public static String param(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
