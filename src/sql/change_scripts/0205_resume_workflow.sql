alter table user_role
	add column resume_id int(10) unsigned after application_id,
	add unique index (resume_id, user_id, role_id),
	add foreign key (resume_id) references application (id)
;

alter table comment
	add column resume_id int(10) unsigned after application_id,
	add index (resume_id),
	add foreign key (resume_id) references application (id)
;

alter table resource_condition
	add column resume_id int(10) unsigned after application_id,
	add unique index (resume_id, action_condition),
	add foreign key (resume_id) references application (id)
;

alter table state_transition_pending
	add column resume_id int(10) unsigned after application_id,
	add unique index (resume_id, action_id),
	add foreign key (resume_id) references application (id)
;

alter table user_notification
	add column resume_id int(10) unsigned after application_id,
	add unique index (resume_id, user_id, notification_definition_id),
	add foreign key (resume_id) references application (id)
;

alter table user_feedback
	add column resume_id int(10) unsigned after application_id,
	add index (resume_id, sequence_identifier),
	add foreign key (resume_id) references application (id)
;

alter table resource_state
	add column resume_id int(10) unsigned after application_id,
	add unique index (resume_id, state_id),
	add foreign key (resume_id) references application (id)
;

alter table resource_previous_state
	add column resume_id int(10) unsigned after application_id,
	add unique index (resume_id, state_id),
	add foreign key (resume_id) references application (id)
;
