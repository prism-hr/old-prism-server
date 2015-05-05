alter table program
	add column institution_partner_id int(10) unsigned after institution_id,
	add index (institution_partner_id, sequence_identifier),
	add foreign key (institution_partner_id) references institution (id)
;

update advert inner join program
	on advert.id = program.advert_id
set program.institution_partner_id = advert.institution_partner_id
;

alter table project
	add column institution_partner_id int(10) unsigned after institution_id,
	add index (institution_partner_id, sequence_identifier),
	add foreign key (institution_partner_id) references institution (id)
;

update advert inner join project
	on advert.id = project.advert_id
set project.institution_partner_id = advert.institution_partner_id
;

alter table advert
	drop index institution_partner_id,
	drop index institution_partner_id_2,
	drop foreign key advert_ibfk_6,
	drop column institution_partner_id
;

alter table project
	drop foreign key project_program_fk
;

alter table project
	modify column program_id int(10) unsigned,
	add foreign key (program_id) references program (id)
;
