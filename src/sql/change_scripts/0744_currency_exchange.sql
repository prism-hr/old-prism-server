ALTER TABLE ADVERT
	ADD COLUMN last_currency_conversion_date DATE AFTER advert_closing_date_id,
	ADD INDEX (last_currency_conversion_date),
	CHANGE COLUMN fee_currency fee_currency_specified VARCHAR(10),
	CHANGE COLUMN pay_currency pay_currency_specified VARCHAR(10)
;
