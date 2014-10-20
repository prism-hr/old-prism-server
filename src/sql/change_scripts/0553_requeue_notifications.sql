UPDATE REGISTERED_USER
SET latest_task_notification_date = NULL
WHERE DATE(latest_task_notification_date) = "2013-12-18"
;

UPDATE REGISTERED_USER
SET latest_update_notification_date = NULL
WHERE DATE(latest_update_notification_date) = "2013-12-18"
;