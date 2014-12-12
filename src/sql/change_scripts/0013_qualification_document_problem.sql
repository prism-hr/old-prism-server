update application_qualification inner join document
	on application_qualification.document_id = document.id
	set application_qualification.document_id = null
where document.file_name = "Amjad AlQahtani UCL 2014.pdf"
;

alter table imported_entity
	drop foreign key imported_entity_ibfk_2,
	drop column root_id
;

alter table imported_institution
	drop foreign key imported_institution_ibfk_3,
	drop column root_id
;

alter table imported_language_qualification_type
	drop foreign key imported_language_qualification_type_ibfk_2,
	drop column root_id
;

update system
set helpdesk = "http://uclprism.freshdesk.com/"
;
