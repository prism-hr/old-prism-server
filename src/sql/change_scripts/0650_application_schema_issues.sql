ALTER TABLE ROLE
	DROP COLUMN do_send_update_notification
;

INSERT INTO ROLE (id)
VALUES ("INSTITUTION_ADMINISTRATOR")
;

INSERT INTO USER_ROLE (institution_id, user_id, role_id, requesting_user_id)
VALUES (5243, 1024, "INSTITUTION_ADMINISTRATOR", 1024)
;

UPDATE USER_ROLE
SET requesting_user_id = 1024
WHERE system_id IS NOT NULL
;

ALTER TABLE USER
	DROP COLUMN is_system_user
;

DROP TABLE ROLE_TRANSITION
;

DROP TABLE STATE_TRANSITION
;

DROP TABLE STATE_TRANSITION_PROPAGATION
;

SET FOREIGN_KEY_CHECKS = 0
;

DELETE FROM STATE
WHERE id = "INSTITUTION_DISABLED"
;

SET FOREIGN_KEY_CHECKS = 1
;

ALTER TABLE PROGRAM
	DROP COLUMN locked
;

ALTER TABLE ADVERT
	ADD COLUMN due_date DATE,
	ADD INDEX (due_date)
;

UPDATE ADVERT INNER JOIN (
	SELECT PROGRAM_INSTANCE.program_id AS program_id, 
		MAX(PROGRAM_INSTANCE.disabled_date) AS due_date
	FROM PROGRAM_INSTANCE
	WHERE PROGRAM_INSTANCE.identifier = "CUSTOM"
	GROUP BY PROGRAM_INSTANCE.program_id) AS PROGRAM_DUE_DATE
	ON ADVERT.id = PROGRAM_DUE_DATE.program_id
SET ADVERT.due_date = PROGRAM_DUE_DATE.due_date
;

UPDATE ADVERT INNER JOIN PROJECT
	ON ADVERT.id = PROJECT.program_id
INNER JOIN ADVERT AS PROJECT_ADVERT
	ON PROJECT.id = PROJECT_ADVERT.id
SET PROJECT_ADVERT.due_date = ADVERT.due_date
;

UPDATE ADVERT INNER JOIN ADVERT_CLOSING_DATE
	ON ADVERT.advert_closing_date_id = ADVERT_CLOSING_DATE.id
SET ADVERT.due_date = ADVERT_CLOSING_DATE.closing_date
WHERE ADVERT.advert_type = "PROJECT"
	AND ADVERT.due_date IS NULL
	OR ADVERT.due_date > ADVERT_CLOSING_DATE.closing_date
;

UPDATE ADVERT
SET due_date = NULL
WHERE state_id LIKE "%DISABLED_COMPLETED"
;

DELETE FROM SYSTEM_CONFIGURATION
WHERE id = "APPLICATION_PROCESSING_BUFFER_DURATION"
;

UPDATE SYSTEM_CONFIGURATION
SET id = "ACTION_EXPIRY_DURATION"
WHERE id = "APPLICATION_ACTION_EXPIRY_DURATION"
;

DELETE 
FROM ROLE
WHERE id = "PROGRAM_PRACTITIONER"
;

UPDATE ADVERT INNER JOIN (
	SELECT program_id AS program_id,
		IF(MAX(deadline) < CONCAT(YEAR(MAX(deadline)), "-11-08"),
			CONCAT(YEAR(MAX(deadline)), "-11-08"),
			CONCAT(YEAR(MAX(deadline)) + 1, "-11-08")) AS due_date
	FROM PROGRAM_INSTANCE
	GROUP BY program_id)
	AS PROGRAM_DUE_DATE
	ON ADVERT.id = PROGRAM_DUE_DATE.program_id
SET ADVERT.due_date = PROGRAM_DUE_DATE.due_date
;

ALTER TABLE PROGRAM_INSTANCE
	CHANGE COLUMN IDENTIFIER identifier VARCHAR(10)
;

UPDATE PROGRAM_INSTANCE
SET disabled_date = NULL
WHERE identifier = "CUSTOM"
;

UPDATE APPLICATION
SET due_date = DATE(created_timestamp) + INTERVAL 2419200 SECOND
WHERE due_date IS NULL
;
