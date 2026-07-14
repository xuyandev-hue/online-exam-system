package com.training.exam.dao;

import com.training.exam.model.Exam;
import com.training.exam.model.Question;
import com.training.exam.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDao {
    public List<Exam> findAll(boolean onlyPublished) throws SQLException {
        String sql = "SELECT * FROM exams" + (onlyPublished ? " WHERE published=1" : "") + " ORDER BY id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Exam> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        }
    }

    public Exam findById(long id) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM exams WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void save(Exam exam, long[] questionIds, long userId) throws SQLException {
        String examSql = "INSERT INTO exams(title, duration_minutes, start_time, end_time, max_attempts, switch_limit, total_score, published, created_by) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(examSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, exam.getTitle());
                ps.setInt(2, exam.getDurationMinutes());
                ps.setTimestamp(3, exam.getStartTime());
                ps.setTimestamp(4, exam.getEndTime());
                ps.setInt(5, exam.getMaxAttempts());
                ps.setInt(6, exam.getSwitchLimit());
                ps.setInt(7, exam.getTotalScore());
                ps.setBoolean(8, exam.isPublished());
                ps.setLong(9, userId);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    long examId = keys.getLong(1);
                    insertQuestions(conn, examId, questionIds, exam.getTotalScore());
                }
            }
            conn.commit();
        }
    }

    private void insertQuestions(Connection conn, long examId, long[] questionIds, int totalScore) throws SQLException {
        List<Question> selected = findQuestions(conn, questionIds);
        int fixed = 0;
        int unspecified = 0;
        for (Question q : selected) {
            if (q.getScore() > 0) fixed += q.getScore(); else unspecified++;
        }
        int remaining = Math.max(0, totalScore - fixed);
        int base = unspecified == 0 ? 0 : remaining / unspecified;
        int extra = unspecified == 0 ? 0 : remaining % unspecified;
        int scaledAssignedSum = 0;
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO exam_questions(exam_id, question_id, sort_no, assigned_score) VALUES(?,?,?,?)")) {
            int sortNo = 1;
            for (int i = 0; i < selected.size(); i++) {
                Question q = selected.get(i);
                int assigned = q.getScore();
                if (fixed > 0 && unspecified == 0) {
                    assigned = i == selected.size() - 1
                            ? totalScore - scaledAssignedSum
                            : (int) Math.round((double) q.getScore() * totalScore / fixed);
                    scaledAssignedSum += assigned;
                } else if (assigned <= 0) {
                    assigned = base + (extra > 0 ? 1 : 0);
                    if (extra > 0) extra--;
                }
                /*    - q.getScore()：当前题目在题库中的原始分值；
                      - totalScore：当前试卷总分；
                      - fixed：所有选中题目的原始分值总和；
                      - assigned：当前题目在这张试卷中的实际分值；
                      - scaledAssignedSum：前面题目已经分配的总分。
                * */
                ps.setLong(1, examId);
                ps.setLong(2, q.getId());
                ps.setInt(3, sortNo++);
                ps.setInt(4, assigned);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<Question> findQuestions(Connection conn, long[] questionIds) throws SQLException {
        List<Question> list = new ArrayList<>();
        if (questionIds.length == 0) return list;
        StringBuilder sql = new StringBuilder("SELECT * FROM questions WHERE id IN (");
        for (int i = 0; i < questionIds.length; i++) sql.append(i == 0 ? "?" : ",?");
        sql.append(") ORDER BY FIELD(type,'SINGLE','MULTIPLE','JUDGE','SUBJECTIVE'), id");
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < questionIds.length; i++) ps.setLong(i + 1, questionIds[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Question q = new Question();
                    q.setId(rs.getLong("id"));
                    q.setType(rs.getString("type"));
                    q.setScore(rs.getInt("score"));
                    list.add(q);
                }
            }
        }
        return list;
    }

    private Exam map(ResultSet rs) throws SQLException {
        Exam exam = new Exam();
        exam.setId(rs.getLong("id"));
        exam.setTitle(rs.getString("title"));
        exam.setDurationMinutes(rs.getInt("duration_minutes"));
        exam.setStartTime(rs.getTimestamp("start_time"));
        exam.setEndTime(rs.getTimestamp("end_time"));
        exam.setMaxAttempts(rs.getInt("max_attempts"));
        exam.setSwitchLimit(rs.getInt("switch_limit"));
        exam.setTotalScore(rs.getInt("total_score"));
        exam.setPublished(rs.getBoolean("published"));
        return exam;
    }
}
