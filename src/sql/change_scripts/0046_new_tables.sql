create table theme (
    id int(10) unsigned not null auto_increment,
    name varchar(255) not null,
    description text not null,
    adopted_count int(10) unsigned not null,
    created_timestamp datetime,
    updated_timestamp datetime,
    primary key (id),
    unique index (name))
collate = utf8_general_ci 
engine = innodb
;

create table advert_theme (
    id int(10) unsigned not null auto_increment,
    advert_id int(10) unsigned not null,
    theme_id int(10) unsigned not null,
    description text,
    primary key (id),
    unique index (advert_id, theme_id),
    index (theme_id))
collate = utf8_general_ci
engine = innodb
;

create table application_theme (
    id int(10) unsigned not null auto_increment,
    application_id int(10) unsigned not null,
    theme_id int(10) unsigned not null,
    description text not null,
    preference int(1) unsigned not null,
    primary key (id),
    unique index (application_id, theme_id),
    index (theme_id),
    foreign key (application_id) references application (id),
    foreign key (theme_id) references theme (id))
collate = utf8_general_ci
engine = innodb
;

alter table application_personal_detail
    change column gender_id gender varchar(50),
    drop index gender_id,
    add index (gender),
    add column ethnicity varchar(50) after skype,
    add column disability varchar(50) after ethnicity,
    add index (ethnicity),
    add index (disability),
    drop index imported_nationality_id,
    add index (nationality_id),
    drop index imported_domicile_id,
    add index (domicile_id)
;

alter table user_personal_detail
    change column gender_id gender varchar(50),
    drop index imported_gender_id,
    add index (gender),
    modify column date_of_birth date,
    add column ethnicity varchar(50) after skype,
    add column disability varchar(50) after ethnicity,
    add index (ethnicity),
    add index (disability),
    drop index imported_nationality_id,
    add index (nationality_id),
    drop index imported_domicile_id,
    add index (domicile_id)
;

alter table application_document
    modify column personal_summary varchar(5000)
;

alter table user_document
    modify column personal_summary varchar(5000)
;

create table advert_location (
    id int(10) unsigned not null auto_increment,
    advert_id int(10) unsigned not null,
    location_advert_id int(10) unsigned not null,
    primary key (id),
    unique index (advert_id, location_advert_id),
    index (location_advert_id),
    foreign key (advert_id) references advert (id),
    foreign key (location_advert_id) references advert (id))
collate = utf8_general_ci
engine = innodb
;

create table application_location (
    id int(10) unsigned not null auto_increment,
    application_id int(10) unsigned not null,
    location_advert_id int(10) unsigned not null,
    description text,
    preference int(1) unsigned not null,
    primary key (id),
    unique index (application_id, location_advert_id),
    index (location_advert_id),
    foreign key (application_id) references application (id),
    foreign key (location_advert_id) references advert (id))
collate = utf8_general_ci
engine = innodb
;

create table application_award (
    id int(10) unsigned not null auto_increment,
    application_id int(10) unsigned not null,
    name varchar(255) not null,
    description text not null,
    award_year int(4) unsigned not null,
    award_month int(2) unsigned not null,
    last_update_timestamp datetime,
    primary key (id),
    unique index (application_id, name, award_year, award_month),
    foreign key (application_id) references application (id))
collate = utf8_general_ci
engine = innodb
;
