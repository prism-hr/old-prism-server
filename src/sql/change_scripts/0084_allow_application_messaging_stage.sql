update state_group
set ordinal = ordinal + 20
;

delete
from state_duration_configuration
where state_duration_definition_id = "APPLICATION_CONFIRM_ELIGIBILITY_DURATION"
;

delete
from state_duration_definition
where id = "APPLICATION_CONFIRM_ELIGIBILITY_DURATION"
;
