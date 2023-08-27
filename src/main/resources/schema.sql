DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS fcm;
DROP TABLE IF EXISTS education;
DROP TABLE IF EXISTS portfolio;
DROP TABLE IF EXISTS skill;
DROP TABLE IF EXISTS work;
DROP TABLE IF EXISTS team_member;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS offer;
DROP TABLE IF EXISTS favorite_user;
DROP TABLE IF EXISTS favorite_team;
DROP TABLE IF EXISTS admin;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS team;
DROP TABLE IF EXISTS contact;

CREATE TABLE contact
(
    contact_id              BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NOT NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME(6)     NOT NULL,
    email                   VARCHAR(255)    NOT NULL,
    is_verified             BIT             NOT NULL,
    verification_code       VARCHAR(255)    NOT NULL,
    PRIMARY KEY (contact_id)
);

CREATE TABLE users
(
    user_id                 BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NOT NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME(6)     NOT NULL,
    birthdate               DATETIME(6)     NULL,
    gender                  VARCHAR(1)      NOT NULL,
    image_url               VARCHAR(255)    NULL,
    is_notified             BIT             NOT NULL,
    is_seeking_team         BIT             NOT NULL,
    is_temporary_password   BIT             NOT NULL,
    last_request_at         DATETIME(6)     NOT NULL,
    nickname                VARCHAR(6)      NOT NULL,
    password                VARCHAR(255)    NOT NULL,
    position                VARCHAR(20)     NOT NULL,
    profile_description     VARCHAR(120)    NULL,
    rating                  FLOAT           NOT NULL,
    review_cnt              INT             NOT NULL,
    username                VARCHAR(15)     NOT NULL,
    visited_cnt             BIGINT          NOT NULL,
    contact_id              BIGINT          NOT NULL,
    PRIMARY KEY (user_id),
    FOREIGN KEY (contact_id) REFERENCES contact(contact_id)
);

CREATE TABLE admin
(
    admin_id                BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NOT NULL,
    is_deleted              BIT             NOT NULL,
    is_approved             BIT             NULL,
    updated_at              DATETIME(6)     NOT NULL,
    last_request_at         DATETIME(6)     NOT NULL,
    legal_name              VARCHAR(5)      NOT NULL,
    birthdate               DATETIME(6)     NOT NULL,
    password                VARCHAR(255)    NOT NULL,
    username                VARCHAR(15)     NOT NULL,
    PRIMARY KEY (admin_id)
);

