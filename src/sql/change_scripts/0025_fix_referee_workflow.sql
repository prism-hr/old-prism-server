delete user_role.* 
from user_role inner join user_role as other_role
	on user_role.application_id = other_role.application_id
		and user_role.user_id = other_role.user_id
		and user_role.role_id = "APPLICATION_REFEREE"
		and other_role.role_id = "APPLICATION_VIEWER_REFEREE"
;

update user_role inner join application_referee
	on user_role.application_id = application_referee.application_id
		and user_role.user_id = application_referee.user_id
set user_role.role_id = "APPLICATION_VIEWER_REFEREE"
where user_role.role_id = "APPLICATION_REFEREE"
	and application_referee.comment_id is not null
;
