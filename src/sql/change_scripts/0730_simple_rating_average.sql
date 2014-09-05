ALTER TABLE PROJECT
	ADD COLUMN application_created_count INT(10) UNSIGNED AFTER title,
	ADD COLUMN application_approved_count INT(10) UNSIGNED AFTER application_submitted_count,
	ADD COLUMN application_rejected_count INT(10) UNSIGNED AFTER application_approved_count,
	ADD COLUMN application_withdrawn_count INT(10) UNSIGNED AFTER application_rejected_count,
	ADD COLUMN application_rating_count_average DECIMAL(10, 2) AFTER application_withdrawn_count,
	ADD COLUMN application_rating_average DECIMAL(3,2) AFTER application_rating_count_average,
	ADD INDEX (application_created_count, sequence_identifier),
	ADD INDEX (application_approved_count, sequence_identifier),
	ADD INDEX (application_rejected_count, sequence_identifier),
	ADD INDEX (application_withdrawn_count, sequence_identifier),
	ADD INDEX (application_rating_count_average, sequence_identifier),
	ADD INDEX (application_rating_average, sequence_identifier)
;

ALTER TABLE PROGRAM
	ADD COLUMN application_created_count INT(10) UNSIGNED AFTER imported,
	ADD COLUMN application_approved_count INT(10) UNSIGNED AFTER application_submitted_count,
	ADD COLUMN application_rejected_count INT(10) UNSIGNED AFTER application_approved_count,
	ADD COLUMN application_withdrawn_count INT(10) UNSIGNED AFTER application_rejected_count,
	ADD COLUMN application_rating_count_average DECIMAL(10, 2) AFTER application_withdrawn_count,
	ADD COLUMN application_rating_average DECIMAL(3,2) AFTER application_rating_count_average,
	ADD INDEX (application_created_count, sequence_identifier),
	ADD INDEX (application_approved_count, sequence_identifier),
	ADD INDEX (application_rejected_count, sequence_identifier),
	ADD INDEX (application_withdrawn_count, sequence_identifier),
	ADD INDEX (application_rating_count_average, sequence_identifier),
	ADD INDEX (application_rating_average, sequence_identifier)
;

ALTER TABLE INSTITUTION
	ADD COLUMN application_created_count INT(10) UNSIGNED AFTER is_ucl_institution,
	ADD COLUMN application_approved_count INT(10) UNSIGNED AFTER application_submitted_count,
	ADD COLUMN application_rejected_count INT(10) UNSIGNED AFTER application_approved_count,
	ADD COLUMN application_withdrawn_count INT(10) UNSIGNED AFTER application_rejected_count,
	ADD COLUMN application_rating_count_average DECIMAL(10, 2) AFTER application_withdrawn_count,
	ADD COLUMN application_rating_average DECIMAL(3,2) AFTER application_rating_count_average,
	ADD INDEX (application_created_count, sequence_identifier),
	ADD INDEX (application_approved_count, sequence_identifier),
	ADD INDEX (application_rejected_count, sequence_identifier),
	ADD INDEX (application_withdrawn_count, sequence_identifier),
	ADD INDEX (application_rating_count_average, sequence_identifier),
	ADD INDEX (application_rating_average, sequence_identifier)
;

ALTER TABLE APPLICATION
	DROP FOREIGN KEY application_ibfk_7,
	CHANGE COLUMN confirmed_supervisor_user_id confirmed_primary_supervisor_id INT(10) UNSIGNED,
	ADD FOREIGN KEY (confirmed_primary_supervisor_id) REFERENCES USER (id),
	ADD COLUMN confirmed_secondary_supervisor_id INT(10) UNSIGNED AFTER confirmed_primary_supervisor_id,
	ADD INDEX (confirmed_secondary_supervisor_id, sequence_identifier),
	ADD FOREIGN KEY (confirmed_secondary_supervisor_id) REFERENCES USER (id)
;

UPDATE APPLICATION INNER JOIN (
	SELECT COMMENT.application_id AS application_id,
		COMMENT.id AS comment_id,
		COMMENT_ASSIGNED_USER.user_id AS confirmed_secondary_supervisor_id
	FROM COMMENT INNER JOIN (
		SELECT COMMENT.application_id AS application_id,
			MAX(COMMENT.created_timestamp) AS created_timestamp
		FROM COMMENT
		WHERE COMMENT.action_id = "APPLICATION_CONFIRM_OFFER_RECOMMENDATION"
		GROUP BY COMMENT.application_id) AS CONFIRMATION
		ON COMMENT.application_id = CONFIRMATION.application_id
			AND COMMENT.created_timestamp = CONFIRMATION.created_timestamp
			AND COMMENT.action_id = "APPLICATION_CONFIRM_OFFER_RECOMMENDATION"
	INNER JOIN COMMENT_ASSIGNED_USER
		ON COMMENT.id = COMMENT_ASSIGNED_USER.comment_id
	AND COMMENT_ASSIGNED_USER.role_id = "APPLICATION_SECONDARY_SUPERVISOR") AS OFFER_SUMMARY
	ON APPLICATION.id = OFFER_SUMMARY.application_id
SET APPLICATION.confirmed_secondary_supervisor_id = OFFER_SUMMARY.confirmed_secondary_supervisor_id
;

