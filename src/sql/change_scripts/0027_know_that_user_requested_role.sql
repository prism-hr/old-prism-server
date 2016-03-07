alter table user_role
    add column requested int(1) unsigned after role_id
;
