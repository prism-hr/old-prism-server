alter table advert
	add column logo_image_id int(10) unsigned after description,
	add column background_image_id int(10) unsigned after logo_image_id,
	add index (logo_image_id),
	add index (background_image_id),
	add foreign key (logo_image_id) references document (id),
	add foreign key (background_image_id) references document (id)
;

insert into advert (title, summary, description, logo_image_id, homepage, sequence_identifier)
	select title, summary, description, logo_document_id, homepage, 
		concat(unix_timestamp(updated_timestamp), "000", lpad(id, "0", 10))
	from institution
;

alter table institution
	add column advert_id int(10) unsigned after system_id,
	add index (advert_id, sequence_identifier),
	add foreign key (advert_id) references advert (id)
;

update institution inner join advert
	on institution.title = advert.title
set institution.advert_id = advert.id
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
