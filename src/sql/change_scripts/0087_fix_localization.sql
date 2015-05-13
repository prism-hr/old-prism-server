alter table advert
	add column logo_image_id int(10) unsigned after description,
	add column background_image_id int(10) unsigned after logo_image_id,
	add column institution_id int(10) unsigned,
	add index (logo_image_id),
	add index (background_image_id),
	add foreign key (logo_image_id) references document (id),
	add foreign key (background_image_id) references document (id)
;

insert into advert (title, summary, description, logo_image_id, homepage, sequence_identifier, institution_id)
	select title, summary, description, logo_document_id, homepage, 
		concat(unix_timestamp(updated_timestamp), "000", lpad(id, "0", 10)),  id
	from institution
;

alter table institution
	add column advert_id int(10) unsigned after system_id,
	add index (advert_id, sequence_identifier),
	add foreign key (advert_id) references advert (id)
;

update institution inner join advert
	on institution.id = advert.institution_id
set institution.advert_id = advert.id
;

alter table advert
	drop column institution_id
;

delete advert.*
from advert left join institution
	on advert.id = institution.advert_id
left join program
	on advert.id = program.advert_id
left join project
	on advert.id = project.advert_id
where institution.id is null
	and program.id is null
	and project.id is null
;

alter table institution
	drop column summary,
	drop column description,
	drop column homepage,
	drop index logo_document_id,
	drop foreign key institution_ibfk_8,
	drop column logo_document_id
;

alter table advert
	drop column default_start_date
;

update institution inner join advert
	on institution.advert_id = advert.id
set advert.institution_address_id = institution.institution_address_id
;

alter table institution
	drop foreign key institution_ibfk_7,
	drop index institution_address_id,
	drop column institution_address_id
;

alter table project
	modify column user_id int(10) unsigned not null
;

alter table program
	modify column user_id int(10) unsigned not null
;

alter table institution
	drop column default_program_type,
	drop column default_study_option,
	drop index institution_domicile_id,
	drop index title_2,
	add unique index (institution_domicile_id, user_id, title)
;

alter table imported_institution
	drop index institution_id,
	add unique index (institution_id, code)
;

alter table institution_domicile
	drop index code,
	drop index location_x,
	drop index location_y,
	drop column location_x,
	drop column location_y,
	drop column location_view_ne_x,
	drop column location_view_ne_y,
	drop column location_view_sw_x,
	drop column location_view_sw_y;
;

drop table advert_institution
;

drop table advert_program_type
;

delete
from advert_domain
;

alter table action_custom_question_configuration
	drop column locale
;

alter table display_property_configuration
	drop column locale
;

alter table institution
	drop column locale
;

alter table notification_configuration
	drop column locale
;

alter table state_duration_configuration
	drop column locale
;

alter table system
	drop column locale
;

alter table user
	drop column locale
;

alter table workflow_property_configuration
	drop column locale
;

alter table advert
	drop column month_study_duration_minimum,
	drop column month_study_duration_maximum,
	drop index month_study_duration_minimum,
	drop index month_study_duration_maximum
;

alter table program_study_option
	add column institution_id int(10) unsigned after id,
	add column project_id int(10) unsigned after program_id,
	modify column program_id int(10) unsigned,
	add unique index (institution_id, study_option_id),
	add unique index (project_id, study_option_id),
	add foreign key (institution_id) references institution (id),
	add foreign key (program_id) references program (id),
	add foreign key (project_id) references project (id)
;

rename table program_study_option to resource_study_option
;

rename table program_study_option_instance to resource_study_option_instance
;

alter table advert
	add column advert_type_id int(10) unsigned after id,
	add index (advert_type_id, sequence_identifier),
	add foreign key (advert_type_id) references imported_entity (id)
;

update program inner join advert
	on program.advert_id = advert.id
set advert.advert_type_id = program.program_type_id
;

update project inner join program
	on project.program_id = program.id
inner join advert
	on project.advert_id = advert.id
set advert.advert_type_id = program.program_type_id
;

