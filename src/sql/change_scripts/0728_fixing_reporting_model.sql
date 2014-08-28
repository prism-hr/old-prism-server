UPDATE APPLICATION_PROCESSING_SUMMARY INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		APPLICATION_PROCESSING.state_group_id AS state_group_id,
		ROUND(AVG(APPLICATION_PROCESSING.instance_count), 2) AS instance_count_average,
		ROUND(AVG(APPLICATION_PROCESSING.day_duration_sum), 2) AS day_duration_sum_average
	FROM APPLICATION INNER JOIN APPLICATION_PROCESSING
		ON APPLICATION.id = APPLICATION_PROCESSING.application_id
	GROUP BY APPLICATION.institution_id, APPLICATION_PROCESSING.state_group_id) AS INSTITUTION_SUMMARY
ON APPLICATION_PROCESSING_SUMMARY.institution_id = INSTITUTION_SUMMARY.institution_id
	AND APPLICATION_PROCESSING_SUMMARY.state_group_id = INSTITUTION_SUMMARY.state_group_id
SET APPLICATION_PROCESSING_SUMMARY.instance_count_average = INSTITUTION_SUMMARY.instance_count_average,
	APPLICATION_PROCESSING_SUMMARY.day_duration_sum_average = INSTITUTION_SUMMARY.day_duration_sum_average
;

UPDATE APPLICATION_PROCESSING_SUMMARY INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		APPLICATION_PROCESSING.state_group_id AS state_group_id,
		ROUND(AVG(APPLICATION_PROCESSING.instance_count), 2) AS instance_count_average,
		ROUND(AVG(APPLICATION_PROCESSING.day_duration_sum), 2) AS day_duration_sum_average
	FROM APPLICATION INNER JOIN APPLICATION_PROCESSING
		ON APPLICATION.id = APPLICATION_PROCESSING.application_id
	GROUP BY APPLICATION.program_id, APPLICATION_PROCESSING.state_group_id) AS program_SUMMARY
ON APPLICATION_PROCESSING_SUMMARY.program_id = program_SUMMARY.program_id
	AND APPLICATION_PROCESSING_SUMMARY.state_group_id = program_SUMMARY.state_group_id
SET APPLICATION_PROCESSING_SUMMARY.instance_count_average = program_SUMMARY.instance_count_average,
	APPLICATION_PROCESSING_SUMMARY.day_duration_sum_average = program_SUMMARY.day_duration_sum_average
;

UPDATE APPLICATION_PROCESSING_SUMMARY INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		APPLICATION_PROCESSING.state_group_id AS state_group_id,
		ROUND(AVG(APPLICATION_PROCESSING.instance_count), 2) AS instance_count_average,
		ROUND(AVG(APPLICATION_PROCESSING.day_duration_sum), 2) AS day_duration_sum_average
	FROM APPLICATION INNER JOIN APPLICATION_PROCESSING
		ON APPLICATION.id = APPLICATION_PROCESSING.application_id
	GROUP BY APPLICATION.project_id, APPLICATION_PROCESSING.state_group_id) AS project_SUMMARY
ON APPLICATION_PROCESSING_SUMMARY.project_id = project_SUMMARY.project_id
	AND APPLICATION_PROCESSING_SUMMARY.state_group_id = project_SUMMARY.state_group_id
SET APPLICATION_PROCESSING_SUMMARY.instance_count_average = project_SUMMARY.instance_count_average,
	APPLICATION_PROCESSING_SUMMARY.day_duration_sum_average = project_SUMMARY.day_duration_sum_average
;

ALTER TABLE APPLICATION_PROCESSING_SUMMARY
	MODIFY COLUMN instance_count_average DECIMAL(10,2) NOT NULL,
	MODIFY COLUMN day_duration_sum_average DECIMAL(10,2) NOT NULL
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type, code, name, enabled)
VALUES (5243, "PROGRAM_TYPE", "UNDERGRADUATE_STUDY", "Undergraduate Study", 1),
	(5243, "PROGRAM_TYPE", "POSTGRADUATE_STUDY", "Postgraduate Study", 1),
	(5243, "PROGRAM_TYPE", "POSTGRADUATE_RESEARCH", "Postgraduate Research", 1),
	(5243, "PROGRAM_TYPE", "INTERNSHIP", "Internship", 1),
	(5243, "PROGRAM_TYPE", "SECONDMENT", "Secondment", 1),
	(5243, "PROGRAM_TYPE", "EMPLOYMENT", "Employment", 1),
	(5243, "PROGRAM_TYPE", "CONTINUING_PROFESSIONAL_DEVELOPMENT", "Continuing Professional Development", 1),
	(5243, "PROGRAM_TYPE", "UNCLASSIFIED", "Other", 1)
;

INSERT INTO IMPORTED_ENTITY_FEED (institution_id, imported_entity_type, location, last_imported_date)
VALUES(5243, "PROGRAM_TYPE", "xml/defaultEntities/programType.xml", "2014-08-20")
;

UPDATE IMPORTED_ENTITY
SET name = code,
	code = "MODULAR_FLEXIBLE",
WHERE imported_entity_type = "STUDY_OPTION"
	AND code = "B+++++"
;

UPDATE IMPORTED_ENTITY
SET name = code, 
	code = "FULL_TIME",
WHERE imported_entity_type = "STUDY_OPTION"
	AND code = "F+++++"
;

UPDATE IMPORTED_ENTITY
SET name = code, 
	code = "PART_TIME"
WHERE imported_entity_type = "STUDY_OPTION"
	AND code = "P+++++"
;

ALTER TABLE ADVERT
	ADD COLUMN summary VARCHAR(1000) AFTER title
;

