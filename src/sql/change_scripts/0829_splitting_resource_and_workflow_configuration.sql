CREATE TABLE RESOURCE_PROPERTY_DEFINITION (
	id VARCHAR(100) NOT NULL,
	optional INT(1) UNSIGNED NOT NULL,
	range_specification INT(1) UNSIGNED NOT NULL,
	minimum_permitted INT(3) UNSIGNED NULL DEFAULT NULL,
	maximum_permitted INT(3) UNSIGNED NULL DEFAULT NULL,
	scope_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	INDEX scope_id (scope_id),
	CONSTRAINT FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
) ENGINE = INNODB
;

CREATE TABLE RESOURCE_PROPERTY_CONFIGURATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED NULL DEFAULT NULL,
	institution_id INT(10) UNSIGNED NULL DEFAULT NULL,
	program_id INT(10) UNSIGNED NULL DEFAULT NULL,
	locale VARCHAR(10) NULL DEFAULT NULL,
	program_type VARCHAR(50) NULL DEFAULT NULL,
	resource_property_definition_id VARCHAR(100) NOT NULL,
	version INT(10) UNSIGNED,
	enabled INT(1) UNSIGNED NOT NULL,
	minimum INT(3) UNSIGNED NULL DEFAULT NULL,
	maximum INT(3) UNSIGNED NULL DEFAULT NULL,
	system_default INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, locale, program_type, resource_property_definition_id),
	UNIQUE INDEX (institution_id, program_type, resource_property_definition_id),
	UNIQUE INDEX (program_id, resource_property_definition_id),
	INDEX system_default (system_id, system_default),
	INDEX (version),
	FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (resource_property_definition_id) REFERENCES RESOURCE_PROPERTY_DEFINITION (id)
) ENGINE = INNODB
;

ALTER TABLE WORKFLOW_PROPERTY_CONFIGURATION
	ADD FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	ADD FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	ADD FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	ADD FOREIGN KEY (workflow_property_definition_id) REFERENCES WORKFLOW_PROPERTY_DEFINITION (id)
;
