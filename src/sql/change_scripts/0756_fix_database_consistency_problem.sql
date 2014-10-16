DELETE
FROM NOTIFICATION_TEMPLATE
WHERE (id LIKE "%_TASK_REQUEST"
	OR id LIKE "%_TASK_REQUEST_REMINDER"
	OR id LIKE "%_UPDATE_NOTIFICATION")
AND id NOT LIKE "SYSTEM_%"
	OR id = "SYSTEM_IMPORT_ERROR_NOTIFICATION"
;