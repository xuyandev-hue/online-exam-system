package com.training.exam.service;

import com.training.exam.model.Answer;
import com.training.exam.model.Question;

import java.util.*;
import java.util.stream.Collectors;

public class GradingService {
    private final AiCorrectService aiCorrectService = new AiCorrectService();

    public Answer grade(Question question, String rawAnswer) {
        Answer answer = new Answer();
        answer.setQuestionId(question.getId());
        answer.setAnswerText(rawAnswer);
        if ("SUBJECTIVE".equals(question.getType())) {
            AiCorrectService.AiResult result = aiCorrectService.correct(question, rawAnswer);
            answer.setScore(result.getScore());
            answer.setCommentText(result.getComment());
            return answer;
        }
        boolean correct = normalize(question.getAnswer()).equals(normalize(rawAnswer));
        answer.setScore(correct ? question.getScore() : 0);
        answer.setCommentText(correct ? "客观题自动判分：正确。" : "客观题自动判分：错误。参考答案：" + question.getAnswer());
        return answer;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Arrays.stream(value.toUpperCase(Locale.ROOT).split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .sorted()
                .collect(Collectors.joining(","));
    }
}
