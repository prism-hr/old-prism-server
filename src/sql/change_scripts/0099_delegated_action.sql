alter table action
	add column delegated_action_id varchar(100) after fallback_action_id,
	add index (delegated_action_id),
	add foreign key (delegated_action_id) references action (id)
;
