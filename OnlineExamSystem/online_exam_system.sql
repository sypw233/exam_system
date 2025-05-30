-- 创建数据库
CREATE DATABASE online_exam_system CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE online_exam_system;

-- 用户表
CREATE TABLE users
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)                          NOT NULL UNIQUE,
    password    VARCHAR(255)                         NOT NULL,
    role        ENUM ('admin', 'teacher', 'student') NOT NULL,
    email       VARCHAR(100) UNIQUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 试题表
CREATE TABLE questions
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    content     TEXT                                                                    NOT NULL,
    type        ENUM ('single', 'multiple', 'true_false', 'fill_blank', 'short_answer') NOT NULL,
    options     JSON,
    answer      TEXT                                                                    NOT NULL,
    difficulty  ENUM ('easy', 'medium', 'hard')                                         NOT NULL,
    category    VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    creator_id  BIGINT                                                                  NOT NULL,
    FOREIGN KEY (creator_id) REFERENCES users (id) ON DELETE CASCADE
);

-- 考试表
CREATE TABLE exams
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    description TEXT,
    start_time  DATETIME     NOT NULL,
    end_time    DATETIME     NOT NULL,
    creator_id  BIGINT       NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users (id) ON DELETE CASCADE
);

-- 考试试题关联表
CREATE TABLE exam_questions
(
    exam_id     BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    score       INT    NOT NULL,
    PRIMARY KEY (exam_id, question_id),
    FOREIGN KEY (exam_id) REFERENCES exams (id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE
);

-- 答题记录表
CREATE TABLE exam_submissions
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id     BIGINT NOT NULL,
    student_id  BIGINT NOT NULL,
    submit_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_score INT,
    FOREIGN KEY (exam_id) REFERENCES exams (id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users (id) ON DELETE CASCADE
);

-- 答题详情表
CREATE TABLE submission_details
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id BIGINT NOT NULL,
    question_id   BIGINT NOT NULL,
    answer        TEXT,
    score         INT,
    FOREIGN KEY (submission_id) REFERENCES exam_submissions (id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE
);
