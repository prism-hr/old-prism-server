SET FOREIGN_KEY_CHECKS = 0
;

UPDATE NOTIFICATION_CONFIGURATION
SET notification_template_id = "SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION"
WHERE notification_template_id = "SYSTEM_RECOMMENDATION_NOTIFICATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET id = "SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION"
WHERE id = "SYSTEM_RECOMMENDATION_NOTIFICATION"
;

SET FOREIGN_KEY_CHECKS = 1
;

ALTER TABLE USER_ACCOUNT
	CHANGE COLUMN send_recommendation_notification send_application_recommendation_notification INT(1) UNSIGNED NOT NULL,
	CHANGE COLUMN last_notified_date_recommendation last_notified_date_application_recommendation DATE
;
