/* Assign supervisor continued */

DELETE OFFER_RECOMMENDED_COMMENT.*
FROM OFFER_RECOMMENDED_COMMENT INNER JOIN SUPERVISOR
	ON OFFER_RECOMMENDED_COMMENT.supervisor_id = SUPERVISOR.id
	OR OFFER_RECOMMENDED_COMMENT.secondary_supervisor_id = SUPERVISOR.id
INNER JOIN APPROVAL_ROUND
	ON SUPERVISOR.approval_round_id = APPROVAL_ROUND.id
LEFT JOIN APPLICATION
	ON APPROVAL_ROUND.id = APPLICATION.latest_approval_round_id
	OR APPROVAL_ROUND.application_form_id = APPLICATION.id
WHERE APPLICATION.id IS NULL
;

DELETE APPROVAL_COMMENT.*
FROM APPROVAL_COMMENT INNER JOIN SUPERVISOR
	ON APPROVAL_COMMENT.supervisor_id = SUPERVISOR.id
	OR APPROVAL_COMMENT.secondary_supervisor_id = SUPERVISOR.id
INNER JOIN APPROVAL_ROUND
	ON SUPERVISOR.approval_round_id = APPROVAL_ROUND.id
LEFT JOIN APPLICATION
	ON APPROVAL_ROUND.id = APPLICATION.latest_approval_round_id
	OR APPROVAL_ROUND.application_form_id = APPLICATION.id
WHERE APPLICATION.id IS NULL
;

DELETE SUPERVISION_CONFIRMATION_COMMENT.*
FROM SUPERVISION_CONFIRMATION_COMMENT INNER JOIN SUPERVISOR
	ON SUPERVISION_CONFIRMATION_COMMENT.supervisor_id = SUPERVISOR.id
	OR SUPERVISION_CONFIRMATION_COMMENT.secondary_supervisor_id = SUPERVISOR.id
INNER JOIN APPROVAL_ROUND
	ON SUPERVISOR.approval_round_id = APPROVAL_ROUND.id
LEFT JOIN APPLICATION
	ON APPROVAL_ROUND.id = APPLICATION.latest_approval_round_id
	OR APPROVAL_ROUND.application_form_id = APPLICATION.id
WHERE APPLICATION.id IS NULL
;

DELETE SUPERVISOR.*
FROM SUPERVISOR INNER JOIN APPROVAL_ROUND
	ON SUPERVISOR.approval_round_id = APPROVAL_ROUND.id
LEFT JOIN APPLICATION
	ON APPROVAL_ROUND.id = APPLICATION.latest_approval_round_id
	OR APPROVAL_ROUND.application_form_id = APPLICATION.id
WHERE APPLICATION.id IS NULL
;

DELETE APPROVAL_ROUND.*
FROM APPROVAL_ROUND LEFT JOIN APPLICATION
	ON APPROVAL_ROUND.id = APPLICATION.latest_approval_round_id
	OR APPROVAL_ROUND.application_form_id = APPLICATION.id
WHERE APPLICATION.id IS NULL
;

ALTER TABLE COMMENT
	ADD COLUMN approval_round_id INT(10) UNSIGNED,
	MODIFY COLUMN application_interview_duration INT(10) UNSIGNED AFTER application_interview_timezone
;

INSERT INTO COMMENT (application_id, action_id, user_id, role_id, created_timestamp, transition_state_id, 
	application_equivalent_experience, application_position_title, application_position_description, 
	application_position_provisional_start_date, application_appointment_conditions, approval_round_id)
	SELECT EVENT.application_form_id, "APPLICATION_ASSIGN_SUPERVISORS", EVENT.user_id, "PROGRAM_ADMINISTRATOR", 
		EVENT.event_date, "APPLICATION_APPROVAL_PENDING_FEEDBACK", 
		APPROVAL_ROUND.missing_qualification_explanation, APPROVAL_ROUND.project_title, 
		APPROVAL_ROUND.project_abstract, APPROVAL_ROUND.recommended_start_date, 
		APPROVAL_ROUND.recommended_conditions, APPROVAL_ROUND.id
	FROM APPROVAL_STATE_CHANGE_EVENT INNER JOIN EVENT
		ON APPROVAL_STATE_CHANGE_EVENT.id = EVENT.id
	INNER JOIN APPROVAL_ROUND
		ON APPROVAL_STATE_CHANGE_EVENT.approval_round_id = APPROVAL_ROUND.id
;

UPDATE APPROVAL_COMMENT INNER JOIN SUPERVISOR
	ON APPROVAL_COMMENT.supervisor_id = SUPERVISOR.id
INNER JOIN COMMENT ON
	SUPERVISOR.approval_round_id = COMMENT.approval_round_id
SET COMMENT.application_position_title = APPROVAL_COMMENT.project_title,
	COMMENT.application_position_description = APPROVAL_COMMENT.project_abstract,
	COMMENT.application_position_provisional_start_date = APPROVAL_COMMENT.recommended_start_date,
	COMMENT.application_appointment_conditions = APPROVAL_COMMENT.recommended_conditions
;

