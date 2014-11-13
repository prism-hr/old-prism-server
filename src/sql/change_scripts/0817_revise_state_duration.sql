DROP TABLE ACTION_TRIGGER_STATE
;

CREATE TABLE STATE_DURATION_DEFINITION (
	id VARCHAR(50) NOT NULL,
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
	CHANGE COLUMN state_id state_duration_definition_id VARCHAR(50) NOT NULL,
	ADD FOREIGN KEY (state_duration_definition_id) REFERENCES STATE_DURATION_DEFINITION (id)
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
	ADD INDEX (customizable_action)
;

ALTER TABLE ACTION
	MODIFY COLUMN customizable_action INT(1) UNSIGNED NOT NULL
;

CREATE TABLE ACTION_PROPERTY_CONFIGURATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	locale VARCHAR(10),
	program_type VARCHAR(50),
	action_id VARCHAR(100) NOT NULL,
	version INT(10) UNSIGNED,
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
	active INT(1) UNSIGNED NOT NULL,
	system_default INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, locale, program_type, action_id, version, display_index),
	UNIQUE INDEX (institution_id, program_type, action_id, version, display_index),
	UNIQUE INDEX (program_id, action_id, version, display_index),
	INDEX (system_id, locale, program_type, action_id, active), 
	INDEX (institution_id, program_type, action_id, active),
	INDEX (program_id, action_id, active),
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
	property_label TEXT NOT NULL,
	property_value TEXT NOT NULL,
	property_weight DECIMAL(3,2),
	PRIMARY KEY (id),
	INDEX (comment_id),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id)
) ENGINE = INNODB
;

ALTER TABLE ROLE
	CHANGE COLUMN is_scope_creator scope_creator INT(1) UNSIGNED NOT NULL
;

ALTER TABLE NOTIFICATION_CONFIGURATION
	DROP COLUMN day_reminder_interval
;

ALTER TABLE APPLICATION
	DROP COLUMN previous_closing_date
;

ALTER TABLE STATE
	ADD COLUMN state_duration_evaluation VARCHAR(50) AFTER state_group_id
;

CREATE TABLE RESOURCE_STATE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	project_id INT(10) UNSIGNED,
	application_id INT(10) UNSIGNED,
	state_id VARCHAR(50) NOT NULL,
	primary_state INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, state_id, primary_state),
	UNIQUE INDEX (institution_id, state_id, primary_state),
	UNIQUE INDEX (program_id, state_id, primary_state),
	UNIQUE INDEX (project_id, state_id, primary_state),
	UNIQUE INDEX (application_id, state_id, primary_state),
	FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (project_id) REFERENCES PROJECT (id),
	FOREIGN KEY (application_id) REFERENCES APPLICATION (id)
) ENGINE = INNODB
;

ALTER TABLE STATE_GROUP
	ADD COLUMN parallelizable INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER repeatable
;

ALTER TABLE STATE_GROUP
	MODIFY COLUMN parallelizable INT(1) UNSIGNED NOT NULL
;

CREATE TABLE COMMENT_TRANSITION_STATE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL,
	transition_state_id VARCHAR(50) NOT NULL,
	primary_state INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (comment_id, transition_state_id),
	INDEX primary_state (comment_id, primary_state),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id),
	FOREIGN KEY (transition_state_id) REFERENCES STATE (id)
) ENGINE = INNODB
;

INSERT INTO COMMENT_TRANSITION_STATE (comment_id, transition_state_id, primary_state)
	SELECT id, transition_state_id, 1
	FROM COMMENT
	WHERE transition_state_id IS NOT NULL
;

INSERT INTO RESOURCE_STATE (application_id, state_id, primary_state)
	SELECT id, state_id, 1
	FROM APPLICATION
;

INSERT INTO RESOURCE_STATE (project_id, state_id, primary_state)
	SELECT id, state_id, 1
	FROM PROJECT
;

INSERT INTO RESOURCE_STATE (program_id, state_id, primary_state)
	SELECT id, state_id, 1
	FROM PROGRAM
;

DELETE COMMENT_ASSIGNED_USER.*
FROM COMMENT_ASSIGNED_USER INNER JOIN COMMENT
	ON COMMENT_ASSIGNED_USER.comment_id = COMMENT.id
INNER JOIN INSTITUTION
	ON COMMENT.institution_id = INSTITUTION.id
WHERE INSTITUTION.state_id IS NULL
;

DELETE COMMENT.*
FROM COMMENT INNER JOIN INSTITUTION
	ON COMMENT.institution_id = INSTITUTION.id
WHERE INSTITUTION.state_id IS NULL
;

UPDATE INSTITUTION
SET institution_address_id = NULL
WHERE state_id IS NULL
;

DELETE INSTITUTION_ADDRESS.*
FROM INSTITUTION INNER JOIN INSTITUTION_ADDRESS
	ON INSTITUTION.id = INSTITUTION_ADDRESS.institution_id
WHERE INSTITUTION.state_id IS NULL
;

DELETE
FROM INSTITUTION
WHERE state_id IS NULL
;

