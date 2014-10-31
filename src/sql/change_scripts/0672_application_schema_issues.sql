/* Rename for consistency */

RENAME TABLE ACTION_VISIBILITY_EXCLUSION_RULE TO ACTION_REDACTION_TYPE
;

ALTER TABLE ACTION_VISIBILITY_EXCLUSION
	CHANGE COLUMN action_visibility_exclusion_rule_id action_redaction_type_id VARCHAR(50) NOT NULL
;

RENAME TABLE ACTION_VISIBILITY_EXCLUSION TO ACTION_REDACTION
;

/* Better performant model for action precedence */

CREATE TABLE ACTION_ENHANCEMENT_TYPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
;

INSERT INTO ACTION_ENHANCEMENT_TYPE
VALUES ("APPLICATION_EDIT_ALL_DATA"),
	("APPLICATION_EDIT_REFERENCE_DATA"),
	("APPLICATION_VIEW_CREATOR_DATA"),
	("APPLICATION_VIEW_REFERENCE_DATA"),
	("PROGRAM_EDIT_REQUEST_DATA")
;

/* Map existing precedence values to new model */

CREATE TABLE STATE_ACTION_ENHANCEMENT (	
	state_action_assignment_id INT(10) UNSIGNED NOT NULL,
	action_enhancement_type_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (state_action_assignment_id, action_enhancement_type_id),
	INDEX (action_enhancement_type_id),
	FOREIGN KEY (state_action_assignment_id) REFERENCES STATE_ACTION_ASSIGNMENT (id),
	FOREIGN KEY (action_enhancement_type_id) REFERENCES ACTION_ENHANCEMENT_TYPE (id)
) ENGINE = INNODB
;

INSERT INTO STATE_ACTION_ENHANCEMENT
	SELECT STATE_ACTION_ASSIGNMENT.id, "APPLICATION_EDIT_ALL_DATA"
	FROM STATE_ACTION_ASSIGNMENT INNER JOIN STATE_ACTION
		ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_EDIT_AS_CREATOR", "APPLICATION_CORRECT")
;

INSERT INTO STATE_ACTION_ENHANCEMENT
	SELECT STATE_ACTION_ASSIGNMENT.id, "APPLICATION_EDIT_REFERENCE_DATA"
	FROM STATE_ACTION_ASSIGNMENT INNER JOIN STATE_ACTION
		ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
	WHERE STATE_ACTION.action_id = "APPLICATION_EDIT_AS_ADMINISTRATOR"
;

INSERT INTO STATE_ACTION_ENHANCEMENT
	SELECT STATE_ACTION_ASSIGNMENT.id, "APPLICATION_VIEW_CREATOR_DATA"
	FROM STATE_ACTION_ASSIGNMENT INNER JOIN STATE_ACTION
		ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
	WHERE STATE_ACTION.action_id = "APPLICATION_VIEW_AS_CREATOR"
;

INSERT INTO STATE_ACTION_ENHANCEMENT
	SELECT STATE_ACTION_ASSIGNMENT.id, "APPLICATION_VIEW_CREATOR_DATA"
	FROM STATE_ACTION_ASSIGNMENT INNER JOIN STATE_ACTION
		ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
	WHERE STATE_ACTION.action_id = "APPLICATION_VIEW_AS_RECRUITER"
		AND STATE_ACTION_ASSIGNMENT.role_id IN ("INSTITUTION_ADMINISTRATOR", "INSTITUTION_ADMITTER")
;

INSERT INTO STATE_ACTION_ENHANCEMENT
	SELECT STATE_ACTION_ASSIGNMENT.id, "APPLICATION_VIEW_REFERENCE_DATA"
	FROM STATE_ACTION_ASSIGNMENT INNER JOIN STATE_ACTION
		ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
	WHERE STATE_ACTION.action_id = "APPLICATION_VIEW_AS_RECRUITER"
;

INSERT INTO STATE_ACTION_ENHANCEMENT
	SELECT STATE_ACTION_ASSIGNMENT.id, "PROGRAM_EDIT_REQUEST_DATA"
	FROM STATE_ACTION_ASSIGNMENT INNER JOIN STATE_ACTION
		ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
	WHERE STATE_ACTION.action_id = "PROGRAM_EDIT"
;

INSERT INTO ACTION (id, action_type_id, scope_id)
VALUES ("APPLICATION_VIEW_EDIT", "USER_INVOCATION", "APPLICATION"),
	("PROGRAM_VIEW_EDIT", "USER_INVOCATION", "PROGRAM")
;

ALTER TABLE STATE_ACTION
	DROP COLUMN precedence
