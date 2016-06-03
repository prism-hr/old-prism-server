create table advert_target_pending (
    id int(10) unsigned not null auto_increment,
    advert_id int(10) unsigned not null,
    user_id int(10) unsigned not null,
    advert_target_list text not null,
    advert_target_message text,
    primary key (id),
    index (advert_id),
    index (user_id),
    foreign key (advert_id) references advert (id),
    foreign key (user_id) references user (id))
collate = utf8_general_ci
engine = innodb
;