UPDATE PROJECT INNER JOIN (
	SELECT COUNT(id) AS value,
		project_id AS project_id
	FROM APPLICATION
	GROUP BY project_id) AS APPLICATION_COUNT
	ON PROJECT.id = APPLICATION_COUNT.project_id
SET application_created_count = APPLICATION_COUNT.value
;

UPDATE PROJECT INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.project_id AS project_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_APPROVED"
		AND STATE.id != "APPLICATION_APPROVED"
	GROUP BY project_id) AS APPLICATION_COUNT
	ON PROJECT.id = APPLICATION_COUNT.project_id
SET application_approved_count = APPLICATION_COUNT.value
;

UPDATE PROJECT INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.project_id AS project_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_REJECTED"
		AND STATE.id != "APPLICATION_REJECTED"
	GROUP BY project_id) AS APPLICATION_COUNT
	ON PROJECT.id = APPLICATION_COUNT.project_id
SET application_rejected_count = APPLICATION_COUNT.value
;

UPDATE PROJECT INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.project_id AS project_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_WITHDRAWN"
	GROUP BY project_id) AS APPLICATION_COUNT
	ON PROJECT.id = APPLICATION_COUNT.project_id
SET application_withdrawn_count = APPLICATION_COUNT.value
;

UPDATE PROGRAM INNER JOIN (
	SELECT COUNT(id) AS value,
		program_id AS program_id
	FROM APPLICATION
	GROUP BY program_id) AS APPLICATION_COUNT
	ON PROGRAM.id = APPLICATION_COUNT.program_id
SET application_created_count = APPLICATION_COUNT.value
;

UPDATE PROGRAM INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.program_id AS program_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_APPROVED"
		AND STATE.id != "APPLICATION_APPROVED"
	GROUP BY program_id) AS APPLICATION_COUNT
	ON PROGRAM.id = APPLICATION_COUNT.program_id
SET application_approved_count = APPLICATION_COUNT.value
;

UPDATE PROGRAM INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.program_id AS program_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_REJECTED"
		AND STATE.id != "APPLICATION_REJECTED"
	GROUP BY program_id) AS APPLICATION_COUNT
	ON PROGRAM.id = APPLICATION_COUNT.program_id
SET application_rejected_count = APPLICATION_COUNT.value
;

UPDATE PROGRAM INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.program_id AS program_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_WITHDRAWN"
	GROUP BY program_id) AS APPLICATION_COUNT
	ON PROGRAM.id = APPLICATION_COUNT.program_id
SET application_withdrawn_count = APPLICATION_COUNT.value
;

UPDATE INSTITUTION INNER JOIN (
	SELECT COUNT(id) AS value,
		institution_id AS institution_id
	FROM APPLICATION
	GROUP BY institution_id) AS APPLICATION_COUNT
	ON INSTITUTION.id = APPLICATION_COUNT.institution_id
SET application_created_count = APPLICATION_COUNT.value
;

UPDATE INSTITUTION INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.institution_id AS institution_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_APPROVED"
		AND STATE.id != "APPLICATION_APPROVED"
	GROUP BY institution_id) AS APPLICATION_COUNT
	ON INSTITUTION.id = APPLICATION_COUNT.institution_id
SET application_approved_count = APPLICATION_COUNT.value
;

UPDATE INSTITUTION INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.institution_id AS institution_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_REJECTED"
		AND STATE.id != "APPLICATION_REJECTED"
	GROUP BY institution_id) AS APPLICATION_COUNT
	ON INSTITUTION.id = APPLICATION_COUNT.institution_id
SET application_rejected_count = APPLICATION_COUNT.value
;

UPDATE INSTITUTION INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.institution_id AS institution_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_WITHDRAWN"
	GROUP BY institution_id) AS APPLICATION_COUNT
	ON INSTITUTION.id = APPLICATION_COUNT.institution_id
SET application_withdrawn_count = APPLICATION_COUNT.value
;

UPDATE PROJECT INNER JOIN (
	SELECT project_id AS project_id,
		ROUND(AVG(rating_count), 2) AS rating_count_average,
		ROUND(AVG(rating_average), 2) AS rating_average
	FROM APPLICATION
	GROUP BY project_id) AS AVERAGE
	ON PROJECT.id = AVERAGE.project_id
SET PROJECT.application_rating_count_average = AVERAGE.rating_count_average,
	PROJECT.application_rating_average = AVERAGE.rating_average
;

UPDATE PROGRAM INNER JOIN (
	SELECT program_id AS program_id,
		ROUND(AVG(rating_count), 2) AS rating_count_average,
		ROUND(AVG(rating_average), 2) AS rating_average
	FROM APPLICATION
	GROUP BY program_id) AS AVERAGE
	ON PROGRAM.id = AVERAGE.program_id
SET PROGRAM.application_rating_count_average = AVERAGE.rating_count_average,
	PROGRAM.application_rating_average = AVERAGE.rating_average
;

UPDATE INSTITUTION INNER JOIN (
	SELECT institution_id AS institution_id,
		ROUND(AVG(rating_count), 2) AS rating_count_average,
		ROUND(AVG(rating_average), 2) AS rating_average
	FROM APPLICATION
	GROUP BY institution_id) AS AVERAGE
	ON INSTITUTION.id = AVERAGE.institution_id
SET INSTITUTION.application_rating_count_average = AVERAGE.rating_count_average,
	INSTITUTION.application_rating_average = AVERAGE.rating_average
;
