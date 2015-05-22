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

update user_role inner join application_referee
	on user_role.application_id = application_referee.application_id
	and user_role.user_id = application_referee.user_id
inner join application
	on application_referee.application_id = application.id
inner join resource_state
	on application.id = resource_state.application_id
set user_role.role_id = "APPLICATION_REFEREE"
where user_role.role_id = "APPLICATION_VIEWER_REFEREE"
	and application_referee.comment_id is null
	and resource_state.state_id like "APPLICATION_REFERENCE%"
;

drop table state_termination
;

create table state_termination (
	id int(10) unsigned not null auto_increment,
	state_transition_id int(10) unsigned not null,
	termination_state_id varchar(50) not null,
	state_termination_evaluation varchar(50),
	primary key (id),
	unique index (state_transition_id, termination_state_id),
	index (termination_state_id),
	foreign key (state_transition_id) references state_transition (id),
	foreign key (termination_state_id) references state (id))
;

update user_role inner join application_referee
	on user_role.application_id = application_referee.application_id
	and user_role.user_id = application_referee.user_id
	and user_role.role_id = "APPLICATION_REFEREE"
set user_role.last_notified_date = current_date()
where application_referee.comment_id is null
	and user_role.last_notified_date is null
;

update application
set state_id = "APPLICATION_REVIEW_PENDING_COMPLETION",
	previous_state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
where id in (15772, 15998, 16154)
;

update resource_state
set state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
where application_id in (15772, 15998, 16154)
	and primary_state is true
;

update resource_previous_state
set state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
where application_id in (15772, 15998, 16154)
	and primary_state is true
;

update comment
set state_id = "APPLICATION_REVIEW_PENDING_COMPLETION",
	transition_state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
where transition_state_id like "APPLICATION_REFERENCE%"
	and application_id in (15772, 15998, 16154)
;

update comment inner join comment_state
	on comment.id = comment_state.comment_id
set comment_state.state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
where comment.application_id in (15772, 15998, 16154)
	and comment_state.primary_state is true
	and comment.state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
;

update comment inner join comment_transition_state
	on comment.id = comment_transition_state.comment_id
set comment_transition_state.state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
where comment.application_id in (15772, 15998, 16154)
	and comment_transition_state.primary_state is true
	and comment.transition_state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
;

update application
set state_id = "APPLICATION_VERIFICATION",
	previous_state_id = "APPLICATION_VERIFICATION"
where id in (17027, 16930, 16486, 16363)
;

update resource_state
set state_id = "APPLICATION_VERIFICATION"
where application_id in (17027, 16930, 16486, 16363)
	and primary_state is true
;

update resource_previous_state
set state_id = "APPLICATION_VERIFICATION"
where application_id in (17027, 16930, 16486, 16363)
	and primary_state is true
;

update comment
set state_id = "APPLICATION_VERIFICATION",
	transition_state_id = "APPLICATION_VERIFICATION"
where transition_state_id like "APPLICATION_REFERENCE%"
	and application_id in (17027, 16930, 16486, 16363)
;

update comment inner join comment_state
	on comment.id = comment_state.comment_id
set comment_state.state_id = "APPLICATION_VERIFICATION"
where comment.application_id in (17027, 16930, 16486, 16363)
	and comment_state.primary_state is true
	and comment.state_id = "APPLICATION_VERIFICATION"
;

update comment inner join comment_transition_state
	on comment.id = comment_transition_state.comment_id
set comment_transition_state.state_id = "APPLICATION_VERIFICATION"
where comment.application_id in (17027, 16930, 16486, 16363)
	and comment_transition_state.primary_state is true
	and comment.transition_state_id = "APPLICATION_VERIFICATION"
;

update application
set state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION",
	previous_state_id = "APPLICATION_VERIFICATION"
where id in (16288, 16295)
;

update resource_state
set state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
where application_id in (16288, 16295)
	and primary_state is true
;

update resource_previous_state
set state_id = "APPLICATION_VERIFICATION"
where application_id in (16288, 16295)
	and primary_state is true
;

update comment
set state_id = "APPLICATION_VERIFICATION",
	transition_state_id = "APPLICATION_VERIFICATION"
where transition_state_id = "APPLICATION_REFERENCE"
	and application_id in (16288, 16295)
;

