update state_transition
set state_transition_evaluation_id = null
where state_transition_evaluation_id = "PROGRAM_ESCALATED_OUTCOME"
;

delete
from state_transition_evaluation
where id = "PROGRAM_ESCALATED_OUTCOME"
;
