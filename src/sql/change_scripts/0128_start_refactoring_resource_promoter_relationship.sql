create table advert_promoter (
	id int(10) unsigned not null auto_increment,
	advert_id int(10) unsigned not null,
	institution_promoter_id int(10) unsigned not null,
	enabled int(1) unsigned not null,
	primary key (id),
	unique index (advert_id, institution_promoter_id),
	index (institution_promoter_id),
	index (advert_id, enabled),
	foreign key (advert_id) references advert (id),
	foreign key (institution_promoter_id) references institution (id))
collate = utf8_general_ci,
	engine = innodb
;

insert into advert_promoter(advert_id, institution_promoter_id, enabled)
	select advert_id, institution_partner_id, true
	from program
	where institution_partner_id is not null
;

insert into advert_promoter(advert_id, institution_promoter_id, enabled)
	select advert_id, institution_partner_id, true
	from project
	where institution_partner_id is not null
;

alter table imported_institution_subject_area
	drop primary key,
	add column id int(10) unsigned not null auto_increment first,
	add primary key (id),
	add unique index (imported_institution_id, imported_subject_area_id)
;

alter table imported_program_subject_area
	drop primary key,
	add column id int(10) unsigned not null auto_increment first,
	add primary key (id),
	add unique index (imported_program_id, imported_subject_area_id)
;

drop table advert_institution
;

rename table advert_promoter to advert_institution
;

alter table advert_institution
	change column institution_promoter_id institution_id int(10) unsigned not null
;

alter table advert_institution
	drop index institution_promoter_id,
	add index (institution_id)
;

create table advert_program (
	advert_id int(10) unsigned not null,
	imported_program_id int(10) unsigned not null,
	primary key (advert_id, imported_program_id),
	index (imported_program_id),
	foreign key (advert_id) references advert (id),
	foreign key (imported_program_id) references imported_program (id))
collate = utf8_general_ci,
	engine = innodb
;

alter table application
	drop foreign key application_ibfk_7,
	drop index institution_partner_id,
	drop column institution_partner_id
;

alter table project
	drop foreign key project_ibfk_10,
	drop index institution_partner_id,
	drop column institution_partner_id
;

alter table program
	drop foreign key program_ibfk_11,
	drop index institution_partner_id,
	drop column institution_partner_id
;

alter table institution
	drop foreign key institution_ibfk_12,
	drop index institution_partner_id,
	drop column institution_partner_id
;

alter table system
	drop foreign key system_ibfk_3,
	drop index institution_partner_id,
	drop column institution_partner_id
;
