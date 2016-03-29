alter table state_action_recipient
	add column id int(10) unsigned not null auto_increment first,
	add primary key (id),
	add column external_mode int(1) unsigned not null,
	add index (external_mode)
;
