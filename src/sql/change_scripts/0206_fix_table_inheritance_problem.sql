alter table application
	add column scope_id varchar(50) not null default "APPLICATION" after id,
	add index (scope_id, sequence_identifier),
	add foreign key (scope_id) references scope (id)
;

alter table application
	modify column scope_id varchar(50) not null
;
