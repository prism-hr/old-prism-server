ALTER TABLE APPLICATION_FORM_PERSONAL_DETAIL ADD COLUMN language_qualification_available BOOLEAN DEFAULT false AFTER english_first_language
;

CREATE TABLE APPLICATION_FORM_PERSONAL_DETAIL_LANGUAGE_QUALIFICATIONS (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	application_form_personal_detail_id INTEGER UNSIGNED DEFAULT NULL,
	qualification_type VARCHAR(100) DEFAULT NULL,
	other_qualification_type_name VARCHAR(100) DEFAULT NULL,
	date_of_examination DATE DEFAULT NULL,
	overall_score VARCHAR(10) DEFAULT NULL,
	reading_score VARCHAR(10) DEFAULT NULL,
	writing_score VARCHAR(10) DEFAULT NULL,
	speaking_score VARCHAR(10) DEFAULT NULL,
	listening_score VARCHAR(10) DEFAULT NULL,
	exam_taken_online BOOLEAN DEFAULT NULL,
	language_qualification_document_id INTEGER UNSIGNED DEFAULT NULL,
	PRIMARY KEY (id),
	KEY application_form_personal_detail_fk (application_form_personal_detail_id),
	KEY language_qualification_document_id_fk (language_qualification_document_id),
	CONSTRAINT application_form_personal_detail_lang_fk FOREIGN KEY (application_form_personal_detail_id) REFERENCES APPLICATION_FORM_PERSONAL_DETAIL (id),
	CONSTRAINT cstr_language_qualification_document_id_fk FOREIGN KEY (language_qualification_document_id) REFERENCES DOCUMENT (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

CREATE TABLE APPLICATION_FORM_PERSONAL_DETAIL_PASSPORT (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	application_form_personal_detail_id INTEGER UNSIGNED DEFAULT NULL,
	passport_number VARCHAR(35) DEFAULT NULL,
	passport_name VARCHAR(100) DEFAULT NULL, 
	passport_issue_date DATE DEFAULT NULL,
	passport_expiry_date DATE DEFAULT NULL,
	PRIMARY KEY (id),
	KEY application_form_personal_detail_fk (application_form_personal_detail_id),
	CONSTRAINT application_form_personal_detail_passport_fk FOREIGN KEY (application_form_personal_detail_id) REFERENCES APPLICATION_FORM_PERSONAL_DETAIL (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

INSERT INTO APPLICATION_FORM_PERSONAL_DETAIL_PASSPORT (application_form_personal_detail_id, passport_number, passport_name, passport_issue_date, passport_expiry_date) 
SELECT id, passport_number, passport_name, passport_issue_date, passport_expiry_date FROM APPLICATION_FORM_PERSONAL_DETAIL WHERE passport_name IS NOT NULL
;

ALTER TABLE APPLICATION_FORM_PERSONAL_DETAIL 
DROP COLUMN passport_number, 
DROP COLUMN passport_name, 
DROP COLUMN passport_issue_date,
DROP COLUMN passport_expiry_date
;
