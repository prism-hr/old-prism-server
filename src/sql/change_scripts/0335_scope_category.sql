alter table scope
	add column scope_category varchar(50) not null default "APPLICATION" after id,
	add index (scope_category)
;

alter table scope
	modify column scope_category varchar(50) not null
;
