alter table action
	add column partnership_state varchar(50) after fallback_action_id,
	add column partnership_transition_state varchar(50) after partnership_state
;

alter table advert_target_advert
	drop index advert_id_3,
	drop column endorsed,
	add column partnership_state varchar(50)
;
