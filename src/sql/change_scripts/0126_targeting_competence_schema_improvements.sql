alter table imported_institution_subject_area
	add column relation_strength decimal (3,2) unsigned not null
;

alter table imported_program_subject_area
	add column relation_strength decimal (3,2) unsigned not null
;

alter table competence
	modify column description text not null
;
