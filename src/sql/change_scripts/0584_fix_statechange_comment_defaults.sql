UPDATE STATECHANGE_COMMENT
SET use_custom_questions = 0
WHERE use_custom_questions IS NULL
;

UPDATE STATECHANGE_COMMENT
SET use_custom_reference_questions = 0
WHERE use_custom_reference_questions IS NULL
;

ALTER TABLE STATECHANGE_COMMENT
	MODIFY use_custom_questions INT(1) UNSIGNED DEFAULT 0,
	MODIFY use_custom_reference_questions INT(1) UNSIGNED DEFAULT 0
;
