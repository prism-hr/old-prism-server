ALTER TABLE REGISTERED_USER
	ADD COLUMN latest_update_notification_date DATETIME AFTER latest_task_notification_date,
	ADD INDEX (latest_update_notification_date)
;
