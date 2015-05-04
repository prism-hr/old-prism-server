create table resource_condition (
	id int(10) unsigned not null auto_increment,
	system_id int(10) unsigned,
	institution_id int(10) unsigned,
	program_id int(10) unsigned,
	project_id int(10) unsigned,
	application_id int(10) unsigned,
	action_condition varchar(50) not null,
	primary key (id),
	unique index (system_id, action_condition),
	unique index (institution_id, action_condition),
	unique index (program_id, action_condition),
	unique index (project_id, action_condition),
	unique index (application_id, action_condition),
	foreign key (system_id) references system (id),
	foreign key (institution_id) references institution (id),
	foreign key (program_id) references program (id),
	foreign key (project_id) references project (id),
	foreign key (application_id) references application (id)) 
engine = innodb
collate = utf8_general_ci
;

alter table state_action
	add column action_condition varchar(50) after raises_urgent_flag,
	add index (action_condition)
;

insert into resource_condition (institution_id, action_condition)
	select id, "ACCEPT_PROGRAM"
	from institution
;

insert into resource_condition (program_id, action_condition)
	select id, "ACCEPT_PROJECT"
	from program
		union
	select id, "ACCEPT_APPLICATION"
	from program
;

insert into resource_condition (project_id, action_condition)
	select id, "ACCEPT_APPLICATION"
	from project
;
