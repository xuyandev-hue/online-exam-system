package com.training.exam.service;

import com.training.exam.model.Question;
import com.training.exam.util.DBUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AiCorrectService {
    public AiResult correct(Question question, String studentAnswer) {
        if (Boolean.parseBoolean(DBUtil.env("AI_MOCK", "true"))) {
            return mock(question, studentAnswer);
        }
        String apiUrl = DBUtil.env("AI_API_URL", "");
        String apiKey = DBUtil.env("AI_API_KEY", "");
        String model = DBUtil.env("AI_MODEL", "deepseek-chat");
        if (apiUrl.isBlank() || apiKey.isBlank()) {
            return new AiResult(0, "AI 配置缺失，需人工复核。");
        }
        try {
            String response = callOpenAiCompatibleApi(apiUrl, apiKey, model, question, studentAnswer);
            String content = extractAssistantContent(response);
            int score = extractScore(content, question.getScore());
            String comment = extractComment(content);
            return new AiResult(score, comment);
        } catch (IOException e) {
            return new AiResult(0, "AI 调用失败，需人工复核：" + e.getMessage());
        }
    }

    private AiResult mock(Question question, String studentAnswer) {
        if (studentAnswer == null || studentAnswer.isBlank()) {
            return new AiResult(0, "未作答。");
        }
        String reference = question.getAnswer() == null ? "" : question.getAnswer();
        String normalized = studentAnswer.toLowerCase();
        String[] keywords = {"docker", "compose", "tomcat", "mysql", "容器", "编排", "环境变量", "数据卷", "持久化", "网络", "部署"};
        int hits = 0;
        for (String keyword : keywords) {
            if (normalized.contains(keyword.toLowerCase())) {
                hits++;
            }
        }
        if (hits == 0) {
            return new AiResult(0, "Mock AI：答案与题目无关，建议得分 0 分。参考答案：" + reference);
        }
        int score = Math.min(question.getScore(), Math.max(1, hits * question.getScore() / 4));
        return new AiResult(score, "Mock AI：答案命中 " + hits + " 个参考要点，建议得分 " + score + " 分。参考答案：" + reference);
    }

    private String callOpenAiCompatibleApi(String apiUrl, String apiKey, String model, Question question, String studentAnswer) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(60000);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);

        String rubric = nullToEmpty(question.getAnalysis()).trim();
        String scoringInstruction = rubric.isBlank()
                ? "本题未提供评分标准，请根据参考答案，从内容的正确性、相关性和完整性综合判断，并按实际满分合理给分。"
                : "本题提供了评分标准，必须逐项判断学生答案命中了哪些评分点，并严格依据评分标准计分。"
                + "评分标准中的百分比按本题实际满分换算；如果标准写的是固定分值，则将各项固定分值视为相对权重，按本题实际满分等比例换算。";

        String prompt = "你是在线考试系统的主观题阅卷老师。请根据题目、参考答案、评分标准和学生答案评分。\n"
                + "本题实际满分为 " + question.getScore() + " 分，score 必须是 0 到 " + question.getScore() + " 之间的整数，不得超过实际满分。\n"
                + scoringInstruction + "\n"
                + "如果学生未作答或答案与题目无关，必须给 0 分；部分正确时按照已满足的评分点给部分分。\n"
                + "小数得分四舍五入为整数。comment 要简要说明命中的评分点、缺失内容和计分依据。\n"
                + "只返回 JSON，不要返回 Markdown。格式：{\"score\":数字,\"comment\":\"简短中文评语\"}。\n"
                + "题目：" + nullToEmpty(question.getContent()) + "\n"
                + "参考答案：" + nullToEmpty(question.getAnswer()) + "\n"
                + "评分标准：" + (rubric.isBlank() ? "未提供" : rubric) + "\n"
                + "学生答案：" + nullToEmpty(studentAnswer);

        String body = "{"
                + "\"model\":\"" + escapeJson(model) + "\","
                + "\"temperature\":0.1,"
                + "\"messages\":["
                + "{\"role\":\"system\",\"content\":\"你是严谨的考试阅卷助手。必须遵守评分标准和实际满分限制，只输出 JSON。\"},"
                + "{\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"}"
                + "]"
                + "}";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        InputStream stream = status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream();
        String response = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        if (status < 200 || status >= 300) {
            throw new IOException("HTTP " + status + " " + response);
        }
        return response;
    }

    private String extractAssistantContent(String response) {
        Matcher matcher = Pattern.compile("\"content\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"").matcher(response);
        String content = null;
        while (matcher.find()) {
            content = unescapeJson(matcher.group(1));
        }
        return content == null ? response : content;
    }

    private int extractScore(String content, int maxScore) {
        Matcher jsonScore = Pattern.compile("\"score\"\\s*:\\s*(\\d+)").matcher(content);
        if (jsonScore.find()) {
            return clamp(Integer.parseInt(jsonScore.group(1)), maxScore);
        }
        Matcher chineseScore = Pattern.compile("(\\d+)\\s*分").matcher(content);
        if (chineseScore.find()) {
            return clamp(Integer.parseInt(chineseScore.group(1)), maxScore);
        }
        return 0;
    }

    private String extractComment(String content) {
        Matcher matcher = Pattern.compile("\"comment\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"").matcher(content);
        if (matcher.find()) {
            return unescapeJson(matcher.group(1));
        }
        return content.length() > 300 ? content.substring(0, 300) : content;
    }

    private int clamp(int score, int maxScore) {
        return Math.max(0, Math.min(maxScore, score));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String escapeJson(String value) {
        return nullToEmpty(value)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private String unescapeJson(String value) {
        return value.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    public static class AiResult {
        private final int score;
        private final String comment;

        public AiResult(int score, String comment) {
            this.score = score;
            this.comment = comment;
        }

        public int getScore() {
            return score;
        }

        public String getComment() {
            return comment;
        }
    }
}
