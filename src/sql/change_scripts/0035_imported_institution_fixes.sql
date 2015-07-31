alter table imported_institution
	modify column code varchar(50)
;

alter table imported_institution
	add column custom int(1) unsigned not null default 0
;

alter table imported_institution
	modify column custom int(1) unsigned not null
;

update imported_institution
set custom = 1
where code like "CUSTOM%"
;
