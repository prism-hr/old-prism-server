alter table application
	modify column application_month_sequence int(2) unsigned,
	modify column application_week_sequence int(2) unsigned
;

update application
set application_month_sequence = null,
	application_week_sequence = null
;

alter table application
	modify column application_month_sequence date,
	modify column application_week_sequence date
;

update application
set application_month_sequence = date(concat(year(created_timestamp), "-", month(created_timestamp), "-01"))
;

update application
set application_week_sequence = if(
	dayofweek(created_timestamp) = 1,
		date(created_timestamp - interval 6 day),
		date(created_timestamp - interval (dayofweek(created_timestamp) - 2) day))
;
