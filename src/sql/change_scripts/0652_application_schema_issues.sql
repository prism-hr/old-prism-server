CREATE TABLE STATE_ACTION_ASSIGNMENT (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	state_action_id INT(10) UNSIGNED,
	role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (state_action_id, role_id),
	INDEX (role_id),
	FOREIGN KEY (state_action_id) REFERENCES STATE_ACTION (id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id)
) ENGINE = INNODB
;

/* Application creator actions */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, id, "APPLICATION_CREATOR"
	FROM STATE_ACTION
	WHERE state_id = "APPLICATION_UNSUBMITTED"
		UNION
	SELECT NULL, id, "APPLICATION_CREATOR"
	FROM STATE_ACTION
	WHERE state_id = "APPLICATION_UNSUBMITTED_PENDING_COMPLETION"
		UNION
	SELECT NULL, id, "APPLICATION_CREATOR"
	FROM STATE_ACTION
	WHERE action_id IN ("APPLICATION_EDIT_AS_CREATOR",
		"APPLICATION_VIEW_AS_CREATOR", "APPLICATION_WITHDRAW")
		AND state_id NOT LIKE "%UNSUBMITTED%"
;

/* Application administrator actions */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	INNER JOIN ROLE
	WHERE STATE_ACTION.action_id IN ("APPLICATION_COMMENT", 
		"APPLICATION_EMAIL_CREATOR")
		AND ROLE.id IN ("SYSTEM", "INSTITUTION_ADMINISTRATOR", 
			"PROGRAM_ADMINISTRATOR", "PROJECT_ADMINISTRATOR", 
			"PROJECT_PRIMARY_SUPERVISOR")
		AND STATE.parent_state_id != "APPLICATION_UNSUBMITTED"
;

INSERT INTO STATE_ACTION_ASSIGNMENT 
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "APPLICATION_VIEW_AS_RECRUITER"
		AND ROLE.id IN ("SYSTEM_ADMINISTRATOR", "INSTITUTION_ADMINISTRATOR", 
			"PROGRAM_ADMINISTRATOR", "PROJECT_ADMINISTRATOR", 
			"PROJECT_PRIMARY_SUPERVISOR")
		AND STATE.parent_state_id IN ("APPLICATION_VALIDATION", 
			"APPLICATION_APPROVAL", "APPLICATION_APPROVED",
			"APPLICATION_REJECTED", "APPLICATION_WITHDRAWN")
		AND STATE.id != "APPLICATION_APPROVAL"
;

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "APPLICATION_EDIT_AS_ADMINISTRATOR"
		AND ROLE.id IN ("SYSTEM_ADMINISTRATOR", "INSTITUTION_ADMINISTRATOR", 
			"PROGRAM_ADMINISTRATOR", "PROJECT_ADMINISTRATOR", 
			"PROJECT_PRIMARY_SUPERVISOR")
		AND (STATE.parent_state_id IN ("APPLICATION_REVIEW", "APPLICATION_INTERVIEW")
			OR STATE.id = "APPLICATION_APPROVAL")
;

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id IN ("APPLICATION_ASSESS_ELIGIBILITY", 
		"APPLICATION_COMPLETE_VALIDATION_STAGE", "APPLICATION_ASSIGN_REVIEWERS", 
		"APPLICATION_COMPLETE_REVIEW_STAGE", "APPLICATION_ASSIGN_INTERVIEWERS",
		"APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS", 
		"APPLICATION_COMPLETE_INTERVIEW_STAGE", "APPLICATION_ASSIGN_SUPERVISORS",
		"APPLICATION_COMPLETE_APPROVAL_STAGE", 
		"APPLICATION_CONFIRM_OFFER_RECOMMENDATION", 
		"APPLICATION_CONFIRM_REJECTION", "APPLICATION_MOVE_TO_DIFFERENT_STAGE")
		AND ROLE.id IN ("SYSTEM_ADMINISTRATOR", "INSTITUTION_ADMINISTRATOR", 
			"PROGRAM_ADMINISTRATOR", "PROJECT_ADMINISTRATOR", 
			"PROJECT_PRIMARY_SUPERVISOR")
;

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "APPLICATION_ADMINISTRATOR"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_ASSIGN_REVIEWERS", 
		"APPLICATION_COMPLETE_REVIEW_STAGE", "APPLICATION_ASSIGN_INTERVIEWERS",
		"APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS", 
		"APPLICATION_COMPLETE_INTERVIEW_STAGE", "APPLICATION_ASSIGN_SUPERVISORS",
		"APPLICATION_COMPLETE_APPROVAL_STAGE", 
		"APPLICATION_CONFIRM_OFFER_RECOMMENDATION", 
		"APPLICATION_CONFIRM_REJECTION", "APPLICATION_MOVE_TO_DIFFERENT_STAGE",
		"APPLICATION_COMMENT", "APPLICATION_EMAIL_CREATOR",
		"APPLICATION_VIEW_AS_RECRUITER")
		AND STATE.parent_state_id IN ("APPLICATION_REVIEW", "APPLICATION_INTERVIEW",
			"APPLICATION_APPROVAL")
