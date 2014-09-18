CREATE TABLE ADMITTER_COMMENT (
  `id` int(10) unsigned NOT NULL,
  `qualified_for_phd` varchar(30) DEFAULT NULL,
  `english_compentency_ok` varchar(30) DEFAULT NULL,
  `home_or_overseas` varchar(30) DEFAULT NULL,
  `comment_type` varchar(50) NOT NULL,
  KEY `admitter_comment_fk` (`id`),
  CONSTRAINT `admitter_comment_fk` FOREIGN KEY (`id`) REFERENCES COMMENT (`id`)
)
;
