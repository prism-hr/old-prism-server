alter table user_account
	add column activity_cached_increment int(10) unsigned after activity_cached_timestamp
;

update user_account
set activity_cached_increment = 1
where activity_cached_timestamp is not null
;

update state_group
set ordinal = ordinal + 20
;

set session foreign_key_checks = 0
;

update application
set state_id = 'APPLICATION_ACCEPTED'
where state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update application
set previous_state_id = 'APPLICATION_ACCEPTED'
where previous_state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update comment
set state_id = 'APPLICATION_ACCEPTED'
where state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update comment
set transition_state_id = 'APPLICATION_ACCEPTED'
where transition_state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update comment_state
set state_id = 'APPLICATION_ACCEPTED'
where state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update comment_transition_state
set state_id = 'APPLICATION_ACCEPTED'
where state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update department
set state_id = 'APPLICATION_ACCEPTED'
where state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update department
set previous_state_id = 'APPLICATION_ACCEPTED'
where previous_state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update institution
set previous_state_id = 'APPLICATION_ACCEPTED'
where previous_state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update institution
set state_id = 'APPLICATION_ACCEPTED'
where state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update program
set state_id = 'APPLICATION_ACCEPTED'
where state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update program
set previous_state_id = 'APPLICATION_ACCEPTED'
where previous_state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update project
set state_id = 'APPLICATION_ACCEPTED'
where state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update project
set previous_state_id = 'APPLICATION_ACCEPTED'
where previous_state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update resource_previous_state
set state_id = 'APPLICATION_ACCEPTED'
where state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update resource_state
set state_id = 'APPLICATION_ACCEPTED'
where state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update state_action
set state_id = 'APPLICATION_ACCEPTED'
where state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update state_action_pending
set transition_state_id = 'APPLICATION_ACCEPTED'
where transition_state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update state_transition
set transition_state_id = 'APPLICATION_ACCEPTED'
where transition_state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update system
set state_id = 'APPLICATION_ACCEPTED'
where state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update system
set previous_state_id = 'APPLICATION_ACCEPTED'
where previous_state_id = 'APPLICATION_APPROVED_COMPLETED'
;

update state
set id = "APPLICATION_ACCEPTED"
where id = "APPLICATION_APPROVED_COMPLETED"
;

set session foreign_key_checks = 1
;
