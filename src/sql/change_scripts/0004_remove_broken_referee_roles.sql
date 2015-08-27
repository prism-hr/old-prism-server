update ignore application_referee inner join comment
	on application_referee.application_id = comment.application_id
	and application_referee.user_id = comment.user_id
	and comment.action_id = "APPLICATION_PROVIDE_REFERENCE"
inner join user_role
	on comment.application_id = user_role.application_id
	and comment.user_id = user_role.user_id
	and user_role.role_id = "APPLICATION_REFEREE"
set user_role.role_id = "APPLICATION_VIEWER_REFEREE"
;

delete user_role.* 
from application_referee inner join comment
	on application_referee.application_id = comment.application_id
	and application_referee.user_id = comment.user_id
	and comment.action_id = "APPLICATION_PROVIDE_REFERENCE"
inner join user_role
	on comment.application_id = user_role.application_id
	and comment.user_id = user_role.user_id
	and user_role.role_id = "APPLICATION_REFEREE"
;

insert into resource_state(application_id, state_id, primary_state)
	select application.id, "APPLICATION_VERIFICATION", 0
	from application inner join comment
		on application.id = comment.application_id
		and comment.action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
		and comment.application_eligible = "UNSURE"
	left join comment as confirm_comment
		on application.id = confirm_comment.application_id
		and confirm_comment.action_id = "APPLICATION_CONFIRM_ELIGIBILITY"
	inner join state
		on application.state_id = state.id
	where confirm_comment.id is null
		and (state.state_group_id in ("APPLICATION_REVIEW", "APPLICATION_INTERVIEW", "APPLICATION_APPROVAL")
			or state.id in ("APPLICATION_APPROVED", "APPLICATION_REJECTED"))
	group by application.id
;
