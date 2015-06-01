update application_personal_detail inner join application
	on application_personal_detail.id = application.application_personal_detail_id
inner join imported_age_range as right_age_range
	on application.institution_id = right_age_range.institution_id
	and right_age_range.code = "FORTY_FORTYNINE"
left join imported_age_range as wrong_age_range
	on application_personal_detail.age_range_id = wrong_age_range.id
	and wrong_age_range.code = "FOURTY_FOURTYNINE"
set application_personal_detail.age_range_id = right_age_range.id,
	right_age_range.enabled = 1
;

delete from imported_age_range
where code = "FOURTY_FOURTYNINE"
;

update application_personal_detail inner join application
	on application_personal_detail.id = application.application_personal_detail_id
inner join imported_age_range
	on application.institution_id = imported_age_range.institution_id
	and (((year(application.created_timestamp) - year(application_personal_detail.date_of_birth)) 
		>= imported_age_range.lower_bound
		and (year(application.created_timestamp) - year(application_personal_detail.date_of_birth)
		<= imported_age_range.upper_bound)
		or ((year(application.created_timestamp) - year(application_personal_detail.date_of_birth)
			>= imported_age_range.lower_bound
			and imported_age_range.upper_bound is null))))
set application_personal_detail.age_range_id = imported_age_range.id
;
