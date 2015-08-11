alter table imported_subject_area
	modify column jacs_code varchar(100) not null,
	add column jacs_code_old varchar(100) not null after jacs_code,
	add index (jacs_code_old),
	add column ucas_subject int(10) unsigned not null,
	add index (ucas_subject)
;

set foreign_key_checks = 0
;

alter table imported_subject_area
	modify column id int(10) unsigned not null
;

set foreign_key_checks = 1
;

alter table imported_subject_area
	modify column ucas_subject int(10) unsigned after description
;
