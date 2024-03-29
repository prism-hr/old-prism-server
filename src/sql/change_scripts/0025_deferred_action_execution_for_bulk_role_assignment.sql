create table state_action_pending (
    id int(10) unsigned not null auto_increment,
    system_id int(10) unsigned,
    institution_id int(10) unsigned,
    department_id int(10) unsigned,
    program_id int(10) unsigned,
    project_id int(10) unsigned,
    application_id int(10) unsigned,
    user_id int(10) unsigned not null,
    action_id varchar(100) not null,
    content text,
    assign_user_role_id varchar(50),
    assign_user_list text,
    assign_user_message text,
    created_timestamp datetime not null,
    primary key (id),
    index (system_id),
    index (institution_id),
    index (department_id),
    index (program_id),
    index (project_id),
    index (application_id),
    index (user_id),
    index (action_id),
    index (assign_user_role_id),
    foreign key (system_id) references system (id),
    foreign key (institution_id) references institution (id),
    foreign key (department_id) references department (id),
    foreign key (program_id) references program (id),
    foreign key (project_id) references project (id),
    foreign key (application_id) references application (id),
    foreign key (action_id) references action (id),
    foreign key (user_id) references user (id),
    foreign key (assign_user_role_id) references role (id))
collate = utf8_general_ci
engine = innodb
;

alter table state_action_pending
    drop column created_timestamp
;
