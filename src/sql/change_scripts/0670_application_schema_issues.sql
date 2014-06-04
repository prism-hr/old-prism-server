/* Fix inherited action - now correctly modelled as a delegate action */

DROP TABLE STATE_ACTION_INHERITANCE
;

ALTER TABLE ACTION
	ADD COLUMN delegate_action_id VARCHAR(100),
	ADD INDEX (delegate_action_id),
	ADD FOREIGN KEY (delegate_action_id) REFERENCES ACTION (id)
;

UPDATE ACTION
SET delegate_action_id = "APPLICATION_EDIT_AS_ADMINISTRATOR"
WHERE id = "APPLICATION_PROVIDE_REFERENCE"
;

/* Application missing constraints */

ALTER TABLE APPLICATION
	DROP INDEX personal_detail_id,
	ADD UNIQUE INDEX (application_personal_detail_id),
	DROP INDEX programme_details_id,
	ADD UNIQUE INDEX (application_program_detail_id),
	DROP INDEX application_form_address_id,
	ADD UNIQUE INDEX (application_address_id),
	DROP INDEX application_form_document_id,
	ADD UNIQUE INDEX (application_document_id),
	DROP INDEX additional_info_id,
	ADD UNIQUE INDEX (application_additional_information_id)
;

/* Scope  and action type tables */

CREATE TABLE SCOPE (
	id VARCHAR(50) NOT NULL,
	precedence INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (precedence)
) ENGINE = INNODB
;

INSERT INTO SCOPE (id, precedence)
VALUES ("SYSTEM", 1),
	("INSTITUTION", 2),
	("PROGRAM", 3),
	("PROJECT", 4),
	("APPLICATION", 5)
;

ALTER TABLE ROLE
	ADD COLUMN scope_id VARCHAR(50),
	ADD INDEX (scope_id),
	ADD FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
;

UPDATE ROLE INNER JOIN SCOPE
SET ROLE.scope_id = SCOPE.id
WHERE ROLE.id LIKE CONCAT(SCOPE.id, "_%")
;

ALTER TABLE ROLE
	MODIFY COLUMN scope_id VARCHAR(50) NOT NULL
;

CREATE TABLE ACTION_TYPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
;

INSERT INTO ACTION_TYPE (id)
VALUES ("USER_INVOCATION"),
	("SYSTEM_ESCALATION"),
	("SYSTEM_PROPAGATION")
;

ALTER TABLE ACTION
	ADD COLUMN action_type_id VARCHAR(50) AFTER id,
	ADD INDEX (action_type_id),
	ADD FOREIGN KEY (action_type_id) REFERENCES ACTION_TYPE (id),
	ADD COLUMN scope_id VARCHAR(50) AFTER action_type_id,
	ADD INDEX (scope_id),
	ADD FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
;

UPDATE ACTION INNER JOIN SCOPE
SET ACTION.scope_id = SCOPE.id
WHERE ACTION.id LIKE CONCAT(SCOPE.id, "_%")
;

ALTER TABLE ACTION
	MODIFY COLUMN scope_id VARCHAR(50) NOT NULL
;

UPDATE ACTION
SET action_type_id = "SYSTEM_ESCALATION"
WHERE id LIKE "%_ESCALATE"
	OR id LIKE "%_EXPORT"
;

UPDATE ACTION
SET action_type_id = "SYSTEM_PROPAGATION"
WHERE id LIKE "%_SUSPEND"
	OR id LIKE "%_RESTORE"
	OR id LIKE "%_EXPORT"
	OR id LIKE "%_TERMINATE"
	OR id LIKE "%_COMPLETE_RECRUITMENT"
	OR id LIKE "%_IMPORT_"
;

UPDATE ACTION
SET action_type_id = "USER_INVOCATION"
WHERE action_type_id IS NULL
;

ALTER TABLE ACTION
	MODIFY COLUMN action_type_id VARCHAR(50) NOT NULL
;

ALTER TABLE STATE_TRANSITION_EVALUATION
	ADD COLUMN scope_id VARCHAR(50),
	ADD INDEX (scope_id),
	ADD FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
;

UPDATE STATE_TRANSITION_EVALUATION INNER JOIN SCOPE
SET STATE_TRANSITION_EVALUATION.scope_id = SCOPE.id
WHERE STATE_TRANSITION_EVALUATION.id LIKE CONCAT(SCOPE.id, "_%")
;

