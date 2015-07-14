alter table action
	add column system_invocation_only int(1) unsigned not null default 0,
	add index (system_invocation_only)
;

update action
set system_invocation_only = 1
where action_type = "SYSTEM_INVOCATION"
;

alter table action
	modify column system_invocation_only int(1) unsigned not null,
	drop index action_type_id,
	drop column action_type
;

alter table action
	modify column system_invocation_only int(1) unsigned not null after id
;

insert into state_group
values ("DEPARTMENT_APPROVED", 2, 1, "DEPARTMENT")
;

insert into state(id, state_group_id, scope_id)
values ("DEPARTMENT_APPROVED", "DEPARTMENT_APPROVED", "DEPARTMENT")
;

insert into action(id, system_invocation_only, action_category, rating_action, transition_action, 
	declinable_action, visible_action, fallback_action_id, scope_id, creation_scope_id)
values ("SYSTEM_VIEW_DEPARTMENT_LIST", false, "VIEW_RESOURCE_LIST", false, false, false, false,
	"SYSTEM_VIEW_EDIT", "SYSTEM", null),
("INSTITUTION_CREATE_DEPARTMENT", false, "CREATE_RESOURCE", false, true, false, true,
	"SYSTEM_VIEW_DEPARTMENT_LIST", "INSTITUTION", "DEPARTMENT")
;

update department
set state_id = "DEPARTMENT_APPROVED",
	previous_state_id = "DEPARTMENT_APPROVED"
;

insert into resource_state (department_id, state_id, primary_state, created_date)
	select id, state_id, true, date(created_timestamp)
	from department
;

insert into resource_previous_state (department_id, state_id, primary_state, created_date)
	select id, state_id, true, date(created_timestamp)
	from department
;

alter table comment
	drop index department_id,
	add index (department_id)
;

insert into comment(department_id, user_id, action_id, 
	declined_response, state_id, transition_state_id, created_timestamp)
	select id, user_id, "INSTITUTION_CREATE_DEPARTMENT", false, state_id, 
		state_id, created_timestamp
	from department
;

insert into comment_state (comment_id, state_id, primary_state)
	select id, state_id, true
	from comment
	where department_id is not null
;

insert into comment_transition_state (comment_id, state_id, primary_state)
	select id, state_id, true
	from comment
	where department_id is not null
;

insert into role
values ("DEPARTMENT_ADMINISTRATOR", "ADMINISTRATOR", true, "DEPARTMENT")
;

insert into comment_assigned_user (comment_id, user_id, role_id, role_transition_type)
	select id, user_id, "DEPARTMENT_ADMINISTRATOR", "CREATE"
	from comment
	where department_id is not null
;

delete 
from role
where id like "%_ADVERTISER"
;

