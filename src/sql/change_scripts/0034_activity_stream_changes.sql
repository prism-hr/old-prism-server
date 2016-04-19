create table user_account_update (
    id int(10) unsigned not null auto_increment,
    user_account_id int (10) unsigned not null,
    content text not null,
    created_timestamp datetime not null,
    sequence_identifier varchar(23),
    primary key (id),
    index (user_account_id),
    unique index (sequence_identifier),
    foreign key (user_account_id) references user_account (id))
collate = utf8_general_ci
engine = innodb
;

alter table comment
    add column sequence_identifier varchar(23) after created_timestamp,
    add unique index (sequence_identifier)
;

update comment
set sequence_identifier = concat(unix_timestamp(created_timestamp), lpad(id, 13, "0"))
;