CREATE TABLE PROGRAM_STUDY_OPTION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	program_id INT(10) UNSIGNED NOT NULL,
	study_option_id INT(10) UNSIGNED NOT NULL,
	enabled INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (program_id, study_option_id),
	INDEX (study_option_id),
	INDEX (enabled),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (study_option_id) REFERENCES IMPORTED_ENTITY (id)
) ENGINE = INNODB
;

ALTER TABLE PROGRAM
	ADD COLUMN program_type_id INT(10) UNSIGNED AFTER referrer,
	ADD INDEX (program_type_id, sequence_identifier),
	ADD FOREIGN KEY (program_type_id) REFERENCES IMPORTED_ENTITY (id)
;

UPDATE PROGRAM INNER JOIN IMPORTED_ENTITY
	ON PROGRAM.program_type = IMPORTED_ENTITY.code
	AND IMPORTED_ENTITY.imported_entity_type = "PROGRAM_TYPE"
SET PROGRAM.program_type_id = IMPORTED_ENTITY.id
;

ALTER TABLE PROGRAM
	DROP INDEX program_type,
	DROP COLUMN program_type,
	MODIFY COLUMN program_type_id INT(10) UNSIGNED NOT NULL
;

CREATE TABLE PROGRAM_STUDY_OPTION_INSTANCE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	program_study_option_id INT(10) UNSIGNED NOT NULL,
	application_deadline DATE NOT NULL,
	application_start_date DATE NOT NULL,
	academic_year VARCHAR(4) NOT NULL,
	sequence_identifier VARCHAR(10) NOT NULL,
	enabled INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (program_study_option_id, academic_year),
	FOREIGN KEY (program_study_option_id) REFERENCES PROGRAM_STUDY_OPTION (id)
) ENGINE = INNODB
;

INSERT INTO PROGRAM_STUDY_OPTION (id, program_id, study_option_id, enabled)
	SELECT NULL, program_id, study_option_id, MAX(enabled)
	FROM PROGRAM_INSTANCE
	GROUP BY program_id, study_option_id
;

INSERT INTO PROGRAM_STUDY_OPTION_INSTANCE (id, program_study_option_id, application_deadline, 
	application_start_date, academic_year, sequence_identifier, enabled)
	SELECT NULL, PROGRAM_STUDY_OPTION.id, PROGRAM_INSTANCE.deadline, PROGRAM_INSTANCE.start_date,
		PROGRAM_INSTANCE.academic_year, PROGRAM_INSTANCE.sequence_identifier, PROGRAM_INSTANCE.enabled
	FROM PROGRAM_STUDY_OPTION INNER JOIN PROGRAM_INSTANCE
		ON PROGRAM_STUDY_OPTION.program_id = PROGRAM_INSTANCE.program_id
		AND PROGRAM_STUDY_OPTION.study_option_id = PROGRAM_INSTANCE.study_option_id
;

ALTER TABLE PROGRAM_STUDY_OPTION_INSTANCE
	MODIFY COLUMN application_deadline DATE NOT NULL AFTER application_start_date
;

DROP TABLE PROGRAM_INSTANCE
;

ALTER TABLE PROGRAM_STUDY_OPTION_INSTANCE
	CHANGE COLUMN application_deadline application_close_date DATE NOT NULL
;

ALTER TABLE PROGRAM_STUDY_OPTION
	ADD COLUMN application_start_date DATE AFTER study_option_id,
	ADD COLUMN application_close_date DATE AFTER application_start_date
;

UPDATE PROGRAM_STUDY_OPTION INNER JOIN (
	SELECT program_study_option_id AS study_option_id,
		MIN(application_start_date) start_date, 
		MAX(application_close_date) close_date
	FROM PROGRAM_STUDY_OPTION_INSTANCE
	GROUP BY program_study_option_id) AS BOUND
ON PROGRAM_STUDY_OPTION.id = BOUND.study_option_id
SET PROGRAM_STUDY_OPTION.application_start_date = BOUND.start_date,
	PROGRAM_STUDY_OPTION.application_close_date = BOUND.close_date
;

ALTER TABLE PROGRAM_STUDY_OPTION
	MODIFY COLUMN application_start_date DATE NOT NULL,
	MODIFY COLUMN application_close_date DATE NOT NULL
;

ALTER TABLE ADVERT
	DROP COLUMN immediate_start,
	ADD COLUMN default_start_date DATE AFTER publish_date
;

ALTER TABLE PROGRAM
	ADD COLUMN imported INT(1) UNSIGNED AFTER require_project_definition
;

ALTER TABLE PROGRAM
	ADD COLUMN imported INT(1) UNSIGNED AFTER require_project_definition,
	ADD INDEX (imported)
;

UPDATE PROGRAM
SET PROGRAM.imported = IF(imported_code IS NULL, 0, 1)
;

ALTER TABLE PROGRAM
	MODIFY COLUMN imported INT(1) UNSIGNED NOT NULL
;

ALTER TABLE ADVERT
	DROP INDEX immediate_start
;

ALTER TABLE PROGRAM_STUDY_OPTION
	ADD COLUMN default_start_date DATE AFTER application_close_date
;

ALTER TABLE PROJECT
	ADD COLUMN immediate_start INT(1) UNSIGNED AFTER title
;

ALTER TABLE PROGRAM
	ADD COLUMN month_group_start_frequency INT(10) UNSIGNED AFTER require_project_definition
;

ALTER TABLE PROJECT
	DROP COLUMN immediate_start,
	DROP COLUMN default_start_date,
	DROP INDEX default_start_date
;

ALTER TABLE ADVERT
	DROP COLUMN publish_date,
	DROP INDEX publish_date
;

ALTER TABLE PROGRAM_STUDY_OPTION
	DROP COLUMN default_start_date
;
