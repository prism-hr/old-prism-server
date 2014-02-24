CREATE TABLE APPLICATION_FORM_UPDATE (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `application_form_id` int(10) unsigned NOT NULL,
  `update_timestamp` timestamp NOT NULL,
  `update_visibility` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `application_form_application_form_update` (`application_form_id`),
  CONSTRAINT `application_form_application_form_update` FOREIGN KEY (`application_form_id`) REFERENCES APPLICATION_FORM (`id`)
)
;
