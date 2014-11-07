CREATE TABLE STATE_DURATION_DEFINITION (
	id VARCHAR(50) NOT NULL,
	evaluation INT(1) UNSIGNED,
	scope_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	INDEX (scope_id),
	FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
) ENGINE = INNODB
;

RENAME TABLE STATE_DURATION TO STATE_DURATION_CONFIGURATION
;

DELETE
FROM STATE_DURATION_CONFIGURATION
;

ALTER TABLE STATE_DURATION_CONFIGURATION
	DROP FOREIGN KEY state_duration_configuration_ibfk_4,
	CHANGE COLUMN state_id state_duration_configuration_id VARCHAR(50) NOT NULL,
	ADD FOREIGN KEY (state_duration_configuration_id) REFERENCES STATE_DURATION_DEFINITION (id)
;

RENAME TABLE DISPLAY_PROPERTY TO DISPLAY_PROPERTY_DEFINITION
;

RENAME TABLE DISPLAY_VALUE TO DISPLAY_PROPERTY_CONFIGURATION
;

ALTER TABLE DISPLAY_PROPERTY_CONFIGURATION
	CHANGE COLUMN display_property_id display_property_definition_id VARCHAR(100) NOT NULL
;

RENAME TABLE NOTIFICATION_TEMPLATE TO NOTIFICATION_DEFINITION
;

ALTER TABLE NOTIFICATION_DEFINITION
	CHANGE COLUMN reminder_notification_template_id reminder_definition_id VARCHAR(100)
;

ALTER TABLE NOTIFICATION_CONFIGURATION
	CHANGE COLUMN notification_template_id notification_definition_id VARCHAR(100) NOT NULL
;

ALTER TABLE STATE_ACTION
	CHANGE COLUMN notification_template_id notification_definition_id VARCHAR(100)
;

ALTER TABLE STATE_ACTION_NOTIFICATION
	CHANGE COLUMN notification_template_id notification_definition_id VARCHAR(100)
;

ALTER TABLE ACTION
	ADD COLUMN customizable_action INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER emphasizedAction,
	ADD COLUMN configurable_action INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER customizableAction,
	ADD COLUMN singleton_action INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER configurable_action,
	ADD INDEX (customizable_action),
	ADD INDEX (customizable_action)
;

ALTER TABLE ACTION
	MODIFY COLUMN customizable_action INT(1) UNSIGNED NOT NULL,
	MODIFY COLUMN configurable_action INT(1) UNSIGNED NOT NULL,
	MODIFY COLUMN singleton_action INT(1) UNSIGNED NOT NULL
;

CREATE TABLE ACTION_CONFIGURATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	locale VARCHAR(10),
	program_type VARCHAR(50),
	action_id VARCHAR(100) NOT NULL,
	start_state_group_id VARCHAR(50) NOT NULL,
	system_default INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, locale, program_type, action_id),
	UNIQUE INDEX (institution_id, program_type, action_id),
	UNIQUE INDEX (program_id, action_id),
	INDEX (action_id),
	INDEX (start_state_group_id),
	INDEX system_default (system_id, system_default),
	FOREIGN KEY (action_id) REFERENCES ACTION (id),
	FOREIGN KEY (start_state_group_id) REFERENCES STATE_GROUP (id)
) ENGINE = INNODB
;

CREATE TABLE ACTION_PROPERTY_CONFIGURATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	locale VARCHAR(10),
	program_type VARCHAR(50),
	action_id VARCHAR(100) NOT NULL,
	action_property_type VARCHAR(50) NOT NULL,
	display_name TEXT NOT NULL,
	display_editable INT(1) UNSIGNED NOT NULL,
	display_index INT(3) UNSIGNED NOT NULL,
	display_label TEXT NOT NULL,
	display_description TEXT,
	display_placeholder TEXT,
	display_options TEXT,
	display_required INT(1) UNSIGNED NOT NULL,
	display_validation TEXT,
	display_weighting DECIMAL(3,2),
	system_default INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, locale, program_type, action_id, display_index),
	UNIQUE INDEX (institution_id, program_type, action_id, display_index),
	UNIQUE INDEX (program_id, action_id, display_index),
	INDEX (action_id),
	INDEX system_default (system_id, system_default),
	FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id)
) ENGINE = INNODB
;

CREATE TABLE COMMENT_PROPERTY (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL,
	action_property_type VARCHAR(50) NOT NULL,
	property_value TEXT,
	PRIMARY KEY (id),
	INDEX (comment_id),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id)
) ENGINE = INNODB
;

DROP TABLE ACTION_TRIGGER_STATE
;

CREATE TABLE RESOURCE_ACTION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	project_id INT(10) UNSIGNED,
	application_id INT(10) UNSIGNED,
	action_id VARCHAR(100) NOT NULL,
	PRIMARY KEY (id),
	INDEX (system_id),
	INDEX (institution_id),
	INDEX (program_id),
	INDEX (project_id),
	INDEX (application_id),
	FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (project_id) REFERENCES PROJECT (id),
	FOREIGN KEY (application_id) REFERENCES APPLICATION (id)
) ENGINE = INNODB
;

ALTER TABLE ACTION_CONFIGURATION
	ADD COLUMN resource_action_evaluation VARCHAR(50)
;

ALTER TABLE USER_ROLE
	ADD COLUMN activated INT(1) UNSIGNED NOT NULL DEFAULT 1 AFTER role_id,
	ADD INDEX (system_id, user_id, role_id, activated),
	ADD INDEX (institution_id, user_id, role_id, activated),
	ADD INDEX (program_id, user_id, role_id, activated),
	ADD INDEX (project_id, user_id, role_id, activated),
	ADD INDEX (application_id, user_id, role_id, activated)
;

ALTER TABLE USER_ROLE
	MODIFY COLUMN activated INT(1) UNSIGNED NOT NULL
;

ALTER TABLE ROLE
	CHANGE COLUMN is_scope_creator scope_creator INT(1) UNSIGNED NOT NULL,
	ADD COLUMN activate_immediately INT(1) UNSIGNED NOT NULL DEFAULT 1 AFTER scope_creator
;

ALTER TABLE ROLE
	MODIFY COLUMN activate_immediately INT(1) UNSIGNED NOT NULL
;

