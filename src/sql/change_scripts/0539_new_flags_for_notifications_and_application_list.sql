ALTER TABLE APPLICATION_FORM_USER_ROLE
	ADD COLUMN update_timestamp DATETIME,
	ADD INDEX (update_timestamp),
	ADD COLUMN raises_update_flag INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (raises_update_flag)
;

ALTER TABLE APPLICATION_FORM_ACTION_REQUIRED
	ADD COLUMN raises_urgent_flag INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (raises_urgent_flag)
;

ALTER TABLE APPLICATION_ROLE
	ADD COLUMN do_send_update_notification INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (do_send_update_notification)
;

UPDATE APPLICATION_ROLE
SET do_send_update_notification = 1
WHERE id = "APPLICANT"
;