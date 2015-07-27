alter table imported_program_subject_area
	add column enabled int(1) unsigned not null,
	add index (enabled)
;

alter table imported_institution_subject_area
	add column enabled int(1) unsigned not null,
	add index (enabled)
;
