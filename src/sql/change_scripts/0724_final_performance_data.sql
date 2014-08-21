ALTER TABLE PROJECT
	ADD COLUMN application_in_validation_count INT(10) UNSIGNED AFTER title,
	ADD COLUMN application_in_review_count INT(10) UNSIGNED AFTER application_in_validation_count,
	ADD COLUMN application_in_interview_count INT(10) UNSIGNED AFTER application_in_review_count,
	ADD COLUMN application_in_approval_count INT(10) UNSIGNED AFTER application_in_interview_count,
	ADD COLUMN application_validated_count INT(10) UNSIGNED AFTER application_submitted_count,
	ADD COLUMN application_reviewed_count INT(10) UNSIGNED AFTER application_validated_count,
	ADD COLUMN application_interviewed_count INT(10) UNSIGNED AFTER application_reviewed_count,
	ADD COLUMN application_approvaled_count INT(10) UNSIGNED AFTER application_interviewed_count,
	ADD INDEX (application_in_validation_count, sequence_identifier),
	ADD INDEX (application_in_review_count, sequence_identifier),
	ADD INDEX (application_in_interview_count, sequence_identifier),
	ADD INDEX (application_in_approval_count, sequence_identifier),
	ADD INDEX (application_validated_count, sequence_identifier),
	ADD INDEX (application_reviewed_count, sequence_identifier),
	ADD INDEX (application_interviewed_count, sequence_identifier),
	ADD INDEX (application_approvaled_count, sequence_identifier)
;

ALTER TABLE PROGRAM
	ADD COLUMN application_in_validation_count INT(10) UNSIGNED AFTER require_project_definition,
	ADD COLUMN application_in_review_count INT(10) UNSIGNED AFTER application_in_validation_count,
	ADD COLUMN application_in_interview_count INT(10) UNSIGNED AFTER application_in_review_count,
	ADD COLUMN application_in_approval_count INT(10) UNSIGNED AFTER application_in_interview_count,
	ADD COLUMN application_validated_count INT(10) UNSIGNED AFTER application_submitted_count,
	ADD COLUMN application_reviewed_count INT(10) UNSIGNED AFTER application_validated_count,
	ADD COLUMN application_interviewed_count INT(10) UNSIGNED AFTER application_reviewed_count,
	ADD COLUMN application_approvaled_count INT(10) UNSIGNED AFTER application_interviewed_count,
	ADD INDEX (application_in_validation_count, sequence_identifier),
	ADD INDEX (application_in_review_count, sequence_identifier),
	ADD INDEX (application_in_interview_count, sequence_identifier),
	ADD INDEX (application_in_approval_count, sequence_identifier),
	ADD INDEX (application_validated_count, sequence_identifier),
	ADD INDEX (application_reviewed_count, sequence_identifier),
	ADD INDEX (application_interviewed_count, sequence_identifier),
	ADD INDEX (application_approvaled_count, sequence_identifier)
;

ALTER TABLE INSTITUTION
	ADD COLUMN application_in_validation_count INT(10) UNSIGNED AFTER is_ucl_institution,
	ADD COLUMN application_in_review_count INT(10) UNSIGNED AFTER application_in_validation_count,
	ADD COLUMN application_in_interview_count INT(10) UNSIGNED AFTER application_in_review_count,
	ADD COLUMN application_in_approval_count INT(10) UNSIGNED AFTER application_in_interview_count,
	ADD COLUMN application_validated_count INT(10) UNSIGNED AFTER application_submitted_count,
	ADD COLUMN application_reviewed_count INT(10) UNSIGNED AFTER application_validated_count,
	ADD COLUMN application_interviewed_count INT(10) UNSIGNED AFTER application_reviewed_count,
	ADD COLUMN application_approvaled_count INT(10) UNSIGNED AFTER application_interviewed_count,
	ADD INDEX (application_in_validation_count, sequence_identifier),
	ADD INDEX (application_in_review_count, sequence_identifier),
	ADD INDEX (application_in_interview_count, sequence_identifier),
	ADD INDEX (application_in_approval_count, sequence_identifier),
	ADD INDEX (application_validated_count, sequence_identifier),
	ADD INDEX (application_reviewed_count, sequence_identifier),
	ADD INDEX (application_interviewed_count, sequence_identifier),
	ADD INDEX (application_approvaled_count, sequence_identifier)
;

UPDATE PROJECT INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		COUNT(APPLICATION.id) AS application_count
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE APPLICATION.project_id IS NOT NULL
		AND STATE.state_group_id = "APPLICATION_VALIDATION"
	GROUP BY APPLICATION.project_id) AS VALIDATION
	ON PROJECT.id = VALIDATION.project_id
SET PROJECT.application_in_validation_count = VALIDATION.application_count
;

UPDATE PROJECT INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		COUNT(APPLICATION.id) AS application_count
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE APPLICATION.project_id IS NOT NULL
		AND STATE.state_group_id = "APPLICATION_REVIEW"
	GROUP BY APPLICATION.project_id) AS REVIEW
	ON PROJECT.id = REVIEW.project_id
SET PROJECT.application_in_review_count = REVIEW.application_count
;

UPDATE PROJECT INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		COUNT(APPLICATION.id) AS application_count
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE APPLICATION.project_id IS NOT NULL
		AND STATE.state_group_id = "APPLICATION_INTERVIEW"
	GROUP BY APPLICATION.project_id) AS INTERVIEW
	ON PROJECT.id = INTERVIEW.project_id
SET PROJECT.application_in_interview_count = INTERVIEW.application_count
;

UPDATE PROJECT INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		COUNT(APPLICATION.id) AS application_count
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE project_id IS NOT NULL
		AND STATE.state_group_id = "APPLICATION_APPROVAL"
	GROUP BY APPLICATION.project_id) AS APPROVAL
	ON PROJECT.id = APPROVAL.project_id
SET PROJECT.application_in_approval_count = APPROVAL.application_count
;