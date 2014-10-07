RENAME TABLE DISPLAY_CONFIGURATION TO DISPLAY_PROPERTY
;

CREATE TABLE DISPLAY_CATEGORY (
	id VARCHAR(50) NOT NULL,
	scope_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	INDEX (scope_id),
	FOREIGN KEY (scope_id) REFERENCES SCOPE(id)
) ENGINE = INNODB
;

ALTER TABLE DISPLAY_PROPERTY
	CHANGE COLUMN property_category display_category_id VARCHAR(50) NOT NULL,
	ADD INDEX (display_category_id),
	ADD FOREIGN KEY (display_category_id) REFERENCES DISPLAY_CATEGORY (id)
;

ALTER TABLE DISPLAY_PROPERTY
	ADD COLUMN property_default INT(1) UNSIGNED NOT NULL,
	ADD INDEX (system_id, property_default)
;

ALTER TABLE DISPLAY_PROPERTY
	CHANGE COLUMN property_key property_index VARCHAR(50) NOT NULL
;

ALTER TABLE COMMENT
	ADD COLUMN application_rejection_reason_display VARCHAR(255) AFTER application_rejection_reason_id
;

