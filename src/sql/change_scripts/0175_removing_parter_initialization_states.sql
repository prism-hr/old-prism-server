delete
from role_transition
where state_transition_id in (
	select id
	from state_transition
	where state_action_id in (
		select id 
		from state_action
		where state_id in (
			select id
			from state
			where id like "%_PARENT"))
		or transition_state_id in (
			select id
			from state
			where id like "%_PARENT"))
;

delete
from state_transition 
where state_action_id in (
	select id 
	from state_action
	where state_id in (
		select id
		from state
		where id like "%_PARENT"))
	or transition_state_id in (
		select id
		from state
		where id like "%_PARENT")
;

delete
from state_action_notification
where state_action_id in (
	select id 
	from state_action
	where state_id in (
		select id
		from state
		where id like "%_PARENT"))
;

delete
from state_action_assignment
where state_action_id in (
	select id 
	from state_action
	where state_id in (
		select id
		from state
		where id like "%_PARENT"))
;

delete
from state_action
where state_id in (
	select id
	from state
	where id like "%_PARENT")
;

delete
from state
where id like "%_PARENT"
;

delete
from state_group
where id like "%_PARENT"
;

delete
from state_transition_propagation
where propagated_action_id in (
	select id
	from action
	where id like "%_STARTUP%"
		and id not like "%_SYSTEM%")
;

delete
from role_transition
where state_transition_id in (
	select id
	from state_transition
	where state_action_id in (
		select id
		from state_action
		where action_id in (
		select id
		from action
		where id like "%_STARTUP%"
			and id not like "%_SYSTEM%")))
;	

delete
from state_transition
where state_action_id in (
	select id
	from state_action
	where action_id in (
	select id
	from action
	where id like "%_STARTUP%"
		and id not like "%_SYSTEM%"))
;

delete
from state_action
where action_id in (
	select id
	from action
	where id like "%_STARTUP%"
		and id not like "%_SYSTEM%")
;

delete
from comment_state
where comment_id in (
	select id
	from comment
	where action_id in (
		select id
		from action
		where id like "%_STARTUP%"
			and id not like "%_SYSTEM%"))
;

delete
from comment_transition_state
where comment_id in (
	select id
	from comment
	where action_id in (
		select id
		from action
		where id like "%_STARTUP%"
			and id not like "%_SYSTEM%"))
;

delete
from comment_assigned_user
where comment_id in (
	select id
	from comment
	where action_id in (
		select id
		from action
		where id like "%_STARTUP%"
			and id not like "%_SYSTEM%"))
;

delete
from comment
where action_id in (
	select id
	from action
	where id like "%_STARTUP%"
		and id not like "%_SYSTEM%")
;

delete
from action
where id like "%_STARTUP%"
	and id not like "%_SYSTEM%"
;

delete
from state_transition_evaluation
where id like "%_STARTED_OUTCOME"
;
