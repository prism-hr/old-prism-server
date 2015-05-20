alter table state_transition_pending
	drop column state_transition_id
;

alter table role
	modify column scope_creator int(1) unsigned
;
