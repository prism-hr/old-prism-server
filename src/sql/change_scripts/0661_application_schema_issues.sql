/* Bugs in program instance table */

ALTER TABLE PROGRAM_INSTANCE
	DROP COLUMN disabled_date
;

UPDATE PROGRAM_INSTANCE
SET identifier = IF(program_id = 33, "CUSTOM", "UNKNOWN")
WHERE identifier IS NULL
;

ALTER TABLE PROGRAM_INSTANCE
	MODIFY COLUMN program_id INT(10) UNSIGNED NOT NULL,
	CHANGE COLUMN identifier sequence_identifier VARCHAR(10) NOT NULL AFTER program_id
;

/* Split PRiSM system institution and reference institution */

CREATE TABLE IMPORTED_INSTITUTION LIKE INSTITUTION
;

INSERT INTO IMPORTED_INSTITUTION
	SELECT *
	FROM INSTITUTION
;

ALTER TABLE IMPORTED_INSTITUTION
	DROP COLUMN state_id,
	DROP COLUMN system_id
;

CREATE TABLE INSTITUTION_DOMICILE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	code VARCHAR(10) NOT NULL,
	name VARCHAR(100) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (code),
	UNIQUE INDEX (name)
) ENGINE = INNODB
	SELECT id, code, name
	FROM IMPORTED_ENTITY
	WHERE imported_entity_type_id = "DOMICILE"
;

ALTER TABLE INSTITUTION
	DROP FOREIGN KEY institution_ibfk_3,
	CHANGE COLUMN domicile_id institution_domicile_id INT(10) UNSIGNED NOT NULL,
	ADD FOREIGN KEY (institution_domicile_id) REFERENCES INSTITUTION_DOMICILE (id)
;

ALTER TABLE IMPORTED_INSTITUTION
	ADD COLUMN institution_id INT(10) UNSIGNED NOT NULL DEFAULT 5243 AFTER id,
	DROP INDEX domicile_id,
	DROP INDEX code,
	DROP INDEX name_idx,
	ADD UNIQUE INDEX (institution_id, domicile_id, code),
	ADD UNIQUE INDEX (institution_id, domicile_id, name),
	ADD INDEX (domicile_id),
	ADD FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	ADD FOREIGN KEY (domicile_id) REFERENCES IMPORTED_ENTITY (id)
;

ALTER TABLE INSTITUTION
	DROP INDEX name_idx
;

INSERT INTO IMPORTED_ENTITY_TYPE
VALUES ("INSTITUTION")
;

ALTER TABLE IMPORTED_ENTITY
	DROP INDEX enabled,
	ADD INDEX (institution_id, imported_entity_type_id, enabled)
;

ALTER TABLE IMPORTED_INSTITUTION
	ADD COLUMN enabled INT(1) UNSIGNED
;

UPDATE IMPORTED_INSTITUTION
SET enabled = 1
;

ALTER TABLE IMPORTED_INSTITUTION
	MODIFY COLUMN enabled INT(1) UNSIGNED NOT NULL,
	ADD INDEX (institution_id, domicile_id, enabled)
;

/* Application withdrawn comments */

INSERT INTO COMMENT (application_id, action_id, user_id, role_id, created_timestamp, transition_state_id)
	SELECT APPLICATION.id, "APPLICATION_WITHDRAW", APPLICATION.user_id, "APPLICATION_CREATOR", MAX(EVENT.event_date), "APPLICATION_WITHDRAWN_COMPLETED"
	FROM APPLICATION INNER JOIN EVENT
		ON APPLICATION.id = EVENT.application_form_id
	WHERE APPLICATION.state_id LIKE "APPLICATION_WITHDRAWN%"
		AND APPLICATION.submitted_timestamp IS NULL
	GROUP BY APPLICATION.id
		UNION
	SELECT APPLICATION.id, "APPLICATION_WITHDRAW", APPLICATION.user_id, "APPLICATION_CREATOR", MAX(EVENT.event_date), "APPLICATION_WITHDRAWN"
	FROM APPLICATION INNER JOIN EVENT
		ON APPLICATION.id = EVENT.application_form_id
	WHERE APPLICATION.state_id LIKE "APPLICATION_WITHDRAWN%"
		AND APPLICATION.submitted_timestamp IS NOT NULL
	GROUP BY APPLICATION.id
;

/* Application assess eligibility comments */

ALTER TABLE COMMENT
	ADD COLUMN application_qualified VARCHAR(30),
	ADD COLUMN application_competent_in_work_language VARCHAR(30),
	ADD COLUMN application_residence_status VARCHAR(30)
;

UPDATE COMMENT INNER JOIN VALIDATION_COMMENT
	ON COMMENT.id = VALIDATION_COMMENT.id
SET COMMENT.action_id = "APPLICATION_ASSESS_ELIGIBILITY",
	COMMENT.role_id = "APPLICATION_ADMINISTRATOR",
	COMMENT.transition_state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION",
	COMMENT.application_qualified = VALIDATION_COMMENT.qualified_for_phd,
	COMMENT.application_competent_in_work_language = VALIDATION_COMMENT.english_compentency_ok,
	COMMENT.application_residence_status = VALIDATION_COMMENT.home_or_overseas
;

DROP TABLE VALIDATION_COMMENT
;

/* Rearrange comment table */

ALTER TABLE COMMENT
	MODIFY COLUMN action_id VARCHAR(100) AFTER delegate_role_id
;

/* Application confirm eligibility comments */

UPDATE COMMENT INNER JOIN ADMITTER_COMMENT
	ON COMMENT.id = ADMITTER_COMMENT.id
