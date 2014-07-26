ALTER TABLE application_form_personal_detail
	ADD COLUMN language_qualification_type VARCHAR(15) DEFAULT NULL,
	ADD COLUMN language_qualification_type_name VARCHAR(100) DEFAULT NULL,
	ADD COLUMN language_exam_date DATE DEFAULT NULL,
	ADD COLUMN language_overall_score VARCHAR(10) DEFAULT NULL,
	ADD COLUMN language_reading_score VARCHAR(10) DEFAULT NULL,
	ADD COLUMN language_writing_score VARCHAR(10) DEFAULT NULL,
	ADD COLUMN language_speaking_score VARCHAR(10) DEFAULT NULL,
	ADD COLUMN language_listening_score VARCHAR(10) DEFAULT NULL,
	ADD COLUMN language_exam_online BOOLEAN DEFAULT NULL,
	ADD COLUMN language_qualification_document_id INTEGER UNSIGNED DEFAULT NULL,
	ADD COLUMN passport_number VARCHAR(35) DEFAULT NULL,
	ADD COLUMN passport_name VARCHAR(100) DEFAULT NULL, 
	ADD COLUMN passport_issue_date DATE DEFAULT NULL,
	ADD COLUMN passport_expiry_date DATE DEFAULT NULL,
	ADD CONSTRAINT personal_detail_language_qualification_document_id_fk FOREIGN KEY (language_qualification_document_id) REFERENCES DOCUMENT (id)
;

update application_form_personal_detail pd
inner join application_form_personal_detail_language_qualifications lq
on pd.language_qualification_id = lq.id
set 
	language_qualification_type = lq.qualification_type,
	language_qualification_type_name = lq.other_qualification_type_name,
	language_exam_date = lq.date_of_examination,
	language_overall_score = lq.overall_score,
	language_reading_score = lq.reading_score,
	language_writing_score = lq.writing_score,
	language_speaking_score = lq.speaking_score,
	language_listening_score = lq.listening_score,
	language_exam_online = lq.exam_taken_online,
	pd.language_qualification_document_id = lq.language_qualification_document_id
;

update application_form_personal_detail pd
inner join application_form_personal_detail_passport p
on pd.passport_information_id = p.id
set 
	pd.passport_number = p.passport_number,
	pd.passport_name = p.passport_name,
	pd.passport_issue_date = p.passport_issue_date,
	pd.passport_expiry_date = p.passport_expiry_date
;

ALTER TABLE application_form_personal_detail
	DROP FOREIGN KEY personal_detail_language_qualification_fk,
	DROP FOREIGN KEY personal_detail_passport_information_fk,
	DROP COLUMN language_qualification_id,
	DROP COLUMN passport_information_id
;

DROP TABLE application_form_personal_detail_language_qualifications
;

DROP TABLE application_form_personal_detail_passport
;
