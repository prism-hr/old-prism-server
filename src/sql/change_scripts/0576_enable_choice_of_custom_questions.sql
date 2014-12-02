ALTER TABLE APPLICATION_FORM
	 ADD COLUMN use_custom_reference_questions INT(1) UNSIGNED,
	 ADD INDEX (use_custom_reference_questions)
;
	
ALTER TABLE REVIEW_ROUND
	ADD COLUMN use_custom_questions INT(1) UNSIGNED,
	ADD INDEX (use_custom_questions)
;
	
ALTER TABLE INTERVIEW
	ADD COLUMN use_custom_questions INT(1) UNSIGNED,
	ADD INDEX (use_custom_questions)
;

ALTER TABLE STATECHANGE_COMMENT
	ADD COLUMN use_custom_questions INT(1) UNSIGNED,
	ADD INDEX (use_custom_questions),
	ADD COLUMN use_custom_reference_questions INT(1) UNSIGNED,
	ADD INDEX (use_custom_reference_questions)
;
