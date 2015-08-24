alter table imported_institution
	drop index imported_domicile_id,
	change column imported_domicile_id domicile_id int(10) unsigned not null,
	add index (domicile_id),
	add column ucas_id varchar(10),
	add column facebook_id varchar(20),
	add unique index (ucas_id),
	add unique index (facebook_id),
	drop column custom
;
