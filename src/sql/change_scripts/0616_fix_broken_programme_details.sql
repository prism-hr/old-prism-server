UPDATE application_form INNER JOIN application_form_programme_detail
	ON application_form.programme_details_id = application_form_programme_detail.id
SET application_form_programme_detail.STUDY_CODE = "F+++++",
	application_form_programme_detail.study_option = "Full-time",
	application_form_programme_detail.sources_of_interest_id = 12
WHERE application_form.status IN ("VALIDATION", "REVIEW", "INTERVIEW", "APPROVAL")
	AND application_form_programme_detail.STUDY_CODE IS NULL
	AND application_form_programme_detail.sources_of_interest_id IS NULL
;
