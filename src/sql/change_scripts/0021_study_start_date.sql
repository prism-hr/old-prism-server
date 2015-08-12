alter table application
	add column study_start_date DATE after study_application_id,
	add index (study_start_date, sequence_identifier)
;
