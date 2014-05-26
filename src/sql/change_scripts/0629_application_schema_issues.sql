CREATE TABLE APPLICATION_FORM_LANGUAGE_QUALIFICATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	qualification_type VARCHAR(50) NOT NULL,
	qualification_type_other VARCHAR(100),
	exam_date DATE NOT NULL,
	overall_score VARCHAR(10) NOT NULL,
	reading_score VARCHAR(10) NOT NULL,
	writing_score VARCHAR(10) NOT NULL,
	speaking_score VARCHAR(10) NOT NULL,
	listening_score VARCHAR(10) NOT NULL,
	exam_online INT(1) NOT NULL,
	document_id INT(10) UNSIGNED,
	personal_details_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	INDEX (document_id),
	FOREIGN KEY (document_id) REFERENCES DOCUMENT (id)
) ENGINE = INNODB
;

INSERT INTO APPLICATION_FORM_LANGUAGE_QUALIFICATION (qualification_type,
	qualification_type_other, exam_date, overall_score, reading_score, writing_score,
	speaking_score, listening_score, exam_online, document_id, personal_details_id)
	SELECT language_qualification_type, language_qualification_type_name,
		language_exam_date, language_overall_score, language_reading_score,
		language_writing_score, language_speaking_score, language_listening_score,
		language_exam_online, language_qualification_document_id, id
	FROM APPLICATION_FORM_PERSONAL_DETAIL
	WHERE language_qualification_type IS NOT NULL
;

ALTER TABLE APPLICATION_FORM_PERSONAL_DETAIL
	ADD COLUMN application_form_language_qualification_id INT(10) UNSIGNED AFTER language_qualification_available,
	ADD INDEX (application_form_language_qualification_id),
	ADD CONSTRAINT APPLICATION_FORM_PERSONAL_DETAIL_ibfk3 FOREIGN KEY (application_form_language_qualification_id) 
		REFERENCES APPLICATION_FORM_LANGUAGE_QUALIFICATION (id)
;

UPDATE APPLICATION_FORM_PERSONAL_DETAIL INNER JOIN APPLICATION_FORM_LANGUAGE_QUALIFICATION
	ON APPLICATION_FORM_PERSONAL_DETAIL.id = APPLICATION_FORM_LANGUAGE_QUALIFICATION.personal_details_id
SET APPLICATION_FORM_PERSONAL_DETAIL.application_form_language_qualification_id =
	APPLICATION_FORM_LANGUAGE_QUALIFICATION.id
;

ALTER TABLE APPLICATION_FORM_LANGUAGE_QUALIFICATION
	DROP COLUMN personal_details_id
;

ALTER TABLE APPLICATION_FORM_PERSONAL_DETAIL
	DROP COLUMN language_qualification_type, 
	DROP COLUMN language_qualification_type_name,
	DROP COLUMN language_exam_date, 
	DROP COLUMN language_overall_score, 
	DROP COLUMN language_reading_score,
	DROP COLUMN language_writing_score, 
	DROP COLUMN language_speaking_score, 
	DROP COLUMN language_listening_score,
	DROP COLUMN language_exam_online,
	DROP FOREIGN KEY personal_detail_language_qualification_document_id_fk,
	DROP COLUMN language_qualification_document_id
;

