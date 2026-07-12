SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS online_exam DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE online_exam;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  real_name VARCHAR(50) NOT NULL,
  role ENUM('TEACHER','STUDENT') NOT NULL,
  class_name VARCHAR(50) DEFAULT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS questions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type ENUM('SINGLE','MULTIPLE','JUDGE','SUBJECTIVE') NOT NULL,
  content TEXT NOT NULL,
  option_a VARCHAR(500),
  option_b VARCHAR(500),
  option_c VARCHAR(500),
  option_d VARCHAR(500),
  answer TEXT NOT NULL,
  analysis TEXT,
  score INT NOT NULL DEFAULT 0,
  created_by BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exams (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  duration_minutes INT NOT NULL,
  start_time DATETIME NULL,
  end_time DATETIME NULL,
  max_attempts INT NOT NULL DEFAULT 1,
  total_score INT NOT NULL DEFAULT 100,
  published TINYINT(1) NOT NULL DEFAULT 0,
  created_by BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exam_questions (
  exam_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  sort_no INT NOT NULL DEFAULT 0,
  assigned_score INT NOT NULL DEFAULT 0,
  PRIMARY KEY (exam_id, question_id)
);

CREATE TABLE IF NOT EXISTS exam_records (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  exam_id BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  status ENUM('IN_PROGRESS','SUBMITTED') NOT NULL,
  attempt_no INT NOT NULL DEFAULT 1,
  total_score INT NOT NULL DEFAULT 0,
  started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  submitted_at TIMESTAMP NULL,
  INDEX idx_exam_student (exam_id, student_id)
);

CREATE TABLE IF NOT EXISTS answers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  record_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  answer_text TEXT,
  score INT NOT NULL DEFAULT 0,
  comment_text TEXT,
  UNIQUE KEY uk_record_question (record_id, question_id)
);

INSERT INTO users(username, password, real_name, role, class_name) VALUES
('teacher', '123456', '示例教师', 'TEACHER', NULL),
('student1', '123456', '张三', 'STUDENT', '软件一班'),
('student2', '123456', '李四', 'STUDENT', '软件一班')
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO questions(type, content, option_a, option_b, option_c, option_d, answer, analysis, score) VALUES
('SINGLE', 'Servlet 主要运行在什么容器中？', 'Tomcat', 'MySQL', 'Git', 'Docker Compose', 'A', 'Servlet 通常运行在 Web 容器中，例如 Tomcat。', 5),
('MULTIPLE', '下面哪些属于 HTTP 请求方法？', 'GET', 'POST', 'SELECT', 'PUT', 'A,B,D', 'GET、POST、PUT 是常见 HTTP 方法。', 5),
('JUDGE', 'JDBC 可以用于 Java 程序访问数据库。', NULL, NULL, NULL, NULL, 'T', 'JDBC 是 Java 数据库连接规范。', 5),
('SUBJECTIVE', '简述 Docker Compose 在本项目部署中的作用。', NULL, NULL, NULL, NULL, '用于编排 Tomcat 和 MySQL 两个容器，统一配置网络、环境变量和数据卷。', '围绕容器编排、统一启动、环境配置、数据持久化评分。', 10)
ON DUPLICATE KEY UPDATE content = VALUES(content);

INSERT INTO exams(title, duration_minutes, start_time, end_time, max_attempts, total_score, published, created_by)
VALUES ('Java Web 基础测试', 30, '2026-01-01 00:00:00', '2027-12-31 23:59:59', 3, 100, 1, 1);
INSERT IGNORE INTO exam_questions(exam_id, question_id, sort_no, assigned_score) VALUES (1, 1, 1, 25), (1, 2, 2, 25), (1, 3, 3, 25), (1, 4, 4, 25);
