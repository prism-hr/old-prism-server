/* Fields to enabled curation of system reference institutions and domiciles */

ALTER TABLE INSTITUTION_DOMICILE
	ADD COLUMN enabled INT(1) UNSIGNED NOT NULL DEFAULT 1
;

ALTER TABLE INSTITUTION_DOMICILE
	MODIFY COLUMN enabled INT(1) UNSIGNED NOT NULL
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN system_id INT(10) UNSIGNED AFTER id,
	ADD COLUMN homepage VARCHAR(2000) AFTER name
;

UPDATE INSTITUTION
SET homepage = "http://www.ucl.ac.uk/"
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN homepage VARCHAR(2000) NOT NULL
;

ALTER TABLE INSTITUTION
	ADD COLUMN enabled INT(1) UNSIGNED NOT NULL DEFAULT 1
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN enabled INT(1) UNSIGNED NOT NULL
;

/* Assign interviewer comment */

ALTER TABLE COMMENT
	ADD COLUMN application_interview_datetime DATETIME AFTER application_desire_to_supervise,
	ADD COLUMN application_interview_timezone VARCHAR(50) AFTER application_interview_datetime,
	ADD COLUMN application_interview_duration INT(10) AFTER application_interview_timezone,
	ADD COLUMN application_interviewee_instructions TEXT AFTER application_interview_timezone,
	ADD COLUMN application_interviewer_instructions TEXT AFTER application_interviewee_instructions,
	ADD COLUMN application_interview_location VARCHAR(2000) AFTER application_interviewer_instructions,
	ADD COLUMN interview_id INT(10) UNSIGNED
;

INSERT INTO COMMENT (application_id, action_id, user_id, role_id, created_timestamp, transition_state_id, 
	application_use_custom_recruiter_questions, application_interview_datetime, application_interview_timezone, 
	application_interview_duration, application_interviewee_instructions, application_interviewer_instructions, 
	application_interview_location, interview_id)
	SELECT EVENT.application_form_id, "APPLICATION_ASSIGN_INTERVIEWERS", EVENT.user_id, "PROGRAM_ADMINISTRATOR", 
		EVENT.event_date, "APPLICATION_INTERVIEW_PENDING_SCHEDULING", INTERVIEW.use_custom_questions, 
		NULL, NULL, NULL, NULL, NULL, NULL, INTERVIEW.id
	FROM INTERVIEW_STATE_CHANGE_EVENT INNER JOIN EVENT
		ON INTERVIEW_STATE_CHANGE_EVENT.id = EVENT.id
	INNER JOIN INTERVIEW
		ON INTERVIEW_STATE_CHANGE_EVENT.interview_id = INTERVIEW.id
	INNER JOIN INTERVIEW_PARTICIPANT
		ON INTERVIEW.id = INTERVIEW_PARTICIPANT.interview_id
	GROUP BY INTERVIEW.id
		UNION
	SELECT EVENT.application_form_id, "APPLICATION_ASSIGN_INTERVIEWERS", EVENT.user_id, "PROGRAM_ADMINISTRATOR", 
		EVENT.event_date, "APPLICATION_INTERVIEW_PENDING_INTERVIEW", INTERVIEW.use_custom_questions, 
		CONCAT(INTERVIEW.due_date, " ", INTERVIEW.interview_time, ":00"), INTERVIEW.time_zone,
		INTERVIEW.duration, INTERVIEW.further_details, INTERVIEW.further_interviewer_details, 
		INTERVIEW.location_url, INTERVIEW.id
	FROM INTERVIEW_STATE_CHANGE_EVENT INNER JOIN EVENT
		ON INTERVIEW_STATE_CHANGE_EVENT.id = EVENT.id
	INNER JOIN INTERVIEW
		ON INTERVIEW_STATE_CHANGE_EVENT.interview_id = INTERVIEW.id
	WHERE EVENT.event_date < CONCAT(INTERVIEW.due_date, " ", INTERVIEW.interview_time, ";00")
		UNION
	SELECT EVENT.application_form_id, "APPLICATION_ASSIGN_INTERVIEWERS", EVENT.user_id, "PROGRAM_ADMINISTRATOR", 
		EVENT.event_date, "APPLICATION_INTERVIEW_PENDING_FEEDBACK", INTERVIEW.use_custom_questions, 
		CONCAT(INTERVIEW.due_date, " ", INTERVIEW.interview_time, ":00"), INTERVIEW.time_zone,
		INTERVIEW.duration, INTERVIEW.further_details, INTERVIEW.further_interviewer_details, 
		INTERVIEW.location_url,	INTERVIEW.id
	FROM INTERVIEW_STATE_CHANGE_EVENT INNER JOIN EVENT
		ON INTERVIEW_STATE_CHANGE_EVENT.id = EVENT.id
	INNER JOIN INTERVIEW
		ON INTERVIEW_STATE_CHANGE_EVENT.interview_id = INTERVIEW.id
	WHERE EVENT.event_date >= CONCAT(INTERVIEW.due_date, " ", INTERVIEW.interview_time, ";00")
;

DROP TABLE INTERVIEW_STATE_CHANGE_EVENT
;