CREATE TABLE APPLICATION_FORM_PASSPORT (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	number VARCHAR(35) NOT NULL,
	name VARCHAR(100) NOT NULL,
	issue_date DATE NOT NULL,
	expiry_date DATE NOT NULL,
	personal_details_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
;

INSERT INTO APPLICATION_FORM_PASSPORT (number, name, issue_date, expiry_date, personal_details_id)
	SELECT passport_number, passport_name, passport_issue_date, passport_expiry_date, id
	FROM APPLICATION_FORM_PERSONAL_DETAIL
	WHERE passport_number IS NOT NULL
		AND LENGTH(passport_number) != 0
;

ALTER TABLE APPLICATION_FORM_PERSONAL_DETAIL
	ADD COLUMN application_form_passport_id INT(10) UNSIGNED AFTER passport_available,
	ADD INDEX (application_form_passport_id),
	ADD CONSTRAINT APPLICATION_FORM_PERSONAL_DETAIL_ibfk4 FOREIGN KEY (application_form_passport_id) 
		REFERENCES APPLICATION_FORM_PASSPORT (id)
;

UPDATE APPLICATION_FORM_PERSONAL_DETAIL INNER JOIN APPLICATION_FORM_PASSPORT
	ON APPLICATION_FORM_PERSONAL_DETAIL.id = APPLICATION_FORM_PASSPORT.personal_details_id
SET APPLICATION_FORM_PERSONAL_DETAIL.application_form_passport_id =
	APPLICATION_FORM_PASSPORT.id
;

ALTER TABLE APPLICATION_FORM_PASSPORT
	DROP COLUMN personal_details_id
;

ALTER TABLE APPLICATION_FORM_PERSONAL_DETAIL
	DROP COLUMN passport_number, 
	DROP COLUMN passport_name,
	DROP COLUMN passport_issue_date, 
	DROP COLUMN passport_expiry_date
;

ALTER TABLE SUGGESTED_SUPERVISOR
	ADD PRIMARY KEY (id),
	DROP INDEX suggested_supervisor_fk
;

RENAME TABLE APPLICATION_FORM_PROGRAMME_DETAIL TO APPLICATION_FORM_PROGRAM_DETAIL
;

ALTER TABLE APPLICATION_FORM
	CHANGE COLUMN programme_details_id program_detail_id INT(10) UNSIGNED
;

UPDATE APPLICATION_FORM_PROGRAM_DETAIL
SET study_code = "F+++++"
WHERE study_code IS NULL
;

CREATE TABLE STUDY_OPTION (
	id VARCHAR(50) NOT NULL,
	display_name VARCHAR(50) NOT NULL,
	PRIMARY KEY (id))
ENGINE = INNODB
	SELECT study_code AS id, study_option AS display_name
	FROM PROGRAM_INSTANCE
	GROUP BY study_code
;

ALTER TABLE PROGRAM_INSTANCE
	CHANGE COLUMN STUDY_CODE study_option_id VARCHAR(50) NOT NULL,
	ADD INDEX (study_option_id),
	ADD FOREIGN KEY (study_option_id) REFERENCES STUDY_OPTION (id),
	DROP COLUMN study_option
;	

ALTER TABLE APPLICATION_FORM_PROGRAM_DETAIL
	CHANGE COLUMN STUDY_CODE study_option_id VARCHAR(50) NOT NULL,
	ADD INDEX (study_option_id),
	ADD FOREIGN KEY (study_option_id) REFERENCES STUDY_OPTION (id),
	DROP COLUMN programme_name,
	DROP COLUMN project_name,
	DROP COLUMN study_option
;

DROP TABLE INSTITUTION_ADMINISTRATOR_LINK
;

UPDATE APPLICATION_FORM
SET is_exported = excluded
WHERE excluded = 1
;

ALTER TABLE APPLICATION_FORM
	DROP FOREIGN KEY approver_user_fk,
	DROP COLUMN approver_user_id,
	CHANGE COLUMN app_date_time created_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CHANGE COLUMN submitted_on_timestamp submitted_timestamp DATETIME,
	CHANGE COLUMN last_updated update_timestamp DATETIME,
	DROP COLUMN accepted_terms,
	DROP COLUMN project_title,
	DROP FOREIGN KEY app_admin_fk,
	DROP COLUMN app_administrator_id,
	DROP COLUMN registry_users_notified,
	DROP FOREIGN KEY admin_requested_fk,
	DROP COLUMN admin_requested_registry_id,
	DROP COLUMN pending_approval_restart,
	CHANGE COLUMN IP_ADDRESS submitted_ip_address VARBINARY(16),
	DROP COLUMN avg_rating,
	DROP COLUMN excluded
;

ALTER TABLE APPLICATION_FORM_PERSONAL_DETAIL
	MODIFY COLUMN ethnicity_id INT(10) UNSIGNED NOT NULL,
	MODIFY COLUMN disability_id INT(10) UNSIGNED NOT NULL
;

DELETE 
FROM APPLICATION_FORM_EMPLOYMENT_POSITION
WHERE application_form_id IS NULL
;

ALTER TABLE APPLICATION_FORM_EMPLOYMENT_POSITION
	CHANGE COLUMN ADDRESS_ID address_id INT(10) UNSIGNED NOT NULL,
	MODIFY COLUMN application_form_id INT(10) UNSIGNED NOT NULL
;

DELETE 
FROM APPLICATION_FORM_FUNDING
WHERE application_form_id IS NULL
	OR document_id IS NULL
;

ALTER TABLE APPLICATION_FORM_FUNDING
	MODIFY COLUMN application_form_id INT(10) UNSIGNED NOT NULL,
	MODIFY COLUMN document_id INT(10) UNSIGNED NOT NULL
;

ALTER TABLE APPLICATION_FORM_FUNDING
	MODIFY COLUMN application_form_id INT(10) UNSIGNED NOT NULL
;

ALTER TABLE ADDRESS
	CHANGE COLUMN DOMICILE_ID domicile_id INT(10) UNSIGNED NOT NULL,
	CHANGE COLUMN ADDRESS1 address_line_1 VARCHAR(50) NOT NULL,
	CHANGE COLUMN ADDRESS2 address_line_2 VARCHAR(50),
	CHANGE COLUMN ADDRESS3 address_town VARCHAR(50) NOT NULL,
	CHANGE COLUMN ADDRESS4 address_region VARCHAR(50),
	CHANGE COLUMN ADDRESS5 address_code VARCHAR(12)
;

ALTER TABLE ADVERT 
	DROP COLUMN last_edited_timestamp
;

RENAME TABLE PROGRAM_CLOSING_DATES TO ADVERT_CLOSING_DATE
;

ALTER TABLE ADVERT_CLOSING_DATE
	DROP FOREIGN KEY advert_closing_date_ibfk_1,
	CHANGE COLUMN program_id advert_id INT(10) UNSIGNED NOT NULL,
	ADD FOREIGN KEY (advert_id) REFERENCES ADVERT (id)
;

INSERT INTO ADVERT_CLOSING_DATE (advert_id, closing_date, study_places)
	SELECT id, closing_date, 1
	FROM PROJECT
	WHERE closing_date IS NOT NULL
;

ALTER TABLE PROJECT
	DROP COLUMN closing_date
;
	