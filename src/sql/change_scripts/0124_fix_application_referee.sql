delete user_role.*
from user_role left join resource_state
	on user_role.application_id = resource_state.application_id
	and resource_state.state_id like "APPLICATION_REFERENCE%"
where resource_state.id is null
	and user_role.role_id = "APPLICATION_REFEREE"
;
