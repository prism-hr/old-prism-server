delete
from comment_assigned_user
where role_id in (
	select id
	from role
	where id like "%_SPONSOR")
;

delete
from user_role
where role_id in (
	select id
	from role
	where id like "%_SPONSOR")
;

delete
from state_action_notification
where role_id in (
	select id
	from role
	where id like "%_SPONSOR")
;

delete
from state_action_assignment
where role_id in (
	select id
	from role
	where id like "%_SPONSOR")
;

delete
from role_transition
where transition_role_id in (
	select id
	from role
	where id like "%_SPONSOR")
;

delete
from role_transition
where role_id in (
	select id
	from role
	where id like "%_SPONSOR")
;

delete
from role
where id like "%_SPONSOR"
;
