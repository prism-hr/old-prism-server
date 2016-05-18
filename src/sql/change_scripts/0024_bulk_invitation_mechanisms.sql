create table invitation (
    id int(10) unsigned not null,
    message text,
    primary key (id))
collate = utf8_general_ci
engine = innodb
;

alter table invitation
    add column user_id int(10) unsigned not null after id,
    add index (user_id),
    add foreign key (user_id) references user (id)
;

alter table user_role
    add column invitation_id int(10) unsigned after role_id,
    add index (invitation_id),
    add foreign key (invitation_id) references invitation (id)
;

alter table advert_target
    add column invitation_id int(10) unsigned after accept_advert_user_id,
    add index (invitation_id),
    add foreign key (invitation_id) references invitation (id)
;
