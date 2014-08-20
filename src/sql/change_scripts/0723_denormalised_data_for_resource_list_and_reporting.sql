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

SET SESSION GROUP_CONCAT_MAX_LEN = 1000000
;

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

UPDATE COMMENT
SET transition_state_id = NULL
WHERE action_id = "APPLICATION_ASSESS_ELIGIBILITY"
;

CREATE PROCEDURE SP_BUILD_APPLICATION_PROCESSING ()
BEGIN

	SET @current_application_id = (
		SELECT MIN(APPLICATION.id)
		FROM APPLICATION);
		
	CREATE TEMPORARY TABLE PROCESSED_COMMENT (
		id INT(10) UNSIGNED NOT NULL,
		PRIMARY KEY (id)
	) ENGINE = MEMORY;
	
	WHILE @current_application_id IS NOT NULL DO 
		SET @current_application_comment_id = (
			SELECT COMMENT.id
			FROM COMMENT
			WHERE COMMENT.application_id = @current_application_id
				AND COMMENT.action_id IN("PROGRAM_CREATE_APPLICATION", 
					"PROJECT_CREATE_APPLICATION", "APPLICATION_COMPLETE", 
					"APPLICATION_COMPLETE_VALIDATION_STAGE", 
					"APPLICATION_COMPELTE_REVIEW_STAGE", 
					"APPLICATION_COMPLETE_INTERVIEW_STAGE", 
					"APPLICATION_COMPLETE_APPROVAL_STAGE",
					"APPLICATION_MOVE_TO_DIFFERENT_STAGE")
			GROUP BY COMMENT.application_id, COMMENT.action_id, COMMENT.transition_state_id, COMMENT.created_timestamp
			ORDER BY COMMENT.created_timestamp, id
			LIMIT 0, 1);
			
		WHILE @current_application_comment_id IS NOT NULL DO
			INSERT INTO PROCESSED_COMMENT(id)
				VALUES(@current_application_comment_id);
		
			SELECT COMMENT.action_id, STATE.state_group_id, COMMENT.created_timestamp
				INTO @action_id, @transition_state_group_id, @created_timestamp
			FROM COMMENT INNER JOIN STATE
				ON COMMENT.transition_state_id = STATE.id
			WHERE COMMENT.id = @current_application_comment_id;
			
			IF (@transition_state_group_id IN ("APPLICATION_VALIDATION", "APPLICATION_REVIEW", "APPLICATION_INTERVIEW", "APPLICATION_APPROVAL")) THEN
				INSERT INTO APPLICATION_PROCESSING (application_id, state_group_id, instance_count, day_duration_sum)
				VALUES (@current_application_id, @transition_state_group_id, 1, 0)
				ON DUPLICATE KEY UPDATE instance_count = instance_count + 1; 
			END IF;
			
			IF @last_transition_state_group_id IS NOT NULL AND @current_application_id = @last_application_id THEN
				UPDATE APPLICATION_PROCESSING
				SET day_duration_sum = day_duration_sum + DATEDIFF(@created_timestamp, @last_created_timestamp)
				WHERE application_id = @current_application_id
					AND state_group_id = @last_transition_state_group_id;
			END IF;
			
			SET @last_transition_state_group_id = @transition_state_group_id;
			SET @last_created_timestamp = @created_timestamp;
			SET @last_application_id = @current_application_id;
			
			SET @current_application_comment_id = (
				SELECT COMMENT.id
				FROM COMMENT
				WHERE COMMENT.application_id = @current_application_id
					AND COMMENT.action_id IN("PROGRAM_CREATE_APPLICATION", 
						"PROJECT_CREATE_APPLICATION", "APPLICATION_COMPLETE", 
						"APPLICATION_COMPLETE_VALIDATION_STAGE", 
						"APPLICATION_COMPELTE_REVIEW_STAGE", 
						"APPLICATION_COMPLETE_INTERVIEW_STAGE", 
						"APPLICATION_COMPLETE_APPROVAL_STAGE", 
						"APPLICATION_MOVE_TO_DIFFERENT_STAGE")
					AND COMMENT.created_timestamp >= @created_timestamp
						AND COMMENT.id NOT IN (
							SELECT PROCESSED_COMMENT.id
							FROM PROCESSED_COMMENT)
				GROUP BY COMMENT.application_id, COMMENT.action_id, COMMENT.transition_state_id, COMMENT.created_timestamp
				ORDER BY COMMENT.created_timestamp, id
				LIMIT 0, 1);
		END WHILE;
	
		SET @current_application_id = (
			SELECT MIN(APPLICATION.id)
			FROM APPLICATION
			WHERE APPLICATION.id > @current_application_id
			ORDER BY APPLICATION.id);
	END WHILE;
	
	DROP TABLE PROCESSED_COMMENT;
	
