delete user_role.*
from user_role left join application_referee
	on user_role.user_id = application_referee.user_id
	and user_role.application_id = application_referee.application_id
where application_referee.id is null
	and user_role.role_id = "APPLICATION_REFEREE"
;
