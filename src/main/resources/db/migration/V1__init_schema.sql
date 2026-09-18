-- =====================================================================
-- V1 : Core schema for "Web hoc tieng Trung"
-- MySQL 8.0 / InnoDB / utf8mb4 (bat buoc de luu Han tu + pinyin co dau)
-- Convention:
--   PK        : BIGINT AUTO_INCREMENT  -> java.lang.Long
--   Timestamp : DATETIME(6) luu theo UTC (hibernate.jdbc.time_zone=UTC)
--   Enum      : VARCHAR(N) + @Enumerated(EnumType.STRING)
--   Boolean   : BIT(1) -> java.lang.Boolean
--   Khoa      : pk_ / uk_ / fk_ / ix_
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. IDENTITY & ACCESS
-- ---------------------------------------------------------------------
CREATE TABLE roles (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE users (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    email             VARCHAR(190) NOT NULL,
    username          VARCHAR(60)  NOT NULL,
    password_hash     VARCHAR(100) NOT NULL,
    display_name      VARCHAR(120) NOT NULL,
    avatar_url        VARCHAR(500) NULL,
    native_language   VARCHAR(10)  NOT NULL DEFAULT 'vi',
    current_hsk_level INT          NOT NULL DEFAULT 1,
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    last_login_at     DATETIME(6)  NULL,
    created_at        DATETIME(6)  NOT NULL,
    updated_at        DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_username (username),
    KEY ix_users_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    KEY ix_user_roles_role (role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Chi luu HASH (SHA-256) cua refresh token, khong bao gio luu token goc
CREATE TABLE refresh_tokens (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    token_hash VARCHAR(64)  NOT NULL,
    expires_at DATETIME(6)  NOT NULL,
    revoked_at DATETIME(6)  NULL,
    user_agent VARCHAR(255) NULL,
    ip_address VARCHAR(45)  NULL,
    created_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_tokens_hash (token_hash),
    KEY ix_refresh_tokens_user (user_id),
    KEY ix_refresh_tokens_expires (expires_at),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- 2. TU VUNG / HSK
-- ---------------------------------------------------------------------
CREATE TABLE topics (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    slug        VARCHAR(120) NOT NULL,
    name_vi     VARCHAR(150) NOT NULL,
    name_zh     VARCHAR(150) NULL,
    description VARCHAR(500) NULL,
    icon        VARCHAR(100) NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_topics_slug (slug)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE words (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    simplified      VARCHAR(60)   NOT NULL,
    traditional     VARCHAR(60)   NULL,
    pinyin          VARCHAR(120)  NOT NULL,
    pinyin_numbered VARCHAR(120)  NULL,
    meaning_vi      VARCHAR(500)  NOT NULL,
    meaning_en      VARCHAR(500)  NULL,
    part_of_speech  VARCHAR(30)   NULL,
    hsk_level       INT           NOT NULL DEFAULT 1,
    stroke_count    INT           NULL,
    frequency_rank  INT           NULL,
    audio_url       VARCHAR(500)  NULL,
    image_url       VARCHAR(500)  NULL,
    note            VARCHAR(1000) NULL,
    created_at      DATETIME(6)   NOT NULL,
    updated_at      DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_words_simplified_pinyin (simplified, pinyin),
    KEY ix_words_hsk_level (hsk_level),
    KEY ix_words_simplified (simplified),
    KEY ix_words_pinyin (pinyin)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE word_examples (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    word_id         BIGINT       NOT NULL,
    sentence_zh     VARCHAR(500) NOT NULL,
    sentence_pinyin VARCHAR(800) NULL,
    sentence_vi     VARCHAR(800) NOT NULL,
    audio_url       VARCHAR(500) NULL,
    sort_order      INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY ix_word_examples_word (word_id),
    CONSTRAINT fk_word_examples_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE word_topics (
    word_id  BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    PRIMARY KEY (word_id, topic_id),
    KEY ix_word_topics_topic (topic_id),
    CONSTRAINT fk_word_topics_word  FOREIGN KEY (word_id)  REFERENCES words (id)  ON DELETE CASCADE,
    CONSTRAINT fk_word_topics_topic FOREIGN KEY (topic_id) REFERENCES topics (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- 3. KHOA HOC / BAI HOC
-- ---------------------------------------------------------------------
CREATE TABLE courses (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    slug              VARCHAR(150) NOT NULL,
    title             VARCHAR(200) NOT NULL,
    description       TEXT         NULL,
    level             VARCHAR(20)  NOT NULL DEFAULT 'HSK1',
    thumbnail_url     VARCHAR(500) NULL,
    published         BIT(1)       NOT NULL DEFAULT b'0',
    sort_order        INT          NOT NULL DEFAULT 0,
    estimated_minutes INT          NULL,
    created_at        DATETIME(6)  NOT NULL,
    updated_at        DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_courses_slug (slug),
    KEY ix_courses_level (level),
    KEY ix_courses_published (published)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE lessons (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    course_id         BIGINT        NOT NULL,
    slug              VARCHAR(150)  NOT NULL,
    title             VARCHAR(200)  NOT NULL,
    summary           VARCHAR(1000) NULL,
    content           LONGTEXT      NULL,
    video_url         VARCHAR(500)  NULL,
    audio_url         VARCHAR(500)  NULL,
    sort_order        INT           NOT NULL DEFAULT 0,
    estimated_minutes INT           NULL,
    published         BIT(1)        NOT NULL DEFAULT b'0',
    created_at        DATETIME(6)   NOT NULL,
    updated_at        DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_lessons_course_slug (course_id, slug),
    KEY ix_lessons_course (course_id),
    CONSTRAINT fk_lessons_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE lesson_words (
    lesson_id  BIGINT NOT NULL,
    word_id    BIGINT NOT NULL,
    sort_order INT    NOT NULL DEFAULT 0,
    PRIMARY KEY (lesson_id, word_id),
    KEY ix_lesson_words_word (word_id),
    CONSTRAINT fk_lesson_words_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE CASCADE,
    CONSTRAINT fk_lesson_words_word   FOREIGN KEY (word_id)   REFERENCES words (id)   ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE lesson_grammar (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    lesson_id   BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    structure   VARCHAR(500) NULL,
    explanation TEXT         NULL,
    example_zh  VARCHAR(500) NULL,
    example_vi  VARCHAR(800) NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY ix_lesson_grammar_lesson (lesson_id),
    CONSTRAINT fk_lesson_grammar_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE enrollments (
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    user_id      BIGINT      NOT NULL,
    course_id    BIGINT      NOT NULL,
    enrolled_at  DATETIME(6) NOT NULL,
    completed_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_enrollments_user_course (user_id, course_id),
    KEY ix_enrollments_course (course_id),
    CONSTRAINT fk_enrollments_user   FOREIGN KEY (user_id)   REFERENCES users (id)   ON DELETE CASCADE,
    CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE user_lesson_progress (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    user_id          BIGINT      NOT NULL,
    lesson_id        BIGINT      NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    progress_percent INT         NOT NULL DEFAULT 0,
    last_viewed_at   DATETIME(6) NULL,
    completed_at     DATETIME(6) NULL,
    created_at       DATETIME(6) NOT NULL,
    updated_at       DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ulp_user_lesson (user_id, lesson_id),
    KEY ix_ulp_lesson (lesson_id),
    CONSTRAINT fk_ulp_user   FOREIGN KEY (user_id)   REFERENCES users (id)   ON DELETE CASCADE,
    CONSTRAINT fk_ulp_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- 4. QUIZ / KIEM TRA
-- ---------------------------------------------------------------------
CREATE TABLE quizzes (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    slug               VARCHAR(150) NOT NULL,
    title              VARCHAR(200) NOT NULL,
    description        TEXT         NULL,
    course_id          BIGINT       NULL,
    lesson_id          BIGINT       NULL,
    hsk_level          INT          NULL,
    time_limit_seconds INT          NULL,
    pass_score         INT          NOT NULL DEFAULT 60,
    published          BIT(1)       NOT NULL DEFAULT b'0',
    created_at         DATETIME(6)  NOT NULL,
    updated_at         DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_quizzes_slug (slug),
    KEY ix_quizzes_course (course_id),
    KEY ix_quizzes_lesson (lesson_id),
    CONSTRAINT fk_quizzes_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE SET NULL,
    CONSTRAINT fk_quizzes_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE questions (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    quiz_id       BIGINT        NOT NULL,
    type          VARCHAR(30)   NOT NULL DEFAULT 'SINGLE_CHOICE',
    prompt        VARCHAR(1000) NOT NULL,
    prompt_pinyin VARCHAR(1000) NULL,
    prompt_vi     VARCHAR(1000) NULL,
    audio_url     VARCHAR(500)  NULL,
    image_url     VARCHAR(500)  NULL,
    correct_text  VARCHAR(500)  NULL,
    explanation   TEXT          NULL,
    points        INT           NOT NULL DEFAULT 1,
    sort_order    INT           NOT NULL DEFAULT 0,
    word_id       BIGINT        NULL,
    PRIMARY KEY (id),
    KEY ix_questions_quiz (quiz_id),
    KEY ix_questions_word (word_id),
    CONSTRAINT fk_questions_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE,
    CONSTRAINT fk_questions_word FOREIGN KEY (word_id) REFERENCES words (id)   ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE question_options (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    question_id BIGINT       NOT NULL,
    content     VARCHAR(500) NOT NULL,
    pinyin      VARCHAR(500) NULL,
    is_correct  BIT(1)       NOT NULL DEFAULT b'0',
    sort_order  INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY ix_question_options_question (question_id),
    CONSTRAINT fk_question_options_question FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE quiz_attempts (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    user_id          BIGINT      NOT NULL,
    quiz_id          BIGINT      NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    started_at       DATETIME(6) NOT NULL,
    submitted_at     DATETIME(6) NULL,
    score            INT         NOT NULL DEFAULT 0,
    max_score        INT         NOT NULL DEFAULT 0,
    correct_count    INT         NOT NULL DEFAULT 0,
    question_count   INT         NOT NULL DEFAULT 0,
    passed           BIT(1)      NOT NULL DEFAULT b'0',
    duration_seconds INT         NULL,
    PRIMARY KEY (id),
    KEY ix_quiz_attempts_user (user_id),
    KEY ix_quiz_attempts_quiz (quiz_id),
    KEY ix_quiz_attempts_user_quiz (user_id, quiz_id),
    CONSTRAINT fk_quiz_attempts_user FOREIGN KEY (user_id) REFERENCES users (id)   ON DELETE CASCADE,
    CONSTRAINT fk_quiz_attempts_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE quiz_attempt_answers (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    attempt_id         BIGINT       NOT NULL,
    question_id        BIGINT       NOT NULL,
    selected_option_id BIGINT       NULL,
    text_answer        VARCHAR(500) NULL,
    is_correct         BIT(1)       NOT NULL DEFAULT b'0',
    points_awarded     INT          NOT NULL DEFAULT 0,
    answered_at        DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_qaa_attempt_question (attempt_id, question_id),
    KEY ix_qaa_question (question_id),
    KEY ix_qaa_option (selected_option_id),
    CONSTRAINT fk_qaa_attempt  FOREIGN KEY (attempt_id)         REFERENCES quiz_attempts (id)    ON DELETE CASCADE,
    CONSTRAINT fk_qaa_question FOREIGN KEY (question_id)        REFERENCES questions (id)        ON DELETE CASCADE,
    CONSTRAINT fk_qaa_option   FOREIGN KEY (selected_option_id) REFERENCES question_options (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- 5. FLASHCARD + SRS (thuat toan SM-2)
-- ---------------------------------------------------------------------
CREATE TABLE decks (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    owner_id    BIGINT        NULL,
    topic_id    BIGINT        NULL,
    slug        VARCHAR(150)  NULL,
    name        VARCHAR(200)  NOT NULL,
    description VARCHAR(1000) NULL,
    hsk_level   INT           NULL,
    is_public   BIT(1)        NOT NULL DEFAULT b'0',
    is_system   BIT(1)        NOT NULL DEFAULT b'0',
    card_count  INT           NOT NULL DEFAULT 0,
    created_at  DATETIME(6)   NOT NULL,
    updated_at  DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_decks_slug (slug),
    KEY ix_decks_owner (owner_id),
    KEY ix_decks_topic (topic_id),
    CONSTRAINT fk_decks_owner FOREIGN KEY (owner_id) REFERENCES users (id)  ON DELETE CASCADE,
    CONSTRAINT fk_decks_topic FOREIGN KEY (topic_id) REFERENCES topics (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE flashcards (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    deck_id    BIGINT        NOT NULL,
    word_id    BIGINT        NULL,
    front      VARCHAR(500)  NOT NULL,
    back       VARCHAR(1000) NOT NULL,
    hint       VARCHAR(500)  NULL,
    audio_url  VARCHAR(500)  NULL,
    image_url  VARCHAR(500)  NULL,
    sort_order INT           NOT NULL DEFAULT 0,
    created_at DATETIME(6)   NOT NULL,
    updated_at DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    KEY ix_flashcards_deck (deck_id),
    KEY ix_flashcards_word (word_id),
    CONSTRAINT fk_flashcards_deck FOREIGN KEY (deck_id) REFERENCES decks (id) ON DELETE CASCADE,
    CONSTRAINT fk_flashcards_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Trang thai on tap cua tung user tren tung the (SM-2)
CREATE TABLE review_states (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    user_id          BIGINT      NOT NULL,
    flashcard_id     BIGINT      NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'NEW',
    ease_factor      DOUBLE      NOT NULL DEFAULT 2.5,
    interval_days    INT         NOT NULL DEFAULT 0,
    repetitions      INT         NOT NULL DEFAULT 0,
    lapses           INT         NOT NULL DEFAULT 0,
    due_at           DATETIME(6) NOT NULL,
    last_reviewed_at DATETIME(6) NULL,
    created_at       DATETIME(6) NOT NULL,
    updated_at       DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_review_states_user_card (user_id, flashcard_id),
    KEY ix_review_states_due (user_id, due_at),
    KEY ix_review_states_card (flashcard_id),
    CONSTRAINT fk_review_states_user FOREIGN KEY (user_id)      REFERENCES users (id)      ON DELETE CASCADE,
    CONSTRAINT fk_review_states_card FOREIGN KEY (flashcard_id) REFERENCES flashcards (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE review_logs (
    id                BIGINT      NOT NULL AUTO_INCREMENT,
    user_id           BIGINT      NOT NULL,
    flashcard_id      BIGINT      NOT NULL,
    rating            VARCHAR(20) NOT NULL,
    previous_interval INT         NOT NULL DEFAULT 0,
    new_interval      INT         NOT NULL DEFAULT 0,
    ease_factor_after DOUBLE      NOT NULL DEFAULT 2.5,
    reviewed_at       DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY ix_review_logs_user_time (user_id, reviewed_at),
    KEY ix_review_logs_card (flashcard_id),
    CONSTRAINT fk_review_logs_user FOREIGN KEY (user_id)      REFERENCES users (id)      ON DELETE CASCADE,
    CONSTRAINT fk_review_logs_card FOREIGN KEY (flashcard_id) REFERENCES flashcards (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
