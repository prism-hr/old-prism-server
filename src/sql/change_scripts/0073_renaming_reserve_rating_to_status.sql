alter table application
	change column application_reserve_rating application_reserve_status varchar(50)
;

alter table comment
	change column application_reserve_rating application_reserve_status varchar(50)
;
