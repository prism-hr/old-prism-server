ALTER TABLE APPLICATION_DOCUMENT
	ADD COLUMN covering_letter_id INT(10) UNSIGNED,
	ADD INDEX (covering_letter_id),
	ADD FOREIGN KEY (covering_letter_id) REFERENCES DOCUMENT (id)
;

ALTER TABLE COMMENT_CUSTOM_RESPONSE
	DROP COLUMN custom_question_type,
	DROP COLUMN property_label,
	DROP COLUMN property_weight,
	ADD COLUMN action_custom_question_configuration_id INT(10) UNSIGNED NOT NULL AFTER id,
	ADD INDEX (action_custom_question_configuration_id),
	ADD FOREIGN KEY (action_custom_question_configuration_id) REFERENCES ACTION_CUSTOM_QUESTION_CONFIGURATION (id)
;

ALTER TABLE COMMENT
	DROP COLUMN action_custom_question_version
;