ALTER TABLE STATE_TRANSITION_EVALUATION
	MODIFY COLUMN scope_id VARCHAR(50) NOT NULL
;

/* Fix primary keys in join tables */

ALTER TABLE PROGRAM_EXPORT_PROGRAM
	DROP PRIMARY KEY,
	DROP COLUMN id,
	ADD PRIMARY KEY (program_export_id, program_id),
	DROP INDEX program_export_id
;

CREATE TABLE ROLE_EXCLUSION (
	role_id VARCHAR(50) NOT NULL,
	excluded_role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (role_id, excluded_role_id),
	INDEX (excluded_role_id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (excluded_role_id) REFERENCES ROLE (id)
) ENGINE = INNODB
;

INSERT INTO ROLE_EXCLUSION (role_id, excluded_role_id)
SELECT ROLE_TRANSITION.role_id, ROLE_TRANSITION_EXCLUSION.role_id
FROM ROLE_TRANSITION INNER JOIN ROLE_TRANSITION_EXCLUSION
	ON ROLE_TRANSITION.id = ROLE_TRANSITION_EXCLUSION.role_transition_id
GROUP BY ROLE_TRANSITION.role_id, ROLE_TRANSITION_EXCLUSION.role_id
;

DROP TABLE ROLE_TRANSITION_EXCLUSION
;

RENAME TABLE STATE_TRANSITION_PROPAGATION TO STATE_TRANSITION_PROPAGATION_OLD
;

CREATE TABLE STATE_TRANSITION_PROPAGATION (
	state_transition_id INT(10) UNSIGNED NOT NULL,
	action_id VARCHAR(100) NOT NULL,
	PRIMARY KEY (state_transition_id, action_id),
	INDEX (action_id),
	FOREIGN KEY (state_transition_id) REFERENCES STATE_TRANSITION (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id)
) ENGINE = INNODB
;

INSERT INTO STATE_TRANSITION_PROPAGATION (state_transition_id, action_id)
	SELECT STATE_TRANSITION_PROPAGATION_OLD.state_transition_id, STATE_ACTION.action_id
	FROM STATE_TRANSITION_PROPAGATION_OLD INNER JOIN STATE_TRANSITION
		ON STATE_TRANSITION_PROPAGATION_OLD.propagated_state_transition_id = STATE_TRANSITION.id
	INNER JOIN STATE_ACTION
		ON STATE_TRANSITION.state_action_id = STATE_ACTION.id
	GROUP BY STATE_TRANSITION_PROPAGATION_OLD.state_transition_id, STATE_ACTION.action_id
;

DROP TABLE STATE_TRANSITION_PROPAGATION_OLD
;

/* Unused columns in state table */

ALTER TABLE STATE
	DROP COLUMN is_fertile_state,
	DROP COLUMN is_assessment_state
;

/* Changes to comment custom question */

ALTER TABLE COMMENT_CUSTOM_QUESTION
	MODIFY COLUMN comment_custom_question_version_id INT(10) UNSIGNED,
	ADD COLUMN is_enabled INT(1) UNSIGNED NOT NULL DEFAULT 1
;

ALTER TABLE COMMENT_CUSTOM_QUESTION
	MODIFY COLUMN is_enabled INT(1) UNSIGNED NOT NULL
;

/* Clean up configuration table */

DELETE
FROM CONFIGURATION
WHERE configuration_parameter_id = "APPLICATION_EXPORT_ENABLED"
;

CREATE TABLE PROGRAM_TYPE_STUDY_DURATION (
	id INT (10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_type_id VARCHAR(50) NOT NULL,
	month_duration INT(3) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, program_type_id),
	UNIQUE INDEX (institution_id, program_type_id),
	FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_type_id) REFERENCES PROGRAM_TYPE (id)
) ENGINE = INNODB
;

INSERT INTO PROGRAM_TYPE_STUDY_DURATION (system_id, program_type_id, month_duration)
	SELECT 1, program_type_id, parameter_value
	FROM CONFIGURATION
	WHERE program_type_id IS NOT NULL
;

INSERT INTO PROGRAM_TYPE_STUDY_DURATION (institution_id, program_type_id, month_duration)
	SELECT 5243, program_type_id, parameter_value
	FROM CONFIGURATION
	WHERE program_type_id IS NOT NULL
