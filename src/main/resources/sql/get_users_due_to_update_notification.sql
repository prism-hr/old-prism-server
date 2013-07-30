SELECT registered_user.id
FROM application_form INNER JOIN registered_user
ON application_form.applicant_id = registered_user.id
INNER JOIN application_form_update
ON application_form.id = application_form_update.application_form_id
INNER JOIN application_form_last_access
ON application_form.id = application_form_last_access.application_form_id
WHERE application_form.status IN ("VALIDATION", "REVIEW", "INTERVIEW", "APPROVAL")
AND DATE(application_form_update.update_timestamp) = CURRENT_DATE() - INTERVAL :interval ${TIME_UNIT}
AND application_form_update.update_timestamp > application_form_last_access.last_access_timestamp
AND application_form_update.update_visibility = 1
AND registered_user.id = application_form_last_access.user_id
AND registered_user.accountNonExpired = 1
AND registered_user.accountNonLocked = 1
GROUP BY registered_user.id;
