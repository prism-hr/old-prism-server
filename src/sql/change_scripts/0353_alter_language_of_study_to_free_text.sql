ALTER TABLE APPLICATION_FORM_QUALIFICATION 
DROP FOREIGN KEY language_qual_fk,
DROP COLUMN language_id
;
ALTER TABLE APPLICATION_FORM_QUALIFICATION ADD COLUMN qualification_language VARCHAR(70)
;
