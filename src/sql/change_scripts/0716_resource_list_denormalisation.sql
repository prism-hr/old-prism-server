ALTER TABLE APPLICATION
	ADD COLUMN sequence_identifier VARCHAR (20),
	ADD INDEX (sequence_identifier)
;

ALTER TABLE PROJECT
	ADD COLUMN sequence_identifier VARCHAR (20),
	ADD INDEX (sequence_identifier)
;
	
ALTER TABLE PROGRAM
	ADD COLUMN sequence_identifier VARCHAR (20),
	ADD INDEX (sequence_identifier)	
;

ALTER TABLE INSTITUTION
	ADD COLUMN sequence_identifier VARCHAR (20),
	ADD INDEX (sequence_identifier)
;

ALTER TABLE SYSTEM
	ADD COLUMN sequence_identifier VARCHAR (20),
	ADD INDEX (sequence_identifier)
;

CREATE PROCEDURE SP_GENERATE_APPLICATION_SEQUENCE ()
BEGIN

	DECLARE current DATE;
	DECLARE maximum DATE;

	CREATE TEMPORARY TABLE APPLICATION_DATE_INDEX (
		date_index DATE NOT NULL,
		PRIMARY KEY (date_index)
	) ENGINE = MEMORY
		SELECT DISTINCT DATE(updated_timestamp) AS date_index
		FROM APPLICATION
		ORDER BY updated_timestamp;
			
	SET current = (
		SELECT MIN(date_index)
		FROM APPLICATION_DATE_INDEX);
		
	SET maximum = (
		SELECT MAX(date_index)
		FROM APPLICATION_DATE_INDEX);
		
	WHILE current IS NOT NULL DO
		SET @index = 0;

		UPDATE APPLICATION
		SET sequence_identifier = CONCAT(DATE_FORMAT(current, "%Y%m%d"), "-", LPAD(@index := @index + 1, 10, "0"))
		WHERE DATE(updated_timestamp) = current
		ORDER BY updated_timestamp, id;
			
		SET current = (
			SELECT MIN(date_index)
			FROM APPLICATION_DATE_INDEX
			WHERE date_index > current);
	END WHILE;
	
	DROP TABLE APPLICATION_DATE_INDEX;

END
;

CALL SP_GENERATE_APPLICATION_SEQUENCE ()
;

DROP PROCEDURE SP_GENERATE_APPLICATION_SEQUENCE
;

CREATE PROCEDURE SP_GENERATE_PROJECT_SEQUENCE ()
BEGIN

	DECLARE current DATE;
	DECLARE maximum DATE;

	CREATE TEMPORARY TABLE PROJECT_DATE_INDEX (
		date_index DATE NOT NULL,
		PRIMARY KEY (date_index)
	) ENGINE = MEMORY
		SELECT DISTINCT DATE(updated_timestamp) AS date_index
		FROM PROJECT
		ORDER BY updated_timestamp;
			
	SET current = (
		SELECT MIN(date_index)
		FROM PROJECT_DATE_INDEX);
		
	SET maximum = (
		SELECT MAX(date_index)
		FROM PROJECT_DATE_INDEX);
		
	WHILE current IS NOT NULL DO
		SET @index = 0;

		UPDATE PROJECT
		SET sequence_identifier = CONCAT(DATE_FORMAT(current, "%Y%m%d"), "-", LPAD(@index := @index + 1, 10, "0"))
		WHERE DATE(updated_timestamp) = current
		ORDER BY updated_timestamp, id;
			
		SET current = (
			SELECT MIN(date_index)
			FROM PROJECT_DATE_INDEX
			WHERE date_index > current);
	END WHILE;
	
	DROP TABLE PROJECT_DATE_INDEX;

END
;

CALL SP_GENERATE_PROJECT_SEQUENCE ()
;

DROP PROCEDURE SP_GENERATE_PROJECT_SEQUENCE
;