END
;

CALL SP_BUILD_APPLICATION_PROCESSING ()
;

DROP PROCEDURE SP_BUILD_APPLICATION_PROCESSING
;

ALTER TABLE DOCUMENT
	DROP COLUMN document_type
;

SET SESSION GROUP_CONCAT_MAX_LEN = 1000000
;

INSERT INTO APPLICATION_PROCESSING_SUMMARY (project_id, state_group_id, instance_count_percentile_05, 
	instance_count_percentile_20, instance_count_percentile_35, instance_count_percentile_50,
	instance_count_percentile_65, instance_count_percentile_80, instance_count_percentile_95,
	day_duration_sum_percentile_05, day_duration_sum_percentile_20, day_duration_sum_percentile_35,
	day_duration_sum_percentile_50, day_duration_sum_percentile_65, day_duration_sum_percentile_80,
	day_duration_sum_percentile_95)
	SELECT APPLICATION.project_id AS project_id, APPLICATION_PROCESSING.state_group_id AS state_group_id, 
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(5/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_05,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(20/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_20,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(35/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_35,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(50/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_50,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(65/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_65,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(80/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_80,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(95/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_95,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(5/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_05,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(20/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_20,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(35/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_35,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(50/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_50,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(65/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_65,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(80/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_80,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(95/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_95
	FROM APPLICATION INNER JOIN APPLICATION_PROCESSING
		ON APPLICATION.id = APPLICATION_PROCESSING.application_id
	WHERE APPLICATION.project_id IS NOT NULL
	GROUP BY APPLICATION.project_id, APPLICATION_PROCESSING.state_group_id
;

INSERT INTO APPLICATION_PROCESSING_SUMMARY (program_id, state_group_id, instance_count_percentile_05, 
	instance_count_percentile_20, instance_count_percentile_35, instance_count_percentile_50,
	instance_count_percentile_65, instance_count_percentile_80, instance_count_percentile_95,
	day_duration_sum_percentile_05, day_duration_sum_percentile_20, day_duration_sum_percentile_35,
	day_duration_sum_percentile_50, day_duration_sum_percentile_65, day_duration_sum_percentile_80,
	day_duration_sum_percentile_95)
	SELECT APPLICATION.program_id AS program_id, APPLICATION_PROCESSING.state_group_id AS state_group_id, 
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(5/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_05,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(20/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_20,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(35/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_35,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(50/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_50,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(65/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_65,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(80/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_80,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(95/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_95,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(5/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_05,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(20/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_20,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(35/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_35,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(50/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_50,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(65/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_65,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(80/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_80,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(95/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_95
	FROM APPLICATION INNER JOIN APPLICATION_PROCESSING
		ON APPLICATION.id = APPLICATION_PROCESSING.application_id
	WHERE APPLICATION.program_id IS NOT NULL
	GROUP BY APPLICATION.program_id, APPLICATION_PROCESSING.state_group_id
;

INSERT INTO APPLICATION_PROCESSING_SUMMARY (institution_id, state_group_id, instance_count_percentile_05, 
	instance_count_percentile_20, instance_count_percentile_35, instance_count_percentile_50,
	instance_count_percentile_65, instance_count_percentile_80, instance_count_percentile_95,
	day_duration_sum_percentile_05, day_duration_sum_percentile_20, day_duration_sum_percentile_35,
	day_duration_sum_percentile_50, day_duration_sum_percentile_65, day_duration_sum_percentile_80,
	day_duration_sum_percentile_95)
	SELECT APPLICATION.institution_id AS institution_id, APPLICATION_PROCESSING.state_group_id AS state_group_id, 
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(5/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_05,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(20/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_20,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(35/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_35,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(50/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_50,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(65/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_65,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(80/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_80,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.instance_count AS CHAR) ORDER BY APPLICATION_PROCESSING.instance_count SEPARATOR ','),
				',', ROUND(95/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS instance_count_percentile_95,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(5/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_05,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(20/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_20,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(35/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_35,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(50/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_50,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(65/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_65,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(80/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_80,
		CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(
				GROUP_CONCAT(CAST(APPLICATION_PROCESSING.day_duration_sum AS CHAR) ORDER BY APPLICATION_PROCESSING.day_duration_sum SEPARATOR ','),
				',', ROUND(95/100 * COUNT(*)) + 1), ',', -1) AS DECIMAL(3,0)) AS day_duration_sum_percentile_95
	FROM APPLICATION INNER JOIN APPLICATION_PROCESSING
		ON APPLICATION.id = APPLICATION_PROCESSING.application_id
	WHERE APPLICATION.institution_id IS NOT NULL
	GROUP BY APPLICATION.institution_id, APPLICATION_PROCESSING.state_group_id
;

UPDATE COMMENT INNER JOIN APPLICATION
	ON COMMENT.application_id = APPLICATION.id
INNER JOIN (
	SELECT SUMMARY.application_id 
	FROM (
		SELECT application_id, 
			GROUP_CONCAT(action_id 
				ORDER BY created_timestamp
				SEPARATOR ", ") AS action_history,
			COUNT(action_id) AS action_count
		FROM COMMENT
		WHERE application_id IS NOT NULL
		GROUP BY application_id
		HAVING COUNT(action_id) > 1
		ORDER BY COUNT(action_id) DESC
	) AS SUMMARY
	WHERE SUMMARY.action_history NOT LIKE "PROGRAM_CREATE_APPLICATION, %"
		AND SUMMARY.action_history NOT LIKE "PROJECT_CREATE_APPLICATION, %") AS FILTER
	ON APPLICATION.id = FILTER.application_id
SET COMMENT.created_timestamp = COMMENT.created_timestamp - INTERVAL 1 DAY,
	APPLICATION.submitted_timestamp = APPLICATION.submitted_timestamp - INTERVAL 1 DAY
WHERE COMMENT.action_id IN ("PROGRAM_CREATE_APPLICATION", "PROJECT_CREATE_APPLICATION")
;

UPDATE COMMENT INNER JOIN  (
	SELECT SUMMARY.application_id AS application_id
	FROM (
		SELECT application_id, 
			GROUP_CONCAT(action_id 
				ORDER BY created_timestamp
				SEPARATOR ", ") AS action_history,
			COUNT(action_id) AS action_count
		FROM COMMENT
		WHERE application_id IS NOT NULL
		GROUP BY application_id
		HAVING COUNT(action_id) > 2
		ORDER BY COUNT(action_id) DESC) AS SUMMARY
	INNER JOIN APPLICATION
		ON SUMMARY.application_id = APPLICATION.id
	WHERE SUMMARY.action_history NOT LIKE "%EXPORT"
		AND SUMMARY.action_history NOT LIKE "%EXPORT, APPLICATION_COMMENT"
		AND SUMMARY.action_history LIKE "%EXPORT%"
		AND SUMMARY.action_history LIKE "%APPLICATION_PROVIDE_REFERENCE") AS FILTER
	ON COMMENT.application_id = FILTER.application_id
INNER JOIN (
	SELECT COMMENT.application_id AS application_id,
		MAX(COMMENT.created_timestamp) AS adjustment_timestamp
	FROM COMMENT
	WHERE COMMENT.action_id = "APPLICATION_ASSIGN_SUPERVISORS"
	GROUP BY COMMENT.application_id) AS REMAP
	ON COMMENT.application_id = REMAP.application_id
SET COMMENT.created_timestamp = REMAP.adjustment_timestamp
WHERE COMMENT.action_id = "APPLICATION_PROVIDE_REFERENCE"
	AND COMMENT.delegate_user_id IS NOT NULL
;

UPDATE COMMENT INNER JOIN  (
	SELECT SUMMARY.application_id AS application_id
	FROM (
		SELECT application_id, 
			GROUP_CONCAT(action_id 
				ORDER BY created_timestamp
				SEPARATOR ", ") AS action_history,
			COUNT(action_id) AS action_count
		FROM COMMENT
		WHERE application_id IS NOT NULL
		GROUP BY application_id
		HAVING COUNT(action_id) > 2
		ORDER BY COUNT(action_id) DESC) AS SUMMARY
	INNER JOIN APPLICATION
		ON SUMMARY.application_id = APPLICATION.id
	WHERE SUMMARY.action_history NOT LIKE "%EXPORT"
		AND SUMMARY.action_history NOT LIKE "%EXPORT, APPLICATION_COMMENT"
		AND SUMMARY.action_history LIKE "%EXPORT%"
		AND SUMMARY.action_history LIKE "%APPLICATION_PROVIDE_REFERENCE") AS FILTER
	ON COMMENT.application_id = FILTER.application_id
INNER JOIN (
	SELECT COMMENT.application_id AS application_id,
		MAX(COMMENT.created_timestamp) AS adjustment_timestamp
	FROM COMMENT
	WHERE COMMENT.action_id = "APPLICATION_ASSIGN_REVIEWERS"
	GROUP BY COMMENT.application_id) AS REMAP
	ON COMMENT.application_id = REMAP.application_id
SET COMMENT.created_timestamp = REMAP.adjustment_timestamp
WHERE COMMENT.action_id = "APPLICATION_PROVIDE_REFERENCE"
	AND COMMENT.delegate_user_id IS NOT NULL
;

UPDATE COMMENT INNER JOIN  (
	SELECT SUMMARY.application_id AS application_id
	FROM (
		SELECT application_id, 
			GROUP_CONCAT(action_id 
				ORDER BY created_timestamp
				SEPARATOR ", ") AS action_history,
			COUNT(action_id) AS action_count
		FROM COMMENT
		WHERE application_id IS NOT NULL
		GROUP BY application_id
		HAVING COUNT(action_id) > 2
		ORDER BY COUNT(action_id) DESC) AS SUMMARY
	INNER JOIN APPLICATION
		ON SUMMARY.application_id = APPLICATION.id
	WHERE SUMMARY.action_history NOT LIKE "%EXPORT"
		AND SUMMARY.action_history NOT LIKE "%EXPORT, APPLICATION_COMMENT"
		AND SUMMARY.action_history LIKE "%EXPORT%"
		AND SUMMARY.action_history LIKE "%APPLICATION_PROVIDE_REFERENCE") AS FILTER
	ON COMMENT.application_id = FILTER.application_id
INNER JOIN (
	SELECT COMMENT.application_id AS application_id,
		MAX(COMMENT.created_timestamp) AS adjustment_timestamp
	FROM COMMENT
	WHERE COMMENT.action_id = "APPLICATION_ASSIGN_INTERVIEWERS"
	GROUP BY COMMENT.application_id) AS REMAP
	ON COMMENT.application_id = REMAP.application_id
SET COMMENT.created_timestamp = REMAP.adjustment_timestamp
WHERE COMMENT.action_id = "APPLICATION_PROVIDE_REFERENCE"
	AND COMMENT.delegate_user_id IS NOT NULL
;

UPDATE COMMENT 
SET COMMENT.created_timestamp = COMMENT.created_timestamp + INTERVAL 3 DAY
WHERE COMMENT.application_id = 6752
	AND COMMENT.action_id = "APPLICATION_EXPORT"
;