;

DELETE FROM CONFIGURATION
WHERE program_type_id IS NOT NULL
;

DELETE FROM CONFIGURATION_PARAMETER
WHERE id != "ACTION_EXPIRY_DURATION"
;

ALTER TABLE CONFIGURATION
	ADD COLUMN system_id INT(10) UNSIGNED AFTER id,
	MODIFY COLUMN institution_id INT(10) UNSIGNED,
	DROP FOREIGN KEY configuration_ibfk_3,
	DROP COLUMN program_type_id,
	ADD UNIQUE INDEX (system_id, parameter_value),
	DROP INDEX institution_id,
	ADD UNIQUE INDEX (institution_id, parameter_value),
	ADD FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	MODIFY COLUMN parameter_value INT(10) UNSIGNED NOT NULL
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE CONFIGURATION_PARAMETER
SET id = "DAY_ACTION_EXPIRY_DURATION"
;

UPDATE CONFIGURATION
SET configuration_parameter_id = "DAY_ACTION_EXPIRY_DURATION"
;

SET FOREIGN_KEY_CHECKS = 1
;

INSERT INTO CONFIGURATION (system_id, configuration_parameter_id, parameter_value)
	SELECT 1, configuration_parameter_id, parameter_value
	FROM CONFIGURATION
;

UPDATE CONFIGURATION
SET parameter_value = parameter_value / 86400
;

/* State duration expiry fixed */

UPDATE STATE_DURATION
SET expiry_duration = expiry_duration / 86400
;

ALTER TABLE STATE_DURATION
	CHANGE COLUMN expiry_duration day_expiry_duration INT(3) UNSIGNED NOT NULL
;

/* State action and default action */

ALTER TABLE STATE_ACTION
	DROP INDEX state_id,
	ADD UNIQUE INDEX (state_id, action_id),
	ADD COLUMN is_default_action INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER raises_urgent_flag,
	ADD INDEX (is_default_action)
;

ALTER TABLE STATE_ACTION
	MODIFY COLUMN is_default_action INT(1) UNSIGNED NOT NULL
;

UPDATE STATE_ACTION INNER JOIN STATE_ACTION_ASSIGNMENT
	ON STATE_ACTION.id = STATE_ACTION_ASSIGNMENT.state_action_id
SET STATE_ACTION.is_default_action = 1
WHERE STATE_ACTION_ASSIGNMENT.is_default = 1
;

ALTER TABLE STATE_ACTION_ASSIGNMENT
DROP COLUMN is_default
;

UPDATE STATE_ACTION
SET is_default_action = 0
;

UPDATE STATE_ACTION
SET is_default_action = 1
WHERE action_id IN ("APPLICATION_COMPLETE", "APPLICATION_EDIT_AS_CREATOR", "APPLICATION_EDIT_AS_CREATOR")
;

UPDATE STATE_ACTION
SET is_default_action = 1
WHERE action_id = "PROJECT_CONFIGURE"
;

UPDATE STATE_ACTION
SET is_default_action = 1
WHERE action_id = "PROGRAM_VIEW"
	AND state_id LIKE "PROGRAM_APPROVAL_%"
;

UPDATE STATE_ACTION
SET is_default_action = 1
WHERE action_id IN ("PROGRAM_RESTORE", "PROGRAM_CONFIGURE")
;

/* Reminder interval in days */

UPDATE NOTIFICATION_TEMPLATE
SET reminder_interval = reminder_interval / 86400
;

ALTER TABLE NOTIFICATION_TEMPLATE
	CHANGE COLUMN reminder_interval day_reminder_interval INT(3) UNSIGNED
;

/* Transient object updated timestamps */

ALTER TABLE APPLICATION
	MODIFY COLUMN created_timestamp DATETIME NOT NULL AFTER due_date,
	ADD COLUMN updated_timestamp DATETIME AFTER created_timestamp
;

UPDATE APPLICATION
SET updated_timestamp = created_timestamp
;

UPDATE APPLICATION INNER JOIN (
	SELECT application_id AS application_id,
		MAX(created_timestamp) AS updated_timestamp
	FROM COMMENT
	WHERE application_id IS NOT NULL
	GROUP BY application_id) AS LAST_UPDATE
	ON APPLICATION.id = LAST_UPDATE.application_id
