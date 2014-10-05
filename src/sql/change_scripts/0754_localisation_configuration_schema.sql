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
	ADD COLUMN locale VARCHAR(10) NOT NULL DEFAULT "EN_GB" AFTER program_id,
	ADD COLUMN locked INT(1) UNSIGNED NOT NULL DEFAULT 1,
	DROP INDEX system_id,
	DROP INDEX institution_id,
	DROP INDEX program_id,
	ADD UNIQUE INDEX (system_id, locale, notification_template_id),
	ADD UNIQUE INDEX (institution_id, locale, notification_template_id),
	ADD UNIQUE INDEX (program_id, locale, notification_template_id),
	ADD INDEX (system_id, locale, notification_template_id, locked),
	ADD INDEX (institution_id, locale, notification_template_id, locked),
	ADD INDEX (program_id, locale, notification_template_id, locked)
;

ALTER TABLE NOTIFICATION_CONFIGURATION
	MODIFY COLUMN locale VARCHAR(10) NOT NULL,
	MODIFY COLUMN locked INT(1) UNSIGNED NOT NULL
;

