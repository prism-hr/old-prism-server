ALTER TABLE PROJECT
	ADD COLUMN application_created_count INT(10) UNSIGNED AFTER title,
	ADD COLUMN application_approved_count INT(10) UNSIGNED AFTER application_submitted_count,
	ADD COLUMN application_rejected_count INT(10) UNSIGNED AFTER application_approved_count,
	ADD COLUMN application_withdrawn_count INT(10) UNSIGNED AFTER application_rejected_count,
	ADD COLUMN application_rating_count_average DECIMAL(10, 2) AFTER application_withdrawn_count,
	ADD COLUMN application_rating_average DECIMAL(3,2) AFTER application_rating_count_average,
	ADD INDEX (application_created_count, sequence_identifier),
	ADD INDEX (application_approved_count, sequence_identifier),
	ADD INDEX (application_rejected_count, sequence_identifier),
	ADD INDEX (application_withdrawn_count, sequence_identifier),
	ADD INDEX (application_rating_count_average, sequence_identifier),
	ADD INDEX (application_rating_average, sequence_identifier)
;

ALTER TABLE PROGRAM
	ADD COLUMN application_created_count INT(10) UNSIGNED AFTER imported,
	ADD COLUMN application_approved_count INT(10) UNSIGNED AFTER application_submitted_count,
	ADD COLUMN application_rejected_count INT(10) UNSIGNED AFTER application_approved_count,
	ADD COLUMN application_withdrawn_count INT(10) UNSIGNED AFTER application_rejected_count,
	ADD COLUMN application_rating_count_average DECIMAL(10, 2) AFTER application_withdrawn_count,
	ADD COLUMN application_rating_average DECIMAL(3,2) AFTER application_rating_count_average,
	ADD INDEX (application_created_count, sequence_identifier),
	ADD INDEX (application_approved_count, sequence_identifier),
	ADD INDEX (application_rejected_count, sequence_identifier),
	ADD INDEX (application_withdrawn_count, sequence_identifier),
	ADD INDEX (application_rating_count_average, sequence_identifier),
	ADD INDEX (application_rating_average, sequence_identifier)
;

ALTER TABLE INSTITUTION
	ADD COLUMN application_created_count INT(10) UNSIGNED AFTER is_ucl_institution,
	ADD COLUMN application_approved_count INT(10) UNSIGNED AFTER application_submitted_count,
	ADD COLUMN application_rejected_count INT(10) UNSIGNED AFTER application_approved_count,
	ADD COLUMN application_withdrawn_count INT(10) UNSIGNED AFTER application_rejected_count,
	ADD COLUMN application_rating_count_average DECIMAL(10, 2) AFTER application_withdrawn_count,
	ADD COLUMN application_rating_average DECIMAL(3,2) AFTER application_rating_count_average,
	ADD INDEX (application_created_count, sequence_identifier),
	ADD INDEX (application_approved_count, sequence_identifier),
	ADD INDEX (application_rejected_count, sequence_identifier),
	ADD INDEX (application_withdrawn_count, sequence_identifier),
	ADD INDEX (application_rating_count_average, sequence_identifier),
	ADD INDEX (application_rating_average, sequence_identifier)
;

ALTER TABLE APPLICATION
	DROP FOREIGN KEY application_ibfk_7,
	CHANGE COLUMN confirmed_supervisor_user_id confirmed_primary_supervisor_id INT(10) UNSIGNED,
	ADD FOREIGN KEY (confirmed_primary_supervisor_id) REFERENCES USER (id),
	ADD COLUMN confirmed_secondary_supervisor_id INT(10) UNSIGNED AFTER confirmed_primary_supervisor_id,
	ADD INDEX (confirmed_secondary_supervisor_id, sequence_identifier),
	ADD FOREIGN KEY (confirmed_secondary_supervisor_id) REFERENCES USER (id)
;

