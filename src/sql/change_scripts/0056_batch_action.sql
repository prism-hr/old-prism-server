create table resource_batch_process (
	id varchar(50) not null,
	scope_id varchar(50) not null,
	primary key (id),
	index (scope_id),
	foreign key (scope_id) references scope (id)
) engine = innodb
;

create table resource_batch_process_scope (
	resource_batch_process_id varchar(50) not null,
	batch_scope_id varchar(50) not null,
	primary key (resource_batch_process_id, batch_scope_id),
	index (scope_id),
	foreign key (resource_batch_process_id) reference resource_batch_process (id),
	foreign key (batch_scope_id) references scope (id)
) engine = innodb
;

create table resource_batch (
	id int(10) unsigned not null auto_increment,
	system_id int(10) unsigned,
	institution_id int(10) unsigned,
	program_id int(10) unsigned,
	project_id int(10) unsigned,
	resource_batch_process_id varchar(50) not null,
	name varchar(255) not null,
	primary key (id),
	unique index (system_id, state_group_id, name),
	unique index (institution_id, state_group_id, name),
	unique index (program_id, state_group_id, name),
	unique index (project_id, state_group_id, name),
	index (resource_batch_process_id),
	foreign key (system_id) references system (id),
	foreign key (institution_id) references institution (id),
	foreign key (program_id) references program (id),
	foreign key (project_id) references project (id),
	foreign key (resource_batch_process_id) references resource_batch_process (id)
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
	add index (resource_batch_id),
	add foreign key (resource_batch_id) references resource_batch (id),
	add column resource_batch_comment_id int(10) unsigned after resource_batch_id,
	add index (resource_batch_comment_id),
	add foreign key (resource_batch_comment_id) references comment (id)
;

alter table comment
	drop column creator_ip_address
;

alter table action
	add column action_behaviour varchar(50) not null default "POST" after action_category
;

alter table action
	modify column action_behaviour varchar(50) not null
;

alter table state_transition
	add column resource_batch_process_join_id varchar(50),
	add column resource_batch_process_exit_id varchar(50),
	add index (resource_batch_process_join_id),
	add index (resource_batch_process_exit_id),
	add foreign key (resource_batch_process_join_id) references resource_batch_process (id),
	add foreign key (resource_batch_process_exit_id) references resource_batch_process (id)
;

alter table comment_state
	drop column primary_state,
	drop primary key,
	drop column id,
	add primary key (comment_id, state_id)
;

alter table comment_transition_state
	drop column primary_state,
	drop primary key,
	drop column id,
	add primary key (comment_id, state_id)
;
