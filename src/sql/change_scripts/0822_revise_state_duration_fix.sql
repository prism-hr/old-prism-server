ALTER TABLE WORKFLOW_PROPERTY_DEFINITION
	DROP FOREIGN KEY workflow_property_definition_ibfk_1,
	DROP INDEX scope_id
;

ALTER TABLE WORKFLOW_PROPERTY_DEFINITION
	ADD INDEX (scope_id),
	ADD FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
;

ALTER TABLE DISPLAY_PROPERTY_DEFINITION
	CHANGE COLUMN display_category display_property_category VARCHAR(50) NOT NULL
;

SET FOREIGN_KEY_CHECKS = 0
;

ALTER TABLE STATE
	MODIFY COLUMN state_duration_definition_id VARCHAR(100)
;

SET FOREIGN_KEY_CHECKS = 1
;

ALTER TABLE STATE_DURATION_CONFIGURATION
	CHANGE COLUMN day_duration duration INT(3) UNSIGNED NOT NULL
;