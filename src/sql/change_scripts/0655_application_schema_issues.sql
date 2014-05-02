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
	MODIFY COLUMN state_id VARCHAR(50) NOT NULL,
	MODIFY COLUMN due_date DATE NOT NULL
;

UPDATE PROJECT INNER JOIN ADVERT
	ON PROJECT.id = ADVERT.id
SET PROJECT.title = ADVERT.title,
	PROJECT.state_id = ADVERT.state_id,
	PROJECT.due_date = ADVERT.due_date
;

ALTER TABLE PROJECT
	MODIFY COLUMN state_id VARCHAR(50) NOT NULL,
	MODIFY COLUMN due_date DATE NOT NULL
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

INSERT INTO ROLE (id)
VALUES ("PROGRAM_APPLICATION_CREATOR"),
	("PROJECT_APPLICATION_CREATOR"),
	("SYSTEM_APPLICATION_CREATOR"),
	("SYSTEM_PROGRAM_CREATOR")
;

/* Fix uniqueness constraints on imported data tables */

/* Fix suggested supervisor table */

/* Transform comments */