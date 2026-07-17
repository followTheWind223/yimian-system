-- MySQL dump 10.13  Distrib 8.3.0, for Win64 (x86_64)
--
-- Host: localhost    Database: yimian
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `sys_blog`
--

DROP TABLE IF EXISTS `sys_blog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_blog` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '??',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '?? Markdown',
  `summary` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '??',
  `author_id` bigint NOT NULL COMMENT '??ID',
  `status` tinyint DEFAULT '1' COMMENT '??: 0=??,1=???,2=???,3=???',
  `is_pinned` tinyint DEFAULT '0' COMMENT '????',
  `view_count` int DEFAULT '0' COMMENT '????',
  `like_count` int DEFAULT '0' COMMENT '???',
  `comment_count` int DEFAULT '0' COMMENT '???',
  `ref_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '????: knowledge/folder/null',
  `ref_id` bigint DEFAULT NULL COMMENT '??ID',
  `topic_ids` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联话题ID列表，逗号分隔（冗余，便于列表直接展示）',
  `published_at` datetime DEFAULT NULL COMMENT '????',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  `deleted` tinyint DEFAULT '0' COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_author_deleted` (`author_id`,`deleted`),
  KEY `idx_status_deleted` (`status`,`deleted`),
  KEY `idx_ref` (`ref_type`,`ref_id`),
  KEY `idx_published_at` (`published_at`),
  KEY `idx_hot` (`is_pinned`,`view_count`,`like_count`,`comment_count`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='?????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_blog`
--

LOCK TABLES `sys_blog` WRITE;
/*!40000 ALTER TABLE `sys_blog` DISABLE KEYS */;
INSERT INTO `sys_blog` VALUES (3,'测试12121','121231312十大大苏打实打实的十大大苏打萨达萨达飒飒大苏打是阿萨 大撒大撒的撒的撒的撒的撒大大打死大飒飒仨仨的撒仨仨的撒旦仨阿萨a\'s','121231312十大大苏打实打实的十大大苏打萨达萨达飒飒大苏打是阿萨 大撒大撒的撒的撒的撒的撒大大打死大飒飒仨仨的撒仨仨的撒旦仨阿萨a\'s',10,1,0,31,1,0,'knowledge',9,NULL,'2026-07-15 21:37:34','2026-07-15 21:37:34','2026-07-16 15:05:36',0),(4,'3131231','123123131231我3123123123123123123312312','123123131231我3123123123123123123312312',2,1,0,1,0,0,NULL,NULL,NULL,'2026-07-16 16:35:00','2026-07-16 16:35:00','2026-07-16 16:35:00',0),(5,'2121212121','121212121212221212112121','121212121212221212112121',10,1,0,2,0,0,NULL,NULL,NULL,'2026-07-16 19:41:13','2026-07-16 19:41:13','2026-07-16 19:53:50',0),(6,'测试擦撒大大实打实a','大苏打萨达萨达萨达萨达大撒大撒大撒撒打算的撒的撒萨达阿萨','大苏打萨达萨达萨达萨达大撒大撒大撒撒打算的撒的撒萨达阿萨',2,1,0,3,0,0,NULL,NULL,NULL,'2026-07-16 19:54:24','2026-07-16 19:54:23','2026-07-16 19:59:00',0);
/*!40000 ALTER TABLE `sys_blog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_blog_collect`
--

DROP TABLE IF EXISTS `sys_blog_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_blog_collect` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `blog_id` bigint NOT NULL COMMENT '博客ID',
  `folder_id` bigint NOT NULL COMMENT '收藏夹ID',
  `user_id` bigint NOT NULL COMMENT '收藏用户ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_blog_id` (`blog_id`),
  KEY `idx_folder_id` (`folder_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_blog_user_deleted` (`blog_id`,`user_id`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客收藏记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_blog_collect`
--

LOCK TABLES `sys_blog_collect` WRITE;
/*!40000 ALTER TABLE `sys_blog_collect` DISABLE KEYS */;
INSERT INTO `sys_blog_collect` VALUES (1,3,1,10,'2026-07-15 22:41:16',0);
/*!40000 ALTER TABLE `sys_blog_collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_blog_image`
--

DROP TABLE IF EXISTS `sys_blog_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_blog_image` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `blog_id` bigint NOT NULL COMMENT '所属博客ID',
  `url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片访问URL',
  `sort` int DEFAULT '0' COMMENT '排序号（1~9）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_blog_id` (`blog_id`),
  KEY `idx_blog_deleted` (`blog_id`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_blog_image`
--

LOCK TABLES `sys_blog_image` WRITE;
/*!40000 ALTER TABLE `sys_blog_image` DISABLE KEYS */;
INSERT INTO `sys_blog_image` VALUES (1,3,'/api/file/blog/2026/07/15/3ceb07ed5e834c69ac627014e4312917.png',1,'2026-07-15 21:37:34',1),(2,3,'/api/file/blog/2026/07/15/3cdc5cf8b1894da2bbe252c388c6462a.jpg',2,'2026-07-15 21:37:34',1),(3,3,'/api/file/blog/2026/07/15/3ceb07ed5e834c69ac627014e4312917.png',1,'2026-07-15 21:52:26',0),(4,3,'/api/file/blog/2026/07/15/3cdc5cf8b1894da2bbe252c388c6462a.jpg',2,'2026-07-15 21:52:26',0),(5,5,'/api/file/blog/2026/07/16/ac71468b12904b9c9a7a5a263e418913.png',1,'2026-07-16 19:41:13',0),(6,6,'/api/file/blog/2026/07/16/93cb2ca4af9646aa8699cc5b1a8b8c05.png',1,'2026-07-16 19:54:23',1),(7,6,'/api/file/blog/2026/07/16/43370a5e042340a884d7f95f2499f234.png',2,'2026-07-16 19:54:23',1),(8,6,'/api/file/blog/2026/07/16/93cb2ca4af9646aa8699cc5b1a8b8c05.png',1,'2026-07-16 19:59:00',0),(9,6,'/api/file/blog/2026/07/16/43370a5e042340a884d7f95f2499f234.png',2,'2026-07-16 19:59:00',0),(10,6,'https://yi-mian-system.oss-cn-beijing.aliyuncs.com/blog/2026/07/16/9897d2ae451d44569bb2cbb2c5504b81.png',3,'2026-07-16 19:59:00',0);
/*!40000 ALTER TABLE `sys_blog_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_blog_like`
--

DROP TABLE IF EXISTS `sys_blog_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_blog_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `blog_id` bigint NOT NULL COMMENT '博客ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（取消点赞时标记）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_blog_user` (`blog_id`,`user_id`),
  KEY `idx_blog_id` (`blog_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客点赞记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_blog_like`
--

LOCK TABLES `sys_blog_like` WRITE;
/*!40000 ALTER TABLE `sys_blog_like` DISABLE KEYS */;
INSERT INTO `sys_blog_like` VALUES (1,3,10,'2026-07-16 13:07:57',0);
/*!40000 ALTER TABLE `sys_blog_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_blog_topic`
--

DROP TABLE IF EXISTS `sys_blog_topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_blog_topic` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `blog_id` bigint NOT NULL COMMENT '博客ID',
  `topic_id` bigint NOT NULL COMMENT '话题ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_blog_topic` (`blog_id`,`topic_id`),
  KEY `idx_blog_id` (`blog_id`),
  KEY `idx_topic_id` (`topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客-话题关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_blog_topic`
--

LOCK TABLES `sys_blog_topic` WRITE;
/*!40000 ALTER TABLE `sys_blog_topic` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_blog_topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_blog_topic_category`
--

DROP TABLE IF EXISTS `sys_blog_topic_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_blog_topic_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Topic category name',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Topic category description',
  `color` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Display color',
  `sort` int DEFAULT '0' COMMENT 'Sort order',
  `blog_count` int DEFAULT '0' COMMENT 'Published blog count',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  `deleted` tinyint DEFAULT '0' COMMENT 'Logic delete flag',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_blog_topic_category_name` (`name`),
  KEY `idx_blog_topic_category_sort` (`sort`),
  KEY `idx_blog_topic_category_count` (`blog_count`),
  KEY `idx_blog_topic_category_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Blog topic category table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_blog_topic_category`
--

LOCK TABLES `sys_blog_topic_category` WRITE;
/*!40000 ALTER TABLE `sys_blog_topic_category` DISABLE KEYS */;
INSERT INTO `sys_blog_topic_category` VALUES (1,'Java 基础','Java 语言核心概念与基础语法','#e74c3c',1,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(2,'JVM 与性能','JVM 内存模型、GC 调优与性能诊断','#e74c3c',2,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(3,'并发编程','多线程、锁机制与并发工具类','#e74c3c',3,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(4,'Spring 生态','Spring Boot / Cloud / MVC 等框架实践','#6db33f',4,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(5,'数据库','MySQL、PostgreSQL 与 SQL 优化','#336791',5,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(6,'缓存与队列','Redis、RabbitMQ、Kafka 等中间件','#f39c12',6,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(7,'微服务与架构','分布式系统、RPC、DDD 与系统设计','#9b59b6',7,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(8,'计算机网络','HTTP、TCP/IP、网络排障','#3498db',8,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(9,'操作系统','Linux、进程线程与 IO 模型','#1abc9c',9,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(10,'数据结构与算法','常见数据结构、排序与算法题复盘','#e67e22',10,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(11,'设计模式','GOF 设计模式与工程实践','#95a5a6',11,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(12,'前端技术','Vue、React、TypeScript 等','#2ecc71',12,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(13,'DevOps','CI/CD、容器化与运维实践','#34495e',13,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(14,'面试复盘','面试经历、追问分析与经验总结','#6366f1',14,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(15,'工程实践','代码规范、重构、测试与团队协作','#fd79a8',15,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0),(16,'AI 与 LLM','大模型、RAG、Agent 与 AI 工程化','#a29bfe',16,0,'2026-07-15 21:59:42','2026-07-15 21:59:42',0);
/*!40000 ALTER TABLE `sys_blog_topic_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_blog_topic_relation`
--

DROP TABLE IF EXISTS `sys_blog_topic_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_blog_topic_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `blog_id` bigint NOT NULL COMMENT 'Blog ID',
  `topic_id` bigint NOT NULL COMMENT 'Blog topic category ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  `deleted` tinyint DEFAULT '0' COMMENT 'Logic delete flag',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_blog_topic_relation` (`blog_id`,`topic_id`),
  KEY `idx_blog_topic_relation_blog` (`blog_id`),
  KEY `idx_blog_topic_relation_topic` (`topic_id`),
  KEY `idx_blog_topic_relation_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Blog-topic relation table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_blog_topic_relation`
--

LOCK TABLES `sys_blog_topic_relation` WRITE;
/*!40000 ALTER TABLE `sys_blog_topic_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_blog_topic_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_comment`
--

DROP TABLE IF EXISTS `sys_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `target_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Comment target type: knowledge/blog',
  `target_id` bigint NOT NULL COMMENT 'Target ID',
  `parent_id` bigint DEFAULT NULL COMMENT 'Top-level parent comment ID; null for root comments',
  `reply_to_comment_id` bigint DEFAULT NULL COMMENT 'Comment being replied to',
  `reply_to_user_id` bigint DEFAULT NULL COMMENT 'User being replied to',
  `author_id` bigint NOT NULL COMMENT 'Author user ID',
  `content` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Comment content',
  `like_count` int DEFAULT '0' COMMENT 'Like count',
  `reply_count` int DEFAULT '0' COMMENT 'Reply count',
  `status` tinyint DEFAULT '1' COMMENT 'Status: 1=normal',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  `deleted` tinyint DEFAULT '0' COMMENT 'Logic delete flag',
  PRIMARY KEY (`id`),
  KEY `idx_comment_target_parent` (`target_type`,`target_id`,`parent_id`,`deleted`),
  KEY `idx_comment_parent` (`parent_id`,`deleted`),
  KEY `idx_comment_author` (`author_id`),
  KEY `idx_comment_hot` (`target_type`,`target_id`,`parent_id`,`deleted`,`like_count`,`reply_count`,`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Comment table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_comment`
--

LOCK TABLES `sys_comment` WRITE;
/*!40000 ALTER TABLE `sys_comment` DISABLE KEYS */;
INSERT INTO `sys_comment` VALUES (1,'knowledge',10,NULL,NULL,NULL,10,'不错',0,3,1,'2026-07-16 13:36:13','2026-07-16 17:24:58',0),(2,'knowledge',10,1,1,10,10,'喜欢',1,0,1,'2026-07-16 13:36:20','2026-07-16 17:25:09',0),(3,'knowledge',10,1,2,10,10,'是的',0,0,1,'2026-07-16 14:19:42','2026-07-16 14:19:42',0),(4,'knowledge',10,1,2,10,2,'何以为',0,0,1,'2026-07-16 17:24:58','2026-07-16 17:24:58',0),(5,'knowledge',10,NULL,NULL,NULL,10,'你好',0,2,1,'2026-07-16 18:11:31','2026-07-16 18:13:49',0),(6,'knowledge',10,5,5,10,10,'你好',0,0,1,'2026-07-16 18:11:38','2026-07-16 18:11:38',0),(7,'knowledge',10,5,6,10,10,'不错',0,0,1,'2026-07-16 18:13:49','2026-07-16 18:13:49',0);
/*!40000 ALTER TABLE `sys_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_comment_like`
--

DROP TABLE IF EXISTS `sys_comment_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_comment_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `comment_id` bigint NOT NULL COMMENT 'Comment ID',
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  `deleted` tinyint DEFAULT '0' COMMENT 'Logic delete flag',
  PRIMARY KEY (`id`),
  KEY `idx_comment_like_comment_user` (`comment_id`,`user_id`,`deleted`),
  KEY `idx_comment_like_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Comment like table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_comment_like`
--

LOCK TABLES `sys_comment_like` WRITE;
/*!40000 ALTER TABLE `sys_comment_like` DISABLE KEYS */;
INSERT INTO `sys_comment_like` VALUES (1,2,2,'2026-07-16 17:25:09','2026-07-16 17:25:09',0);
/*!40000 ALTER TABLE `sys_comment_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_favorite_folder`
--

DROP TABLE IF EXISTS `sys_favorite_folder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_favorite_folder` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收藏夹名称',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收藏夹描述',
  `user_id` bigint NOT NULL COMMENT '所属用户ID',
  `is_public` tinyint DEFAULT '0' COMMENT '是否公开: 0=私有, 1=公开',
  `cover_image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图URL',
  `item_count` int DEFAULT '0' COMMENT '收藏条目数',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_public_deleted` (`is_public`,`deleted`),
  KEY `idx_user_deleted` (`user_id`,`deleted`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏夹表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_favorite_folder`
--

LOCK TABLES `sys_favorite_folder` WRITE;
/*!40000 ALTER TABLE `sys_favorite_folder` DISABLE KEYS */;
INSERT INTO `sys_favorite_folder` VALUES (1,'测试',NULL,10,0,NULL,2,22,'2026-07-15 17:14:43','2026-07-15 22:41:16',0),(2,'java','',2,1,NULL,0,1,'2026-07-16 16:09:20','2026-07-16 16:22:15',0);
/*!40000 ALTER TABLE `sys_favorite_folder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_favorite_item`
--

DROP TABLE IF EXISTS `sys_favorite_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_favorite_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `folder_id` bigint NOT NULL COMMENT '所属收藏夹ID',
  `item_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'knowledge' COMMENT 'Favorite item type: knowledge/blog',
  `target_id` bigint DEFAULT NULL COMMENT 'Target ID for item_type',
  `knowledge_id` bigint DEFAULT NULL COMMENT 'Knowledge ID, used when item_type=knowledge',
  `blog_id` bigint DEFAULT NULL COMMENT 'Blog ID, used when item_type=blog',
  `sort` int DEFAULT '0' COMMENT '排序号',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_folder_id` (`folder_id`),
  KEY `idx_knowledge_id` (`knowledge_id`),
  KEY `idx_folder_deleted` (`folder_id`,`deleted`),
  KEY `idx_folder_knowledge_deleted` (`folder_id`,`knowledge_id`,`deleted`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_favorite_item_target` (`item_type`,`target_id`,`deleted`),
  KEY `idx_folder_target_deleted` (`folder_id`,`item_type`,`target_id`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏条目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_favorite_item`
--

LOCK TABLES `sys_favorite_item` WRITE;
/*!40000 ALTER TABLE `sys_favorite_item` DISABLE KEYS */;
INSERT INTO `sys_favorite_item` VALUES (1,1,'knowledge',10,10,NULL,0,'2026-07-15 17:14:43','2026-07-15 22:53:29',0),(2,1,'blog',3,NULL,3,0,'2026-07-15 22:41:16','2026-07-15 22:53:29',0);
/*!40000 ALTER TABLE `sys_favorite_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_knowledge`
--

DROP TABLE IF EXISTS `sys_knowledge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_knowledge` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目（如"HashMap底层原理"）',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '正文（Markdown原文）',
  `content_hash` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容SHA-256哈希，用于去重',
  `difficulty` tinyint DEFAULT '1' COMMENT '难度：1=简单 2=中等 3=困难',
  `status` tinyint DEFAULT '0' COMMENT '状态：0=待审核 1=审核通过 2=审核拒绝 3=草稿',
  `audit_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核意见（拒绝时填写）',
  `audit_user_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `submit_user_id` bigint NOT NULL COMMENT '提交人ID',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `collect_count` int DEFAULT '0' COMMENT '被收藏总次数（冗余，方便排序）',
  `comment_count` int DEFAULT '0' COMMENT '评论数（冗余）',
  `rag_doc_ids` text COLLATE utf8mb4_unicode_ci COMMENT 'RAG入库的文档ID列表（JSON数组）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_hash` (`content_hash`),
  KEY `idx_submit_user_id` (`submit_user_id`),
  KEY `idx_status` (`status`,`created_at`),
  KEY `idx_view_count` (`view_count`),
  KEY `idx_difficulty` (`difficulty`),
  FULLTEXT KEY `ft_title_content` (`title`,`content`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识题目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_knowledge`
--

LOCK TABLES `sys_knowledge` WRITE;
/*!40000 ALTER TABLE `sys_knowledge` DISABLE KEYS */;
INSERT INTO `sys_knowledge` VALUES (1,'测试题目','test','9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08',2,0,NULL,NULL,NULL,2,7,0,0,0,NULL,'2026-07-14 15:32:37','2026-07-14 17:59:05',0),(2,'HashMap底层原理与扩容机制','## 核心概念\n\nHashMap是Java中最常用的Map实现，基于哈希表。\n\n## 原理详解\n\n1. JDK1.8采用数组+链表+红黑树\n2. 哈希算法通过扰动函数降低碰撞\n3. 扩容：size>threshold时2倍扩容\n4. 树化：链表>=8且数组>=64\n\n## 常见追问\n\n- 为什么容量是2的幂：位运算代替取模\n- 加载因子为什么0.75：平衡空间与冲突','8fcffa8c20a843099d431eb1eaacf9b37e0aa7258496063b02525d9dd57462b0',2,0,NULL,NULL,NULL,2,2,0,0,0,NULL,'2026-07-14 18:05:31','2026-07-14 18:15:47',0),(3,'TCP三次握手与四次挥手详解','## 三次握手\n\n1. 客户端SYN=1 seq=x -> 服务端\n2. 服务端SYN=1 ACK=1 seq=y ack=x+1 -> 客户端\n3. 客户端ACK=1 ack=y+1 -> 服务端\n\n## 四次挥手\n\n1. 主动方FIN -> 被动方\n2. 被动方ACK -> 主动方\n3. 被动方FIN -> 主动方\n4. 主动方ACK -> 被动方\n\n## 常见追问\n\n为什么三次握手？防止历史连接。\nTIME_WAIT为什么2MSL？确保最后ACK到达。','12ac3378c39139a81694614fbbe05b838266f586631cb64524b545fbdd14e2c9',2,0,NULL,NULL,NULL,2,0,0,0,0,NULL,'2026-07-14 18:06:24','2026-07-14 18:06:24',0),(4,'Spring 事务传播机制详解','## 七种传播行为\n\n| 传播行为 | 说明 |\n|----------|------|\n| REQUIRED（默认） | 有事务则加入，无则新建 |\n| REQUIRES_NEW | 始终新建事务，挂起当前 |\n| SUPPORTS | 有事务则加入，无则非事务 |\n| NOT_SUPPORTED | 非事务执行，挂起当前事务 |\n| MANDATORY | 必须在事务中，否则抛异常 |\n| NEVER | 必须非事务，否则抛异常 |\n| NESTED | 嵌套事务，savepoint 回滚 |\n\n## REQUIRED vs REQUIRES_NEW\n\n`REQUIRED`：外层回滚则内层也回滚。\n`REQUIRES_NEW`：内外事务独立，内层回滚不影响外层。\n\n## 常见坑\n\n1. 自调用失效：同类方法调用不走代理，`@Transactional` 不生效\n2. 非 public 方法：AOP 代理只拦截 public 方法\n3. 异常被 catch：事务只回滚 RuntimeException，checked exception 需显式 rollbackFor','d5e2207889c35f1dd40c6cce406762333e82f3fbc8e15ff4338643a5395e39f7',2,1,NULL,NULL,NULL,2,581,42,28,3,NULL,'2026-07-14 21:03:23','2026-07-15 15:29:51',0),(5,'Redis 五种数据结构及应用场景','## String\n\n- 缓存：`SET key value EX 300`\n- 计数器：`INCR article:1:views`\n- 分布式锁：`SET lock:order uuid NX EX 10`\n\n## Hash\n\n- 用户信息：`HSET user:1 name \"张三\" age 25`\n- 购物车：`HSET cart:1 sku001 3`\n\n## List\n\n- 消息队列：`LPUSH queue:task msg` / `BRPOP queue:task 0`\n- 最新列表：`LPUSH news:latest item` / `LTRIM news:latest 0 99`\n\n## Set\n\n- 标签：`SADD user:1:tags \"Java\" \"Spring\"`\n- 共同好友：`SINTER user:1:friends user:2:friends`\n\n## Sorted Set\n\n- 排行榜：`ZADD rank:score 980 user1`\n- 延时队列：score = 时间戳，`ZRANGEBYSCORE` 取出到期任务','63fa6c62c3998cc5d0bdbf19a82281446154651b9a8a8fa71e7311e44a385373',1,1,NULL,NULL,NULL,2,728,65,48,5,NULL,'2026-07-14 21:03:23','2026-07-16 19:40:48',0),(6,'volatile 关键字的作用与底层原理','## 两大作用\n\n1. **保证可见性**：写 volatile 变量后立即刷新到主内存，读 volatile 变量前从主内存加载\n2. **禁止指令重排序**：通过内存屏障（Memory Barrier）阻止 JIT 编译器重排指令\n\n## 不保证原子性\n\n`count++` 是三步操作（读-改-写），volatile 不能保证复合操作的原子性。\n\n## 底层实现\n\n- x86 架构：`lock` 前缀指令，锁定总线/缓存行\n- Java 内存模型（JMM）：StoreStore → volatile写 → StoreLoad 屏障\n\n## 经典应用\n\n1. **DCL 单例**：`private static volatile Singleton instance;`\n2. **状态标志位**：`volatile boolean running = true;`\n\n## 与 synchronized 对比\n\n| 维度 | volatile | synchronized |\n|------|----------|-------------|\n| 可见性 | ✅ | ✅ |\n| 原子性 | ❌ | ✅ |\n| 性能 | 高 | 较低 |\n| 适用 | 一写多读 | 读写互斥 |','e719b28d84cdd56d6f05cc4b45e900e1bf09510c240883fa6c0ed250ddbd40c1',2,1,NULL,NULL,NULL,2,460,35,22,2,NULL,'2026-07-14 21:03:23','2026-07-14 21:03:23',0),(7,'分布式锁的三种实现方案对比','## Redis 实现\n\n```\nSET lock:order uuid NX PX 30000\n```\n\n- 加锁 + 过期原子操作\n- Lua 脚本解锁：`if redis.call(\"get\",KEYS[1])==ARGV[1] then return redis.call(\"del\",KEYS[1])`\n- Redisson 封装了看门狗自动续期\n\n## Zookeeper 实现\n\n- 临时顺序节点：`/locks/lock-0000000001`\n- 最小节点获得锁\n- 前一节点注册 Watcher，删除时唤醒后继\n- 缺点：羊群效应，性能较差\n\n## 数据库实现\n\n- `INSERT INTO lock_table (lock_key) VALUES (\"order_lock\")`\n- 唯一索引保证互斥\n- 缺点：无过期机制，需额外清理\n\n## 选型\n\n| 方案 | 可靠性 | 性能 | 复杂度 |\n|------|--------|------|--------|\n| Redis | 中（AP） | 高 | 低 |\n| ZK | 高（CP） | 中 | 中 |\n| DB | 高 | 低 | 低 |\n\n> 一般场景用 Redisson 即可，金融场景可考虑 ZK/etcd。','d0740fdd98efca403a5c133c0c742a7cf993cfadb66640f85595bd73ed14c29e',3,1,NULL,NULL,NULL,2,621,55,38,4,NULL,'2026-07-14 21:03:23','2026-07-15 15:29:33',0),(8,'JVM 类加载机制与双亲委派模型','## 类加载过程\n\n1. **加载**：读取 .class 字节码 → 方法区\n2. **验证**：文件格式、元数据、字节码验证\n3. **准备**：静态变量分配内存，赋零值\n4. **解析**：符号引用 → 直接引用\n5. **初始化**：执行 `<clinit>`，静态变量赋值\n\n## 双亲委派模型\n\n```\nBootstrapClassLoader (rt.jar)\n    ↑\nExtensionClassLoader (jre/lib/ext)\n    ↑\nApplicationClassLoader (classpath)\n    ↑\n自定义 ClassLoader\n```\n\n## 为什么双亲委派？\n\n1. 防止核心类被篡改（如自定义 java.lang.String）\n2. 避免类的重复加载\n\n## 打破双亲委派\n\n- Tomcat：每个 WebApp 独立 ClassLoader\n- SPI（JDBC）：`Thread.currentThread().getContextClassLoader()`\n- OSGi：网状类加载','344040cbc6465941d8e070ff69349faf0b291bff34a9dd82df1b122d83a14de5',2,1,NULL,NULL,NULL,2,391,30,18,2,NULL,'2026-07-14 21:03:23','2026-07-16 15:53:17',0),(9,'单例模式的五种写法','## 1. 饿汉式\n\n```java\nprivate static final Singleton INSTANCE = new Singleton();\npublic static Singleton getInstance() { return INSTANCE; }\n```\n\n## 2. 懒汉式（双重检查锁）\n\n```java\nprivate static volatile Singleton instance;\npublic static Singleton getInstance() {\n  if (instance == null) {\n    synchronized (Singleton.class) {\n      if (instance == null) instance = new Singleton();\n    }\n  }\n  return instance;\n}\n```\n\n## 3. 静态内部类（推荐）\n\n```java\nprivate static class Holder {\n  static final Singleton INSTANCE = new Singleton();\n}\npublic static Singleton getInstance() { return Holder.INSTANCE; }\n```\n\n## 4. 枚举（最安全）\n\n```java\nenum Singleton { INSTANCE; }\n```\n\n防反射、防序列化破坏。\n\n## 5. 容器式\n\nSpring 默认 scope=singleton，由容器管理单例。','d58edd35cada98cb3aed49d69199c4f7a51e07995d64e68b5cc45e9baae746da',1,1,NULL,NULL,NULL,2,344,28,15,1,NULL,'2026-07-14 21:03:23','2026-07-16 15:53:11',0),(10,'创建 BigDecimal 的正确姿势','> BigDecimal 是Java提供的高精度数值计算类,专门解决float和double的精度丢失问题。涉及到钱的计算,必须用BigDecimal。\n\n为什么float和double会丢精度?因为它们是用IEEE 754标准的二进制浮点数表示的,像0.1这种十进制小数在二进制里是无限循环小数,存储时只能截断,精度就丢了。看个经典例子:\n\n```java\nSystem.out.println(0.1 + 0.2);  // 输出 0.30000000000000004，不是 0.3\nSystem.out.println(1.0 - 0.9);  // 输出 0.09999999999999998，不是 0.1\n```\n\nBigDecimal 用整数加小数点位置的方式存储,能精确表示任意精度的十进制数。\n\n```java\nBigDecimal a = new BigDecimal(\"1.0\");\nBigDecimal b = new BigDecimal(\"0.9\");\nSystem.out.println(a.subtract(b));  // 输出 0.1，精确无误\n```\n\n# 创建 BigDecimal 的正确姿势\n\n这里有个大坑,很多人踩过:\n```java\n// 错误写法，会引入精度问题\nBigDecimal bad = new BigDecimal(0.1);\nSystem.out.println(bad);  // 输出 0.1000000000000000055511151231257827021181583404541015625\n\n// 正确写法一：用字符串构造\nBigDecimal good1 = new BigDecimal(\"0.1\");\nSystem.out.println(good1);  // 输出 0.1\n\n// 正确写法二：用 valueOf\nBigDecimal good2 = BigDecimal.valueOf(0.1);\nSystem.out.println(good2);  // 输出 0.1\n```\n\n> `new BigDecimal(double)`会把double的精度问题原封不动带进来,因为传进去的0.1在double阶段就已经不精确了。`BigDecimal.valueOf` 内部会先把double 转成 String再构造,所以没问题。\n\n《阿里巴巴Java开发手册》明确规定,浮点数之间的等值判断,基本数据类型不能用 == 来比较,包装数据类型不能用equals来判断。\n\n\n# 除法必须指定精度\n\n`BigDecimal` 做除法时,如果除不尽又没指定精度和舍入模式,会直接抛ArithmeticException:\n```java\nBigDecimal a = new BigDecimal(\"1\");\nBigDecimal b = new BigDecimal(\"3\");\n// 错误写法，会抛异常\n// BigDecimal c = a.divide(b);  // ArithmeticException: Non-terminating decimal expansion\n\n// 正确写法，指定保留 4 位小数，四舍五入\nBigDecimal c = a.divide(b, 4, RoundingMode.HALF_UP);\nSystem.out.println(c);  // 输出 0.3333\n```\n\n## 八种舍入模式\n\n### `UP`（远离0）\n\n👉 有小数就进位\n\n```\n1.21 → 1.3  \n-1.21 → -1.3\n```\n\n---\n\n### 2️⃣ `DOWN`（向0截断）\n\n👉 直接截掉\n\n```\n1.29 → 1.2  \n-1.29 → -1.2\n```\n\n---\n\n### 3️⃣ `CEILING`（向正无穷）\n\n👉 往大的方向走\n\n```\n1.21 → 1.3  \n-1.21 → -1.2\n```\n\n---\n\n### 4️⃣ `FLOOR`（向负无穷）\n\n👉 往小的方向走\n\n```\n1.21 → 1.2  \n-1.21 → -1.3\n```\n\n---\n\n### 5️⃣ `HALF_UP`（四舍五入 ⭐最常用）\n\n👉 ≥5进位\n\n```\n1.25 → 1.3  \n1.24 → 1.2\n```\n\n---\n\n### 6️⃣ `HALF_DOWN`（五舍六入）\n\n👉 >5才进位\n\n```\n1.25 → 1.2  \n1.26 → 1.3\n```\n\n---\n\n### 7️⃣ `HALF_EVEN`（银行家算法 ⭐重要）\n\n👉 5时看前一位是否为偶数\n\n```\n1.25 → 1.2（2是偶数）  \n1.35 → 1.4（3是奇数）\n```\n\n👉 用于金融，减少累计误差\n\n---\n\n### 8️⃣ `UNNECESSARY`（不允许舍入）\n\n👉 如果需要舍入 → 直接报错\n\n```\nBigDecimal a = new BigDecimal(\"10\");  \nBigDecimal b = new BigDecimal(\"3\");  \n  \na.divide(b, 2, RoundingMode.UNNECESSARY); // ❗异常\n```\n\n## 总结\n\n| 舍入模式        | 说明             | 2.5 结果 | -2.5 结果 |\n|-----------------|------------------|----------|-----------|\n| UP              | 远离零方向舍入   | 3        | -3        |\n| DOWN            | 靠近零方向舍入   | 2        | -2        |\n| CEILING         | 向正无穷方向舍入 | 3        | -2        |\n| FLOOR           | 向负无穷方向舍入 | 2        | -3        |\n| HALF_UP         | 四舍五入         | 3        | -3        |\n| HALF_DOWN       | 五舍六入         | 2        | -2        |\n| HALF_EVEN       | 银行家舍入法     | 2        | -2        |\n| UNNECESSARY     | 不允许舍入       | 抛异常   | 抛异常    |\n\n# compareTo 比较而不是 equals\n\nBigDecimal的equals方法会同时比较值和精度(scale),所以:\n\n```java\nBigDecimal a = new BigDecimal(\"1.0\");\nBigDecimal b = new BigDecimal(\"1.00\");\nSystem.out.println(a.equals(b));     // false，精度不同\nSystem.out.println(a.compareTo(b));  // 0，值相等\n```\n\n比较BigDecimal的大小,永远用compareTo,返回-1、0、1分别表示小于、等于、大于。\n\n# BigDecimal 的性能问题\n\nBigDecimal 是不可变类,每次运算都会创建新对象。在大量循环计算场景下,会产生很多临时对象,增加GC压力。\n\n```java\n// 低效写法\nBigDecimal sum = BigDecimal.ZERO;\nfor (int i = 0; i < 100000; i++) {\n    sum = sum.add(new BigDecimal(\"0.1\"));  // 每次循环创建 2 个对象\n}\n\n// 优化写法：复用 BigDecimal 常量\nBigDecimal increment = new BigDecimal(\"0.1\");\nBigDecimal sum = BigDecimal.ZERO;\nfor (int i = 0; i < 100000; i++) {\n    sum = sum.add(increment);  // 每次循环只创建 1 个对象\n}\n```\n\n如果对性能要求极高,可以考虑用long存储最小单位。比如金额用“分”存储,1.23元存成123分,运算时用long,最后展示时再转换。很多支付系统都是这么干的。\n\n# MySQL中存储金额\n\n数据库层面，MySQL存储金额应该用DECIMAL类型，而不要用FLOAT或DOUBLE，原因同样是精度问题\n\n# BigDecimal内部如何存储数据的\n\n`BigDecimal` = 整数（intVal） + 小数位数（scale）\n\n可以理解成\n```java\n值 = intVal × 10^(-scale)\n```\n\n核心源码：\n```java\nprivate final BigInteger intVal; // 大整数（核心）\nprivate final int scale;         // 小数位数\nprivate transient int precision; // 精度（位数）\nprivate transient long intCompact; // 小数值优化（重要）\n```\n\n举个例子\n\n```java\nBigDecimal a = new BigDecimal(\"123.45\");\n\n//其内部为\nintVal = 12345\nscale  = 2\n\n//计算方式\n12345 × 10^-2 = 123.45\n```\n\n# BigDecimal.valueOf 和new BigDecimal(String)有什么区别?\n\n`BigDecimal.valueOf(double)`内部会先调用`Double.toString`把double转成字符串,再用字符串构造`BigDecimal`,所以结果是精确的。`new BigDecimal(String)`直接用字符串构造。两者结果一样,但valueOf有个小优化:对于0到10的整数有缓存,会直接返回缓存对象。\n\n```java\n//new对象\nBigDecimal a = new BigDecimal(\"0.1\");\n//完全精准，直接按字符串解析，没有任何中间误差\nintVal = 1\nscale  = 1\n\n//valueOf（安全写法）\nBigDecimal a = BigDecimal.valueOf(0.1);\n//内部其实是\nreturn new BigDecimal(Double.toString(0.1)); //先把 `double` 转成字符串，再调用 `new BigDecimal(String)`\n\n```\n\n# ValueOf（）\n\n`valueOf` 本质上是一个**静态工厂方法（static factory method）**，不是关键字，也不是特殊语法。\n\n## 在 `BigDecimal` 里的作用\n\n比如：\n\nBigDecimal a = BigDecimal.valueOf(10);  \nBigDecimal b = BigDecimal.valueOf(0.1);\n\n👉 它的作用是：\n\n- 把 **基本类型（int / double）**\n- 转成 **BigDecimal 对象**\n\n\n# 使用BigDecimal常见问题\n\n## 1.构造时别用double\n\n```java\n// 错误写法，double 传进去时就已经丢精度了\nBigDecimal bad = new BigDecimal(0.1);\nSystem.out.println(bad);  // 0.1000000000000000055511151231257827021181583404541015625\n\n// 正确写法，用字符串构造\nBigDecimal good = new BigDecimal(\"0.1\");\nSystem.out.println(good);  // 0.1\n```\n\n如果一定要用double，用`BigDecimal.valueOf()`方法。\n\n## 2，比较时用compareTo\n\n```java\nBigDecimal a = new BigDecimal(\"1.0\");\nBigDecimal b = new BigDecimal(\"1.00\");\nSystem.out.println(a.equals(b));      // false，scale 不同\nSystem.out.println(a.compareTo(b));   // 0，数值相等\n```\n\nequals 会同时比较值和scale,scale不一样就返回false。业务上判断数值是否相等得用compareTo。\n## 3，除法必须指定精度\n\n```java\nBigDecimal a = new BigDecimal(\"1\");\nBigDecimal b = new BigDecimal(\"3\");\n// a.divide(b);  // 抛 ArithmeticException: Non-terminating decimal expansion\n\n// 正确写法：指定保留 4 位小数，四舍五入\nBigDecimal result = a.divide(b, 4, RoundingMode.HALF_UP);\nSystem.out.println(result);  // 0.3333\n```\n','abe333f9b97715195b828651e6be8939dd636b8587dc4c5a6bad0b90c3b49061',2,1,NULL,2,'2026-07-15 15:33:43',10,18,1,1,7,NULL,'2026-07-15 15:32:44','2026-07-16 18:13:49',0);
/*!40000 ALTER TABLE `sys_knowledge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_knowledge_like`
--

DROP TABLE IF EXISTS `sys_knowledge_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_knowledge_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `knowledge_id` bigint NOT NULL COMMENT 'Knowledge ID',
  `user_id` bigint NOT NULL COMMENT 'User ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  `deleted` tinyint DEFAULT '0' COMMENT 'Logic delete flag',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_knowledge_user` (`knowledge_id`,`user_id`),
  KEY `idx_knowledge_like_knowledge_user_deleted` (`knowledge_id`,`user_id`,`deleted`),
  KEY `idx_knowledge_like_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Knowledge like table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_knowledge_like`
--

LOCK TABLES `sys_knowledge_like` WRITE;
/*!40000 ALTER TABLE `sys_knowledge_like` DISABLE KEYS */;
INSERT INTO `sys_knowledge_like` VALUES (1,10,10,'2026-07-16 14:48:12','2026-07-16 14:48:12',0);
/*!40000 ALTER TABLE `sys_knowledge_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_knowledge_tag`
--

DROP TABLE IF EXISTS `sys_knowledge_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_knowledge_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `knowledge_id` bigint NOT NULL COMMENT '知识ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_knowledge_tag` (`knowledge_id`,`tag_id`),
  KEY `idx_knowledge_id` (`knowledge_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识-标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_knowledge_tag`
--

LOCK TABLES `sys_knowledge_tag` WRITE;
/*!40000 ALTER TABLE `sys_knowledge_tag` DISABLE KEYS */;
INSERT INTO `sys_knowledge_tag` VALUES (1,1,1),(2,2,1),(19,2,5),(3,3,9),(18,3,20),(4,4,6),(5,4,7),(7,5,12),(8,6,1),(9,6,3),(10,6,4),(11,7,12),(12,8,1),(13,8,3),(15,9,1),(16,9,23),(20,10,2);
/*!40000 ALTER TABLE `sys_knowledge_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_log_module`
--

DROP TABLE IF EXISTS `sys_log_module`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_log_module` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块编码，对应注解 @OperationLog(module=...)，如 USER',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块名称（展示用），如 用户管理',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模块描述',
  `sort` int DEFAULT '0' COMMENT '排序号',
  `enabled` tinyint DEFAULT '1' COMMENT '启用状态：0=禁用，1=启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志模块字典';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_log_module`
--

LOCK TABLES `sys_log_module` WRITE;
/*!40000 ALTER TABLE `sys_log_module` DISABLE KEYS */;
INSERT INTO `sys_log_module` VALUES (1,'USER','用户管理',NULL,1,1,'2026-07-13 14:53:57','2026-07-13 14:53:57'),(2,'ROLE','角色管理',NULL,2,1,'2026-07-13 14:53:57','2026-07-13 14:53:57'),(3,'PERMISSION','权限管理',NULL,3,1,'2026-07-13 14:53:57','2026-07-13 14:53:57'),(4,'PROFILE','个人中心',NULL,4,1,'2026-07-13 14:53:57','2026-07-13 14:53:57'),(5,'AUTH','登录认证',NULL,5,1,'2026-07-13 14:53:57','2026-07-13 14:53:57'),(6,'LOG_MODULE','日志模块字典',NULL,6,1,NULL,NULL),(9,'KNOWLEDGE','知识模块',NULL,7,1,'2026-07-14 15:17:19','2026-07-14 15:17:19'),(10,'TAG','标签模块',NULL,8,1,'2026-07-14 21:03:23','2026-07-14 21:03:23'),(13,'FAVORITE','收藏夹模块',NULL,9,1,'2026-07-15 17:02:21','2026-07-15 17:02:21'),(14,'BLOG','博客模块',NULL,10,1,'2026-07-15 18:12:49','2026-07-15 18:13:07'),(16,'TOPIC','Blog Topic',NULL,11,1,'2026-07-15 21:35:02','2026-07-15 21:59:42'),(18,'COMMENT','Comment module',NULL,11,1,'2026-07-16 13:32:58','2026-07-16 13:32:58');
/*!40000 ALTER TABLE `sys_log_module` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notification`
--

DROP TABLE IF EXISTS `sys_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sender_id` bigint DEFAULT NULL,
  `receiver_id` bigint NOT NULL,
  `target_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `target_id` bigint DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `extra` json DEFAULT NULL,
  `read` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_receiver_read` (`receiver_id`,`read`,`created_at` DESC),
  KEY `idx_receiver_type` (`receiver_id`,`type`,`created_at` DESC),
  KEY `idx_sender` (`sender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notification`
--

LOCK TABLES `sys_notification` WRITE;
/*!40000 ALTER TABLE `sys_notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_operation_log`
--

DROP TABLE IF EXISTS `sys_operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint DEFAULT NULL COMMENT '操作人ID（未登录时为NULL）',
  `username` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人用户名（冗余，避免每次JOIN）',
  `module` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块，如"用户管理""角色管理"',
  `operation` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型，如"创建用户""删除角色"',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '动态描述，如"删除了用户张三(ID:123)"',
  `method` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'HTTP方法：GET/POST/PUT/DELETE',
  `request_uri` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求路径，如/api/admin/users/123',
  `class_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '全限定方法名，如com.yimian.system.controller.admin.AdminUserController.deleteUser',
  `request_params` text COLLATE utf8mb4_unicode_ci COMMENT '请求参数JSON（敏感字段自动脱敏）',
  `ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '客户端IP（支持IPv6最长45字符）',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '浏览器/客户端User-Agent',
  `response_code` int DEFAULT NULL COMMENT 'HTTP状态码：200/400/401/500',
  `result_code` int DEFAULT NULL COMMENT '业务码（Result.code）',
  `result_msg` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务消息（Result.message）',
  `error_msg` text COLLATE utf8mb4_unicode_ci COMMENT '异常信息（仅message不含堆栈）',
  `duration` bigint DEFAULT NULL COMMENT '接口执行耗时（毫秒）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '操作结果：1=成功 0=失败',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`,`created_at`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_module` (`module`,`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作日志表（只追加，不修改不删除）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_operation_log`
--

LOCK TABLES `sys_operation_log` WRITE;
/*!40000 ALTER TABLE `sys_operation_log` DISABLE KEYS */;
INSERT INTO `sys_operation_log` VALUES (7,NULL,NULL,'AUTH','用户登录','用户 admin 尝试登录','POST','/api/auth/login','com.yimian.system.controller.AuthController.login','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.127.0 Chrome/148.0.7778.97 Electron/42.2.0 Safari/537.36',NULL,200,'操作成功',NULL,84,1,'2026-07-13 15:06:09'),(8,NULL,NULL,'AUTH','用户登录','用户 admin 尝试登录','POST','/api/auth/login','com.yimian.system.controller.AuthController.login','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.127.0 Chrome/148.0.7778.97 Electron/42.2.0 Safari/537.36',NULL,200,'操作成功',NULL,73,1,'2026-07-13 15:09:56'),(9,2,'admin','LOG_MODULE','新增模块','新增模块 LOG_MODULE','POST','/api/admin/logs/modules','com.yimian.system.controller.admin.AdminLogModuleController.create','{\"dto\":{\"code\":\"LOG_MODULE\",\"name\":\"日志模块字典\",\"description\":null,\"sort\":6,\"enabled\":1}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.127.0 Chrome/148.0.7778.97 Electron/42.2.0 Safari/537.36',NULL,200,'操作成功',NULL,14,1,'2026-07-13 15:20:40'),(10,2,'admin','LOG_MODULE','新增模块','新增模块 test','POST','/api/admin/logs/modules','com.yimian.system.controller.admin.AdminLogModuleController.create','{\"dto\":{\"code\":\"test\",\"name\":\"11\",\"description\":null,\"sort\":0,\"enabled\":1}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.127.0 Chrome/148.0.7778.97 Electron/42.2.0 Safari/537.36',NULL,200,'操作成功',NULL,14,1,'2026-07-13 15:25:01'),(11,2,'admin','LOG_MODULE','删除模块','删除模块 ID=7','DELETE','/api/admin/logs/modules/7','com.yimian.system.controller.admin.AdminLogModuleController.delete','{\"id\":7}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.127.0 Chrome/148.0.7778.97 Electron/42.2.0 Safari/537.36',NULL,200,'操作成功',NULL,10,1,'2026-07-13 15:25:06'),(12,2,'admin','USER','新增用户','新增用户 test222','POST','/api/admin/users','com.yimian.system.controller.admin.AdminUserController.create','{\"dto\":{\"username\":\"test222\",\"password\":\"***\",\"email\":null,\"phone\":null,\"nickname\":\"new\",\"roleCodes\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.127.0 Chrome/148.0.7778.97 Electron/42.2.0 Safari/537.36',NULL,200,'操作成功',NULL,79,1,'2026-07-13 15:35:50'),(13,NULL,NULL,'AUTH','用户登录','用户 admin 尝试登录','POST','/api/auth/login','com.yimian.system.controller.AuthController.login','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.127.0 Chrome/148.0.7778.97 Electron/42.2.0 Safari/537.36',NULL,200,'操作成功',NULL,147,1,'2026-07-13 17:18:04'),(14,2,'admin','USER','新增用户','新增用户 yanmoyyds','POST','/api/admin/users','com.yimian.system.controller.admin.AdminUserController.create','{\"dto\":{\"username\":\"yanmoyyds\",\"password\":\"***\",\"email\":null,\"phone\":null,\"nickname\":null,\"roleCodes\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.127.0 Chrome/148.0.7778.97 Electron/42.2.0 Safari/537.36',NULL,200,'操作成功',NULL,95,1,'2026-07-13 17:18:42'),(15,NULL,NULL,'AUTH','用户登录','用户 yanmoyyds 尝试登录','POST','/api/auth/login','com.yimian.system.controller.AuthController.login','{\"dto\":{\"username\":\"yanmoyyds\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.127.0 Chrome/148.0.7778.97 Electron/42.2.0 Safari/537.36',NULL,200,'操作成功',NULL,113,1,'2026-07-13 17:18:54'),(16,NULL,NULL,'AUTH','用户登录','用户 admin 尝试登录','POST','/api/auth/login','com.yimian.system.controller.AuthController.login','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.1 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,244,1,'2026-07-13 19:40:40'),(17,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.1 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,209,1,'2026-07-13 20:18:18'),(18,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.1 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,198,1,'2026-07-13 20:35:05'),(19,2,'admin','PROFILE','修改个人信息','修改个人信息','PUT','/api/user/profile','com.yimian.system.controller.UserController.updateProfile','{\"dto\":{\"email\":null,\"phone\":null,\"nickname\":null,\"avatar\":\"/api/file/avatar/2026/07/13/68445b14cb67463cbe87da73832e4740.jpg\"}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.1 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,32,1,'2026-07-13 20:43:08'),(20,NULL,NULL,'AUTH','管理员登录','管理员 admin 登录后台','POST','/api/auth/admin/login','com.yimian.system.controller.AuthController.adminLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":\"\",\"captcha\":\"\"}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,176,1,'2026-07-14 15:30:32'),(21,2,'admin','KNOWLEDGE','提交题目','提交知识题目: 测试题目','POST','/api/knowledge/submit','com.yimian.system.controller.KnowledgeController.submit','{\"dto\":{\"title\":\"测试题目\",\"content\":\"test\",\"difficulty\":2,\"tagIds\":[1]}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,24,1,'2026-07-14 15:32:37'),(22,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.127.0 Chrome/148.0.7778.97 Electron/42.2.0 Safari/537.36',NULL,200,'操作成功',NULL,99,1,'2026-07-14 16:15:34'),(23,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,245,1,'2026-07-14 17:30:37'),(24,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,119,1,'2026-07-14 17:31:52'),(25,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,126,1,'2026-07-14 17:33:25'),(26,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,104,1,'2026-07-14 17:34:18'),(27,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,122,1,'2026-07-14 17:34:32'),(28,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.7 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,123,1,'2026-07-14 17:47:39'),(29,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,119,1,'2026-07-14 18:03:05'),(30,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,104,1,'2026-07-14 18:03:16'),(31,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,112,1,'2026-07-14 18:03:31'),(32,2,'admin','KNOWLEDGE','提交题目','提交知识题目: HashMap底层原理与扩容机制','POST','/api/knowledge/submit','com.yimian.system.controller.KnowledgeController.submit','{\"dto\":{\"title\":\"HashMap底层原理与扩容机制\",\"content\":\"## 核心概念\\n\\nHashMap是Java中最常用的Map实现，基于哈希表。\\n\\n## 原理详解\\n\\n1. JDK1.8采用数组+链表+红黑树\\n2. 哈希算法通过扰动函数降低碰撞\\n3. 扩容：size>threshold时2倍扩容\\n4. 树化：链表>=8且数组>=64\\n\\n## 常见追问\\n\\n- 为什么容量是2的幂：位运算代替取模\\n- 加载因子为什么0.75：平衡空间与冲突\",\"difficulty\":2,\"tagIds\":[1]}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.7 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,62,1,'2026-07-14 18:05:31'),(33,2,'admin','KNOWLEDGE','提交题目','提交知识题目: TCP三次握手与四次挥手详解','POST','/api/knowledge/submit','com.yimian.system.controller.KnowledgeController.submit','{\"dto\":{\"title\":\"TCP三次握手与四次挥手详解\",\"content\":\"## 三次握手\\n\\n1. 客户端SYN=1 seq=x -> 服务端\\n2. 服务端SYN=1 ACK=1 seq=y ack=x+1 -> 客户端\\n3. 客户端ACK=1 ack=y+1 -> 服务端\\n\\n## 四次挥手\\n\\n1. 主动方FIN -> 被动方\\n2. 被动方ACK -> 主动方\\n3. 被动方FIN -> 主动方\\n4. 主动方ACK -> 被动方\\n\\n## 常见追问\\n\\n为什么三次握手？防止历史连接。\\nTIME_WAIT为什么2MSL？确保最后ACK到达。\",\"difficulty\":2,\"tagIds\":[9]}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.7 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,25,1,'2026-07-14 18:06:24'),(34,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.7 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,122,1,'2026-07-14 18:28:39'),(35,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.7 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,81,1,'2026-07-14 18:34:08'),(36,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.7 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,80,1,'2026-07-14 18:42:59'),(37,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,166,1,'2026-07-14 19:24:49'),(38,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,110,1,'2026-07-14 19:28:04'),(39,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.7 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,120,1,'2026-07-14 20:15:37'),(40,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,114,1,'2026-07-14 20:17:49'),(41,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,291,1,'2026-07-14 20:33:02'),(42,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,83,1,'2026-07-14 20:33:18'),(43,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.7 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,112,1,'2026-07-14 20:35:35'),(44,2,'admin','TAG','新增标签','新增标签: Go','POST','/api/admin/tags','com.yimian.system.controller.TagController.create','{\"entity\":{\"id\":null,\"createdAt\":null,\"updatedAt\":null,\"deleted\":null,\"name\":\"Go\",\"sort\":27,\"color\":\"#00ADD8\"}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,19,1,'2026-07-14 20:38:20'),(45,NULL,NULL,'AUTH','管理员登录','管理员 admin 登录后台','POST','/api/auth/admin/login','com.yimian.system.controller.AuthController.adminLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.7 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,93,1,'2026-07-14 20:45:34'),(46,2,'admin','TAG','新增标签','新增标签: Docker','POST','/api/admin/tags','com.yimian.system.controller.TagController.create','{\"entity\":{\"id\":null,\"createdAt\":null,\"updatedAt\":null,\"deleted\":null,\"name\":\"Docker\",\"sort\":0,\"color\":\"#6366f1\"}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.7 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,26,1,'2026-07-14 20:46:50'),(47,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,113,1,'2026-07-14 21:03:57'),(48,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,98,1,'2026-07-14 21:16:14'),(49,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,95,1,'2026-07-14 21:16:44'),(50,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,102,1,'2026-07-14 21:17:03'),(51,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,85,1,'2026-07-14 21:17:24'),(52,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,118,1,'2026-07-14 21:18:00'),(53,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,127,1,'2026-07-14 21:18:40'),(54,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,93,1,'2026-07-14 21:19:24'),(55,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,116,1,'2026-07-14 21:19:39'),(56,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,116,1,'2026-07-14 21:19:51'),(57,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,90,1,'2026-07-14 21:20:06'),(58,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,93,1,'2026-07-14 21:20:29'),(59,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.19.0',NULL,200,'操作成功',NULL,103,1,'2026-07-14 21:20:47'),(60,NULL,NULL,'AUTH','管理员登录','管理员 admin 登录后台','POST','/api/auth/admin/login','com.yimian.system.controller.AuthController.adminLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.7 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,103,1,'2026-07-14 21:21:34'),(61,NULL,NULL,'AUTH','管理员登录','管理员 admin 登录后台','POST','/api/auth/admin/login','com.yimian.system.controller.AuthController.adminLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Claude/1.20186.7 Chrome/148.0.7778.271 Electron/42.5.1 Safari/537.36 MSIX',NULL,200,'操作成功',NULL,112,1,'2026-07-14 21:34:10'),(62,NULL,NULL,'AUTH','管理员登录','管理员 admin 登录后台','POST','/api/auth/admin/login','com.yimian.system.controller.AuthController.adminLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.127.0 Chrome/148.0.7778.97 Electron/42.2.0 Safari/537.36',NULL,200,'操作成功',NULL,73,1,'2026-07-14 21:34:40'),(63,NULL,NULL,'AUTH','管理员登录','管理员 admin 登录后台','POST','/api/auth/admin/login','com.yimian.system.controller.AuthController.adminLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,66,1,'2026-07-14 21:41:07'),(64,NULL,NULL,'AUTH','管理员登录','管理员 admin 登录后台','POST','/api/auth/admin/login','com.yimian.system.controller.AuthController.adminLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,201,1,'2026-07-15 15:05:03'),(65,NULL,NULL,'AUTH','用户注册','新用户注册 ymyyds','POST','/api/auth/register','com.yimian.system.controller.AuthController.register','{\"dto\":{\"username\":\"ymyyds\",\"password\":\"***\",\"email\":null,\"phone\":null,\"nickname\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,188,1,'2026-07-15 15:26:24'),(66,NULL,NULL,'AUTH','用户端登录','用户 ymyyds 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"ymyyds\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,99,1,'2026-07-15 15:26:28'),(67,10,'ymyyds','KNOWLEDGE','提交题目','提交知识题目: 创建 BigDecimal 的正确姿势','POST','/api/knowledge/submit','com.yimian.system.controller.KnowledgeController.submit','{\"dto\":{\"title\":\"创建 BigDecimal 的正确姿势\",\"content\":\"> BigDecimal 是Java提供的高精度数值计算类,专门解决float和double的精度丢失问题。涉及到钱的计算,必须用BigDecimal。\\n\\n为什么float和double会丢精度?因为它们是用IEEE 754标准的二进制浮点数表示的,像0.1这种十进制小数在二进制里是无限循环小数,存储时只能截断,精度就丢了。看个经典例子:\\n\\n```java\\nSystem.out.println(0.1 + 0.2);  // 输出 0.30000000000000004，不是 0.3\\nSystem.out.println(1.0 - 0.9);  // 输出 0.09999999999999998，不是 0.1\\n```\\n\\nBigDecimal 用整数加小数点位置的方式存储,能精确表示任意精度的十进制数。\\n\\n```java\\nBigDecimal a = new BigDecimal(\\\"1.0\\\");\\nBigDecimal b = new BigDecimal(\\\"0.9\\\");\\nSystem.out.println(a.subtract(b));  // 输出 0.1，精确无误\\n```\\n\\n# 创建 BigDecimal 的正确姿势\\n\\n这里有个大坑,很多人踩过:\\n```java\\n// 错误写法，会引入精度问题\\nBigDecimal bad = new BigDecimal(0.1);\\nSystem.out.println(bad);  // 输出 0.1000000000000000055511151231257827021181583404541015625\\n\\n// 正确写法一：用字符串构造\\nBigDecimal good1 = new BigDecimal(\\\"0.1\\\");\\nSystem.out.println(good1);  // 输出 0.1\\n\\n// 正确写法二：用 valueOf\\nBigDecimal good2 = BigDecimal.valueOf(0.1);\\nSystem.out.println(good2);  // 输出 0.1\\n```\\n\\n> `new BigDecimal(double)`会把double的精度问题原封不动带进来,因为传进去的0.1在double阶段就已经不精确了。`BigDecimal.valueOf` 内部会先把double 转成 String再构造,所以没问题。\\n\\n《阿里巴巴Java开发手册》明确规定,浮点数之间的等值判断,基本数据类型不能用 == 来比较,包装数据类型不能用equals来判断。\\n\\n\\n# 除法必须指定精度\\n\\n`BigDecimal` 做除法时,如果除不尽又没指定精度和舍入模式,会直接抛ArithmeticException:\\n```java\\nBigDecimal a = new BigDecimal(\\\"1\\\");\\nBigDecimal b = new BigDecimal(\\\"3\\\");\\n// 错误写法，会抛异常\\n// BigDecimal c = a.divide(b);  // ArithmeticException: Non-terminating decimal expansion\\n\\n// 正确写法，指定保留 4 位小数，四舍五入\\nBigDecimal c = a.divide(b, 4, RoundingMode.HALF_UP);\\nSystem.out.println(c);  // 输出 0.3333\\n```\\n\\n## 八种舍入模式\\n\\n### `UP`（远离0）\\n\\n👉 有小数就进位\\n\\n```\\n1.21 → 1.3  \\n-1.21 → -1.3\\n```\\n\\n---\\n\\n### 2️⃣ `DOWN`（向0截断）\\n\\n👉 直接截掉\\n\\n```\\n1.29 → 1.2  \\n-1.29 → -1.2\\n```\\n\\n---\\n\\n### 3️⃣ `CEILING`（向正无穷）\\n\\n👉 往大的方向走\\n\\n```\\n1.21 → 1.3  \\n-1.21 → -1.2\\n```\\n\\n---\\n\\n### 4️⃣ `FLOOR`（向负无穷）\\n\\n👉 往小的方向走\\n\\n```\\n1.21 → 1.2  \\n-1.21 → -1.3\\n```\\n\\n---\\n\\n### 5️⃣ `HALF_UP`（四舍五入 ⭐最常用）\\n\\n👉 ≥5进位\\n\\n```\\n1.25 → 1.3  \\n1.24 → 1.2\\n```\\n\\n---\\n\\n### 6️⃣ `HALF_DOWN`（五舍六入）\\n\\n👉 >5才进位\\n\\n```\\n1.25 → 1.2  \\n1.26 → 1.3\\n```\\n\\n---\\n\\n### 7️⃣ `HALF_EVEN`（银行家算法 ⭐重要）\\n\\n👉 5时看前一位是否为偶数\\n\\n```\\n1.25 → 1.2（2是偶数）  \\n1.35 → 1.4（3是奇数）\\n```\\n\\n👉 用于金融，减少累计误差\\n\\n---\\n\\n### 8️⃣ `UNNECESSARY`（不允许舍入）\\n\\n👉 如果需要舍入 → 直接报错\\n\\n```\\nBigDecimal a = new BigDecimal(\\\"10\\\");  \\nBigDecimal b = new BigDecimal(\\\"3\\\");  \\n  \\na.divide(b, 2, RoundingMode.UNNECESSARY); // ❗异常\\n```\\n\\n## 总结\\n\\n| 舍入模式        | 说明             | 2.5 结果 | -2.5 结果 |\\n|-----------------|------------------|----------|-----------|\\n| UP              | 远离零方向舍入   | 3        | -3        |\\n| DOWN            | 靠近零方向舍入   | 2        | -2        |\\n| CEILING         | 向正无穷方向舍入 | 3        | -2        |\\n| FLOOR           | 向负无穷方向舍入 | 2        | -3        |\\n| HALF_UP         | 四舍五入         | 3        | -3        |\\n| HALF_DOWN       | 五舍六入         | 2        | -2        |\\n| HALF_EVEN       | 银行家舍入法     | 2        | -2        |\\n| UNNECESSARY     | 不允许舍入       | 抛异常   | 抛异常    |\\n\\n# compareTo 比较而不是 equals\\n\\nBigDecimal的equals方法会同时比较值和精度(scale),所以:\\n\\n```java\\nBigDecimal a = new BigDecimal(\\\"1.0\\\");\\nBigDecimal b = new BigDecimal(\\\"1.00\\\");\\nSystem.out.println(a.equals(b));     // false，精度不同\\nSystem.out.println(a.compareTo(b));  // 0，值相等\\n```\\n\\n比较BigDecimal的大小,永远用compareTo,返回-1、0、1分别表示小于、等于、大于。\\n\\n# BigDecimal 的性能问题\\n\\nBigDecimal 是不可变类,每次运算都会创建新对象。在大量循环计算场景下,会产生很多临时对象,增加GC压力。\\n\\n```java\\n// 低效写法\\nBigDecimal sum = BigDecimal.ZERO;\\nfor (int i = 0; i < 100000; i++) {\\n    sum = sum.add(new BigDecimal(\\\"0.1\\\"));  // 每次循环创建 2 个对象\\n}\\n\\n// 优化写法：复用 BigDecimal 常量\\nBigDecimal increment = new BigDecimal(\\\"0.1\\\");\\nBigDecimal sum = BigDecimal.ZERO;\\nfor (int i = 0; i < 100000; i++) {\\n    sum = sum.add(increment);  // 每次循环只创建 1 个对象\\n}\\n```\\n\\n如果对性能要求极高,可以考虑用long存储最小单位。比如金额用“分”存储,1.23元存成123分,运算时用long,最后展示时再转换。很多支付系统都是这么干的。\\n\\n# MySQL中存储金额\\n\\n数据库层面，MySQL存储金额应该用DECIMAL类型，而不要用FLOAT或DOUBLE，原因同样是精度问题\\n\\n# BigDecimal内部如何存储数据的\\n\\n`BigDecimal` = 整数（intVal） + 小数位数（scale）\\n\\n可以理解成\\n```java\\n值 = intVal × 10^(-scale)\\n```\\n\\n核心源码：\\n```java\\nprivate final BigInteger intVal; // 大整数（核心）\\nprivate final int scale;         // 小数位数\\nprivate transient int precision; // 精度（位数）\\nprivate transient long intCompact; // 小数值优化（重要）\\n```\\n\\n举个例子\\n\\n```java\\nBigDecimal a = new BigDecimal(\\\"123.45\\\");\\n\\n//其内部为\\nintVal = 12345\\nscale  = 2\\n\\n//计算方式\\n12345 × 10^-2 = 123.45\\n```\\n\\n# BigDecimal.valueOf 和new BigDecimal(String)有什么区别?\\n\\n`BigDecimal.valueOf(double)`内部会先调用`Double.toString`把double转成字符串,再用字符串构造`BigDecimal`,所以结果是精确的。`new BigDecimal(String)`直接用字符串构造。两者结果一样,但valueOf有个小优化:对于0到10的整数有缓存,会直接返回缓存对象。\\n\\n```java\\n//new对象\\nBigDecimal a = new BigDecimal(\\\"0.1\\\");\\n//完全精准，直接按字符串解析，没有任何中间误差\\nintVal = 1\\nscale  = 1\\n\\n//valueOf（安全写法）\\nBigDecimal a = BigDecimal.valueOf(0.1);\\n//内部其实是\\nreturn new BigDecimal(Double.toString(0.1)); //先把 `double` 转成字符串，再调用 `new BigDecimal(String)`\\n\\n```\\n\\n# ValueOf（）\\n\\n`valueOf` 本质上是一个**静态工厂方法（static factory method）**，不是关键字，也不是特殊语法。\\n\\n## 在 `BigDecimal` 里的作用\\n\\n...(truncated)','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,91,1,'2026-07-15 15:32:44'),(68,2,'admin','KNOWLEDGE','审核通过','审核通过知识题目 ID=10','PUT','/api/admin/knowledge/10/approve','com.yimian.system.controller.admin.AdminKnowledgeController.approve','{\"id\":10}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,28,1,'2026-07-15 15:33:43'),(69,10,'ymyyds','PROFILE','修改个人信息','修改个人信息','PUT','/api/user/profile','com.yimian.system.controller.UserController.updateProfile','{\"dto\":{\"email\":null,\"phone\":null,\"nickname\":null,\"avatar\":\"/api/file/avatar/2026/07/15/cc04d221078a44d8b05c3995a0cd72ca.png\"}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,19,1,'2026-07-15 16:25:41'),(70,10,'ymyyds','PROFILE','修改个人信息','修改个人信息','PUT','/api/user/profile','com.yimian.system.controller.UserController.updateProfile','{\"dto\":{\"email\":null,\"phone\":null,\"nickname\":null,\"avatar\":\"/api/file/avatar/2026/07/15/2039f5e853cd47078f763b1ec49eb9d2.jpg\"}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,24,1,'2026-07-15 16:38:58'),(71,10,'ymyyds','FAVORITE','创建收藏夹','创建收藏夹: 测试','POST','/api/favorites/folders','com.yimian.system.controller.FavoriteController.createFolder','{\"dto\":{\"name\":\"测试\",\"description\":null,\"isPublic\":0,\"coverImage\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,12,1,'2026-07-15 17:14:43'),(72,10,'ymyyds','FAVORITE','收藏题目','收藏题目到收藏夹 ID=1','POST','/api/favorites/folders/1/items','com.yimian.system.controller.FavoriteController.addItem','{\"folderId\":1,\"dto\":{\"knowledgeId\":10}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,14,1,'2026-07-15 17:14:43'),(73,10,'ymyyds','BLOG','发布博客','发布博客: 测试12121','POST','/api/blogs','com.yimian.system.controller.BlogController.create','{\"dto\":{\"title\":\"测试12121\",\"content\":\"121231312十大大苏打实打实的十大大苏打萨达萨达飒飒大苏打是阿萨 大撒大撒的撒的撒的撒的撒大大打死大飒飒仨仨的撒仨仨的撒旦仨阿萨a\'s\",\"summary\":null,\"images\":[\"/api/file/blog/2026/07/15/3ceb07ed5e834c69ac627014e4312917.png\",\"/api/file/blog/2026/07/15/3cdc5cf8b1894da2bbe252c388c6462a.jpg\"],\"status\":1,\"refType\":null,\"refId\":null,\"topicIds\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,35,1,'2026-07-15 21:37:34'),(74,10,'ymyyds','BLOG','编辑博客','编辑博客 ID=3','PUT','/api/blogs/3','com.yimian.system.controller.BlogController.update','{\"id\":3,\"dto\":{\"title\":\"测试12121\",\"content\":\"121231312十大大苏打实打实的十大大苏打萨达萨达飒飒大苏打是阿萨 大撒大撒的撒的撒的撒的撒大大打死大飒飒仨仨的撒仨仨的撒旦仨阿萨a\'s\",\"summary\":\"121231312十大大苏打实打实的十大大苏打萨达萨达飒飒大苏打是阿萨 大撒大撒的撒的撒的撒的撒大大打死大飒飒仨仨的撒仨仨的撒旦仨阿萨a\'s\",\"images\":[\"/api/file/blog/2026/07/15/3ceb07ed5e834c69ac627014e4312917.png\",\"/api/file/blog/2026/07/15/3cdc5cf8b1894da2bbe252c388c6462a.jpg\"],\"status\":1,\"refType\":\"knowledge\",\"refId\":9,\"topicIds\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,32,1,'2026-07-15 21:52:26'),(75,10,'ymyyds','BLOG','收藏博客','收藏博客 ID=3 到收藏夹 ID=1','POST','/api/blogs/3/collect','com.yimian.system.controller.BlogController.collect','{\"id\":3,\"folderId\":1}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,11,1,'2026-07-15 22:41:16'),(76,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,20,1,'2026-07-16 13:07:57'),(77,10,'ymyyds','COMMENT','Create comment','Create comment','POST','/api/comments','com.yimian.system.controller.CommentController.create','{\"dto\":{\"targetType\":\"knowledge\",\"targetId\":10,\"content\":\"不错\",\"parentId\":null,\"replyToCommentId\":null,\"replyToUserId\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,30,1,'2026-07-16 13:36:13'),(78,10,'ymyyds','COMMENT','Create comment','Create comment','POST','/api/comments','com.yimian.system.controller.CommentController.create','{\"dto\":{\"targetType\":\"knowledge\",\"targetId\":10,\"content\":\"喜欢\",\"parentId\":1,\"replyToCommentId\":1,\"replyToUserId\":10}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,22,1,'2026-07-16 13:36:20'),(79,10,'ymyyds','COMMENT','Create comment','Create comment','POST','/api/comments','com.yimian.system.controller.CommentController.create','{\"dto\":{\"targetType\":\"knowledge\",\"targetId\":10,\"content\":\"是的\",\"parentId\":1,\"replyToCommentId\":2,\"replyToUserId\":10}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,81,1,'2026-07-16 14:19:42'),(80,10,'ymyyds','KNOWLEDGE','鐐硅禐棰樼洰','鐐硅禐/鍙栨秷鐐硅禐 棰樼洰 ID=10','POST','/api/knowledge/10/like','com.yimian.system.controller.KnowledgeController.toggleLike','{\"id\":10}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,58,1,'2026-07-16 14:48:12'),(81,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,11,1,'2026-07-16 14:48:24'),(82,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,5,1,'2026-07-16 14:48:25'),(83,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,6,1,'2026-07-16 14:48:36'),(84,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,5,1,'2026-07-16 14:48:36'),(85,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,19,1,'2026-07-16 15:00:26'),(86,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,6,1,'2026-07-16 15:00:27'),(87,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,6,1,'2026-07-16 15:00:28'),(88,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,10,1,'2026-07-16 15:00:28'),(89,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,11,1,'2026-07-16 15:00:29'),(90,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,7,1,'2026-07-16 15:00:31'),(91,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,9,1,'2026-07-16 15:04:37'),(92,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,23,1,'2026-07-16 15:04:37'),(93,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,6,1,'2026-07-16 15:04:39'),(94,10,'ymyyds','BLOG','点赞','点赞/取消点赞 博客 ID=3','POST','/api/blogs/3/like','com.yimian.system.controller.BlogController.toggleLike','{\"id\":3}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,10,1,'2026-07-16 15:04:40'),(95,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,144,1,'2026-07-16 15:27:36'),(96,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,127,1,'2026-07-16 15:52:55'),(97,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,71,1,'2026-07-16 15:55:02'),(98,2,'admin','USER','关注/取消关注用户','关注/取消关注用户 ID=10','POST','/api/user/10/follow','com.yimian.system.controller.UserController.toggleFollow','{\"id\":10}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,33,1,'2026-07-16 15:58:10'),(99,2,'admin','USER','关注/取消关注用户','关注/取消关注用户 ID=10','POST','/api/user/10/follow','com.yimian.system.controller.UserController.toggleFollow','{\"id\":10}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,7,1,'2026-07-16 15:58:11'),(100,2,'admin','USER','关注/取消关注用户','关注/取消关注用户 ID=10','POST','/api/user/10/follow','com.yimian.system.controller.UserController.toggleFollow','{\"id\":10}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,8,1,'2026-07-16 15:58:11'),(101,2,'admin','FAVORITE','创建收藏夹','创建收藏夹: java','POST','/api/favorites/folders','com.yimian.system.controller.FavoriteController.createFolder','{\"dto\":{\"name\":\"java\",\"description\":\"\",\"isPublic\":1,\"coverImage\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,9,1,'2026-07-16 16:09:20'),(102,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,105,1,'2026-07-16 16:30:52'),(103,2,'admin','BLOG','发布博客','发布博客: 3131231','POST','/api/blogs','com.yimian.system.controller.BlogController.create','{\"dto\":{\"title\":\"3131231\",\"content\":\"123123131231我3123123123123123123312312\",\"summary\":null,\"images\":null,\"status\":1,\"refType\":null,\"refId\":null,\"topicIds\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,16,1,'2026-07-16 16:35:00'),(104,2,'admin','COMMENT','Create comment','Create comment','POST','/api/comments','com.yimian.system.controller.CommentController.create','{\"dto\":{\"targetType\":\"knowledge\",\"targetId\":10,\"content\":\"何以为\",\"parentId\":1,\"replyToCommentId\":2,\"replyToUserId\":10}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,27,1,'2026-07-16 17:24:58'),(105,2,'admin','COMMENT','Toggle comment like','Toggle comment like ID=2','POST','/api/comments/2/like','com.yimian.system.controller.CommentController.toggleLike','{\"id\":2}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,62,1,'2026-07-16 17:25:09'),(106,NULL,NULL,'AUTH','用户端登录','用户 ymyyds 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"ymyyds\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,85,1,'2026-07-16 17:25:22'),(107,NULL,NULL,'AUTH','用户登录','用户 admin 尝试登录','POST','/api/auth/login','com.yimian.system.controller.AuthController.login','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.26090.1',NULL,200,'操作成功',NULL,231,1,'2026-07-16 17:36:31'),(108,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,198,1,'2026-07-16 18:00:12'),(109,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,98,1,'2026-07-16 18:04:57'),(110,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,111,1,'2026-07-16 18:06:43'),(111,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.6.0',NULL,200,'操作成功',NULL,273,1,'2026-07-16 18:09:25'),(112,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','curl/8.6.0',NULL,200,'操作成功',NULL,370,1,'2026-07-16 18:10:35'),(113,NULL,NULL,'AUTH','用户端登录','用户 ymyyds 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"ymyyds\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,133,1,'2026-07-16 18:11:14'),(114,NULL,NULL,'AUTH','用户端登录','用户 ymyyds 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"ymyyds\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,201,1,'2026-07-16 18:11:14'),(115,10,'ymyyds','COMMENT','Create comment','Create comment','POST','/api/comments','com.yimian.system.controller.CommentController.create','{\"dto\":{\"targetType\":\"knowledge\",\"targetId\":10,\"content\":\"你好\",\"parentId\":null,\"replyToCommentId\":null,\"replyToUserId\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,35,1,'2026-07-16 18:11:31'),(116,10,'ymyyds','COMMENT','Create comment','Create comment','POST','/api/comments','com.yimian.system.controller.CommentController.create','{\"dto\":{\"targetType\":\"knowledge\",\"targetId\":10,\"content\":\"你好\",\"parentId\":5,\"replyToCommentId\":5,\"replyToUserId\":10}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,44,1,'2026-07-16 18:11:38'),(117,10,'ymyyds','COMMENT','Create comment','Create comment','POST','/api/comments','com.yimian.system.controller.CommentController.create','{\"dto\":{\"targetType\":\"knowledge\",\"targetId\":10,\"content\":\"不错\",\"parentId\":5,\"replyToCommentId\":6,\"replyToUserId\":10}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,70,1,'2026-07-16 18:13:49'),(118,10,'ymyyds','BLOG','发布博客','发布博客: 2121212121','POST','/api/blogs','com.yimian.system.controller.BlogController.create','{\"dto\":{\"title\":\"2121212121\",\"content\":\"121212121212221212112121\",\"summary\":null,\"images\":[\"/api/file/blog/2026/07/16/ac71468b12904b9c9a7a5a263e418913.png\"],\"status\":1,\"refType\":null,\"refId\":null,\"topicIds\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36 Edg/150.0.0.0',NULL,200,'操作成功',NULL,43,1,'2026-07-16 19:41:13'),(119,NULL,NULL,'AUTH','用户端登录','用户 admin 登录用户端','POST','/api/auth/user/login','com.yimian.system.controller.AuthController.userLogin','{\"dto\":{\"username\":\"admin\",\"password\":\"***\",\"captchaKey\":null,\"captcha\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.129.0 Chrome/148.0.7778.280 Electron/42.6.0 Safari/537.36',NULL,200,'操作成功',NULL,212,1,'2026-07-16 19:53:34'),(120,2,'admin','BLOG','发布博客','发布博客: 测试擦撒大大实打实a','POST','/api/blogs','com.yimian.system.controller.BlogController.create','{\"dto\":{\"title\":\"测试擦撒大大实打实a\",\"content\":\"大苏打萨达萨达萨达萨达大撒大撒大撒撒打算的撒的撒萨达阿萨 \",\"summary\":null,\"images\":[\"/api/file/blog/2026/07/16/93cb2ca4af9646aa8699cc5b1a8b8c05.png\",\"/api/file/blog/2026/07/16/43370a5e042340a884d7f95f2499f234.png\"],\"status\":1,\"refType\":null,\"refId\":null,\"topicIds\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.129.0 Chrome/148.0.7778.280 Electron/42.6.0 Safari/537.36',NULL,200,'操作成功',NULL,25,1,'2026-07-16 19:54:23'),(121,2,'admin','BLOG','编辑博客','编辑博客 ID=6','PUT','/api/blogs/6','com.yimian.system.controller.BlogController.update','{\"id\":6,\"dto\":{\"title\":\"测试擦撒大大实打实a\",\"content\":\"大苏打萨达萨达萨达萨达大撒大撒大撒撒打算的撒的撒萨达阿萨\",\"summary\":\"大苏打萨达萨达萨达萨达大撒大撒大撒撒打算的撒的撒萨达阿萨\",\"images\":[\"/api/file/blog/2026/07/16/93cb2ca4af9646aa8699cc5b1a8b8c05.png\",\"/api/file/blog/2026/07/16/43370a5e042340a884d7f95f2499f234.png\",\"https://yi-mian-system.oss-cn-beijing.aliyuncs.com/blog/2026/07/16/9897d2ae451d44569bb2cbb2c5504b81.png\"],\"status\":1,\"refType\":null,\"refId\":null,\"topicIds\":null}}','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.129.0 Chrome/148.0.7778.280 Electron/42.6.0 Safari/537.36',NULL,200,'操作成功',NULL,63,1,'2026-07-16 19:59:00');
/*!40000 ALTER TABLE `sys_operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `perm_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `perm_name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限描述',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`perm_code`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (1,'user:list','查看用户列表',NULL,'2026-07-12 15:26:41','2026-07-12 15:26:41',0),(2,'user:view','查看用户详情',NULL,'2026-07-12 15:26:41','2026-07-12 15:26:41',0),(3,'user:create','新增用户',NULL,'2026-07-12 15:26:41','2026-07-12 15:26:41',0),(4,'user:edit','编辑用户',NULL,'2026-07-12 15:26:41','2026-07-12 15:26:41',0),(5,'user:delete','删除用户',NULL,'2026-07-12 15:26:41','2026-07-12 15:26:41',0),(6,'user:reset-password','重置密码','管理员无需旧密码直接重置用户密码','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(7,'user:assign-roles','分配角色','管理员给用户分配/更换角色','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(13,'role:list','查看角色列表','管理员查看所有角色','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(14,'role:view','查看角色详情','管理员查看单个角色详情及权限','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(15,'role:create','新增角色','管理员创建新角色','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(16,'role:edit','编辑角色','管理员修改角色名称/描述/排序','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(17,'role:delete','删除角色','管理员逻辑删除角色','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(18,'role:assign-perms','分配权限','管理员给角色分配/更换权限','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(19,'perm:list','查看权限列表','管理员查看所有权限','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(20,'perm:view','查看权限详情','管理员查看单个权限详情','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(21,'perm:create','新增权限','管理员创建新权限','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(22,'perm:edit','编辑权限','管理员修改权限名称/描述','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(23,'perm:delete','删除权限','管理员逻辑删除权限','2026-07-12 18:19:15','2026-07-12 18:19:15',0),(42,'self:profile','个人信息','查看和修改自己的昵称/邮箱/手机','2026-07-12 18:23:46','2026-07-12 18:23:46',0),(43,'self:change-password','修改密码','修改自己的登录密码','2026-07-12 18:23:46','2026-07-12 18:23:46',0),(44,'log:list','查看操作日志','管理员查看操作日志列表','2026-07-12 19:25:34','2026-07-12 19:25:34',0),(45,'knowledge:direct-upload','直接上传题目','跳过审核直接发布题目（需此权限）','2026-07-14 15:17:13','2026-07-14 15:17:13',0),(46,'knowledge:audit','审核题目','审核知识题目（通过/拒绝）','2026-07-14 15:17:13','2026-07-14 15:17:13',0),(47,'tag:manage','标签管理','标签CRUD权限','2026-07-14 21:03:23','2026-07-14 21:03:23',0),(48,'knowledge:submit','提交题目','提交题目审核','2026-07-14 21:16:04','2026-07-14 21:16:04',0),(49,'knowledge:edit','编辑题目','编辑自己提交的题目','2026-07-14 21:16:04','2026-07-14 21:16:04',0),(50,'knowledge:delete','删除题目','删除自己提交的题目','2026-07-14 21:16:04','2026-07-14 21:16:04',0),(51,'knowledge:list','查看题目列表','查看题目列表','2026-07-14 21:16:04','2026-07-14 21:16:04',0),(52,'knowledge:view','查看题目详情','查看题目详情','2026-07-14 21:16:04','2026-07-14 21:16:04',0),(53,'tag:list','查看标签列表','查看标签列表','2026-07-14 21:16:04','2026-07-14 21:16:04',0),(54,'tag:create','新增标签','新增标签','2026-07-14 21:16:04','2026-07-14 21:16:04',0),(55,'tag:edit','编辑标签','编辑标签','2026-07-14 21:16:04','2026-07-14 21:16:04',0),(56,'tag:delete','删除标签','删除标签','2026-07-14 21:16:04','2026-07-14 21:16:04',0),(57,'favorite:create','创建收藏夹','创建个人收藏夹','2026-07-15 17:02:21','2026-07-15 17:02:21',0),(58,'favorite:edit','编辑收藏夹','编辑自己的收藏夹','2026-07-15 17:02:21','2026-07-15 17:02:21',0),(59,'favorite:delete','删除收藏夹','删除自己的收藏夹','2026-07-15 17:02:21','2026-07-15 17:02:21',0),(60,'favorite:item:add','收藏题目','将知识题目加入自己的收藏夹','2026-07-15 17:02:21','2026-07-15 17:02:21',0),(61,'favorite:item:delete','取消收藏','从自己的收藏夹移除知识题目','2026-07-15 17:02:21','2026-07-15 17:02:21',0),(62,'favorite:list','查看收藏夹列表','查看公开收藏夹和自己的收藏夹','2026-07-15 17:02:21','2026-07-15 17:02:21',0),(63,'favorite:view','查看收藏夹详情','查看公开收藏夹详情和自己的收藏状态','2026-07-15 17:02:21','2026-07-15 17:02:21',0),(64,'blog:create','发布博客','创建博客或保存草稿','2026-07-15 18:12:49','2026-07-15 18:13:07',0),(65,'blog:edit','编辑博客','编辑自己的博客','2026-07-15 18:12:49','2026-07-15 18:13:07',0),(66,'blog:delete','删除博客','删除自己的博客','2026-07-15 18:12:49','2026-07-15 18:13:07',0),(67,'blog:list','查看博客列表','查看已发布博客列表','2026-07-15 18:12:49','2026-07-15 18:13:07',0),(68,'blog:view','查看博客详情','查看已发布博客详情','2026-07-15 18:12:49','2026-07-15 18:13:07',0),(74,'topic:list','查看话题','查看话题列表','2026-07-15 21:35:02','2026-07-15 21:35:02',0),(75,'topic:create','新增话题','管理员新增话题','2026-07-15 21:35:02','2026-07-15 21:35:02',0),(76,'topic:edit','编辑话题','管理员编辑话题','2026-07-15 21:35:02','2026-07-15 21:35:02',0),(77,'topic:delete','删除话题','管理员删除话题','2026-07-15 21:35:02','2026-07-15 21:35:02',0),(78,'blog:like','Like blog','Like or unlike blog','2026-07-15 21:47:33','2026-07-16 15:57:31',0),(79,'blog:collect','Collect blog','Collect blog to favorite folder','2026-07-15 21:47:33','2026-07-16 15:57:31',0),(80,'comment:view','View comments','View comments','2026-07-16 13:32:58','2026-07-16 13:32:58',0),(81,'comment:create','Create comments','Create comments and replies','2026-07-16 13:32:58','2026-07-16 13:32:58',0),(82,'comment:like','Like comments','Like or unlike comments','2026-07-16 13:32:58','2026-07-16 13:32:58',0),(83,'knowledge:like','Like knowledge','Like or unlike knowledge','2026-07-16 14:46:01','2026-07-16 14:46:01',0),(84,'user:view-public','View public user profile','View public user profile','2026-07-16 15:54:38','2026-07-16 15:54:38',0),(85,'user:follow','Follow user','Follow or unfollow user','2026-07-16 15:54:38','2026-07-16 15:54:38',0),(100,'notification:list','娑堟伅閫氱煡-鍒楄〃','娑堟伅閫氱煡鍒楄〃鏌ヨ?','2026-07-16 18:01:49','2026-07-16 18:01:49',0),(101,'notification:read','娑堟伅閫氱煡-鏍囪?宸茶?','鏍囪?娑堟伅宸茶?','2026-07-16 18:01:49','2026-07-16 18:01:49',0),(102,'notification:delete','娑堟伅閫氱煡-鍒犻櫎','鍒犻櫎娑堟伅閫氱煡','2026-07-16 18:01:49','2026-07-16 18:01:49',0);
/*!40000 ALTER TABLE `sys_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `role_name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色描述',
  `sort` int DEFAULT '0' COMMENT '排序号',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'ROLE_ADMIN','管理员',NULL,1,'2026-07-12 15:26:41','2026-07-12 15:26:41',0),(2,'ROLE_INTERVIEWER','面试官',NULL,2,'2026-07-12 15:26:41','2026-07-12 15:26:41',0),(3,'ROLE_CANDIDATE','候选人',NULL,3,'2026-07-12 15:26:41','2026-07-12 15:26:41',0),(4,'ROLE_USER','普通用户',NULL,4,'2026-07-12 15:26:41','2026-07-12 15:26:41',0);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_permission`
--

DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `perm_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`,`perm_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_perm_id` (`perm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=157 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES (4,1,1),(5,1,2),(1,1,3),(3,1,4),(2,1,5),(11,1,6),(12,1,7),(13,1,13),(14,1,14),(15,1,15),(16,1,16),(17,1,17),(18,1,18),(19,1,19),(20,1,20),(21,1,21),(22,1,22),(23,1,23),(27,1,42),(28,1,43),(38,1,44),(39,1,45),(40,1,46),(41,1,47),(50,1,48),(46,1,49),(43,1,50),(48,1,51),(52,1,52),(60,1,53),(57,1,54),(59,1,55),(58,1,56),(67,1,57),(71,1,58),(69,1,59),(73,1,60),(75,1,61),(77,1,62),(79,1,63),(82,1,64),(86,1,65),(84,1,66),(88,1,67),(90,1,68),(97,1,74),(100,1,75),(102,1,76),(101,1,77),(103,1,78),(104,1,79),(110,1,80),(112,1,81),(111,1,82),(117,1,83),(120,1,84),(121,1,85),(154,1,100),(156,1,101),(152,1,102),(8,2,1),(9,2,2),(140,2,78),(141,2,79),(137,2,80),(139,2,81),(138,2,82),(136,2,83),(127,2,84),(128,2,85),(31,3,42),(32,3,43),(146,3,78),(147,3,79),(143,3,80),(145,3,81),(144,3,82),(142,3,83),(129,3,84),(130,3,85),(33,4,42),(34,4,43),(44,4,45),(49,4,48),(45,4,49),(42,4,50),(47,4,51),(51,4,52),(66,4,57),(70,4,58),(68,4,59),(72,4,60),(74,4,61),(76,4,62),(78,4,63),(81,4,64),(85,4,65),(83,4,66),(87,4,67),(89,4,68),(98,4,74),(105,4,78),(106,4,79),(113,4,80),(115,4,81),(114,4,82),(118,4,83),(122,4,84),(123,4,85),(153,4,100),(155,4,101),(151,4,102);
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_tag`
--

DROP TABLE IF EXISTS `sys_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称（如"Java"）',
  `sort` int DEFAULT '0' COMMENT '排序号',
  `color` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签颜色（前端展示用，如#6366f1）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_tag`
--

LOCK TABLES `sys_tag` WRITE;
/*!40000 ALTER TABLE `sys_tag` DISABLE KEYS */;
INSERT INTO `sys_tag` VALUES (1,'Java',1,'#e74c3c','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(2,'Java基础',2,'#e74c3c','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(3,'JVM',3,'#e74c3c','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(4,'多线程',4,'#e74c3c','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(5,'集合框架',5,'#e74c3c','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(6,'Spring',6,'#6db33f','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(7,'Spring Boot',7,'#6db33f','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(8,'Spring Cloud',8,'#6db33f','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(9,'Spring MVC',9,'#6db33f','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(10,'MyBatis',10,'#6db33f','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(11,'MySQL',11,'#336791','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(12,'Redis',12,'#336791','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(13,'PostgreSQL',13,'#336791','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(14,'RabbitMQ',14,'#f39c12','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(15,'Kafka',15,'#f39c12','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(16,'Elasticsearch',16,'#f39c12','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(17,'微服务',17,'#9b59b6','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(18,'RPC',18,'#9b59b6','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(19,'分布式事务',19,'#9b59b6','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(20,'计算机网络',20,'#3498db','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(21,'操作系统',21,'#1abc9c','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(22,'数据结构',22,'#e67e22','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(23,'设计模式',23,'#95a5a6','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(24,'前端',24,'#2ecc71','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(25,'DevOps',25,'#34495e','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(26,'Python',26,'#3776ab','2026-07-14 14:40:53','2026-07-14 14:40:53',0),(27,'Go',27,'#00ADD8','2026-07-14 20:38:20','2026-07-14 20:38:20',NULL),(28,'Docker',0,'#6366f1','2026-07-14 20:46:50','2026-07-14 20:46:50',NULL);
/*!40000 ALTER TABLE `sys_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_topic`
--

DROP TABLE IF EXISTS `sys_topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_topic` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '话题名称（如"Java并发"）',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '话题简介',
  `color` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '话题颜色（前端展示用，如#6366f1）',
  `sort` int DEFAULT '0' COMMENT '排序号',
  `blog_count` int DEFAULT '0' COMMENT '关联博客数（冗余，方便排序）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_topic_name` (`name`),
  KEY `idx_sort` (`sort`),
  KEY `idx_blog_count` (`blog_count`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='话题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_topic`
--

LOCK TABLES `sys_topic` WRITE;
/*!40000 ALTER TABLE `sys_topic` DISABLE KEYS */;
INSERT INTO `sys_topic` VALUES (1,'Java 基础','Java 语言核心概念与基础语法','#e74c3c',1,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(2,'JVM 与性能','JVM 内存模型、GC 调优与性能诊断','#e74c3c',2,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(3,'并发编程','多线程、锁机制与并发工具类','#e74c3c',3,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(4,'Spring 生态','Spring Boot / Cloud / MVC 等框架实践','#6db33f',4,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(5,'数据库','MySQL、PostgreSQL 与 SQL 优化','#336791',5,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(6,'缓存与队列','Redis、RabbitMQ、Kafka 等中间件','#f39c12',6,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(7,'微服务与架构','分布式系统、RPC、DDD 与系统设计','#9b59b6',7,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(8,'计算机网络','HTTP、TCP/IP、网络排障','#3498db',8,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(9,'操作系统','Linux、进程线程与 IO 模型','#1abc9c',9,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(10,'数据结构与算法','常见数据结构、排序与算法题复盘','#e67e22',10,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(11,'设计模式','GOF 设计模式与工程实践','#95a5a6',11,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(12,'前端技术','Vue、React、TypeScript 等','#2ecc71',12,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(13,'DevOps','CI/CD、容器化与运维实践','#34495e',13,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(14,'面试复盘','面试经历、追问分析与经验总结','#6366f1',14,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(15,'工程实践','代码规范、重构、测试与团队协作','#fd79a8',15,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0),(16,'AI 与 LLM','大模型、RAG、Agent 与 AI 工程化','#a29bfe',16,0,'2026-07-15 21:35:02','2026-07-15 21:35:02',0);
/*!40000 ALTER TABLE `sys_topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'BCrypt加密密码',
  `email` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `status` tinyint DEFAULT '1' COMMENT '状态:0禁用,1启用,2锁定',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后登录IP',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'yasdasda','$2a$10$4AQuH0e8hD/v2eayxf.fTe/qZHCbFCKob.mt8y21e8wwfj5kL/MqO','o97fmn40@vip.qq.com','024 5221 8141','寻强',NULL,1,NULL,NULL,NULL,NULL,1),(2,'admin','$2a$10$8/Yu.fL2QEyK24yAVX9J9uvqkW0G92PnfYMAyyQsOlfiEJCRbeQ9.','admin@gmail.com','123123','管理员','/api/file/avatar/2026/07/13/68445b14cb67463cbe87da73832e4740.jpg',1,'2026-07-16 19:53:34','127.0.0.1',NULL,'2026-07-16 19:53:34',0),(7,'test2','$2a$10$wkUnQO7mAkldi577Uj50oe3adDleAZUY9Acno2GQhk8km5TCI7tTa','test@gmail.com','1212121','strin',NULL,1,NULL,NULL,NULL,NULL,0),(8,'test222','$2a$10$I70dreSF8zP.yIFY5yZLNuhUOnSrpJmnqJWR222ccrhxwn.i9ZP3G',NULL,NULL,'new',NULL,1,NULL,NULL,'2026-07-13 15:35:50','2026-07-13 15:35:50',0),(9,'yanmoyyds','$2a$10$55Xq1ovJTC0pJ8JreRrb5.PmJ7lBqM0DVtn9gkYvpK4/jnHw8zwO2',NULL,NULL,'yanmoyyds',NULL,1,'2026-07-13 17:18:54','127.0.0.1','2026-07-13 17:18:42','2026-07-13 17:18:54',0),(10,'ymyyds','$2a$10$dNSpHtNlL6mftOEnIyP5veNSX8uCsqEkxhv/WL/9rtPgigCaIk6sm',NULL,NULL,'ymyyds','/api/file/avatar/2026/07/15/2039f5e853cd47078f763b1ec49eb9d2.jpg',1,'2026-07-16 18:11:15','127.0.0.1','2026-07-15 15:26:24','2026-07-16 18:11:14',0);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_follow`
--

DROP TABLE IF EXISTS `sys_user_follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_follow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `follower_id` bigint NOT NULL COMMENT 'Follower user ID',
  `followee_id` bigint NOT NULL COMMENT 'Followed user ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  `deleted` tinyint DEFAULT '0' COMMENT 'Logic delete flag',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_followee` (`follower_id`,`followee_id`),
  KEY `idx_user_follow_follower` (`follower_id`,`deleted`),
  KEY `idx_user_follow_followee` (`followee_id`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User follow relation table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_follow`
--

LOCK TABLES `sys_user_follow` WRITE;
/*!40000 ALTER TABLE `sys_user_follow` DISABLE KEYS */;
INSERT INTO `sys_user_follow` VALUES (1,2,10,'2026-07-16 15:58:10','2026-07-16 15:58:11',0);
/*!40000 ALTER TABLE `sys_user_follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (5,1,1),(3,2,1),(2,2,4),(4,7,4),(6,8,4),(7,9,4),(8,10,4);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-17 14:41:18
