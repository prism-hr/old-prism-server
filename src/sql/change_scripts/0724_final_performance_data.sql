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

UPDATE PROJECT INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		COUNT(DISTINCT COMMENT.application_id) AS application_count
	FROM APPLICATION INNER JOIN COMMENT
		ON APPLICATION.id = COMMENT.application_id
	WHERE APPLICATION.project_id IS NOT NULL
		AND COMMENT.action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
	GROUP BY APPLICATION.project_id) AS VALIDATED
	ON PROJECT.id = VALIDATED.project_id
SET PROJECT.application_validated_count = VALIDATED.application_count
;

UPDATE PROJECT INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		COUNT(DISTINCT COMMENT.application_id) AS application_count
	FROM APPLICATION INNER JOIN COMMENT
		ON APPLICATION.id = COMMENT.application_id
	WHERE APPLICATION.project_id IS NOT NULL
		AND COMMENT.action_id = "APPLICATION_COMPLETE_REVIEW_STAGE"
	GROUP BY APPLICATION.project_id) AS REVIEWED
	ON PROJECT.id = REVIEWED.project_id
SET PROJECT.application_reviewed_count = REVIEWED.application_count
;

UPDATE PROJECT INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		COUNT(DISTINCT COMMENT.application_id) AS application_count
	FROM APPLICATION INNER JOIN COMMENT
		ON APPLICATION.id = COMMENT.application_id
	WHERE APPLICATION.project_id IS NOT NULL
		AND COMMENT.action_id = "APPLICATION_COMPLETE_INTERVIEW_STAGE"
	GROUP BY APPLICATION.project_id) AS INTERVIEWED
	ON PROJECT.id = INTERVIEWED.project_id
SET PROJECT.application_interviewed_count = INTERVIEWED.application_count
;

UPDATE PROJECT INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		COUNT(DISTINCT COMMENT.application_id) AS application_count
	FROM APPLICATION INNER JOIN COMMENT
		ON APPLICATION.id = COMMENT.application_id
	WHERE APPLICATION.project_id IS NOT NULL
		AND COMMENT.action_id = "APPLICATION_COMPLETE_APPROVAL_STAGE"
	GROUP BY APPLICATION.project_id) AS APPROVALED
	ON PROJECT.id = APPROVALED.project_id
SET PROJECT.application_approvaled_count = APPROVALED.application_count
;

UPDATE PROGRAM INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		COUNT(APPLICATION.id) AS application_count
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE APPLICATION.program_id IS NOT NULL
		AND STATE.state_group_id = "APPLICATION_VALIDATION"
	GROUP BY APPLICATION.program_id) AS VALIDATION
	ON PROGRAM.id = VALIDATION.program_id
SET PROGRAM.application_in_validation_count = VALIDATION.application_count
;

UPDATE PROGRAM INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		COUNT(APPLICATION.id) AS application_count
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE APPLICATION.program_id IS NOT NULL
		AND STATE.state_group_id = "APPLICATION_REVIEW"
	GROUP BY APPLICATION.program_id) AS REVIEW
	ON PROGRAM.id = REVIEW.program_id
SET PROGRAM.application_in_review_count = REVIEW.application_count
;

UPDATE PROGRAM INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		COUNT(APPLICATION.id) AS application_count
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE APPLICATION.program_id IS NOT NULL
		AND STATE.state_group_id = "APPLICATION_INTERVIEW"
	GROUP BY APPLICATION.program_id) AS INTERVIEW
	ON PROGRAM.id = INTERVIEW.program_id
SET PROGRAM.application_in_interview_count = INTERVIEW.application_count
;

UPDATE PROGRAM INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		COUNT(APPLICATION.id) AS application_count
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE program_id IS NOT NULL
		AND STATE.state_group_id = "APPLICATION_APPROVAL"
	GROUP BY APPLICATION.program_id) AS APPROVAL
	ON PROGRAM.id = APPROVAL.program_id
SET PROGRAM.application_in_approval_count = APPROVAL.application_count
;

UPDATE PROGRAM INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		COUNT(DISTINCT COMMENT.application_id) AS application_count
	FROM APPLICATION INNER JOIN COMMENT
		ON APPLICATION.id = COMMENT.application_id
	WHERE APPLICATION.program_id IS NOT NULL
		AND COMMENT.action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
	GROUP BY APPLICATION.program_id) AS VALIDATED
	ON PROGRAM.id = VALIDATED.program_id
SET PROGRAM.application_validated_count = VALIDATED.application_count
;

UPDATE PROGRAM INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		COUNT(DISTINCT COMMENT.application_id) AS application_count
	FROM APPLICATION INNER JOIN COMMENT
		ON APPLICATION.id = COMMENT.application_id
	WHERE APPLICATION.program_id IS NOT NULL
		AND COMMENT.action_id = "APPLICATION_COMPLETE_REVIEW_STAGE"
	GROUP BY APPLICATION.program_id) AS REVIEWED
	ON PROGRAM.id = REVIEWED.program_id
SET PROGRAM.application_reviewed_count = REVIEWED.application_count
;

