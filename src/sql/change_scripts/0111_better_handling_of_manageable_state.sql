alter table state
	add column manageable int(1) unsigned after published,
	add index (manageable)
;
