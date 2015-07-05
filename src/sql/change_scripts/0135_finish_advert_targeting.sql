alter table advert_subject_area
	drop primary key,
	add column id int(10) unsigned not null auto_increment first,
	add primary key (id),
	add unique index(advert_id, imported_subject_area_id),
	add column importance decimal(3,2) unsigned not null
;

rename table advert_promoter to advert_institution
;

alter table advert_institution
	add column importance decimal(3,2) unsigned not null
;

alter table advert_institution
	change column institution_promoter_id institution_id int(10) unsigned not null,
	modify column importance decimal(3,2) unsigned not null after institution_id
;

create table advert_department (
	id int(10) unsigned not null auto_increment,
	advert_id int(10) unsigned not null,
	department_id int(10) unsigned not null,
	importance decimal(3,2) unsigned not null,
	enabled int(1) unsigned not null,
	primary key (id),
	unique index (advert_id, department_id),
	index (department_id),
	index (advert_id, enabled),
	foreign key (advert_id) references advert (id),
	foreign key (department_id) references department (id))
collate = utf8_general_ci
engine = innodb
;

create table advert_program (
	id int(10) unsigned not null auto_increment,
	advert_id int(10) unsigned not null,
	imported_program_id int(10) unsigned not null,
	importance decimal(3,2) unsigned not null,
	enabled int(1) unsigned not null,
	primary key (id),
	unique index (advert_id, imported_program_id),
	index (imported_program_id),
	index (advert_id, enabled),
	foreign key (advert_id) references advert (id),
	foreign key (imported_program_id) references imported_program (id))
collate = utf8_general_ci
engine = innodb
;

drop table advert_domain
;

update imported_entity
set imported_entity_type = concat("IMPORTED_", imported_entity_type)
;

alter table imported_program
	drop column code,
	drop index code
;

alter table system
	change column last_data_import_date last_data_import_timestamp datetime
;

alter table imported_entity_feed
	drop column map_for_export
;

alter table imported_entity_feed
	drop column username,
	drop column password
;

alter table imported_age_range_mapping
	add column imported_timestamp timestamp not null default current_timestamp on update current_timestamp,
	add index (institution_id, imported_age_range_id, imported_timestamp)
;

alter table imported_entity_mapping
	add column imported_timestamp timestamp not null default current_timestamp on update current_timestamp,
	add index (institution_id, imported_entity_id, imported_timestamp)
;

alter table imported_institution_mapping
	add column imported_timestamp timestamp not null default current_timestamp on update current_timestamp,
	add index (institution_id, imported_institution_id, imported_timestamp)
;

alter table imported_language_qualification_type_mapping
	add column imported_timestamp timestamp not null default current_timestamp on update current_timestamp,
	add index (institution_id, imported_language_qualification_type_id, imported_timestamp)
;

alter table imported_program_mapping
	add column imported_timestamp timestamp not null default current_timestamp on update current_timestamp,
	add index (institution_id, imported_program_id, imported_timestamp)
;

alter table imported_subject_area_mapping
	add column imported_timestamp timestamp not null default current_timestamp on update current_timestamp,
	add index (institution_id, imported_subject_area_id, imported_timestamp)
;

update imported_program
set name = concat(qualification, " ", name)
where qualification is not null
;

alter table imported_program
	drop index imported_institution_id,
	add unique index(imported_institution_id, name)
;

alter table application_qualification
	change column program_id imported_program_id int(10) unsigned not null
;

alter table resource_study_option_instance
	change column academic_year business_year varchar(4) not null
;

alter table advert_closing_date
	drop column study_places
;

alter table application
	add column department_id int(10) unsigned after institution_partner_id,
	add index (department_id, sequence_identifier),
	add foreign key (department_id) references department (id)
;

update application inner join program
	on application.program_id = program.id
set application.department_id = program.department_id
;

alter table resource_study_location
	drop foreign key resource_study_location_ibfk_2,
	drop index institution_id,
	drop column institution_id
;

alter table resource_study_option
	drop foreign key resource_study_option_ibfk_3,
	drop index institution_id,
	drop column institution_id
;

insert into advert_institution(advert_id, institution_id, importance, enabled)
	select advert.id, project.institution_id, 1.00, 1
	from advert inner join project
		on advert.id = project.advert_id
	where project.institution_partner_id is not null
		union
	select advert.id, program.institution_id, 1.00, 1
	from advert inner join program
		on advert.id = program.advert_id
	where program.institution_partner_id is not null
;

update project
set institution_id = institution_partner_id,
	institution_partner_id = null
where institution_partner_id is not null
;

update program
set institution_id = institution_partner_id,
	institution_partner_id = null
where institution_partner_id is not null
;

alter table display_property_configuration
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id, opportunity_type, display_property_definition_id),
	add foreign key (department_id) references department (id)
;

alter table resource_condition
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id, action_condition),
	add foreign key (department_id) references department (id)
;

