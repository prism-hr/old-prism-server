CREATE TABLE `USER_ACCOUNT_LINK` (
  `registered_user_id` int(10) unsigned NOT NULL,
  `registered_user_link_id` int(10) unsigned NOT NULL,
  KEY `registered_user_id_fk` (`registered_user_id`),
  KEY `registered_user_link_id_fk` (`registered_user_link_id`),
  CONSTRAINT `registered_user_id_one_fk` FOREIGN KEY (`registered_user_id`) REFERENCES `REGISTERED_USER` (`id`),
  CONSTRAINT `registered_user_id_two_fk` FOREIGN KEY (`registered_user_id`) REFERENCES `REGISTERED_USER` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;
