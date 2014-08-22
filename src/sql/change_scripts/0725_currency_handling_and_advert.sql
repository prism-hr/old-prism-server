ALTER TABLE INSTITUTION_DOMICILE
	ADD COLUMN currency VARCHAR(10) NOT NULL DEFAULT "TEMPORARY" AFTER name,
	ADD INDEX (currency)
;

ALTER TABLE INSTITUTION_DOMICILE
	MODIFY COLUMN currency VARCHAR(10) NOT NULL 
;

ALTER TABLE INSTITUTION
	ADD COLUMN currency VARCHAR(10) NOT NULL DEFAULT "GBP" AFTER title,
	ADD INDEX (currency)
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN currency VARCHAR(10) NOT NULL
;

ALTER TABLE ADVERT
	CHANGE COLUMN month_study_duration month_study_duration_minimum INT(10) UNSIGNED,
	ADD COLUMN month_study_duration_maximum INT(10) UNSIGNED AFTER month_study_duration_minimum,
	ADD INDEX (month_study_duration_minimum),
	ADD INDEX (month_study_duration_maximum),
	ADD COLUMN currency VARCHAR(10) AFTER month_study_duration_maximum,
	DROP COLUMN fee_interval,
	DROP COLUMN fee_value,
	DROP COLUMN fee_annualised,
	DROP COLUMN pay_value,
	DROP COLUMN pay_interval,
	DROP COLUMN pay_annualised
;

ALTER TABLE APPLICATION
	MODIFY COLUMN average_rating DECIMAL(3,2) UNSIGNED
;

ALTER TABLE PROJECT
	MODIFY COLUMN applicant_rating_percentile_05 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_20 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_35 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_50 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_65 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_80 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_95 DECIMAL(3,2) UNSIGNED
;

ALTER TABLE PROGRAM
	MODIFY COLUMN applicant_rating_percentile_05 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_20 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_35 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_50 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_65 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_80 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_95 DECIMAL(3,2) UNSIGNED
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN applicant_rating_percentile_05 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_20 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_35 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_50 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_65 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_80 DECIMAL(3,2) UNSIGNED,
	MODIFY COLUMN applicant_rating_percentile_95 DECIMAL(3,2) UNSIGNED
;

ALTER TABLE IMPORTED_LANGUAGE_QUALIFICATION_TYPE
	MODIFY COLUMN minimum_overall_score DECIMAL (5,2) UNSIGNED,
	MODIFY COLUMN maximum_overall_score DECIMAL (5,2) UNSIGNED,
	MODIFY COLUMN minimum_reading_score DECIMAL (5,2) UNSIGNED,
	MODIFY COLUMN maximum_reading_score DECIMAL (5,2) UNSIGNED,
	MODIFY COLUMN minimum_writing_score DECIMAL (5,2) UNSIGNED,
	MODIFY COLUMN maximum_writing_score DECIMAL (5,2) UNSIGNED,
	MODIFY COLUMN minimum_speaking_score DECIMAL (5,2) UNSIGNED,
	MODIFY COLUMN maximum_speaking_score DECIMAL (5,2) UNSIGNED,
	MODIFY COLUMN minimum_listening_score DECIMAL (5,2) UNSIGNED,
	MODIFY COLUMN maximum_listening_score DECIMAL (5,2) UNSIGNED
;

ALTER TABLE ADVERT
	ADD COLUMN fee_interval VARCHAR(10),
	ADD COLUMN month_fee_minimum_specified DECIMAL(10,2) UNSIGNED,
	ADD COLUMN month_fee_maximum_specified DECIMAL(10,2) UNSIGNED,
	ADD COLUMN year_fee_minimum_specified DECIMAL(10,2) UNSIGNED,
	ADD COLUMN year_fee_maximum_specified DECIMAL(10,2) UNSIGNED,
	ADD COLUMN month_fee_minimum_at_locale DECIMAL(10,2) UNSIGNED,
	ADD COLUMN month_fee_maximum_at_locale DECIMAL(10,2) UNSIGNED,
	ADD COLUMN year_fee_minimum_at_locale DECIMAL(10,2) UNSIGNED,
	ADD COLUMN year_fee_maximum_at_locale DECIMAL(10,2) UNSIGNED,
	ADD COLUMN pay_interval VARCHAR(10),
	ADD COLUMN month_pay_minimum_specified DECIMAL(10,2) UNSIGNED,
	ADD COLUMN month_pay_maximum_specified DECIMAL(10,2) UNSIGNED,
	ADD COLUMN year_pay_minimum_specified DECIMAL(10,2) UNSIGNED,
	ADD COLUMN year_pay_maximum_specified DECIMAL(10,2) UNSIGNED,
	ADD COLUMN month_pay_minimum_at_locale DECIMAL(10,2) UNSIGNED,
	ADD COLUMN month_pay_maximum_at_locale DECIMAL(10,2) UNSIGNED,
	ADD COLUMN year_pay_minimum_at_locale DECIMAL(10,2) UNSIGNED,
	ADD COLUMN year_pay_maximum_at_locale DECIMAL(10,2) UNSIGNED,
	ADD INDEX (month_fee_minimum_specified),
	ADD INDEX (month_fee_maximum_specified),
	ADD INDEX (year_fee_minimum_specified),
	ADD INDEX (year_fee_maximum_specified),
	ADD INDEX (month_fee_minimum_at_locale),
	ADD INDEX (month_fee_maximum_at_locale),
	ADD INDEX (year_fee_minimum_at_locale),
	ADD INDEX (year_fee_maximum_at_locale),
	ADD INDEX (month_pay_minimum_specified),
	ADD INDEX (month_pay_maximum_specified),
	ADD INDEX (year_pay_minimum_specified),
	ADD INDEX (year_pay_maximum_specified),
	ADD INDEX (month_pay_minimum_at_locale),
	ADD INDEX (month_pay_maximum_at_locale),
	ADD INDEX (year_pay_minimum_at_locale),
	ADD INDEX (year_pay_maximum_at_locale)
;

ALTER TABLE USER_ROLE
	ADD COLUMN notification_template_id VARCHAR(50) AFTER role_id,
	ADD COLUMN notification_last_sent_date DATE AFTER notification_template_id,
	ADD INDEX (notification_template_id),
	ADD INDEX (notification_last_sent_date),
	ADD FOREIGN KEY (notification_template_id) REFERENCES NOTIFICATION_TEMPLATE (id)
;

TRUNCATE TABLE USER_NOTIFICATION
;

ALTER TABLE USER_NOTIFICATION
	DROP FOREIGN KEY user_notification_ibfk_1,
	DROP INDEX user_role_id,
	CHANGE COLUMN user_role_id user_id INT(10) UNSIGNED NOT NULL,
	ADD UNIQUE INDEX (user_id, notification_template_id),
	ADD FOREIGN KEY (user_id) REFERENCES USER (id)
;

ALTER TABLE USER_NOTIFICATION
	CHANGE COLUMN created_timestamp notification_last_sent_date DATE NOT NULL
;

ALTER TABLE PROGRAM
	DROP COLUMN start_date,
	DROP COLUMN end_date
;

ALTER TABLE ADVERT
	MODIFY advert_closing_date_id INT(10) UNSIGNED AFTER year_pay_maximum_at_locale,
	ADD COLUMN publish_date DATE AFTER apply_link,
	ADD INDEX (publish_date)
;

ALTER TABLE COMMENT
	DROP COLUMN user_specified_due_date
;

ALTER TABLE APPLICATION
	ADD COLUMN previous_closing_date DATE AFTER closing_date,
	ADD INDEX (previous_closing_date)
;

