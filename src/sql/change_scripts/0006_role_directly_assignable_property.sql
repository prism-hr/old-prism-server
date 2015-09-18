alter table role
	add column directly_assignable int(1) unsigned not null default 1 after role_category
;

alter table role
	modify column directly_assignable int(1) unsigned not null
;
