CREATE TABLE `client_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `team_id` varchar(255) NOT NULL,
  `bot_token` varchar(1024) DEFAULT NULL,
  `webhook` varchar(1024) NOT NULL,
  `bot_user_id` varchar(255) DEFAULT NULL,
  `bot_owner_user_id` varchar(255) DEFAULT NULL,
  `created` datetime(3) DEFAULT current_timestamp(3),
  `updated` datetime(3) DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client_info` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `oncall` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `team_id` varchar(255) NOT NULL,
  `channel_id` varchar(255) NOT NULL,
  `current` varchar(255) NOT NULL,
  `created` datetime(3) DEFAULT current_timestamp(3),
  `updated` datetime(3) DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_team_channel` (`team_id`,`channel_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;