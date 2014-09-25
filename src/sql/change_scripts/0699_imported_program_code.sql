ALTER TABLE PROGRAM
	ADD COLUMN imported_code VARCHAR(50) AFTER code,
	ADD INDEX (imported_code),
	DROP COLUMN is_imported
;

UPDATE PROGRAM
SET imported_code = REPLACE(code, "0000005243-", ""),
	code = CONCAT("PRiSM-PR-", LPAD(id, 10, "0"))
WHERE code LIKE "0000005243-%"
;
