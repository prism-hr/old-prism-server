ALTER TABLE REGISTERED_USER
	ADD COLUMN latest_update_notification_date DATETIME AFTER latest_task_notification_date,
	ADD COLUMN latest_digest_notification_type VARCHAR(50),
	ADD INDEX (latest_update_notification_date),
	ADD INDEX (latest_digest_notification_type)
;