;

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, id, "INSTITUTION_ADMINISTRATOR"
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_CORRECT"
;

/* Application approver actions */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "PROGRAM_APPROVER"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_COMMENT", 
		"APPLICATION_EMAIL_CREATOR", "APPLICATION_VIEW_AS_RECRUITER",
		"APPLICATION_COMPLETE_APPROVAL_STAGE", 
		"APPLICATION_CONFIRM_OFFER_RECOMMENDATION", 
		"APPLICATION_CONFIRM_REJECTION")
		AND STATE.parent_state_id != "APPLICATION_UNSUBMITTED"
;

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "PROGRAM_APPROVER"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id = "APPLICATION_MOVE_TO_DIFFERENT_STAGE"
		AND STATE.parent_state_id IN ("APPLICATION_APPROVED", "APPLICATION_REJECTED")
;

/* Application viewer actions */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "PROGRAM_VIEWER"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_COMMENT", 
		"APPLICATION_EMAIL_CREATOR", "APPLICATION_VIEW_AS_RECRUITER")
		AND STATE.parent_state_id != "APPLICATION_UNSUBMITTED"
;

/* Application admitter actions */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "INSTITUTION_ADMITTER"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_COMMENT", 
		"APPLICATION_EMAIL_CREATOR", "APPLICATION_VIEW_AS_RECRUITER")
		AND STATE.parent_state_id != "APPLICATION_UNSUBMITTED"
;

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "INSTITUTION_ADMITTER"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_CONFIRM_ELIGIBILITY", 
		"APPLICATION_CORRECT")
;

/* Application referee action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "APPLICATION_REFEREE"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_PROVIDE_REFERENCE", 
		"APPLICATION_EMAIL_CREATOR", "APPLICATION_VIEW_AS_REFEREE")
		AND (STATE.parent_state_id IN ("APPLICATION_REVIEW",
			"APPLICATION_INTERVIEW", "APPLICATION_APPROVAL")
			OR STATE.id IN ("APPLICATION_APPROVED", "APPLICATION_REJECTED"))
;

/* Application reviewer action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "APPLICATION_REVIEWER"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_PROVIDE_REVIEW",
		"APPLICATION_VIEW_AS_RECRUITER", "APPLICATION_EMAIL_CREATOR")
		AND STATE.parent_state_id = "APPLICATION_REVIEW"
		AND STATE.id != STATE.parent_state_id
;

/* Application interview participant action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, id, "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWEE"
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY"
;

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, id, "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWER"
	FROM STATE_ACTION
	WHERE action_id IN ("APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY",
		"APPLICATION_VIEW_AS_RECRUITER", "APPLICATION_EMAIL_CREATOR")
		AND state_id IN ("APPLICATION_INTERVIEW_PENDING_AVAILABILITY",
			"APPLICATION_INTERVIEW_PENDING_SCHEDULING")
;

/* Application interviewer action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "APPLICATION_INTERVIEWER"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_PROVIDE_INTERVIEW_FEEDBACK",
		"APPLICATION_EMAIL_CREATOR", "APPLICATION_VIEW_AS_RECRUITER")
		AND STATE.parent_state_id = "APPLICATION_INTERVIEW"
		AND STATE.id != STATE.parent_state_id
;

/* Application primary supervisor action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "APPLICATION_PRIMARY_SUPERVISOR"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_CONFIRM_PRIMARY_SUPERVISION",
		"APPLICATION_VIEW_AS_RECRUITER", "APPLICATION_EMAIL_CREATOR")
		AND STATE.parent_state_id = "APPLICATION_APPROVAL"
		AND STATE.id != STATE.parent_state_id
;

/* Application secondary supervisor action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "APPLICATION_SECONDARY_SUPERVISOR"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_CONFIRM_PRIMARY_SUPERVISION",
		"APPLICATION_VIEW_AS_RECRUITER", "APPLICATION_EMAIL_CREATOR")
		AND STATE.parent_state_id = "APPLICATION_APPROVAL"
		AND STATE.id != STATE.parent_state_id
;

/* Application viewer (internal) action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "APPLICATION_VIEWER_RECRUITER"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_COMMENT", 
		"APPLICATION_EMAIL_CREATOR", "APPLICATION_VIEW_AS_RECRUITER")
		AND STATE.parent_state_id NOT IN ("APPLICATION_UNSUBMITTED",
			"APPLICATION_VALIDATION")
;

/* Application viewer (external) action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, "APPLICATION_VIEWER_REFEREE"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_COMMENT", 
		"APPLICATION_EMAIL_CREATOR", "APPLICATION_VIEW_AS_REFEREE")
		AND STATE.parent_state_id NOT IN ("APPLICATION_UNSUBMITTED",
			"APPLICATION_VALIDATION")
;

/* System configuration action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, id, "SYSTEM_ADMINISTRATOR"
	FROM STATE_ACTION
	WHERE action_id = "SYSTEM_CONFIGURE"
;

/* Institution configuration action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "INSTITUTION_CONFIGURE"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "INSTITUTION_ADMITTER",
			"SYSTEM_ADMINISTRATOR")
;

/* Institution create program */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "INSTITUTION_CREATE_PROGRAM"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "SYSTEM_ADMINISTRATOR")
;