alter table program
	drop index program_type_id,
	drop foreign key program_ibfk_8,
	drop column program_type_id
;

alter table institution
	drop column helpdesk
;

alter table system
	drop column helpdesk
;

drop table application_processing
;

drop table application_processing_summary
;

alter table program_location
	add column institution_id int(10) unsigned after id,
	add column project_id int(10) unsigned after program_id,
	modify column program_id int(10) unsigned,
	add unique index (institution_id, location),
	add unique index (project_id, location),
	add foreign key (institution_id) references institution (id),
	add foreign key (program_id) references program (id),
	add foreign key (project_id) references project (id)
;

rename table program_location to resource_location
;

alter table program
	add column opportunity_type_id int(10) unsigned after advert_id,
	add index (opportunity_type_id, sequence_identifier),
	add foreign key (opportunity_type_id) references imported_entity (id)
;

update program inner join advert
	on program.advert_id = advert.id
set program.opportunity_type_id = advert.advert_type_id
;

alter table program
	modify column opportunity_type_id int(10) unsigned not null
;

alter table project
	add column opportunity_type_id int(10) unsigned after advert_id,
	add index (opportunity_type_id, sequence_identifier),
	add foreign key (opportunity_type_id) references imported_entity (id)
;

update project inner join advert
	on project.advert_id = advert.id
set project.opportunity_type_id = advert.advert_type_id
;

alter table project
	modify column opportunity_type_id int(10) unsigned not null
;

alter table advert
	drop index advert_type_id,
	drop foreign key advert_ibfk_6,
	drop column advert_type_id
;

update imported_entity_feed
set imported_entity_type = "OPPORTUNITY_TYPE"
where imported_entity_type = "PROGRAM_TYPE"
;

update imported_entity
set imported_entity_type = "OPPORTUNITY_TYPE"
where imported_entity_type = "PROGRAM_TYPE"
;

alter table action_custom_question_configuration
	add column project_id int(10) unsigned after program_id,
	add unique index (project_id, action_custom_question_definition_id, version, display_index),
	add index (project_id, action_custom_question_definition_id, active),
	add foreign key (project_id) references project (id),
	change column program_type opportunity_type varchar(50)
;

alter table notification_configuration
	add column project_id int(10) unsigned after program_id,
	add unique index (project_id, notification_definition_id),
	add foreign key (project_id) references project (id),
	change column program_type opportunity_type varchar(50)
;

alter table display_property_configuration
	add column project_id int(10) unsigned after program_id,
	add unique index (project_id, display_property_definition_id),
	add foreign key (project_id) references project (id),
	change column program_type opportunity_type varchar(50)
;

alter table state_duration_configuration
	add column project_id int(10) unsigned after program_id,
	add unique index (project_id, state_duration_definition_id),
	add foreign key (project_id) references project (id),
	change column program_type opportunity_type varchar(50)
;

alter table workflow_property_configuration
	add column project_id int(10) unsigned after program_id,
	add unique index (project_id, workflow_property_definition_id, version),
	add foreign key (project_id) references project (id),
	change column program_type opportunity_type varchar(50)
;

alter table project
	add column duration_minimum int(3) unsigned after title,
	add column duration_maximum int(3) unsigned after duration_minimum,
	add index (duration_minimum, duration_maximum, sequence_identifier)
;

alter table program
	add column duration_minimum int(3) unsigned after title,
	add column duration_maximum int(3) unsigned after duration_minimum,
	add index (duration_minimum, duration_maximum, sequence_identifier)
;

update program
set duration_minimum = 12,
	duration_maximum = 24
where title like "MRes%"
	or title like "DPA%"
	or title like "MD(Res)%"
;

update program
set duration_minimum = 36,
	duration_maximum = 48
where title like "EngD%"
	or title LIKE "Research Degree%"
;

update project inner join program
	on project.program_id = program.id
set project.duration_minimum = program.duration_minimum,
	project.duration_maximum = program.duration_maximum
;