DELETE APPROVAL_COMMENT.*, COMMENT.*
FROM COMMENT INNER JOIN APPROVAL_COMMENT
	ON COMMENT.id = APPROVAL_COMMENT.id
;

DROP TABLE APPROVAL_COMMENT
;

INSERT IGNORE INTO COMMENT_ASSIGNED_USER (comment_id, user_id, role_id)
	SELECT COMMENT.id, SUPERVISOR.user_id, 
	IF (SUPERVISOR.is_primary = 1,
		"APPLICATION_PRIMARY_SUPERVISOR",
		"APPLICATION_SECONDARY_SUPERVISOR")
	FROM COMMENT INNER JOIN SUPERVISOR
		ON COMMENT.approval_round_id = SUPERVISOR.approval_round_id
;

ALTER TABLE APPLICATION
	DROP FOREIGN KEY latest_approval_round_fk,
	DROP COLUMN latest_approval_round_id
;

ALTER TABLE COMMENT
	DROP COLUMN approval_round_id
;

DROP TABLE APPROVAL_STATE_CHANGE_EVENT
;

/* Supervisor confirmation comment */

ALTER TABLE COMMENT
	ADD COLUMN application_recruiter_accept_appointment INT(1) UNSIGNED AFTER application_appointment_conditions
;

UPDATE SUPERVISION_CONFIRMATION_COMMENT INNER JOIN COMMENT
	ON SUPERVISION_CONFIRMATION_COMMENT.id = COMMENT.id
INNER JOIN SUPERVISOR
	ON SUPERVISION_CONFIRMATION_COMMENT.supervisor_id = SUPERVISOR.id
SET COMMENT.role_id = "APPLICATION_PRIMARY_SUPERVISOR",
	COMMENT.action_id = "APPLICATION_CONFIRM_SUPERVISION",
	COMMENT.application_position_title = SUPERVISION_CONFIRMATION_COMMENT.project_title,
	COMMENT.application_position_description = SUPERVISION_CONFIRMATION_COMMENT.project_abstract,
	COMMENT.application_position_provisional_start_date = SUPERVISION_CONFIRMATION_COMMENT.recommended_start_date,
	COMMENT.application_appointment_conditions = SUPERVISION_CONFIRMATION_COMMENT.recommended_conditions,
	COMMENT.application_recruiter_accept_appointment = SUPERVISOR.confirmed_supervision,
	COMMENT.content = IF(SUPERVISOR.confirmed_supervision = 0, SUPERVISOR.declined_supervision_reason, COMMENT.content)
;

DROP TABLE SUPERVISION_CONFIRMATION_COMMENT
;

/* Approval evaluation comment */

UPDATE COMMENT INNER JOIN APPROVAL_EVALUATION_COMMENT
	ON COMMENT.id = APPROVAL_EVALUATION_COMMENT.id
INNER JOIN STATECHANGE_COMMENT
	ON APPROVAL_EVALUATION_COMMENT.id = STATECHANGE_COMMENT.id
SET COMMENT.role_id = "PROGRAM_APPROVER",
	COMMENT.action_id = "APPLICATION_COMPLETE_APPROVAL_STAGE",
	COMMENT.transition_state_id = CONCAT("APPLICATION_", STATECHANGE_COMMENT.next_status),
	COMMENT.application_use_custom_recruiter_questions = STATECHANGE_COMMENT.use_custom_questions
;

INSERT INTO COMMENT_ASSIGNED_USER (comment_id, user_id, role_id)
	SELECT id, user_id, "APPLICATION_ADMINISTRATOR"
	FROM STATECHANGE_COMMENT
	WHERE comment_type = "APPROVAL_EVALUATION"
		AND user_id IS NOT NULL
;

DROP TABLE APPROVAL_EVALUATION_COMMENT
;

/* Offer recommendation commemnt */

UPDATE COMMENT INNER JOIN OFFER_RECOMMENDED_COMMENT
	ON COMMENT.id = OFFER_RECOMMENDED_COMMENT.id
SET COMMENT.role_id = "PROGRAM_APPROVER",
	COMMENT.action_id = "APPLICATION_CONFIRM_OFFER_RECOMMENDATION",
	COMMENT.application_position_title = OFFER_RECOMMENDED_COMMENT.project_title,
	COMMENT.application_position_description = OFFER_RECOMMENDED_COMMENT.project_abstract,
	COMMENT.application_position_provisional_start_date = OFFER_RECOMMENDED_COMMENT.recommended_start_date,
	COMMENT.application_appointment_conditions = OFFER_RECOMMENDED_COMMENT.recommended_conditions,
	COMMENT.transition_state_id = "APPLICATION_APPROVED_PENDING_EXPORT"
;

INSERT IGNORE INTO COMMENT_ASSIGNED_USER (comment_id, user_id, role_id)
	SELECT OFFER_RECOMMENDED_COMMENT.id, SUPERVISOR.user_id, "APPLICATION_PRIMARY_SUPERVISOR"
	FROM OFFER_RECOMMENDED_COMMENT INNER JOIN SUPERVISOR
		ON OFFER_RECOMMENDED_COMMENT.supervisor_id = SUPERVISOR.id