CREATE TABLE user_role
(
    user_role_id            BIGINT          AUTO_INCREMENT,
    role                    VARCHAR(6)      NOT NULL,
    user_id                 BIGINT          NULL,
    admin_id                BIGINT          NULL,
    PRIMARY KEY (user_role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (admin_id) REFERENCES admin(admin_id)
);

CREATE TABLE fcm
(
    fcm_id                  BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NOT NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME(6)     NOT NULL,
    fcm_token               VARCHAR(255)    NOT NULL,
    user_id                 BIGINT          NOT NULL,
    PRIMARY KEY (fcm_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE education
(
    education_id            BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME(6)     NOT NULL,
    ended_at                DATE            NULL,
    institution_name        VARCHAR(20)     NOT NULL,
    is_current              BIT             NOT NULL,
    started_at              DATE            NOT NULL,
    user_id                 BIGINT          NOT NULL,
    PRIMARY KEY (education_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE portfolio
(
    portfolio_id            BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME        NOT NULL,
    media                   VARCHAR(4)      NOT NULL,
    portfolio_name          VARCHAR(10)     NOT NULL,
    portfolio_url           VARCHAR(1000)   NOT NULL,
    user_id                 BIGINT          NOT NULL,
    PRIMARY KEY (portfolio_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);


CREATE TABLE skill
(
    skill_id                BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NOT NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME(6)     NOT NULL,
    is_experienced          BIT             NOT NULL,
    level                   VARCHAR(4)      NOT NULL,
    skill_name              VARCHAR(20)     NOT NULL,
    user_id                 BIGINT          NOT NULL,
    PRIMARY KEY (skill_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE work
(
    work_id                 BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NOT NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME(6)     NOT NULL,
    corporation_name        VARCHAR(20)     NOT NULL,
    ended_at                DATE            NULL,
    is_current              BIT             NOT NULL,
    started_at              DATE            NOT NULL,
    work_description        VARCHAR(100)    NULL,
    user_id                 BIGINT          NOT NULL,
    PRIMARY KEY (work_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE team
(
    team_id                 BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NOT NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME(6)     NOT NULL,
    backend_cnt             TINYINT         NOT NULL,
    completed_at            DATETIME(6)     NULL,
    designer_cnt            TINYINT         NOT NULL,
    expectation             VARCHAR(200)    NOT NULL,
    frontend_cnt            TINYINT         NOT NULL,
    is_backend_full         BIT             NOT NULL,
    is_designer_full        BIT             NOT NULL,
    is_frontend_full        BIT             NOT NULL,
    is_manager_full         BIT             NOT NULL,
    is_recruiting           BIT             NOT NULL,
    manager_cnt             TINYINT         NOT NULL,
    open_chat_url           VARCHAR(100)    NOT NULL,
    project_description     VARCHAR(500)    NOT NULL,
    project_name            VARCHAR(20)     NOT NULL,
    project_url             VARCHAR(255)    NULL,
    visited_cnt             BIGINT          NOT NULL,
    PRIMARY KEY (team_id)
);

CREATE TABLE team_member
(
    team_member_id          BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NOT NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME(6)     NOT NULL,
    is_leader               BIT             NOT NULL,
    position                VARCHAR(20)     NOT NULL,
    team_id                 BIGINT          NOT NULL,
    user_id                 BIGINT          NOT NULL,
    PRIMARY KEY (team_member_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (team_id) REFERENCES team(team_id)
);

CREATE TABLE review
(
    review_id               BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NOT NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME(6)     NOT NULL,
    post                    VARCHAR(200)    NOT NULL,
    rate                    TINYINT         NOT NULL,
    reviewee_id             BIGINT          NOT NULL,
    reviewer_id             BIGINT          NOT NULL,
    team_id                 BIGINT          NOT NULL,
    PRIMARY KEY (review_id),
    FOREIGN KEY (reviewee_id) REFERENCES users(user_id),
    FOREIGN KEY (reviewer_id) REFERENCES users(user_id),
    FOREIGN KEY (team_id) REFERENCES team(team_id)
);

CREATE TABLE offer
(
    offer_id                BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NOT NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME(6)     NOT NULL,
    is_accepted             BIT             NULL,
    offered_by              VARCHAR(4)      NOT NULL,
    position                VARCHAR(20)     NOT NULL,
    team_id                 BIGINT          NOT NULL,
    user_id                 BIGINT          NOT NULL,
    PRIMARY KEY (offer_id),
    FOREIGN KEY (team_id) REFERENCES team(team_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE favorite_user
(
    favorite_user_id        BIGINT          AUTO_INCREMENT,
    created_at              DATETIME(6)     NOT NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME(6)     NOT NULL,
    team_id                 BIGINT          NOT NULL,
    user_id                 BIGINT          NOT NULL,
    PRIMARY KEY (favorite_user_id),
    FOREIGN KEY (team_id) REFERENCES team(team_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE favorite_team
(
    favorite_team_id        BIGINT          AUTO_INCREMENT,
    created_at              DATETIME        NOT NULL,
    is_deleted              BIT             NOT NULL,
    updated_at              DATETIME(6)     NOT NULL,
    team_id                 BIGINT          NOT NULL,
    user_id                 BIGINT          NOT NULL,
    PRIMARY KEY (favorite_team_id),
    FOREIGN KEY (team_id) REFERENCES team(team_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
)