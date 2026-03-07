CREATE TABLE dbo.app_user (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    username NVARCHAR(100) NOT NULL,
    password_hash NVARCHAR(512) NOT NULL,
    email NVARCHAR(255) NOT NULL,
    enabled BIT NOT NULL CONSTRAINT DF_app_user_enabled DEFAULT 1,
    created_at DATETIME2 NOT NULL CONSTRAINT DF_app_user_created_at DEFAULT SYSUTCDATETIME(),
    CONSTRAINT UQ_app_user_username UNIQUE (username),
    CONSTRAINT UQ_app_user_email UNIQUE (email)
);

CREATE TABLE dbo.app_role (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    role_name NVARCHAR(50) NOT NULL,
    CONSTRAINT UQ_app_role_role_name UNIQUE (role_name)
);

CREATE TABLE dbo.app_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT PK_app_user_role PRIMARY KEY (user_id, role_id),
    CONSTRAINT FK_app_user_role_user FOREIGN KEY (user_id) REFERENCES dbo.app_user(id),
    CONSTRAINT FK_app_user_role_role FOREIGN KEY (role_id) REFERENCES dbo.app_role(id)
);

CREATE TABLE dbo.patient (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    patient_number NVARCHAR(50) NOT NULL,
    first_name NVARCHAR(100) NOT NULL,
    last_name NVARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME2 NOT NULL CONSTRAINT DF_patient_created_at DEFAULT SYSUTCDATETIME(),
    CONSTRAINT UQ_patient_patient_number UNIQUE (patient_number),
    CONSTRAINT UQ_patient_user_id UNIQUE (user_id),
    CONSTRAINT FK_patient_user FOREIGN KEY (user_id) REFERENCES dbo.app_user(id)
);

CREATE TABLE dbo.inbox_submission (
    submission_id       UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    source_system       NVARCHAR(50)  NOT NULL,
    subject_id          NVARCHAR(100) NULL,
    payload_format      NVARCHAR(20)  NOT NULL,
    payload_json        NVARCHAR(MAX) NULL,
    content_hash        CHAR(64)      NULL,
    received_at_utc     DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE dbo.audit_log (
    audit_id        BIGINT IDENTITY(1,1) PRIMARY KEY,
    event_type      NVARCHAR(50) NOT NULL,
    submission_id   UNIQUEIDENTIFIER NULL,
    message         NVARCHAR(4000) NULL,
    created_at_utc  DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);
