create table state_transition_notification (
	id int(10) unsigned not null auto_increment,
	state_transition_id int(10) unsigned not null,
	role_id varchar(50) not null,
	notification_definition_id varchar(100) not null,
	primary key (id),
	unique index (state_transition_id, role_id, notification_definition_id),
	index (role_id),
	index (notification_definition_id),
	foreign key (state_transition_id) references state_transition (id),
	foreign key (role_id) references role (id),
	foreign key (notification_definition_id) references notification_definition (id))
collate = utf8_general_ci
engine = innodb
;

drop table state_action_notification
;
