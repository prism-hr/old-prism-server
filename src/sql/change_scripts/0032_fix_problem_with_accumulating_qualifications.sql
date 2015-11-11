alter table user_qualification
    add column application_qualification_id int(10) unsigned after user_account_id,
    add index (application_qualification_id),
    add foreign key (application_qualification_id) references application_qualification (id)
;

alter table user_employment_position
    add column application_employment_position_id int(10) unsigned after user_account_id,
    add index (application_employment_position_id),
    add foreign key (application_employment_position_id) references application_employment_position (id)
;

alter table user_referee
    add column application_referee_id int(10) unsigned after user_account_id,
    add index (application_referee_id),
    add foreign key (application_referee_id) references application_referee (id)
;
