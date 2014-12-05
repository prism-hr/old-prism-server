ALTER TABLE APPLICATION
	ADD COLUMN code_legacy VARCHAR(50) AFTER code,
	ADD INDEX (code_legacy, sequence_identifier)
;
