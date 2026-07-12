package com.training.exam.dao;

import com.training.exam.model.Question;
import com.training.exam.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDao {
    public List<Question> findAll() throws SQLException {
        String sql = "SELECT * FROM questions ORDER BY FIELD(type,'SINGLE','MULTIPLE','JUDGE','SUBJECTIVE'), id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Question> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        }
    }

    public Question findById(long id) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM questions WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Question> findByExam(long examId) throws SQLException {
        String sql = "SELECT q.*, eq.assigned_score AS exam_score FROM questions q JOIN exam_questions eq ON q.id=eq.question_id WHERE eq.exam_id=? ORDER BY FIELD(q.type,'SINGLE','MULTIPLE','JUDGE','SUBJECTIVE'), eq.sort_no, q.id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Question> list = new ArrayList<>();
                while (rs.next()) {
                    Question q = map(rs);
                    q.setScore(rs.getInt("exam_score"));
                    list.add(q);
                }
                return list;
            }
        }
    }

    public void save(Question q, long userId) throws SQLException {
        String sql = "INSERT INTO questions(type, content, option_a, option_b, option_c, option_d, answer, analysis, score, created_by) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q.getType());
            ps.setString(2, q.getContent());
            ps.setString(3, q.getOptionA());
            ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC());
            ps.setString(6, q.getOptionD());
            ps.setString(7, q.getAnswer());
            ps.setString(8, q.getAnalysis());
            ps.setInt(9, q.getScore());
            ps.setLong(10, userId);
            ps.executeUpdate();
        }
    }

    public void update(Question q) throws SQLException {
        String sql = "UPDATE questions SET type=?, content=?, option_a=?, option_b=?, option_c=?, option_d=?, answer=?, analysis=?, score=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q.getType());
            ps.setString(2, q.getContent());
            ps.setString(3, q.getOptionA());
            ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC());
            ps.setString(6, q.getOptionD());
            ps.setString(7, q.getAnswer());
            ps.setString(8, q.getAnalysis());
            ps.setInt(9, q.getScore());
            ps.setLong(10, q.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM questions WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Question map(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setId(rs.getLong("id"));
        q.setType(rs.getString("type"));
        q.setContent(rs.getString("content"));
        q.setOptionA(rs.getString("option_a"));
        q.setOptionB(rs.getString("option_b"));
        q.setOptionC(rs.getString("option_c"));
        q.setOptionD(rs.getString("option_d"));
        q.setAnswer(rs.getString("answer"));
        q.setAnalysis(rs.getString("analysis"));
        q.setScore(rs.getInt("score"));
        return q;
    }
}
