update application
set previous_state_id = state_id,
	state_id = "APPLICATION_REJECTED_COMPLETED",
	due_date = date(updated_timestamp) + interval 168 day
where id in (6004, 6236, 6275, 15427, 15378, 15165, 13730, 11852, 3742, 1821)
;

update resource_state
set state_id = "APPLICATION_REJECTED_COMPLETED"
where application_id in (6004, 6236, 6275, 15427, 15378, 15165, 13730, 11852, 3742, 1821)
;

update resource_previous_state
set state_id = "APPLICATION_REJECTED_PENDING_EXPORT"
where application_id in (6004, 6236, 6275, 15427, 15378, 15165, 13730, 11852, 3742, 1821)
;
