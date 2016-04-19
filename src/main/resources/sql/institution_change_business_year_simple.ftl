update application
set application_year = concat(year(created_timestamp))
where institution_id = ${institutionId?c};