alter table resource_previous_state
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id, state_id),
	add foreign key (department_id) references department (id)
;

alter table resource_state
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id, state_id),
	add foreign key (department_id) references department (id)
;

alter table resource_state_transition_summary
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id, state_group_id, transition_state_selection),
	add foreign key (department_id) references department (id)
;

alter table system	
	drop foreign key system_ibfk_3,
	drop index institution_partner_id,
	drop column institution_partner_id
;

alter table institution	
	drop foreign key institution_ibfk_12,
	drop index institution_partner_id,
	drop column institution_partner_id
;

alter table program	
	drop foreign key program_ibfk_11,
	drop index institution_partner_id,
	drop column institution_partner_id
;

alter table project
	drop foreign key project_ibfk_10,
	drop index institution_partner_id,
	drop column institution_partner_id
;

alter table application
	drop foreign key application_ibfk_7,
	drop index institution_partner_id,
	drop column institution_partner_id
;

alter table user_feedback
	add column department_id int(10) unsigned after institution_id,
	add index (department_id, sequence_identifier),
	add foreign key (department_id) references department (id)
;

alter table user_notification
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id, user_id, notification_definition_id),
	add foreign key (department_id) references department (id)
;

alter table user_role
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id, user_id, role_id),
	add foreign key (department_id) references department (id)
;

alter table action_custom_question_configuration
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id, opportunity_type, action_custom_question_definition_id, version, display_index),
	add foreign key (department_id) references department (id)
;

alter table notification_configuration
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id, opportunity_type, notification_definition_id),
	add foreign key (department_id) references department (id)
;

alter table state_duration_configuration
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id, opportunity_type, state_duration_definition_id),
	add foreign key (department_id) references department (id)
;

alter table state_transition_pending
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id, action_id),
	add foreign key (department_id) references department (id)
;

alter table workflow_property_configuration
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id, opportunity_type, workflow_property_definition_id, version),
	add foreign key (department_id) references department (id)
;

alter table comment
	add column department_id int(10) unsigned after institution_id,
	add unique index (department_id),
	add foreign key (department_id) references department (id),
	drop foreign key comment_ibfk_17,
	drop index institution_partner_id,
	drop column institution_partner_id,
	drop column removed_partner
;

rename table institution_domicile to advert_domicile
;

alter table institution_address
	drop foreign key institution_address_ibfk_2
;

alter table institution_address
	change column institution_domicile_id advert_domicile_id varchar(10) not null,
	add foreign key (advert_domicile_id) references advert_domicile (id)
;

rename table institution_address to advert_address
;

alter table application_personal_detail
	modify column imported_nationality_id1 int(10) unsigned after imported_domicile_id,
	modify column imported_nationality_id2 int(10) unsigned after imported_nationality_id1,
	modify column first_language_locale int(1) unsigned after imported_nationality_id2,
	modify column application_language_qualification_id int(10) unsigned after first_language_locale,
	modify column imported_domicile_id int(10) unsigned after application_language_qualification_id,
	modify column visa_required int(1) unsigned after imported_domicile_id,
	modify column application_passport_id int(10) unsigned after visa_required,
	modify column phone varchar(50) not null after application_passport_id,
	modify column skype varchar(50) after phone,
	modify column imported_ethnicity_id int(10) unsigned after skype,
	modify column imported_disability_id int(10) unsigned after imported_ethnicity_id
;

alter table user
	drop foreign key user_ibfk_2,
	drop index portrait_image_id,
	drop column portrait_image_id
;

alter table user_account
	add column portrait_image_id int(10) unsigned after user_account_external_id,
	add index (portrait_image_id),
	add foreign key (portrait_image_id) references document (id)
;

alter table comment
	add column application_export_succeeded int(1) unsigned after application_rating
;

update comment
set application_export_succeeded = 
	if((action_id = "APPLICATION_EXPORT" and application_export_reference is not null), 1, 0)
where action_id = "APPLICATION_EXPORT"
;

drop table imported_entity_feed
;

rename table advert_domicile to imported_advert_domicile
;

alter table advert_address
	change column advert_domicile_id imported_advert_domicile_id varchar(10) not null
;

create table imported_advert_domicile_mapping (
	id int(10) unsigned not null auto_increment,
	institution_id int(10) unsigned not null,
	imported_advert_domicile_id varchar(10) not null,
	code varchar(50),
	enabled int(1) unsigned not null,
	imported_timestamp timestamp default current_timestamp on update current_timestamp,
	primary key (id),
	unique index (institution_id, imported_advert_domicile_id),
	index (institution_id, enabled),
	index (institution_id, imported_advert_domicile_id, imported_timestamp),
	foreign key (institution_id) references institution (id),
	foreign key (imported_advert_domicile_id) references imported_advert_domicile (id)) 
collate = utf8_general_ci
engine = innodb
;

alter table imported_subject_area
	change column code jacs_code varchar(50) not null
;

alter table system
	drop column last_data_import_timestamp
;
