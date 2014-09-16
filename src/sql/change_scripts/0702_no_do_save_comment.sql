ALTER TABLE ACTION
	DROP COLUMN do_save_comment
;

ALTER TABLE INSTITUTION
	ADD COLUMN logo_document_id INT(10) UNSIGNED AFTER homepage,
	ADD INDEX (logo_document_id),
	ADD FOREIGN KEY (logo_document_id) REFERENCES DOCUMENT(id)
;
