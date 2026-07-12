# 在线考试与自动评阅系统

面向课程教学的 Java Web 实训项目，覆盖教师出题、组卷发布、学生在线答题、倒计时自动提交、客观题自动判分、主观题 AI 辅助评阅、成绩统计和 CSV 导出。

## 技术栈

- 后端：Servlet、JDBC、Java 17
- 前端：JSP、原生 JavaScript、CSS
- 数据库：MySQL 8
- 部署：Docker Compose、Tomcat 9、MySQL 容器
- AI：`HttpURLConnection` 调用大语言模型 API，默认启用 Mock 模式

## 目录结构

```text
src/main/java/com/training/exam
  dao       JDBC 数据访问
  model     实体类
  service   判分与 AI 评阅
  servlet   Web 控制器
  util      数据库和 Web 工具
src/main/webapp
  WEB-INF/jsp  JSP 页面
  static        CSS 和 JS
sql/init.sql    建库建表和初始化数据
Dockerfile      构建 war 并部署到 Tomcat
docker-compose.yml
```

## 默认账号

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 教师 | `teacher` | `123456` |
| 学生 | `student1` | `123456` |
| 学生 | `student2` | `123456` |

## 本地或 ECS 运行

服务器安装 Docker 和 Docker Compose 后，在项目根目录执行：

```bash
docker compose up -d --build
```

访问：

```text
http://服务器IP:8080
```

查看日志：

```bash
docker logs -f exam-web
docker logs -f exam-mysql
```

停止服务：

```bash
docker compose down
```

保留数据库数据时不要删除 volume。需要彻底清空数据时执行：

```bash
docker compose down -v
```

## 环境变量

`docker-compose.yml` 已设置默认值，也可以在 `.env` 中覆盖：

```env
MYSQL_ROOT_PASSWORD=root123456
DB_NAME=online_exam
DB_USER=exam_user
DB_PASSWORD=exam_pass
AI_MOCK=true
AI_API_URL=
AI_API_KEY=
```

默认 `AI_MOCK=true`，系统会用本地模拟评分保证演示流程完整。如果要接入真实大模型 API，设置 `AI_MOCK=false`，并填写 `AI_API_URL` 和 `AI_API_KEY`。

## Maven 打包

如果本机安装了 Maven，可以执行：

```bash
mvn clean package
```

生成：

```text
target/online-exam-system.war
```

## Git 分支建议

- `main`：稳定版本
- `develop`：集成测试
- `feature/user`：登录、权限、用户相关功能
- `feature/exam`：题库、组卷、考试、成绩
- `feature/ai-correct`：AI 主观题评阅

功能完成后合并到 `develop`，经测试和代码评审后再合并到 `main`。
