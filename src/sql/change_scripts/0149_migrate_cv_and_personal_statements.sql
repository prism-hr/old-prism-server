UPDATE APPLICATION_FORM as a set cv_id = (select id from DOCUMENT where document_type = 'CV' and application_form_id =a.id)
;
UPDATE APPLICATION_FORM as a set personal_statement_id = (select id from DOCUMENT where document_type = 'PERSONAL_STATEMENT' and application_form_id = a.id)
;