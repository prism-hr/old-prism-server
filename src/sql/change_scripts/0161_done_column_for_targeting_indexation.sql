alter table imported_program
	add column indexed int(1) unsigned not null default 0 after code,
	add index (indexed)
;

alter table imported_program
	modify column indexed int(1) unsigned not null
;

update imported_program left join imported_program_subject_area
	on imported_program.id = imported_program_subject_area.imported_program_id
	set imported_program.indexed = 1
where imported_program_subject_area.id is not null
;

alter table imported_program_subject_area
	drop column enabled
;

alter table imported_institution_subject_area
	drop column enabled
;
