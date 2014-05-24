/* Make configuration possible at institution and program level */

DELETE
FROM SYSTEM_CONFIGURATION
WHERE id = "APPLICATION_EXPORT_BATCH_SIZE"
;

CREATE TABLE CONFIGURATION_PARAMETER (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
	SELECT id AS id 
	FROM SYSTEM_CONFIGURATION
		UNION
	SELECT "PROGRAM_STUDY_DURATION"
;

CREATE TABLE CONFIGURATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	institution_id INT(10) UNSIGNED NOT NULL,
	program_type_id VARCHAR(50),
	program_id INT(10) UNSIGNED,
	configuration_parameter_id VARCHAR(50) NOT NULL,
	parameter_value INT(10) UNSIGNED,
	PRIMARY KEY (id),
	UNIQUE INDEX (institution_id, program_type_id, configuration_parameter_id),
	UNIQUE INDEX (program_id, configuration_parameter_id),
	INDEX (program_type_id),
	INDEX (configuration_parameter_id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (program_type_id) REFERENCES PROGRAM_TYPE (id),
	FOREIGN KEY (configuration_parameter_id) REFERENCES CONFIGURATION_PARAMETER (id)
) ENGINE = INNODB
	SELECT NULL AS id, 5243 AS institution_id, NULL AS program_type_id, NULL AS program_id, 
		id AS configuration_parameter_id, value AS parameter_value
	FROM SYSTEM_CONFIGURATION
		UNION
	SELECT NULL AS id, 5243 AS institution_id, id AS program_type_id, NULL AS program_id, 
		"PROGRAM_STUDY_DURATION" AS configuration_parameter_id, default_study_duration AS parameter_value
	FROM PROGRAM_TYPE
;

ALTER TABLE PROGRAM_TYPE
	DROP COLUMN default_study_duration
;

DROP TABLE SYSTEM_CONFIGURATION
;

/* Exporting programs */

INSERT INTO ACTION (id)
VALUES ("SYSTEM_EXPORT_PROGRAMS"),
	("INSTITUTION_EXPORT_PROGRAMS")
;

INSERT INTO STATE_ACTION(state_id, action_id, raises_urgent_flag)
	SELECT "SYSTEM_APPROVED", "SYSTEM_EXPORT_PROGRAMS", 0
		UNION
	SELECT "INSTITUTION_APPROVED", "INSTITUTION_EXPORT_PROGRAMS", 0
;

INSERT INTO STATE_ACTION_ASSIGNMENT(state_action_id, role_id)
	SELECT id, "SYSTEM_ADMINISTRATOR"
	FROM STATE_ACTION
	WHERE action_id = "SYSTEM_EXPORT_PROGRAMS"
		UNION
	SELECT STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "INSTITUTION_EXPORT_PROGRAMS"
		AND ROLE.id IN ("SYSTEM_ADMINISTRATOR", "INSTITUTION_ADMINISTRATOR")
;

/* Unify application complete and edit actions */

UPDATE STATE_ACTION
SET action_id = "APPLICATION_COMPLETE"
WHERE action_id = "APPLICATION_EDIT_AS_ADMINISTRATOR"
;

UPDATE ACTION_VISIBILITY_EXCLUSION
SET action_id = "APPLICATION_COMPLETE"
WHERE action_id = "APPLICATION_EDIT_AS_ADMINISTRATOR"
;

DELETE
FROM ACTION
WHERE id = "APPLICATION_EDIT_AS_ADMINISTRATOR"
;

/* Actions that any user can perform */

ALTER TABLE ACTION
	ADD COLUMN bypass_permission_check INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (bypass_permission_check)
;

UPDATE ACTION
SET bypass_permission_check = 1
WHERE id IN ("PROJECT_CREATE_APPLICATION", "PROGRAM_CREATE_APPLICATION", "INSTITUTION_CREATE_PROGRAM")
;

ALTER TABLE ACTION
	MODIFY COLUMN bypass_permission_check INT(1) UNSIGNED NOT NULL
;

/* Remove requesting user */

ALTER TABLE USER_ROLE
	DROP COLUMN requesting_user_id
;
