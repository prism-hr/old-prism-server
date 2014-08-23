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

ALTER TABLE APPLICATION
	CHANGE COLUMN referrer_url referrer VARCHAR(255)
;

ALTER TABLE PROJECT
	ADD COLUMN referrer VARCHAR(255) AFTER program_id,
	ADD INDEX (referrer)
;

ALTER TABLE PROGRAM
	ADD COLUMN referrer VARCHAR(255) AFTER institution_id,
	ADD INDEX (referrer)
;

ALTER TABLE INSTITUTION
	ADD COLUMN referrer VARCHAR(255) AFTER system_id,
	ADD INDEX (referrer)
;

ALTER TABLE COMMENT
	DROP COLUMN application_equivalent_experience
;

ALTER TABLE APPLICATION_REFEREE
	DROP COLUMN include_in_export
;

ALTER TABLE APPLICATION_QUALIFICATION
	DROP COLUMN include_in_export
;

ALTER TABLE PROJECT
	DROP FOREIGN KEY project_ibfk_1,
	ADD COLUMN advert_id INT(10) UNSIGNED AFTER id,
	ADD INDEX (advert_id),
	ADD FOREIGN KEY (advert_id) REFERENCES ADVERT (id)
;

UPDATE PROJECT
SET advert_id = id
;

ALTER TABLE PROGRAM
	DROP FOREIGN KEY program_ibfk_2,
	ADD COLUMN advert_id INT(10) UNSIGNED AFTER id,
	ADD INDEX (advert_id),
	ADD FOREIGN KEY (advert_id) REFERENCES ADVERT (id)
;

UPDATE PROGRAM
SET advert_id = id
;

ALTER TABLE PROGRAM
	ADD COLUMN user_id INT(10) UNSIGNED AFTER advert_id,
	ADD INDEX (user_id),
	ADD FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE PROGRAM INNER JOIN ADVERT
	ON PROGRAM.id = ADVERT.id
SET PROGRAM.user_id = ADVERT.user_id
;

ALTER TABLE PROJECT
	ADD COLUMN user_id INT(10) UNSIGNED AFTER advert_id,
	ADD INDEX (user_id),
	ADD FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE PROJECT INNER JOIN ADVERT
	ON PROJECT.id = ADVERT.id
SET PROJECT.user_id = ADVERT.user_id
;

ALTER TABLE ADVERT
	DROP FOREIGN KEY fk_advert_user_id,
	DROP COLUMN user_id
;

ALTER TABLE ADVERT
	ADD COLUMN title VARCHAR(255) AFTER id,
	ADD COLUMN sequence_identifier VARCHAR(25),
	DROP INDEX institution_address_id,
	DROP INDEX publish_date,
	DROP INDEX immediate_start,
	DROP INDEX month_study_duration_minimum,
	DROP INDEX month_study_duration_maximum,
	DROP INDEX month_fee_minimum_specified,
	DROP INDEX month_fee_maximum_specified,
	DROP INDEX year_fee_minimum_specified,
	DROP INDEX year_fee_maximum_specified,
	DROP INDEX month_fee_minimum_at_locale,
	DROP INDEX month_fee_maximum_at_locale,
	DROP INDEX year_fee_minimum_at_locale,
	DROP INDEX year_fee_maximum_at_locale,
	DROP INDEX month_pay_minimum_specified,
	DROP INDEX month_pay_maximum_specified,
	DROP INDEX year_pay_minimum_specified,
	DROP INDEX year_pay_maximum_specified,
	DROP INDEX month_pay_minimum_at_locale,
	DROP INDEX month_pay_maximum_at_locale,
	DROP INDEX year_pay_minimum_at_locale,
	DROP INDEX year_pay_maximum_at_locale,
	DROP INDEX advert_closing_date_id,
	ADD INDEX (title, sequence_identifier),
	ADD INDEX (publish_date, sequence_identifier),
	ADD INDEX (immediate_start, sequence_identifier),
	ADD INDEX (institution_address_id, sequence_identifier),
	ADD INDEX (month_study_duration_minimum, sequence_identifier),
	ADD INDEX (month_study_duration_maximum, sequence_identifier),
	ADD INDEX (month_fee_minimum_specified, sequence_identifier),
	ADD INDEX (month_fee_maximum_specified, sequence_identifier),
	ADD INDEX (year_fee_minimum_specified, sequence_identifier),
	ADD INDEX (year_fee_maximum_specified, sequence_identifier),
	ADD INDEX (month_fee_minimum_at_locale, sequence_identifier),
	ADD INDEX (month_fee_maximum_at_locale, sequence_identifier),
	ADD INDEX (year_fee_minimum_at_locale, sequence_identifier),
	ADD INDEX (year_fee_maximum_at_locale, sequence_identifier),
	ADD INDEX (month_pay_minimum_specified, sequence_identifier),
	ADD INDEX (month_pay_maximum_specified, sequence_identifier),
	ADD INDEX (year_pay_minimum_specified, sequence_identifier),
	ADD INDEX (year_pay_maximum_specified, sequence_identifier),
	ADD INDEX (month_pay_minimum_at_locale, sequence_identifier),
	ADD INDEX (month_pay_maximum_at_locale, sequence_identifier),
	ADD INDEX (year_pay_minimum_at_locale, sequence_identifier),
	ADD INDEX (year_pay_maximum_at_locale, sequence_identifier),
	ADD INDEX (advert_closing_date_id, sequence_identifier)
;

UPDATE ADVERT INNER JOIN PROGRAM
	ON ADVERT.id = PROGRAM.advert_id
SET ADVERT.title = PROGRAM.title,
	ADVERT.sequence_identifier = CONCAT(PROGRAM.sequence_identifier, "-PM")
;

UPDATE ADVERT INNER JOIN PROJECT
	ON ADVERT.id = PROJECT.advert_id
SET ADVERT.title = PROJECT.title,
	ADVERT.sequence_identifier = CONCAT(PROJECT.sequence_identifier, "-PT")
;

ALTER TABLE ADVERT
	MODIFY title VARCHAR(255) NOT NULL,
	MODIFY sequence_identifier VARCHAR(25) NOT NULL
;

DROP TABLE PROGRAM_EXPORT_PROGRAM
;

DROP TABLE PROGRAM_EXPORT
;

ALTER TABLE APPLICATION
	ADD COLUMN advert_id INT(10) UNSIGNED AFTER project_id,
	ADD INDEX (advert_id, sequence_identifier),
	ADD FOREIGN KEY (advert_id) REFERENCES ADVERT (id)
;

UPDATE APPLICATION INNER JOIN PROGRAM
	ON APPLICATION.program_id = PROGRAM.id
SET APPLICATION.advert_id = PROGRAM.advert_id
;

UPDATE APPLICATION INNER JOIN PROJECT
	ON APPLICATION.project_id = PROJECT.id
SET APPLICATION.advert_id = PROJECT.advert_id
;

ALTER TABLE APPLICATION
	MODIFY COLUMN advert_id INT(10) UNSIGNED NOT NULL
;
