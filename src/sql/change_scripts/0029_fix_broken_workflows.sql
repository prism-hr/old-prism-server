update application
set state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK",
	previous_state_id = "APPLICATION_REVIEW"
where id in (16883, 16626, 15977, 16616, 16233, 16749, 16528, 15486, 16718, 16773, 17081)
;

update resource_state
set state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
where application_id in (16883, 16626, 15977, 16616, 16233, 16749, 16528, 15486, 16718, 16773, 17081)
	and primary_state is true
;

update resource_state
set state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
where application_id in (16883, 16626, 15977, 16616, 16233, 16749, 16528, 15486, 16718, 16773, 17081)
	and primary_state is true
;

update comment
set state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK",
	transition_state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
where transition_state_id like "APPLICATION_REFERENCE%"
	and application_id in (16883, 16626, 15977, 16616, 16233, 16749, 16528, 15486, 16718, 16773, 17081, 15574)
;

update comment inner join comment_state
	on comment.id = comment_state.comment_id
set comment_state.state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
where comment.application_id in (16833, 16626, 15977, 16616, 16233, 16749, 16528, 15486, 16718, 16773, 17081, 15574)
	and comment_state.primary_state is true
	and comment.state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
;

update comment inner join comment_transition_state
	on comment.id = comment_transition_state.comment_id
set comment_transition_state.state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
where comment.application_id in (16833, 16626, 15977, 16616, 16233, 16749, 16528, 15486, 16718, 16773, 17081, 15574)
	and comment_transition_state.primary_state is true
	and comment.transition_state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
;

update application
set state_id = "APPLICATION_REVIEW_PENDING_COMPLETION",
	previous_state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
where id in (15749)
;

update resource_state
set state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
where application_id in (15749)
	and primary_state is true
;

update resource_previous_state
set state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
where application_id in (15749)
	and primary_state is true
;

update comment
set transition_state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
where transition_state_id = "APPLICATION_REFERENCE"
	and application_id in (15749)
;

update comment inner join comment_state
	on comment.id = comment_state.comment_id
set comment_state.state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
where comment.application_id in (15749)
	and comment_state.primary_state is true
	and comment.state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
;

update comment inner join comment_transition_state
	on comment.id = comment_transition_state.comment_id
set comment_transition_state.state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
where comment.application_id in (15749)
	and comment_transition_state.primary_state is true
	and comment.transition_state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
;

alter table comment
	drop column role_id,
	drop column delegate_role_id
;
