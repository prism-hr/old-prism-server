alter table state_action_pending
    drop index system_id,
    add index (system_id, action_id),
    drop index institution_id,
    add index (institution_id, action_id),
    drop index department_id,
    add index (department_id, action_id),
    drop index program_id,
    add index (program_id, action_id),
    drop index project_id,
    add index (project_id, action_id),
    drop index application_id,
    add index (application_id, action_id)
;