INSERT INTO RESOURCE_STATE (institution_id, state_id, primary_state)
	SELECT id, state_id, 1
	FROM INSTITUTION
;

INSERT INTO RESOURCE_STATE (system_id, state_id, primary_state)
	SELECT id, state_id, 1
	FROM SYSTEM
;

ALTER TABLE COMMENT
	MODIFY COLUMN application_rating DECIMAL(3,2)
;

CREATE TABLE STATE_TERMINATION (
	state_transition_id INT(10) UNSIGNED NOT NULL,
	termination_state_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (state_transition_id, termination_state_id),
	INDEX (termination_state_id),
	FOREIGN KEY (state_transition_id) REFERENCES STATE_TRANSITION (id),
	FOREIGN KEY (termination_state_id) REFERENCES STATE (id)
) ENGINE = INNODB
;

ALTER TABLE STATE
	ADD COLUMN state_duration_definition_id VARCHAR(50) AFTER state_group_id,
	ADD INDEX (state_duration_definition_id),
	ADD FOREIGN KEY (state_duration_definition_id) REFERENCES STATE_DURATION_DEFINITION (id)
;

ALTER TABLE	STATE_TRANSITION
	MODIFY COLUMN transition_state_id VARCHAR(50)
;

ALTER TABLE IMPORTED_ENTITY
	ADD COLUMN root_id INT(10) UNSIGNED AFTER id,
	ADD INDEX (root_id),
	ADD FOREIGN KEY (root_id) REFERENCES IMPORTED_ENTITY (id)
;

UPDATE IMPORTED_ENTITY INNER JOIN IMPORTED_ENTITY AS ROOT
	ON IMPORTED_ENTITY.imported_entity_type = ROOT.imported_entity_type
	AND IMPORTED_ENTITY.code = ROOT.code
SET IMPORTED_ENTITY.root_id = ROOT.id
WHERE ROOT.institution_id = 5243
;

UPDATE IMPORTED_ENTITY
SET root_id = NULL
WHERE institution_id = 5243
;

ALTER TABLE IMPORTED_INSTITUTION
	ADD COLUMN root_id INT(10) UNSIGNED AFTER id,
	ADD INDEX (root_id),
	ADD FOREIGN KEY (root_id) REFERENCES IMPORTED_ENTITY (id)
;

UPDATE IMPORTED_INSTITUTION INNER JOIN IMPORTED_INSTITUTION AS ROOT
	ON IMPORTED_INSTITUTION.code = ROOT.code
SET IMPORTED_INSTITUTION.root_id = ROOT.id
WHERE ROOT.institution_id = 5243
;

UPDATE IMPORTED_INSTITUTION
SET root_id = NULL
WHERE institution_id = 5243
;

ALTER TABLE IMPORTED_LANGUAGE_QUALIFICATION_TYPE
	ADD COLUMN root_id INT(10) UNSIGNED AFTER id,
	ADD INDEX (root_id),
	ADD FOREIGN KEY (root_id) REFERENCES IMPORTED_ENTITY (id)
;

UPDATE IMPORTED_LANGUAGE_QUALIFICATION_TYPE INNER JOIN IMPORTED_LANGUAGE_QUALIFICATION_TYPE AS ROOT
	ON IMPORTED_LANGUAGE_QUALIFICATION_TYPE.code = ROOT.code
SET IMPORTED_LANGUAGE_QUALIFICATION_TYPE.root_id = ROOT.id
WHERE ROOT.institution_id = 5243
;

UPDATE IMPORTED_LANGUAGE_QUALIFICATION_TYPE
SET root_id = NULL
WHERE institution_id = 5243
;

ALTER TABLE ADDRESS
	MODIFY COLUMN domicile_id INT(10) UNSIGNED
;

ALTER TABLE APPLICATION_FUNDING
	MODIFY COLUMN funding_source_id INT(10) UNSIGNED
;

ALTER TABLE APPLICATION_LANGUAGE_QUALIFICATION
	MODIFY COLUMN language_qualification_type_id INT(10) UNSIGNED
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	MODIFY COLUMN title_id INT(10) UNSIGNED,
	MODIFY COLUMN gender_id INT(10) UNSIGNED,
	MODIFY COLUMN country_id INT(10) UNSIGNED,
	MODIFY COLUMN nationality_id1 INT(10) UNSIGNED,
	MODIFY COLUMN nationality_id2 INT(10) UNSIGNED,
	MODIFY COLUMN domicile_id INT(10) UNSIGNED,
	MODIFY COLUMN ethnicity_id INT(10) UNSIGNED,
	MODIFY COLUMN disability_id INT(10) UNSIGNED
;

ALTER TABLE APPLICATION_QUALIFICATION
	MODIFY COLUMN institution_id INT(10) UNSIGNED,
	MODIFY COLUMN qualification_type_id INT(10) UNSIGNED
;

ALTER TABLE INSTITUTION_ADDRESS
	DROP FOREIGN KEY institution_address_ibfk_3,
	DROP COLUMN institution_domicile_region_id
;
