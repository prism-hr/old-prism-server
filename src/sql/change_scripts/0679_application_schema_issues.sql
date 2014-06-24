/* Refactor foreign key in institution domicile */

ALTER TABLE INSTITUTION
	DROP FOREIGN KEY institution_ibfk_4,
	CHANGE COLUMN institution_domicile_id old_domicile_id INTEGER(10) UNSIGNED NOT NULL,
	ADD COLUMN institution_domicile_id VARCHAR(10) AFTER old_domicile_id,
	ADD INDEX (institution_domicile_id)
;

UPDATE INSTITUTION INNER JOIN INSTITUTION_DOMICILE
	ON INSTITUTION.old_domicile_id = INSTITUTION_DOMICILE.id
SET INSTITUTION.institution_domicile_id = INSTITUTION_DOMICILE.code
;

ALTER TABLE INSTITUTION_DOMICILE
	DROP PRIMARY KEY,
	DROP COLUMN id,
	CHANGE COLUMN code id VARCHAR(10) NOT NULL,
	ADD PRIMARY KEY (id)
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN institution_domicile_id VARCHAR(10) NOT NULL,
	ADD FOREIGN KEY (institution_domicile_id) REFERENCES INSTITUTION_DOMICILE (id),
	DROP COLUMN old_domicile_id
;

/* Make workflow configuration modifiable */

ALTER TABLE STATE_ACTION
	ADD COLUMN enabled INT(1) UNSIGNED NOT NULL DEFAULT 1,
	ADD INDEX (enabled)
;

ALTER TABLE STATE_ACTION
	MODIFY COLUMN enabled INT(1) UNSIGNED NOT NULL
;
