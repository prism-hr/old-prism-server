ALTER TABLE APPLICATION_FORM_USER_ROLE
	ADD COLUMN update_timestamp DATETIME,
	ADD INDEX (update_timestamp),
	ADD COLUMN raises_update_flag INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (raises_update_flag)
;

UPDATE APPLICATION_FORM_USER_ROLE
INNER JOIN APPLICATION_ROLE
	ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
INNER JOIN (
	SELECT MAX(update_timestamp) AS update_timestamp,
		application_form_id AS application_form_id,
		update_visibility AS update_visibility
	FROM APPLICATION_FORM_UPDATE
	GROUP BY application_form_id,
		update_visibility) AS LATEST_UPDATE
	ON APPLICATION_FORM_USER_ROLE.application_form_id = LATEST_UPDATE.application_form_id
	AND APPLICATION_ROLE.update_visibility >= LATEST_UPDATE.update_visibility
LEFT JOIN APPLICATION_FORM_LAST_ACCESS
	ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_LAST_ACCESS.application_form_id
	AND APPLICATION_FORM_USER_ROLE.registered_user_id = APPLICATION_FORM_LAST_ACCESS.user_id
SET APPLICATION_FORM_USER_ROLE.raises_update_flag = 1,
	APPLICATION_FORM_USER_ROLE.update_timestamp = LATEST_UPDATE.update_timestamp
WHERE LATEST_UPDATE.update_timestamp > APPLICATION_FORM_LAST_ACCESS.last_access_timestamp
	OR APPLICATION_FORM_LAST_ACCESS.last_access_timestamp IS NULL
;

ALTER TABLE APPLICATION_FORM_ACTION_REQUIRED
	ADD COLUMN raises_urgent_flag INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (raises_urgent_flag)
;

UPDATE APPLICATION_FORM_ACTION_REQUIRED
SET raises_urgent_flag = 1
WHERE deadline_timestamp < CURRENT_DATE()
;

ALTER TABLE APPLICATION_ROLE
	ADD COLUMN do_send_update_notification INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (do_send_update_notification)
;

UPDATE APPLICATION_ROLE
SET do_send_update_notification = 1
WHERE id = "APPLICANT";