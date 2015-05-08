alter table state_transition
	drop column exclude_selection,
	drop index state_transition_evaluation_id_2
;
