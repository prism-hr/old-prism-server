alter table state_transition
	add column exclude_selection int(1) unsigned not null default 0,
	add index (state_transition_evaluation_id, exclude_selection)
;

alter table state_transition
	modify column exclude_selection int(1) unsigned not null
;
