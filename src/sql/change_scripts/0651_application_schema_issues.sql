INSERT INTO NOTIFICATION_TEMPLATE (id, notification_type_id)
VALUES ("APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION", "INDIVIDUAL")
;

INSERT INTO NOTIFICATION_TEMPLATE_VERSION (notification_template_id, subject, content)
	SELECT "APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION", REPLACE(subject, "Confirmation", "Update"), content
	FROM NOTIFICATION_TEMPLATE_VERSION INNER JOIN NOTIFICATION_TEMPLATE
		ON NOTIFICATION_TEMPLATE.notification_template_version_id = NOTIFICATION_TEMPLATE_VERSION.id
	WHERE NOTIFICATION_TEMPLATE.id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET notification_template_version_id = LAST_INSERT_ID()
WHERE id = "APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION"
;

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
	WHERE action_id = "APPLICATION_COMPLETE"
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
	SELECT NULL, STATE_ACTION.id, ROLE.id, "APPLICATION_UPDATE_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id IN ("APPLICATION_EDIT_AS_CREATOR",
		"APPLICATION_WITHDRAW", "APPLICATION_PROVIDE_REFERENCE",
		"APPLICATION_ASSESS_ELIGIBILITY", "APPLICATION_CONFIRM_ELIGIBILITY",
		"APPLICATION_ASSIGN_REVIEWERS", "APPLICATION_PROVIDE_REVIEW", 
		"APPLICATION_ASSIGN_INTERVIEWERS", "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS", 
		"APPLICATION_PROVIDE_INTERVIEW_FEEDBACK", "APPLICATION_ASSIGN_SUPERVISORS", 
		"APPLICATION_CONFIRM_PRIMARY_SUPERVISION", 
		"APPLICATION_CONFIRM_OFFER_RECOMMENDATION", "APPLICATION_CONFIRM_REJECTION",
		"APPLICATION_COMPLETE_VALIDATION_STAGE", "APPLICATION_COMPLETE_REVIEW_STAGE",
		"APPLICATION_COMPLETE_INTERVIEW_STAGE", "APPLICATION_COMPLETE_APPROVAL_STAGE",
		"APPLICATION_MOVE_TO_DIFFERENT_STAGE")
		AND STATE_ACTION.state_id NOT LIKE "%UNSUBMITTED%"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "PROGRAM_ADMINISTRATOR", 
			"PROJECT_ADMINISTRATOR")
		UNION
	SELECT NULL, STATE_ACTION.id, ROLE.id, 
		"APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "PROGRAM_ADMINISTRATOR", 
			"PROJECT_ADMINISTRATOR")
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, STATE_ACTION.id, "APPLICATION_ADMINISTRATOR", "APPLICATION_UPDATE_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id IN ("APPLICATION_EDIT_AS_CREATOR",
		"APPLICATION_WITHDRAW", "APPLICATION_PROVIDE_REFERENCE",
		"APPLICATION_ASSIGN_REVIEWERS", "APPLICATION_PROVIDE_REVIEW", 
		"APPLICATION_ASSIGN_INTERVIEWERS", "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS", 
		"APPLICATION_PROVIDE_INTERVIEW_FEEDBACK", "APPLICATION_ASSIGN_SUPERVISORS", 
		"APPLICATION_CONFIRM_PRIMARY_SUPERVISION", "APPLICATION_COMPLETE_REVIEW_STAGE",
		"APPLICATION_COMPLETE_INTERVIEW_STAGE", "APPLICATION_COMPLETE_APPROVAL_STAGE",
		"APPLICATION_MOVE_TO_DIFFERENT_STAGE")
		AND STATE.parent_state_id IN ("APPLICATION_REVIEW", "APPLICATION_INTERVIEW",
			"APPLICATION_APPROVAL")
		UNION
	SELECT NULL, id, "APPLICATION_ADMINISTRATOR", 
		"APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION"
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY"
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, id, "PROGRAM_APPROVER", "APPLICATION_UPDATE_NOTIFICATION"
	FROM STATE_ACTION
	WHERE state_id IN ("APPLICATION_APPROVAL_PENDING_FEEDBACK",
		"APPLICATION_APPROVAL_PENDING_COMPLETION", "APPLICATION_APPROVED", 
		"APPLICATION_REJECTED")
		AND action_id IN ("APPLICATION_WITHDRAW", "APPLICATION_PROVIDE_REFERENCE", 
			"APPLICATION_CONFIRM_OFFER_RECOMMENDATION", "APPLICATION_CONFIRM_REJECTION",
			"APPLICATION_CONFIRM_PRIMARY_SUPERVISION", "APPLICATION_COMPLETE_APPROVAL_STAGE",
			"APPLICATION_MOVE_TO_DIFFERENT_STAGE", "APPLICATION_COMMENT")
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, STATE_ACTION.id, ROLE.id, "APPLICATION_UPDATE_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "APPLICATION_CORRECT"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "INSTITUTION_ADMITTER")
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, id, "INSTITUTION_ADMITTER", "APPLICATION_UPDATE_NOTIFICATION"
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_CONFIRM_ELIGIBILITY"
;


INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, id, "INSTITUTION_ADMINISTRATOR", "PROGRAM_UPDATE_NOTIFICATION"
	FROM STATE_ACTION
	WHERE action_id = "PROGRAM_COMPLETE_APPROVAL_STAGE"
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, id, "PROGRAM_ADMINISTRATOR", "PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION"
	FROM STATE_ACTION
	WHERE action_id = "PROGRAM_COMPLETE_APPROVAL_STAGE"
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, STATE_ACTION.id, ROLE.id, "PROGRAM_UPDATE_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "PROGRAM_CONFIGURE"
		AND STATE_ACTION.state_id = "PROGRAM_DISABLED_PENDING_REACTIVATION"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "PROGRAM_ADMINISTRATOR")
;

INSERT INTO NOTIFICATION_TEMPLATE (id, notification_type_id)
VALUES ("PROJECT_UPDATE_NOTIFICATION", "SYNDICATED")
;

INSERT INTO NOTIFICATION_TEMPLATE_VERSION (notification_template_id, subject, content)
	SELECT "PROJECT_UPDATE_NOTIFICATION", subject, content
	FROM NOTIFICATION_TEMPLATE_VERSION INNER JOIN NOTIFICATION_TEMPLATE
		ON NOTIFICATION_TEMPLATE.notification_template_version_id = NOTIFICATION_TEMPLATE_VERSION.id
	WHERE NOTIFICATION_TEMPLATE.id = "PROGRAM_UPDATE_NOTIFICATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET notification_template_version_id = LAST_INSERT_ID()
WHERE id = "PROJECT_UPDATE_NOTIFICATION"
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, STATE_ACTION.id, ROLE.id, "PROJECT_UPDATE_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "PROJECT_CONFIGURE"
		AND STATE_ACTION.state_id = "PROJECT_DISABLED_PENDING_REACTIVATION"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "PROGRAM_ADMINISTRATOR",
			"PROJECT_ADMINISTRATOR")
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, STATE_ACTION.id, ROLE.id, "APPLICATION_UPDATE_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "APPLICATION_COMMENT"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "PROGRAM_ADMINISTRATOR", 
			"PROJECT_ADMINISTRATOR")
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, STATE_ACTION.id, "APPLICATION_ADMINISTRATOR", "APPLICATION_UPDATE_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN STATE
		ON STATE_ACTION.state_id = STATE.id
	WHERE STATE_ACTION.action_id = "APPLICATION_COMMENT"
		AND STATE.parent_state_id IN ("APPLICATION_REVIEW", "APPLICATION_INTERVIEW",
			"APPLICATION_APPROVAL")
;
