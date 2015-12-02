alter table state_action_pending
    drop index system_id,
    add index (system_id, action_id),
    drop index institution_id,
    add index (institution_id, action_id),
    drop index department_id,
    add index (department_id, action_id),
    drop index program_id,
    add index (program_id, action_id),
    drop index project_id,
    add index (project_id, action_id),
    drop index application_id,
    add index (application_id, action_id)
;

update state_action_pending
set state_id = "INSTITUTION_APPROVED"
where institution_id is not null
;

update state_action_pending
set state_id = "DEPARTMENT_APPROVED"
where department_id is not null
;

update state_action_pending
set state_id = "PROGRAM_APPROVED"
where program_id is not null
;

update state_action_pending
set state_id = "PROJECT_APPROVED"
where project_id is not null
;

alter table state_action_pending
    modify column state_id varchar(100) not null
;

create table state_action_pending_theme (
    id int(10) unsigned not null auto_increment,
    state_action_pending_id int(10) unsigned not null,
    theme_id int(10) unsigned not null,
    primary key (id),
    unique index (state_action_pending_id, theme_id),
    index (theme_id),
    foreign key (state_action_pending_id) references state_action_pending (id),
    foreign key (theme_id) references theme (id))
collate = utf8_general_ci
engine = innodb
;

create table state_action_pending_location (
    id int(10) unsigned not null auto_increment,
    state_action_pending_id int(10) unsigned not null,
    location_advert_id int(10) unsigned not null,
    primary key (id),
    unique index (state_action_pending_id, location_advert_id),
    index (location_advert_id),
    foreign key (state_action_pending_id) references state_action_pending (id),
    foreign key (location_advert_id) references advert (id))
collate = utf8_general_ci
engine = innodb
;

create table resource_state_action_pending (
    id int(10) unsigned not null auto_increment,
    system_id int(10) unsigned,
    institution_id int(10) unsigned,
    department_id int(10) unsigned,
    program_id int(10) unsigned,
    project_id int(10) unsigned,
    application_id int(10) unsigned,
    state_action_pending_id int(10) unsigned not null,
    primary key (id),
    unique index (system_id, state_action_pending_id),
    unique index (institution_id, state_action_pending_id),
    unique index (department_id, state_action_pending_id),
    unique index (program_id, state_action_pending_id),
    unique index (project_id, state_action_pending_id),
    unique index (application_id, state_action_pending_id),
    index (state_action_pending_id),
    foreign key (system_id) references system (id),
    foreign key (institution_id) references institution (id),
    foreign key (department_id) references department (id),
    foreign key (program_id) references program (id),
    foreign key (project_id) references project (id),
    foreign key (application_id) references application (id))
collate = utf8_general_ci
engine = innodb
;
