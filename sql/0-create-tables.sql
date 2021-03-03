-- MariaDB dump 10.18  Distrib 10.5.7-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: oncall
-- ------------------------------------------------------
-- Server version       10.5.7-MariaDB-1:10.5.7+maria~bionic

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `channel_info`
--

DROP TABLE IF EXISTS `channel_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `channel_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `team_id` varchar(255) NOT NULL,
  `channel_id` varchar(255) NOT NULL,
  `bot_token` varchar(1024) DEFAULT NULL,
  `webhook` varchar(1024) NOT NULL,
  `bot_user_id` varchar(255) DEFAULT NULL,
  `bot_owner_user_id` varchar(255) DEFAULT NULL,
  `created` datetime(3) DEFAULT current_timestamp(3),
  `updated` datetime(3) DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_channel_info` (`team_id`,`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oncall`
--

DROP TABLE IF EXISTS `oncall`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oncall` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `team_id` varchar(255) NOT NULL,
  `channel_id` varchar(255) NOT NULL,
  `current` varchar(255) NOT NULL,
  `created` datetime(3) DEFAULT current_timestamp(3),
  `updated` datetime(3) DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-03-04  2:27:43