UPDATE APPLICATION INNER JOIN (
	SELECT COMMENT.application_id AS application_id,
		COMMENT.id AS comment_id,
		COMMENT_ASSIGNED_USER.user_id AS confirmed_secondary_supervisor_id
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
	AND COMMENT_ASSIGNED_USER.role_id = "APPLICATION_SECONDARY_SUPERVISOR") AS OFFER_SUMMARY
	ON APPLICATION.id = OFFER_SUMMARY.application_id
SET APPLICATION.confirmed_secondary_supervisor_id = OFFER_SUMMARY.confirmed_secondary_supervisor_id
;

UPDATE PROJECT INNER JOIN (
	SELECT COUNT(id) AS value,
		project_id AS project_id
	FROM APPLICATION
	GROUP BY project_id) AS APPLICATION_COUNT
	ON PROJECT.id = APPLICATION_COUNT.project_id
SET application_created_count = APPLICATION_COUNT.value
;

UPDATE PROJECT INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.project_id AS project_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_APPROVED"
		AND STATE.id != "APPLICATION_APPROVED"
	GROUP BY project_id) AS APPLICATION_COUNT
	ON PROJECT.id = APPLICATION_COUNT.project_id
SET application_approved_count = APPLICATION_COUNT.value
;

UPDATE PROJECT INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.project_id AS project_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_REJECTED"
		AND STATE.id != "APPLICATION_REJECTED"
	GROUP BY project_id) AS APPLICATION_COUNT
	ON PROJECT.id = APPLICATION_COUNT.project_id
SET application_rejected_count = APPLICATION_COUNT.value
;

UPDATE PROJECT INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.project_id AS project_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_WITHDRAWN"
	GROUP BY project_id) AS APPLICATION_COUNT
	ON PROJECT.id = APPLICATION_COUNT.project_id
SET application_withdrawn_count = APPLICATION_COUNT.value
;

UPDATE PROGRAM INNER JOIN (
	SELECT COUNT(id) AS value,
		program_id AS program_id
	FROM APPLICATION
	GROUP BY program_id) AS APPLICATION_COUNT
	ON PROGRAM.id = APPLICATION_COUNT.program_id
SET application_created_count = APPLICATION_COUNT.value
;

UPDATE PROGRAM INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.program_id AS program_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_APPROVED"
		AND STATE.id != "APPLICATION_APPROVED"
	GROUP BY program_id) AS APPLICATION_COUNT
	ON PROGRAM.id = APPLICATION_COUNT.program_id
SET application_approved_count = APPLICATION_COUNT.value
;

UPDATE PROGRAM INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.program_id AS program_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_REJECTED"
		AND STATE.id != "APPLICATION_REJECTED"
	GROUP BY program_id) AS APPLICATION_COUNT
	ON PROGRAM.id = APPLICATION_COUNT.program_id
SET application_rejected_count = APPLICATION_COUNT.value
;

UPDATE PROGRAM INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.program_id AS program_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_WITHDRAWN"
	GROUP BY program_id) AS APPLICATION_COUNT
	ON PROGRAM.id = APPLICATION_COUNT.program_id
SET application_withdrawn_count = APPLICATION_COUNT.value
;

UPDATE INSTITUTION INNER JOIN (
	SELECT COUNT(id) AS value,
		institution_id AS institution_id
	FROM APPLICATION
	GROUP BY institution_id) AS APPLICATION_COUNT
	ON INSTITUTION.id = APPLICATION_COUNT.institution_id
SET application_created_count = APPLICATION_COUNT.value
;

UPDATE INSTITUTION INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.institution_id AS institution_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_APPROVED"
		AND STATE.id != "APPLICATION_APPROVED"
	GROUP BY institution_id) AS APPLICATION_COUNT
	ON INSTITUTION.id = APPLICATION_COUNT.institution_id
SET application_approved_count = APPLICATION_COUNT.value
;

UPDATE INSTITUTION INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.institution_id AS institution_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_REJECTED"
		AND STATE.id != "APPLICATION_REJECTED"
	GROUP BY institution_id) AS APPLICATION_COUNT
	ON INSTITUTION.id = APPLICATION_COUNT.institution_id
SET application_rejected_count = APPLICATION_COUNT.value
;

