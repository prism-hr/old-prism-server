/* Content access exclusions for timelines */

CREATE TABLE ACTION_VISIBILITY_EXCLUSION_RULE (
	id VARCHAR (50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
	SELECT "ALL_CONTENT" AS id
		UNION
	SELECT "ALL_ASSESSMENT_CONTENT"
		UNION
	SELECT "EVERYTHING"
;

CREATE TABLE ACTION_VISIBILITY_EXCLUSION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	action_id VARCHAR (100) NOT NULL,
	role_id VARCHAR (50) NOT NULL,
	action_visibility_exclusion_rule_id VARCHAR(50) NOT NULL,
	precedence INT(1) UNSIGNED,
	PRIMARY KEY (id),
	UNIQUE INDEX (action_id, role_id),
	INDEX (role_id),
	INDEX (action_visibility_exclusion_rule_id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (action_visibility_exclusion_rule_id) REFERENCES ACTION_VISIBILITY_EXCLUSION_RULE (id)
) ENGINE = INNODB
;

INSERT INTO ACTION_VISIBILITY_EXCLUSION (action_id, role_id, action_visibility_exclusion_rule_id, precedence)
	SELECT ACTION.id, ROLE.id, "ALL_CONTENT", NULL
	FROM ACTION INNER JOIN ROLE
	WHERE ACTION.id IN ("APPLICATION_ASSESS_ELIGIBILITY", "APPLICATION_ASSIGN_INTERVIEWERS",
		"APPLICATION_ASSIGN_REVIEWERS", "APPLICATION_ASSIGN_SUPERVISORS", "APPLICATION_COMMENT",
		"APPLICATION_COMPLETE_APPROVAL_STAGE", "APPLICATION_COMPLETE_INTERVIEW_STAGE",
		"APPLICATION_COMPLETE_REVIEW_STAGE", "APPLICATION_COMPLETE_VALIDATION_STAGE", 
		"APPLICATION_CONFIRM_ELIGIBILITY", "APPLICATION_CONFIRM_SUPERVISION", "APPLICATION_EDIT_AS_ADMINISTRATOR", 
		"APPLICATION_MOVE_TO_DIFFERENT_STAGE", "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY", 
		"APPLICATION_PROVIDE_INTERVIEW_FEEDBACK", "APPLICATION_PROVIDE_REVIEW", 
		"APPLICATION_UPDATE_INTERVIEW_AVAILABILITY")
		AND ROLE.id IN ("APPLICATION_CREATOR", "APPLICATION_REFEREE")
		UNION
	SELECT "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS", "APPLICATION_CREATOR", "ALL_ASSESSMENT_CONTENT", 1
		UNION
	SELECT "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS", "APPLICATION_REFEREE", "ALL_CONTENT", 0
		UNION
	SELECT "APPLICATION_PROVIDE_REFERENCE", "APPLICATION_CREATOR", "ALL_ASSESSMENT_CONTENT", 1
		UNION
	SELECT "APPLICATION_PROVIDE_REFERENCE", "APPLICATION_REFEREE", "ALL_CONTENT", 0
		UNION
	SELECT "APPLICATION_EDIT_AS_CREATOR", "APPLICATION_REFEREE", "ALL_CONTENT", 0
		UNION
	SELECT ACTION.id, ROLE.id, "EVERYTHING", NULL
	FROM ACTION INNER JOIN ROLE
	WHERE ACTION.id IN ("APPLICATION_CORRECT", "EXPORT")
		AND ROLE.id IN ("APPLICATION_CREATOR", "APPLICATION_REFEREE")
;
	
/* Realign custom question tables */

RENAME TABLE SCORING_DEFINITION TO COMMENT_CUSTOM_QUESTION
;

RENAME TABLE SCORE TO COMMENT_CUSTOM_QUESTION_RESPONSE
;

/* Container for appointment timeslots */

CREATE TABLE COMMENT_APPOINTMENT_TIMESLOT (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL,
	timeslot_datetime DATETIME NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (comment_id, timeslot_datetime),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id)
) ENGINE = INNODB
;

CREATE TABLE COMMENT_ASSIGNED_USER (
	id INT (10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL,
	user_id INT(10) UNSIGNED NOT NULL,
	role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (comment_id, user_id, role_id),
	INDEX (user_id),
	INDEX (role_id),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id),
	FOREIGN KEY (user_id) REFERENCES USER (id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id)
) ENGINE = INNODB
;

/* Tidy up comment and add support for other workflows */

ALTER TABLE COMMENT
	ADD COLUMN program_id INT(10) UNSIGNED AFTER id,
	ADD COLUMN project_id INT(10) UNSIGNED AFTER program_id,
	DROP FOREIGN KEY application_id_fk,
	CHANGE COLUMN application_form_id application_id INT(10) UNSIGNED,
	ADD COLUMN action_id VARCHAR(100) AFTER application_id,
	MODIFY COLUMN user_id INT(10) UNSIGNED NOT NULL AFTER action_id,
	ADD COLUMN role_id VARCHAR(50) AFTER user_id,
	ADD COLUMN delegate_user_id INT(10) UNSIGNED AFTER role_id,
	ADD COLUMN delegate_role_id VARCHAR (50) AFTER delegate_user_id,
	ADD COLUMN transition_state_id VARCHAR(50) AFTER content,
	ADD INDEX (program_id),
	ADD INDEX (project_id),
	ADD INDEX (action_id),
	ADD INDEX (role_id),
	ADD INDEX (delegate_user_id),
	ADD INDEX (delegate_role_id),
	ADD INDEX (transition_state_id),
	ADD FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	ADD FOREIGN KEY (project_id) REFERENCES PROJECT (id),
	ADD FOREIGN KEY (action_id) REFERENCES ACTION (id),
	ADD FOREIGN KEY (application_id) REFERENCES APPLICATION (id),
	ADD FOREIGN KEY (role_id) REFERENCES ROLE (id),
	ADD FOREIGN KEY (delegate_user_id) REFERENCES USER (id),
	ADD FOREIGN KEY (delegate_role_id) REFERENCES ROLE (id),
	ADD FOREIGN KEY (transition_state_id) REFERENCES STATE (id),
	CHANGE COLUMN COMMENT content TEXT
;

/* Create the program workflow comments */

INSERT INTO COMMENT (program_id, action_id, user_id, role_id, created_timestamp, transition_state_id)
	SELECT PROGRAM.id, "INSTITUTION_CREATE_PROGRAM", OPPORTUNITY_REQUEST.user_id, "SYSTEM_ADMINISTRATOR", OPPORTUNITY_REQUEST.created_date, "PROGRAM_APPROVAL"
	FROM PROGRAM INNER JOIN OPPORTUNITY_REQUEST
		ON PROGRAM.id = OPPORTUNITY_REQUEST.source_program_id
		UNION
	SELECT PROGRAM.id, "PROGRAM_COMPLETE_APPROVAL_STAGE", 1024, "SYSTEM_ADMINISTRATOR", MAX(OPPORTUNITY_REQUEST_COMMENT.created_timestamp), "PROGRAM_APPROVED"
	FROM PROGRAM INNER JOIN OPPORTUNITY_REQUEST
		ON PROGRAM.id = OPPORTUNITY_REQUEST.source_program_id
	INNER JOIN OPPORTUNITY_REQUEST_COMMENT
		ON OPPORTUNITY_REQUEST.id = OPPORTUNITY_REQUEST_COMMENT.opportunity_request_id
		AND OPPORTUNITY_REQUEST_COMMENT.comment_type = "APPROVE"
	GROUP BY PROGRAM.id
		UNION
	SELECT PROGRAM.id, "PROGRAM_CONFIGURE", ADVERT.user_id, "PROGRAM_ADMINISTRATOR", NOW(), "PROGRAM_DEACTIVATED"
	FROM PROGRAM INNER JOIN ADVERT
		ON PROGRAM.id = ADVERT.id
	WHERE PROGRAM.state_id = "PROGRAM_DEACTIVATED"
	GROUP BY PROGRAM.id
		UNION
	SELECT PROGRAM.id, "PROGRAM_COMPLETE_APPROVAL_STAGE", 1024, "SYSTEM_ADMINISTRATOR", MIN(PROGRAM_INSTANCE.start_date), "PROGRAM_APPROVED"
	FROM PROGRAM INNER JOIN PROGRAM_INSTANCE
		ON PROGRAM.id = PROGRAM_INSTANCE.program_id
	LEFT JOIN OPPORTUNITY_REQUEST
		ON PROGRAM.id = OPPORTUNITY_REQUEST.source_program_id
	WHERE OPPORTUNITY_REQUEST.id IS NULL
	GROUP BY PROGRAM.id
		UNION
	SELECT PROGRAM.id, "PROGRAM_ESCALATE", 1024, "SYSTEM_ADMINISTRATOR", MAX(PROGRAM_INSTANCE.disabled_date), "PROGRAM_DISABLED_PENDING_REACTIVATION"
	FROM PROGRAM INNER JOIN PROGRAM_INSTANCE
		ON PROGRAM.id = PROGRAM_INSTANCE.program_id
	INNER JOIN OPPORTUNITY_REQUEST
		ON PROGRAM.id = OPPORTUNITY_REQUEST.source_program_id
	WHERE PROGRAM.state_id LIKE "PROGRAM_DISABLED_%"
	GROUP BY PROGRAM.id
		UNION
	SELECT PROGRAM.id, "PROGRAM_ESCALATE", 1024, "SYSTEM_ADMINISTRATOR", MAX(PROGRAM_INSTANCE.disabled_date), "PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION"
	FROM PROGRAM INNER JOIN PROGRAM_INSTANCE
		ON PROGRAM.id = PROGRAM_INSTANCE.program_id
	LEFT JOIN OPPORTUNITY_REQUEST
		ON PROGRAM.id = OPPORTUNITY_REQUEST.source_program_id
	WHERE OPPORTUNITY_REQUEST.id IS NULL
		AND PROGRAM.state_id LIKE "PROGRAM_DISABLED_%"
	GROUP BY PROGRAM.id
		UNION
	SELECT PROGRAM.id, "PROGRAM_ESCALATE", 1024, "SYSTEM_ADMINISTRATOR", DATE(MAX(PROGRAM_INSTANCE.disabled_date) + INTERVAL 2419200 SECOND), "PROGRAM_DISABLED_COMPLETED"
	FROM PROGRAM INNER JOIN PROGRAM_INSTANCE
		ON PROGRAM.id = PROGRAM_INSTANCE.program_id
	WHERE PROGRAM.state_id LIKE "PROGRAM_DISABLED_%"
	GROUP BY PROGRAM.id
	HAVING DATE(MAX(PROGRAM_INSTANCE.disabled_date)) + INTERVAL 2419200 SECOND < CURRENT_DATE()
;

DROP TABLE OPPORTUNITY_REQUEST_COMMENT
;

DROP TABLE OPPORTUNITY_REQUEST
;

/* Create the project workflow comments */

INSERT INTO COMMENT (project_id, action_id, user_id, role_id, created_timestamp, transition_state_id)
	SELECT PROJECT.id, "PROGRAM_CREATE_PROJECT", USER_ROLE.user_id, USER_ROLE.role_id, "2014-01-02 09:00:00", "PROJECT_APPROVED"
	FROM PROJECT INNER JOIN USER_ROLE
		ON PROJECT.id = USER_ROLE.project_id
	WHERE USER_ROLE.role_id = "PROJECT_PRIMARY_SUPERVISOR"
		UNION
	SELECT PROJECT.id, "PROJECT_CONFIGURE", USER_ROLE.user_id, USER_ROLE.role_id, NOW(), "PROJECT_DEACTIVATED"
	FROM PROJECT INNER JOIN USER_ROLE
		ON PROJECT.id = USER_ROLE.project_id
	WHERE USER_ROLE.role_id = "PROJECT_PRIMARY_SUPERVISOR"
		AND PROJECT.state_id = "PROJECT_DEACTIVATED"
		UNION
	SELECT PROJECT.id, "PROJECT_CONFIGURE", USER_ROLE.user_id, USER_ROLE.role_id, CURRENT_DATE() - INTERVAL 2419200 SECOND - INTERVAL 1 DAY, "PROJECT_DISABLED"
	FROM PROJECT INNER JOIN USER_ROLE
		ON PROJECT.id = USER_ROLE.project_id
	WHERE USER_ROLE.role_id = "PROJECT_PRIMARY_SUPERVISOR"
		AND PROJECT.state_id = "PROJECT_DISABLED_COMPLETED"
		UNION
	SELECT PROJECT.id, "PROJECT_ESCALATE", USER_ROLE.user_id, USER_ROLE.role_id, CURRENT_DATE() - INTERVAL 1 DAY, "PROJECT_DISABLED_COMPLETED"
	FROM PROJECT INNER JOIN USER_ROLE
		ON PROJECT.id = USER_ROLE.project_id
	WHERE USER_ROLE.role_id = "PROJECT_PRIMARY_SUPERVISOR"
		AND PROJECT.state_id = "PROJECT_DISABLED_COMPLETED"
		UNION
	SELECT PROJECT.id, "PROJECT_ESCALATE", USER_ROLE.user_id, USER_ROLE.role_id, PROJECT.due_date - INTERVAL 2419200 SECOND, "PROJECT_DISABLED_PENDING_REACTIVATION"
	FROM PROJECT INNER JOIN USER_ROLE
		ON PROJECT.id = USER_ROLE.project_id
	WHERE USER_ROLE.role_id = "PROJECT_PRIMARY_SUPERVISOR"
		AND PROJECT.state_id = "PROJECT_DISABLED_PENDING_REACTIVATION"
;

