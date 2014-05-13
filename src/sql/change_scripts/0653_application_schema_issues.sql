INSERT INTO ROLE (id)
VALUES ("APPLICATION_INTERVIEWEE")
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE ROLE
SET id = "APPLICATION_POTENTIAL_INTERVIEWEE"
WHERE id = "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWEE"
;

UPDATE ROLE
SET id = "APPLICATION_POTENTIAL_INTERVIEWER"
WHERE id = "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWER"
;

UPDATE ROLE_INHERITANCE
SET role_id = "APPLICATION_POTENTIAL_INTERVIEWEE"
WHERE role_id = "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWEE"
;

UPDATE ROLE_INHERITANCE
SET inherited_role_id = "APPLICATION_POTENTIAL_INTERVIEWEE"
WHERE inherited_role_id = "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWEE"
;

UPDATE ROLE_INHERITANCE
SET inherited_role_id = "APPLICATION_POTENTIAL_INTERVIEWER"
WHERE inherited_role_id = "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWER"
;

UPDATE STATE_ACTION_ASSIGNMENT
SET role_id = "APPLICATION_POTENTIAL_INTERVIEWEE"
WHERE role_id = "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWEE"
;

UPDATE STATE_ACTION_ASSIGNMENT
SET role_id = "APPLICATION_POTENTIAL_INTERVIEWER"
WHERE role_id = "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWER"
;

UPDATE STATE_ACTION_NOTIFICATION
SET role_id = "APPLICATION_POTENTIAL_INTERVIEWEE"
WHERE role_id = "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWEE"
;

UPDATE STATE_ACTION_NOTIFICATION
SET role_id = "APPLICATION_POTENTIAL_INTERVIEWER"
WHERE role_id = "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWER"
;

UPDATE USER_ROLE
SET role_id = "APPLICATION_POTENTIAL_INTERVIEWEE"
WHERE role_id = "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWEE"
;

UPDATE USER_ROLE
SET role_id = "APPLICATION_POTENTIAL_INTERVIEWER"
WHERE role_id = "APPLICATION_INTERVIEW_PARTICIPANT_INTERVIEWER"
;

SET FOREIGN_KEY_CHECKS = 1
;

INSERT INTO STATE_ACTION_ASSIGNMENT (state_action_id, role_id)
	SELECT STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "APPLICATION_RETRACT_INTERVIEW_AVAILABILITY"
	AND ROLE.id IN ("APPLICATION_INTERVIEWEE", "APPLICATION_INTERVIEWER")
;

UPDATE APPLICATION
SET due_date = DATE(created_timestamp) + INTERVAL 2419200 SECOND
WHERE state_id = "APPLICATION_UNSUBMITTED"
;

UPDATE APPLICATION
SET state_id = "APPLICATION_UNSUBMITTED_PENDING_COMPLETION"
WHERE state_id = "APPLICATION_UNSUBMITTED"
	AND due_date < CURRENT_DATE()
;

INSERT INTO USER_ROLE (application_id, user_id, role_id, requesting_user_id, assigned_timestamp)
	SELECT APPLICATION.id, APPLICATION.user_id, "APPLICATION_INTERVIEWEE", ADVERT.user_id, INTERVIEW.created_date
	FROM APPLICATION INNER JOIN ADVERT
		ON APPLICATION.program_id = ADVERT.id
	INNER JOIN INTERVIEW
		ON APPLICATION.latest_interview_id = INTERVIEW.id
	WHERE APPLICATION.state_id = "APPLICATION_INTERVIEW_PENDING_INTERVIEW"
		AND APPLICATION.project_id IS NULL
;

INSERT INTO USER_ROLE (application_id, user_id, role_id, requesting_user_id, assigned_timestamp)
	SELECT APPLICATION.id, APPLICATION.user_id, "APPLICATION_INTERVIEWEE", ADVERT.user_id, INTERVIEW.created_date
	FROM APPLICATION INNER JOIN ADVERT
		ON APPLICATION.project_id = ADVERT.id
	INNER JOIN INTERVIEW
		ON APPLICATION.latest_interview_id = INTERVIEW.id
	WHERE APPLICATION.state_id = "APPLICATION_INTERVIEW_PENDING_INTERVIEW"
;

INSERT IGNORE INTO USER_ROLE (application_id, user_id, role_id, requesting_user_id, assigned_timestamp)
	SELECT APPLICATION.id, SUPERVISOR.user_id, "APPLICATION_SECONDARY_SUPERVISOR", ADVERT.user_id, APPROVAL_ROUND.created_date
	FROM APPLICATION INNER JOIN ADVERT
		ON APPLICATION.program_id = ADVERT.id
	INNER JOIN APPROVAL_ROUND
		ON APPLICATION.latest_approval_round_id = APPROVAL_ROUND.id
	INNER JOIN SUPERVISOR
		ON APPROVAL_ROUND.id = SUPERVISOR.approval_round_id
	INNER JOIN USER_ROLE
		ON APPLICATION.id = USER_ROLE.application_id
		AND USER_ROLE.role_id = "APPLICATION_PRIMARY_SUPERVISOR"
	WHERE APPLICATION.project_id IS NULL
		AND SUPERVISOR.is_primary = 0
		AND APPLICATION.state_id IN ("APPLICATION_APPROVAL_PENDING_FEEDBACK", "APPLICATION_APPROVAL_PENDING_COMPLETION")