UPDATE INSTITUTION INNER JOIN (
	SELECT COUNT(APPLICATION.id) AS value,
		APPLICATION.institution_id AS institution_id
	FROM APPLICATION INNER JOIN STATE
		ON APPLICATION.state_id = STATE.id
	WHERE STATE.state_group_id = "APPLICATION_WITHDRAWN"
	GROUP BY institution_id) AS APPLICATION_COUNT
	ON INSTITUTION.id = APPLICATION_COUNT.institution_id
SET application_withdrawn_count = APPLICATION_COUNT.value
;

UPDATE PROJECT INNER JOIN (
	SELECT project_id AS project_id,
		ROUND(AVG(rating_count), 2) AS rating_count_average,
		ROUND(AVG(rating_average), 2) AS rating_average
	FROM APPLICATION
	GROUP BY project_id) AS AVERAGE
	ON PROJECT.id = AVERAGE.project_id
SET PROJECT.application_rating_count_average = AVERAGE.rating_count_average,
	PROJECT.application_rating_average = AVERAGE.rating_average
;

UPDATE PROGRAM INNER JOIN (
	SELECT program_id AS program_id,
		ROUND(AVG(rating_count), 2) AS rating_count_average,
		ROUND(AVG(rating_average), 2) AS rating_average
	FROM APPLICATION
	GROUP BY program_id) AS AVERAGE
	ON PROGRAM.id = AVERAGE.program_id
SET PROGRAM.application_rating_count_average = AVERAGE.rating_count_average,
	PROGRAM.application_rating_average = AVERAGE.rating_average
;

UPDATE INSTITUTION INNER JOIN (
	SELECT institution_id AS institution_id,
		ROUND(AVG(rating_count), 2) AS rating_count_average,
		ROUND(AVG(rating_average), 2) AS rating_average
	FROM APPLICATION
	GROUP BY institution_id) AS AVERAGE
	ON INSTITUTION.id = AVERAGE.institution_id
SET INSTITUTION.application_rating_count_average = AVERAGE.rating_count_average,
	INSTITUTION.application_rating_average = AVERAGE.rating_average
;

ALTER TABLE COMMENT
	DROP FOREIGN KEY comment_ibfk_10,
	DROP INDEX action_on_parent_resource_id,
	CHANGE COLUMN action_on_parent_resource_id parent_resource_transition_state_id VARCHAR(50),
	ADD INDEX (parent_resource_transition_state_id),
	ADD FOREIGN KEY (parent_resource_transition_state_id) REFERENCES STATE (id)
;

UPDATE COMMENT INNER JOIN APPLICATION
	ON COMMENT.application_id = APPLICATION.id
INNER JOIN PROGRAM
	ON APPLICATION.program_id = PROGRAM.id
LEFT JOIN PROJECT
	ON APPLICATION.project_id = PROJECT.id
SET COMMENT.parent_resource_transition_state_id =
	IF(PROJECT.id IS NOT NULL,
		PROJECT.state_id,
		PROGRAM.state_id)
WHERE COMMENT.action_id = "APPLICATION_CONFIRM_OFFER_RECOMMENDATION"
;

ALTER TABLE APPLICATION
	ADD INDEX (institution_id, rating_count),
	ADD INDEX (institution_id, rating_average),
	ADD INDEX (program_id, rating_count),
	ADD INDEX (program_id, rating_average),
	ADD INDEX (project_id, rating_count),
	ADD INDEX (project_id, rating_average)
;

ALTER TABLE APPLICATION_PROCESSING_SUMMARY
	DROP COLUMN instance_count_percentile_05,
	DROP COLUMN instance_count_percentile_20,
	DROP COLUMN instance_count_percentile_35,
	DROP COLUMN instance_count_percentile_50,
	DROP COLUMN instance_count_percentile_65,
	DROP COLUMN instance_count_percentile_80,
	DROP COLUMN instance_count_percentile_95,
	DROP COLUMN day_duration_sum_percentile_05,
	DROP COLUMN day_duration_sum_percentile_20,
	DROP COLUMN day_duration_sum_percentile_35,
	DROP COLUMN day_duration_sum_percentile_50,
	DROP COLUMN day_duration_sum_percentile_65,
	DROP COLUMN day_duration_sum_percentile_80,
	DROP COLUMN day_duration_sum_percentile_95