UPDATE PROGRAM INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		COUNT(DISTINCT COMMENT.application_id) AS application_count
	FROM APPLICATION INNER JOIN COMMENT
		ON APPLICATION.id = COMMENT.application_id
	WHERE APPLICATION.program_id IS NOT NULL
		AND COMMENT.action_id = "APPLICATION_COMPLETE_INTERVIEW_STAGE"
	GROUP BY APPLICATION.program_id) AS INTERVIEWED
	ON PROGRAM.id = INTERVIEWED.program_id
SET PROGRAM.application_interviewed_count = INTERVIEWED.application_count
;

UPDATE PROGRAM INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		COUNT(DISTINCT COMMENT.application_id) AS application_count
	FROM APPLICATION INNER JOIN COMMENT
		ON APPLICATION.id = COMMENT.application_id
	WHERE APPLICATION.program_id IS NOT NULL
		AND COMMENT.action_id = "APPLICATION_COMPLETE_APPROVAL_STAGE"
	GROUP BY APPLICATION.program_id) AS APPROVALED
	ON PROGRAM.id = APPROVALED.program_id
SET PROGRAM.application_approvaled_count = APPROVALED.application_count
;

UPDATE INSTITUTION INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		COUNT(APPLICATION.id) AS application_count
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE APPLICATION.institution_id IS NOT NULL
		AND STATE.state_group_id = "APPLICATION_VALIDATION"
	GROUP BY APPLICATION.institution_id) AS VALIDATION
	ON INSTITUTION.id = VALIDATION.institution_id
SET INSTITUTION.application_in_validation_count = VALIDATION.application_count
;

UPDATE INSTITUTION INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		COUNT(APPLICATION.id) AS application_count
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE APPLICATION.institution_id IS NOT NULL
		AND STATE.state_group_id = "APPLICATION_REVIEW"
	GROUP BY APPLICATION.institution_id) AS REVIEW
	ON INSTITUTION.id = REVIEW.institution_id
SET INSTITUTION.application_in_review_count = REVIEW.application_count
;

UPDATE INSTITUTION INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		COUNT(APPLICATION.id) AS application_count
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE APPLICATION.institution_id IS NOT NULL
		AND STATE.state_group_id = "APPLICATION_INTERVIEW"
	GROUP BY APPLICATION.institution_id) AS INTERVIEW
	ON INSTITUTION.id = INTERVIEW.institution_id
SET INSTITUTION.application_in_interview_count = INTERVIEW.application_count
;

UPDATE INSTITUTION INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		COUNT(APPLICATION.id) AS application_count
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE institution_id IS NOT NULL
		AND STATE.state_group_id = "APPLICATION_APPROVAL"
	GROUP BY APPLICATION.institution_id) AS APPROVAL
	ON INSTITUTION.id = APPROVAL.institution_id
SET INSTITUTION.application_in_approval_count = APPROVAL.application_count
;

UPDATE INSTITUTION INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		COUNT(DISTINCT COMMENT.application_id) AS application_count
	FROM APPLICATION INNER JOIN COMMENT
		ON APPLICATION.id = COMMENT.application_id
	WHERE APPLICATION.institution_id IS NOT NULL
		AND COMMENT.action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
	GROUP BY APPLICATION.institution_id) AS VALIDATED
	ON INSTITUTION.id = VALIDATED.institution_id
SET INSTITUTION.application_validated_count = VALIDATED.application_count
;

UPDATE INSTITUTION INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		COUNT(DISTINCT COMMENT.application_id) AS application_count
	FROM APPLICATION INNER JOIN COMMENT
		ON APPLICATION.id = COMMENT.application_id
	WHERE APPLICATION.institution_id IS NOT NULL
		AND COMMENT.action_id = "APPLICATION_COMPLETE_REVIEW_STAGE"
	GROUP BY APPLICATION.institution_id) AS REVIEWED
	ON INSTITUTION.id = REVIEWED.institution_id
SET INSTITUTION.application_reviewed_count = REVIEWED.application_count
;

UPDATE INSTITUTION INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		COUNT(DISTINCT COMMENT.application_id) AS application_count
	FROM APPLICATION INNER JOIN COMMENT
		ON APPLICATION.id = COMMENT.application_id
	WHERE APPLICATION.institution_id IS NOT NULL
		AND COMMENT.action_id = "APPLICATION_COMPLETE_INTERVIEW_STAGE"
	GROUP BY APPLICATION.institution_id) AS INTERVIEWED
	ON INSTITUTION.id = INTERVIEWED.institution_id
SET INSTITUTION.application_interviewed_count = INTERVIEWED.application_count
;

UPDATE INSTITUTION INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		COUNT(DISTINCT COMMENT.application_id) AS application_count
	FROM APPLICATION INNER JOIN COMMENT
		ON APPLICATION.id = COMMENT.application_id
	WHERE APPLICATION.institution_id IS NOT NULL
		AND COMMENT.action_id = "APPLICATION_COMPLETE_APPROVAL_STAGE"
	GROUP BY APPLICATION.institution_id) AS APPROVALED
	ON INSTITUTION.id = APPROVALED.institution_id
SET INSTITUTION.application_approvaled_count = APPROVALED.application_count
;

