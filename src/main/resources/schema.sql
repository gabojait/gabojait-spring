DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS offer;
DROP TABLE IF EXISTS favorite;
DROP TABLE IF EXISTS team_member;
DROP TABLE IF EXISTS team;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS fcm;
DROP TABLE IF EXISTS work;
DROP TABLE IF EXISTS skill;
DROP TABLE IF EXISTS portfolio;
DROP TABLE IF EXISTS education;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS suspend;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS block;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS contact;

CREATE TABLE contact
(
    contact_id                  INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email                       VARCHAR(255) NOT NULL,
    verification_code            VARCHAR(6)   NOT NULL,
    is_verified                  BIT          NOT NULL,
    created_at                  DATETIME(6)  NOT NULL,
    updated_at                  DATETIME(6)  NOT NULL,
    CONSTRAINT uq_contact_email UNIQUE (email)
);

CREATE TABLE users
(
    user_id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username                    VARCHAR(15)             NOT NULL,
    password                    VARCHAR(255)            NOT NULL,
    gender                      VARCHAR(1)              NOT NULL,
    image_url                   VARCHAR(255),
    nickname                    VARCHAR(8)              NOT NULL,
    position                    VARCHAR(20)             NOT NULL,
    profile_description          VARCHAR(120),
    visited_cnt                 INT UNSIGNED            NOT NULL,
    review_cnt                  INT UNSIGNED            NOT NULL,
    rating                      FLOAT                   NOT NULL,
    birthdate                   DATE,
    is_notified                  BIT                     NOT NULL,
    is_seeking_team             BIT                     NOT NULL,
    is_temporary_password       BIT                     NOT NULL,
    last_request_at             DATETIME(6)             NOT NULL,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    contact_id                  INT UNSIGNED         NOT NULL,
    CONSTRAINT uq_user_username UNIQUE (username),
    CONSTRAINT uq_user_nickname UNIQUE (nickname),
    CONSTRAINT fk_user_contact FOREIGN KEY (contact_id) REFERENCES contact (contact_id)
);

CREATE TABLE report
(
    report_id                   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    reason                      VARCHAR(100)            NOT NULL,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    user_id                     INT UNSIGNED            NOT NULL,
    reporter_id                 INT UNSIGNED            NOT NULL,
    CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES users (user_id)
);

CREATE TABLE suspend
(
    suspend_id                  INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    reason                      VARCHAR(100),
    start_at                    DATETIME                NOT NULL,
    end_at                      DATETIME                NOT NULL,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    user_id                     INT UNSIGNED            NOT NULL,
    admin_id                    INT UNSIGNED            NOT NULL,
    report_id                   INT UNSIGNED,
    CONSTRAINT fk_suspend_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_suspend_admin FOREIGN KEY (admin_id) REFERENCES users (user_id),
    CONSTRAINT fk_suspend_report FOREIGN KEY (report_id) REFERENCES report (report_id)
);

CREATE TABLE block
(
    block_id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    start_at                    DATETIME                NOT NULL,
    end_at                      DATETIME                NOT NULL,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    user_id                     INT UNSIGNED            NOT NULL,
    blocker_id                  INT UNSIGNED            NOT NULL,
    CONSTRAINT fk_block_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_block_blocker FOREIGN KEY (blocker_id) REFERENCES users (user_id)
);

