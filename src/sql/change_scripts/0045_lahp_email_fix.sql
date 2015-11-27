alter table user_notification
    add column active int(1) unsigned not null default 1 after notification_definition_id,
    add index (system_id, active),
    add index (institution_id, active),
    add index (department_id, active),
    add index (program_id, active),
    add index (project_id, active),
    add index (application_id, active)
;

alter table user_notification
    modify column active int(1) unsigned not null
;

alter table user_notification
    drop index system_id,
    drop index institution_id,
    drop index department_id,
    drop index program_id,
    drop index project_id,
    drop index application_id,
    add index (system_id, user_id, notification_definition_id),
    add index (institution_id, user_id, notification_definition_id),
    add index (department_id, user_id, notification_definition_id),
    add index (program_id, user_id, notification_definition_id),
    add index (project_id, user_id, notification_definition_id),
    add index (application_id, user_id, notification_definition_id)
;

alter table user_notification
    change column last_notified_timestamp notified_timestamp datetime not null
;
