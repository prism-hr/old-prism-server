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

/* Comment attribute as key value */
/* Find and label move to different stage */
/* Put use custom question in the right place */

CREATE TABLE COMMENT_ATTRIBUTE_TYPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
;

CREATE TABLE COMMENT_ATTRIBUTE_DATA_TYPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
;

INSERT INTO COMMENT_ATTRIBUTE_DATA_TYPE (id)
VALUES ("STRING"),
	("DATETIME"),
	("BOOLEAN"),
	("INTEGER"),
	("DOCUMENT")
;

CREATE TABLE COMMENT_ATTRIBUTE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL, 
	comment_attribute_type_id VARCHAR(50) NOT NULL,
	comment_attribute_data_type_id VARCHAR(50) NOT NULL,
	value_string LONGTEXT,
	value_integer BIGINT,
	value_datetime DATETIME,
	document_id INT(10) UNSIGNED,
	PRIMARY KEY (id),
	INDEX (comment_id),
	INDEX (comment_attribute_type_id),
	INDEX (comment_attribute_data_type_id),
	INDEX (value_integer),
	INDEX (document_id),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id),
	FOREIGN KEY (comment_attribute_type_id) REFERENCES COMMENT_ATTRIBUTE_TYPE (id),
	FOREIGN KEY (comment_attribute_data_type_id) REFERENCES COMMENT_ATTRIBUTE_DATA_TYPE (id),
	FOREIGN KEY (document_id) REFERENCES DOCUMENT (id)
) ENGINE = INNODB
;

/* Drop stored procedures */
