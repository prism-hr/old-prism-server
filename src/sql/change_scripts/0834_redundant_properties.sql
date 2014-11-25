DELETE
FROM STATE_DURATION_CONFIGURATION
WHERE state_duration_definition_id LIKE "SYSTEM_%"
;

DELETE
FROM STATE_DURATION_DEFINITION
WHERE id LIKE "SYSTEM_%"
;

DELETE FROM WORKFLOW_PROPERTY_CONFIGURATION
WHERE workflow_property_definition_id LIKE "%PROOF_OF_COMPETENCE%"
;

DELETE FROM WORKFLOW_PROPERTY_DEFINITION
WHERE id LIKE "%PROOF_OF_COMPETENCE%"
;

ALTER TABLE APPLICATION_DOCUMENT
	MODIFY COLUMN research_statement_id INTEGER(10) UNSIGNED,
	MODIFY COLUMN covering_letter_id INTEGER(10) UNSIGNED
;

ALTER TABLE APPLICATION_LANGUAGE_QUALIFICATION
	MODIFY COLUMN document_id INTEGER(10) UNSIGNED
;

ALTER TABLE APPLICATION_QUALIFICATION	
	MODIFY COLUMN document_id INTEGER(10) UNSIGNED
;

ALTER TABLE APPLICATION_FUNDING	
	MODIFY COLUMN document_id INTEGER(10) UNSIGNED
;

ALTER TABLE WORKFLOW_PROPERTY_DEFINITION
	CHANGE COLUMN range_specification define_range INT(1) UNSIGNED NOT NULL,
	ADD COLUMN can_be_disabled INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER define_range
;

ALTER TABLE WORKFLOW_PROPERTY_DEFINITION
	MODIFY COLUMN can_be_disabled INT(1) UNSIGNED NOT NULL
;

DELETE 
FROM WORKFLOW_PROPERTY_CONFIGURATION
;

ALTER TABLE WORKFLOW_PROPERTY_CONFIGURATION
	ADD COLUMN required INT(1) UNSIGNED AFTER enabled
;

ALTER TABLE WORKFLOW_PROPERTY_CONFIGURATION
	MODIFY COLUMN enabled INT(1) UNSIGNED
;

ALTER TABLE WORKFLOW_PROPERTY_DEFINITION
	ADD COLUMN category VARCHAR(50) NOT NULL DEFAULT "TEMP" AFTER id
;

ALTER TABLE WORKFLOW_PROPERTY_DEFINITION
	MODIFY COLUMN category VARCHAR(50) NOT NULL
;

DELETE
FROM ROLE_TRANSITION
;

DELETE
FROM WORKFLOW_PROPERTY_CONFIGURATION
;

DELETE
FROM WORKFLOW_PROPERTY_DEFINITION
;

ALTER TABLE DISPLAY_PROPERTY_DEFINITION
	CHANGE COLUMN display_property_category category VARCHAR(50) NOT NULL
;

ALTER TABLE COMMENT_CUSTOM_RESPONSE
	MODIFY COLUMN property_value TEXT
;