;

ALTER TABLE INSTITUTION
	DROP INDEX application_rating_count_percentile_05,
	DROP COLUMN application_rating_count_percentile_05,
	DROP INDEX application_rating_count_percentile_20,
	DROP COLUMN application_rating_count_percentile_20,
	DROP INDEX application_rating_count_percentile_35,
	DROP COLUMN application_rating_count_percentile_35,
	DROP INDEX application_rating_count_percentile_50,
	DROP COLUMN application_rating_count_percentile_50,
	DROP INDEX application_rating_count_percentile_65,
	DROP COLUMN application_rating_count_percentile_65,
	DROP INDEX application_rating_count_percentile_80,
	DROP COLUMN application_rating_count_percentile_80,
	DROP INDEX application_rating_count_percentile_95,
	DROP COLUMN application_rating_count_percentile_95,
	DROP INDEX applicant_rating_percentile_05,
	DROP COLUMN application_rating_average_percentile_05,
	DROP INDEX applicant_rating_percentile_20,
	DROP COLUMN application_rating_average_percentile_20,
	DROP INDEX applicant_rating_percentile_35,
	DROP COLUMN application_rating_average_percentile_35,
	DROP INDEX applicant_rating_percentile_50,
	DROP COLUMN application_rating_average_percentile_50,
	DROP INDEX applicant_rating_percentile_65,
	DROP COLUMN application_rating_average_percentile_65,
	DROP INDEX applicant_rating_percentile_80,
	DROP COLUMN application_rating_average_percentile_80,
	DROP INDEX applicant_rating_percentile_95,
	DROP COLUMN application_rating_average_percentile_95
;

ALTER TABLE PROGRAM
	DROP INDEX application_rating_count_percentile_05,
	DROP COLUMN application_rating_count_percentile_05,
	DROP INDEX application_rating_count_percentile_20,
	DROP COLUMN application_rating_count_percentile_20,
	DROP INDEX application_rating_count_percentile_35,
	DROP COLUMN application_rating_count_percentile_35,
	DROP INDEX application_rating_count_percentile_50,
	DROP COLUMN application_rating_count_percentile_50,
	DROP INDEX application_rating_count_percentile_65,
	DROP COLUMN application_rating_count_percentile_65,
	DROP INDEX application_rating_count_percentile_80,
	DROP COLUMN application_rating_count_percentile_80,
	DROP INDEX application_rating_count_percentile_95,
	DROP COLUMN application_rating_count_percentile_95,
	DROP INDEX applicant_rating_percentile_05,
	DROP COLUMN application_rating_average_percentile_05,
	DROP INDEX applicant_rating_percentile_20,
	DROP COLUMN application_rating_average_percentile_20,
	DROP INDEX applicant_rating_percentile_35,
	DROP COLUMN application_rating_average_percentile_35,
	DROP INDEX applicant_rating_percentile_50,
	DROP COLUMN application_rating_average_percentile_50,
	DROP INDEX applicant_rating_percentile_65,
	DROP COLUMN application_rating_average_percentile_65,
	DROP INDEX applicant_rating_percentile_80,
	DROP COLUMN application_rating_average_percentile_80,
	DROP INDEX applicant_rating_percentile_95,
	DROP COLUMN application_rating_average_percentile_95
;

