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
