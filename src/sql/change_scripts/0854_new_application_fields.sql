ALTER TABLE APPLICATION
	ADD COLUMN study_application_id VARCHAR(255) AFTER study_program,
	ADD INDEX (study_application_id, sequence_identifier)
;

ALTER TABLE APPLICATION
	ADD COLUMN previous_application INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER application_program_detail_id,
	ADD INDEX (previous_application, sequence_identifier)
;

ALTER TABLE APPLICATION
	MODIFY COLUMN previous_application INT(1) UNSIGNED NOT NULL
;
