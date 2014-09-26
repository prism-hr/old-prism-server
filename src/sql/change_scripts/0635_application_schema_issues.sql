RENAME TABLE APPLICATION_ROLE TO ROLE
;

RENAME TABLE APPLICATION_ROLE_SCOPE TO ROLE_SCOPE
;

ALTER TABLE application_action_optional
	CHANGE COLUMN application_role_id role_id VARCHAR(50) NOT NULL
;

ALTER TABLE application_action_required
	CHANGE COLUMN application_role_id role_id VARCHAR(50) NOT NULL
;

ALTER TABLE application_user_role
	CHANGE COLUMN application_role_id role_id VARCHAR(50) NOT NULL
;

ALTER TABLE institution_user_role
	CHANGE COLUMN application_role_id role_id VARCHAR(50) NOT NULL
;

ALTER TABLE pending_role_notification
	CHANGE COLUMN role_id role_id VARCHAR(50) NOT NULL
;

ALTER TABLE program_user_role
	CHANGE COLUMN application_role_id role_id VARCHAR(50) NOT NULL
;

ALTER TABLE project_user_role
	CHANGE COLUMN application_role_id role_id VARCHAR(50) NOT NULL
;

ALTER TABLE system_user_role
	CHANGE COLUMN application_role_id role_id VARCHAR(50) NOT NULL
;

ALTER TABLE role
	CHANGE COLUMN application_role_scope_id role_scope_id VARCHAR(50) NOT NULL
;

CREATE TABLE STATE_TRANSITION (
	action_id VARCHAR(50) NOT NULL,
	state_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (action_id, state_id),
	INDEX (state_id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id),
	FOREIGN KEY (state_id) REFERENCES STATE (id)
) ENGINE = INNODB
;

