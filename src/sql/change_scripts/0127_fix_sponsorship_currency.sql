alter table comment
	change column sponsorship_currency sponsorship_currency_specified varchar(10),
	add column sponsorship_currency_converted varchar(10) after sponsorship_currency_specified
;

update comment
set sponsorship_currency_converted = "GBP"
where sponsorship_currency_specified is not null
;
