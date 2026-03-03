CREATE TABLE dbo.inbox_submission (
                                      submission_id       UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
                                      source_system       NVARCHAR(50)  NOT NULL,        -- e.g. 'epro', 'garmin'
                                      subject_id          NVARCHAR(100) NULL,            -- patient pseudo id or device/user id
                                      payload_format      NVARCHAR(20)  NOT NULL,        -- 'json', 'text', ...
                                      payload_json        NVARCHAR(MAX) NULL,
                                      content_hash        CHAR(64)      NULL,
                                      received_at_utc     DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME()
);



CREATE TABLE dbo.audit_log (
                               audit_id        BIGINT IDENTITY(1,1) PRIMARY KEY,
                               event_type      NVARCHAR(50) NOT NULL,             -- e.g. 'INGEST_OK', 'INGEST_DUP'
                               submission_id   UNIQUEIDENTIFIER NULL,
                               message         NVARCHAR(4000) NULL,
                               created_at_utc  DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

