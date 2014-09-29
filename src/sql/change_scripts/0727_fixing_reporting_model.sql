TRUNCATE TABLE APPLICATION_PROCESSING
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
					"APPLICATION_COMPLETE_REVIEW_STAGE", 
					"APPLICATION_COMPLETE_INTERVIEW_STAGE", 
					"APPLICATION_COMPLETE_APPROVAL_STAGE",
					"APPLICATION_MOVE_TO_DIFFERENT_STAGE",
					"APPLICATION_WITHDRAW")
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
			
			INSERT INTO APPLICATION_PROCESSING (application_id, state_group_id, instance_count, day_duration_sum, last_updated_date)
			VALUES (@current_application_id, @transition_state_group_id, 1, 0, DATE(@created_timestamp))
			ON DUPLICATE KEY UPDATE instance_count = instance_count + 1, last_updated_date = DATE(@created_timestamp);
			
			IF @last_transition_state_group_id IS NOT NULL AND @current_application_id = @last_application_id THEN
				UPDATE APPLICATION_PROCESSING
				SET day_duration_sum = day_duration_sum + DATEDIFF(@created_timestamp, @last_created_timestamp),
					last_updated_date = @created_timestamp
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
						"APPLICATION_COMPLETE_REVIEW_STAGE", 
						"APPLICATION_COMPLETE_INTERVIEW_STAGE", 
						"APPLICATION_COMPLETE_APPROVAL_STAGE", 
						"APPLICATION_MOVE_TO_DIFFERENT_STAGE",
						"APPLICATION_WITHDRAW")
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

ALTER TABLE PROJECT
	DROP INDEX application_in_validation_count,
	DROP INDEX application_in_review_count,
	DROP INDEX application_in_interview_count,
	DROP INDEX application_in_approval_count,
	DROP INDEX application_created_count,
	DROP INDEX application_validated_count,
	DROP INDEX application_reviewed_count,
	DROP INDEX application_interviewed_count,
	DROP INDEX application_approvaled_count,
	DROP INDEX application_approved_count,
	DROP INDEX application_rejected_count,
	DROP INDEX application_withdrawn_count,
	DROP COLUMN application_in_validation_count,
	DROP COLUMN application_in_review_count,
	DROP COLUMN application_in_interview_count,
	DROP COLUMN application_in_approval_count,
	DROP COLUMN application_created_count,
	DROP COLUMN application_validated_count,
	DROP COLUMN application_reviewed_count,
	DROP COLUMN application_interviewed_count,
	DROP COLUMN application_approvaled_count,
	DROP COLUMN application_approved_count,
	DROP COLUMN application_rejected_count,
	DROP COLUMN application_withdrawn_count
;

ALTER TABLE PROGRAM
	DROP INDEX application_in_validation_count,
	DROP INDEX application_in_review_count,
	DROP INDEX application_in_interview_count,
	DROP INDEX application_in_approval_count,
	DROP INDEX application_created_count,
	DROP INDEX application_validated_count,
	DROP INDEX application_reviewed_count,
	DROP INDEX application_interviewed_count,
	DROP INDEX application_approvaled_count,
	DROP INDEX application_approved_count,
	DROP INDEX application_rejected_count,
	DROP INDEX application_withdrawn_count,
	DROP COLUMN application_in_validation_count,
	DROP COLUMN application_in_review_count,
	DROP COLUMN application_in_interview_count,
	DROP COLUMN application_in_approval_count,
	DROP COLUMN application_created_count,
	DROP COLUMN application_validated_count,
	DROP COLUMN application_reviewed_count,
	DROP COLUMN application_interviewed_count,
	DROP COLUMN application_approvaled_count,
	DROP COLUMN application_approved_count,
	DROP COLUMN application_rejected_count,
	DROP COLUMN application_withdrawn_count
;

ALTER TABLE INSTITUTION
	DROP INDEX application_in_validation_count,
	DROP INDEX application_in_review_count,
	DROP INDEX application_in_interview_count,
	DROP INDEX application_in_approval_count,
	DROP INDEX application_created_count,
	DROP INDEX application_validated_count,
	DROP INDEX application_reviewed_count,
	DROP INDEX application_interviewed_count,
	DROP INDEX application_approvaled_count,
	DROP INDEX application_approved_count,
	DROP INDEX application_rejected_count,
	DROP INDEX application_withdrawn_count,
	DROP COLUMN application_in_validation_count,
	DROP COLUMN application_in_review_count,
	DROP COLUMN application_in_interview_count,
	DROP COLUMN application_in_approval_count,
	DROP COLUMN application_created_count,
	DROP COLUMN application_validated_count,
	DROP COLUMN application_reviewed_count,
	DROP COLUMN application_interviewed_count,
	DROP COLUMN application_approvaled_count,
	DROP COLUMN application_approved_count,
	DROP COLUMN application_rejected_count,
	DROP COLUMN application_withdrawn_count
