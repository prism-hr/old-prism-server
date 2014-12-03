UPDATE STATE_DURATION
SET locale = "EN_GB"
;

ALTER TABLE STATE_DURATION
	MODIFY COLUMN locale VARCHAR(10) NOT NULL DEFAULT "EN_GB" AFTER program_id,
	MODIFY COLUMN program_type VARCHAR(50) AFTER locale,
	DROP INDEX system_id,
	DROP INDEX institution_id,
	DROP INDEX program_id,
	ADD UNIQUE INDEX (system_id, locale, program_type, state_id),
	ADD UNIQUE INDEX (institution_id, program_type, state_id),
	ADD UNIQUE INDEX (program_id, state_id),
	DROP INDEX system_id_2,
	ADD INDEX system_default (system_id, system_default)
;

ALTER TABLE STATE_DURATION
	MODIFY COLUMN locale VARCHAR(10) NOT NULL
;

UPDATE NOTIFICATION_CONFIGURATION
SET locale = "EN_GB"
;

ALTER TABLE NOTIFICATION_CONFIGURATION
	MODIFY COLUMN locale VARCHAR(10) NOT NULL DEFAULT "EN_GB" AFTER program_id,
	MODIFY COLUMN program_type VARCHAR(50) AFTER locale,
	DROP INDEX system_id,
	DROP INDEX institution_id,
	DROP INDEX program_id,
	ADD UNIQUE INDEX (system_id, locale, program_type, notification_template_id),
	ADD UNIQUE INDEX (institution_id, program_type, notification_template_id),
	ADD UNIQUE INDEX (program_id, notification_template_id),
	DROP INDEX system_id_2,
	ADD INDEX system_default (system_id, system_default)
;

ALTER TABLE NOTIFICATION_CONFIGURATION
	MODIFY COLUMN locale VARCHAR(10) NOT NULL
;

UPDATE DISPLAY_PROPERTY
SET locale = "EN_GB"
;

ALTER TABLE DISPLAY_PROPERTY
	MODIFY COLUMN locale VARCHAR(10) NOT NULL DEFAULT "EN_GB" AFTER program_id,
	MODIFY COLUMN program_type VARCHAR(50) AFTER locale,
	DROP INDEX system_id,
	DROP INDEX institution_id,
	DROP INDEX program_id,
	ADD UNIQUE INDEX (system_id, locale, program_type, property_index),
	ADD UNIQUE INDEX (institution_id, program_type, property_index),
	ADD UNIQUE INDEX (program_id, property_index),
	DROP INDEX system_id_2,
	DROP INDEX system_id_3,
	DROP INDEX institution_id_2,
	DROP INDEX program_id_2,
	ADD INDEX system_default (system_id, system_default),
	ADD INDEX (system_id, locale, program_type, display_category_id),
	ADD INDEX (institution_id, program_type, display_category_id),
	ADD INDEX (program_id, display_category_id) 
;

ALTER TABLE DISPLAY_PROPERTY
	MODIFY COLUMN locale VARCHAR(10) NOT NULL
;

UPDATE COMMENT_CUSTOM_QUESTION
SET locale = "EN_GB"
;

ALTER TABLE COMMENT_CUSTOM_QUESTION
	MODIFY COLUMN locale VARCHAR(10) NOT NULL DEFAULT "EN_GB" AFTER program_id,
	MODIFY COLUMN program_type VARCHAR(50) AFTER locale,
	DROP INDEX system_id,
	DROP INDEX institution_id,
	DROP INDEX program_id,
	ADD UNIQUE INDEX (system_id, locale, program_type, action_id),
	ADD UNIQUE INDEX (institution_id, program_type, action_id),
	ADD UNIQUE INDEX (program_id, action_id),
	DROP INDEX system_id_2,
	ADD INDEX system_default (system_id, system_default)
;

ALTER TABLE COMMENT_CUSTOM_QUESTION
	MODIFY COLUMN locale VARCHAR(10) NOT NULL
;

