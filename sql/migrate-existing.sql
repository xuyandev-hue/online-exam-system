SET @switch_limit_exists = (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'exams'
    AND column_name = 'switch_limit'
);
SET @switch_limit_ddl = IF(
  @switch_limit_exists = 0,
  'ALTER TABLE exams ADD COLUMN switch_limit INT NOT NULL DEFAULT 2 AFTER max_attempts',
  'SELECT 1'
);
PREPARE switch_limit_statement FROM @switch_limit_ddl;
EXECUTE switch_limit_statement;
DEALLOCATE PREPARE switch_limit_statement;

UPDATE exams SET switch_limit = 2 WHERE switch_limit IS NULL OR switch_limit < 1;

INSERT INTO users(username, password, real_name, role, class_name)
VALUES ('student3', '123456', '王五', 'STUDENT', '软件一班')
ON DUPLICATE KEY UPDATE
  password = VALUES(password),
  real_name = VALUES(real_name),
  role = VALUES(role),
  class_name = VALUES(class_name);