alter table institution
	drop column application_created_count,
	drop index application_created_count,
	drop column application_submitted_count,
	drop index application_submitted_count,
	drop column application_approved_count,
	drop index application_approved_count,
	drop column application_rejected_count,
	drop index application_rejected_count,
	drop column application_withdrawn_count,
	drop index application_withdrawn_count, 
	drop column application_rating_count_average_non_zero,
	drop column application_rating_count,
	drop index application_rating_count_average,
	drop column application_rating_average,
	drop index application_rating_average
;

alter table program
	drop column application_created_count,
	drop index application_created_count,
	drop column application_submitted_count,
	drop index application_submitted_count,
	drop column application_approved_count,
	drop index application_approved_count,
	drop column application_rejected_count,
	drop index application_rejected_count,
	drop column application_withdrawn_count,
	drop index application_withdrawn_count, 
	drop column application_rating_count_average_non_zero,
	drop column application_rating_count,
	drop index application_rating_count_average,
	drop column application_rating_average,
	drop index application_rating_average
;

alter table project
	drop column application_created_count,
	drop index application_created_count,
	drop column application_submitted_count,
	drop index application_submitted_count,
	drop column application_approved_count,
	drop index application_approved_count,
	drop column application_rejected_count,
	drop index application_rejected_count,
	drop column application_withdrawn_count,
	drop index application_withdrawn_count, 
	drop column application_rating_count_average_non_zero,
	drop column application_rating_count,
	drop index application_rating_count_average,
	drop column application_rating_average,
	drop index application_rating_average
;

alter table institution
	add column business_year_start_month int(2) unsigned after currency
;

update institution
set business_year_start_month = 10
;

alter table institution
	modify column business_year_start_month int(2) unsigned not null
;

update application set application_year =
	concat(
		if(month(created_timestamp) < 10, (year(created_timestamp) - 1), year(created_timestamp)), "/",
		if(month(created_timestamp) < 10, year(created_timestamp), (year(created_timestamp) + 1)))
;

update application
set application_month = month(created_timestamp)
;

alter table application
	add column application_month_sequence int(2) unsigned after application_month,
	drop index institution_id_4,
	add index (institution_id, application_year, application_month_sequence),
	add index (program_id, application_year, application_month_sequence),
	add index (project_id, application_year, application_month_sequence)
;

update application
set application_month_sequence = 
	if(application_month > 9, 
		(application_month - 9),
		(application_month + (12 - 9)))
;

alter table application
	modify column application_month_sequence int(2) unsigned
;

alter table application
	modify column application_year varchar(10),
	modify column application_month int(2) unsigned
;

alter table advert
	add column institution_partner_id int(10) unsigned after apply_homepage,
	add index (institution_partner_id, sequence_identifier),
	add foreign key (institution_partner_id) references institution (id)
;

alter table institution
	add column logo_image_id int(10) unsigned after title,
	add column background_image_id int(10) unsigned after logo_image_id,
	add index (logo_image_id),
	add index (background_image_id),
	add foreign key (logo_image_id) references document (id),
	add foreign key (background_image_id) references document (id)
;

update institution inner join advert
	on institution.advert_id = advert.id
set institution.logo_image_id = advert.logo_image_id,
	institution.background_image_id = advert.background_image_id
;

alter table advert
	drop index logo_image_id,
	drop foreign key advert_ibfk_4,
	drop column logo_image_id,
	drop index background_image_id,
	drop foreign key advert_ibfk_5,
	drop column background_image_id
;

rename table resource_location to resource_study_location
;

alter table institution_address
	drop index institution_id,
	drop foreign key institution_address_ibfk_5,
	drop column institution_id
;

alter table resource_study_location
	change column location study_location varchar(255) not null
;

alter table institution_address
	add column google_id varchar(255) after address_code,
	add index (google_id)
;

alter table institution
	add column end_date date after previous_state_id,
	add index (end_date)
;

alter table resource_study_option
	drop column enabled
;

alter table resource_study_option_instance
	drop column enabled
;

alter table state_action_assignment
	drop index delegated_action_id,
	drop foreign key state_action_assignment_ibfk_3,
	drop column delegated_action_id
;
