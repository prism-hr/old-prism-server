CREATE TABLE `user_account_link` (
  `registered_user_id` int(10) unsigned NOT NULL,
  `registered_user_link_id` int(10) unsigned NOT NULL,
  KEY `registered_user_id_fk` (`registered_user_id`),
  KEY `registered_user_link_id_fk` (`registered_user_link_id`),
  CONSTRAINT `registered_user_id_fk` FOREIGN KEY (`registered_user_id`) REFERENCES `registered_user` (`id`),
  CONSTRAINT `registered_user_link_id_fk` FOREIGN KEY (`registered_user_id`) REFERENCES `registered_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;
