ALTER TABLE USER_NOTIFICATION
	DROP INDEX user_role_id,
	DROP FOREIGN KEY user_notification_ibfk_1,
	DROP COLUMN user_role_id,
	ADD COLUMN user_id INT(10) UNSIGNED NOT NULL AFTER application_id,
	ADD INDEX (user_id),
	ADD FOREIGN KEY (user_id) REFERENCES USER (id)
	CHANGE COLUMN created_timestamp created_date DATE NOT NULL
;