CREATE TABLE user_role
(
    user_role_id                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    role                        VARCHAR(6)              NOT NULL,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    user_id                     INT UNSIGNED            NOT NULL,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE education
(
    education_id                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    institution_name            VARCHAR(20)             NOT NULL,
    is_current                  BIT                     NOT NULL,
    started_at                  DATE                    NOT NULL,
    ended_at                    DATE,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    user_id                     INT UNSIGNED            NOT NULL,
    CONSTRAINT fk_education_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE portfolio
(
    portfolio_id                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    portfolio_name              VARCHAR(10)             NOT NULL,
    media                       VARCHAR(4)              NOT NULL,
    portfolio_url               VARCHAR(1000)           NOT NULL,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    user_id                     INT UNSIGNED            NOT NULL,
    CONSTRAINT fk_portfolio_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE skill
(
    skill_id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    skill_name                  VARCHAR(20)             NOT NULL,
    level                       VARCHAR(4)              NOT NULL,
    is_experienced              BIT                     NOT NULL,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    user_id                     INT UNSIGNED            NOT NULL,
    CONSTRAINT fk_skill_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE work
(
    work_id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    corporation_name            VARCHAR(20)             NOT NULL,
    work_description            VARCHAR(100),
    is_current                  BIT                     NOT NULL,
    started_at                  DATE                    NOT NULL,
    ended_at                    DATE,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    user_id                     INT UNSIGNED            NOT NULL,
    CONSTRAINT fk_work_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE fcm
(
    fcm_id                      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    fcm_token                   VARCHAR(255)            NOT NULL,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    user_id                     INT UNSIGNED,
    CONSTRAINT fk_fcm_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE notification
(
    notification_id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title                       VARCHAR(255)            NOT NULL,
    body                        VARCHAR(255)            NOT NULL,
    deep_link_type              VARCHAR(31)             NOT NULL,
    is_read                     BIT                     NOT NULL,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    is_deleted                  BIT                     NOT NULL,
    user_id                     INT UNSIGNED,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE team
(
    team_id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_name                VARCHAR(20)             NOT NULL,
    project_url                 VARCHAR(255),
    open_chat_url               VARCHAR(100)            NOT NULL,
    project_description         VARCHAR(500)            NOT NULL,
    expectation                 VARCHAR(200)            NOT NULL,
    backend_current_cnt         TINYINT                 NOT NULL,
    backend_max_cnt             TINYINT                 NOT NULL,
    designer_current_cnt        TINYINT                 NOT NULL,
    designer_max_cnt            TINYINT                 NOT NULL,
    frontend_current_cnt        TINYINT                 NOT NULL,
    frontend_max_cnt            TINYINT                 NOT NULL,
    manager_current_cnt         TINYINT                 NOT NULL,
    manager_max_cnt             TINYINT                 NOT NULL,
    visited_cnt                 BIGINT                  NOT NULL,
    is_recruiting               BIT                     NOT NULL,
    completed_at                DATETIME(6),
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    is_deleted                  BIT                     NOT NULL
);


CREATE TABLE team_member
(
    team_member_id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    position                    VARCHAR(20)             NOT NULL,
    team_member_status          VARCHAR(10)             NOT NULL,
    is_leader                   BIT,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    is_deleted                  BIT                     NOT NULL,
    team_id                     INT UNSIGNED         NOT NULL,
    user_id                     INT UNSIGNED         NOT NULL,
    CONSTRAINT fk_team_member_team FOREIGN KEY (team_id) REFERENCES team (team_id),
    CONSTRAINT fk_team_member_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE favorite
(
    favorite_id                 INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    favorite_team_id            INT UNSIGNED,
    favorite_user_id            INT UNSIGNED,
    user_id                     INT UNSIGNED            NOT NULL,
    CONSTRAINT fk_favorite_favorite_team FOREIGN KEY (favorite_team_id) REFERENCES team (team_id),
    CONSTRAINT fk_favorite_favorite_user FOREIGN KEY (favorite_user_id) REFERENCES users (user_id),
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE offer
(
    offer_id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    is_deleted                  BIT                     NOT NULL,
    is_accepted                 BIT,
    offered_by                  VARCHAR(6)              NOT NULL,
    position                    VARCHAR(20)             NOT NULL,
    team_id                     INT UNSIGNED            NOT NULL,
    user_id                     INT UNSIGNED            NOT NULL,
    CONSTRAINT fk_offer_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_offer_team FOREIGN KEY (team_id) REFERENCES team (team_id)
);


CREATE TABLE review
(
    review_id                   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post                        VARCHAR(200)            NOT NULL,
    rating                      TINYINT                 NOT NULL,
    created_at                  DATETIME(6)             NOT NULL,
    updated_at                  DATETIME(6)             NOT NULL,
    is_deleted                  BIT                     NOT NULL,
    reviewee_id                 INT UNSIGNED            NOT NULL,
    reviewer_id                 INT UNSIGNED            NOT NULL,
    CONSTRAINT fk_review_reviewee FOREIGN KEY (reviewee_id) REFERENCES team_member (team_member_id),
    CONSTRAINT fk_review_reviewer FOREIGN KEY (reviewer_id) REFERENCES team_member (team_member_id)
);