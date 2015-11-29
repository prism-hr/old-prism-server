create table user_award (
    id int(10) unsigned not null auto_increment,
    user_account_id int(10) unsigned not null,
    application_award_id int(10) unsigned,
    name varchar(255) not null,
    description text not null,
    award_year int(4) not null,
    award_month int(2) not null,
    primary key (id),
    unique index (user_account_id, name, award_year, award_month),
    index (application_award_id),
    foreign key (user_account_id) references user_account (id),
    foreign key (application_award_id) references application_award (id))
collate = utf8_general_ci
engine = innodb
;