SET APPLICATION.updated_timestamp = LAST_UPDATE.updated_timestamp
;

ALTER TABLE APPLICATION
	MODIFY COLUMN updated_timestamp DATETIME NOT NULL
;

ALTER TABLE PROGRAM
	ADD COLUMN created_timestamp DATETIME,
	ADD COLUMN updated_timestamp DATETIME AFTER created_timestamp
;

UPDATE PROGRAM INNER JOIN (
	SELECT program_id AS program_id,
		MIN(created_timestamp) AS created_timestamp
	FROM COMMENT
	WHERE program_id IS NOT NULL
	GROUP BY program_id) AS LAST_UPDATE
	ON PROGRAM.id = LAST_UPDATE.program_id
SET PROGRAM.created_timestamp = LAST_UPDATE.created_timestamp
;

UPDATE PROGRAM INNER JOIN (
	SELECT program_id AS program_id,
		MAX(created_timestamp) AS updated_timestamp
	FROM COMMENT
	WHERE program_id IS NOT NULL
	GROUP BY program_id) AS LAST_UPDATE
	ON PROGRAM.id = LAST_UPDATE.program_id
SET PROGRAM.updated_timestamp = LAST_UPDATE.updated_timestamp
;

ALTER TABLE PROGRAM
	MODIFY COLUMN created_timestamp DATETIME NOT NULL,
	MODIFY COLUMN updated_timestamp DATETIME NOT NULL
;

ALTER TABLE PROJECT
	MODIFY COLUMN created_timestamp DATETIME NOT NULL AFTER due_date,
	ADD COLUMN updated_timestamp DATETIME AFTER created_timestamp
;

UPDATE PROJECT INNER JOIN (
	SELECT project_id AS project_id,
		MAX(created_timestamp) AS updated_timestamp
	FROM COMMENT
	WHERE project_id IS NOT NULL
	GROUP BY project_id) AS LAST_UPDATE
	ON PROJECT.id = LAST_UPDATE.project_id
SET PROJECT.updated_timestamp = LAST_UPDATE.updated_timestamp
;

ALTER TABLE PROJECT
	MODIFY COLUMN updated_timestamp DATETIME NOT NULL
;

/* Rename restrict to action invoker */

ALTER TABLE ROLE_TRANSITION
	CHANGE COLUMN restrict_to_invoker restrict_to_action_owner INT(1) UNSIGNED NOT NULL
;

/* Clean up comment */

UPDATE COMMENT
SET declined_response = 0
WHERE declined_response IS NULL
;

ALTER TABLE COMMENT
	MODIFY COLUMN declined_response INT(1) UNSIGNED NOT NULL,
	ADD COLUMN user_specified_due_date DATE AFTER transition_state_id,
	ADD COLUMN action_on_parent_resource_id VARCHAR(100) AFTER application_export_reference,
	ADD INDEX (action_on_parent_resource_id),
	ADD FOREIGN KEY (action_on_parent_resource_id) REFERENCES ACTION (id),
	MODIFY COLUMN role_id VARCHAR(1000) NOT NULL,
	MODIFY COLUMN created_timestamp DATETIME NOT NULL
;

ALTER TABLE COMMENT
	MODIFY COLUMN action_id VARCHAR(100) NOT NULL
;

/* State transition pending */

CREATE TABLE STATE_TRANSITION_PENDING (
	id INT(10) UNSIGNED NOT NULL,
	system_id INT(10) UNSIGNED NOT NULL,
	institution_id INT(10) UNSIGNED NOT NULL,
	program_id INT(10) UNSIGNED NOT NULL,
	project_id INT(10) UNSIGNED NOT NULL,
	application_id INT(10) UNSIGNED NOT NULL,
	state_transition_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, state_transition_id),
	UNIQUE INDEX (institution_id, state_transition_id),
	UNIQUE INDEX (program_id, state_transition_id),
	UNIQUE INDEX (project_id, state_transition_id),
	UNIQUE INDEX (application_id, state_transition_id),
	FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (project_id) REFERENCES PROJECT (id),
	FOREIGN KEY (application_id) REFERENCES APPLICATION (id)
) ENGINE = INNODB
;

/* Simplify update notification functionality */

/* Other application entities that might be imported */

