package com.training.exam.dao;

import com.training.exam.model.Answer;
import com.training.exam.model.ExamRecord;
import com.training.exam.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecordDao {
    public long startRecord(long examId, long studentId) throws SQLException {
        String existing = "SELECT id FROM exam_records WHERE exam_id=? AND student_id=? AND status='IN_PROGRESS' ORDER BY id DESC LIMIT 1";
        String count = "SELECT COUNT(*) FROM exam_records WHERE exam_id=? AND student_id=? AND status='SUBMITTED'";
        String insert = "INSERT INTO exam_records(exam_id, student_id, status, attempt_no) VALUES(?,?, 'IN_PROGRESS', ?)";
        try (Connection conn = DBUtil.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(existing)) {
                ps.setLong(1, examId);
                ps.setLong(2, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
            int attemptNo = 1;
            try (PreparedStatement ps = conn.prepareStatement(count)) {
                ps.setLong(1, examId);
                ps.setLong(2, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) attemptNo = rs.getInt(1) + 1;
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, examId);
                ps.setLong(2, studentId);
                ps.setInt(3, attemptNo);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    return rs.getLong(1);
                }
            }
        }
    }

    public int countAttempts(long examId, long studentId) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM exam_records WHERE exam_id=? AND student_id=? AND status='SUBMITTED'")) {
            ps.setLong(1, examId);
            ps.setLong(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void submit(long recordId, List<Answer> answers, int totalScore) throws SQLException {
        String answerSql = "INSERT INTO answers(record_id, question_id, answer_text, score, comment_text) VALUES(?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE answer_text=VALUES(answer_text), score=VALUES(score), comment_text=VALUES(comment_text)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(answerSql)) {
                for (Answer answer : answers) {
                    ps.setLong(1, recordId);
                    ps.setLong(2, answer.getQuestionId());
                    ps.setString(3, answer.getAnswerText());
                    ps.setInt(4, answer.getScore());
                    ps.setString(5, answer.getCommentText());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            try (PreparedStatement ps = conn.prepareStatement("UPDATE exam_records SET status='SUBMITTED', total_score=?, submitted_at=NOW() WHERE id=?")) {
                ps.setInt(1, totalScore);
                ps.setLong(2, recordId);
                ps.executeUpdate();
            }
            conn.commit();
        }
    }

    public List<ExamRecord> findByStudent(long studentId) throws SQLException {
        String sql = "SELECT r.*, e.title exam_title, e.total_score exam_total_score FROM exam_records r JOIN exams e ON r.exam_id=e.id WHERE r.student_id=? ORDER BY r.id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<ExamRecord> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        }
    }

    public List<ExamRecord> findBestByStudent(long studentId) throws SQLException {
        String sql = "SELECT r.*, e.title exam_title, e.total_score exam_total_score FROM exam_records r JOIN exams e ON r.exam_id=e.id "
                + "WHERE r.student_id=? AND r.status='SUBMITTED' "
                + "AND r.id=(SELECT r2.id FROM exam_records r2 WHERE r2.student_id=r.student_id AND r2.exam_id=r.exam_id AND r2.status='SUBMITTED' "
                + "ORDER BY r2.total_score DESC, r2.submitted_at DESC, r2.id DESC LIMIT 1) ORDER BY r.id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<ExamRecord> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }

    public List<ExamRecord> findSubmitted() throws SQLException {
        String sql = "SELECT r.*, e.title exam_title, e.total_score exam_total_score, u.real_name student_name, u.class_name FROM exam_records r "
                + "JOIN exams e ON r.exam_id=e.id JOIN users u ON r.student_id=u.id WHERE r.status='SUBMITTED' ORDER BY r.submitted_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<ExamRecord> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        }
    }

    public List<Answer> findAnswers(long recordId) throws SQLException {
        String sql = "SELECT a.*, q.type question_type, q.content question_content, q.option_a, q.option_b, q.option_c, q.option_d, "
                + "q.answer correct_answer, q.analysis, eq.assigned_score max_score FROM answers a "
                + "JOIN exam_records r ON a.record_id=r.id JOIN questions q ON a.question_id=q.id "
                + "LEFT JOIN exam_questions eq ON eq.exam_id=r.exam_id AND eq.question_id=a.question_id "
                + "WHERE a.record_id=? ORDER BY eq.sort_no, a.question_id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, recordId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Answer> list = new ArrayList<>();
                while (rs.next()) {
                    Answer answer = new Answer();
                    answer.setQuestionId(rs.getLong("question_id"));
                    answer.setAnswerText(rs.getString("answer_text"));
                    answer.setScore(rs.getInt("score"));
                    answer.setCommentText(rs.getString("comment_text"));
                    answer.setQuestionType(rs.getString("question_type"));
                    answer.setQuestionContent(rs.getString("question_content"));
                    answer.setOptionA(rs.getString("option_a"));
                    answer.setOptionB(rs.getString("option_b"));
                    answer.setOptionC(rs.getString("option_c"));
                    answer.setOptionD(rs.getString("option_d"));
                    answer.setCorrectAnswer(rs.getString("correct_answer"));
                    answer.setAnalysis(rs.getString("analysis"));
                    answer.setMaxScore(rs.getInt("max_score"));
                    list.add(answer);
                }
                return list;
            }
        }
    }

    public ExamRecord findById(long recordId) throws SQLException {
        String sql = "SELECT r.*, e.title exam_title, e.total_score exam_total_score, u.real_name student_name, u.class_name FROM exam_records r "
                + "JOIN exams e ON r.exam_id=e.id JOIN users u ON r.student_id=u.id WHERE r.id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, recordId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    private ExamRecord map(ResultSet rs) throws SQLException {
        ExamRecord record = new ExamRecord();
        record.setId(rs.getLong("id"));
        record.setExamId(rs.getLong("exam_id"));
        record.setStudentId(rs.getLong("student_id"));
        record.setStatus(rs.getString("status"));
        record.setTotalScore(rs.getInt("total_score"));
        record.setAttemptNo(rs.getInt("attempt_no"));
        record.setStartedAt(rs.getTimestamp("started_at"));
        record.setSubmittedAt(rs.getTimestamp("submitted_at"));
        safeSet(rs, "exam_title", record::setExamTitle);
        safeSet(rs, "student_name", record::setStudentName);
        safeSet(rs, "class_name", record::setClassName);
        try {
            record.setExamTotalScore(rs.getInt("exam_total_score"));
        } catch (SQLException ignored) {
            // Optional joined column.
        }
        return record;
    }

    private void safeSet(ResultSet rs, String column, Setter setter) throws SQLException {
        try {
            setter.set(rs.getString(column));
        } catch (SQLException ignored) {
            // Optional joined column.
        }
    }

    private interface Setter {
        void set(String value);
    }
}
