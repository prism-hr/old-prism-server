insert ignore into user_connection (user_requested_id, user_connected_id, connected, created_timestamp)
	select user_role.user_id, application.user_id, false, user_role.assigned_timestamp
	from application inner join user_role
		on application.id = user_role.application_id
	where application.state_id like "APPLICATION_APPROVED_%"
		and user_role.role_id in ("APPLICATION_PRIMARY_SUPERVISOR", "APPLICATION_SECONDARY_SUPERVISOR")
;

delete
from user_connection
where user_requested_id = user_connected_id
;