ALTER TABLE PROJECT
	DROP INDEX application_rating_count_percentile_05,
	DROP COLUMN application_rating_count_percentile_05,
	DROP INDEX application_rating_count_percentile_20,
	DROP COLUMN application_rating_count_percentile_20,
	DROP INDEX application_rating_count_percentile_35,
	DROP COLUMN application_rating_count_percentile_35,
	DROP INDEX application_rating_count_percentile_50,
	DROP COLUMN application_rating_count_percentile_50,
	DROP INDEX application_rating_count_percentile_65,
	DROP COLUMN application_rating_count_percentile_65,
	DROP INDEX application_rating_count_percentile_80,
	DROP COLUMN application_rating_count_percentile_80,
	DROP INDEX application_rating_count_percentile_95,
	DROP COLUMN application_rating_count_percentile_95,
	DROP INDEX applicant_rating_percentile_05,
	DROP COLUMN application_rating_average_percentile_05,
	DROP INDEX applicant_rating_percentile_20,
	DROP COLUMN application_rating_average_percentile_20,
	DROP INDEX applicant_rating_percentile_35,
	DROP COLUMN application_rating_average_percentile_35,
	DROP INDEX applicant_rating_percentile_50,
	DROP COLUMN application_rating_average_percentile_50,
	DROP INDEX applicant_rating_percentile_65,
	DROP COLUMN application_rating_average_percentile_65,
	DROP INDEX applicant_rating_percentile_80,
	DROP COLUMN application_rating_average_percentile_80,
	DROP INDEX applicant_rating_percentile_95,
	DROP COLUMN application_rating_average_percentile_95
;

ALTER TABLE APPLICATION_PROCESSING_SUMMARY
	CHANGE COLUMN day_duration_sum_average day_duration_average DECIMAL(10,2) UNSIGNED,
	CHANGE COLUMN instance_sum instance_count INT(10) UNSIGNED NOT NULL,
	CHANGE COLUMN instance_sum_live instance_count_live INT(10) UNSIGNED NOT NULL,
	CHANGE COLUMN instance_count_average instance_count_average_non_zero DECIMAL(10,2) UNSIGNED NOT NULL
;


UPDATE APPLICATION_PROCESSING_SUMMARY
SET day_duration_average = NULL
WHERE day_duration_average = 0.00
;

ALTER TABLE APPLICATION_PROCESSING
	DROP INDEX instance_count,
	DROP INDEX day_duration_sum,
	ADD COLUMN day_duration_average DECIMAL(10,2) UNSIGNED AFTER instance_count
;

UPDATE APPLICATION_PROCESSING
SET day_duration_average = ROUND(day_duration_sum / instance_count, 2)
WHERE day_duration_sum != 0;
;

ALTER TABLE APPLICATION_PROCESSING
	DROP COLUMN day_duration_sum
;

ALTER TABLE COMMENT
	DROP INDEX application_id
;

UPDATE APPLICATION_PROCESSING_SUMMARY INNER JOIN (
	SELECT APPLICATION.project_id AS project_id, 
		APPLICATION_PROCESSING.state_group_id AS state_group_id,
		ROUND(SUM(APPLICATION_PROCESSING.day_duration_average) / SUM(APPLICATION_PROCESSING.instance_count), 2) AS day_duration_average
	FROM APPLICATION INNER JOIN APPLICATION_PROCESSING
		ON APPLICATION.id = APPLICATION_PROCESSING.application_id
	WHERE APPLICATION.project_id IS NOT NULL
		AND APPLICATION_PROCESSING.day_duration_average IS NOT NULL
	GROUP BY APPLICATION.project_id, APPLICATION_PROCESSING.state_group_id) AS PROJECT_SUMMARY
	ON APPLICATION_PROCESSING_SUMMARY.project_id = PROJECT_SUMMARY.project_id
	AND APPLICATION_PROCESSING_SUMMARY.state_group_id = PROJECT_SUMMARY.state_group_id
SET APPLICATION_PROCESSING_SUMMARY.day_duration_average = PROJECT_SUMMARY.day_duration_average
;

UPDATE APPLICATION_PROCESSING_SUMMARY INNER JOIN (
	SELECT APPLICATION.program_id AS program_id, 
		APPLICATION_PROCESSING.state_group_id AS state_group_id,
		ROUND(SUM(APPLICATION_PROCESSING.day_duration_average) / SUM(APPLICATION_PROCESSING.instance_count), 2) AS day_duration_average
	FROM APPLICATION INNER JOIN APPLICATION_PROCESSING
		ON APPLICATION.id = APPLICATION_PROCESSING.application_id
	WHERE APPLICATION.program_id IS NOT NULL
		AND APPLICATION_PROCESSING.day_duration_average IS NOT NULL
	GROUP BY APPLICATION.program_id, APPLICATION_PROCESSING.state_group_id) AS PROGRAM_SUMMARY
	ON APPLICATION_PROCESSING_SUMMARY.program_id = PROGRAM_SUMMARY.program_id
	AND APPLICATION_PROCESSING_SUMMARY.state_group_id = PROGRAM_SUMMARY.state_group_id
