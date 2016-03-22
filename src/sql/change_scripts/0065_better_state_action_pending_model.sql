alter table state_action_pending
    drop column state_action_pending_type,
    add column template_comment_id int(10) unsigned,
    add index (template_comment_id),
    add index (system_id, template_comment_id),
    add index (institution_id, template_comment_id),
    add index (department_id, template_comment_id),
    add index (program_id, template_comment_id),
    add index (project_id, template_comment_id),
    add index (application_id, template_comment_id),
    add foreign key (template_comment_id) references comment (id)
;

alter table action
    add column replicable_user_assignment_action int(1) unsigned not null default 0 after visible_action,
    add index (replicable_user_assignment_action)
;

alter table action
    modify column replicable_user_assignment_action int(1) unsigned not null
;
