alter table notification_configuration
	add column project_id int(10) unsigned after program_id,
	add index (project_id),
	add foreign key (project_id) references project (id)
;

alter table display_property_configuration
	add column project_id int(10) unsigned after program_id,
	add index (project_id),
	add foreign key (project_id) references project (id)
;

alter table action_custom_question_configuration
	add column project_id int(10) unsigned after program_id,
	add index (project_id),
	add foreign key (project_id) references project (id)
;

alter table state_duration_configuration
	add column project_id int(10) unsigned after program_id,
	add index (project_id),
	add foreign key (project_id) references project (id)
;

alter table workflow_property_configuration
	add column project_id int(10) unsigned after program_id,
	add index (project_id),
	add foreign key (project_id) references project (id)
;

alter table notification_configuration
	drop index institution_id,
	add unique index (institution_id, locale, program_type, notification_definition_id),
	drop index program_id,
	add unique index (program_id, locale, notification_definition_id),
	drop index project_id,
	add unique index (project_id, locale, notification_definition_id)
;

alter table display_property_configuration
	drop index institution_id,
	add unique index (institution_id, locale, program_type, display_property_definition_id),
	drop index program_id,
	add unique index (program_id, locale, display_property_definition_id),
	drop index project_id,
	add unique index (project_id, locale, display_property_definition_id)
;

alter table action_custom_question_configuration
	drop index institution_id,
	add unique index (institution_id, locale, program_type, action_custom_question_definition_id),
	drop index program_id,
	add unique index (program_id, locale, action_custom_question_definition_id),
	drop index project_id,
	add unique index (project_id, locale, action_custom_question_definition_id)
;

alter table state_duration_configuration
	drop index institution_id,
	add unique index (institution_id, locale, program_type, state_duration_definition_id),
	drop index program_id,
	add unique index (program_id, locale, state_duration_definition_id),
	drop index project_id,
	add unique index (project_id, locale, state_duration_definition_id)
;

alter table workflow_property_configuration
	drop index institution_id,
	add unique index (institution_id, locale, program_type, workflow_property_definition_id),
	drop index program_id,
	add unique index (program_id, locale, workflow_property_definition_id),
	drop index project_id,
	add unique index (project_id, locale, workflow_property_definition_id)
;

alter table program
	add column locale varchar(10) after title
;

update program inner join institution
	on program.institution_id = institution.id
set program.locale = institution.locale
;

alter table program
	modify column locale varchar(10) not null
;

alter table project
	add column locale varchar(10) after title
;

update project inner join program
	on project.program_id = program.id
set project.locale = program.locale
;

alter table project
	modify column locale varchar(10) not null
;

alter table institution
	change column default_program_type program_type varchar (50) not null after locale
;

alter table program
	add column program_type varchar(50) after locale
;

update program inner join imported_entity
	on program.program_type_id = imported_entity.id
set program.program_type = imported_entity.code
;

alter table program
	modify column program_type varchar(50) not null
;

alter table program
	change column program_type_id imported_program_type_id int(10) unsigned not null
;

alter table project
	add column program_type varchar(50) after locale
;

update project inner join program
	on project.program_id = program.id
set project.program_type = program.program_type
;

alter table project
	modify column program_type varchar(50) not null
;

alter table institution
	add index (system_id, locale, program_type, sequence_identifier)
;

alter table program
	add index (system_id, institution_id, locale, program_type, sequence_identifier)
;

alter table project
	add index (system_id, institution_id, program_id, locale, program_type, sequence_identifier)
;

alter table advert
	add column institution_id int(10) unsigned after id,
	add column program_id int(10) unsigned after institution_id,
	add column project_id int(10) unsigned after program_id,
	add column locale varchar(10) after project_id,
	add column program_type varchar(50) after locale,
	add unique index (institution_id, locale, program_type),
	add unique index (program_id, locale, program_type),
	add unique index (project_id, locale, program_type),
	add index (institution_id, locale, program_type, sequence_identifier),
	add index (program_id, locale, program_type, sequence_identifier),
	add index (project_id, locale, program_type, sequence_identifier),
	add foreign key (institution_id) references institution (id),
	add foreign key (program_id) references program (id),
	add foreign key (project_id) references project (id)
;

alter table program
	modify column imported_program_type_id int(10) unsigned not null after imported_code,
	modify column referrer varchar(255) after advert_id