;

INSERT INTO STATE_ACTION(state_id, action_id, raises_urgent_flag, is_default_action)
	SELECT STATE_ACTION.state_id, "APPLICATION_VIEW_EDIT", 0, 1
	FROM STATE_ACTION
	WHERE STATE_ACTION.action_id IN (
		"APPLICATION_CORRECT",
		"APPLICATION_EDIT_AS_ADMINISTRATOR",
		"APPLICATION_EDIT_AS_CREATOR",
		"APPLICATION_VIEW_AS_CREATOR",
		"APPLICATION_VIEW_AS_RECRUITER",
		"APPLICATION_VIEW_AS_REFEREE")
	GROUP BY STATE_ACTION.state_id
;

UPDATE IGNORE STATE_ACTION_ASSIGNMENT INNER JOIN STATE_ACTION
	ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
INNER JOIN (
	SELECT state_id AS state_id,
		id AS state_action_id
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_VIEW_EDIT") AS APPLICATION_VIEW_EDIT
	ON STATE_ACTION.state_id = APPLICATION_VIEW_EDIT.state_id
SET STATE_ACTION_ASSIGNMENT.state_action_id = APPLICATION_VIEW_EDIT.state_action_id
	WHERE STATE_ACTION.action_id IN (
		"APPLICATION_CORRECT",
		"APPLICATION_EDIT_AS_ADMINISTRATOR",
		"APPLICATION_EDIT_AS_CREATOR",
		"APPLICATION_VIEW_AS_CREATOR",
		"APPLICATION_VIEW_AS_RECRUITER",
		"APPLICATION_VIEW_AS_REFEREE")
;

INSERT INTO STATE_ACTION(state_id, action_id, raises_urgent_flag, is_default_action)
	SELECT STATE_ACTION.state_id, "PROGRAM_VIEW_EDIT", 0, 1
	FROM STATE_ACTION
	WHERE STATE_ACTION.action_id IN (
		"PROGRAM_VIEW",
		"PROGRAM_EDIT")
	GROUP BY STATE_ACTION.state_id
;

UPDATE IGNORE STATE_ACTION_ASSIGNMENT INNER JOIN STATE_ACTION
	ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
INNER JOIN (
	SELECT state_id AS state_id,
		id AS state_action_id
	FROM STATE_ACTION
	WHERE action_id = "PROGRAM_VIEW_EDIT") AS APPLICATION_VIEW_EDIT
	ON STATE_ACTION.state_id = APPLICATION_VIEW_EDIT.state_id
SET STATE_ACTION_ASSIGNMENT.state_action_id = APPLICATION_VIEW_EDIT.state_action_id
	WHERE STATE_ACTION.action_id IN (
		"PROGRAM_VIEW",
		"PROGRAM_EDIT")
;

UPDATE STATE_ACTION INNER JOIN STATE_ACTION_ASSIGNMENT 
	ON STATE_ACTION.id = STATE_ACTION_ASSIGNMENT.state_action_id
INNER JOIN STATE_ACTION_ENHANCEMENT
	ON STATE_ACTION_ASSIGNMENT.id = STATE_ACTION_ENHANCEMENT.state_action_assignment_id
INNER JOIN (
	SELECT STATE_ACTION_ASSIGNMENT.id AS assignment_id,
		STATE_ACTION.state_id AS state_id,
		STATE_ACTION_ASSIGNMENT.role_id AS role_id
	FROM STATE_ACTION INNER JOIN STATE_ACTION_ASSIGNMENT
		ON STATE_ACTION.id = STATE_ACTION_ASSIGNMENT.state_action_id
	WHERE STATE_ACTION.action_id IN (
		"APPLICATION_VIEW_EDIT",
		"PROGRAM_VIEW_EDIT")) AS NEW_ASSIGNMENT
	ON STATE_ACTION.state_id = NEW_ASSIGNMENT.state_id
	AND STATE_ACTION_ASSIGNMENT.role_id = NEW_ASSIGNMENT.role_id
SET STATE_ACTION_ENHANCEMENT.state_action_assignment_id = NEW_ASSIGNMENT.assignment_id
;

UPDATE STATE_ACTION_NOTIFICATION INNER JOIN STATE_ACTION
	ON STATE_ACTION_NOTIFICATION.state_action_id = STATE_ACTION.id
INNER JOIN (
	SELECT state_id AS state_id,
		id AS state_action_id
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_VIEW_EDIT") AS APPLICATION_VIEW_EDIT
	ON STATE_ACTION.state_id = APPLICATION_VIEW_EDIT.state_id
SET STATE_ACTION_NOTIFICATION.state_action_id = APPLICATION_VIEW_EDIT.state_action_id
	WHERE STATE_ACTION.action_id IN (
		"APPLICATION_CORRECT",
		"APPLICATION_EDIT_AS_ADMINISTRATOR",
		"APPLICATION_EDIT_AS_CREATOR",
		"APPLICATION_VIEW_AS_CREATOR",
		"APPLICATION_VIEW_AS_RECRUITER",
		"APPLICATION_VIEW_AS_REFEREE")
;

UPDATE STATE_ACTION_NOTIFICATION INNER JOIN STATE_ACTION
	ON STATE_ACTION_NOTIFICATION.state_action_id = STATE_ACTION.id
INNER JOIN (
	SELECT state_id AS state_id,
		id AS state_action_id
	FROM STATE_ACTION
	WHERE action_id = "PROGRAM_VIEW_EDIT") AS APPLICATION_VIEW_EDIT
	ON STATE_ACTION.state_id = APPLICATION_VIEW_EDIT.state_id
SET STATE_ACTION_NOTIFICATION.state_action_id = APPLICATION_VIEW_EDIT.state_action_id
	WHERE STATE_ACTION.action_id IN (
		"PROGRAM_VIEW",
		"PROGRAM_EDIT")
;

UPDATE IGNORE STATE_TRANSITION INNER JOIN STATE_ACTION
	ON STATE_TRANSITION.state_action_id = STATE_ACTION.id
INNER JOIN (
	SELECT state_id AS state_id,
		id AS state_action_id
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_VIEW_EDIT") AS APPLICATION_VIEW_EDIT
	ON STATE_ACTION.state_id = APPLICATION_VIEW_EDIT.state_id
SET STATE_TRANSITION.state_action_id = APPLICATION_VIEW_EDIT.state_action_id
	WHERE STATE_ACTION.action_id IN (
		"APPLICATION_CORRECT",
		"APPLICATION_EDIT_AS_ADMINISTRATOR",
		"APPLICATION_EDIT_AS_CREATOR",
		"APPLICATION_VIEW_AS_CREATOR",
		"APPLICATION_VIEW_AS_RECRUITER",
		"APPLICATION_VIEW_AS_REFEREE")
;

UPDATE IGNORE STATE_TRANSITION INNER JOIN STATE_ACTION
	ON STATE_TRANSITION.state_action_id = STATE_ACTION.id
INNER JOIN (
	SELECT state_id AS state_id,
		id AS state_action_id
	FROM STATE_ACTION
	WHERE action_id = "PROGRAM_VIEW_EDIT") AS APPLICATION_VIEW_EDIT
	ON STATE_ACTION.state_id = APPLICATION_VIEW_EDIT.state_id
SET STATE_TRANSITION.state_action_id = APPLICATION_VIEW_EDIT.state_action_id
	WHERE STATE_ACTION.action_id IN (
		"PROGRAM_VIEW",
		"PROGRAM_EDIT")
;

DELETE STATE_ACTION_ASSIGNMENT
FROM STATE_ACTION_ASSIGNMENT INNER JOIN STATE_ACTION
	ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
WHERE STATE_ACTION.action_id IN (
	"APPLICATION_CORRECT",
	"APPLICATION_EDIT_AS_ADMINISTRATOR",
	"APPLICATION_EDIT_AS_CREATOR",
	"APPLICATION_VIEW_AS_CREATOR",
	"APPLICATION_VIEW_AS_RECRUITER",
	"APPLICATION_VIEW_AS_REFEREE",
	"PROGRAM_VIEW",
	"PROGRAM_EDIT")
;

DELETE STATE_ACTION_NOTIFICATION
FROM STATE_ACTION_NOTIFICATION INNER JOIN STATE_ACTION
	ON STATE_ACTION_NOTIFICATION.state_action_id = STATE_ACTION.id
WHERE STATE_ACTION.action_id IN (
	"APPLICATION_CORRECT",
	"APPLICATION_EDIT_AS_ADMINISTRATOR",
	"APPLICATION_EDIT_AS_CREATOR",
	"APPLICATION_VIEW_AS_CREATOR",
	"APPLICATION_VIEW_AS_RECRUITER",
	"APPLICATION_VIEW_AS_REFEREE",
	"PROGRAM_VIEW",
	"PROGRAM_EDIT")
;

DELETE STATE_TRANSITION
FROM STATE_TRANSITION INNER JOIN STATE_ACTION
	ON STATE_TRANSITION.state_action_id = STATE_ACTION.id
WHERE STATE_ACTION.action_id IN (
	"APPLICATION_CORRECT",
	"APPLICATION_EDIT_AS_ADMINISTRATOR",
	"APPLICATION_EDIT_AS_CREATOR",
	"APPLICATION_VIEW_AS_CREATOR",
	"APPLICATION_VIEW_AS_RECRUITER",
	"APPLICATION_VIEW_AS_REFEREE",
	"PROGRAM_VIEW",
	"PROGRAM_EDIT")
;

DELETE 
FROM STATE_ACTION
WHERE action_id IN (
	"APPLICATION_CORRECT",
	"APPLICATION_EDIT_AS_ADMINISTRATOR",
	"APPLICATION_EDIT_AS_CREATOR",
	"APPLICATION_VIEW_AS_CREATOR",
	"APPLICATION_VIEW_AS_RECRUITER",
	"APPLICATION_VIEW_AS_REFEREE",
	"PROGRAM_VIEW",
	"PROGRAM_EDIT")
;

UPDATE ACTION_REDACTION
SET action_id = "APPLICATION_VIEW_EDIT"
WHERE action_id = "APPLICATION_EDIT_AS_ADMINISTRATOR"
;

DELETE
FROM ACTION_REDACTION
WHERE action_id = "APPLICATION_CORRECT"
;

DELETE FROM ACTION_REDACTION_TYPE
WHERE id = "EVERYTHING"
;

DELETE STATE_ACTION_ENHANCEMENT.*
FROM STATE_ACTION_ENHANCEMENT INNER JOIN STATE_ACTION_ASSIGNMENT 
	ON STATE_ACTION_ENHANCEMENT.state_action_assignment_id = STATE_ACTION_ASSIGNMENT.id
INNER JOIN STATE_ACTION
	ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
WHERE STATE_ACTION_ASSIGNMENT.role_id LIKE "SYSTEM%"
	AND STATE_ACTION.action_id LIKE "APPLICATION%"
;

DELETE STATE_ACTION_ASSIGNMENT.*
FROM STATE_ACTION_ASSIGNMENT INNER JOIN STATE_ACTION
	ON STATE_ACTION_ASSIGNMENT.state_action_id = STATE_ACTION.id
WHERE STATE_ACTION_ASSIGNMENT.role_id LIKE "SYSTEM%"
	AND STATE_ACTION.action_id LIKE "APPLICATION%"
;

ALTER TABLE STATE_ACTION_ENHANCEMENT
	DROP PRIMARY KEY,
	ADD COLUMN id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	ADD PRIMARY KEY (id),
	ADD UNIQUE INDEX (state_action_assignment_id, action_enhancement_type_id),
	ADD COLUMN delegated_action_id VARCHAR(100),
	ADD INDEX (delegated_action_id),
	ADD FOREIGN KEY (delegated_action_id) REFERENCES ACTION (id)
;

UPDATE STATE_ACTION_ENHANCEMENT
SET delegated_action_id = "APPLICATION_PROVIDE_REFERENCE"
WHERE action_enhancement_type_id = "APPLICATION_EDIT_REFERENCE_DATA"
;

ALTER TABLE ACTION
	DROP FOREIGN KEY action_ibfk_1,
	DROP COLUMN delegate_action_id
;

UPDATE STATE_TRANSITION
SET transition_action_id = "APPLICATION_VIEW_EDIT"
WHERE transition_action_id IN (
	"APPLICATION_CORRECT",
	"APPLICATION_EDIT_AS_ADMINISTRATOR",
	"APPLICATION_EDIT_AS_CREATOR",
	"APPLICATION_VIEW_AS_CREATOR",
	"APPLICATION_VIEW_AS_RECRUITER",
	"APPLICATION_VIEW_AS_REFEREE")
;

UPDATE STATE_TRANSITION
SET transition_action_id = "PROGRAM_VIEW_EDIT"
WHERE transition_action_id IN (
	"PROGRAM_VIEW",
	"PROGRAM_EDIT")
;

DELETE
FROM ACTION_REDACTION
WHERE action_id = "APPLICATION_EDIT_AS_CREATOR"
;

DELETE 
FROM ACTION
WHERE id IN (
	"APPLICATION_CORRECT",
	"APPLICATION_EDIT_AS_ADMINISTRATOR",
	"APPLICATION_EDIT_AS_CREATOR",
	"APPLICATION_VIEW_AS_CREATOR",
	"APPLICATION_VIEW_AS_RECRUITER",
	"APPLICATION_VIEW_AS_REFEREE",
	"PROGRAM_VIEW",
	"PROGRAM_EDIT")
;