ALTER TABLE IMPORTED_ENTITY
	MODIFY COLUMN code VARCHAR(20) NOT NULL,
	MODIFY COLUMN institution_id INT(10) UNSIGNED NOT NULL
;

ALTER TABLE IMPORTED_ENTITY_FEED
	MODIFY COLUMN institution_id INT(10) UNSIGNED NOT NULL
;

INSERT INTO IMPORTED_ENTITY_TYPE (id)
VALUES ("FUNDING_SOURCE"),
	("LANGUAGE_QUALIFICATION_TYPE"),
	("TITLE")
;

/* Map funding type to imported entity */

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type_id, code, name, enabled)
	SELECT 5243, "FUNDING_SOURCE", award_type, CONCAT(SUBSTRING(award_type, 1, 1), LOWER(SUBSTRING(award_type, 2))), 1
	FROM APPLICATION_FUNDING
	GROUP BY award_type
;

ALTER TABLE APPLICATION_FUNDING
	ADD COLUMN funding_source_id INT(10) UNSIGNED AFTER application_id,
	ADD INDEX (funding_source_id),
	ADD FOREIGN KEY (funding_source_id) REFERENCES IMPORTED_ENTITY (id)
;

UPDATE APPLICATION_FUNDING INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_FUNDING.award_type = IMPORTED_ENTITY.code
SET APPLICATION_FUNDING.funding_source_id = IMPORTED_ENTITY.id
;

ALTER TABLE APPLICATION_FUNDING
	MODIFY COLUMN funding_source_id INT(10) UNSIGNED NOT NULL,
	DROP COLUMN award_type
;

/* Map title to imported entity */

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type_id, code, name, enabled)
	SELECT 5243, "TITLE", title, CONCAT(SUBSTRING(title, 1, 1), LOWER(SUBSTRING(title, 2))), 1
	FROM APPLICATION_PERSONAL_DETAIL
	GROUP BY title
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	ADD COLUMN title_id INT(10) UNSIGNED AFTER id,
	ADD INDEX (title_id),
	ADD FOREIGN KEY (title_id) REFERENCES IMPORTED_ENTITY (id)
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_PERSONAL_DETAIL.title = IMPORTED_ENTITY.code
SET APPLICATION_PERSONAL_DETAIL.title_id = IMPORTED_ENTITY.id
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	MODIFY COLUMN title_id INT(10) UNSIGNED NOT NULL,
	DROP COLUMN title
;

