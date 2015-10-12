delete
from state_transition
where state_action_id in (
	select id
	from state_action
	where action_id in (
		select id
		from action
		where id like "%_ENDORSE"))
;

delete
from state_action_assignment
where state_action_id in (
	select id
	from state_action
	where action_id in (
		select id
		from action
		where id like "%_ENDORSE"))
;

delete
from state_action
where action_id in (
	select id
	from action
	where id like "%_ENDORSE")
;

delete
from action
where id like "%_ENDORSE"
;
