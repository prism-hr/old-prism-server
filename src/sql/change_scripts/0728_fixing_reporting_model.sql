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
