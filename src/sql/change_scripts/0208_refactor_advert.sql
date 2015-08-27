alter table advert
	add column imported_opportunity_type_id int(10) unsigned,
	add index (imported_opportunity_type_id, sequence_identifier),
	add foreign key (imported_opportunity_type_id) references imported_entity (id)
;

update advert inner join project
	on project.advert_id = advert.id
set advert.imported_opportunity_type_id = project.imported_opportunity_type_id
;

update advert inner join program
	on program.advert_id = advert.id
set advert.imported_opportunity_type_id = program.imported_opportunity_type_id
;

alter table advert
	add column user_id int(10) unsigned after id,
	add index (user_id, sequence_identifier)
;

alter table advert
	add foreign key (user_id) references user (id)
;

update institution inner join advert
	on institution.advert_id = advert.id
set advert.user_id = institution.user_id
;

update department inner join advert
	on department.advert_id = advert.id
set advert.user_id = department.user_id
;

update program inner join advert
	on program.advert_id = advert.id
set advert.user_id = program.user_id
;

update project inner join advert
	on project.advert_id = advert.id
set advert.user_id = project.user_id
;

alter table advert
	modify column user_id int(10) unsigned not null
;