INSERT IGNORE INTO COMMENT_ASSIGNED_USER (comment_id, user_id, role_id)
	SELECT COMMENT.id, APPLICATION.user_id, "APPLICATION_POTENTIAL_INTERVIEWEE"
	FROM COMMENT INNER JOIN APPLICATION
		ON COMMENT.application_id = APPLICATION.id
	WHERE COMMENT.transition_state_id = "APPLICATION_INTERVIEW_PENDING_AVAILABILITY"
		UNION
	SELECT COMMENT.id, APPLICATION.user_id, "APPLICATION_INTERVIEWEE"
	FROM COMMENT INNER JOIN APPLICATION
		ON COMMENT.application_id = APPLICATION.id
	WHERE COMMENT.transition_state_id = "APPLICATION_INTERVIEW_PENDING_INTERVIEW"
		UNION
	SELECT COMMENT.id, INTERVIEWER.user_id, "APPLICATION_POTENTIAL_INTERVIEWER"
	FROM COMMENT INNER JOIN INTERVIEWER
		ON COMMENT.interview_id = INTERVIEWER.interview_id
	WHERE COMMENT.transition_state_id = "APPLICATION_INTERVIEW_PENDING_SCHEDULING"
		UNION
	SELECT COMMENT.id, INTERVIEWER.user_id, "APPLICATION_INTERVIEWER"
	FROM COMMENT INNER JOIN INTERVIEWER
		ON COMMENT.interview_id = INTERVIEWER.interview_id
	WHERE COMMENT.transition_state_id IN ("APPLICATION_INTERVIEW_PENDING_INTERVIEW",
		"APPLICATION_INTERVIEW_PENDING_FEEDBACK")
;

ALTER TABLE COMMENT_APPOINTMENT_TIMESLOT
	ADD COLUMN timeslot_id INT(10) UNSIGNED NOT NULL
;

INSERT INTO COMMENT_APPOINTMENT_TIMESLOT (comment_id, timeslot_datetime, timeslot_id)
	SELECT COMMENT.id, CONCAT(INTERVIEW_TIMESLOT.due_date, " ", INTERVIEW_TIMESLOT.start_time, ":00"), INTERVIEW_TIMESLOT.id
	FROM COMMENT INNER JOIN INTERVIEW_TIMESLOT
		ON COMMENT.interview_id = INTERVIEW_TIMESLOT.interview_id
;

INSERT INTO USER_NOTIFICATION_INDIVIDUAL (user_role_id, notification_template_id, last_notification_timestamp)
	SELECT USER_ROLE.id, "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST", MAX(DATE(INTERVIEW_PARTICIPANT.last_notified))
	FROM INTERVIEW INNER JOIN INTERVIEW_PARTICIPANT
		ON INTERVIEW.id = INTERVIEW_PARTICIPANT.interview_id
	INNER JOIN USER_ROLE
		ON INTERVIEW.application_form_id = USER_ROLE.application_id
		AND INTERVIEW_PARTICIPANT.user_id = USER_ROLE.user_id
	WHERE USER_ROLE.role_id LIKE "APPLICATION_POTENTIAL_INTERVIEW%"
		AND INTERVIEW_PARTICIPANT.last_notified IS NOT NULL
	GROUP BY USER_ROLE.id
;

/* Interview provide interview availability */

CREATE TABLE COMMENT_APPOINTMENT_PREFERENCE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL,
	comment_appointment_timeslot_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (comment_id, comment_appointment_timeslot_id),
	INDEX (comment_appointment_timeslot_id),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id),
	FOREIGN KEY (comment_appointment_timeslot_id) REFERENCES COMMENT_APPOINTMENT_TIMESLOT (id)
) ENGINE = INNODB
;

UPDATE APPLICATION INNER JOIN COMMENT
	ON APPLICATION.id = COMMENT.application_id
INNER JOIN INTERVIEW_VOTE_COMMENT
	ON COMMENT.id = INTERVIEW_VOTE_COMMENT.id
SET COMMENT.role_id = 
	IF (APPLICATION.user_id,
		"APPLICATION_POTENTIAL_INTERVIEWEE",
		"APPLICATION_POTENTIAL_INTERVIEWER"),
	COMMENT.action_id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY"
;

INSERT INTO COMMENT_APPOINTMENT_PREFERENCE (comment_id, comment_appointment_timeslot_id)
	SELECT INTERVIEW_VOTE_COMMENT.id, COMMENT_APPOINTMENT_TIMESLOT.id
	FROM INTERVIEW_VOTE_COMMENT INNER JOIN INTERVIEW_PARTICIPANT
		ON INTERVIEW_VOTE_COMMENT.interview_participant_id = INTERVIEW_PARTICIPANT.id
	INNER JOIN INTERVIEW_TIMESLOT_VOTE
		ON INTERVIEW_PARTICIPANT.id = INTERVIEW_TIMESLOT_VOTE.participant_id
	INNER JOIN COMMENT_APPOINTMENT_TIMESLOT
		ON INTERVIEW_TIMESLOT_VOTE.timeslot_id = COMMENT_APPOINTMENT_TIMESLOT.timeslot_id
;

ALTER TABLE COMMENT_APPOINTMENT_TIMESLOT
	DROP COLUMN timeslot_id
;

DROP TABLE INTERVIEW_TIMESLOT_VOTE
;

DROP TABLE INTERVIEW_VOTE_COMMENT
;

DROP TABLE INTERVIEW_PARTICIPANT
;

DROP TABLE INTERVIEW_TIMESLOT
;

INSERT INTO COMMENT_ASSIGNED_USER (comment_id, user_id, role_id)
	SELECT COMMENT.id, COMMENT.user_id,
		IF (COMMENT.user_id = APPLICATION.user_id,
			"APPLICATION_INTERVIEWEE",
			"APPLICATION_INTERVIEWER")
	FROM COMMENT INNER JOIN APPLICATION
		ON COMMENT.application_id = APPLICATION.id
	WHERE COMMENT.action_id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY"
;
