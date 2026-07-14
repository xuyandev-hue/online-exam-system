package com.training.exam.model;

import java.sql.Timestamp;

public class ExamRecord {
    private long id;
    private long examId;
    private long studentId;
    private String studentName;
    private String className;
    private String examTitle;
    private String status;
    private int totalScore;
    private int examTotalScore;
    private int attemptNo;
    private Timestamp startedAt;
    private Timestamp submittedAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getExamId() { return examId; }
    public void setExamId(long examId) { this.examId = examId; }
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public int getExamTotalScore() { return examTotalScore; }
    public void setExamTotalScore(int examTotalScore) { this.examTotalScore = examTotalScore; }
    public int getAttemptNo() { return attemptNo; }
    public void setAttemptNo(int attemptNo) { this.attemptNo = attemptNo; }
    public Timestamp getStartedAt() { return startedAt; }
    public void setStartedAt(Timestamp startedAt) { this.startedAt = startedAt; }
    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }
}
