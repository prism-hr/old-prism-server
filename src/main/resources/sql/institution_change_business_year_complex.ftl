update application
set application_year = 
	if(application_month > ${businessYearEndMonth},
		concat(year(created_timestamp), "/", (year(created_timestamp) + 1)),
		concat((year(created_timestamp) - 1), "/", year(created_timestamp))),  
	application_month_sequence = 
	if(application_month > ${businessYearEndMonth}, 
		(application_month - ${businessYearEndMonth}),
		(application_month + (12 - ${businessYearEndMonth}))) 
where institution_id = :id;
