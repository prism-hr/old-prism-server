INSERT IGNORE INTO USER_ROLE (application_id, user_id, role_id, assigned_timestamp)
	SELECT APPLICATION.id, APPLICATION_SUPERVISOR.user_id, 
		"APPLICATION_SUGGESTED_SUPERVISOR", APPLICATION.submitted_timestamp
	FROM APPLICATION INNER JOIN APPLICATION_PROGRAM_DETAIL
		ON APPLICATION.application_program_detail_id = APPLICATION_PROGRAM_DETAIL.id 
	INNER JOIN APPLICATION_SUPERVISOR
		ON APPLICATION_PROGRAM_DETAIL.id = APPLICATION_SUPERVISOR.application_program_detail_id
	WHERE APPLICATION.submitted_timestamp IS NOT NULL
		UNION
	SELECT APPLICATION.id, APPLICATION_REFEREE.user_id, 
		"APPLICATION_REFEREE", APPLICATION.submitted_timestamp
	FROM APPLICATION INNER JOIN APPLICATION_REFEREE
		ON APPLICATION.id = APPLICATION_REFEREE.application_id
	WHERE APPLICATION.submitted_timestamp IS NOT NULL
;

INSERT INTO IMPORTED_ENTITY_FEED (institution_id, imported_entity_type, location)
VALUES (5243, "FUNDING_SOURCE", "xml/defaultEntities/fundingSource.xml")
;

INSERT INTO IMPORTED_ENTITY_FEED (institution_id, imported_entity_type, location)
VALUES (5243, "LANGUAGE_QUALIFICATION_TYPE", "xml/defaultEntities/languageQualificationType.xml")
;

INSERT INTO IMPORTED_ENTITY_FEED (institution_id, imported_entity_type, location)
VALUES (5243, "TITLE", "xml/defaultEntities/title.xml")
;

INSERT INTO IMPORTED_ENTITY_FEED (institution_id, imported_entity_type, location)
VALUES (5243, "INSTITUTION", "xml/defaultEntities/institution.xml")
;

INSERT INTO IMPORTED_ENTITY_FEED (institution_id, imported_entity_type, location)
VALUES (5243, "GENDER", "xml/defaultEntities/gender.xml")
;

INSERT INTO IMPORTED_ENTITY_FEED (institution_id, imported_entity_type, location)
VALUES (5243, "REJECTION_REASON", "xml/defaultEntities/rejectionReason.xml")
;

INSERT INTO IMPORTED_ENTITY_FEED (institution_id, imported_entity_type, location)
VALUES (5243, "RESIDENCE_STATE", "xml/defaultEntities/residenceState.xml")
;

ALTER TABLE IMPORTED_ENTITY
	MODIFY COLUMN code VARCHAR(50) NOT NULL,
	MODIFY COLUMN name VARCHAR(255) NOT NULL
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type, code, name, enabled)
	SELECT 5243, "REJECTION_REASON", "APPLICATION_INCOMPLETE", "We are unable to form a judgement on your suitability based upon the information that you have supplied in your application.", 1
		UNION
	SELECT 5243, "REJECTION_REASON", "UNQUALIFIED_FOR_INSTITUTION", "Your qualifications and experience are not sufficient to satisfy the entrance requirements for our organisation.", 1
		UNION
	SELECT 5243, "REJECTION_REASON", "UNQUALIFIED_FOR_OPPORTUNITY", "Your qualifications and experience are not appropriate for your preferred course of study.", 1
		UNION
	SELECT 5243, "REJECTION_REASON", "UNABLE_TO_FIND_SUPERVISOR", "At the present time, we are unable to a supervisor to support you in your preferred course of study.", 1
		UNION
	SELECT 5243, "REJECTION_REASON", "DID_NOT_ATTEND_INTERVIEW", "You failed to present for interview as arranged.", 1
		UNION
	SELECT 5243, "REJECTION_REASON", "OPPORTUNITY_OVERSUBSCRIBED", "Although you may be suitable for your preferred course of study, the competition for places was such that we were unable to progress your application on this occasion.", 1
		UNION
	SELECT 5243, "REJECTION_REASON", "OPPORTUNITY_DISCONTINUED", "We are no longer able to offer your preferred course of study.",  1
		UNION
	SELECT 5243, "REJECTION_REASON", "WITHDRAWN", "You have informed us that you no longer wish to be considered.",  1
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type, code, name, enabled)
	SELECT 5243, "RESIDENCE_STATE", "HOME_EU", "Home/EU", 1
		UNION
	SELECT 5243, "RESIDENCE_STATE", "OVERSEAS", "Overseas", 1
		UNION
	SELECT 5243, "RESIDENCE_STATE", "UNSURE", "Unsure", 1
;

ALTER TABLE COMMENT
	ADD COLUMN application_residence_state_id INT(10) UNSIGNED AFTER application_residence_status,
	ADD INDEX (application_residence_state_id),
	ADD FOREIGN KEY (application_residence_state_id) REFERENCES IMPORTED_ENTITY (id)
;

UPDATE COMMENT
SET application_residence_state_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE imported_entity_type = "RESIDENCE_STATE"
	AND code = "HOME_EU")
WHERE application_residence_status = "HOME"
;

UPDATE COMMENT
SET application_residence_state_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE imported_entity_type = "RESIDENCE_STATE"
	AND code = "OVERSEAS")
WHERE application_residence_status = "OVERSEAS"
;

UPDATE COMMENT
SET application_residence_state_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE imported_entity_type = "RESIDENCE_STATE"
	AND code = "UNSURE")
WHERE application_residence_status = "UNSURE"
;

ALTER TABLE COMMENT
	DROP COLUMN application_residence_status
;

CREATE TABLE WORKFLOW_CONFIGURATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	configuration_parameter VARCHAR(50) NOT NULL,
	minimum_required INT(10) UNSIGNED,
	maximum_required INT(10) UNSIGNED,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, configuration_parameter),
	UNIQUE INDEX (institution_id, configuration_parameter),
	UNIQUE INDEX (program_id, configuration_parameter),
	FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id)
) ENGINE = INNODB
;

DROP TABLE ADVERT_RECRUITMENT_PREFERENCE
;

CREATE TABLE ADVERT_RECRUITMENT_PREFERENCE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	advert_id INT(10) UNSIGNED NOT NULL,
	institution_id INT(10) UNSIGNED NOT NULL,
	program_id INT(10) UNSIGNED,
	PRIMARY KEY (id),
	UNIQUE INDEX (advert_id, institution_id),
	UNIQUE INDEX (advert_id, program_id),
	INDEX (institution_id),
	INDEX (program_id),
	FOREIGN KEY (advert_id) REFERENCES ADVERT (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id)
) ENGINE = INNODB
;

CREATE TABLE APPLICATION_OTHER_PROJECT (
	application_program_detail_id INT(10) UNSIGNED NOT NULL,
	project_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (application_program_detail_id, project_id),
	INDEX (project_id),
	FOREIGN KEY (application_program_detail_id) REFERENCES APPLICATION_PROGRAM_DETAIL (id),
	FOREIGN KEY (project_id) REFERENCES PROJECT (id)
) ENGINE = INNODB
;

ALTER TABLE IMPORTED_INSTITUTION
	MODIFY COLUMN code VARCHAR(50) NOT NULL,
	MODIFY COLUMN name VARCHAR(255) NOT NULL
;

ALTER TABLE IMPORTED_LANGUAGE_QUALIFICATION_TYPE
	MODIFY COLUMN code VARCHAR(50) NOT NULL,
	MODIFY COLUMN name VARCHAR(255) NOT NULL
;

