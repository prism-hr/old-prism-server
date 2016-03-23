delete
from state_transition
where state_action_id in (
	select id
	from state_action
	where (action_id like "INSTITUTION%"
		and action_id like "%ENDORSE%")
		or (action_id like "DEPARTMENT%"
			and action_id like "%ENDORSE%"))
		or (transition_action_id like "INSTITUTION%"
			and transition_action_id like "%ENDORSE%")
			or (transition_action_id like "DEPARTMENT%"
				and transition_action_id like "%ENDORSE%")
;


delete
from state_action_assignment
where state_action_id in (
	select id
	from state_action
	where (action_id like "INSTITUTION%"
		and action_id like "%ENDORSE%")
		or (action_id like "DEPARTMENT%"
			and action_id like "%ENDORSE%"))
;

delete
from state_action
where (action_id like "INSTITUTION%"
	and action_id like "%ENDORSE%")
	or (action_id like "DEPARTMENT%"
		and action_id like "%ENDORSE%")
;

delete 
from action
where (id like "INSTITUTION%"
	and id like "%ENDORSE%")
	or (id like "DEPARTMENT%"
		and id like "%ENDORSE%")
;