/* Move language qualification type to imported entity */

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "classification"
WHERE qualification_type_other = "qualification_type_other"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "6 week Presessional Course in English and Academic Skills"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "Academic English Summer Program, Cranfield University"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Academic qualification undertaken in English"
WHERE qualification_type_other = "Academic qualification in the UK"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "B1 / BSOL Entry Level"
WHERE qualification_type_other = "B1"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Academic qualification undertaken in English"
WHERE qualification_type_other = "BA (Hons) English"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Bell English Language Test"
WHERE qualification_type_other = "Bell English Language Test"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Brunel English Language Test"
WHERE qualification_type_other = "BrunELT"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Business Language Testing Service "
WHERE qualification_type_other = "BULATS"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Advanced English"
WHERE qualification_type_other = "CAE"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Advanced English"
WHERE qualification_type_other = "Cambirdge Certificate of Advanced English (CAE)"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Advanced English"
WHERE qualification_type_other = "Cambridge Advanced"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Advanced English"
WHERE qualification_type_other = "Cambridge Certificate for Advanced English"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Advanced English"
WHERE qualification_type_other = "Cambridge certificate of advanced english"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Advanced English"
WHERE qualification_type_other = "Cambridge Certificate of Advanced English (CAE)"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Advanced English"
WHERE qualification_type_other = "Cambridge Certificate of Advanced English - CAE"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Proficiency in English"
WHERE qualification_type_other = "Cambridge Certificate of Proficiency in English"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambride"
WHERE qualification_type_other = "Cambridge ESOL First Certificate in English (FCE)"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Advanced English"
WHERE qualification_type_other = "Cambridge ESOL Level 2 Certificate in ESOL International; Certificate in Advanced English"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge O-Level English"
WHERE qualification_type_other = "Cambridge International Examinations English O-level"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge O-Level English"
WHERE qualification_type_other = "Cambridge O Level"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Proficiency in English"
WHERE qualification_type_other = "Cambridge Proficiency Certificate"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Proficiency in English"
WHERE qualification_type_other = "Cambridge Proficiency; BSc in English University"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "O-Level / School Level Graduation in English"
WHERE qualification_type_other = "Candian Secondary School Diploma"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "English for Higher Education"
WHERE qualification_type_other = "Certificate IV in English for Academic Purposes"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Proficiency in English"
WHERE qualification_type_other = "Certificate of Proficiency in English"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Proficiency in English"
WHERE qualification_type_other = "Certificate of Proficiency in English (ECPE)"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Michigan Certificate of Proficiency in English"
WHERE qualification_type_other = "Certificate of Proficiency in English (Michigan)"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Proficiency in English"
WHERE qualification_type_other = "Certificate of Proficiency in English (University of Cambridge)"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Michigan Certificate of Proficiency in English"
WHERE qualification_type_other = "Certificate of Proficiency in English, University of Michigan"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "B1 / BSOL Entry Level"
WHERE qualification_type_other = "Certificazione di livello"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "B1 / BSOL Entry Level"
WHERE qualification_type_other = "Common European Framework of Reference (CEF) level"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "DAAD English Certificate"
WHERE qualification_type_other = "DAAD English Certificate"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Dublin City University English Exam"
WHERE qualification_type_other = "DCU SALIS exam"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Proficiency in English"
WHERE qualification_type_other = "ECPE"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Module Study in English"
WHERE qualification_type_other = "English as Matric subject"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "B1 / BSOL Entry Level"
WHERE qualification_type_other = "English Diploma level B1+ Common European Framework"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "English for Higher Education"
WHERE qualification_type_other = "English for Academic Purposes (grade 5)"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "English for Higher Education"
WHERE qualification_type_other = "English for Higher Education"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "English for Higher Education"
WHERE qualification_type_other = "english for tertiary studies"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Personal Study / Night Classes"
WHERE qualification_type_other = "English Language & Study Skills for Academic Purposes, King's College London"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Personal Study / Night Classes"
WHERE qualification_type_other = "English Language Teaching Unit in University of Leicester"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "English for Higher Education"
WHERE qualification_type_other = "English Medium Study"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Proficiency in English"
WHERE qualification_type_other = "ESOL,Certificate of Proficiency in English, University of Cambridge"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge First Certificate in English"
WHERE qualification_type_other = "FCE"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge First Certificate in English"
WHERE qualification_type_other = "First Certificate In English"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge First Certificate in English"
WHERE qualification_type_other = "First Certificate in English (FCE)"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge First Certificate in English"
WHERE qualification_type_other = "First Certificate in English (Level B2) - University of Cambridge"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge First Certificate in English"
WHERE qualification_type_other = "First Certificate in English Level B2"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge First Certificate in English"
WHERE qualification_type_other = "First Certificate in English, University of Cambridge"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge First Certificate in English"
WHERE qualification_type_other = "First Certificate of English- Univ Cambridge"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "GSCE / O-Level / School Level Graduation in English"
WHERE qualification_type_other = "GCE"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "GSCE / O-Level / School Level Graduation in English"
WHERE qualification_type_other = "GCE O level English Language"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "GMAT Exam"
WHERE qualification_type_other = "GMAT"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "GRE Literature in English"
WHERE qualification_type_other = "Graduate Record Examination (GRE) General - Computer Based"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "GSCE / O-Level / School Level Graduation in English"
WHERE qualification_type_other = "GSCE"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "English Language Entrance Test for Other University"
WHERE qualification_type_other = "Higher Institute of Languages Masters Degree Application Test"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "IELTS Academic"
WHERE qualification_type_other = "IELTS"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Academic qualification undertaken in English"
WHERE qualification_type_other = "I studied General, Academic English and Bachelor of Engineering at QUT from Oct. 2007 to Jul 2012"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "IELTS Academic"
WHERE qualification_type_other = "IELTS"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "IELTS Academic"
WHERE qualification_type_other = "IELTS"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "IELTS Academic"
WHERE qualification_type_other = "IELTS"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "GSCE / O-Level / School Level Graduation in English"
WHERE qualification_type_other = "IGCSE"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "Intensive English"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "Intensive English program (IEP)"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "B1 / BSOL Entry Level"
WHERE qualification_type_other = "Intermediat"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "UK Residency Exam"
WHERE qualification_type_other = "Life in the UK test"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Malaysia University English Test"
WHERE qualification_type_other = "Malaysia University English Test"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Academic qualification undertaken in English"
WHERE qualification_type_other = "Master degree from McGill University"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Academic qualification undertaken in English"
WHERE qualification_type_other = "Master Degree from University of Glasgow"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Academic qualification undertaken in English"
WHERE qualification_type_other = "MASTER DEGREE IN UK"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Academic qualification undertaken in English"
WHERE qualification_type_other = "Masters taught in English"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Academic qualification undertaken in English"
WHERE qualification_type_other = "MEng Electrical Engineering from UCL studied in English"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Academic qualification undertaken in English"
WHERE qualification_type_other = "Msc business information technology"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Academic qualification undertaken in English"
WHERE qualification_type_other = "MSC Criminology and Forensic Psychology"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Academic qualification undertaken in English"
WHERE qualification_type_other = "MSc Statistics from UCL"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "MSRT / MCHE Exam"
WHERE qualification_type_other = "MSRT(MCHE)"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "New Interchange program study"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Personal Study / Night Classes"
WHERE qualification_type_other = "Oxford House College London"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "PALSO Standard Certificate English"
WHERE qualification_type_other = "PALSO"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Pearson Test of English"
WHERE qualification_type_other = "Pearson PTE Academic"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Pearson Test of English"
WHERE qualification_type_other = "PEARSON TEST FOR ENGLISH"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Pearson Test of English"
WHERE qualification_type_other = "Pearson Test of English"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Academic qualification undertaken in English"
WHERE qualification_type_other = "Post-doc in UCL, MS&I in 2010"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "Pre-sessional Academic English Course"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "Pre-sessional Course"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "pre-sessional english course of bristol university"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "Pre-sessional English Language Course"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Proficiency in English"
WHERE qualification_type_other = "Proficiency in English"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Michigan Certificate of Proficiency in English"
WHERE qualification_type_other = "Proficiency Michigan"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Pearson Test of English"
WHERE qualification_type_other = "PTE"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "GSCE / O-Level / School Level Graduation in English"
WHERE qualification_type_other = "SENIOR SECONDARY SCHOOL EXAM"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "English Language Entrance Test for Other University"
WHERE qualification_type_other = "Sufficiency Test"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "TOEFL iBT"
WHERE qualification_type_other = "TOEFL"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "TOEFL iBT"
WHERE qualification_type_other = "TOEFL"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "TOEFL iBT"
WHERE qualification_type_other = "TOEFL"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "TOEFL iBT"
WHERE qualification_type_other = "TOEIC"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Trinity College Certificate of Proficiency in English"
WHERE qualification_type_other = "Trinity College London"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Trinity College Certificate of Proficiency in English"
WHERE qualification_type_other = "Trinity College, Grade 6 of spoken english for speakers of other languages"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "UCL Certificate of Proficiency in English"
WHERE qualification_type_other = "UCL Language Center Certificate"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "UCL language Center [Extended Pre-sessional Course]"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "UCL Pre-sessional English Courses"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "UCL Pre-sessional English Language Course"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "UCL Pre-sessional English Language couse"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "UCL Presessional English Course"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "UCTI Intensive  English Program"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Intensive Course / Summer School"
WHERE qualification_type_other = "ULNSW English course"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge First Certificate in English"
WHERE qualification_type_other = "University Language Scheme"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge First Certificate in English"
WHERE qualification_type_other = "University of Cambridge(First Certificate in English)"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "University of Leicester Certificate of Proficiency in English"
WHERE qualification_type_other = "University of Leicester Presessional Programme Course D"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "GSCE / O-Level / School Level Graduation in English"
WHERE qualification_type_other = "WAEC GCE O-Levels / GCE A-Levels"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "GSCE / O-Level / School Level Graduation in English"
WHERE qualification_type_other = "West African Examinations Council (English O-Level)"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = NULL
WHERE LENGTH(qualification_type_other) = 0
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "IELTS Academic"
WHERE qualification_type_other = "IELTS General"
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type_id, code, name, enabled)
	SELECT 5243, "LANGUAGE_QUALIFICATION_TYPE", "TOEFL", "TOEFL iBT", 1
		UNION
	SELECT 5243, "LANGUAGE_QUALIFICATION_TYPE", "IELTS", "IELTS Academic", 1
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_LANGUAGE_QUALIFICATION.qualification_type_other = IMPORTED_ENTITY.name
SET APPLICATION_LANGUAGE_QUALIFICATION.qualification_type = IMPORTED_ENTITY.code
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Advanced English"
WHERE qualification_type_other = "Cambridge ESOL Level 2 Certificate in ESOL International: Certificate in Advanced English"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "Cambridge Certificate of Proficiency in English"
WHERE qualification_type_other = "Cambride"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "IELTS Academic",
	qualification_type = "IELTS_ACADEMIC"