SET APPLICATION_PROCESSING_SUMMARY.day_duration_average = PROGRAM_SUMMARY.day_duration_average
;

UPDATE APPLICATION_PROCESSING_SUMMARY INNER JOIN (
	SELECT APPLICATION.institution_id AS institution_id, 
		APPLICATION_PROCESSING.state_group_id AS state_group_id,
		ROUND(SUM(APPLICATION_PROCESSING.day_duration_average) / SUM(APPLICATION_PROCESSING.instance_count), 2) AS day_duration_average
	FROM APPLICATION INNER JOIN APPLICATION_PROCESSING
		ON APPLICATION.id = APPLICATION_PROCESSING.application_id
	WHERE APPLICATION.institution_id IS NOT NULL
		AND APPLICATION_PROCESSING.day_duration_average IS NOT NULL
	GROUP BY APPLICATION.institution_id, APPLICATION_PROCESSING.state_group_id) AS INSTITUTION_SUMMARY
	ON APPLICATION_PROCESSING_SUMMARY.institution_id = INSTITUTION_SUMMARY.institution_id
	AND APPLICATION_PROCESSING_SUMMARY.state_group_id = INSTITUTION_SUMMARY.state_group_id
SET APPLICATION_PROCESSING_SUMMARY.day_duration_average = INSTITUTION_SUMMARY.day_duration_average
;

ALTER TABLE PROJECT
	CHANGE COLUMN application_rating_count_average application_rating_count_average_non_zero DECIMAL(10,2),
	ADD COLUMN application_rating_count INT(10) UNSIGNED AFTER application_withdrawn_count
;

UPDATE PROJECT INNER JOIN (
	SELECT project_id AS project_id, 
		SUM(rating_count) AS rating_count
	FROM APPLICATION
	GROUP by project_id) AS PROJECT_SUMMARY
	ON PROJECT.id = PROJECT_SUMMARY.project_id
SET PROJECT.application_rating_count = PROJECT_SUMMARY.rating_count
;

ALTER TABLE PROGRAM
	CHANGE COLUMN application_rating_count_average application_rating_count_average_non_zero DECIMAL(10,2),
	ADD COLUMN application_rating_count INT(10) UNSIGNED AFTER application_withdrawn_count
;

UPDATE PROGRAM INNER JOIN (
	SELECT program_id AS program_id, 
		SUM(rating_count) AS rating_count
	FROM APPLICATION
	GROUP by program_id) AS PROGRAM_SUMMARY
	ON PROGRAM.id = PROGRAM_SUMMARY.program_id
SET PROGRAM.application_rating_count = PROGRAM_SUMMARY.rating_count
;

ALTER TABLE INSTITUTION
	CHANGE COLUMN application_rating_count_average application_rating_count_average_non_zero DECIMAL(10,2),
	ADD COLUMN application_rating_count INT(10) UNSIGNED AFTER application_withdrawn_count
;

UPDATE INSTITUTION INNER JOIN (
	SELECT institution_id AS institution_id, 
		SUM(rating_count) AS rating_count
	FROM APPLICATION
	GROUP by institution_id) AS INSTITUTION_SUMMARY
	ON INSTITUTION.id = INSTITUTION_SUMMARY.institution_id
SET INSTITUTION.application_rating_count = INSTITUTION_SUMMARY.rating_count
;

