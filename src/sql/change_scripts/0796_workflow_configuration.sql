DELETE
FROM DISPLAY_PROPERTY
;

DELETE
FROM DISPLAY_CATEGORY
;

RENAME TABLE DISPLAY_PROPERTY TO DISPLAY_PROPERTY_OLD
;

CREATE TABLE DISPLAY_PROPERTY (
	id VARCHAR(100) NOT NULL,
	display_category VARCHAR(100) NOT NULL,
	scope_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	INDEX (scope_id),
	INDEX (scope_id, display_category, id),
	FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
) ENGINE = INNODB
;

CREATE TABLE DISPLAY_VALUE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	locale VARCHAR(10) NOT NULL,
	program_type VARCHAR(50),
	display_property_id VARCHAR(100) NOT NULL,
	value TEXT NOT NULL,
	system_default INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, locale, program_type, display_property_id),
	UNIQUE INDEX (institution_id, program_type, display_property_id),
	UNIQUE INDEX (program_id, display_property_id),
	INDEX (display_property_id),
	INDEX system_default (system_id, system_default)
) ENGINE = INNODB
;

DROP TABLE DISPLAY_PROPERTY_OLD
;

DROP TABLE DISPLAY_CATEGORY
;
