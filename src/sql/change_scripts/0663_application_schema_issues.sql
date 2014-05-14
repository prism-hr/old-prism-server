/* Provide review comment */

ALTER TABLE COMMENT
	ADD COLUMN application_desire_to_interview INT(1) UNSIGNED AFTER application_suitable_for_opportunity,
	ADD COLUMN application_desire_to_supervise INT(1) UNSIGNED AFTER application_desire_to_interview
;

UPDATE REVIEW_COMMENT INNER JOIN COMMENT
	ON REVIEW_COMMENT.id = COMMENT.id
SET COMMENT.role_id = "APPLICATION_REVIEWER",
	COMMENT.action_id = "APPLICATION_PROVIDE_REVIEW",
	COMMENT.application_suitable_for_institution = REVIEW_COMMENT.suitable_candidate,
	COMMENT.application_suitable_for_opportunity = REVIEW_COMMENT.applicant_suitable_for_programme,
	COMMENT.application_desire_to_interview = REVIEW_COMMENT.willing_to_interview,
	COMMENT.application_desire_to_supervise = REVIEW_COMMENT.willing_to_work_with_applicant,
	COMMENT.application_rating = REVIEW_COMMENT.applicant_rating,
	COMMENT.declined_response = REVIEW_COMMENT.decline
;

DROP TABLE REVIEW_COMMENT
;

/* Review evaluation comment */

ALTER TABLE COMMENT
	ADD COLUMN use_custom_recruiter_questions INT(1) UNSIGNED
;
	
UPDATE COMMENT INNER JOIN REVIEW_EVALUATION_COMMENT
	ON COMMENT.id = REVIEW_EVALUATION_COMMENT.id
INNER JOIN STATECHANGE_COMMENT
	ON REVIEW_EVALUATION_COMMENT.id = STATECHANGE_COMMENT.id
SET COMMENT.role_id = "APPLICATION_ADMINISTRATOR",
	COMMENT.action_id = "APPLICATION_COMPLETE_REVIEW_STAGE",
	COMMENT.transition_state_id = CONCAT("APPLICATION_", STATECHANGE_COMMENT.next_status),
	COMMENT.use_custom_recruiter_questions = STATECHANGE_COMMENT.use_custom_questions
;

INSERT INTO COMMENT_ASSIGNED_USER (comment_id, user_id, role_id)
	SELECT id, user_id, "APPLICATION_ADMINISTRATOR"
	FROM STATECHANGE_COMMENT
	WHERE comment_type = "REVIEW_EVALUATION"
		AND user_id IS NOT NULL
;

DROP TABLE REVIEW_EVALUATION_COMMENT
;

/* Assign interviewer comment */

/* Assign application administrators */
/* Assign use custom question flags */
/* Find and expose move to different stage comments */

/* Fix null constraints on comment table */