WHERE qualification_type_other = "IELTS + General english courses + English for Academic study courses"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "O-Level / School Level Graduation in English"
WHERE qualification_type_other = "West African Senior School certificate"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "IELTS Academic",
	qualification_type = "IELTS_ACADEMIC"
WHERE qualification_type_other = "I am taking an IELTS (Academic) test on 27 July 2013"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type_other = "TOEFL iBT",
	qualification_type = "TOEFL"
WHERE qualification_type_other LIKE "TOEFL%"
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
SET qualification_type = "IELTS_ACADEMIC"
WHERE qualIfication_type = "IELTS"
;

ALTER TABLE IMPORTED_ENTITY
	MODIFY COLUMN code VARCHAR(20)
;

CREATE TRIGGER TR_CREATE_ENTITY_CODE 
BEFORE INSERT ON IMPORTED_ENTITY 
FOR EACH ROW 
BEGIN
	
	DECLARE code_integer_part INT(10) UNSIGNED;
	
	IF NEW.code IS NULL THEN
	
		SET code_integer_part = (
			SELECT COUNT(id) + 1
			FROM IMPORTED_ENTITY
			WHERE code LIKE "CUST%");
			
		SET NEW.code = ( 
			SELECT CONCAT("CUST", LPAD(CONCAT("", code_integer_part, ""), 6, "0")));
			
	END IF;
	
