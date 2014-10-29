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

ALTER TABLE ADVERT
	ADD COLUMN enabled INT(1) UNSIGNED NOT NULL DEFAULT 1,
	ADD INDEX (enabled),
	ADD COLUMN registered_user_id INT(10) UNSIGNED,
	ADD INDEX (registered_user_id)
;

UPDATE ADVERT INNER JOIN PROGRAM
	ON ADVERT.id = PROGRAM.advert_id
SET ADVERT.title = PROGRAM.title,
	ADVERT.enabled = PROGRAM.enabled
;

ALTER TABLE PROGRAM
	DROP COLUMN enabled
;

UPDATE ADVERT INNER JOIN PROJECT
	ON ADVERT.id = PROJECT.advert_id
SET ADVERT.enabled = 
	IF(PROJECT.disabled = 0,
		1,
		0),
	ADVERT.registered_user_id = PROJECT.primary_supervisor_id
;

ALTER TABLE PROJECT
	DROP COLUMN disabled,
	DROP FOREIGN KEY project_author_fk,
	DROP COLUMN author_id
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

UPDATE ADVERT
SET registered_user_id = 15
WHERE registered_user_id IS NULL
;

ALTER TABLE ADVERT
	ADD COLUMN program_id INT(10) UNSIGNED,
	MODIFY title VARCHAR(255) NOT NULL,
	MODIFY description VARCHAR(3000) NOT NULL DEFAULT "Programme advert coming soon!",
	MODIFY active INT(1) UNSIGNED NOT NULL DEFAULT 1,
	MODIFY enabled INT(1) UNSIGNED NOT NULL DEFAULT 1,
	MODIFY study_duration INT(4) UNSIGNED NOT NULL,
	MODIFY registered_user_id INT(10) UNSIGNED NOT NULL,
	ADD FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER (id)
;

INSERT INTO ADVERT (title, program_id, study_duration, registered_user_id)
	SELECT PROGRAM.title, PROGRAM.id, PROGRAM_TYPE.default_study_duration,
		IF(CONTACT.registered_user_id IS NOT NULL,
			CONTACT.registered_user_id,
			15)
	FROM PROGRAM LEFT JOIN (
		SELECT MIN(administrator_id) as registered_user_id,
			program_id AS program_id
		FROM PROGRAM_ADMINISTRATOR_LINK
		GROUP BY program_id) AS CONTACT
		ON PROGRAM.id = CONTACT.program_id
	INNER JOIN PROGRAM_TYPE
		ON PROGRAM.program_type_id = PROGRAM_TYPE.id
	WHERE PROGRAM.advert_id IS NULL
;

UPDATE PROGRAM INNER JOIN ADVERT
	ON PROGRAM.id = ADVERT.program_id
SET PROGRAM.advert_id = ADVERT.id
WHERE PROGRAM.id = ADVERT.program_id
;

ALTER TABLE ADVERT
	DROP COLUMN program_id
;

SET foreign_key_checks = 0
;

UPDATE APPLICATION_FORM INNER JOIN PROJECT
	ON APPLICATION_FORM.project_id = PROJECT.id
SET APPLICATION_FORM.project_id = PROJECT.advert_id
;

UPDATE APPLICATION_FORM INNER JOIN PROGRAM
	ON APPLICATION_FORM.program_id = PROGRAM.id
SET APPLICATION_FORM.program_id = PROGRAM.advert_id
;

UPDATE PENDING_ROLE_NOTIFICATION INNER JOIN PROGRAM
	ON PENDING_ROLE_NOTIFICATION.program_id = PROGRAM.id
SET PENDING_ROLE_NOTIFICATION.program_id = PROGRAM.advert_id
;

UPDATE PROGRAM_ADMINISTRATOR_LINK INNER JOIN PROGRAM
	ON PROGRAM_ADMINISTRATOR_LINK.program_id = PROGRAM.id
SET PROGRAM_ADMINISTRATOR_LINK.program_id = PROGRAM.advert_id
;

UPDATE PROGRAM_APPROVER_LINK INNER JOIN PROGRAM
	ON PROGRAM_APPROVER_LINK.program_id = PROGRAM.id
SET PROGRAM_APPROVER_LINK.program_id = PROGRAM.advert_id
;

UPDATE PROGRAM_VIEWER_LINK INNER JOIN PROGRAM
	ON PROGRAM_VIEWER_LINK.program_id = PROGRAM.id
SET PROGRAM_VIEWER_LINK.program_id = PROGRAM.advert_id
;

UPDATE PROGRAM_INSTANCE INNER JOIN PROGRAM
	ON PROGRAM_INSTANCE.program_id = PROGRAM.id
SET PROGRAM_INSTANCE.program_id = PROGRAM.advert_id
;

UPDATE PROJECT INNER JOIN PROGRAM
	ON PROJECT.program_id = PROGRAM.id
SET PROJECT.program_id = PROGRAM.advert_id
;

UPDATE SCORING_DEFINITION INNER JOIN PROGRAM
	ON SCORING_DEFINITION.program_id = PROGRAM.id
SET SCORING_DEFINITION.program_id = PROGRAM.advert_id
;

UPDATE PROGRAM
SET id = advert_id
;

UPDATE PROJECT
SET id = advert_id
;

ALTER TABLE PROGRAM
	DROP FOREIGN KEY program_advert_fk,
	DROP COLUMN advert_id,
	MODIFY id INT(10) UNSIGNED NOT NULL,
	ADD FOREIGN KEY (id) REFERENCES ADVERT (id)
;

ALTER TABLE PROJECT
	DROP FOREIGN KEY project_advert_fk,
	DROP COLUMN advert_id,
	MODIFY id INT(10) UNSIGNED NOT NULL,
	ADD FOREIGN KEY (id) REFERENCES ADVERT (id)
;

SET foreign_key_checks = 1
;
