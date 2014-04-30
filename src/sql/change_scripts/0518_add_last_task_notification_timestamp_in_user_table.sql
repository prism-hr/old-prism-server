ALTER TABLE REGISTERED_USER
DROP COLUMN digest_notification_type,
ADD COLUMN latest_task_notification_date DATETIME
;