update application_referee inner join user_role
	on application_referee.user_id = user_role.user_id
	and application_referee.application_id = user_role.application_id
	and user_role.role_id = "APPLICATION_REFEREE"
inner join comment
	on application_referee.comment_id = comment.id
set user_role.role_id = "APPLICATION_VIEWER_REFEREE"
where application_referee.comment_id is not null
	and application_referee.user_id = comment.delegate_user_id
;