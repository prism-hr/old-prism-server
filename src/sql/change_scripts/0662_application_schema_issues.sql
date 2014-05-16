/* Provide reference comment continued */

ALTER TABLE COMMENT
	ADD COLUMN application_suitable_for_institution INT(1) UNSIGNED AFTER application_residence_status,
	ADD COLUMN application_suitable_for_opportunity INT(1) UNSIGNED AFTER application_suitable_for_institution,
	ADD COLUMN application_rating INT(1) UNSIGNED AFTER application_suitable_for_opportunity
;

UPDATE COMMENT INNER JOIN REFERENCE_COMMENT
	ON COMMENT.id = REFERENCE_COMMENT.id
SET COMMENT.action_id = "APPLICATION_PROVIDE_REFERENCE",
	COMMENT.role_id = "APPLICATION_REFEREE",
	COMMENT.delegate_user_id = REFERENCE_COMMENT.user_id,
	COMMENT.delegate_role_id = IF(
		REFERENCE_COMMENT.user_id IS NOT NULL,
		"PROGRAM_ADMINISTRATOR",
		NULL),
	COMMENT.application_suitable_for_institution = REFERENCE_COMMENT.suitable_for_UCL,
	COMMENT.application_suitable_for_opportunity = REFERENCE_COMMENT.suitable_for_Programme,
	COMMENT.application_rating = REFERENCE_COMMENT.applicant_rating,
	COMMENT.created_timestamp = IF(
		REFERENCE_COMMENT.updated_time_stamp IS NOT NULL,
		REFERENCE_COMMENT.updated_time_stamp,
		COMMENT.created_timestamp)
;

DROP TABLE REFERENCE_COMMENT
;

ALTER TABLE COMMENT
	ADD COLUMN declined_response INT(1) UNSIGNED AFTER action_id
;

INSERT INTO COMMENT (application_id, action_id, user_id, role_id, created_timestamp, declined_response)
	SELECT EVENT.application_form_id, "APPLICATION_PROVIDE_REFERENCE", APPLICATION_REFEREE.user_id, "APPLICATION_REFEREE", EVENT.event_date, 1
	FROM APPLICATION_REFEREE INNER JOIN REFERENCE_EVENT
		ON APPLICATION_REFEREE.id = REFERENCE_EVENT.referee_id
	INNER JOIN EVENT
		ON REFERENCE_EVENT.id = EVENT.id
	WHERE APPLICATION_REFEREE.declined = 1
;

ALTER TABLE APPLICATION_REFEREE
	DROP COLUMN declined,
	CHANGE COLUMN send_to_UCL include_in_export INT(1) UNSIGNED
;

DROP TABLE REFERENCE_EVENT
;

UPDATE APPLICATION_REFEREE
SET comment_id = NULL
;

UPDATE APPLICATION_REFEREE INNER JOIN COMMENT
	ON APPLICATION_REFEREE.application_id = COMMENT.application_id
	AND APPLICATION_REFEREE.user_id = COMMENT.user_id
	AND COMMENT.action_id = "APPLICATION_PROVIDE_REFERENCE"
SET APPLICATION_REFEREE.comment_id = COMMENT.id
;

ALTER TABLE APPLICATION_QUALIFICATION
	CHANGE COLUMN export include_in_export INT(1) UNSIGNED
;

/* Assign reviewers comment */

ALTER TABLE COMMENT
	ADD COLUMN review_round_id INT(10) UNSIGNED,
	ADD COLUMN use_custom_recruiter_questions INT(1) UNSIGNED
;
		
INSERT INTO COMMENT (application_id, action_id, user_id, role_id, created_timestamp, transition_state_id, use_custom_recruiter_questions, review_round_id)
	SELECT EVENT.application_form_id, "APPLICATION_ASSIGN_REVIEWERS", EVENT.user_id, "PROGRAM_ADMINISTRATOR", 
		EVENT.event_date, "APPLICATION_REVIEW_PENDING_FEEDBACK", REVIEW_ROUND.use_custom_questions, REVIEW_ROUND.id
	FROM REVIEW_STATE_CHANGE_EVENT INNER JOIN EVENT
		ON REVIEW_STATE_CHANGE_EVENT.id = EVENT.id
	INNER JOIN REVIEW_ROUND
		ON REVIEW_STATE_CHANGE_EVENT.review_round_id = REVIEW_ROUND.id
;

INSERT IGNORE INTO COMMENT_ASSIGNED_USER (comment_id, user_id, role_id)
	SELECT COMMENT.id, REVIEWER.user_id, "APPLICATION_REVIEWER"
	FROM COMMENT INNER JOIN REVIEWER
		ON COMMENT.review_round_id = REVIEWER.review_round_id
;

ALTER TABLE COMMENT 
	DROP COLUMN review_round_id
;

DROP TABLE REVIEW_STATE_CHANGE_EVENT
;

ALTER TABLE REVIEW_COMMENT
	DROP FOREIGN KEY reviewer_review_fk,
	DROP COLUMN reviewer_id
;

DROP TABLE REVIEWER
;

ALTER TABLE APPLICATION
	DROP FOREIGN KEY latest_review_round_fk,
	DROP COLUMN latest_review_round_id
;

ALTER TABLE REVIEW_EVALUATION_COMMENT
	DROP FOREIGN KEY review_eval_com_rev_round_fk,
	DROP COLUMN review_round_id
;

DROP TABLE REVIEW_ROUND
;

/* Recreate primary key on role inheritance */

ALTER TABLE ROLE_INHERITANCE
	DROP PRIMARY KEY,
	DROP COLUMN id,
	ADD PRIMARY KEY (role_id, inherited_role_id),
	DROP INDEX role_id
;

ALTER TABLE USER_ROLE
	MODIFY COLUMN requesting_user_id INT(10) UNSIGNED
;
