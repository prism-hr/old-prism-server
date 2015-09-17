ALTER TABLE user_account
	ADD COLUMN linkedin_id VARCHAR(50) AFTER temporary_password_expiry_timestamp,
	ADD COLUMN linkedin_profile_id MEDIUMTEXT AFTER linkedin_id,
	ADD COLUMN linkedin_image_id MEDIUMTEXT AFTER linkedin_profile_id,
	DROP FOREIGN KEY user_account_ibfk_1,
	DROP COLUMN user_account_external_id,
	ADD UNIQUE INDEX (linkedin_id)
;

DROP TABLE user_account_external
;
