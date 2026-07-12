package com.training.exam.dao;

import com.training.exam.model.User;
import com.training.exam.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT id, username, real_name, role, class_name FROM users WHERE username=? AND password=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setRealName(rs.getString("real_name"));
                user.setRole(rs.getString("role"));
                user.setClassName(rs.getString("class_name"));
                return user;
            }
        }
    }
}
