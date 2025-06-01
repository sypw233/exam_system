-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: online_exam_system
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `course_selections`
--

DROP TABLE IF EXISTS `course_selections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_selections` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `selection_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `course_selections_ibfk_1` (`student_id`),
  KEY `course_selections_ibfk_2` (`course_id`),
  CONSTRAINT `course_selections_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `course_selections_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_selections`
--

LOCK TABLES `course_selections` WRITE;
/*!40000 ALTER TABLE `course_selections` DISABLE KEYS */;
INSERT INTO `course_selections` VALUES (2,5,2,'2024-12-11 00:36:41'),(3,5,3,'2024-12-11 00:36:41'),(4,5,4,'2024-12-11 00:36:41'),(6,20,1,'2024-12-11 09:50:49'),(7,18,1,'2024-12-12 00:53:45');
/*!40000 ALTER TABLE `course_selections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `teacher_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `courses_ibfk_1` (`teacher_id`),
  CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (1,'计算机科学导论',1,'2024-12-11 00:36:41'),(2,'数据结构与算法',6,'2024-12-11 00:36:41'),(3,'数据库系统原理',1,'2024-12-11 00:36:41'),(4,'操作系统',1,'2024-12-11 00:36:41'),(5,'人工智能基础',1,'2024-12-11 00:36:41');
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_questions`
--

DROP TABLE IF EXISTS `exam_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_questions` (
  `exam_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  PRIMARY KEY (`exam_id`,`question_id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `exam_questions_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE,
  CONSTRAINT `exam_questions_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_questions`
--

LOCK TABLES `exam_questions` WRITE;
/*!40000 ALTER TABLE `exam_questions` DISABLE KEYS */;
INSERT INTO `exam_questions` VALUES (60,1),(62,1),(60,2),(62,2),(60,3),(60,4),(60,5),(60,6),(63,6),(60,7),(63,7),(60,8),(60,9),(60,10),(60,11),(60,12),(60,13),(61,13),(60,14),(61,14),(60,15),(61,15),(60,16),(61,16),(60,17),(60,18),(60,19),(60,20),(60,21),(60,22),(60,23),(61,23),(60,24),(61,24),(60,25),(60,26),(60,27),(60,28),(60,29),(60,30),(60,31),(60,32),(60,33),(60,34),(60,35),(60,36),(64,36),(60,37),(64,37),(60,38),(64,38);
/*!40000 ALTER TABLE `exam_questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_submissions`
--

DROP TABLE IF EXISTS `exam_submissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_submissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `answers` json DEFAULT NULL,
  `submit_score` int DEFAULT NULL COMMENT '正确题目数',
  `submit_detail` json DEFAULT NULL,
  `submit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `exam_submissions_ibfk_1` (`user_id`),
  KEY `idx_exam_id_user_id` (`exam_id`,`user_id`),
  CONSTRAINT `exam_submissions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `exam_submissions_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_submissions`
--

LOCK TABLES `exam_submissions` WRITE;
/*!40000 ALTER TABLE `exam_submissions` DISABLE KEYS */;
INSERT INTO `exam_submissions` VALUES (2,1,61,NULL,NULL,NULL,'2024-12-08 02:43:24'),(3,1,62,NULL,NULL,NULL,'2024-12-08 02:45:00'),(4,1,62,'{\"1\": \"b\", \"2\": \"b\"}',NULL,NULL,'2024-12-08 03:01:20'),(5,1,61,'{\"13\": \"a\", \"14\": \"false\", \"15\": \"b\", \"16\": \"b\", \"23\": \"b\", \"24\": [\"c\", \"b\"]}',NULL,NULL,'2024-12-08 03:01:39'),(6,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"b\", \"16\": \"b\", \"23\": \"b\", \"24\": [\"a\", \"b\"]}',NULL,NULL,'2024-12-09 19:11:44'),(7,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"b\", \"16\": \"b\", \"23\": \"b\", \"24\": [\"a\", \"b\"]}',NULL,NULL,'2024-12-09 19:11:46'),(8,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"a\", \"16\": \"b\", \"23\": \"a\", \"24\": [\"b\"]}',NULL,NULL,'2024-12-09 19:12:29'),(9,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"b\", \"16\": \"b\", \"23\": \"a\", \"24\": [\"c\"]}',NULL,NULL,'2024-12-09 19:14:58'),(10,4,61,'{\"13\": \"a\", \"14\": \"false\", \"15\": \"a\", \"16\": \"c\", \"23\": \"b\", \"24\": [\"a\", \"c\"]}',NULL,NULL,'2024-12-09 19:15:40'),(11,4,61,'{\"13\": \"a\", \"14\": \"false\", \"15\": \"a\", \"16\": \"b\", \"23\": \"a\", \"24\": [\"c\", \"b\"]}',NULL,NULL,'2024-12-09 23:05:58'),(12,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"a\", \"16\": \"a\", \"23\": \"a\", \"24\": [\"b\", \"c\"]}',2,NULL,'2024-12-09 23:16:16'),(13,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"a\", \"16\": \"a\", \"23\": \"b\", \"24\": [\"c\", \"b\"]}',3,NULL,'2024-12-09 23:19:02'),(14,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"a\", \"16\": \"a\", \"23\": \"a\", \"24\": [\"a\", \"b\"]}',3,'{\"13\": true, \"14\": true, \"15\": false, \"16\": false, \"23\": false, \"24\": true}','2024-12-09 23:20:42'),(15,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"a\", \"16\": \"a\", \"23\": \"b\", \"24\": [\"c\"]}',3,'{\"13\": true, \"14\": true, \"15\": false, \"16\": false, \"23\": true, \"24\": false}','2024-12-09 23:21:27'),(16,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"a\", \"16\": \"a\", \"23\": \"a\", \"24\": [\"a\", \"b\"]}',3,'{\"13\": true, \"14\": true, \"15\": false, \"16\": false, \"23\": false, \"24\": true}','2024-12-09 23:26:45'),(17,4,61,'{\"13\": \"a\", \"14\": \"false\", \"15\": \"a\", \"16\": \"b\", \"23\": \"b\", \"24\": [\"b\", \"c\"]}',3,'{\"13\": true, \"14\": false, \"15\": false, \"16\": true, \"23\": true, \"24\": false}','2024-12-09 23:27:11'),(18,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"a\", \"16\": \"b\", \"23\": \"a\", \"24\": [\"b\"]}',3,'{\"13\": true, \"14\": true, \"15\": false, \"16\": true, \"23\": false, \"24\": false}','2024-12-09 23:27:37'),(19,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"a\", \"16\": \"a\", \"23\": \"a\", \"24\": [\"a\"]}',32,'{\"13\": true, \"14\": true, \"15\": false, \"16\": false, \"23\": false, \"24\": false}','2024-12-09 23:32:03'),(20,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"b\", \"16\": \"b\", \"23\": \"b\", \"24\": [\"a\"]}',64,'{\"13\": true, \"14\": true, \"15\": false, \"16\": true, \"23\": true, \"24\": false}','2024-12-09 23:32:45'),(21,4,61,'{\"13\": \"a\", \"14\": \"true\", \"15\": \"b\", \"16\": \"a\", \"23\": \"a\", \"24\": [\"a\"]}',32,'{\"13\": true, \"14\": true, \"15\": false, \"16\": false, \"23\": false, \"24\": false}','2024-12-09 23:33:59'),(22,4,61,'{\"13\": \"a\", \"14\": \"false\", \"15\": \"a\", \"16\": \"b\", \"23\": \"b\", \"24\": [\"b\"]}',48,'{\"13\": true, \"14\": false, \"15\": false, \"16\": true, \"23\": true, \"24\": false}','2024-12-09 23:38:55'),(23,4,64,'{\"36\": [\"c\"], \"37\": \"1111是多大的\", \"38\": \"a\"}',33,'{\"36\": false, \"37\": false, \"38\": true}','2024-12-09 23:56:58');
/*!40000 ALTER TABLE `exam_submissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exams`
--

DROP TABLE IF EXISTS `exams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exams` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `creator_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `total_score` int NOT NULL,
  `course_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `exams_ibfk_1` (`creator_id`),
  KEY `exams_ibfk_2` (`course_id`),
  CONSTRAINT `exams_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `exams_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exams`
--

LOCK TABLES `exams` WRITE;
/*!40000 ALTER TABLE `exams` DISABLE KEYS */;
INSERT INTO `exams` VALUES (32,'编程基础考试','测试学生对编程基础的掌握程度','2024-12-20 14:00:00','2024-12-20 16:00:00',5,'2024-12-05 19:59:39',80,1),(33,'数据库原理考试','包含数据库设计、SQL 查询及优化','2024-12-21 09:00:00','2024-12-21 11:00:00',5,'2024-12-05 19:59:39',90,1),(34,'软件工程期中考试','软件工程课程期中的测试，考察设计模式与架构知识','2024-11-10 10:00:00','2024-11-10 12:00:00',5,'2024-12-05 19:59:39',85,1),(35,'计算机网络实验考试','计算机网络课程的实验考试，涉及网络协议分析','2024-11-15 13:00:00','2024-11-15 15:00:00',5,'2024-12-05 19:59:39',75,1),(36,'人工智能导论','人工智能基础，考察机器学习、深度学习基本知识','2024-12-25 09:00:00','2024-12-25 11:00:00',5,'2024-12-05 19:59:39',95,1),(37,'操作系统期末考试','操作系统课程期末考试，包含进程管理、内存管理等','2024-12-18 10:00:00','2024-12-18 12:00:00',7,'2024-12-05 19:59:39',85,1),(38,'数据结构与算法考试','数据结构与算法的测试，包含排序、查找等内容','2024-12-22 13:00:00','2024-12-22 15:00:00',5,'2024-12-05 19:59:39',90,1),(39,'前端开发考试','考察前端开发基础，包括 HTML、CSS 和 JavaScript','2024-12-10 14:00:00','2024-12-10 16:00:00',9,'2024-12-05 19:59:39',80,1),(40,'计算机组成原理考试','计算机组成原理考试，考察计算机硬件架构','2024-12-14 08:00:00','2024-12-14 10:00:00',10,'2024-12-05 19:59:39',88,1),(45,'111','221','2024-12-07 09:17:28','2024-12-07 09:17:30',1,'2024-12-07 17:25:58',100,1),(46,'111','221','2024-12-07 09:17:28','2024-12-07 09:17:30',1,'2024-12-07 17:26:44',100,1),(48,'111','221','2024-12-07 09:17:28','2024-12-07 09:17:30',1,'2024-12-07 17:34:01',100,1),(50,'ssss','111','2024-12-07 09:17:28','2024-12-07 09:17:30',1,'2024-12-07 17:39:47',100,1),(51,'1','','2024-10-09 16:00:00','2024-12-07 09:40:48',1,'2024-12-07 17:41:16',100,1),(52,'带大的','','2024-12-07 10:31:10','2024-12-07 10:31:46',1,'2024-12-07 18:31:55',100,1),(53,'11','','2024-12-07 10:32:43','2024-12-07 10:32:44',1,'2024-12-07 18:32:48',100,1),(54,'t1','','2024-12-07 10:37:10','2024-12-07 10:37:11',1,'2024-12-07 18:37:16',100,1),(55,'1','1','2024-12-07 10:42:35','2024-12-07 10:42:36',1,'2024-12-07 18:42:40',100,1),(56,'1','','2024-12-07 10:45:44','2024-12-07 10:45:45',1,'2024-12-07 18:45:49',100,1),(57,'1','2','2024-12-07 10:45:44','2024-12-07 10:45:45',1,'2024-12-07 19:20:26',100,1),(60,'test1','ttt','2024-12-07 14:44:35','2024-12-07 14:44:36',1,'2024-12-07 22:44:45',100,1),(61,'test2','','2024-12-07 15:20:27','2024-12-07 15:20:28',1,'2024-12-07 23:20:36',100,1),(62,'test3','','2024-12-07 18:43:51','2024-12-07 18:43:52',1,'2024-12-08 02:43:57',100,1),(63,'kk','','2024-12-08 14:52:28','2024-12-08 14:52:29',1,'2024-12-08 22:52:33',100,1),(64,'tttttt','','2024-12-09 15:41:22','2024-12-09 15:41:23',4,'2024-12-09 23:41:24',100,1);
/*!40000 ALTER TABLE `exams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8mb4_general_ci NOT NULL,
  `type` enum('single','multiple','true_false','fill_blank','short_answer') COLLATE utf8mb4_general_ci NOT NULL,
  `options` json DEFAULT NULL,
  `answer` text COLLATE utf8mb4_general_ci NOT NULL,
  `difficulty` enum('easy','medium','hard') COLLATE utf8mb4_general_ci NOT NULL,
  `category` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `creator_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `creator_id` (`creator_id`),
  CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
INSERT INTO `questions` VALUES (1,'Java 中如何声明一个整数变量？','single','{\"a\": \"int x = 10;\", \"b\": \"10 = int x;\", \"c\": \"int 10 = x;\", \"d\": \"x = 10 int;\"}','a','easy','Java基础','2024-12-07 22:42:22',1),(2,'Java 中哪个类可以用来处理文本文件的读取和写入？','single','{\"a\": \"File\", \"b\": \"BufferedReader\", \"c\": \"Scanner\", \"d\": \"FileReader\"}','b','medium','Java基础','2024-12-07 22:42:22',1),(3,'以下哪个是 Java 的基础数据类型？','multiple','{\"a\": \"int\", \"b\": \"Integer\", \"c\": \"String\", \"d\": \"Double\"}','a,d','easy','Java基础','2024-12-07 22:42:22',1),(4,'在 Java 中，哪个关键字可以用于继承？','single','{\"a\": \"extends\", \"b\": \"implements\", \"c\": \"interface\", \"d\": \"super\"}','a','easy','Java基础','2024-12-07 22:42:22',1),(5,'Java 中是否可以直接创建一个接口的对象？','true_false',NULL,'false','medium','Java基础','2024-12-07 22:42:22',1),(6,'Java 中的 String 类是否可变？','true_false',NULL,'false','medium','Java基础','2024-12-07 22:42:22',1),(7,'解释多态的概念，并举一个例子。','short_answer',NULL,'多态是指同一方法调用可以根据对象的不同而表现出不同的行为。例如，一个父类引用指向子类对象，调用子类的重写方法。','medium','Java面向对象','2024-12-07 22:42:22',1),(8,'多态的实现方式有哪些？','short_answer',NULL,'多态的实现方式有方法重载和方法重写。','medium','Java面向对象','2024-12-07 22:42:22',1),(9,'Java 中的 final 关键字不能修饰哪些内容？','multiple','{\"a\": \"类\", \"b\": \"方法\", \"c\": \"变量\", \"d\": \"对象\"}','d','medium','Java基础','2024-12-07 22:42:22',1),(10,'Java 中，String 和 StringBuffer 的区别是什么？','short_answer',NULL,'String 是不可变类，StringBuffer 是可变类，StringBuffer 适合做大量字符串操作。','hard','Java面向对象','2024-12-07 22:42:22',1),(11,'以下哪个选项是正确的 Java 数组声明方式？','single','{\"a\": \"int[] arr;\", \"b\": \"arr[] int;\", \"c\": \"int arr[];\", \"d\": \"int arr;[]\"}','a','easy','Java基础','2024-12-07 22:42:22',1),(12,'Java 中哪个方法可以用来获取当前时间的毫秒表示？','single','{\"a\": \"System.currentTimeMillis()\", \"b\": \"System.getCurrentTime()\", \"c\": \"Date.now()\", \"d\": \"Time.now()\"}','a','medium','Java基础','2024-12-07 22:42:22',1),(13,'Java 中，异常的捕获顺序应该是怎样的？','single','{\"a\": \"先捕获子类异常，再捕获父类异常\", \"b\": \"先捕获父类异常，再捕获子类异常\", \"c\": \"捕获顺序无关紧要\", \"d\": \"不能捕获异常\"}','a','medium','Java异常处理','2024-12-07 22:42:22',1),(14,'以下哪项描述是正确的？','true_false',NULL,'true','easy','Java基础','2024-12-07 22:42:22',1),(15,'Java 中如何创建一个线程？','single','{\"a\": \"继承 Thread 类并重写 run 方法\", \"b\": \"实现 Runnable 接口\", \"c\": \"使用 ExecutorService 类\", \"d\": \"以上都是\"}','d','hard','Java线程','2024-12-07 22:42:22',1),(16,'在 Java 中，如何声明一个常量？','single','{\"a\": \"const int x = 10;\", \"b\": \"final int x = 10;\", \"c\": \"immutable int x = 10;\", \"d\": \"int x = final 10;\"}','b','easy','Java基础','2024-12-07 22:42:22',1),(17,'判断以下语句的正确性：在 Java 中，父类和子类可以有相同的字段名。','true_false',NULL,'true','medium','Java面向对象','2024-12-07 22:42:22',1),(18,'以下哪个选项是 Java 中的集合类？','multiple','{\"a\": \"ArrayList\", \"b\": \"LinkedList\", \"c\": \"TreeSet\", \"d\": \"HashMap\"}','a,b,c,d','medium','Java集合','2024-12-07 22:42:22',1),(19,'简述 Java 中的泛型使用。','short_answer',NULL,'泛型允许在类、接口和方法中使用类型参数，可以增加代码的通用性，避免强制类型转换。','hard','Java泛型','2024-12-07 22:42:22',1),(20,'以下哪种方式不能用来实现线程同步？','single','{\"a\": \"synchronized 关键字\", \"b\": \"Lock 接口\", \"c\": \"Thread.sleep() 方法\", \"d\": \"ReentrantLock\"}','c','medium','Java线程','2024-12-07 22:42:22',1),(21,'Java 中的类加载器负责什么？','short_answer',NULL,'类加载器负责将类的字节码从磁盘读取到内存，并初始化类。','hard','Java基础','2024-12-07 22:42:22',1),(22,'在 Java 中，String 类是如何实现的？','short_answer',NULL,'String 类是通过 char 数组实现的，采用不可变模式设计。','hard','Java字符串','2024-12-07 22:42:22',1),(23,'以下哪个选项表示正确的 Lambda 表达式？','single','{\"a\": \"() -> { return 1; }\", \"b\": \"(x, y) -> x + y\", \"c\": \"int -> x + y\", \"d\": \"() => 1\"}','b','hard','Java Lambda','2024-12-07 22:42:22',1),(24,'在 Java 中，如何实现线程的安全性？','multiple','{\"a\": \"使用 synchronized 关键字\", \"b\": \"使用 Lock 接口\", \"c\": \"使用 volatile 关键字\", \"d\": \"使用执行器池\"}','a,b','hard','Java线程','2024-12-07 22:42:22',1),(25,'Java 中，System.out.println() 主要是用来做什么的？','single','{\"a\": \"打印错误信息\", \"b\": \"打印调试信息\", \"c\": \"打印输出信息\", \"d\": \"打印输入信息\"}','c','easy','Java基础','2024-12-07 22:42:22',1),(26,'Java 中哪个接口用于表示集合中的元素是唯一的？','single','{\"a\": \"List\", \"b\": \"Set\", \"c\": \"Map\", \"d\": \"Queue\"}','b','medium','Java集合','2024-12-07 22:42:22',1),(27,'简述 Java 中的包机制。','short_answer',NULL,'Java 的包机制是用来组织类的工具，能够避免类名冲突，并提供访问控制。','hard','Java基础','2024-12-07 22:42:22',1),(28,'在 Java 中，调用一个方法的正确方式是什么？','single','{\"a\": \"Method();\", \"b\": \"Method;()\", \"c\": \"method();\", \"d\": \"method;()\"}','a','easy','Java基础','2024-12-07 22:42:22',1),(29,'Java 中的接口可以包含实现吗？','true_false',NULL,'false','medium','Java基础','2024-12-07 22:42:22',1),(30,'简述 Java 中的反射机制。','short_answer',NULL,'反射机制允许在运行时加载类和方法，可以动态地创建对象和调用方法。','hard','Java反射','2024-12-07 22:42:22',1),(31,'在 Java 中，哪个方法可以用来将字符串转换为大写字母？','single','{\"a\": \"toUpperCase()\", \"b\": \"toLowerCase()\", \"c\": \"convertToUpper()\", \"d\": \"toCapitalize()\"}','a','easy','Java基础','2024-12-07 22:42:22',1),(32,'Java 中，\"==\" 和 \"equals()\" 方法的区别是什么？','short_answer',NULL,'\"==\" 用于比较引用是否相同，\"equals()\" 用于比较对象内容是否相同。','medium','Java基础','2024-12-07 22:42:22',1),(33,'以下哪个类用于表示一个 Java 中的线程池？','single','{\"a\": \"ThreadPoolExecutor\", \"b\": \"ExecutorService\", \"c\": \"Executor\", \"d\": \"Thread\"}','b','medium','Java线程','2024-12-07 22:42:22',1),(34,'Java 中如何避免死锁？','short_answer',NULL,'避免死锁的方法包括资源分配顺序一致、使用锁超时机制等。','hard','Java线程','2024-12-07 22:42:22',1),(35,'在 Java 中，\"throw\" 和 \"throws\" 的区别是什么？','short_answer',NULL,'\"throw\" 用于抛出异常，\"throws\" 用于声明可能抛出的异常。','medium','Java异常处理','2024-12-07 22:42:22',1),(36,'Java 中，可以通过哪些方式来处理异常？','multiple','{\"a\": \"try-catch\", \"b\": \"throw\", \"c\": \"throws\", \"d\": \"if-else\"}','a,b,c','medium','Java异常处理','2024-12-07 22:42:22',1),(37,'简述 Java 中的 synchronized 关键字的作用。','short_answer',NULL,'synchronized 用于修饰方法或代码块，保证同一时刻只有一个线程可以执行该方法或代码块。','hard','Java线程','2024-12-07 22:42:22',1),(38,'在 Java 中，如何创建一个接口？','single','{\"a\": \"interface MyInterface {}\", \"b\": \"interface MyInterface[] {}\", \"c\": \"class MyInterface {}\", \"d\": \"class MyInterface[] {}\"}','a','medium','Java基础','2024-12-07 22:42:22',1);
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `role` enum('admin','teacher','student') COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'test1','test','teacher','test@test.com','2024-12-02 23:15:17'),(4,'test2','test','admin','test2@test.com','2024-12-03 00:13:34'),(5,'user1','test','student','user1@example.com','2024-12-05 19:58:34'),(6,'user2','password123','teacher','user2@example.com','2024-12-05 19:58:34'),(7,'user3','password123','teacher','user3@example.com','2024-12-05 19:58:34'),(9,'user5','password123','teacher','user5@example.com','2024-12-05 19:58:34'),(10,'user6','password123','teacher','user6@example.com','2024-12-05 19:58:34'),(11,'user7','password123','teacher','user7@example.com','2024-12-05 19:58:34'),(12,'user8','password123','teacher','user8@example.com','2024-12-05 19:58:34'),(13,'user9','password123','teacher','user9@example.com','2024-12-05 19:58:34'),(14,'user10','password123','teacher','user10@example.com','2024-12-05 19:58:34'),(15,'student1','password123','student','student1@example.com','2024-12-11 09:48:18'),(16,'student2','password123','student','student2@example.com','2024-12-11 09:48:18'),(17,'student3','password123','student','student3@example.com','2024-12-11 09:48:18'),(18,'student4','password123','student','student4@example.com','2024-12-11 09:48:18'),(19,'student5','password123','student','student5@example.com','2024-12-11 09:48:18'),(20,'student6','password123','student','student6@example.com','2024-12-11 09:48:18'),(21,'student7','password123','student','student7@example.com','2024-12-11 09:48:18'),(22,'student8','password123','student','student8@example.com','2024-12-11 09:48:18'),(23,'student9','password123','student','student9@example.com','2024-12-11 09:48:18'),(24,'student10','password123','student','student10@example.com','2024-12-11 09:48:18');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-13 22:52:32
