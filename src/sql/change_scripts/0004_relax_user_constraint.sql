alter table application_qualification
	modify user_id int(10) unsigned
;

alter table application_employment_position
	modify user_id int(10) unsigned
;

alter table user_qualification
	modify user_id int(10) unsigned
;

alter table user_employment_position
	modify user_id int(10) unsigned
;
