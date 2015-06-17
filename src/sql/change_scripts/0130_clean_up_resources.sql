alter table institution
	drop end_date
;

alter table program
	drop end_date
;

alter table project
	drop end_date
;

alter table advert
	add column background_image_id int(10) unsigned after description,
	add index (background_image_id),
	add foreign key (background_image_id) references document (id)
;

update advert inner join institution
	on advert.id = institution.advert_id
set advert.background_image_id = institution.background_image_id
;

alter table institution
	drop foreign key institution_ibfk_11,
	drop column background_image_id
;

update advert inner join program
	on advert.id = program.advert_id
set advert.background_image_id = program.background_image_id
;

alter table program
	drop foreign key program_ibfk_12,
	drop column background_image_id
;

update advert inner join project
	on advert.id = project.advert_id
set advert.background_image_id = project.background_image_id
;

alter table project
	drop foreign key project_ibfk_12,
	drop column background_image_id
;

alter table resource_study_option
	modify column application_start_date date,
	modify column application_close_date date
;

alter table project
	add column imported_code varchar(50) after code,
	add index (imported_code, sequence_identifier)
;

alter table application
	drop column referrer
;

alter table project
	drop column referrer
;

alter table program
	drop column referrer
;

alter table institution
	drop column referrer
;

alter table application
	drop index referrer
;

alter table project
	drop index referrer
;

alter table program
	drop index referrer
;

alter table institution
	drop index referrer
;

alter table program
	modify column imported_code varchar(50) after code,
	drop column imported,
	drop index imported
;

alter table system
	modify column cipher_salt varchar(36) not null after last_data_import_date,
	modify column amazon_access_key varchar(50) after cipher_salt,
	modify column amazon_secret_key varchar(50) after amazon_access_key
;

alter table imported_age_range
	drop foreign key imported_age_range_ibfk_1
;
