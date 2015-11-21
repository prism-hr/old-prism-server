delete 
from state_duration_configuration
where state_duration_definition_id in ("APPLICATION_PURGE_DURATION",
 	"APPLICATION_RESERVE_DURATION")
;

update state
set state_duration_definition_id = null
where state_duration_definition_id in ("APPLICATION_PURGE_DURATION",
 	"APPLICATION_RESERVE_DURATION")
;

delete
from state_duration_definition
where id in ("APPLICATION_PURGE_DURATION",
 	"APPLICATION_RESERVE_DURATION")
;