;

INSERT IGNORE INTO COMMENT_ASSIGNED_USER (comment_id, user_id, role_id)
	SELECT OFFER_RECOMMENDED_COMMENT.id, SUPERVISOR.user_id, "APPLICATION_SECONDARY_SUPERVISOR"
	FROM OFFER_RECOMMENDED_COMMENT INNER JOIN SUPERVISOR
		ON OFFER_RECOMMENDED_COMMENT.secondary_supervisor_id = SUPERVISOR.id
;

DROP TABLE OFFER_RECOMMENDED_COMMENT
;

DROP TABLE SUPERVISOR
;

DROP TABLE APPROVAL_ROUND
;

DROP TABLE STATECHANGE_COMMENT
;

DROP TABLE CONFIRM_ELIGIBILITY_EVENT
;

/* Rejection comment */

INSERT INTO COMMENT (application_id, action_id, user_id, role_id, content, created_timestamp, transition_state_id)
	SELECT APPLICATION.id, "APPLICATION_CONFIRM_REJECTION", EVENT.user_id, "PROGRAM_APPROVER", REJECT_REASON.text,
		MAX(EVENT.event_date), "APPLICATION_REJECTED_PENDING_EXPORT"
	FROM APPLICATION INNER JOIN REJECTION
		ON APPLICATION.rejection_id = REJECTION.id
	INNER JOIN REJECT_REASON
		ON REJECTION.reject_reason_id = REJECT_REASON.id
	INNER JOIN EVENT
		ON APPLICATION.id = EVENT.application_form_id
	GROUP BY APPLICATION.id
;

ALTER TABLE APPLICATION
	DROP FOREIGN KEY app_rejection_fk,
	DROP COLUMN rejection_id,
	DROP COLUMN reject_notification_date
;

DROP TABLE REJECTION
;

DROP TABLE REJECT_REASON
;

DROP TABLE STATE_CHANGE_EVENT
;

DROP TABLE EVENT
;

/* Application export comment */

ALTER TABLE COMMENT
	ADD COLUMN application_export_request TEXT AFTER custom_question_response,
	ADD COLUMN application_export_response TEXT AFTER application_export_request,
	ADD COLUMN application_export_reference VARCHAR(50) AFTER application_export_response,
	ADD COLUMN application_export_error TEXT AFTER application_export_response
;

UPDATE COMMENT INNER JOIN APPLICATION_TRANSFER_COMMENT
	ON COMMENT.id = APPLICATION_TRANSFER_COMMENT.id
INNER JOIN APPLICATION_TRANSFER_ERROR
	ON APPLICATION_TRANSFER_COMMENT.application_form_transfer_error_id = APPLICATION_TRANSFER_ERROR.id
INNER JOIN APPLICATION
	ON COMMENT.application_id = APPLICATION.id
INNER JOIN STATE
	ON APPLICATION.state_id = STATE.id
SET COMMENT.action_id = "APPLICATION_EXPORT",
	COMMENT.user_id = 1024,
	COMMENT.role_id = "SYSTEM_ADMINISTRATOR",
	COMMENT.transition_state_id = 
		IF(APPLICATION_TRANSFER_COMMENT.succeeded = 1,
			CONCAT(STATE.parent_state_id, "_COMPLETED"),
			IF(APPLICATION_TRANSFER_ERROR.application_transfer_error_type_id = "WEBSERVICE_UNREACHABLE",
				CONCAT(STATE.parent_state_id, 
					IF(STATE.parent_state_id = "APPLICATION_WITHDRAWN",
						"",
						"_PENDING_EXPORT")),
				CONCAT(STATE.parent_state_id, "_PENDING_CORRECTION"))),
	COMMENT.application_export_request = APPLICATION_TRANSFER_ERROR.request_copy,
	COMMENT.application_export_response = APPLICATION_TRANSFER_ERROR.response_copy,
	COMMENT.application_export_reference = APPLICATION.ucl_booking_ref_number,
	COMMENT.application_export_error = APPLICATION_TRANSFER_ERROR.report
;

DROP TABLE APPLICATION_TRANSFER_COMMENT
;

DROP TABLE APPLICATION_TRANSFER_ERROR
;

DROP TABLE APPLICATION_TRANSFER_ERROR_TYPE
;

ALTER TABLE APPLICATION
	DROP FOREIGN KEY application_ibfk_1,
	DROP COLUMN application_transfer_id,
	DROP COLUMN ucl_booking_ref_number
;

DROP TABLE APPLICATION_TRANSFER
;

DROP TABLE APPLICATION_TRANSFER_STATE
;

UPDATE COMMENT
SET declined_response = NULL
WHERE declined_response = 0
;

DELETE
FROM COMMENT
WHERE content LIKE "%restart%"
;

DELETE 
FROM COMMENT
WHERE content LIKE "Referred to UCL Admissions for advice on eligibility and fees status.%"
;

DELETE
FROM COMMENT
WHERE content LIKE "Delegated application for processing%"
;

DELETE
FROM COMMENT
WHERE content LIKE "Reference request message was not delivered%"
;