SET COMMENT.action_id = "APPLICATION_CONFIRM_ELIGIBILITY",
	COMMENT.role_id = "INSTITUTION_ADMITTER",
	COMMENT.application_qualified = ADMITTER_COMMENT.qualified_for_phd,
	COMMENT.application_competent_in_work_language = ADMITTER_COMMENT.english_compentency_ok,
	COMMENT.application_residence_status = ADMITTER_COMMENT.home_or_overseas
;

DROP TABLE ADMITTER_COMMENT
;

/* Application complete validation comments */

INSERT INTO COMMENT (application_id, action_id, user_id, role_id, content, created_timestamp, transition_state_id)
	SELECT COMMENT.application_id, "APPLICATION_COMPLETE_VALIDATION_STAGE", COMMENT.user_id, COMMENT.role_id, COMMENT.content, COMMENT.created_timestamp, 
	CONCAT("APPLICATION_", STATECHANGE_COMMENT.next_status)
	FROM COMMENT INNER JOIN STATECHANGE_COMMENT
		ON COMMENT.id = STATECHANGE_COMMENT.id
	WHERE COMMENT.action_id = "APPLICATION_ASSESS_ELIGIBILITY"
;
	
ALTER TABLE COMMENT_ASSIGNED_USER
	ADD COLUMN comment_custom_question_id INT(10) UNSIGNED,
	ADD INDEX (comment_custom_question_id),
	ADD FOREIGN KEY (comment_custom_question_id) REFERENCES COMMENT_CUSTOM_QUESTION (id)
;

ALTER TABLE COMMENT_CUSTOM_QUESTION
	CHANGE COLUMN stage action_id VARCHAR(100) NOT NULL
;

UPDATE COMMENT_CUSTOM_QUESTION
SET action_id = "APPLICATION_PROVIDE_REVIEW"
WHERE action_id = "REVIEW"
;

UPDATE COMMENT_CUSTOM_QUESTION
SET action_id = "APPLICATION_PROVIDE_INTERVIEW_FEEDBACK"
WHERE action_id = "INTERVIEW"
;

UPDATE COMMENT_CUSTOM_QUESTION
SET action_id = "APPLICATION_PROVIDE_REFERENCE"
WHERE action_id = "REFERENCE"
;

ALTER TABLE COMMENT_CUSTOM_QUESTION
	ADD INDEX (action_id),
	ADD FOREIGN KEY (action_id) REFERENCES ACTION (id),
	ADD UNIQUE INDEX (program_id, action_id),
	DROP INDEX id,
	DROP INDEX program_fk
;

CREATE TABLE COMMENT_CUSTOM_QUESTION_TYPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
	SELECT question_type AS id
	FROM COMMENT_CUSTOM_QUESTION_RESPONSE
	GROUP BY (question_type)
;

ALTER TABLE COMMENT_CUSTOM_QUESTION_RESPONSE
	DROP FOREIGN KEY comment_fk,
	MODIFY COLUMN comment_id INT(10) UNSIGNED NOT NULL,
	CHANGE COLUMN question_type comment_custom_question_type_id VARCHAR(50) NOT NULL,
	CHANGE COLUMN score_position display_order INT(3) UNSIGNED NOT NULL,
	MODIFY COLUMN question LONGTEXT,
	ADD INDEX (comment_custom_question_type_id),
	ADD FOREIGN KEY (comment_custom_question_type_id) REFERENCES COMMENT_CUSTOM_QUESTION_TYPE (id),
	ADD FOREIGN KEY (comment_id) REFERENCES COMMENT (id),
	MODIFY COLUMN rating_response INT(1) UNSIGNED
;

RENAME TABLE COMMENT_CUSTOM_QUESTION TO COMMENT_CUSTOM_QUESTION_VERSION
;

CREATE TABLE COMMENT_CUSTOM_QUESTION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	program_id INT(10) UNSIGNED NULL DEFAULT NULL,
	action_id VARCHAR(100) NOT NULL,
	comment_custom_question_version_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (program_id, action_id, comment_custom_question_version_id),
	INDEX (action_id),
	INDEX (comment_custom_question_version_id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id),
	FOREIGN KEY (comment_custom_question_version_id) REFERENCES COMMENT_CUSTOM_QUESTION_VERSION (id)
)	ENGINE = INNODB
	SELECT NULL AS id, program_id AS program_id, action_id AS action_id, id AS comment_custom_question_version_id
	FROM COMMENT_CUSTOM_QUESTION_VERSION
;

ALTER TABLE COMMENT_CUSTOM_QUESTION_VERSION
	ADD COLUMN comment_custom_question_id INT(10) UNSIGNED AFTER id,
	ADD INDEX (comment_custom_question_id),
	ADD FOREIGN KEY (comment_custom_question_id) REFERENCES COMMENT_CUSTOM_QUESTION (id)
;

UPDATE COMMENT_CUSTOM_QUESTION INNER JOIN COMMENT_CUSTOM_QUESTION_VERSION
	ON COMMENT_CUSTOM_QUESTION.program_id = COMMENT_CUSTOM_QUESTION_VERSION.program_id
	AND COMMENT_CUSTOM_QUESTION.action_id = COMMENT_CUSTOM_QUESTION_VERSION.action_id
SET COMMENT_CUSTOM_QUESTION_VERSION.comment_custom_question_id = COMMENT_CUSTOM_QUESTION.id
;

ALTER TABLE COMMENT_CUSTOM_QUESTION_VERSION
	MODIFY COLUMN comment_custom_question_id INT(10) UNSIGNED NOT NULL,
	DROP FOREIGN KEY comment_custom_question_version_ibfk_1,
	DROP FOREIGN KEY program_fk,
	DROP COLUMN action_id,
	DROP COLUMN program_id
;

/* Fix not null constraints on comment table */