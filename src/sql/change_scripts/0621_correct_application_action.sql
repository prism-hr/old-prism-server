INSERT INTO ACTION (id, notification) 
VALUES ("CORRECT_APPLICATION", "INDIVIDUAL"),
	("COMPLETE_APPLICATION", NULL),
	("EDIT_AS_ADMINISTRATOR", NULL),
	("EDIT_AS_APPLICANT", NULL)
;

INSERT INTO STAGE_DURATION (stage, duration, unit)
	VALUES("UNSUBMITTED", 4, "WEEKS")
;

UPDATE APPLICATION_FORM
SET due_date = app_date_time + INTERVAL 4 WEEK
WHERE status = "UNSUBMITTED"
;

INSERT INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id, action_id, deadline_timestamp,
	bind_deadline_to_due_date, raises_urgent_flag, assigned_timestamp)
	SELECT APPLICATION_FORM_USER_ROLE.id, "COMPLETE_APPLICATION", APPLICATION_FORM.due_date, 1,
		IF(APPLICATION_FORM.due_date < CURRENT_DATE(), 1, 0), APPLICATION_FORM.app_date_time
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	WHERE APPLICATION_FORM_USER_ROLE.application_role_id = "APPLICANT"
		AND APPLICATION_FORM.status = "UNSUBMITTED"
;

DELETE FROM APPLICATION_FORM_ACTION_OPTIONAL
WHERE action_id = "VIEW_EDIT"
	AND application_role_id = "APPLICANT"
	AND state_id = "UNSUBMITTED"
;

UPDATE APPLICATION_FORM_ACTION_OPTIONAL
SET action_id = "EDIT_AS_APPLICANT"
WHERE action_id = "VIEW_EDIT"
	AND application_role_id = "APPLICANT"
;

UPDATE APPLICATION_FORM_ACTION_OPTIONAL
SET action_id = "EDIT_AS_ADMINISTRATOR"
WHERE action_id = "VIEW_EDIT"
;

DELETE FROM action
WHERE id = "VIEW_EDIT"
;

CREATE TABLE ACTION_TYPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)) 
ENGINE = INNODB
	SELECT id
	FROM action
	WHERE id NOT IN ("COMPLETE_APPLICATION", "CORRECT_APPLICATION", 
		"EDIT_AS_ADMINISTRATOR", "EDIT_AS_APPLICANT", "VIEW")
;

ALTER TABLE ACTION
	ADD COLUMN action_type_id VARCHAR(50) AFTER id,
	ADD COLUMN precedence INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER action_type_id, 
	ADD INDEX (action_type_id),
	ADD UNIQUE INDEX (action_type_id, precedence),
	ADD FOREIGN KEY (action_type_id) REFERENCES ACTION_TYPE (id)
;

UPDATE ACTION INNER JOIN ACTION_TYPE
ON ACTION.id = ACTION_TYPE.id
	SET ACTION.action_type_id = ACTION_TYPE.id
;

INSERT INTO ACTION_TYPE (id)
VALUES("VIEW_EDIT")
;

UPDATE ACTION
SET action_type_id = "VIEW_EDIT",
	precedence = 4
WHERE id = "COMPLETE_APPLICATION"
;

UPDATE ACTION
SET action_type_id = "VIEW_EDIT",
	precedence = 3
WHERE id = "EDIT_AS_APPLICANT"
;

UPDATE ACTION
SET action_type_id = "VIEW_EDIT",
	precedence = 2
WHERE id = "CORRECT_APPLICATION"
;

UPDATE ACTION
SET action_type_id = "VIEW_EDIT",
	precedence = 1
WHERE id = "EDIT_AS_ADMINISTRATOR"
;

UPDATE ACTION
SET action_type_id = "VIEW_EDIT"
WHERE id = "VIEW"
;

ALTER TABLE ACTION
	MODIFY action_type_id VARCHAR(50) NOT NULL
;

DROP PROCEDURE SP_SELECT_USER_ACTIONS
;

CREATE PROCEDURE SP_SELECT_USER_ACTIONS (
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_registered_user_id INT(10) UNSIGNED,
	IN in_action_id VARCHAR(50),
	IN in_action_type_id VARCHAR(50))
BEGIN

