alter table comment_assigned_user
	drop index comment_id,
	add unique index (comment_id, user_id, role_id, role_transition_type)
;
