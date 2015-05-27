alter table application_document
drop index cv_id,
add unique (cv_id)
;

alter table application_document
drop index personal_statement_id,
add unique (personal_statement_id)
;

alter table application_document
drop index covering_letter_id,
add unique (covering_letter_id)
;

alter table application_document
drop index research_statement_id,
add unique (research_statement_id)
;

alter table application_language_qualification
drop index document_id,
add unique (document_id)
;

