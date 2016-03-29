alter table state_action_recipient
	drop index state_action_assignment_id,
	add unique index (state_action_assignment_id, role_id, external_mode)
;
