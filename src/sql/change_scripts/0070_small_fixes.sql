alter table comment_competence
    modify column remark text
;

create table notification_configuration_document (
    id int(10) unsigned not null auto_increment,
    notification_configuration_id int(10) unsigned not null,
    document_id int(10) unsigned not null,
    primary key (id),
    unique index (notification_configuration_id, document_id),
    unique index (document_id),
    foreign key (notification_configuration_id) references notification_configuration (id),
    foreign key (document_id) references document (id))
collate = utf8_general_ci
engine = innodb
;
