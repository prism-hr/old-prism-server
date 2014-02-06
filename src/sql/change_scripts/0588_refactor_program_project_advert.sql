CREATE TABLE program_type (
	id VARCHAR(50) NOT NULL,
	default_study_duration INT(4) UNSIGNED NOT NULL,
	PRIMARY KEY (id)) ENGINE = INNODB
	SELECT "MRES" AS id, 12 AS default_study_duration
		UNION
	SELECT "MSC" AS id, 12 AS default_study_duration
		UNION
	SELECT "RESEARCH_DEGREE" AS id, 48 AS default_study_duration
		UNION
	SELECT "ENGINEERING_DOCTORATE" AS id, 48 AS default_study_duration
		UNION
	SELECT "INTERNSHIP" AS id, 3 AS default_study_duration
		UNION
	SELECT "VISITING_RESEARCH" AS id, 12 AS default_study_duration
;

ALTER TABLE PROGRAM
	ADD COLUMN program_type_id VARCHAR(50),
	ADD INDEX (program_type_id),
	ADD FOREIGN KEY (program_type_id) REFERENCES PROGRAM_TYPE (id)
;

UPDATE PROGRAM
SET program_type_id = "MRES"
WHERE title LIKE "MRes%"
;

UPDATE PROGRAM
SET program_type_id = "ENGINEERING_DOCTORATE"
WHERE title LIKE "EngD%"
;

UPDATE PROGRAM
SET program_type_id = "RESEARCH_DEGREE"
WHERE title LIKE "Research Degree%"
;

UPDATE PROGRAM
SET program_type_id = "VISITING_RESEARCH"
WHERE title LIKE "Visiting Research%"
;

UPDATE PROGRAM
SET program_type_id = "RESEARCH_DEGREE"
WHERE program_type_id IS NULL
;

ALTER TABLE PROGRAM
	MODIFY program_type_id VARCHAR(50) NOT NULL
;

UPDATE ADVERT INNER JOIN PROGRAM
	ON ADVERT.id = PROGRAM.advert_id
SET ADVERT.title = PROGRAM.title
;

ALTER TABLE ADVERT
	ADD COLUMN enabled INT(1) UNSIGNED NOT NULL DEFAULT 1,
	ADD INDEX (enabled),
	ADD COLUMN registered_user_id INT(10) UNSIGNED,
	ADD INDEX (registered_user_id),
	ADD FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER (id)
;

UPDATE ADVERT INNER JOIN PROGRAM
	ON ADVERT.id = PROGRAM.advert_id
SET ADVERT.enabled = PROGRAM.enabled
;

ALTER TABLE PROGRAM
	DROP COLUMN enabled
;

UPDATE ADVERT INNER JOIN PROJECT
	ON ADVERT.id = PROJECT.advert_id
SET ADVERT.enabled = 
	IF(PROJECT.disabled = 0,
		1,
		0)
;

ALTER TABLE PROJECT
	DROP COLUMN disabled
;

UPDATE ADVERT INNER JOIN PROJECT
	ON ADVERT.id = PROJECT.advert_id
SET ADVERT.registered_user_id = PROJECT.administrator_id
;

ALTER TABLE PROJECT
	DROP FOREIGN KEY project_administrator_registered_user_fk,
	DROP COLUMN administrator_id
;

UPDATE ADVERT INNER JOIN PROGRAM
	ON ADVERT.id = PROGRAM.advert_id
INNER JOIN (
	SELECT MIN(administrator_id) as registered_user_id,
		program_id AS program_id
	FROM PROGRAM_ADMINISTRATOR_LINK
	GROUP BY program_id) AS CONTACT
	ON PROGRAM.id = CONTACT.program_id
SET ADVERT.registered_user_id = CONTACT.registered_user_id
;

ALTER TABLE ADVERT
	MODIFY title VARCHAR(255) NOT NULL,
	MODIFY description VARCHAR(3000) NOT NULL DEFAULT "Programme advert coming soon!",
	MODIFY active INT(1) UNSIGNED NOT NULL DEFAULT 1,
	MODIFY enabled INT(1) UNSIGNED NOT NULL DEFAULT 1,
	MODIFY study_duration INT(4) UNSIGNED NOT NULL
;
