ALTER TABLE DISPLAY_PROPERTY
	DROP INDEX scope_id_2
;

ALTER TABLE DISPLAY_PROPERTY
	ADD INDEX (scope_id, display_category)
;

CREATE TABLE WORKFLOW_PROPERTY_DEFINITION (
	id VARCHAR(100) NOT NULL,
	workflow_property_category VARCHAR(100) NOT NULL,
	scope_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (scope_id, workflow_property_category),
	FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
) ENGINE = INNODB
;

CREATE TABLE WORKFLOW_PROPERTY_CONFIGURATION ( 
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	locale VARCHAR(10) NOT NULL,
	program_type VARCHAR(50),
	workflow_property_definition_id VARCHAR(100) NOT NULL,
	enabled INT(1) UNSIGNED NOT NULL,
	minimum INT(3) UNSIGNED,
	maximum INT(3) UNSIGNED,
	state_id VARCHAR(50),
	system_default INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, locale, program_type, workflow_property_definition_id),
	UNIQUE INDEX (institution_id, program_type, workflow_property_definition_id),
	UNIQUE INDEX (program_id, workflow_property_definition_id),
	INDEX (system_id, system_default)
) ENGINE = INNODB
;

	