CREATE TEMPORARY TABLE USER_ACTION_LIST (
		action_id VARCHAR(50) NOT NULL,
		action_type_id VARCHAR(50) NOT NULL,
		precedence INT(1) UNSIGNED NOT NULL,
		raises_urgent_flag INT(1) UNSIGNED NOT NULL DEFAULT 0,
		return_group INT(1) UNSIGNED NOT NULL DEFAULT 0,
		PRIMARY KEY (action_id),
		UNIQUE INDEX (action_type_id),
		INDEX (return_group, action_id)
	) ENGINE = MEMORY;
		
	INSERT INTO USER_ACTION_LIST (action_id, action_type_id, precedence, raises_urgent_flag, return_group)
		SELECT *
		FROM (
			SELECT APPLICATION_FORM_ACTION_REQUIRED.action_id AS action_id, ACTION.action_type_id AS action_type_id, 
				ACTION.precedence AS precedence,
				MAX(APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag) AS raises_urgent_flag,
				MAX(APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag) + 1 AS return_group
		 	FROM APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
			 	ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
			INNER JOIN ACTION
				ON APPLICATION_FORM_ACTION_REQUIRED.action_id = ACTION.id
		 	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
			 	AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			 	AND (in_action_id IS NULL
			 		OR APPLICATION_FORM_ACTION_REQUIRED.action_id = in_action_id)
			 	AND (in_action_type_id IS NULL
			 		OR ACTION.action_type_id = in_action_type_id)
		 	GROUP BY APPLICATION_FORM_ACTION_REQUIRED.action_id) AS ACTION_REQUIRED
	ON DUPLICATE KEY UPDATE
		USER_ACTION_LIST.action_id = IF (
			USER_ACTION_LIST.precedence < ACTION_REQUIRED.precedence, 
			ACTION_REQUIRED.action_id,
			USER_ACTION_LIST.action_id),
		USER_ACTION_LIST.precedence = IF (
			USER_ACTION_LIST.precedence < ACTION_REQUIRED.precedence,
			ACTION_REQUIRED.precedence,
			USER_ACTION_LIST.precedence);
	
	INSERT INTO USER_ACTION_LIST (action_id, action_type_id, precedence)
		SELECT *
		FROM (
			SELECT APPLICATION_FORM_ACTION_OPTIONAL.action_id, ACTION.action_type_id, ACTION.precedence
		 	FROM APPLICATION_FORM_ACTION_OPTIONAL INNER JOIN APPLICATION_FORM_USER_ROLE
				ON APPLICATION_FORM_ACTION_OPTIONAL.application_role_id = APPLICATION_FORM_USER_ROLE.application_role_id
			INNER JOIN APPLICATION_FORM
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
			INNER JOIN ACTION
				ON APPLICATION_FORM_ACTION_OPTIONAL.action_id = ACTION.id
			WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
				AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			 	AND APPLICATION_FORM_ACTION_OPTIONAL.state_id = APPLICATION_FORM.status
			AND (in_action_id IS NULL
			 		OR APPLICATION_FORM_ACTION_OPTIONAL.action_id = in_action_id)
			 	AND (in_action_type_id IS NULL
			 		OR ACTION.action_type_id = in_action_type_id)
		 	GROUP BY APPLICATION_FORM_ACTION_OPTIONAL.action_id) AS ACTION_OPTIONAL
	ON DUPLICATE KEY UPDATE
		USER_ACTION_LIST.action_id = IF (
			USER_ACTION_LIST.precedence < ACTION_OPTIONAL.precedence, 
			ACTION_OPTIONAL.action_id,
			USER_ACTION_LIST.action_id),
		USER_ACTION_LIST.precedence = IF (
			USER_ACTION_LIST.precedence < ACTION_OPTIONAL.precedence,
			ACTION_OPTIONAL.precedence,
			USER_ACTION_LIST.precedence);
		
	SELECT action_id, raises_urgent_flag
	FROM USER_ACTION_LIST
	ORDER BY return_group DESC, action_id;
	
	DROP TABLE USER_ACTION_LIST;

END
;

ALTER TABLE APPLICATION_FORM
	CHANGE status_when_withdrawn last_status VARCHAR(50)
;

ALTER TABLE APPLICATION_FORM
	CHANGE batch_deadline closing_date DATE
;