;

alter table project
	modify advert_id int(10) unsigned not null after program_id
;

alter table institution 
	modify column user_id int(10) unsigned not null after id,
	modify column institution_domicile_id varchar(10) not null after code,
	add column advert_id int(10) unsigned after system_id,
	add index (advert_id, sequence_identifier),
	add foreign key (advert_id) references advert (id)
;

alter table institution
	drop foreign key institution_ibfk_6,
	drop column institution_domicile_id
;

alter table institution
	drop column default_study_option
;

alter table advert
	drop column default_start_date
;

alter table advert
	add column logo_image_id int(10) unsigned after description,
	add column background_image_id int(10) unsigned after logo_image_id,
	add index (logo_image_id),
	add index (background_image_id),
	add foreign key (logo_image_id) references document (id),
	add foreign key (background_image_id) references document (id)
;

alter table institution
	drop column helpdesk
;

alter table system
	drop column helpdesk
;

alter table system
	add column program_type varchar(50) after locale
;

update system
set program_type = "POSTGRADUATE_RESEARCH_STUDY"
;

alter table system
	modify column program_type varchar(50) not null
;

insert into advert(institution_id, locale, program_type, title, summary, description, homepage,
	fee_currency_specified, pay_currency_specified, logo_image_id, institution_address_id)
	select id, locale, program_type, title, summary, description, homepage, currency, currency,
		logo_document_id, institution_address_id
	from institution
;

update advert inner join institution
	on advert.institution_id = institution.id
set institution.advert_id = advert.id
;

alter table institution
	drop foreign key institution_ibfk_7,
	drop foreign key institution_ibfk_8,
	drop column summary,
	drop column description, 
	drop column homepage, 
	drop column currency, 
	drop column logo_document_id, 
	drop column institution_address_id
;

alter table institution
	drop index institution_domicile_id_2,
	drop index institution_address_id
;

alter table institution
	modify column advert_id int(10) unsigned not null
;

update advert inner join institution
	on advert.institution_id = institution.id
set advert.sequence_identifier = concat(unix_timestamp(institution.updated_timestamp), "000", lpad(advert.id, 10, "0"))
;

update advert inner join program
	on program.advert_id = advert.id
set advert.program_id = program.id
;

update advert inner join project
	on project.advert_id = advert.id
set advert.project_id = project.id
;

alter table institution_address
	add column google_id varchar(255) after address_code
;

update institution inner join advert
	on institution.advert_id = advert.id
inner join institution_address
	on advert.institution_address_id = institution_address.id
set institution_address.google_id = institution.google_id
;

alter table institution
	drop column google_id
;

alter table institution
	drop index title_2,
	add unique index (user_id, title)
;

alter table institution
	drop index institution_domicile_id
;

update institution_address
set google_id = "ChIJeTUXaS8bdkgRPhPMrn_GCNA",
	location_x = 51.52465710000000,
	location_y = -0.13371010000000,
	location_view_ne_x = 51.52606378029149,
	location_view_ne_y = -0.13221946970850,
	location_view_sw_x = -0.13491743029150
where address_code = "WC1E 6BT"
;

alter table institution
	modify column advert_id int(10) unsigned
;

alter table institution_address
	modify column institution_id int(10) unsigned
;

rename table program_study_option to advert_study_option
;

alter table advert_study_option
	add column advert_id int(10) unsigned after id,
	add unique index (advert_id, study_option_id),
	add foreign key (advert_id) references advert (id)
;

update advert_study_option inner join program
	on advert_study_option.program_id = program.id
set advert_study_option.advert_id = program.advert_id
;

alter table advert_study_option
	drop index program_id,
	drop foreign key advert_study_option_ibfk_1
;

insert into advert_study_option(program_id, advert_id, study_option_id, application_start_date, application_close_date, enabled)
	select project.program_id, project.advert_id, advert_study_option.study_option_id, advert_study_option.application_start_date,
		advert_study_option.application_close_date, advert_study_option.enabled
	from project inner join advert_study_option
		on project.program_id = advert_study_option.program_id
;

alter table advert_study_option
	drop column program_id
;

rename table program_study_option_instance to advert_study_option_instance
;

alter table advert_study_option_instance
	change column program_study_option_id advert_study_option_id int(10) unsigned not null
;

