-- 用户、博客、收藏夹主键改为应用侧雪花 ID。
-- 注意：历史脚本禁止修改，本脚本仅移除现有表的 AUTO_INCREMENT 属性；
-- 新增记录由 MyBatis-Plus IdType.ASSIGN_ID 生成 64 位雪花 ID。

ALTER TABLE sys_user
    MODIFY COLUMN id BIGINT NOT NULL COMMENT '主键（雪花ID）';

ALTER TABLE sys_blog
    MODIFY COLUMN id BIGINT NOT NULL COMMENT '主键（雪花ID）';

ALTER TABLE sys_favorite_folder
    MODIFY COLUMN id BIGINT NOT NULL COMMENT '主键（雪花ID）';
