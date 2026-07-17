-- ============================================================
-- Normalize logic-delete defaults
-- - Fix historical NULL deleted values.
-- - Ensure deleted columns default to 0 for future manual/XML inserts.
-- ============================================================

DROP PROCEDURE IF EXISTS proc_normalize_deleted_defaults_34;

DELIMITER //

CREATE PROCEDURE proc_normalize_deleted_defaults_34()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE tableName VARCHAR(128);

    DECLARE cur CURSOR FOR
        SELECT TABLE_NAME
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND COLUMN_NAME = 'deleted'
          AND TABLE_NAME IN (
              'sys_user',
              'sys_role',
              'sys_permission',
              'sys_tag',
              'sys_knowledge',
              'sys_favorite_folder',
              'sys_favorite_item',
              'sys_blog',
              'sys_blog_image',
              'sys_blog_like',
              'sys_blog_collect',
              'sys_blog_topic_category',
              'sys_blog_topic_relation',
              'sys_comment',
              'sys_comment_like',
              'sys_knowledge_like',
              'sys_user_follow',
              'sys_notification'
          );

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;

    normalize_loop: LOOP
        FETCH cur INTO tableName;
        IF done = 1 THEN
            LEAVE normalize_loop;
        END IF;

        SET @updateSql = CONCAT('UPDATE `', tableName, '` SET `deleted` = 0 WHERE `deleted` IS NULL');
        PREPARE updateStmt FROM @updateSql;
        EXECUTE updateStmt;
        DEALLOCATE PREPARE updateStmt;

        SET @alterSql = CONCAT(
            'ALTER TABLE `', tableName,
            '` MODIFY COLUMN `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT ''逻辑删除：0=正常,1=删除'''
        );
        PREPARE alterStmt FROM @alterSql;
        EXECUTE alterStmt;
        DEALLOCATE PREPARE alterStmt;
    END LOOP;

    CLOSE cur;
END//

DELIMITER ;

CALL proc_normalize_deleted_defaults_34();

DROP PROCEDURE IF EXISTS proc_normalize_deleted_defaults_34;
