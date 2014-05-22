/* Application export comment */

ALTER TABLE COMMENT
	ADD COLUMN application_export_request TEXT AFTER custom_question_response,
	ADD COLUMN application_export_response TEXT AFTER application_export_request,
	ADD COLUMN application_export_reference VARCHAR(50) AFTER application_export_response,
	ADD COLUMN application_export_error TEXT AFTER application_export_response
;

UPDATE COMMENT INNER JOIN APPLICATION_TRANSFER_COMMENT
	ON COMMENT.id = APPLICATION_TRANSFER_COMMENT.id
INNER JOIN APPLICATION
	ON COMMENT.application_id = APPLICATION.id
INNER JOIN STATE
	ON APPLICATION.state_id = STATE.id
LEFT JOIN APPLICATION_TRANSFER_ERROR
	ON APPLICATION_TRANSFER_COMMENT.application_form_transfer_error_id = APPLICATION_TRANSFER_ERROR.id
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

/* Label generic comments */

UPDATE COMMENT
SET action_id = "APPLICATION_COMMENT",
	role_id = "APPLICATION_VIEWER_RECRUITER"
WHERE action_id IS NULL
	OR role_id IS NULL
;

/* Fix constraints on application */

ALTER TABLE COMMENT
	ADD COLUMN creator_ip_address VARCHAR(50) AFTER application_export_reference
;

UPDATE COMMENT INNER JOIN APPLICATION
	ON COMMENT.application_id = APPLICATION.id
	AND COMMENT.action_id = "APPLICATION_COMPLETE"
SET COMMENT.creator_ip_address = APPLICATION.submitted_ip_address
;

ALTER TABLE APPLICATION
	DROP COLUMN use_custom_reference_questions,
	DROP COLUMN submitted_ip_address,
	DROP FOREIGN KEY prog_app_fk,
	MODIFY COLUMN program_id INT(10) UNSIGNED NOT NULL AFTER user_id,
	ADD FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	MODIFY COLUMN project_id INT(10) UNSIGNED AFTER program_id,
	MODIFY COLUMN closing_date DATE AFTER project_id,
	MODIFY COLUMN application_number VARCHAR(50) AFTER id,
	MODIFY COLUMN application_personal_detail_id INT(10) UNSIGNED AFTER closing_date,
	MODIFY COLUMN application_program_detail_id INT(10) UNSIGNED AFTER application_personal_detail_id,
	MODIFY COLUMN application_address_id INT(10) UNSIGNED AFTER application_program_detail_id,
	MODIFY COLUMN application_document_id INT(10) UNSIGNED AFTER application_address_id,
	MODIFY COLUMN application_additional_information_id INT(10) UNSIGNED AFTER application_document_id,
	MODIFY COLUMN created_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER application_additional_information_id,
	MODIFY COLUMN submitted_timestamp DATETIME AFTER created_timestamp,
	MODIFY COLUMN state_id VARCHAR(50) NOT NULL AFTER submitted_timestamp,
	MODIFY COLUMN previous_state_id VARCHAR(50) AFTER state_id,
	MODIFY COLUMN due_date DATE AFTER previous_state_id
;

/* Put use custom question in the right place */

UPDATE COMMENT INNER JOIN (
	SELECT application_id AS application_id,
		MIN(created_timestamp) AS created_timestamp
	FROM COMMENT
	WHERE action_id IN ("APPLICATION_ASSIGN_REVIEWERS", "APPLICATION_ASSIGN_INTERVIEWERS")
	GROUP BY application_id) AS ASSIGNMENT_TIME
	ON COMMENT.application_id = ASSIGNMENT_TIME.application_id
	AND COMMENT.created_timestamp <= ASSIGNMENT_TIME.created_timestamp
INNER JOIN COMMENT AS ASSIGNMENT_COMMENT
	ON ASSIGNMENT_TIME.application_id = ASSIGNMENT_COMMENT.application_id
	AND ASSIGNMENT_TIME.created_timestamp = ASSIGNMENT_COMMENT.created_timestamp
SET ASSIGNMENT_COMMENT.application_use_custom_recruiter_questions = COMMENT.application_use_custom_recruiter_questions
;

UPDATE COMMENT
SET application_use_custom_recruiter_questions = NULL
WHERE action_id NOT LIKE "APPLICATION_ASSIGN%"
;

UPDATE COMMENT
SET application_use_custom_recruiter_questions = 0
WHERE action_id IN ("APPLICATION_ASSIGN_REVIEWERS", "APPLICATION_ASSIGN_INTERVIEWERS")
	AND application_use_custom_recruiter_questions IS NULL
