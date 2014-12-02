ALTER TABLE DISPLAY_PROPERTY
	DROP INDEX system_id,
	DROP INDEX institution_id,
	DROP INDEX program_id,
	ADD UNIQUE INDEX (system_id, program_type, locale, property_index),
	ADD INDEX (system_id, program_type, locale, display_category_id, property_index),
	ADD UNIQUE INDEX (institution_id, program_type, property_index),
	ADD INDEX (institution_id, program_type, display_category_id, property_index),
	ADD UNIQUE INDEX (program_id, property_index),
	ADD INDEX (program_id, display_category_id, property_index),
	CHANGE COLUMN property_default system_default INT(1) UNSIGNED NOT NULL
;
