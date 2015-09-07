alter table imported_program_subject_area
	add column match_type varchar(50) not null after imported_subject_area_id,
	add index (imported_program_id, match_type)
;
