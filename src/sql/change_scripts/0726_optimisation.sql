ALTER TABLE COMMENT
	ADD INDEX (application_id, application_rating)
;

ALTER TABLE APPLICATION
	ADD INDEX (institution_id, rating_count),
	ADD INDEX (institution_id, average_rating),
	ADD INDEX (program_id, rating_count),
	ADD INDEX (program_id, average_rating),
	ADD INDEX (project_id, rating_count),
	ADD INDEX (project_id, average_rating)
;

ALTER TABLE APPLICATION
	ADD INDEX (rating_count),
	ADD INDEX (average_rating)
;

ALTER TABLE INSTITUTION
	ADD COLUMN application_rating_count_percentile_05 INT(10) UNSIGNED AFTER application_withdrawn_count,
	ADD COLUMN application_rating_count_percentile_20 INT(10) UNSIGNED AFTER application_rating_count_percentile_05,
	ADD COLUMN application_rating_count_percentile_35 INT(10) UNSIGNED AFTER application_rating_count_percentile_20,
	ADD COLUMN application_rating_count_percentile_50 INT(10) UNSIGNED AFTER application_rating_count_percentile_35,
	ADD COLUMN application_rating_count_percentile_65 INT(10) UNSIGNED AFTER application_rating_count_percentile_50,
	ADD COLUMN application_rating_count_percentile_80 INT(10) UNSIGNED AFTER application_rating_count_percentile_65,
	ADD COLUMN application_rating_count_percentile_95 INT(10) UNSIGNED AFTER application_rating_count_percentile_80,
	CHANGE COLUMN applicant_rating_percentile_05 application_rating_average_percentile05 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_20 application_rating_average_percentile20 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_35 application_rating_average_percentile35 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_50 application_rating_average_percentile50 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_65 application_rating_average_percentile65 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_80 application_rating_average_percentile80 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_95 application_rating_average_percentile95 DECIMAL(3,2) UNSIGNED,
	ADD INDEX (application_rating_count_percentile_05, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_20, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_35, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_50, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_65, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_80, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_95, sequence_identifier)
;

ALTER TABLE PROGRAM
	ADD COLUMN application_rating_count_percentile_05 INT(10) UNSIGNED AFTER application_withdrawn_count,
	ADD COLUMN application_rating_count_percentile_20 INT(10) UNSIGNED AFTER application_rating_count_percentile_05,
	ADD COLUMN application_rating_count_percentile_35 INT(10) UNSIGNED AFTER application_rating_count_percentile_20,
	ADD COLUMN application_rating_count_percentile_50 INT(10) UNSIGNED AFTER application_rating_count_percentile_35,
	ADD COLUMN application_rating_count_percentile_65 INT(10) UNSIGNED AFTER application_rating_count_percentile_50,
	ADD COLUMN application_rating_count_percentile_80 INT(10) UNSIGNED AFTER application_rating_count_percentile_65,
	ADD COLUMN application_rating_count_percentile_95 INT(10) UNSIGNED AFTER application_rating_count_percentile_80,
	CHANGE COLUMN applicant_rating_percentile_05 application_rating_average_percentile05 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_20 application_rating_average_percentile20 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_35 application_rating_average_percentile35 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_50 application_rating_average_percentile50 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_65 application_rating_average_percentile65 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_80 application_rating_average_percentile80 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_95 application_rating_average_percentile95 DECIMAL(3,2) UNSIGNED,
	ADD INDEX (application_rating_count_percentile_05, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_20, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_35, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_50, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_65, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_80, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_95, sequence_identifier)
;

