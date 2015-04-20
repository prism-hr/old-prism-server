alter table advert
	add column locale varchar(10) after title,
	add column institution_id int(10) unsigned after id,
	add column program_id int(10) unsigned after institution_id,
	add column project_id int(10) unsigned after program_id,
	add index (locale, sequence_identifier),
	add index (institution_id, sequence_identifier),
	add index (program_id, sequence_identifier),
	add index (project_id, sequence_identifier),
	add foreign key (institution_id) references institution (id),
	add foreign key (program_id) references program (id),
	add foreign key (project_id) references project (id)
;

alter table program
	add column locale varchar(10) after title,
	add index (locale, sequence_identifier)
;

alter table project
	add column locale varchar(10) after title,
	add index (locale, sequence_identifier)
;

update institution inner join program
	on institution.id = program.institution_id
set program.locale = institution.locale
;

update institution inner join project
	on institution.id = project.institution_id
set project.locale = institution.locale
;

update program inner join advert
	on program.advert_id = advert.id
set advert.program_id = program.id,
	advert.locale = program.locale
;

update project inner join advert
	on project.advert_id = advert.id
set advert.project_id = project.id,
	advert.locale = project.locale
;

alter table advert
	add column logo_image_id int(10) unsigned after description,
	add column background_image_id int(10) unsigned after logo_image_id,
	add index (logo_image_id),
	add index (background_image_id),
	add foreign key (logo_image_id) references document (id),
	add foreign key (background_image_id) references document (id)
;

insert into advert (institution_id, title, locale, summary, description, logo_image_id, homepage, sequence_identifier)
	select id, title, locale, summary, description, logo_document_id, homepage, 
		concat(unix_timestamp(updated_timestamp), "000", lpad(id, "0", 10))
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

delete
from advert
where institution_id is null
	and program_id is null
	and project_id is null
;

alter table advert
	modify column locale varchar(10) not null
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

update advert inner join institution
	on advert.institution_id = institution.id
set advert.institution_address_id = institution.institution_address_id
;

alter table institution
	drop foreign key institution_ibfk_7,
	drop index institution_address_id,
	drop column institution_address_id
;

alter table project
	modify column user_id int(10) unsigned not null,
	drop column locale
;

alter table program
	modify column user_id int(10) unsigned not null,
	drop column locale
;

alter table institution
	drop column locale
	drop column default_program_type,
	drop column default_study_option,
	drop index institution_domicile_id,
	drop index title_2,
	add unique index (institution_domicile_id, user_id, title)
;

alter table advert
	add column currency varchar(10) after locale,
	add index (currency, sequence_identifier)
;

update advert inner join institution
	on advert.institution_id = institution.id
set advert.currency = institution.currency
;

update advert inner join program
	on advert.program_id = program.id
inner join institution
	on program.institution_id = institution.id
set advert.currency = institution.currency
;

update advert inner join project
	on advert.project_id = project.id
inner join institution
	on project.institution_id = institution.id
set advert.currency = institution.currency
;

alter table institution
	drop column currency
;

rename table program_location to advert_location
;

alter table advert_location
	add column advert_id int(10) unsigned after id,
	add unique index (advert_id, location),
	add foreign key (advert_id) references advert (id)
;

update program inner join advert_location
	on program.id = advert_location.program_id
set advert_location.advert_id = program.advert_id
;

alter table advert_location
	modify column advert_id int(10) unsigned not null,
	drop index program_id,
	drop foreign key advert_location_ibfk_1,
	drop column program_id
;

rename table program_study_option to advert_study_option
;

alter table advert_study_option
	add column advert_id int(10) unsigned after id,
	add unique index (advert_id, study_option_id),
	add foreign key (advert_id) references advert (id)
;

update program inner join advert_study_option
	on program.id = advert_study_option.program_id
set advert_study_option.advert_id = program.advert_id
;

alter table advert_study_option
	modify column advert_id int(10) unsigned not null,
	drop index program_id,
	drop foreign key advert_study_option_ibfk_1,
	drop column program_id
;

alter table program_study_option_instance
	change column program_study_option_id advert_study_option_id int(10) unsigned not null
;

update imported_entity
set imported_entity_type = "ADVERT_TYPE"
where imported_entity_type = "PROGRAM_TYPE"
;

update imported_entity_feed
set imported_entity_type = "ADVERT_TYPE",
	location = replace(location, "programType", "advertType")
where imported_entity_type = "PROGRAM_TYPE"
;

alter table advert
	add column advert_type_id int(10) unsigned after locale,
	add index (advert_type_id, sequence_identifier),
	add foreign key (advert_type_id) references imported_entity (id)
;

update advert inner join program
	on advert.program_id = program.id
set advert.advert_type_id = program.program_type_id
;

update advert inner join project
	on advert.project_id = project.id
inner join program
	on project.program_id = program.id
set advert.advert_type_id = program.program_type_id
;

alter table program
	drop foreign key program_ibfk_8,
	drop index program_type_id,
	drop column program_type_id
;

alter table notification_configuration
	change column program_type advert_type varchar(50)
;

alter table action_custom_question_configuration
	change column program_type advert_type varchar(50)
;

alter table state_duration_configuration
	change column program_type advert_type varchar(50)
;

alter table display_property_configuration
	change column program_type advert_type varchar(50)
;

alter table workflow_property_configuration
	change column program_type advert_type varchar(50)
;

rename table advert_program_type to advert_advert_type
;

alter table advert_advert_type
	change column program_type advert_type varchar(50) not null
;