;

/* Find and label move to different stage */

UPDATE COMMENT INNER JOIN (
	SELECT COMMENT.application_id AS application_id, 
		COMMENT.id AS comment_id,
		MAX(PREVIOUS_COMMENT.id) AS previous_comment_id
	FROM COMMENT INNER JOIN COMMENT AS PREVIOUS_COMMENT
		ON COMMENT.application_id = PREVIOUS_COMMENT.application_id
		AND COMMENT.id > PREVIOUS_COMMENT.id
	WHERE COMMENT.action_id IN ("APPLICATION_COMPLETE_VALIDATION_STAGE",
		"APPLICATION_COMPLETE_REVIEW_STAGE",
		"APPLICATION_COMPLETE_INTERVIEW_STAGE",
		"APPLICATION_COMPLETE_APPROVAL_STAGE")
	GROUP BY COMMENT.id) STATE_CHANGE_COMMENT
	ON COMMENT.id = STATE_CHANGE_COMMENT.comment_id
INNER JOIN COMMENT AS PREVIOUS_COMMENT
	ON STATE_CHANGE_COMMENT.previous_comment_id = PREVIOUS_COMMENT.id
SET COMMENT.action_id = "APPLICATION_MOVE_TO_DIFFERENT_STAGE"
WHERE COMMENT.action_id = PREVIOUS_COMMENT.action_id
;	
	
/* Drop stored procedures */

DROP PROCEDURE SP_DELETE_APPLICATION_ACTIONS
;

DROP PROCEDURE SP_DELETE_APPLICATION_ROLE
;

DROP PROCEDURE SP_DELETE_APPLICATION_UPDATE
;

DROP PROCEDURE SP_DELETE_EXPIRED_CLOSING_DATES
;

DROP PROCEDURE SP_DELETE_INACTIVE_ADVERTS
;

DROP PROCEDURE SP_DELETE_ORPHAN_DOCUMENTS
;

DROP PROCEDURE SP_DELETE_PROGRAM_ROLE
;

DROP PROCEDURE SP_DELETE_ROLE_ACTION
;

DROP PROCEDURE SP_DELETE_STATE_ACTIONS
;

DROP PROCEDURE SP_DELETE_USER_ACTION
;

DROP PROCEDURE SP_DELETE_USER_ROLE
;

DROP PROCEDURE SP_INSERT_APPLICATION_UPDATE
;

DROP PROCEDURE SP_INSERT_PROGRAM_ROLE
;

DROP PROCEDURE SP_INSERT_USER_ROLE
;

DROP PROCEDURE SP_SELECT_RECOMMENDED_ADVERTS
;

DROP PROCEDURE SP_SELECT_USER_ACTIONS
;

DROP PROCEDURE SP_UPDATE_APPLICATION_FORM_DUE_DATE
;

DROP PROCEDURE SP_UPDATE_APPLICATION_INTEREST
;

DROP PROCEDURE SP_UPDATE_URGENT_APPLICATIONS
;

/* Redundant columns on personal details */

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	DROP COLUMN language_qualification_available
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	DROP COLUMN passport_available
;

/* Denormalize the resource references */

ALTER TABLE APPLICATION
	ADD COLUMN system_id INT(10) UNSIGNED AFTER user_id,
	ADD COLUMN institution_id INT(10) UNSIGNED AFTER system_id,
	ADD INDEX (system_id),
	ADD INDEX (institution_id),
	ADD FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	ADD FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id)
;

UPDATE APPLICATION
SET system_id = 1,
	institution_id = 5243
;

ALTER TABLE APPLICATION
	MODIFY COLUMN system_id INT(10) UNSIGNED NOT NULL,
	MODIFY COLUMN institution_id INT(10) UNSIGNED NOT NULL
;

ALTER TABLE PROGRAM
	ADD COLUMN system_id INT(10) UNSIGNED AFTER id,
	ADD INDEX (system_id),
	ADD FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	MODIFY COLUMN institution_id INT(10) UNSIGNED AFTER system_id
;

UPDATE PROGRAM
SET system_id = 1
;

ALTER TABLE PROGRAM
	MODIFY COLUMN system_id INT(10) UNSIGNED NOT NULL,
	MODIFY COLUMN institution_id INT(10) UNSIGNED NOT NULL
;

ALTER TABLE PROJECT
	ADD COLUMN system_id INT(10) UNSIGNED AFTER id,
	ADD COLUMN institution_id INT(10) UNSIGNED AFTER system_id,
	ADD INDEX (system_id),
	ADD INDEX (institution_id),
	ADD FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	ADD FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id)
