rename table user_notification to old_user_notification
;

create table user_notification (
	id int(10) unsigned not null auto_increment,
	system_id int(10) unsigned null default null,
	institution_id int(10) unsigned null default null,
	program_id int(10) unsigned null default null,
	project_id int(10) unsigned null default null,
	application_id int(10) unsigned null default null,
	user_id int(10) unsigned not null,
	notification_definition_id varchar(100) not null,
	last_notified_date date not null,
	primary key (id),
	unique index (system_id, user_id, notification_definition_id),
	unique index (institution_id, user_id, notification_definition_id),
	unique index (program_id, user_id, notification_definition_id),
	unique index (project_id, user_id, notification_definition_id),
	unique index (application_id, user_id, notification_definition_id),
	index (user_id),
	foreign key (system_id) references system (id),
	foreign key (institution_id) references institution (id),
	foreign key (program_id) references program (id),
	foreign key (project_id) references project (id),
	foreign key (application_id) references application (id),
	foreign key (user_id) references user (id)
)
collate = utf8_general_ci
engine = innodb
;

insert into user_notification (system_id, institution_id, program_id, project_id,
	application_id, user_id, notification_definition_id, last_notified_date)
	select old_user_notification.system_id, old_user_notification.institution_id,
		old_user_notification.program_id, old_user_notification.project_id,
		old_user_notification.application_id, user_role.user_id, 
		old_user_notification.notification_definition_id, 
		max(old_user_notification.last_notified_date)
	from old_user_notification inner join user_role
		on old_user_notification.user_role_id = user_role.id
	where old_user_notification.notification_definition_id is not null
	group by user_role.user_id
;

drop table old_user_notification
;

create table user_connection (
	id int(10) unsigned not null auto_increment,
	user_requested_id int(10) unsigned not null,
	user_connected_id int(10) unsigned not null,
	connected int(1) unsigned not null,
	created_timestamp datetime not null,
	primary key (id),
	unique index (user_requested_id, user_connected_id),
	index (user_connected_id),
	foreign key (user_requested_id) references user_requested (id),
	foreign key (user_connected_id) references user_connected (id))
collate = utf8_general_ci
engine = innodb
;
