ALTER TABLE COMMENT_CUSTOM_QUESTION
	ADD COLUMN program_type VARCHAR(50) AFTER system_id,
	ADD COLUMN locale VARCHAR(10) AFTER program_type,
	DROP INDEX system_id,
	DROP INDEX institution_id,
	DROP INDEX program_id,
	ADD UNIQUE INDEX (system_id, program_type, locale, action_id),
	ADD UNIQUE INDEX (institution_id, program_type, action_id),
	ADD UNIQUE INDEX (program_id, action_id),
	ADD COLUMN system_default INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (system_id, system_default)
;

ALTER TABLE COMMENT_CUSTOM_QUESTION
	MODIFY COLUMN system_default INT(1) UNSIGNED NOT NULL
;

ALTER TABLE NOTIFICATION_CONFIGURATION
	ADD COLUMN program_type VARCHAR(50) AFTER system_id,
	ADD COLUMN locale VARCHAR(10) AFTER program_type,
	DROP INDEX system_id,
	DROP INDEX institution_id,
	DROP INDEX program_id,
	ADD UNIQUE INDEX (system_id, program_type, locale, notification_template_id),
	ADD UNIQUE INDEX (institution_id, program_type, notification_template_id),
	ADD UNIQUE INDEX (program_id, notification_template_id),
	ADD COLUMN system_default INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (system_id, system_default)
;

ALTER TABLE NOTIFICATION_CONFIGURATION
	MODIFY COLUMN system_default INT(1) UNSIGNED NOT NULL
;

ALTER TABLE STATE_DURATION
	ADD COLUMN program_type VARCHAR(50) AFTER system_id,
	ADD COLUMN locale VARCHAR(10) AFTER program_type,
	DROP INDEX system_id,
	DROP INDEX institution_id,
	DROP INDEX program_id,
	ADD UNIQUE INDEX (system_id, program_type, locale, state_id),
	ADD UNIQUE INDEX (institution_id, program_type, state_id),
	ADD UNIQUE INDEX (program_id, state_id),
	ADD COLUMN system_default INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (system_id, system_default)
;

ALTER TABLE STATE_DURATION
	MODIFY COLUMN system_default INT(1) UNSIGNED NOT NULL
;