CREATE PROCEDURE SP_GENERATE_PROGRAM_SEQUENCE ()
BEGIN

	DECLARE current DATE;
	DECLARE maximum DATE;

	CREATE TEMPORARY TABLE PROGRAM_DATE_INDEX (
		date_index DATE NOT NULL,
		PRIMARY KEY (date_index)
	) ENGINE = MEMORY
		SELECT DISTINCT DATE(updated_timestamp) AS date_index
		FROM PROGRAM
		ORDER BY updated_timestamp;
			
	SET current = (
		SELECT MIN(date_index)
		FROM PROGRAM_DATE_INDEX);
		
	SET maximum = (
		SELECT MAX(date_index)
		FROM PROGRAM_DATE_INDEX);
		
	WHILE current IS NOT NULL DO
		SET @index = 0;

		UPDATE PROGRAM
		SET sequence_identifier = CONCAT(DATE_FORMAT(current, "%Y%m%d"), "-", LPAD(@index := @index + 1, 10, "0"))
		WHERE DATE(updated_timestamp) = current
		ORDER BY updated_timestamp, id;
			
		SET current = (
			SELECT MIN(date_index)
			FROM PROGRAM_DATE_INDEX
			WHERE date_index > current);
	END WHILE;
	
	DROP TABLE PROGRAM_DATE_INDEX;

END
;

CALL SP_GENERATE_PROGRAM_SEQUENCE ()
;

DROP PROCEDURE SP_GENERATE_PROGRAM_SEQUENCE
;

CREATE PROCEDURE SP_GENERATE_INSTITUTION_SEQUENCE ()
BEGIN

	DECLARE current DATE;
	DECLARE maximum DATE;

	CREATE TEMPORARY TABLE INSTITUTION_DATE_INDEX (
		date_index DATE NOT NULL,
		PRIMARY KEY (date_index)
	) ENGINE = MEMORY
		SELECT DISTINCT DATE(updated_timestamp) AS date_index
		FROM INSTITUTION
		ORDER BY updated_timestamp;
			
	SET current = (
		SELECT MIN(date_index)
		FROM INSTITUTION_DATE_INDEX);
		
	SET maximum = (
		SELECT MAX(date_index)
		FROM INSTITUTION_DATE_INDEX);
		
	WHILE current IS NOT NULL DO
		SET @index = 0;

		UPDATE INSTITUTION
		SET sequence_identifier = CONCAT(DATE_FORMAT(current, "%Y%m%d"), "-", LPAD(@index := @index + 1, 10, "0"))
		WHERE DATE(updated_timestamp) = current
		ORDER BY updated_timestamp, id;
			
		SET current = (
			SELECT MIN(date_index)
			FROM INSTITUTION_DATE_INDEX
			WHERE date_index > current);
	END WHILE;
	
	DROP TABLE INSTITUTION_DATE_INDEX;

END
;

CALL SP_GENERATE_INSTITUTION_SEQUENCE ()
;

DROP PROCEDURE SP_GENERATE_INSTITUTION_SEQUENCE
;

CREATE PROCEDURE SP_GENERATE_SYSTEM_SEQUENCE ()
BEGIN

	DECLARE current DATE;
	DECLARE maximum DATE;

	CREATE TEMPORARY TABLE SYSTEM_DATE_INDEX (
		date_index DATE NOT NULL,
		PRIMARY KEY (date_index)
	) ENGINE = MEMORY
		SELECT DISTINCT DATE(updated_timestamp) AS date_index
		FROM SYSTEM
		ORDER BY updated_timestamp;
			
	SET current = (
		SELECT MIN(date_index)
		FROM SYSTEM_DATE_INDEX);
		
	SET maximum = (
		SELECT MAX(date_index)
		FROM SYSTEM_DATE_INDEX);
		
	WHILE current IS NOT NULL DO
		SET @index = 0;

		UPDATE SYSTEM
		SET sequence_identifier = CONCAT(DATE_FORMAT(current, "%Y%m%d"), "-", LPAD(@index := @index + 1, 10, "0"))
		WHERE DATE(updated_timestamp) = current
		ORDER BY updated_timestamp, id;
			
		SET current = (
			SELECT MIN(date_index)
			FROM SYSTEM_DATE_INDEX
			WHERE date_index > current);
	END WHILE;
	
	DROP TABLE SYSTEM_DATE_INDEX;

END
;

CALL SP_GENERATE_SYSTEM_SEQUENCE ()
;

DROP PROCEDURE SP_GENERATE_SYSTEM_SEQUENCE
;
