ALTER TABLE APPLICATION_ROLE
	ADD COLUMN update_visibility INT(1) UNSIGNED DEFAULT 1,
	ADD INDEX (update_visibility)
;

DROP TABLE APPLICATION_FORM_USER_ROLE
;

DELETE FROM APPLICATION_ROLE
WHERE id IN ("PROJECTADMINISTRATOR", "REVIEWADMINISTRATOR", "INTERVIEWADMINISTRATOR", "APPROVALADMINISTRATOR", "INTERVIEWPARTICIPANT")
;

UPDATE APPLICATION_ROLE
SET update_visibility = 0
WHERE id = "APPLICANT"
;
	
INSERT INTO APPLICATION_ROLE (id, update_visibility)
	SELECT "PROJECTADMINISTRATOR", 1
		UNION
	SELECT "REVIEWADMINISTRATOR", 1
		UNION
	SELECT "INTERVIEWADMINISTRATOR", 1
		UNION
	SELECT "APPROVALADMINISTRATOR", 1
;

CREATE TABLE ACTION (
	id VARCHAR(50) NOT NULL,
	notification ENUM ("INDIVIDUAL", "SYNDICATED"),
	PRIMARY KEY(id),
	INDEX (notification))
ENGINE = INNODB
;

INSERT INTO ACTION (id, notification)
	SELECT "VIEW", NULL
		UNION
	SELECT "VIEW_EDIT", NULL
		UNION
	SELECT "EMAIL_APPLICANT", NULL
		UNION
	SELECT "COMMENT", NULL
		UNION
	SELECT "COMPLETE_VALIDATION_STAGE", "SYNDICATED"
		UNION
	SELECT "ASSIGN_REVIEWERS", "SYNDICATED"
		UNION
	SELECT "PROVIDE_REVIEW", "SYNDICATED"
		UNION
	SELECT "COMPLETE_REVIEW_STAGE", "SYNDICATED"
		UNION
	SELECT "ASSIGN_INTERVIEWERS", "SYNDICATED"
		UNION
	SELECT "PROVIDE_INTERVIEW_AVAILABILITY", "INDIVIDUAL"
		UNION
	SELECT "CONFIRM_INTERVIEW_ARRANGEMENTS", "SYNDICATED"
		UNION
	SELECT "RETRACT_INTERVIEW_AVAILABILITY", NULL
		UNION
	SELECT "PROVIDE_INTERVIEW_FEEDBACK", "SYNDICATED"
		UNION
	SELECT "COMPLETE_INTERVIEW_STAGE", "SYNDICATED"
		UNION
	SELECT "ASSIGN_SUPERVISORS", "SYNDICATED"
		UNION
	SELECT "CONFIRM_PRIMARY_SUPERVISION", "SYNDICATED"
		UNION
	SELECT "COMPLETE_APPROVAL_STAGE", "SYNDICATED"
		UNION
	SELECT "CONFIRM_OFFER_RECOMMENDATION", "SYNDICATED"
		UNION
	SELECT "CONFIRM_REJECTION", "SYNDICATED"
		UNION
	SELECT "WITHDRAW", NULL
		UNION
	SELECT "PROVIDE_REFERENCE", "INDIVIDUAL"
		UNION
	SELECT "CONFIRM_ELIGIBILITY", "SYNDICATED"
		UNION
	SELECT "MOVE_TO_DIFFERENT_STAGE", NULL
;

CREATE TABLE APPLICATION_FORM_USER_ROLE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	application_form_id INT(10) UNSIGNED NOT NULL,
	registered_user_id INT(10) UNSIGNED NOT NULL,
	application_role_id VARCHAR(50) NOT NULL,
	is_interested_in_applicant INT(1),
	next_required_action_id VARCHAR(50),
	next_required_action_deadline DATE,
	bind_deadline_to_due_date INT(1) UNSIGNED,
	next_required_action_id2 VARCHAR(50),
	next_required_action_deadline2 DATE,
	bind_deadline_to_due_date2 INT(1) UNSIGNED,
	PRIMARY KEY (id),
	UNIQUE INDEX (application_form_id, registered_user_id, application_role_id),
	INDEX (application_role_id),
	INDEX (registered_user_id),
	INDEX (is_interested_in_applicant),
	FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM (id),
	FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER (id),
	FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE (id))
