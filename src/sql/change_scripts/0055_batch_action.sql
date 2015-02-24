alter table action
	drop column emphasized_action
;

create table resource_batch (
	id int(10) unsigned not null auto_increment,
	system_id int(10) unsigned,
	institution_id int(10) unsigned,
	program_id int(10) unsigned,
	project_id int(10) unsigned,
	resource_batch_type varchar(50) not null,
	name varchar(255) not null,
	primary key (id),
	unique index (system_id, resource_batch_type, name),
	unique index (institution_id, resource_batch_type, name),
	unique index (program_id, resource_batch_type, name),
	unique index (project_id, resource_batch_type, name),
	foreign key (system_id) references system (id),
	foreign key (institution_id) references institution (id),
	foreign key (program_id) references program (id),
	foreign key (project_id) references project (id)
) engine = innodb
;

alter table application
	add column resource_batch_id int(10) unsigned after advert_id,
	add index (resource_batch_id),
	add foreign key (resource_batch_id) references resource_batch (id)
;

alter table project
	add column resource_batch_id int(10) unsigned after program_id,
	add index (resource_batch_id),
	add foreign key (resource_batch_id) references resource_batch (id)
;

alter table program
	add column resource_batch_id int(10) unsigned after department_id,
	add index (resource_batch_id),
	add foreign key (resource_batch_id) references resource_batch (id)
;

alter table institution
	add column resource_batch_id int(10) unsigned after system_id,
	add index (resource_batch_id),
	add foreign key (resource_batch_id) references resource_batch (id)
;

alter table comment
	add column resource_batch_id int(10) unsigned after application_id,
	add index (system_id, resource_batch_id),
	add index (institution_id, resource_batch_id),
	add index (program_id, resource_batch_id),
	add index (project_id, resource_batch_id),
	add index (application_id, resource_batch_id),
	add index (resource_batch_id),
	add foreign key (resource_batch_id) references resource_batch (id)
;

create table resource_batch_assigned_user (
	id int(10) unsigned not null auto_increment,
	resource_batch_id int(10) unsigned not null,
	user_id int(10) unsigned not null,
	role_id varchar(50) not null,
	primary key (id),
	unique index (resource_batch_id, user_id, role_id),
	index (user_id),
	index (role_id),
	foreign key (resource_batch_id) references resource_batch (id),
	foreign key (user_id) references user (id),
	foreign key (role_id) references role (id)
) engine = innodb
;

create table state_transition_resource_batch (
	id varchar(50) not null,
	scope_id varchar(50) not null,
	primary key (id),
	index (scope_id),
	foreign key (scope_id) references scope (id)
) engine = innodb
;

alter table state_transition
	add column resource_batch_start_id varchar(50) after state_transition_evaluation_id,
	add column resource_batch_close_id varchar(50) after resource_batch_start,
	add index (resource_batch_start_id),
	add index (resource_batch_close_id),
	add foreign key (resource_batch_start_id) references resource_batch (id),
	add foreign key (resource_batch_close_id) references resource_batch (id)
;

alter table resource_batch
	add column application_shortlisting_mode varchar(50),
	add column application_shortlisting_deadline datetime
;

create table state_transition_resource_batch_role_assignment (
	state_transition_resource_batch_id varchar(50) not null,
	role_id varchar(50) not null,
	primary key (state_transition_resource_batch_id, role_id),
	index (role_id),
	foreign key (state_transition_resource_batch_id) references state_transition_resource_batch (id),
	foreign key (role_id) references role (id)
) engine = innodb
;

