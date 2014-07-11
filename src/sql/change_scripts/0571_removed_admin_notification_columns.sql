ALTER TABLE INTERVIEWER
	DROP COLUMN requires_admin_notification,
	DROP COLUMN admins_notified_on,
	DROP COLUMN first_admin_notification
;

ALTER TABLE REVIEWER
	DROP COLUMN requires_admin_notification,
	DROP COLUMN admins_notified_on
;
