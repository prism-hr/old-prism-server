update application
set application_year = concat(month(created_timestamp)),
	application_month_sequence = application_month
where institution_id = ${institutionId};
