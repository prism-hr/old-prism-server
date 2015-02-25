alter table action
	drop column emphasized_action
;

create table resource_batch_scope (
	state_group_id varchar (50) not null,
	scope_id varchar (50) not null,
	primary key (state_group_id, scope_id),
	index (scope_id),
	foreign key (state_group_id) references state_group (id),
	foreign key (scope_id) references scope (id)
) engine = innodb
;
