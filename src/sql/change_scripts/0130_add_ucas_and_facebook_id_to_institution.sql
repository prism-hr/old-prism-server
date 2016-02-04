alter table imported_institution
	add column facebook_id varchar(20),
	add column ucas_id varchar(6),
	add unique index (facebook_id),
	add unique index (ucas_id)
;
