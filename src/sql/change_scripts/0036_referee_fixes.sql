update ignore application_referee inner join user_role
	on application_referee.application_id = user_role.application_id
	and application_referee.user_id = user_role.user_id
inner join comment
	on application_referee.comment_id = comment.id
set user_role.role_id = "APPLICATION_VIEWER_REFEREE"
where user_role.role_id = "APPLICATION_REFEREE"
	and comment.id is not null
;

delete user_role.* 
from application_referee inner join user_role
	on application_referee.application_id = user_role.application_id
	and application_referee.user_id = user_role.user_id
inner join comment
	on application_referee.comment_id = comment.id
where user_role.role_id = "APPLICATION_REFEREE"
	and comment.id is not null
;
