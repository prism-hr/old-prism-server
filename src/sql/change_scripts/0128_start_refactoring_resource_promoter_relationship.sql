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
