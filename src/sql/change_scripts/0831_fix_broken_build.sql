ALTER TABLE ACTION_CUSTOM_QUESTION_CONFIGURATION
	DROP INDEX system_id,
	DROP INDEX institution_id,
	DROP INDEX program_id

ALTER TABLE ACTION_CUSTOM_QUESTION_CONFIGURATION
	ADD UNIQUE INDEX (system_id, program_type, locale, action_custom_question_definition_id, version, display_index),
	ADD UNIQUE INDEX (institution_id, program_type, action_custom_question_definition_id, version, display_index),
	ADD UNIQUE INDEX (program_id, action_custom_question_definition_id, version, display_index)
;

ALTER TABLE WORKFLOW_PROPERTY_CONFIGURATION
	DROP INDEX system_id,
	DROP INDEX institution_id,
	DROP INDEX program_id
;

ALTER TABLE WORKFLOW_PROPERTY_CONFIGURATION
	ADD UNIQUE INDEX (system_id, program_type, locale, workflow_property_definition_id, version),
	ADD UNIQUE INDEX (institution_id, program_type, workflow_property_definition_id, version),
	ADD UNIQUE INDEX (program_id, workflow_property_definition_id, version)
;

ALTER TABLE APPLICATION
	ADD COLUMN workflow_property_configuration_version INT(10) UNSIGNED AFTER last_notified_update_syndicated
;

ALTER TABLE PROJECT
	ADD COLUMN workflow_property_configuration_version INT(10) UNSIGNED AFTER last_notified_update_syndicated
;

ALTER TABLE PROGRAM
	ADD COLUMN workflow_property_configuration_version INT(10) UNSIGNED AFTER last_notified_update_syndicated
;

ALTER TABLE INSTITUTION
	ADD COLUMN workflow_property_configuration_version INT(10) UNSIGNED AFTER last_notified_update_syndicated
;

ALTER TABLE WORKFLOW_PROPERTY_CONFIGURATION
	ADD COLUMN active INT(1) UNSIGNED NOT NULL DEFAULT 1 AFTER maximum,
	ADD INDEX (active)
;

ALTER TABLE WORKFLOW_PROPERTY_CONFIGURATION
	MODIFY COLUMN active INT(1) UNSIGNED NOT NULL
;

ALTER TABLE WORKFLOW_PROPERTY_DEFINITION
	DROP COLUMN optional
;

ALTER TABLE APPLICATION
	CHANGE COLUMN theme primary_theme TEXT,
	ADD COLUMN secondary_theme TEXT after primary_theme
;
