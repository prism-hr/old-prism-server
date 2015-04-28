alter table user
	add column email_valid int(1) unsigned not null default 1 after email,
	add index (email_valid)
;

alter table user
	modify column email_valid int(1) unsigned not null
;
