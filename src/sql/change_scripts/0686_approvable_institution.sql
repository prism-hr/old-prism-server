ALTER TABLE INSTITUTION
	MODIFY COLUMN address_id INT(10) UNSIGNED AFTER homepage,
	ADD COLUMN previous_state_id VARCHAR(50),
	ADD COLUMN due_date DATE,
	ADD COLUMN created_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	ADD COLUMN updated_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN created_timestamp DATETIME NOT NULL,
	MODIFY COLUMN updated_timestamp DATETIME NOT NULL
;

ALTER TABLE ADVERT
	 MODIFY COLUMN institution_address_id INT(10) UNSIGNED AFTER description
;
