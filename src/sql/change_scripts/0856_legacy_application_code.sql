ALTER TABLE APPLICATION
	ADD COLUMN legacy_code VARCHAR(50) AFTER code,
	ADD INDEX (legacy_code, sequence_identifier)
;
