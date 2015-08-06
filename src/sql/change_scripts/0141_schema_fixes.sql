alter table imported_subject_area
	add column description text after name,
	change column parent_id parent_imported_subject_area_id int(10) unsigned
;
