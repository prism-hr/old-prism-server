set foreign_key_checks = 0
;

alter table state_transition_evaluation
	modify column id varchar(100) not null
;

alter table state_transition
	modify column state_transition_evaluation_id varchar(100)
;

set foreign_key_checks = 1
;
