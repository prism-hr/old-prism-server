ALTER TABLE USER_ROLE
	DROP FOREIGN KEY user_role_ibfk_8,
	DROP COLUMN notification_template_id
;

ALTER TABLE STATE_ACTION_NOTIFICATION
	DROP COLUMN notify_invoker
;
