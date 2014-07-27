ALTER TABLE SYSTEM
	CHANGE COLUMN code name VARCHAR(50) NOT NULL,
	MODIFY COLUMN state_id VARCHAR(50)
;

ALTER TABLE INSTITUTION
	DROP COLUMN code,
	MODIFY COLUMN state_id VARCHAR(50)
;

ALTER TABLE PROGRAM
	MODIFY COLUMN code VARCHAR(50),
	MODIFY COLUMN state_id VARCHAR(50)
;


UPDATE INSTITUTION INNER JOIN PROGRAM
	ON INSTITUTION.id = PROGRAM.institution_id
SET PROGRAM.code = CONCAT(LPAD(INSTITUTION.id, 10, 0), "-", REPLACE(PROGRAM.code, "0UCL-", ""))
;

ALTER TABLE PROJECT
	DROP COLUMN code,
	MODIFY COLUMN state_id VARCHAR(50)
;

ALTER TABLE APPLICATION
	DROP COLUMN code,
	MODIFY COLUMN state_id VARCHAR(50)
;

ALTER TABLE SCOPE
	ADD COLUMN short_code VARCHAR(2),
	ADD UNIQUE INDEX (short_code)
;

UPDATE SCOPE
SET short_code = CONCAT(SUBSTRING(id, 1, 1), SUBSTRING(id, (LENGTH(id)), 1))
;

ALTER TABLE SCOPE
	MODIFY COLUMN short_code VARCHAR(2) NOT NULL
;
