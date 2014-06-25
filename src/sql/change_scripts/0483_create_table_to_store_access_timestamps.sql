CREATE TABLE APPLICATION_FORM_LAST_ACCESS (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `application_form_id` int(10) unsigned NOT NULL,
  `last_access_timestamp` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_application` (`user_id`,`application_form_id`),
  KEY `user_application_form_last_access_fk` (`user_id`),
  KEY `application_form_application_form_last_access_fk` (`application_form_id`),
  CONSTRAINT `user_application_form_last_access_fk` FOREIGN KEY (`user_id`) REFERENCES REGISTERED_USER (`id`),
  CONSTRAINT `application_form_application_form_last_access_fk` FOREIGN KEY (`application_form_id`) REFERENCES APPLICATION_FORM (`id`)
)
;
