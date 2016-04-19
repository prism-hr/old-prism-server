alter table state_action_pending
    add column state_action_pending_type varchar(50) not null default "INVITE" after action_id,
    add index (system_id, state_action_pending_type),
    add index (institution_id, state_action_pending_type),
    add index (department_id, state_action_pending_type),
    add index (program_id, state_action_pending_type),
    add index (project_id, state_action_pending_type),
    add index (application_id, state_action_pending_type)
;

alter table state_action_pending
    modify column state_action_pending_type varchar(50) not null
;

alter table state_action_pending
    drop column content,
    add column transition_state_id varchar(100) after action_id,
    add index (transition_state_id),
    add foreign key (transition_state_id) references state (id)
;
