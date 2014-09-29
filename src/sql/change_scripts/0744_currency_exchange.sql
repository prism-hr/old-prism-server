ALTER TABLE ADVERT
	ADD COLUMN last_currency_conversion_date DATE AFTER advert_closing_date_id,
	ADD INDEX (last_currency_conversion_date),
	CHANGE COLUMN fee_currency fee_currency_specified VARCHAR(10),
	CHANGE COLUMN pay_currency pay_currency_specified VARCHAR(10),
	ADD COLUMN fee_converted INT(1) UNSIGNED AFTER year_fee_maximum_at_locale,
	ADD COLUMN pay_converted INT(1) UNSIGNED AFTER year_pay_maximum_at_locale
;

ALTER TABLE SYSTEM
	CHANGE COLUMN last_data_imported_date last_data_import_date DATE
;
