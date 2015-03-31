delete
from user_role
where role_id = "APPLICATION_SUGGESTED_SUPERVISOR"
;

delete
from role_transition
where "APPLICATION_SUGGESTED_SUPERVISOR" in (role_id, transition_role_id)
;

delete
from role
where id = "APPLICATION_SUGGESTED_SUPERVSIOR"
;

alter table state_group
	drop index scope_id,
	add index (scope_id)
;

update state_group
set sequence_order = sequence_order - 1
;

alter table state_group
	drop index scope_id,
	change column sequence_order ordinal int(2) unsigned not null,
	add unique index (scope_id, ordinal)
;

alter table state_action
	drop column is_default_action
;
