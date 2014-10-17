ALTER TABLE INSTITUTION
	ADD COLUMN default_program_type VARCHAR(50) NOT NULL DEFAULT "STUDY_POSTGRADUATE_RESEARCH" AFTER homepage,
	ADD COLUMN default_study_option VARCHAR(50) NOT NULL DEFAULT "FULL_TIME" AFTER default_program_type
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN default_program_type VARCHAR(50) NOT NULL,
	MODIFY COLUMN default_study_option VARCHAR(50) NOT NULL
;

ALTER TABLE COMMENT
	MODIFY COLUMN application_rejection_reason_id INT(10) UNSIGNED AFTER application_recruiter_accept_appointment
;

UPDATE IMPORTED_ENTITY
SET enabled = 1
WHERE code IN ("UNQUALIFIED_FOR_OPPORTUNITY", "UNQUALIFIED_FOR_INSTITUTION")
;

DELETE FROM IMPORTED_ENTITY
WHERE code IN ("UNQUALIFIED_FOR_OPPORTUNTIY", "UNQUALIFIED_FOR_INSTITUTION(")
;

UPDATE IMPORTED_ENTITY
SET code = "STUDY_UNDERGRADUATE"
WHERE code = "UNDERGRADUATE_STUDY"
;

UPDATE IMPORTED_ENTITY
SET code = "STUDY_POSTGRADUATE_TAUGHT"
WHERE code = "POSTGRADUATE_STUDY"
;

UPDATE IMPORTED_ENTITY
SET code = "STUDY_POSTGRADUATE_RESEARCH"
WHERE code = "POSTGRADUATE_RESEARCH"
;

UPDATE IMPORTED_ENTITY
SET code = "WORK_EXPERIENCE"
WHERE code = "INTERNSHIP"
;

UPDATE IMPORTED_ENTITY
SET code = "EMPLOYMENT_SECONDMENT"
WHERE code = "SECONDMENT"
;

UPDATE IMPORTED_ENTITY
SET code = "TRAINING"
WHERE code = "CONTINUING_PROFESSIONAL_DEVELOPMENT"
;

ALTER TABLE IMPORTED_ENTITY_FEED
	CHANGE COLUMN last_imported_date last_imported_timestamp DATETIME
;

UPDATE PROGRAM
SET program_type_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE code = "STUDY_POSTGRADUATE_RESEARCH")
WHERE program_type_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE code = "STUDY_POSTGRADUATE_TAUGHT")
;

ALTER TABLE APPLICATION
	ADD COLUMN completion_date DATE AFTER application_rating_average,
	ADD INDEX (completion_date, sequence_identifier)
;

UPDATE APPLICATION INNER JOIN (
	SELECT application_id AS application_id,
		MAX(DATE(created_timestamp)) AS completion_date
	FROM COMMENT
	WHERE action_id IN ("APPLICATION_CONFIRM_OFFER_RECOMMENDATION", 
		"APPLICATION_CONFIRM_REJECTION", "APPLICATION_WITHDRAW")
	GROUP BY application_id) AS OUTCOME
	ON APPLICATION.id = OUTCOME.application_id
SET APPLICATION.completion_date = OUTCOME.completion_date
;

DROP TABLE ADVERT_RECRUITMENT_PREFERENCE
;

DROP TABLE ADVERT_CATEGORY
;

CREATE TABLE PROGRAM_RELATION (
	program_id INT(10) UNSIGNED NOT NULL,
	program_relation_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (program_id, program_relation_id),
	INDEX (program_relation_id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (program_relation_id) REFERENCES PROGRAM (id)
) ENGINE = INNODB
;

CREATE TABLE ADVERT_TARGET (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	advert_id INT(10) UNSIGNED NOT NULL,
	institution_id INT(10) UNSIGNED,
	program_type VARCHAR(50),
	PRIMARY KEY (id),
	UNIQUE INDEX (advert_id, institution_id, program_type),
	INDEX (institution_id),
	INDEX (program_type),
	FOREIGN KEY (advert_id) REFERENCES ADVERT (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id)
) ENGINE = INNODB
;

CREATE TABLE ADVERT_KEYWORD (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	advert_id INT(10) UNSIGNED NOT NULL,
	keyword VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (advert_id, keyword),
	INDEX (keyword),
	FOREIGN KEY (advert_id) REFERENCES ADVERT (id)
) ENGINE = INNODB
;

DROP TABLE APPLICATION_OTHER_PROJECT
;

CREATE TABLE APPLICATION_SECONDARY_PROJECT (
	application_id INT(10) UNSIGNED NOT NULL,
	secondary_project_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (application_id, secondary_project_id),
	INDEX (secondary_project_id),
	FOREIGN KEY (application_id) REFERENCES APPLICATION (id),
	FOREIGN KEY (secondary_project_id) REFERENCES PROJECT (id)
) ENGINE = INNODB
;

CREATE PROCEDURE SP_CLONE_ADVERT_ADDRESS ()
BEGIN

	SET @next = (
		SELECT MIN(id)
		FROM ADVERT);
		
	SET @last = (
		SELECT MAX(id)
		FROM ADVERT);
		
	 WHILE @next <= @last DO
	 	SELECT INSTITUTION_ADDRESS.institution_domicile_id, INSTITUTION_ADDRESS.institution_domicile_region_id, 
			INSTITUTION_ADDRESS.address_line_1, INSTITUTION_ADDRESS.address_line_2, INSTITUTION_ADDRESS.address_town, 
			INSTITUTION_ADDRESS.address_district, INSTITUTION_ADDRESS.address_code
		INTO @domicile, @region, @line1, @line2, @town, @district, @code
		FROM ADVERT INNER JOIN INSTITUTION_ADDRESS
			ON ADVERT.institution_address_id = INSTITUTION_ADDRESS.id
		WHERE ADVERT.id = @next;
	 	
	 	INSERT INTO INSTITUTION_ADDRESS(institution_domicile_id, institution_domicile_region_id, address_line_1,
			address_line_2, address_town, address_district, address_code)
		VALUES(@domicile, @region, @line1, @line2, @town, @district, @code);
		
		UPDATE ADVERT
		SET institution_address_id = LAST_INSERT_ID()
		WHERE id = @next;
	 
	 	SET @next = (
	 		@next + 1);
	 END WHILE;

END
;

CALL SP_CLONE_ADVERT_ADDRESS ()
;

DROP PROCEDURE SP_CLONE_ADVERT_ADDRESS
;

