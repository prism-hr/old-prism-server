SET FOREIGN_KEY_CHECKS = 0
;

alter table user_feedback
	add column system_id int(10) unsigned after role_category,
	modify column institution_id int(10) unsigned,
	add column program_id int(10) unsigned after institution_id,
	add column project_id int(10) unsigned after program_id,
	add column application_id int(10) unsigned after project_id,
	modify column user_id int(10) unsigned after application_id,
	add column action_id varchar(100) not null after user_id,
	drop index institution_id,
	add index (system_id, sequence_identifier),
	add index (institution_id, sequence_identifier),
	add index (program_id, sequence_identifier),
	add index (project_id, sequence_identifier),
	add index (application_id, sequence_identifier),
	add index (action_id),
	add foreign key (system_id) references system (id),
	add foreign key (program_id) references program (id),
	add foreign key (project_id) references project (id),
	add foreign key (application_id) references application (id),
	add foreign key (action_id) references action (id)
;

SET FOREIGN_KEY_CHECKS = 1
;