;

TRUNCATE TABLE APPLICATION_PROCESSING_SUMMARY
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

ALTER TABLE APPLICATION
	DROP INDEX applicant_fk,
	DROP INDEX prog_app_fk,
	DROP INDEX status_idx,
	DROP INDEX project_fk,
	DROP INDEX created_timestamp,
	DROP INDEX submitted_timestamp,
	DROP INDEX updated_timestamp,
	DROP INDEX code,
	DROP INDEX institution_id,
	DROP INDEX rating_count_2,
	DROP INDEX average_rating_2,
	DROP INDEX referrer_url,
	ADD INDEX referrer (referrer, sequence_identifier),
	DROP INDEX institution_id_3,
	DROP INDEX institution_id_4,
	DROP INDEX program_id_2,
	DROP INDEX program_id_3,
	DROP INDEX project_id_2,
	DROP INDEX project_id_3,
	DROP INDEX institution_id_2,
	ADD INDEX (institution_id, sequence_identifier),
	DROP INDEX created_timestamp_2,
	ADD INDEX (created_timestamp, sequence_identifier),
	DROP INDEX submitted_timestamp_2,
	ADD INDEX (submitted_timestamp, sequence_identifier),
	DROP INDEX updated_timestamp_2,
	ADD INDEX (updated_timestamp, sequence_identifier),
	DROP INDEX code_2,
	ADD INDEX (code, sequence_identifier),
	DROP INDEX previous_state_id,
	ADD INDEX (previous_state_id, sequence_identifier),
	DROP INDEX previous_closing_date,
	ADD INDEX (previous_closing_date, sequence_identifier)
;

ALTER TABLE PROJECT
	DROP INDEX project_program_fk,
	DROP INDEX state_id,
	DROP INDEX state_id_2,
	ADD INDEX (state_id, sequence_identifier),
	DROP INDEX previous_state_id,
	ADD INDEX (previous_state_id, sequence_identifier),
	DROP INDEX institution_id,
	DROP INDEX institution_id_2,
	ADD INDEX (institution_id, sequence_identifier),
	DROP INDEX updated_timestamp,
	DROP INDEX updated_timestamp_2,
	ADD INDEX (updated_timestamp, sequence_identifier),
	DROP INDEX code,
	DROP INDEX code_2,
	ADD INDEX (code, sequence_identifier),
	DROP INDEX referrer,
	ADD INDEX (referrer, sequence_identifier),
	DROP INDEX user_id,
	ADD INDEX (user_id, sequence_identifier),
	DROP INDEX advert_id,
	ADD INDEX (advert_id, sequence_identifier),
	ADD INDEX (due_date)
;

ALTER TABLE PROGRAM
	DROP INDEX program_type_id,
	DROP INDEX program_type,
	ADD INDEX (program_type, sequence_identifier),
	DROP INDEX state_id,
	DROP INDEX state_id_2,
	ADD INDEX (state_id, sequence_identifier),
	DROP INDEX previous_state_id,
	ADD INDEX (previous_state_id, sequence_identifier),
	DROP INDEX institution_id,
	DROP INDEX institution_id_2,
	ADD INDEX (institution_id, sequence_identifier),
	DROP INDEX code,
	DROP INDEX code_2,
	ADD INDEX (code, sequence_identifier),
	DROP INDEX title,
	DROP INDEX title_2,
	ADD INDEX (title, sequence_identifier),
	DROP INDEX updated_timestamp,
	DROP INDEX updated_timestamp_2,
	ADD INDEX (updated_timestamp, sequence_identifier),
	DROP INDEX imported_code,
	DROP INDEX imported_code_2,
	ADD INDEX (imported_code, sequence_identifier),
	DROP INDEX referrer,
	ADD INDEX (referrer, sequence_identifier),
	DROP INDEX user_id,
	ADD INDEX (user_id, sequence_identifier),
	DROP INDEX advert_id,
	ADD INDEX (advert_id, sequence_identifier),
	ADD INDEX (due_date)
;

ALTER TABLE INSTITUTION
	DROP INDEX state_id,
	DROP INDEX state_id_2,
	ADD INDEX (state_id, sequence_identifier),
	DROP INDEX user_id,
	DROP INDEX user_id_2,
	ADD INDEX (user_id, sequence_identifier),
	DROP INDEX institution_address_id,
	DROP INDEX institution_address_id_2,
	ADD INDEX (institution_address_id, sequence_identifier),
	DROP INDEX code,
	DROP INDEX code_2,
	ADD INDEX (code, sequence_identifier),
	ADD INDEX (due_date),
	DROP INDEX referrer,
	ADD INDEX (referrer, sequence_identifier)
