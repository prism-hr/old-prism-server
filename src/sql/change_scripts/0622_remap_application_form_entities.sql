CREATE TABLE APPLICATION_FORM_ADDRESS (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	current_address_id INT(10) UNSIGNED NOT NULL,
	contact_address_id INT(10) UNSIGNED NOT NULL,
	application_form_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	INDEX (current_address_id),
	INDEX (contact_address_id),
	FOREIGN KEY (current_address_id) REFERENCES ADDRESS (id),
	FOREIGN KEY (contact_address_id) REFERENCES ADDRESS (id))
ENGINE = INNODB
;

INSERT INTO APPLICATION_FORM_ADDRESS (current_address_id, contact_address_id, application_form_id)
	SELECT current_address_id, contact_address_id, id
	FROM APPLICATION_FORM
	WHERE current_address_id IS NOT NULL
		AND contact_address_id IS NOT NULL
;

ALTER TABLE APPLICATION_FORM	
	ADD COLUMN application_form_address_id INT(10) UNSIGNED,
	ADD INDEX (application_form_address_id),
	ADD CONSTRAINT application_form_address_fk FOREIGN KEY (application_form_address_id) REFERENCES APPLICATION_FORM_ADDRESS (id),
	DROP FOREIGN KEY current_address_fk,
	DROP FOREIGN KEY contact_address_fk,
	DROP COLUMN current_address_id,
	DROP COLUMN contact_address_id
;	

UPDATE APPLICATION_FORM INNER JOIN APPLICATION_FORM_ADDRESS
	ON APPLICATION_FORM.id = APPLICATION_FORM_ADDRESS.application_form_id
SET APPLICATION_FORM.application_form_address_id = APPLICATION_FORM_ADDRESS.id
;

ALTER TABLE APPLICATION_FORM_ADDRESS
	DROP COLUMN application_form_id
;

CREATE TABLE APPLICATION_FORM_DOCUMENT (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	cv_id INT(10) UNSIGNED NOT NULL,
	personal_statement_id INT(10) UNSIGNED,
	application_form_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	INDEX (cv_id),
	INDEX (personal_statement_id),
	FOREIGN KEY (cv_id) REFERENCES DOCUMENT (id),
	FOREIGN KEY (personal_statement_id) REFERENCES DOCUMENT (id))
ENGINE = INNODB
;

INSERT INTO APPLICATION_FORM_DOCUMENT (cv_id, personal_statement_id, application_form_id)
	SELECT cv_id, personal_statement_id, id
	FROM APPLICATION_FORM
	WHERE cv_id IS NOT NULL
		AND personal_statement_id IS NOT NULL
;

ALTER TABLE APPLICATION_FORM	
	ADD COLUMN application_form_document_id INT(10) UNSIGNED,
	ADD INDEX (application_form_document_id),
	ADD CONSTRAINT application_form_document_fk FOREIGN KEY (application_form_document_id) REFERENCES APPLICATION_FORM_DOCUMENT (id),
	DROP FOREIGN KEY cv_fk,
	DROP FOREIGN KEY personal_statement_fk,
	DROP COLUMN cv_id,
	DROP COLUMN personal_statement_id
;	

UPDATE APPLICATION_FORM INNER JOIN APPLICATION_FORM_DOCUMENT
	ON APPLICATION_FORM.id = APPLICATION_FORM_DOCUMENT.application_form_id
SET APPLICATION_FORM.application_form_document_id = APPLICATION_FORM_DOCUMENT.id
;

ALTER TABLE APPLICATION_FORM_DOCUMENT
	DROP COLUMN application_form_id
;
