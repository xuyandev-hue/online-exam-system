package com.training.exam.model;

import java.sql.Timestamp;

public class Exam {
    private long id;
    private String title;
    private int durationMinutes;
    private Timestamp startTime;
    private Timestamp endTime;
    private int maxAttempts;
    private int switchLimit;
    private int totalScore;
    private boolean published;
    private int remainingAttempts;
    private String availabilityStatus;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }
    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }
    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
    public int getSwitchLimit() { return switchLimit; }
    public void setSwitchLimit(int switchLimit) { this.switchLimit = switchLimit; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public int getRemainingAttempts() { return remainingAttempts; }
    public void setRemainingAttempts(int remainingAttempts) { this.remainingAttempts = remainingAttempts; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    public boolean isAvailable() { return "AVAILABLE".equals(availabilityStatus); }
}