;

ALTER TABLE APPLICATION_PROCESSING_SUMMARY
	ADD COLUMN instance_sum INT(10) UNSIGNED AFTER state_group_id,
	ADD COLUMN instance_sum_live INT(10) UNSIGNED AFTER instance_sum,
	ADD INDEX (instance_sum),
	ADD INDEX (instance_sum_live)
;

UPDATE APPLICATION_PROCESSING_SUMMARY INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		STATE.state_group_id AS state_group_id,
		COUNT(APPLICATION.id) AS total_live
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	GROUP BY APPLICATION.institution_id, STATE.state_group_id) AS INSTANCE
	ON APPLICATION_PROCESSING_SUMMARY.institution_id = INSTANCE.institution_id
	AND APPLICATION_PROCESSING_SUMMARY.state_group_id = INSTANCE.state_group_id
SET APPLICATION_PROCESSING_SUMMARY.instance_sum_live = INSTANCE.total_live
;

UPDATE APPLICATION_PROCESSING_SUMMARY INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		STATE.state_group_id AS state_group_id,
		COUNT(APPLICATION.id) AS total_live
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	GROUP BY APPLICATION.program_id, STATE.state_group_id) AS INSTANCE
	ON APPLICATION_PROCESSING_SUMMARY.program_id = INSTANCE.program_id
	AND APPLICATION_PROCESSING_SUMMARY.state_group_id = INSTANCE.state_group_id
SET APPLICATION_PROCESSING_SUMMARY.instance_sum_live = INSTANCE.total_live
;

UPDATE APPLICATION_PROCESSING_SUMMARY INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		STATE.state_group_id AS state_group_id,
		COUNT(APPLICATION.id) AS total_live
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	GROUP BY APPLICATION.project_id, STATE.state_group_id) AS INSTANCE
	ON APPLICATION_PROCESSING_SUMMARY.project_id = INSTANCE.project_id
	AND APPLICATION_PROCESSING_SUMMARY.state_group_id = INSTANCE.state_group_id
SET APPLICATION_PROCESSING_SUMMARY.instance_sum_live = INSTANCE.total_live
;

UPDATE APPLICATION_PROCESSING_SUMMARY
SET instance_sum_live = 0
WHERE instance_sum_live IS NULL
;

UPDATE APPLICATION_PROCESSING_SUMMARY INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id,
		APPLICATION_PROCESSING.state_group_id,
		SUM(APPLICATION_PROCESSING.instance_count) AS instance_sum
	FROM APPLICATION INNER JOIN APPLICATION_PROCESSING
		ON APPLICATION.id = APPLICATION_PROCESSING.application_id
	GROUP BY APPLICATION.institution_id, APPLICATION_PROCESSING.state_group_id) AS INSTANCE
	ON APPLICATION_PROCESSING_SUMMARY.institution_id = INSTANCE.institution_id
	AND APPLICATION_PROCESSING_SUMMARY.state_group_id = INSTANCE.state_group_id
SET APPLICATION_PROCESSING_SUMMARY.instance_sum = INSTANCE.instance_sum
;

UPDATE APPLICATION_PROCESSING_SUMMARY INNER JOIN (
	SELECT APPLICATION.program_id AS program_id,
		APPLICATION_PROCESSING.state_group_id,
		SUM(APPLICATION_PROCESSING.instance_count) AS instance_sum
	FROM APPLICATION INNER JOIN APPLICATION_PROCESSING
		ON APPLICATION.id = APPLICATION_PROCESSING.application_id
	GROUP BY APPLICATION.program_id, APPLICATION_PROCESSING.state_group_id) AS INSTANCE
	ON APPLICATION_PROCESSING_SUMMARY.program_id = INSTANCE.program_id
	AND APPLICATION_PROCESSING_SUMMARY.state_group_id = INSTANCE.state_group_id
SET APPLICATION_PROCESSING_SUMMARY.instance_sum = INSTANCE.instance_sum
;

UPDATE APPLICATION_PROCESSING_SUMMARY INNER JOIN (
	SELECT APPLICATION.project_id AS project_id,
		APPLICATION_PROCESSING.state_group_id,
		SUM(APPLICATION_PROCESSING.instance_count) AS instance_sum
	FROM APPLICATION INNER JOIN APPLICATION_PROCESSING
		ON APPLICATION.id = APPLICATION_PROCESSING.application_id
	GROUP BY APPLICATION.project_id, APPLICATION_PROCESSING.state_group_id) AS INSTANCE
	ON APPLICATION_PROCESSING_SUMMARY.project_id = INSTANCE.project_id
	AND APPLICATION_PROCESSING_SUMMARY.state_group_id = INSTANCE.state_group_id
