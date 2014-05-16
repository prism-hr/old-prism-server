/* Confirm interview arrangements action */

UPDATE COMMENT INNER JOIN INTERVIEW_SCHEDULE_COMMENT
	ON COMMENT.id = INTERVIEW_SCHEDULE_COMMENT.id
SET COMMENT.role_id = "PROGRAM_ADMINISTRATOR",
	COMMENT.action_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS",
	COMMENT.application_interviewee_instructions = INTERVIEW_SCHEDULE_COMMENT.further_details,
	COMMENT.application_interviewer_instructions = INTERVIEW_SCHEDULE_COMMENT.further_interviewer_details,
	COMMENT.application_interview_location = INTERVIEW_SCHEDULE_COMMENT.location_url,
	COMMENT.transition_state_id = "APPLICATION_INTERVIEW_PENDING_INTERVIEW"
;

DROP TABLE INTERVIEW_SCHEDULE_COMMENT
;

/* Provide interview feedback action */

UPDATE COMMENT INNER JOIN INTERVIEW_COMMENT
	ON COMMENT.id = INTERVIEW_COMMENT.id
SET COMMENT.role_id = "APPLICATION_INTERVIEWER",
	COMMENT.action_id = "APPLICATION_PROVIDE_INTERVIEW_FEEDBACK",
	COMMENT.application_suitable_for_institution = INTERVIEW_COMMENT.suitable_candidate,
	COMMENT.application_suitable_for_opportunity = INTERVIEW_COMMENT.applicant_suitable_for_programme,
	COMMENT.application_desire_to_supervise = INTERVIEW_COMMENT.willing_to_supervise,
	COMMENT.application_rating = INTERVIEW_COMMENT.applicant_rating
;

DROP TABLE INTERVIEW_COMMENT
;

/* Evaluate interview feedback comment */

ALTER TABLE COMMENT
	CHANGE COLUMN application_desire_to_supervise application_desire_to_recruit INT(1) UNSIGNED,
	DROP COLUMN interview_id
;

UPDATE COMMENT INNER JOIN INTERVIEW_EVALUATION_COMMENT
	ON COMMENT.id = INTERVIEW_EVALUATION_COMMENT.id
INNER JOIN STATECHANGE_COMMENT
	ON INTERVIEW_EVALUATION_COMMENT.id = STATECHANGE_COMMENT.id
SET COMMENT.role_id = "PROGRAM_ADMINISTRATOR",
	COMMENT.action_id = "APPLICATION_COMPLETE_INTERVIEW_STAGE",
	COMMENT.transition_state_id = CONCAT("APPLICATION_", STATECHANGE_COMMENT.next_status),
	COMMENT.application_use_custom_recruiter_questions = STATECHANGE_COMMENT.use_custom_questions
;

INSERT INTO COMMENT_ASSIGNED_USER (comment_id, user_id, role_id)
	SELECT id, user_id, "APPLICATION_ADMINISTRATOR"
	FROM STATECHANGE_COMMENT
	WHERE comment_type = "INTERVIEW_EVALUATION"
		AND user_id IS NOT NULL
;

DROP TABLE INTERVIEW_EVALUATION_COMMENT
;

DROP TABLE INTERVIEWER
;

ALTER TABLE APPLICATION
	DROP FOREIGN KEY latest_interview_fk,
	DROP COLUMN latest_interview_id
;

DROP TABLE INTERVIEW
;

/* Drop forgotten comment tables */

DROP TABLE STATE_CHANGE_SUGGESTION_COMMENT
;

DROP TABLE REQUEST_RESTART_COMMENT
;

/* Assign supervisor comment */

ALTER TABLE COMMENT
	ADD COLUMN application_equivalent_experience TEXT AFTER application_interview_location,
	ADD COLUMN application_position_title VARCHAR(255) AFTER application_equivalent_experience,
	ADD COLUMN application_position_description VARCHAR(2000) AFTER application_position_title,
	ADD COLUMN application_position_provisional_start_date DATE AFTER application_position_description,
	ADD COLUMN application_appointment_conditions TEXT AFTER application_position_provisional_start_date
;

/* Reconfigure use custom question flags */
/* Find and expose move to different stage comments */

/* Fix null constraints on comment table */