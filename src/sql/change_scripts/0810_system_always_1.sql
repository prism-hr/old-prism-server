SET FOREIGN_KEY_CHECKS = 0
;

ALTER TABLE SYSTEM
	MODIFY COLUMN id INT(10) UNSIGNED NOT NULL
;

SET FOREIGN_KEY_CHECKS = 1
;