/* Institution view */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "INSTITUTION_VIEW"
;

/* Program approval action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id IN ("PROGRAM_COMPLETE_APPROVAL_STAGE", 
		"PROGRAM_EMAIL_CREATOR")
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "SYSTEM_ADMINISTRATOR")
;

/* Program configuration action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "PROGRAM_CONFIGURE"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "PROGRAM_ADMINISTRATOR",
			"SYSTEM_ADMINISTRATOR")
;

/* Program view action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "PROGRAM_VIEW"
		AND STATE_ACTION.state_id IN ("PROGRAM_APPROVAL", "PROGRAM_REJECTED",
			"PROGRAM_WITHDRAWN")
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "PROGRAM_ADMINISTRATOR",
			"SYSTEM_ADMINISTRATOR")
		UNION
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "PROGRAM_VIEW"
		AND STATE_ACTION.state_id = "PROGRAM_APPROVAL_PENDING_CORRECTION"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "SYSTEM_ADMINISTRATOR")
		UNION
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "PROGRAM_VIEW"
		AND STATE_ACTION.state_id NOT IN ("PROGRAM_APPROVAL", "PROGRAM_REJECTED",
			"PROGRAM_WITHDRAWN", "PROGRAM_APPROVAL_PENDING_CORRECTION")
;

/* Program edit action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "PROGRAM_EDIT"
		AND ROLE.id = "PROGRAM_ADMINISTRATOR"
;

/* Program withdraw action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "PROGRAM_WITHDRAW"
		AND ROLE.id = "PROGRAM_ADMINISTRATOR"
;

/* Project creation action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "PROGRAM_CREATE_PROJECT"
		AND ROLE.id IN ("SYSTEM_ADMINISTRATOR", "INSTITUTION_ADMINISTRATOR", 
			"PROGRAM_ADMINISTRATOR", "PROGRAM_APPROVER", "PROGRAM_VIEWER", 
			"APPLICATION_ADMINISTRATOR", "APPLICATION_INTERVIEWER", 
			"APPLICATION_PRIMARY_SUPERVISOR", "APPLICATION_REVIEWER", 
			"APPLICATION_SECONDARY_SUPERVISOR", "PROJECT_ADMINISTRATOR",
			"PROJECT_PRIMARY_SUPERVISOR", "PROJECT_SECONDARY_SUPERVISOR",
			"APPLICATION_VIEWER_RECRUITER")
;

/* Project configuration action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "PROJECT_CONFIGURE"
		AND ROLE.id IN ("SYSTEM_ADMINISTRATOR", "INSTITUTION_ADMINISTRATOR", 
			"PROGRAM_ADMINISTRATOR", "PROJECT_ADMINISTRATOR")
;

/* Project view action */

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "PROJECT_VIEW"
;

CREATE TABLE STATE_ACTION_INHERITANCE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	state_action_id INT(10) UNSIGNED NOT NULL,
	inherited_state_action_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (state_action_id, inherited_state_action_id),
	INDEX (inherited_state_action_id),
	FOREIGN KEY (state_action_id) REFERENCES STATE_ACTION (id),
	FOREIGN KEY (inherited_state_action_id) REFERENCES STATE_ACTION (id)
) ENGINE = INNODB
	SELECT NULL AS id, STATE_ACTION.id AS state_action_id,
		INHERITED_STATE_ACTION.id AS inherited_state_action_id
	FROM STATE_ACTION INNER JOIN STATE_ACTION AS INHERITED_STATE_ACTION
		ON STATE_ACTION.state_id = INHERITED_STATE_ACTION.state_id
		AND STATE_ACTION.action_id = "APPLICATION_EDIT_AS_ADMINISTRATOR"
		AND INHERITED_STATE_ACTION.action_id = "APPLICATION_PROVIDE_REFERENCE"
;
