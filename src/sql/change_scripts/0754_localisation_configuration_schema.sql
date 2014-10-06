CREATE TABLE DISPLAY_CONFIGURATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	locale VARCHAR(10) NOT NULL,
	property_category VARCHAR(50) NOT NULL,
	property_key VARCHAR(50) NOT NULL,
	property_value TEXT NOT NULL,
	locked INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, locale, property_category, property_key),
	UNIQUE INDEX (institution_id, locale, property_category, property_key),
	UNIQUE INDEX (program_id, locale, property_category, property_key),
	INDEX (system_id, locale, property_category, property_key, locked),
	INDEX (institution_id, locale, property_category, property_key, locked),
	INDEX (program_id, locale, property_category, property_key, locked),
	FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id)
) ENGINE = INNODB
;

ALTER TABLE USER
	ADD COLUMN locale VARCHAR(10) NOT NULL DEFAULT "EN_GB" AFTER email
;

ALTER TABLE USER
	MODIFY COLUMN locale VARCHAR(10) NOT NULL
;

ALTER TABLE INSTITUTION
	ADD COLUMN locale VARCHAR(10) NOT NULL DEFAULT "EN_GB" AFTER title
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN locale VARCHAR(10) NOT NULL
;

ALTER TABLE PROGRAM
	MODIFY COLUMN code VARCHAR(50) AFTER id,
	MODIFY COLUMN advert_id INT(10) UNSIGNED AFTER user_id,
	MODIFY COLUMN system_id INT(10) UNSIGNED AFTER user_id,
	MODIFY COLUMN institution_id INT(10) UNSIGNED AFTER system_id,
	ADD COLUMN locale VARCHAR(10) NOT NULL DEFAULT "EN_GB" AFTER title
;

ALTER TABLE PROGRAM
	MODIFY COLUMN locale VARCHAR(10) NOT NULL
;

ALTER TABLE SYSTEM
	ADD COLUMN locale VARCHAR(10) NOT NULL DEFAULT "EN_GB" AFTER title
;

ALTER TABLE SYSTEM
	MODIFY COLUMN locale VARCHAR(10) NOT NULL
;

ALTER TABLE STATE_DURATION
	ADD COLUMN locked INT(1) UNSIGNED NOT NULL DEFAULT 1,
	ADD INDEX (system_id, state_id, locked),
	ADD INDEX (institution_id, state_id, locked),
	ADD INDEX (program_id, state_id, locked)
;

ALTER TABLE STATE_DURATION
	MODIFY COLUMN locked INT(1) UNSIGNED NOT NULL
;

ALTER TABLE WORKFLOW_CONFIGURATION
	ADD COLUMN locked INT(1) UNSIGNED NOT NULL DEFAULT 1,
	ADD INDEX (system_id, configuration_parameter, locked),
	ADD INDEX (institution_id, configuration_parameter, locked),
	ADD INDEX (program_id, configuration_parameter, locked)
;

ALTER TABLE WORKFLOW_CONFIGURATION
	MODIFY COLUMN locked INT(1) UNSIGNED NOT NULL
;

ALTER TABLE NOTIFICATION_CONFIGURATION
	ADD COLUMN locked INT(1) UNSIGNED NOT NULL DEFAULT 1,
	ADD INDEX (system_id, notification_template_id, locked),
	ADD INDEX (institution_id, notification_template_id, locked),
	ADD INDEX (program_id, notification_template_id, locked)
;

ALTER TABLE NOTIFICATION_CONFIGURATION
	MODIFY COLUMN locked INT(1) UNSIGNED NOT NULL
;

ALTER TABLE COMMENT_CUSTOM_QUESTION
	CHANGE COLUMN is_enabled enabled INT(1) UNSIGNED NOT NULL,
	DROP INDEX system_id,
	DROP INDEX institution_id,
	ADD UNIQUE INDEX (system_id, action_id),
	ADD UNIQUE INDEX (institution_id, action_id),
	ADD COLUMN locked INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (system_id, action_id, locked),
	ADD INDEX (institution_id, action_id, locked),
	ADD INDEX (program_id, action_id, locked)
;

ALTER TABLE COMMENT_CUSTOM_QUESTION
	MODIFY COLUMN locked INT(1) UNSIGNED NOT NULL
;

ALTER TABLE NOTIFICATION_TEMPLATE_VERSION
	ADD COLUMN notification_configuration_id INT(10) UNSIGNED AFTER id,
	ADD COLUMN locale VARCHAR(10) NOT NULL DEFAULT "EN_GB" AFTER notification_configuration_id,
	ADD COLUMN active INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER content,
	ADD INDEX (notification_configuration_id, locale, active),
	ADD FOREIGN KEY (notification_configuration_id) REFERENCES NOTIFICATION_CONFIGURATION (id)
;

UPDATE NOTIFICATION_TEMPLATE_VERSION INNER JOIN NOTIFICATION_CONFIGURATION
	ON NOTIFICATION_TEMPLATE_VERSION.id = NOTIFICATION_CONFIGURATION.notification_template_version_id
SET NOTIFICATION_TEMPLATE_VERSION.notification_configuration_id = NOTIFICATION_CONFIGURATION.id,
	NOTIFICATION_TEMPLATE_VERSION.active = 1
;

ALTER TABLE NOTIFICATION_TEMPLATE_VERSION
	DROP FOREIGN KEY notification_template_version_ibfk_5,
	DROP FOREIGN KEY notification_template_version_ibfk_6,
	DROP FOREIGN KEY notification_template_version_ibfk_7,
	DROP FOREIGN KEY notification_template_version_ibfk_4,
	DROP COLUMN system_id,
	DROP COLUMN institution_id,
	DROP COLUMN program_id,
	DROP COLUMN notification_template_id,
	MODIFY COLUMN notification_configuration_id INT(10) UNSIGNED NOT NULL,
	MODIFY COLUMN locale VARCHAR(10) NOT NULL,
	MODIFY COLUMN active INT(1) UNSIGNED NOT NULL
;

ALTER TABLE COMMENT_CUSTOM_QUESTION_VERSION
	ADD COLUMN locale VARCHAR(10) NOT NULL DEFAULT "EN_GB" AFTER comment_custom_question_id,
	ADD COLUMN active INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER content,
	ADD INDEX (comment_custom_question_id, locale, active)
;

UPDATE COMMENT_CUSTOM_QUESTION_VERSION INNER JOIN COMMENT_CUSTOM_QUESTION
	ON COMMENT_CUSTOM_QUESTION_VERSION.comment_custom_question_id = COMMENT_CUSTOM_QUESTION.id
	SET COMMENT_CUSTOM_QUESTION_VERSION.active = IF(
		COMMENT_CUSTOM_QUESTION_VERSION.id = COMMENT_CUSTOM_QUESTION.comment_custom_question_version_id,
		1,
		0)
;

ALTER TABLE COMMENT_CUSTOM_QUESTION_VERSION
	MODIFY COLUMN locale VARCHAR(10) NOT NULL,
	MODIFY COLUMN active INT(1) UNSIGNED NOT NULL
;

ALTER TABLE COMMENT_CUSTOM_QUESTION
	DROP FOREIGN KEY comment_custom_question_ibfk_3,
	DROP COLUMN comment_custom_question_version_id
;
