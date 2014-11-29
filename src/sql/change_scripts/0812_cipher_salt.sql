ALTER TABLE SYSTEM
	ADD COLUMN cipher_salt VARCHAR(36) AFTER last_notified_update_syndicated
;

UPDATE SYSTEM
SET cipher_salt = UUID()
;

ALTER TABLE SYSTEM
	MODIFY COLUMN cipher_salt VARCHAR(36) NOT NULL
;
