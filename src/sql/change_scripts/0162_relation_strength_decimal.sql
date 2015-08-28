alter table imported_program_subject_area
	modify column relation_strength decimal(10,9) unsigned not null
;

alter table imported_institution_subject_area
	modify column relation_strength decimal(10,9) unsigned not null
;

alter table user_institution
	modify column relation_strength decimal(10,9) unsigned not null
;

alter table user_program
	modify column relation_strength decimal(10,9) unsigned not null
;

alter table user_subject_area
	modify column relation_strength decimal(10,9) unsigned not null
;

alter table imported_program
	change column code ucas_code varchar(50),
	add column jacs_codes varchar(255),
	add column ucas_subjects varchar(255)
;

alter table imported_institution
	add column indexed int(1) unsigned not null default 0 after facebook_id,
	add index (indexed)
;

alter table imported_institution
	modify column indexed int(1) unsigned not null
;

alter table imported_program
	modify column jacs_codes varchar(255) after ucas_code,
	modify column ucas_subjects varchar(255) after jacs_codes,
	add column ucas_program_count int(10) unsigned not null after ucas_code
;
