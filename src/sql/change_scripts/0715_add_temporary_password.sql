ALTER TABLE user_account
	ADD COLUMN temporary_password varchar(32) AFTER password,
	ADD COLUMN temporary_password_expiry_datetime datetime AFTER temporary_password,
	ADD INDEX (temporary_password),
	ADD INDEX (temporary_password_expiry_datetime datetime)
;
