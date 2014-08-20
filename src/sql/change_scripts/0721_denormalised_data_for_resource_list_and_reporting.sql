UPDATE APPLICATION LEFT JOIN (
	SELECT COMMENT.application_id AS application_id,
		COUNT(COMMENT.id) AS count,
		ROUND(AVG(COMMENT.application_rating), 2) AS average
	FROM COMMENT
	WHERE COMMENT.application_rating IS NOT NULL
	GROUP BY COMMENT.application_id) AS APPLICANT_RATING
	ON APPLICATION.id = APPLICANT_RATING.application_id
SET APPLICATION.rating_count = APPLICANT_RATING.count,
	APPLICATION.average_rating = APPLICANT_RATING.average
;

SET SESSION GROUP_CONCAT_MAX_LEN = 1000000;

UPDATE PROJECT INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(5/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_05,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(20/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_20,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(35/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_35,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(50/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_50,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(65/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_65,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(80/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_80,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(95/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_95
	FROM APPLICATION
	WHERE APPLICATION.average_rating IS NOT NULL
	GROUP BY APPLICATION.project_id) AS APPLICATION_RATING
	ON PROJECT.id = APPLICATION_RATING.project_id
INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		COUNT(APPLICATION.id) AS created_count,
		SUM(
			IF(APPLICATION.submitted_timestamp IS NOT NULL,
				1,
				0)
			) AS submitted_count,
		SUM(
			IF(APPLICATION.state_id = "APPLICATION_APPROVED_COMPLETED",
				1,
				0)
			) AS approved_count,
		SUM(
			IF(APPLICATION.state_id = "APPLICATION_REJECTED_COMPLETED",
				1,
				0)
			) AS rejected_count,
		SUM(
			IF(APPLICATION.state_id = "APPLICATION_WITHDRAWN_COMPLETED",
				1,
				0)
			) AS withdrawn_count
	FROM APPLICATION
	GROUP BY APPLICATION.project_id) AS APPLICATION_PROCESS
	ON PROJECT.id = APPLICATION_PROCESS.project_id
SET PROJECT.application_created_count = APPLICATION_PROCESS.created_count,
	PROJECT.application_submitted_count = APPLICATION_PROCESS.submitted_count,
	PROJECT.application_approved_count = APPLICATION_PROCESS.approved_count,
	PROJECT.application_rejected_count = APPLICATION_PROCESS.rejected_count,
	PROJECT.application_withdrawn_count = APPLICATION_PROCESS.withdrawn_count,
	PROJECT.applicant_rating_percentile_05 = APPLICATION_RATING.percentile_05,
	PROJECT.applicant_rating_percentile_20 = APPLICATION_RATING.percentile_20,
	PROJECT.applicant_rating_percentile_35 = APPLICATION_RATING.percentile_35,
	PROJECT.applicant_rating_percentile_50 = APPLICATION_RATING.percentile_50,
	PROJECT.applicant_rating_percentile_65 = APPLICATION_RATING.percentile_65,
	PROJECT.applicant_rating_percentile_80 = APPLICATION_RATING.percentile_80,
	PROJECT.applicant_rating_percentile_95 = APPLICATION_RATING.percentile_95
;

UPDATE PROGRAM INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(5/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_05,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(20/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_20,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(35/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_35,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(50/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_50,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(65/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_65,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(80/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_80,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(95/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_95
	FROM APPLICATION
	WHERE APPLICATION.average_rating IS NOT NULL
	GROUP BY APPLICATION.program_id) AS APPLICATION_RATING
	ON PROGRAM.id = APPLICATION_RATING.program_id
INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		COUNT(APPLICATION.id) AS created_count,
		SUM(
			IF(APPLICATION.submitted_timestamp IS NOT NULL,
				1,
				0)
			) AS submitted_count,
		SUM(
			IF(APPLICATION.state_id = "APPLICATION_APPROVED_COMPLETED",
				1,
				0)
			) AS approved_count,
		SUM(
			IF(APPLICATION.state_id = "APPLICATION_REJECTED_COMPLETED",
				1,
				0)
			) AS rejected_count,
		SUM(
			IF(APPLICATION.state_id = "APPLICATION_WITHDRAWN_COMPLETED",
				1,
				0)
			) AS withdrawn_count
	FROM APPLICATION
	GROUP BY APPLICATION.program_id) AS APPLICATION_PROCESS
	ON PROGRAM.id = APPLICATION_PROCESS.program_id
SET PROGRAM.application_created_count = APPLICATION_PROCESS.created_count,
	PROGRAM.application_submitted_count = APPLICATION_PROCESS.submitted_count,
	PROGRAM.application_approved_count = APPLICATION_PROCESS.approved_count,
	PROGRAM.application_rejected_count = APPLICATION_PROCESS.rejected_count,
	PROGRAM.application_withdrawn_count = APPLICATION_PROCESS.withdrawn_count,
	PROGRAM.applicant_rating_percentile_05 = APPLICATION_RATING.percentile_05,
	PROGRAM.applicant_rating_percentile_20 = APPLICATION_RATING.percentile_20,
	PROGRAM.applicant_rating_percentile_35 = APPLICATION_RATING.percentile_35,
	PROGRAM.applicant_rating_percentile_50 = APPLICATION_RATING.percentile_50,
	PROGRAM.applicant_rating_percentile_65 = APPLICATION_RATING.percentile_65,
	PROGRAM.applicant_rating_percentile_80 = APPLICATION_RATING.percentile_80,
	PROGRAM.applicant_rating_percentile_95 = APPLICATION_RATING.percentile_95
;

UPDATE INSTITUTION INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(5/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_05,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(20/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_20,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(35/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_35,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(50/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_50,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(65/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_65,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(80/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_80,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
			GROUP_CONCAT(CAST(APPLICATION.average_rating AS CHAR) ORDER BY APPLICATION.average_rating SEPARATOR ','),
			',', ROUND(95/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,2)) AS percentile_95
	FROM APPLICATION
	WHERE APPLICATION.average_rating IS NOT NULL
	GROUP BY APPLICATION.institution_id) AS APPLICATION_RATING
	ON INSTITUTION.id = APPLICATION_RATING.institution_id
INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		COUNT(APPLICATION.id) AS created_count,
		SUM(
			IF(APPLICATION.submitted_timestamp IS NOT NULL,
				1,
				0)
			) AS submitted_count,
		SUM(
			IF(APPLICATION.state_id = "APPLICATION_APPROVED_COMPLETED",
				1,
				0)
			) AS approved_count,
		SUM(
			IF(APPLICATION.state_id = "APPLICATION_REJECTED_COMPLETED",
				1,
				0)
			) AS rejected_count,
		SUM(
			IF(APPLICATION.state_id = "APPLICATION_WITHDRAWN_COMPLETED" AND APPLICATION.previous_state_id NOT LIKE "APPLICATION_UNSUBMITTED%",
				1,
				0)
			) AS withdrawn_count
	FROM APPLICATION
	GROUP BY APPLICATION.institution_id) AS APPLICATION_PROCESS
	ON INSTITUTION.id = APPLICATION_PROCESS.institution_id
SET INSTITUTION.application_created_count = APPLICATION_PROCESS.created_count,
	INSTITUTION.application_submitted_count = APPLICATION_PROCESS.submitted_count,
	INSTITUTION.application_approved_count = APPLICATION_PROCESS.approved_count,
	INSTITUTION.application_rejected_count = APPLICATION_PROCESS.rejected_count,
	INSTITUTION.application_withdrawn_count = APPLICATION_PROCESS.withdrawn_count,
	INSTITUTION.applicant_rating_percentile_05 = APPLICATION_RATING.percentile_05,
	INSTITUTION.applicant_rating_percentile_20 = APPLICATION_RATING.percentile_20,
	INSTITUTION.applicant_rating_percentile_35 = APPLICATION_RATING.percentile_35,
	INSTITUTION.applicant_rating_percentile_50 = APPLICATION_RATING.percentile_50,
	INSTITUTION.applicant_rating_percentile_65 = APPLICATION_RATING.percentile_65,
	INSTITUTION.applicant_rating_percentile_80 = APPLICATION_RATING.percentile_80,
	INSTITUTION.applicant_rating_percentile_95 = APPLICATION_RATING.percentile_95
;

ALTER TABLE APPLICATION
	ADD COLUMN referrer_url VARCHAR(255) AFTER project_id,
	ADD INDEX (referrer_url)
;
