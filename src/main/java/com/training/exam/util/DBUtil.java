package com.training.exam.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBUtil {
    private DBUtil() {}

    public static Connection getConnection() throws SQLException {
        String host = env("DB_HOST", "localhost");
        String port = env("DB_PORT", "3306");
        String db = env("DB_NAME", "online_exam");
        String user = env("DB_USER", "exam_user");
        String password = env("DB_PASSWORD", "exam_pass");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci"
                + "&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found", e);
        }
        return DriverManager.getConnection(url, user, password);
    }

    public static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
