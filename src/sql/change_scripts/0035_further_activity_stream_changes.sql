alter table user_role
    add column accepted_timestamp datetime after assigned_timestamp,
    add column sequence_identifier varchar(23) after accepted_timestamp,
    add index (accepted_timestamp),
    add unique index (sequence_identifier),
    add index (system_id, sequence_identifier),
    add index (institution_id, sequence_identifier),
    add index (department_id, sequence_identifier),
    add index (program_id, sequence_identifier),
    add index (project_id, sequence_identifier),
    add index (application_id, sequence_identifier)
;

alter table advert_target
    add column accepted_timestamp datetime after partnership_state,
    add column sequence_identifier varchar(23) after accepted_timestamp,
    add index (accepted_timestamp),
    add unique index (sequence_identifier),
    add index (advert_id, sequence_identifier),
    add index (target_advert_id, sequence_identifier)
;

update user_role inner join user
    on user_role.user_id = user.id
inner join user_account
    on user.user_account_id = user_account.id
set user_role.sequence_identifier = concat(unix_timestamp(user_role.assigned_timestamp), lpad(user_role.id, 13, "0"))
where user_account.enabled is true
;

update advert_target
set sequence_identifier = concat(unix_timestamp(now()), lpad(id, 13, "0"))
where partnership_state = "ENDORSEMENT_PROVIDED"
;
