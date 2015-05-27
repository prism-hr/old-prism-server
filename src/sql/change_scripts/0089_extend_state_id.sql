set foreign_key_checks = 0
;

alter table state
	modify column id varchar(100) not null
;

alter table application
	modify column state_id varchar(100),
	modify column previous_state_id varchar(100)
;

alter table project
	modify column state_id varchar(100),
	modify column previous_state_id varchar(100)
;

alter table program
	modify column state_id varchar(100),
	modify column previous_state_id varchar(100)
;

alter table institution
	modify column state_id varchar(100),
	modify column previous_state_id varchar(100)
;

alter table system
	modify column state_id varchar(100),
	modify column previous_state_id varchar(100)
;

alter table comment
	modify column state_id varchar(100),
	modify column transition_state_id varchar(100)
;

alter table resource_state
	modify column state_id varchar(100) not null,
	add index (state_id),
	add foreign key (state_id) references state (id)
;

alter table resource_previous_state
	modify column state_id varchar(100) not null,
	add index (state_id),
	add foreign key (state_id) references state (id)
;

alter table comment_state
	modify column state_id varchar(100) not null
;

alter table comment_transition_state
	modify column state_id varchar(100) not null
;

alter table state_action
	modify column state_id varchar(100) not null
;

alter table state_transition
	modify column transition_state_id varchar(100)
;

set foreign_key_checks = 1
;
