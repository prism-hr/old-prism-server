alter table state
	add column ordinal int(1) unsigned after id,
	add index (ordinal)
;

alter table state_group
	add index (ordinal)
;

update state
set ordinal = 0
;

alter table state
	modify column ordinal int(2) unsigned not null
;
