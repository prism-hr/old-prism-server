delete from comment_state
where comment_id in (
	select id
	from comment
	where action_id = "APPLICATION_TERMINATE")
;

delete from comment_transition_state
where comment_id in (
	select id
	from comment
	where action_id = "APPLICATION_TERMINATE")
;

delete from comment_assigned_user
where comment_id in (
	select id
	from comment
	where action_id = "APPLICATION_TERMINATE")
;

delete
from comment
where action_id = "APPLICATION_TERMINATE"
;

delete 
from role_transition
where state_transition_id in (
	select id
	from state_transition
	where state_action_id in (
		select id
		from state_action
		where action_id = "APPLICATION_TERMINATE")
			or transition_action_id = "APPLICATION_TERMINATE")
;

delete
from state_transition
where state_action_id in (
	select id
	from state_action
	where action_id = "APPLICATION_TERMINATE")
		or transition_action_id = "APPLICATION_TERMINATE"
;

delete
from state_action
where action_id = "APPLICATION_TERMINATE"
;

delete 
from state_transition_propagation
where propagated_action_id = "APPLICATION_TERMINATE"
;

delete
from action
where id = "APPLICATION_TERMINATE"
;
