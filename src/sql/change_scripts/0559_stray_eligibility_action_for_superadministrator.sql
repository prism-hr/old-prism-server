DELETE APPLICATION_FORM_ACTION_REQUIRED.* 
FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
	ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
INNER JOIN (
	SELECT APPLICATION_FORM_USER_ROLE.application_form_id AS application_form_id
	FROM APPLICATION_FORM_USER_ROLE LEFT JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	WHERE APPLICATION_FORM_USER_ROLE.application_role_id = "ADMITTER"
	GROUP BY APPLICATION_FORM_USER_ROLE.application_form_id
	HAVING COUNT(APPLICATION_FORM_ACTION_REQUIRED.id) = 0) AS ELIGIBILITY_CONFIRMED
	ON APPLICATION_FORM_USER_ROLE.application_form_id = ELIGIBILITY_CONFIRMED.application_form_id
WHERE APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
	AND APPLICATION_FORM_ACTION_REQUIRED.action_id = "CONFIRM_ELIGIBILITY"
;

UPDATE APPLICATION_FORM_USER_ROLE LEFT JOIN APPLICATION_FORM_ACTION_REQUIRED
	ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 0
WHERE APPLICATION_FORM_ACTION_REQUIRED.id IS NULL
	AND APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 1
;
