update ignore user_role
set role_id = "APPLICATION_VIEWER_REFEREE"
where application_id in (
	select application.id
	from application inner join resource_state
		on application.id = resource_state.application_id
	where application.state_id like "%_COMPLETED"
		and resource_state.state_id like "%_REFERENCE")
and role_id = "APPLICATION_REFEREE"
;

delete
from user_role
where application_id in (
	select application.id
	from application inner join resource_state
		on application.id = resource_state.application_id
	where application.state_id like "%_COMPLETED"
		and resource_state.state_id like "%_REFERENCE")
and role_id = "APPLICATION_REFEREE"
;

delete resource_state.*
from application inner join resource_state
	on application.id = resource_state.application_id
where application.state_id like "%_COMPLETED"
	and resource_state.state_id like "%_REFERENCE"
;