update comment
set state_id = "APPLICATION_VERIFICATION",
	transition_state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
where transition_state_id = "APPLICATION_REFERENCE_PENDING_COMPLETION"
	and application_id in (16288, 16295)
;

update comment inner join comment_state
	on comment.id = comment_state.comment_id
set comment_state.state_id = "APPLICATION_VERIFICATION"
where comment.application_id in (16288, 16295)
	and comment_state.primary_state is true
	and comment.state_id = "APPLICATION_VERIFICATION"
;

update comment inner join comment_state
	on comment.id = comment_state.comment_id
set comment_state.state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
where comment.application_id in (16288, 16295)
	and comment_state.primary_state is true
	and comment.state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
;

update comment inner join comment_transition_state
	on comment.id = comment_transition_state.comment_id
set comment_transition_state.state_id = "APPLICATION_VERIFICATION"
where comment.application_id in (16288, 16295)
	and comment_transition_state.primary_state is true
	and comment.transition_state_id = "APPLICATION_VERIFICATION"
;

update comment inner join comment_transition_state
	on comment.id = comment_transition_state.comment_id
set comment_transition_state.state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
where comment.application_id in (16288, 16295)
	and comment_transition_state.primary_state is true
	and comment.transition_state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
;

update application
set state_id = "APPLICATION_APPROVAL",
	previous_state_id = "APPLICATION_APPROVAL"
where id in (16705)
;

update resource_state
set state_id = "APPLICATION_APPROVAL"
where application_id in (16705)
	and primary_state is true
;

update resource_previous_state
set state_id = "APPLICATION_APPROVAL"
where application_id in (16705)
	and primary_state is true
;

update comment
set state_id = "APPLICATION_APPROVAL",
	transition_state_id = "APPLICATION_APPROVAL"
where transition_state_id like "APPLICATION_REFERENCE%"
	and application_id in (16705)
;

update comment inner join comment_state
	on comment.id = comment_state.comment_id
set comment_state.state_id = "APPLICATION_APPROVAL"
where comment.application_id in (16705)
	and comment_state.primary_state is true
	and comment.state_id = "APPLICATION_APPROVAL"
;

update comment inner join comment_transition_state
	on comment.id = comment_transition_state.comment_id
set comment_transition_state.state_id = "APPLICATION_APPROVAL"
where comment.application_id in (16705)
	and comment_transition_state.primary_state is true
	and comment.transition_state_id = "APPLICATION_APPROVAL"
;

update application
set state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION",
	previous_state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
where id in (16908, 16374, 16900)
;

update resource_state
set state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
where application_id in (16908, 16374, 16900)
	and primary_state is true
;

update resource_previous_state
set state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
where application_id in (16908, 16374, 16900)
	and primary_state is true
;

update comment
set state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION",
	transition_state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
where transition_state_id like "APPLICATION_REFERENCE%"
	and application_id in (16908, 16374, 16900)
;

update comment inner join comment_state
	on comment.id = comment_state.comment_id
set comment_state.state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
where comment.application_id in (16908, 16374, 16900)
	and comment_state.primary_state is true
	and comment.state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
;

update comment inner join comment_transition_state
	on comment.id = comment_transition_state.comment_id
set comment_transition_state.state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
where comment.application_id in (16908, 16374, 16900)
	and comment_transition_state.primary_state is true
	and comment.transition_state_id = "APPLICATION_VERIFICATION_PENDING_COMPLETION"
;

update comment
set state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK",
	transition_state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
where id = 127217
;

update comment_state
set state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
where comment_id = 127217
	and state_id = "APPLICATION_REFERENCE_PENDING_COMPLETION"
;

update comment_transition_state
set state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
where comment_id = 127217
	and state_id = "APPLICATION_REFERENCE_PENDING_COMPLETION"
;

delete resource_state.*
from resource_state inner join application
	on resource_state.application_id = application.id
left join application_referee
	on application.id = application_referee.application_id
	and application_referee.comment_id is null
where application_referee.id is null
	and resource_state.state_id like "APPLICATION_REFERENCE%"
	and resource_state.primary_state is false
;

delete user_role.*
from user_role left join application_referee
	on user_role.user_id = application_referee.user_id
	and user_role.application_id = application_referee.application_id
where user_role.role_id = "APPLICATION_REFEREE"
	and application_referee.id is null
;
