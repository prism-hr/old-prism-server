SELECT APPLICATION_FORM_USER_ROLE.registered_user_id AS registered_user_id,
	REGISTERED_USER.firstName AS registered_user_first_name,
	REGISTERED_USER.lastName AS registered_user_last_name,
	REGISTERED_USER.email AS registered_user_email,
	MAX(APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag) AS has_urgent_flag,
	MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag) AS has_update_flag,
	MAX(APPLICATION_FORM_USER_ROLE.update_timestamp) AS updated_on_timestamp
FROM APPLICATION_FORM_USER_ROLE INNER JOIN REGISTERED_USER
	ON APPLICATION_FORM_USER_ROLE.registered_user_id = REGISTERED_USER.id
INNER JOIN APPLICATION_ROLE
	ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
LEFT JOIN APPLICATION_FORM_ACTION_REQUIRED
	ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
WHERE APPLICATION_ROLE.do_send_update_notification = 1
   AND DATE(APPLICATION_FORM_USER_ROLE.update_timestamp) = CURRENT_DATE()
   AND (APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag IS NULL
  OR APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 0)
GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id
  HAVING MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag) > 0;
