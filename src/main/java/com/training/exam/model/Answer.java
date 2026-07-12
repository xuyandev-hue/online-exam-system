package com.training.exam.model;

public class Answer {
    private long questionId;
    private String answerText;
    private int score;
    private String commentText;

    public long getQuestionId() { return questionId; }
    public void setQuestionId(long questionId) { this.questionId = questionId; }
    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
}
