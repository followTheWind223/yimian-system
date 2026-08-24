-- Enforce unique user email addresses for email-code registration and password reset.
-- Existing duplicate non-null/non-empty emails must be cleaned before this migration can run.

DROP PROCEDURE IF EXISTS proc_user_email_unique_38;

DELIMITER //

CREATE PROCEDURE proc_user_email_unique_38()
BEGIN
    IF EXISTS (
        SELECT 1
        FROM (
            SELECT email
            FROM sys_user
            WHERE email IS NOT NULL AND email <> ''
            GROUP BY email
            HAVING COUNT(*) > 1
        ) duplicated_email
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Duplicate sys_user.email values exist; cleanup required before adding unique index';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'sys_user'
          AND INDEX_NAME = 'idx_email'
    ) THEN
        ALTER TABLE sys_user DROP INDEX idx_email;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'sys_user'
          AND INDEX_NAME = 'uk_user_email'
    ) THEN
        ALTER TABLE sys_user ADD UNIQUE KEY uk_user_email (email);
    END IF;
END//

DELIMITER ;

CALL proc_user_email_unique_38();

DROP PROCEDURE IF EXISTS proc_user_email_unique_38;
