update ignore application inner join resource_state
	on application.id = resource_state.application_id
set resource_state.state_id = "APPLICATION_APPROVED_COMPLETED"
where application.updated_timestamp < current_timestamp() - interval 3 month
	and application.state_id = "APPLICATION_APPROVED"
	and application.created_timestamp < current_timestamp() - interval 1 year
;

update ignore application inner join resource_previous_state
	on application.id = resource_previous_state.application_id
set resource_previous_state.state_id = "APPLICATION_APPROVED"
where application.updated_timestamp < current_timestamp() - interval 3 month
	and application.state_id = "APPLICATION_APPROVED"
	and application.created_timestamp < current_timestamp() - interval 1 year
;

update application set state_id = "APPLICATION_APPROVED_COMPLETED",
	previous_state_id = "APPLICATION_APPROVED",
	due_date = current_date() + interval 168 day
where updated_timestamp < current_timestamp() - interval 3 month
	and state_id = "APPLICATION_APPROVED"
	and created_timestamp < current_timestamp() - interval 1 year
;

update ignore application inner join resource_state
	on application.id = resource_state.application_id
set resource_state.state_id = "APPLICATION_REJECTED_COMPLETED"
where application.updated_timestamp < current_timestamp() - interval 3 month
	and application.state_id = "APPLICATION_REJECTED"
	and application.created_timestamp < current_timestamp() - interval 1 year
;

update ignore application inner join resource_previous_state
	on application.id = resource_previous_state.application_id
set resource_previous_state.state_id = "APPLICATION_REJECTED"
where application.updated_timestamp < current_timestamp() - interval 3 month
	and application.state_id = "APPLICATION_REJECTED"
	and application.created_timestamp < current_timestamp() - interval 1 year
;

update application set state_id = "APPLICATION_REJECTED_COMPLETED",
	previous_state_id = "APPLICATION_REJECTED",
	due_date = current_date() + interval 168 day
where updated_timestamp < current_timestamp() - interval 3 month
	and state_id = "APPLICATION_REJECTED"
	and created_timestamp < current_timestamp() - interval 1 year
;
