alter table application
	modify column institution_id int(10) unsigned
;

alter table application
	modify column retain int(1) unsigned
;

update application
set retain = null
where retain = 0
;

alter table application
	modify column opportunity_category varchar(50) not null
;
