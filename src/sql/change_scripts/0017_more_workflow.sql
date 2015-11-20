delete
from role_transition
where role_id = "APPLICATION_ADMINISTRATOR"
;

delete
from state_action_assignment
where role_id = "APPLICATION_ADMINISTRATOR"
;

delete
from state_action_notification
where role_id = "APPLICATION_ADMINISTRATOR"
;

delete
from role
where id = "APPLICATION_ADMINISTRATOR"
;

delete
from role_transition
where state_transition_id in (
	select id
	from state_transition
	where state_transition_evaluation_id in (
		"APPLICATION_ASSIGNED_REVIEWER_OUTCOME", 
		"APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME"))
;

delete
from state_transition
where state_transition_evaluation_id in (
	"APPLICATION_ASSIGNED_REVIEWER_OUTCOME", 
	"APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME")
;

delete
from state_transition_evaluation
where id in ("APPLICATION_ASSIGNED_REVIEWER_OUTCOME", 
	"APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME")
;
