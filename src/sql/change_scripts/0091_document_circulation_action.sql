alter table action
	add column document_circulation_action int(1) unsigned not null default 0 after visible_action
;

alter table action
	modify column document_circulation_action int(1) unsigned not null
;