;

INSERT IGNORE INTO USER_ROLE (application_id, user_id, role_id, requesting_user_id, assigned_timestamp)
	SELECT APPLICATION.id, SUPERVISOR.user_id, "APPLICATION_SECONDARY_SUPERVISOR", ADVERT.user_id, APPROVAL_ROUND.created_date
	FROM APPLICATION INNER JOIN ADVERT
		ON APPLICATION.project_id = ADVERT.id
	INNER JOIN APPROVAL_ROUND
		ON APPLICATION.latest_approval_round_id = APPROVAL_ROUND.id
	INNER JOIN SUPERVISOR
		ON APPROVAL_ROUND.id = SUPERVISOR.approval_round_id
	INNER JOIN USER_ROLE
		ON APPLICATION.id = USER_ROLE.application_id
		AND USER_ROLE.role_id = "APPLICATION_PRIMARY_SUPERVISOR"
	WHERE SUPERVISOR.is_primary = 0
		AND APPLICATION.state_id IN ("APPLICATION_APPROVAL_PENDING_FEEDBACK", "APPLICATION_APPROVAL_PENDING_COMPLETION")
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE ACTION
SET id = REPLACE(id, "RETRACT", "UPDATE")
;

UPDATE STATE_ACTION
SET action_id = REPLACE(action_id, "RETRACT", "UPDATE")
;

SET FOREIGN_KEY_CHECKS = 1
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, id, "APPLICATION_ADMINISTRATOR", 
		"APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION"
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_UPDATE_INTERVIEW_AVAILABILITY"
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, STATE_ACTION.id, ROLE.id, 
		"APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "APPLICATION_UPDATE_INTERVIEW_AVAILABILITY"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "PROGRAM_ADMINISTRATOR", 
			"PROJECT_ADMINISTRATOR")
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, id, "APPLICATION_INTERVIEWEE", 
		"APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWEE"
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS"
		UNION
	SELECT NULL, id, "APPLICATION_INTERVIEWER", 
		"APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER"
	FROM STATE_ACTION
	WHERE action_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS"
;

UPDATE APPLICATION
SET state_id = "APPLICATION_WITHDRAWN_COMPLETED"
WHERE state_id = "APPLICATION_WITHDRAWN_UNSUBMITTED"
;

DELETE FROM STATE
WHERE id = "APPLICATION_WITHDRAWN_UNSUBMITTED"
;

UPDATE ADVERT
SET state_id = "PROGRAM_DISABLED"
WHERE state_id = "PROGRAM_DISABLED_PENDING_COMPLETION"
;

UPDATE STATE_ACTION
SET state_id = "PROGRAM_DISABLED"
WHERE state_id = "PROGRAM_DISABLED_PENDING_COMPLETION"
;

UPDATE STATE_DURATION
SET state_id = "PROGRAM_DISABLED"
WHERE state_id = "PROGRAM_DISABLED_PENDING_COMPLETION"
;

UPDATE ADVERT
SET state_id = "PROJECT_DISABLED"
WHERE state_id = "PROJECT_DISABLED_PENDING_COMPLETION"
;

UPDATE STATE_ACTION
SET state_id = "PROJECT_DISABLED"
WHERE state_id = "PROJECT_DISABLED_PENDING_COMPLETION"
;

UPDATE STATE_DURATION
SET state_id = "PROJECT_DISABLED"
WHERE state_id = "PROJECT_DISABLED_PENDING_COMPLETION"
;

DELETE FROM STATE
WHERE id IN ("PROGRAM_DISABLED_PENDING_COMPLETION", "PROJECT_DISABLED_PENDING_COMPLETION")
;

UPDATE APPLICATION
SET state_id = "APPLICATION_WITHDRAWN"
WHERE state_id = "APPLICATION_WITHDRAWN_PENDING_EXPORT"
;

UPDATE STATE_ACTION
SET state_id = "APPLICATION_WITHDRAWN"
WHERE state_id = "APPLICATION_WITHDRAWN_PENDING_EXPORT"
;

DELETE FROM STATE
WHERE id = "APPLICATION_WITHDRAWN_PENDING_EXPORT"
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE STATE_ACTION
SET action_id = "APPLICATION_CONFIRM_SUPERVISION"
WHERE action_id = "APPLICATION_CONFIRM_PRIMARY_SUPERVISION"
;

UPDATE ACTION
SET id = "APPLICATION_CONFIRM_SUPERVISION"
WHERE id = "APPLICATION_CONFIRM_PRIMARY_SUPERVISION"
;
	
SET FOREIGN_KEY_CHECKS = 1
;
