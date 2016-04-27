update application
set application_year =
	if(application_month > ${businessYearEndMonth},
		concat(year(created_timestamp), "/", (year(created_timestamp) + 1)),
		concat((year(created_timestamp) - 1), "/", year(created_timestamp)))
where institution_id = ${institutionId?c};
