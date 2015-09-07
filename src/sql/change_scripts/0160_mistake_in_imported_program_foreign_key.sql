alter table imported_program
	drop index imported_institution_id,
	add unique index (imported_institution_id, qualification, name)
;
