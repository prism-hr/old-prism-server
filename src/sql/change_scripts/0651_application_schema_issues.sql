CREATE TABLE STATE_ACTION_NOTIFICATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	state_action_id INT(10) UNSIGNED,
	role_id VARCHAR(50) NOT NULL,
	notification_template_id VARCHAR(100),
	PRIMARY KEY (id),
	UNIQUE INDEX (state_action_id, role_id),
	INDEX (role_id),
	INDEX (notification_template_id),
	FOREIGN KEY (state_action_id) REFERENCES STATE_ACTION (id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (notification_template_id) REFERENCES NOTIFICATION_TEMPLATE (id)
) ENGINE = INNODB
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, id, "APPLICATION_CREATOR", "APPLICATION_COMPLETE_NOTIFICATION"
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_COMPLETE_APPLICATION"
		UNION
	SELECT NULL, id, "APPLICATION_CREATOR", "APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION"
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_CONFIRM_OFFER_RECOMMENDATION"
		UNION
	SELECT NULL, id, "APPLICATION_CREATOR", "APPLICATION_CONFIRM_REJECTION_NOTIFICATION"
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_CONFIRM_REJECTION"
		UNION
	SELECT NULL, id, "APPLICATION_CREATOR", "APPLICATION_UPDATE_NOTIFICATION"
	FROM STATE_ACTION
	WHERE action_id IN ("APPLICATION_ASSIGN_REVIEWERS", "APPLICATION_ASSIGN_INTERVIEWERS",
		"APPLICATION_ASSIGN_SUPERVISORS", "APPLICATION_PROVIDE_REFERENCE")
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, id, "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWEE", 
		"APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWEE"
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS"
		UNION
	SELECT NULL, id, "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWER", 
		"APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER"
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS"
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, STATE_ACTION.id, ROLE.id, "APPLICATION_UPDATE_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id IN ("APPLICATION_EDIT_AS_CREATOR",
		"APPLICATION_WITHDRAW", "APPLICATION_PROVIDE_REFERENCE",
		"APPLICATION_ASSESS_ELIGIBILITY", "APPLICATION_CONFIRM_ELIGIBILITY",
		"APPLICATION_ASSIGN_REVIEWERS", "APPLICATION_PROVIDE_REVIEW", 
		"APPLICATION_ASSIGN_INTERVIEWERS", "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS", 
		"APPLICATION_PROVIDE_INTERVIEW_FEEDBACK", "APPLICATION_ASSIGN_SUPERVISORS", 
		"APPLICATION_CONFIRM_PRIMARY_SUPERVISION", 
		"APPLICATION_CONFIRM_OFFER_RECOMMENDATION", "APPLICATION_CONFIRM_REJECTION")
		AND STATE_ACTION.state_id NOT LIKE "%UNSUBMITTED%"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "PROGRAM_ADMINISTRATOR", 
			"PROJECT_ADMINISTRATOR", "APPLICATION_ADMINISTRATOR")
		UNION
	SELECT NULL, STATE_ACTION.id, ROLE.id, 
		"APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "PROGRAM_ADMINISTRATOR", 
			"PROJECT_ADMINISTRATOR", "APPLICATION_ADMINISTRATOR")
		UNION
	SELECT NULL, STATE_ACTION.id, ROLE.id, 
		"APPLICATION_RETRACT_INTERVIEW_AVAILABILITY_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "APPLICATION_RETRACT_INTERVIEW_AVAILABILITY"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "PROGRAM_ADMINISTRATOR", 
			"PROJECT_ADMINISTRATOR", "APPLICATION_ADMINISTRATOR")
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, id, "PROGRAM_APPROVER", "APPLICATION_UPDATE_NOTIFICATION"
	FROM STATE_ACTION
	WHERE state_id IN ("APPLICATION_APPROVAL_PENDING_FEEDBACK",
		"APPLICATION_APPROVAL_PENDING_COMPLETION", "APPLICATION_APPROVED", 
		"APPLICATION_REJECTED")
		AND action_id IN ("APPLICATION_WITHDRAW", "APPLICATION_PROVIDE_REFERENCE", 
			"APPLICATION_CONFIRM_OFFER_RECOMMENDATION", "APPLICATION_CONFIRM_REJECTION")
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, STATE_ACTION.id, ROLE.id, "APPLICATION_UPDATE_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "APPLICATION_CORRECT_APPLICATION"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "INSTITUTION_ADMITTER")
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, id, "INSTITUTION_ADMINISTRATOR", "PROGRAM_UPDATE_NOTIFICATION"
	FROM STATE_ACTION
	WHERE action_id = "PROGRAM_COMPLETE_APPROVAL_STAGE"
;
