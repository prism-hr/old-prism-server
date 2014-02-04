UPDATE APPLICATION_FORM
SET use_custom_reference_questions = 0
;

UPDATE REVIEW_ROUND
SET use_custom_questions = 0
;

UPDATE INTERVIEW
SET use_custom_questions = 0
;

ALTER TABLE APPLICATION_FORM
	MODIFY use_custom_reference_questions INT(1) UNSIGNED NOT NULL DEFAULT 0
;

ALTER TABLE REVIEW_ROUND
	MODIFY use_custom_questions INT(1) UNSIGNED NOT NULL DEFAULT 0
;

ALTER TABLE INTERVIEW
	MODIFY use_custom_questions INT(1) UNSIGNED NOT NULL DEFAULT 0
;
