ALTER TABLE user_account
	ADD COLUMN temporary_password VARCHAR(32) AFTER password,
	ADD COLUMN temporary_password_expiry_datetime DATETIME AFTER temporary_password
;
