update ignore user_role inner join application_referee
	on user_role.application_id = application_referee.application_id
	and user_role.user_id = application_referee.user_id
	and user_role.role_id = "APPLICATION_REFEREE"
set user_role.role_id = "APPLICATION_VIEWER_REFEREE"
where application_referee.comment_id is not null
;
