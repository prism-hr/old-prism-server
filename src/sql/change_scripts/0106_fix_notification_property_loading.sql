update notification_configuration
set subject = replace(subject, "TEMPLATE_HELPDESK", "TEMPLATE_SYSTEM_HELPDESK"),
	content = replace(content, "TEMPLATE_HELPDESK", "TEMPLATE_SYSTEM_HELPDESK")
;

update notification_configuration
set subject = replace(subject, "APPLICATION_PROJECT_OR_PROGRAM", "APPLICATION_PARENT_RESOURCE"),
	content = replace(content, "APPLICATION_PROJECT_OR_PROGRAM", "APPLICATION_PARENT_RESOURCE")
;

alter table application_program_detail
	add column opportunity_type_id int(10) unsigned after id,
	add index (opportunity_type_id),
	add foreign key (opportunity_type_id) references imported_entity (id)
;

update application_program_detail inner join application
	on application_program_detail.id = application.application_program_detail_id
inner join program
	on application.program_id = program.id
set application_program_detail.opportunity_type_id = program.opportunity_type_id
;

delete 
from application_program_detail
where opportunity_type_id is null
;

alter table application_program_detail
	modify opportunity_type_id int(10) unsigned not null
;
