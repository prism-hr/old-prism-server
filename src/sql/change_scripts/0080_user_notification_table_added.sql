create table user_notification (
	id int(10) unsigned not null auto_increment,
	system_id INT(10) unsigned null,
	institution_id INT(10) unsigned null,
	program_id INT(10) unsigned null,
	project_id INT(10) unsigned null,
	application_id INT(10) unsigned null,
	user_role_id int(10) unsigned not null,
	notification_definition_id varchar(100),
	last_notified_date date,
	primary key (id),
	unique index (system_id, user_role_id),
	unique index (institution_id, user_role_id),
	unique index (program_id, user_role_id),
	unique index (project_id, user_role_id),
	unique index (application_id, user_role_id),
	foreign key (system_id) references system (id),
	foreign key (institution_id) references institution (id),
	foreign key (program_id) references program (id),
	foreign key (project_id) references project (id),
	foreign key (application_id) references application (id),
	foreign key (user_role_id) references user_role (id))
;

insert into user_notification (system_id, institution_id, program_id, project_id, application_id, user_role_id, notification_definition_id, last_notified_date)
		select system_id, institution_id, program_id, project_id, application_id, id,
			case
				when role_id = 'APPLICATION_CREATOR' then 'APPLICATION_COMPLETE_REQUEST'
				when role_id = 'APPLICATION_REFEREE' then 'APPLICATION_PROVIDE_REFERENCE_REQUEST'
				when role_id = 'APPLICATION_VIEWER_REFEREE' then 'APPLICATION_PROVIDE_REFERENCE_REQUEST'
			end,
			last_notified_date
			from user_role
			where last_notified_date is not null
;

alter table user_role
		drop column last_notified_date
;
