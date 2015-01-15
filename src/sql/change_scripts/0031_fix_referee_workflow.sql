insert ignore into resource_state(application_id, state_id, primary_state)
	select application.id, "APPLICATION_REFERENCE", false
	from application inner join resource_previous_state
		on application.id = resource_previous_state.application_id
	inner join application_referee
		on application.id = application_referee.application_id
	inner join resource_state
		on application.id = resource_state.application_id
	where application.state_id like "APPLICATION_VERIFICATION%"
		and application_referee.comment_id is null
	group by application.id
;
