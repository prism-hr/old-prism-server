update application
set application_year = concat(year(created_timestamp)),
    business_year_start_month = ${businessYearStartMonth?c}
where institution_id = ${institutionId?c};
