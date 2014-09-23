ALTER TABLE APPLICATION
	ADD COLUMN code VARCHAR(50) AFTER id,
	ADD INDEX (code)
;

UPDATE APPLICATION
SET code = CONCAT("PRiSM-AN-", LPAD(id, 10, "0"))
; 

ALTER TABLE PROJECT
	ADD COLUMN code VARCHAR(50) AFTER id,
	ADD INDEX (code)
;

UPDATE PROJECT
SET code = CONCAT("PRiSM-PT-", LPAD(id, 10, "0"))
;

ALTER TABLE PROGRAM
	MODIFY COLUMN code VARCHAR(50) AFTER id
;

UPDATE PROGRAM
SET code = CONCAT("PRiSM-PM-", LPAD(id, 10, "0")),
	is_imported = 0
WHERE is_imported = 0
	OR code = "0000005243-ABC"
;

UPDATE PROGRAM
SET require_project_definition = 0
WHERE require_project_definition IS NULL
;

ALTER TABLE PROGRAM
	MODIFY require_project_definition INT(1) UNSIGNED NOT NULL
;

ALTER TABLE INSTITUTION
	ADD COLUMN code VARCHAR(50) AFTER id,
	ADD INDEX (code)
;

UPDATE INSTITUTION
SET code = CONCAT("PRiSM-IN-", LPAD(id, 10, "0"))
;

ALTER TABLE SYSTEM
	ADD COLUMN code VARCHAR(50) AFTER id,
	ADD INDEX (code)
;

UPDATE SYSTEM
SET code = CONCAT("PRiSM-SM-", LPAD(id, 10, "0"))
;
