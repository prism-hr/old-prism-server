alter table role_transition
	add column partner_mode int(1) unsigned not null default 0 after role_transition_type,
	drop index state_transition_id,
	add unique index (state_transition_id, role_id, role_transition_type, partner_mode)
;

alter table role_transition
	modify column partner_mode int(1) unsigned not null 
;