SET APPLICATION_PROCESSING_SUMMARY.instance_sum = INSTANCE.instance_sum
;

ALTER TABLE APPLICATION_PROCESSING_SUMMARY
	MODIFY COLUMN instance_sum INT(10) UNSIGNED NOT NULL,
	MODIFY COLUMN instance_sum_live INT(10) UNSIGNED NOT NULL
;

ALTER TABLE ADVERT
	ADD COLUMN currency_at_locale VARCHAR(10) AFTER currency,
	ADD INDEX (currency),
	ADD INDEX (currency_at_locale)
;

ALTER TABLE APPLICATION
	ADD COLUMN confirmed_start_date DATE AFTER rating_average,
	ADD COLUMN confirmed_supervisor_user_id INT(10) UNSIGNED AFTER confirmed_start_date,
	ADD COLUMN confirmed_offer_type VARCHAR(50) AFTER confirmed_supervisor_user_id,
	ADD INDEX (confirmed_start_date, sequence_identifier),
	ADD INDEX (confirmed_supervisor_user_id, sequence_identifier),
	ADD INDEX (confirmed_offer_type, sequence_identifier),
	ADD FOREIGN KEY (confirmed_supervisor_user_id) REFERENCES USER (id)
;

UPDATE APPLICATION INNER JOIN (
	SELECT COMMENT.application_id AS application_id,
		COMMENT.id AS comment_id,
		COMMENT.application_position_provisional_start_date AS confirmed_start_date,
		COMMENT_ASSIGNED_USER.user_id AS confirmed_supervisor_user_id,
		IF(COMMENT.application_appointment_conditions IS NOT NULL,
			"CONDITIONAL",
			"UNCONDITIONAL") AS confirmed_offer_type
	FROM COMMENT INNER JOIN (
		SELECT COMMENT.application_id AS application_id,
			MAX(COMMENT.created_timestamp) AS created_timestamp
		FROM COMMENT
		WHERE COMMENT.action_id = "APPLICATION_CONFIRM_OFFER_RECOMMENDATION"
		GROUP BY COMMENT.application_id) AS CONFIRMATION
		ON COMMENT.application_id = CONFIRMATION.application_id
			AND COMMENT.created_timestamp = CONFIRMATION.created_timestamp
			AND COMMENT.action_id = "APPLICATION_CONFIRM_OFFER_RECOMMENDATION"
	INNER JOIN COMMENT_ASSIGNED_USER
		ON COMMENT.id = COMMENT_ASSIGNED_USER.comment_id
	AND COMMENT_ASSIGNED_USER.role_id = "APPLICATION_PRIMARY_SUPERVISOR") AS OFFER_SUMMARY
	ON APPLICATION.id = OFFER_SUMMARY.application_id
SET APPLICATION.confirmed_start_date = OFFER_SUMMARY.confirmed_start_date,
	APPLICATION.confirmed_supervisor_user_id = OFFER_SUMMARY.confirmed_supervisor_user_id,
	APPLICATION.confirmed_offer_type = OFFER_SUMMARY.confirmed_offer_type
;

ALTER TABLE ADVERT
	MODIFY sequence_identifier VARCHAR(25)
;

ALTER TABLE ACTION	
	ADD COLUMN rating_action INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER action_category,
	ADD COLUMN transition_action INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER rating_action
;

ALTER TABLE ACTION
	MODIFY COLUMN rating_action INT(1) UNSIGNED NOT NULL,
	MODIFY COLUMN transition_action INT(1) UNSIGNED NOT NULL
;

ALTER TABLE APPLICATION_PROCESSING_SUMMARY
	ADD COLUMN instance_count_average DECIMAL(10,2) UNSIGNED AFTER instance_sum_live,
	ADD COLUMN day_duration_sum_average DECIMAL(10,2) UNSIGNED AFTER instance_count_percentile_95,
	DROP INDEX instance_count_percentile_05,
	DROP INDEX instance_count_percentile_20,
	DROP INDEX instance_count_percentile_35,
	DROP INDEX instance_count_percentile_50,
	DROP INDEX instance_count_percentile_65,
	DROP INDEX instance_count_percentile_80,
	DROP INDEX instance_count_percentile_95,
	DROP INDEX day_duration_sum_percentile_05,
	DROP INDEX day_duration_sum_percentile_20,
	DROP INDEX day_duration_sum_percentile_35,
	DROP INDEX day_duration_sum_percentile_50,
	DROP INDEX day_duration_sum_percentile_65,
	DROP INDEX day_duration_sum_percentile_80,
	DROP INDEX day_duration_sum_percentile_95,
	DROP INDEX instance_sum,
	DROP INDEX instance_sum_live
;