ENGINE = INNODB
;

CREATE TABLE APPLICATION_FORM_ACTION_REQUIRED (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	application_form_user_role_id INT(10) UNSIGNED NOT NULL,
	action_id VARCHAR(50) NOT NULL,
	deadline_timestamp DATE NOT NULL,
	bind_deadline_to_due_date INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (application_form_user_role_id, action_id),
	INDEX (action_id),
	INDEX (deadline_timestamp),
	INDEX (bind_deadline_to_due_date),
	FOREIGN KEY (application_form_user_role_id) REFERENCES APPLICATION_FORM_USER_ROLE (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id))
ENGINE = INNODB
;

CREATE TABLE STATE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY(id))
ENGINE = INNODB
;

INSERT INTO STATE (id)
	SELECT "UNSUBMITTED"
		UNION
	SELECT "VALIDATION"
		UNION
	SELECT "REVIEW"
		UNION
	SELECT "INTERVIEW"
		UNION
	SELECT "APPROVAL"
		UNION
	SELECT "APPROVED"
		UNION
	SELECT "REJECTED"
		UNION
	SELECT "WITHDRAWN"
;

CREATE TABLE APPLICATION_FORM_ACTION_OPTIONAL (
	application_role_id VARCHAR(50) NOT NULL,
	state_id VARCHAR(50) NOT NULL,
	action_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (application_role_id, state_id, action_id),
	INDEX (state_id),
	INDEX (action_id),
	FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE (id),
	FOREIGN KEY (state_id) REFERENCES STATE (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id))
ENGINE = INNODB
;

INSERT INTO APPLICATION_FORM_ACTION_OPTIONAL (application_role_id, state_id, action_id)
	SELECT "APPLICANT", STATE.id, ACTION.id
	FROM STATE INNER JOIN ACTION
	WHERE STATE.id IN ("UNSUBMITTED", "VALIDATION", "REVIEW", "INTERVIEW")
		AND ACTION.id IN ("VIEW_EDIT", "WITHDRAW")
;

INSERT INTO APPLICATION_FORM_ACTION_OPTIONAL (application_role_id, state_id, action_id)
	SELECT "APPLICANT", STATE.id, ACTION.id
	FROM STATE INNER JOIN ACTION
	WHERE STATE.id IN ("APPROVAL", "APPROVED", "REJECTED")
		AND ACTION.id IN ("VIEW", "WITHDRAW")
;

INSERT INTO APPLICATION_FORM_ACTION_OPTIONAL (application_role_id, state_id, action_id)
	SELECT "APPLICANT", STATE.id, ACTION.id
	FROM STATE INNER JOIN ACTION
	WHERE STATE.id = "WITHDRAWN"
		AND ACTION.id = "VIEW"
;

INSERT INTO APPLICATION_FORM_ACTION_OPTIONAL (application_role_id, state_id, action_id)
	SELECT APPLICATION_ROLE.id, STATE.id, ACTION.id
	FROM APPLICATION_ROLE INNER JOIN STATE INNER JOIN ACTION
	WHERE APPLICATION_ROLE.id LIKE "%ADMINISTRATOR%"
		AND STATE.id IN ("VALIDATION", "REVIEW", "INTERVIEW")
		AND ACTION.id IN ("COMMENT", "EMAIL_APPLICANT", "VIEW_EDIT")
;

INSERT INTO APPLICATION_FORM_ACTION_OPTIONAL (application_role_id, state_id, action_id)
	SELECT APPLICATION_ROLE.id, STATE.id, ACTION.id
	FROM APPLICATION_ROLE INNER JOIN STATE INNER JOIN ACTION
	WHERE APPLICATION_ROLE.id != "APPLICANT" 
		AND APPLICATION_ROLE.id NOT LIKE "%ADMINISTRATOR%"
		AND STATE.id IN ("REVIEW", "INTERVIEW", "APPROVAL", "APPROVED", "REJECTED", "WITHDRAWN")
		AND ACTION.id IN ("COMMENT", "EMAIL_APPLICANT", "VIEW")
;

ALTER TABLE APPLICATION_FORM_UPDATE
	ADD INDEX (update_timestamp),
	ADD INDEX (update_visibility)
;

ALTER TABLE APPLICATION_FORM_LAST_ACCESS
	ADD INDEX (last_access_timestamp)
;