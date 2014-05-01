ALTER TABLE ADVERT
	ADD COLUMN program_id INT(10) UNSIGNED AFTER id,
	ADD INDEX (program_id),
	ADD FOREIGN KEY (program_id) REFERENCES ADVERT (id)
;

UPDATE ADVERT INNER JOIN PROJECT
	ON ADVERT.id = PROJECT.id
SET ADVERT.program_id = PROJECT.program_id
;

UPDATE ADVERT
SET program_id = id
WHERE program_id IS NULL
;

ALTER TABLE APPLICATION
	DROP FOREIGN KEY prog_app_fk,
	DROP FOREIGN KEY project_fk,
	DROP COLUMN program_id,
	DROP COLUMN project_id
;

ALTER TABLE USER_ROLE
	DROP FOREIGN KEY user_role_ibfk_3,
	DROP FOREIGN KEY user_role_ibfk_4
;

UPDATE USER_ROLE
SET project_id = program_id
WHERE program_id IS NOT NULL
;

ALTER TABLE USER_ROLE
	DROP COLUMN program_id,
	DROP INDEX program_id,
	CHANGE COLUMN project_id advert_id INT(10) UNSIGNED,
	ADD FOREIGN KEY (advert_id) REFERENCES ADVERT (id)
;

/* Fix uniqueness constraints on imported data tables */

/* Fix suggested supervisor table */

/* Transform comments */