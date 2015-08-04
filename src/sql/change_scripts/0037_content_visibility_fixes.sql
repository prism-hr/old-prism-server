alter table role
	add column override_redaction int(1) unsigned not null default 0
;

alter table role
	modify column override_redaction int(1) unsigned not null
;
