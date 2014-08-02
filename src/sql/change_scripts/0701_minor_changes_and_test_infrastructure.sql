ALTER TABLE SCOPE
	ADD COLUMN fallback_action_id VARCHAR(100),
	ADD INDEX (fallback_action_id),
	ADD FOREIGN KEY (fallback_action_id) REFERENCES ACTION(id)
;

ALTER TABLE IMPORTED_ENTITY_FEED
	ADD COLUMN last_imported_date DATE,
	ADD INDEX (last_imported_date)
;

CREATE TABLE PASSED_TEST (
	id VARCHAR(100) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
;
	