ALTER TABLE PROJECT
	ADD COLUMN application_rating_count_percentile_05 INT(10) UNSIGNED AFTER application_withdrawn_count,
	ADD COLUMN application_rating_count_percentile_20 INT(10) UNSIGNED AFTER application_rating_count_percentile_05,
	ADD COLUMN application_rating_count_percentile_35 INT(10) UNSIGNED AFTER application_rating_count_percentile_20,
	ADD COLUMN application_rating_count_percentile_50 INT(10) UNSIGNED AFTER application_rating_count_percentile_35,
	ADD COLUMN application_rating_count_percentile_65 INT(10) UNSIGNED AFTER application_rating_count_percentile_50,
	ADD COLUMN application_rating_count_percentile_80 INT(10) UNSIGNED AFTER application_rating_count_percentile_65,
	ADD COLUMN application_rating_count_percentile_95 INT(10) UNSIGNED AFTER application_rating_count_percentile_80,
	CHANGE COLUMN applicant_rating_percentile_05 application_rating_average_percentile05 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_20 application_rating_average_percentile20 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_35 application_rating_average_percentile35 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_50 application_rating_average_percentile50 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_65 application_rating_average_percentile65 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_80 application_rating_average_percentile80 DECIMAL(3,2) UNSIGNED,
	CHANGE COLUMN applicant_rating_percentile_95 application_rating_average_percentile95 DECIMAL(3,2) UNSIGNED,
	ADD INDEX (application_rating_count_percentile_05, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_20, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_35, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_50, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_65, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_80, sequence_identifier),
	ADD INDEX (application_rating_count_percentile_95, sequence_identifier)
;

ALTER TABLE APPLICATION
	CHANGE COLUMN average_rating rating_average DECIMAL(3,2) UNSIGNED
;

ALTER TABLE APPLICATION_PROCESSING
	ADD COLUMN last_updated_date DATE AFTER day_duration_sum
;

UPDATE APPLICATION_PROCESSING INNER JOIN (
	SELECT COMMENT.application_id AS application_id,
	MAX(DATE(COMMENT.created_timestamp)) AS occurrence
	FROM COMMENT
	WHERE COMMENT.application_id IS NOT NULL 
		AND COMMENT.transition_state_id = "APPLICATION_VALIDATION"
	GROUP BY COMMENT.application_id) AS LATEST_COMMENT
	ON APPLICATION_PROCESSING.application_id = LATEST_COMMENT.application_id
SET APPLICATION_PROCESSING.last_updated_date = occurrence
WHERE APPLICATION_PROCESSING.state_group_id = "APPLICATION_VALIDATION"
;

UPDATE APPLICATION_PROCESSING INNER JOIN (
	SELECT COMMENT.application_id AS application_id,
	MAX(DATE(COMMENT.created_timestamp)) AS occurrence
	FROM COMMENT
	WHERE COMMENT.application_id IS NOT NULL 
		AND COMMENT.transition_state_id = "APPLICATION_REVIEW"
	GROUP BY COMMENT.application_id) AS LATEST_COMMENT
	ON APPLICATION_PROCESSING.application_id = LATEST_COMMENT.application_id
SET APPLICATION_PROCESSING.last_updated_date = occurrence
WHERE APPLICATION_PROCESSING.state_group_id = "APPLICATION_REVIEW"
;

UPDATE APPLICATION_PROCESSING INNER JOIN (
	SELECT COMMENT.application_id AS application_id,
	MAX(DATE(COMMENT.created_timestamp)) AS occurrence
	FROM COMMENT
	WHERE COMMENT.application_id IS NOT NULL 
		AND COMMENT.transition_state_id = "APPLICATION_INTERVIEW"
	GROUP BY COMMENT.application_id) AS LATEST_COMMENT
	ON APPLICATION_PROCESSING.application_id = LATEST_COMMENT.application_id
SET APPLICATION_PROCESSING.last_updated_date = occurrence
WHERE APPLICATION_PROCESSING.state_group_id = "APPLICATION_INTERVIEW"
;

UPDATE APPLICATION_PROCESSING INNER JOIN (
	SELECT COMMENT.application_id AS application_id,
	MAX(DATE(COMMENT.created_timestamp)) AS occurrence
	FROM COMMENT
	WHERE COMMENT.application_id IS NOT NULL 
		AND COMMENT.transition_state_id = "APPLICATION_APPROVAL"
	GROUP BY COMMENT.application_id) AS LATEST_COMMENT
	ON APPLICATION_PROCESSING.application_id = LATEST_COMMENT.application_id
SET APPLICATION_PROCESSING.last_updated_date = occurrence
WHERE APPLICATION_PROCESSING.state_group_id = "APPLICATION_APPROVAL"
;

