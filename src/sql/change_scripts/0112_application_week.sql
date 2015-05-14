alter table application
	add column application_week int(2) unsigned after application_month_sequence,
	add column application_week_sequence int(2) unsigned after application_week,
	add index (institution_id, application_year, application_month_sequence, application_week_sequence),
	add index (program_id, application_year, application_month_sequence, application_week_sequence),
	add index (project_id, application_year, application_month_sequence, application_week_sequence)
;
