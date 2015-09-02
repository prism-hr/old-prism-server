alter table imported_subject_area
	add column top_index_score decimal(20, 10) unsigned after parent_imported_subject_area_id
;

