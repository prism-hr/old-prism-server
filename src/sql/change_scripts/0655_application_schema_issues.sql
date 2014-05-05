ALTER TABLE APPLICATION
	DROP FOREIGN KEY advert_fk,
	DROP COLUMN advert_id
;

UPDATE PROGRAM INNER JOIN ADVERT
	ON PROGRAM.id = ADVERT.id
SET PROGRAM.title = ADVERT.title
WHERE PROGRAM.title IS NULL
;

ALTER TABLE PROGRAM
	MODIFY COLUMN title VARCHAR(255) NOT NULL,
	CHANGE COLUMN atas_required require_project_definition INT(1) UNSIGNED,
	ADD COLUMN state_id VARCHAR(50),
	ADD COLUMN due_date DATE,
	ADD INDEX (state_id),
	ADD FOREIGN KEY (state_id) REFERENCES STATE (id)
;

ALTER TABLE PROJECT
	ADD COLUMN title VARCHAR(255),
	ADD COLUMN state_id VARCHAR(50),
	ADD COLUMN due_date DATE,
	ADD INDEX (state_id),
	ADD FOREIGN KEY (state_id) REFERENCES STATE (id)
;

UPDATE PROGRAM INNER JOIN ADVERT
	ON PROGRAM.id = ADVERT.id
SET PROGRAM.state_id = ADVERT.state_id,
	PROGRAM.due_date = ADVERT.due_date
;

ALTER TABLE PROGRAM
	MODIFY COLUMN state_id VARCHAR(50) NOT NULL
;

UPDATE PROJECT INNER JOIN ADVERT
	ON PROJECT.id = ADVERT.id
SET PROJECT.title = ADVERT.title,
	PROJECT.state_id = ADVERT.state_id,
	PROJECT.due_date = ADVERT.due_date
;

ALTER TABLE PROJECT
	MODIFY COLUMN state_id VARCHAR(50) NOT NULL
;

ALTER TABLE ADVERT
	DROP FOREIGN KEY advert_ibfk_3,
	DROP COLUMN state_id,
	DROP COLUMN due_date
;

ALTER TABLE PROGRAM
	ADD COLUMN previous_state_id VARCHAR(50) AFTER state_id,
	ADD INDEX (previous_state_id),
	ADD FOREIGN KEY (previous_state_id) REFERENCES STATE (id)
;

ALTER TABLE PROJECT
	ADD COLUMN previous_state_id VARCHAR(50) AFTER state_id,
	ADD INDEX (previous_state_id),
	ADD FOREIGN KEY (previous_state_id) REFERENCES STATE (id)
;

ALTER TABLE APPLICATION
	ADD COLUMN previous_state_id VARCHAR(50) AFTER state_id,
	ADD INDEX (previous_state_id),
	ADD FOREIGN KEY (previous_state_id) REFERENCES STATE (id)
;

UPDATE PROGRAM
SET previous_state_id = "PROGRAM_APPROVED"
WHERE state_id != "PROGRAM_APPROVED"
;

UPDATE PROGRAM
SET previous_state_id = "PROGRAM_APPROVAL"
WHERE state_id = "PROGRAM_APPROVED"
AND program_import_id IS NULL
;

UPDATE PROJECT
SET previous_state_id = "PROJECT_APPROVED"
WHERE state_id != "PROJECT_APPROVED"
;

UPDATE APPLICATION
SET previous_state_id = "APPLICATION_UNSUBMITTED"
WHERE state_id = "APPLICATION_WITHDRAWN_COMPLETED"
AND submitted_timestamp IS NULL
;

ALTER TABLE USER_ROLE
	MODIFY COLUMN assigned_timestamp DATETIME NOT NULL
;

ALTER TABLE USER 
	DROP FOREIGN KEY user_ibfk_2,
	DROP COLUMN action_id,
	DROP FOREIGN KEY user_ibfk_4,
	DROP COLUMN application_id
;

INSERT INTO USER_ROLE (program_id, user_id, role_id, requesting_user_id, assigned_timestamp)
	SELECT PROGRAM.id, USER.id, "PROGRAM_APPLICATION_CREATOR", ADVERT.user_id, "2012-10-01 00:00:00"
	FROM USER INNER JOIN PROGRAM
		ON USER.advert_id = PROGRAM.id
	INNER JOIN ADVERT
		ON PROGRAM.id = ADVERT.id
	LEFT JOIN APPLICATION
		ON PROGRAM.id = APPLICATION.program_id
		AND USER.id = APPLICATION.user_id
	WHERE APPLICATION.id IS NULL
;

INSERT INTO USER_ROLE (project_id, user_id, role_id, requesting_user_id, assigned_timestamp)
	SELECT PROJECT.id, USER.id, "PROJECT_APPLICATION_CREATOR", ADVERT.user_id, "2012-10-01 00:00:00"
	FROM USER INNER JOIN PROJECT
		ON USER.advert_id = PROJECT.id
	INNER JOIN ADVERT
		ON PROJECT.id = ADVERT.id
	LEFT JOIN APPLICATION
		ON PROJECT.id = APPLICATION.program_id
		AND USER.id = APPLICATION.user_id
	WHERE APPLICATION.id IS NULL
;

ALTER TABLE USER 
	DROP FOREIGN KEY user_ibfk_3,
	DROP COLUMN advert_id
;

INSERT INTO ROLE_INHERITANCE (role_id, inherited_role_id)
	SELECT ROLE.id, INHERITED_ROLE.id
	FROM ROLE INNER JOIN ROLE AS INHERITED_ROLE
	WHERE ROLE.id NOT IN ("APPLICATION_INTERVIEWEE", "APPLICATION_POTENTIAL_INTERVIEWEE",
		"APPLICATION_SUGGESTED_SUPERVISOR", "PROGRAM_APPLICATION_CREATOR", 
		"PROGRAM_PROJECT_CREATOR", "PROJECT_APPLICATION_CREATOR", "SYSTEM_APPLICATION_CREATOR",
		"SYSTEM_PROGRAM_CREATOR")
		AND INHERITED_ROLE.id IN ("SYSTEM_APPLICATION_CREATOR", "SYSTEM_PROGRAM_CREATOR")
;

INSERT INTO USER_ROLE (system_id, user_id, role_id, requesting_user_id, assigned_timestamp)
	SELECT 1, USER.id, ROLE.id, 1024, MIN(USER_ROLE.assigned_timestamp)
	FROM USER INNER JOIN USER_ROLE
		ON USER.id = USER_ROLE.user_id
	INNER JOIN ROLE
	WHERE USER.user_account_id IS NOT NULL
		AND ROLE.id IN ("SYSTEM_APPLICATION_CREATOR", "SYSTEM_PROGRAM_CREATOR")
	GROUP BY USER_ROLE.user_id
;

/* Fix uniqueness constraints on imported data tables */

/* Fix suggested supervisor table */

/* Transform comments */