CREATE TABLE NOTIFICATION_METHOD (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
	SELECT "INDIVIDUAL" AS id
		UNION
	SELECT "SYNDICATED" AS id
;

ALTER TABLE ACTION
	DROP INDEX notification,
	CHANGE COLUMN notification notification_method_id VARCHAR(50),
	ADD INDEX (notification_method_id),
	ADD FOREIGN KEY (notification_method_id) REFERENCES NOTIFICATION_METHOD (id)
;

ALTER TABLE STATE
	DROP COLUMN can_be_assigned_from,
	DROP COLUMN can_be_assigned_to
;

ALTER TABLE ACTION
	ADD COLUMN update_scope_id VARCHAR(50),
	ADD INDEX (update_scope_id),
	ADD FOREIGN KEY (update_scope_id) REFERENCES UPDATE_SCOPE (id)
;

UPDATE ACTION
SET update_scope_id = IF(update_visibility = 1, "INTERNAL", "EXTERNAL")
;

ALTER TABLE ACTION
	DROP COLUMN update_visibility
;

INSERT INTO ACTION (id, action_type_id, precedence, notification_method_id, update_scope_id)
	VALUES ("COMPLETE_APPROVAL_STAGE_AS_PROGRAM_APPROVER", "COMPLETE_APPROVAL_STAGE", 1, "SYNDICATED", "INTERNAL")
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE ACTION
SET id = "COMPLETE_APPROVAL_STAGE_AS_PROGRAM_ADMINISTRATOR"
WHERE id = "COMPLETE_APPROVAL_STAGE"
;

UPDATE APPLICATION_ACTION_REQUIRED
SET action_id = "COMPLETE_APPROVAL_STAGE_AS_PROGRAM_APPROVER"
WHERE action_id = "COMPLETE_APPROVAL_STAGE"
	AND role_id IN ("PROGRAM_APPROVER", "SYSTEM_ADMINISTRATOR")
;

UPDATE APPLICATION_ACTION_REQUIRED
SET action_id = "COMPLETE_APPROVAL_STAGE_AS_PROGRAM_ADMINISTRATOR"
WHERE action_id = "COMPLETE_APPROVAL_STAGE"
	AND role_id IN ("PROGRAM_ADMINISTRATOR", "PROJECT_ADMINISTRATOR", "APPLICATION_ADMINISTRATOR")
;

SET FOREIGN_KEY_CHECKS = 1
;

RENAME TABLE ROLE_SCOPE TO SCOPE
;

ALTER TABLE ROLE
	CHANGE COLUMN role_scope_id scope_id VARCHAR(50) NOT NULL
;

ALTER TABLE STATE
	ADD COLUMN scope_id VARCHAR(50) AFTER id,
	ADD INDEX (scope_id),
	ADD FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
;

UPDATE STATE
SET scope_id = "APPLICATION"
;

ALTER TABLE STATE
	MODIFY COLUMN scope_id VARCHAR(50) NOT NULL
;

INSERT INTO STATE_TRANSITION
	SELECT ACTION.id, STATE.id
	FROM ACTION INNER JOIN STATE
	WHERE ACTION.id IN ("COMPLETE_VALIDATION_STAGE", "COMPLETE_REVIEW_STAGE", 
		"COMPLETE_INTERVIEW_STAGE", "COMPLETE_APPROVAL_STAGE_AS_ADMINISTRATOR")
		AND STATE.id IN ("REVIEW", "INTERVIEW", "APPROVAL", "REJECTED")
;

INSERT INTO STATE_TRANSITION
	SELECT ACTION.id, STATE.id
	FROM ACTION INNER JOIN STATE
	WHERE ACTION.id = "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_APPROVER"
		AND STATE.id IN ("REVIEW", "INTERVIEW", "APPROVAL", "APPROVED", "REJECTED")
;

ALTER TABLE STATE_TRANSITION
	ADD COLUMN display_order INT(1) UNSIGNED,
	ADD INDEX (display_order)
;

UPDATE STATE_TRANSITION
SET display_order = 0 
WHERE state_id = "REVIEW"
;

UPDATE STATE_TRANSITION
SET display_order = 1 
WHERE state_id = "INTERVIEW"
;

UPDATE STATE_TRANSITION
SET display_order = 2 
WHERE state_id = "APPROVAL"
;

UPDATE STATE_TRANSITION
SET display_order = 3 
WHERE state_id = "REJECTED"
	AND action_id != "COMPLETE_APPROVAL_STAGE_AS_APPROVER"
;

UPDATE STATE_TRANSITION
SET display_order = 3 
WHERE state_id = "APPROVED"
	AND action_id = "COMPLETE_APPROVAL_STAGE_AS_APPROVER"
;

UPDATE STATE_TRANSITION
SET display_order = 4 
WHERE state_id = "REJECTED"
	AND action_id = "COMPLETE_APPROVAL_STAGE_AS_APPROVER"
;

ALTER TABLE APPLICATION
	CHANGE COLUMN status state_id VARCHAR(50) NOT NULL,
	CHANGE COLUMN last_status last_state_id VARCHAR(50),
	CHANGE COLUMN next_status next_state_id VARCHAR(50)
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE STATE
SET id = CONCAT("APPLICATION_", id)
;

UPDATE APPLICATION
SET state_id = CONCAT("APPLICATION_", state_id)
;

UPDATE APPLICATION
SET last_state_id = CONCAT("APPLICATION_", last_state_id)
;

UPDATE APPLICATION
SET next_state_id = CONCAT("APPLICATION_", next_state_id)
;

UPDATE APPLICATION_ACTION_OPTIONAL
SET state_id = CONCAT("APPLICATION_", state_id)
;

SET FOREIGN_KEY_CHECKS = 1
;

ALTER TABLE ACTION
	ADD COLUMN scope_id VARCHAR(50) AFTER id,
	ADD INDEX (scope_id),
	ADD FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
;

UPDATE ACTION
SET scope_id = "APPLICATION"
;

ALTER TABLE ACTION
	MODIFY COLUMN scope_id VARCHAR(50) NOT NULL
;

SET FOREIGN_KEY_CHECKS = 0
;

ALTER TABLE ACTION
	MODIFY id VARCHAR(100) NOT NULL
;

UPDATE ACTION
SET id = CONCAT("APPLICATION_", id)
;

ALTER TABLE APPLICATION_ACTION_OPTIONAL
	MODIFY action_id VARCHAR(100) NOT NULL
;

UPDATE APPLICATION_ACTION_OPTIONAL
SET action_id = CONCAT("APPLICATION_", action_id)
;

ALTER TABLE APPLICATION_ACTION_REQUIRED
	MODIFY action_id VARCHAR(100) NOT NULL
;

UPDATE APPLICATION_ACTION_REQUIRED
SET action_id = CONCAT("APPLICATION_", action_id)
;

ALTER TABLE STATE_TRANSITION
	MODIFY action_id VARCHAR(100) NOT NULL
;

UPDATE STATE_TRANSITION
SET action_id = CONCAT("APPLICATION_", action_id)
;

ALTER TABLE USER
	MODIFY action_id VARCHAR(100)
;

UPDATE USER
SET action_id = CONCAT("APPLICATION_", action_id)
;

SET FOREIGN_KEY_CHECKS = 1
;

CREATE TABLE PROGRAM_STATE_DURATION (
	program_id INT(10) UNSIGNED NOT NULL,
	state_id VARCHAR(50) NOT NULL,
	duration INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (program_id, state_id),
	INDEX (state_id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (state_id) REFERENCES STATE (id)
) ENGINE = INNODB
	SELECT PROGRAM.id AS program_id, STATE.id AS state_id, 
		STATE.duration AS duration 
	FROM PROGRAM INNER JOIN STATE
	WHERE STATE.duration IS NOT NULL
;