ALTER TABLE APPLICATION_PROCESSING
	MODIFY COLUMN last_updated_date DATE NOT NULL
;

SET GROUP_CONCAT_MAX_LEN = 1000000
;

UPDATE PROJECT INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(5/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_05,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(20/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_20,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(35/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_35,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(50/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_50,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(65/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_65,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(80/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_80,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(95/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_95
	FROM APPLICATION
	WHERE APPLICATION.rating_count IS NOT NULL
	GROUP BY APPLICATION.project_id) AS APPLICATION_RATING_COUNT
	ON PROJECT.id = APPLICATION_RATING_COUNT.project_id
SET PROJECT.application_rating_count_percentile_05 = APPLICATION_RATING_COUNT.percentile_05,
	PROJECT.application_rating_count_percentile_20 = APPLICATION_RATING_COUNT.percentile_20,
	PROJECT.application_rating_count_percentile_35 = APPLICATION_RATING_COUNT.percentile_35,
	PROJECT.application_rating_count_percentile_50 = APPLICATION_RATING_COUNT.percentile_50,
	PROJECT.application_rating_count_percentile_65 = APPLICATION_RATING_COUNT.percentile_65,
	PROJECT.application_rating_count_percentile_80 = APPLICATION_RATING_COUNT.percentile_80,
	PROJECT.application_rating_count_percentile_95 = APPLICATION_RATING_COUNT.percentile_95
;

UPDATE PROGRAM INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(5/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_05,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(20/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_20,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(35/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_35,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(50/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_50,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(65/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_65,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(80/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_80,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(95/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_95
	FROM APPLICATION
	WHERE APPLICATION.rating_count IS NOT NULL
	GROUP BY APPLICATION.program_id) AS APPLICATION_RATING_COUNT
	ON PROGRAM.id = APPLICATION_RATING_COUNT.program_id
SET PROGRAM.application_rating_count_percentile_05 = APPLICATION_RATING_COUNT.percentile_05,
	PROGRAM.application_rating_count_percentile_20 = APPLICATION_RATING_COUNT.percentile_20,
	PROGRAM.application_rating_count_percentile_35 = APPLICATION_RATING_COUNT.percentile_35,
	PROGRAM.application_rating_count_percentile_50 = APPLICATION_RATING_COUNT.percentile_50,
	PROGRAM.application_rating_count_percentile_65 = APPLICATION_RATING_COUNT.percentile_65,
	PROGRAM.application_rating_count_percentile_80 = APPLICATION_RATING_COUNT.percentile_80,
	PROGRAM.application_rating_count_percentile_95 = APPLICATION_RATING_COUNT.percentile_95
;

UPDATE INSTITUTION INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(5/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_05,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(20/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_20,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(35/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_35,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(50/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_50,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(65/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_65,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(80/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_80,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.rating_count AS CHAR) ORDER BY APPLICATION.rating_count SEPARATOR ','),
			',', ROUND(95/100 * COUNT(*)) + 1), ',', -1) AS UNSIGNED) AS percentile_95
	FROM APPLICATION
	WHERE APPLICATION.rating_count IS NOT NULL
	GROUP BY APPLICATION.institution_id) AS APPLICATION_RATING_COUNT
	ON INSTITUTION.id = APPLICATION_RATING_COUNT.institution_id
SET INSTITUTION.application_rating_count_percentile_05 = APPLICATION_RATING_COUNT.percentile_05,
	INSTITUTION.application_rating_count_percentile_20 = APPLICATION_RATING_COUNT.percentile_20,
	INSTITUTION.application_rating_count_percentile_35 = APPLICATION_RATING_COUNT.percentile_35,
	INSTITUTION.application_rating_count_percentile_50 = APPLICATION_RATING_COUNT.percentile_50,
	INSTITUTION.application_rating_count_percentile_65 = APPLICATION_RATING_COUNT.percentile_65,
	INSTITUTION.application_rating_count_percentile_80 = APPLICATION_RATING_COUNT.percentile_80,
	INSTITUTION.application_rating_count_percentile_95 = APPLICATION_RATING_COUNT.percentile_95
;