END
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type_id, name, enabled)
	SELECT 5243, "LANGUAGE_QUALIFICATION_TYPE", qualification_type_other, 1
	FROM APPLICATION_LANGUAGE_QUALIFICATION
	WHERE qualification_type = "OTHER"
	GROUP BY qualification_type_other
;

DROP TRIGGER TR_CREATE_ENTITY_CODE
;

ALTER TABLE IMPORTED_ENTITY
	MODIFY COLUMN code VARCHAR(20) NOT NULL
;

UPDATE IMPORTED_ENTITY
SET code = "IELTS_ACADEMIC"
WHERE code = "IELTS"
;

UPDATE IMPORTED_ENTITY
SET name = "European Engineer"
WHERE name = "European_engineer"
;

UPDATE IMPORTED_ENTITY
SET name = "Professor / Dr"
WHERE name = "Professor_dr"
;

ALTER TABLE APPLICATION_LANGUAGE_QUALIFICATION
	ADD COLUMN language_qualification_type_id INT(10) UNSIGNED AFTER id,
	ADD INDEX (language_qualification_type_id),
	ADD FOREIGN KEY (language_qualification_type_id) REFERENCES IMPORTED_ENTITY (id)
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION INNER JOIN IMPORTED_ENTITY
SET APPLICATION_LANGUAGE_QUALIFICATION.language_qualification_type_id = IMPORTED_ENTITY.id
WHERE APPLICATION_LANGUAGE_QUALIFICATION.qualification_type = IMPORTED_ENTITY.code
	OR APPLICATION_LANGUAGE_QUALIFICATION.qualification_type_other = IMPORTED_ENTITY.name
;

ALTER TABLE APPLICATION_LANGUAGE_QUALIFICATION
	DROP COLUMN qualification_type,
	DROP COLUMN qualification_type_other,
	MODIFY COLUMN language_qualification_type_id INT(10) UNSIGNED NOT NULL
;

ALTER TABLE APPLICATION_REFEREE
	DROP INDEX application_form_referee_fk,
	ADD UNIQUE INDEX (application_id, user_id)
;

/* Closing date unique within advert */

ALTER TABLE ADVERT_CLOSING_DATE
	DROP INDEX program_closing_dates_fk,
	ADD UNIQUE INDEX (advert_id, closing_date)
;
