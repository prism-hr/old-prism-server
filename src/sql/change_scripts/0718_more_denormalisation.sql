ALTER TABLE USER
	ADD COLUMN full_name VARCHAR(100) AFTER last_name,
	ADD INDEX (full_name)
;

UPDATE USER
SET USER.full_name = CONCAT(USER.first_name, " ", USER.last_name)
;

ALTER TABLE USER
	MODIFY COLUMN full_name VARCHAR(100) NOT NULL
;
