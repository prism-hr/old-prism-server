ALTER TABLE ADVERT
	MODIFY COLUMN fee_interval VARCHAR(10) AFTER month_study_duration_maximum,
	CHANGE COLUMN currency fee_currency VARCHAR(10),
	CHANGE COLUMN currency_at_locale fee_currency_at_locale VARCHAR(10),
	DROP INDEX currency,
	DROP INDEX currency_at_locale,
	ADD INDEX (fee_currency, sequence_identifier),
	ADD INDEX (fee_currency_at_locale, sequence_identifier), 
	ADD COLUMN pay_currency VARCHAR(10) AFTER pay_interval,
	ADD COLUMN pay_currency_at_locale VARCHAR(10) AFTER pay_currency,
	ADD INDEX (pay_currency, sequence_identifier),
	ADD INDEX (pay_currency_at_locale, sequence_identifier)
;
