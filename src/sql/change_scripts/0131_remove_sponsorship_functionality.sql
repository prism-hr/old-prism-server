alter table comment
	drop foreign key comment_ibfk_18,
	drop foreign key comment_ibfk_19,
	drop column institution_sponsor_id,
	drop column sponsorship_currency_specified,
	drop column sponsorship_currency_converted,
	drop column sponsorship_amount_specified,
	drop column sponsorship_amount_converted,
	drop column sponsorship_target_fulfilled,
	drop column sponsorship_rejection_id
;

alter table advert
	drop column sponsorship_purpose,
	drop column sponsorship_target,
	drop column sponsorship_secured
;

delete
from resource_condition
where action_condition = "ACCEPT_SPONSOR"
;

delete 
from role_transition
where state_transition_id in (
	select id 
	from state_transition
	where state_action_id in (
		select id 
		from state_action
		where action_id in (
			select id
			from action
			where id like "%_PROVIDE_SPONSORSHIP")))
;

delete 
from state_transition
where state_action_id in (
	select id 
	from state_action
	where action_id in (
		select id
		from action
		where id like "%_PROVIDE_SPONSORSHIP")
)
;

delete 
from state_transition
where transition_action_id in (
	select id
	from action
	where id like "%_PROVIDE_SPONSORSHIP")
;

delete 
from state_action_notification
where state_action_id in (
	select id 
	from state_action
	where action_id in (
		select id
		from action
		where id like "%_PROVIDE_SPONSORSHIP")
)
;

delete 
from state_action
where action_id in (
	select id
	from action
	where id like "%_PROVIDE_SPONSORSHIP")
;

delete 
from comment_assigned_user
where comment_id in (
	select id
	from comment
	where action_id like "%_PROVIDE_SPONSORSHIP")
;

delete 
from comment_state
where comment_id in (
	select id
	from comment
	where action_id like "%_PROVIDE_SPONSORSHIP")
;

delete 
from comment_transition_state
where comment_id in (
	select id
	from comment
	where action_id like "%_PROVIDE_SPONSORSHIP")
;

delete
from comment
where action_id like "%_PROVIDE_SPONSORSHIP"
;

delete 
from action
where id like "%_PROVIDE_SPONSORSHIP"
;
