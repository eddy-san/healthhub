CREATE OR ALTER PROCEDURE dbo.ingest_submission
    @submission_id   UNIQUEIDENTIFIER,
    @source_system   NVARCHAR(50),
    @subject_id      NVARCHAR(100),
    @payload_format  NVARCHAR(20),
    @payload_json    NVARCHAR(MAX),
    @content_hash    CHAR(64)
    AS
BEGIN
    SET NOCOUNT ON;

    -- Idempotent: retries with same submission_id must not create duplicates
    IF EXISTS (SELECT 1 FROM dbo.inbox_submission WHERE submission_id = @submission_id)
BEGIN
INSERT INTO dbo.audit_log (event_type, submission_id, message)
VALUES (N'INGEST_DUP', @submission_id, N'Duplicate submission_id ignored');
RETURN;
END

INSERT INTO dbo.inbox_submission
(submission_id, source_system, subject_id, payload_format, payload_json, content_hash)
VALUES
    (@submission_id, @source_system, @subject_id, @payload_format, @payload_json, @content_hash);

INSERT INTO dbo.audit_log (event_type, submission_id, message)
VALUES (N'INGEST_OK', @submission_id, N'Submission stored');
END;
