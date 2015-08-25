alter table imported_program_subject_area
	modify column relation_strength decimal(20,10) unsigned not null
;

alter table imported_institution_subject_area
	modify column relation_strength decimal(20,10) unsigned not null
;