CREATE TABLE RESOURCE_LIST_FILTER (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	user_account_id INT(10) UNSIGNED NOT NULL,
	scope_id VARCHAR(50) NOT NULL,
	urgent_only INT(1) UNSIGNED NOT NULL,
	match_mode VARCHAR(10) NOT NULL,
	sort_order VARCHAR(10) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (user_account_id, scope_id),
	INDEX (scope_id),
	FOREIGN KEY (user_account_id) REFERENCES USER_ACCOUNT (id),
	FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
) ENGINE = INNODB
;

CREATE TABLE RESOURCE_LIST_FILTER_CONSTRAINT (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	resource_list_filter_id INT(10) UNSIGNED NOT NULL,
	filter_property VARCHAR(50) NOT NULL,
	filter_expression VARCHAR(50) NOT NULL,
	negated INT(1) UNSIGNED NOT NULL,
	display_position INTEGER(3) NOT NULL,
	value_string VARCHAR(50),
	value_state_group_id VARCHAR(50),
	value_date_start DATE,
	value_date_close DATE,
	value_decimal_start DECIMAL(10,2),
	value_decimal_close DECIMAL(10,2),
	PRIMARY KEY (id),
	INDEX (filter_property),
	UNIQUE INDEX (resource_list_filter_id, filter_property, filter_expression, value_string),
	UNIQUE INDEX (resource_list_filter_id, filter_property, filter_expression, value_state_group_id),
	UNIQUE INDEX (resource_list_filter_id, filter_property, filter_expression, value_date_start, value_date_close),
	UNIQUE INDEX (resource_list_filter_id, filter_property, filter_expression, value_decimal_start, value_decimal_close),
	INDEX (resource_list_filter_id, display_position),
	FOREIGN KEY (resource_list_filter_id) REFERENCES RESOURCE_LIST_FILTER (id),
	FOREIGN KEY (value_state_group_id) REFERENCES STATE_GROUP (id)
) ENGINE = INNODB 
;

DROP TABLE FILTER_CONSTRAINT
;

DROP TABLE FILTER
;

CREATE TABLE RESOURCE_LIST_FILTER_CONSTRAINT_ROLE (
	resource_list_filter_constraint_id INT(10) UNSIGNED NOT NULL,
	role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (resource_list_filter_constraint_id, role_id),
	INDEX (role_id),
	FOREIGN KEY (resource_list_filter_constraint_id) REFERENCES RESOURCE_LIST_FILTER_CONSTRAINT (id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id)
) ENGINE = INNODB
;

ALTER TABLE APPLICATION
	CHANGE COLUMN rating_count application_rating_count INT(10) UNSIGNED,
	CHANGE COLUMN rating_average application_rating_averave DECIMAL(3, 2) UNSIGNED
;

UPDATE STATE_TRANSITION
SET state_transition_evaluation = REPLACE(state_transition_evaluation, "CONFIGURED", "VIEW_EDIT"),
	state_transition_evaluation = REPLACE(state_transition_evaluation, "EVALUATED", "STATE_COMPLETED"),
	state_transition_evaluation = REPLACE(state_transition_evaluation, "REACTIVATED", "RESTORED")
;

CREATE TABLE STATE_TRANSITION_EVALUATION (
	id VARCHAR(50) NOT NULL,
	next_state_selection INT(1) UNSIGNED NOT NULL,
	scope_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	INDEX (next_state_selection),
	INDEX (scope_id),
	FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
) ENGINE = INNODB
;

INSERT INTO STATE_TRANSITION_EVALUATION (id, next_state_selection, scope_id)
	SELECT STATE_TRANSITION.state_transition_evaluation AS id,
		0 AS next_state_selection,
		STATE.scope_id AS scope_id
	FROM STATE_TRANSITION INNER JOIN STATE
		ON STATE_TRANSITION.transition_state_id = STATE.id
	WHERE STATE_TRANSITION.state_transition_evaluation IS NOT NULL
	GROUP BY STATE_TRANSITION.state_transition_evaluation
;

ALTER TABLE STATE_TRANSITION
	CHANGE COLUMN state_transition_evaluation state_transition_evaluation_id VARCHAR(50),
	ADD FOREIGN KEY (state_transition_evaluation_id) REFERENCES STATE_TRANSITION_EVALUATION(id)
;
