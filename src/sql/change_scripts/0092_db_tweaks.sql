insert ignore into user_connection(user_requested_id, user_connected_id, 
	connected, created_timestamp)
	select user_role.user_id, other_user_role.user_id,
		true, user_role.assigned_timestamp
	from user_role
	inner join user_role as other_user_role
		on user_role.application_id = other_user_role.application_id
		and other_user_role.role_id = "APPLICATION_SECONDARY_SUPERVISOR"
	where user_role.role_id = "APPLICATION_PRIMARY_SUPERVISOR"
;

insert ignore into user_connection (user_requested_id, user_connected_id, connected, created_timestamp)
	select comment_assigned_user.user_id, secondary.user_id, true, comment.created_timestamp 
from comment inner join comment_assigned_user
		on comment.id = comment_assigned_user.comment_id
		and comment_assigned_user.role_id = "APPLICATION_PRIMARY_SUPERVISOR"
		and comment_assigned_user.role_transition_type = "CREATE"
	inner join comment_assigned_user as secondary
		on comment.id = secondary.comment_id
		and secondary.role_id = "APPLICATION_SECONDARY_SUPERVISOR"
		and secondary.role_transition_type = "CREATE"
	where comment.state_id = "APPLICATION_APPROVED"
		and comment.transition_state_id like "APPLICATION_APPROVED_%"
;

alter table resource_condition
	add column partner_mode int(1) unsigned not null default 0 after action_condition,
	add index (system_id, action_condition, partner_mode),
	add index (institution_id, action_condition, partner_mode),
	add index (program_id, action_condition, partner_mode),
	add index (project_id, action_condition, partner_mode),
	add index (application_id, action_condition, partner_mode)
;

alter table resource_condition
	modify column partner_mode int(1) unsigned not null
;