;

UPDATE PROJECT
SET system_id = 1,
	institution_id = 5243
;

ALTER TABLE PROJECT
	MODIFY COLUMN system_id INT(10) UNSIGNED NOT NULL,
	MODIFY COLUMN institution_id INT(10) UNSIGNED NOT NULL
;

UPDATE PROJECT
SET system_id = 1,
	institution_id = 5243
;

/* Fertile property in state */

ALTER TABLE STATE
	CHANGE COLUMN is_under_assessment is_assessment_state INT(1) UNSIGNED NOT NULL,
	ADD COLUMN is_fertile_state INT(1) UNSIGNED AFTER parent_state_id,
	ADD COLUMN is_duplicatable_state INT(1) UNSIGNED AFTER is_assessment_state
;

UPDATE STATE
SET is_fertile_state = 0,
	is_duplicatable_state = 0
;

UPDATE STATE
SET is_fertile_state = 1
WHERE id LIKE "SYSTEM%"
	OR id LIKE "INSTITUTION%"
	OR ((id LIKE "PROGRAM%"
		OR id LIKE "PROJECT%")
			AND id NOT LIKE "%COMPLETED")
;

UPDATE STATE
SET is_duplicatable_state = 1
WHERE parent_state_id LIKE "%_COMPLETED"
;

ALTER TABLE STATE
	MODIFY COLUMN is_fertile_state INT(1) UNSIGNED NOT NULL,
	MODIFY COLUMN is_duplicatable_state INT(1) UNSIGNED NOT NULL
;

/* Add users to system and institution and standardise human readable ID */

ALTER TABLE INSTITUTION
	ADD COLUMN user_id INT(10) UNSIGNED NOT NULL DEFAULT 1024 AFTER id,
	ADD INDEX (user_id),
	ADD FOREIGN KEY (user_id) REFERENCES USER (id)
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN user_id INT(10) UNSIGNED NOT NULL
;

ALTER TABLE SYSTEM
	ADD COLUMN user_id INT(10) UNSIGNED NOT NULL DEFAULT 1024 AFTER id,
	ADD INDEX (user_id),
	ADD FOREIGN KEY (user_id) REFERENCES USER (id)
;

ALTER TABLE SYSTEM
	MODIFY COLUMN user_id INT(10) UNSIGNED NOT NULL
;

ALTER TABLE ADVERT
	MODIFY COLUMN user_id INT(10) UNSIGNED AFTER id
;

INSERT INTO ACTION (id)
VALUES ("SYSTEM_EXPORT_APPLICATIONS"),
	("INSTITUTION_EXPORT_APPLICATIONS"),
	("PROGRAM_EXPORT_APPLICATIONS")
;

INSERT INTO STATE_ACTION(state_id, action_id, raises_urgent_flag)
	SELECT "SYSTEM_APPROVED", "SYSTEM_EXPORT_APPLICATIONS", 0
		UNION
	SELECT "INSTITUTION_APPROVED", "INSTITUTION_EXPORT_APPLICATIONS", 0
		UNION
	SELECT id, "PROGRAM_EXPORT_APPLICATIONS", 0
	FROM STATE
	WHERE parent_state_id != "PROGRAM_APPROVAL"
;

INSERT INTO STATE_ACTION_ASSIGNMENT(state_action_id, role_id)
	SELECT id, "SYSTEM_ADMINISTRATOR"
	FROM STATE_ACTION
	WHERE action_id = "SYSTEM_EXPORT_APPLICATIONS"
		UNION
	SELECT STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "INSTITUTION_EXPORT_APPLICATIONS"
		AND ROLE.id IN ("SYSTEM_ADMINISTRATOR", "INSTITUTION_ADMINISTRATOR")
		UNION
	SELECT STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "PROGRAM_EXPORT_APPLICATIONS"
		AND ROLE.id IN ("SYSTEM_ADMINISTRATOR", "INSTITUTION_ADMINISTRATOR",
			"PROGRAM_ADMINISTRATOR")
;

/* Allow roles at time of action invokation to be stored as concatenated string */

ALTER TABLE COMMENT
	DROP INDEX role_id,
	DROP FOREIGN KEY comment_ibfk_5,
	DROP FOREIGN KEY comment_ibfk_7,
	DROP INDEX delegate_role_id,
	MODIFY COLUMN role_id VARCHAR(1000) NOT NULL,
	MODIFY COLUMN delegate_role_id VARCHAR(1000)
;
