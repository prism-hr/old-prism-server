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

alter table state_duration_definition
	add column escalation int(1) unsigned not null default 0 after id
;

alter table state_duration_definition
	modify column escalation int(1) unsigned not null after id
;

update notification_configuration
set content = REPLACE(content, "TEMPLATE_VIEW_EDIT", "ACTION_VIEW_EDIT"),
	subject = REPLACE(subject, "TEMPLATE_VIEW_EDIT", "ACTION_VIEW_EDIT")
;

update notification_configuration
set content = replace(content, "at ${COMMENT_DATE_TIME}", "for your institution")
;
