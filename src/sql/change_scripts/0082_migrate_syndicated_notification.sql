insert ignore into user_notification (system_id, user_role_id, notification_definition_id, last_notified_date)
	select 1, user_role.id, "SYSTEM_INSTITUTION_TASK_REQUEST", user.last_notified_date_application
	from user_role inner join user
		on user_role.user_id = user.id
	where user.last_notified_date_institution is not null
;

insert ignore into user_notification (system_id, user_role_id, notification_definition_id, last_notified_date)
	select 1, user_role.id, "SYSTEM_PROGRAM_TASK_REQUEST", user.last_notified_date_application
	from user_role inner join user
		on user_role.user_id = user.id
	where user.last_notified_date_program is not null
;

insert ignore into user_notification (system_id, user_role_id, notification_definition_id, last_notified_date)
	select 1, user_role.id, "SYSTEM_PROJECT_TASK_REQUEST", user.last_notified_date_application
	from user_role inner join user
		on user_role.user_id = user.id
	where user.last_notified_date_project is not null
;

insert ignore into user_notification (system_id, user_role_id, notification_definition_id, last_notified_date)
	select 1, user_role.id, "SYSTEM_APPLICATION_TASK_REQUEST", user.last_notified_date_application
	from user_role inner join user
		on user_role.user_id = user.id
	where user.last_notified_date_application is not null
;

alter table user
	drop column last_notified_date_system,
	drop column last_notified_date_institution,
	drop column last_notified_date_program,
	drop column last_notified_date_project,
	drop column last_notified_date_application
;

alter table user_account
	drop column last_notified_date_application_recommendation
;

alter table system
	add column last_notified_recommendation_syndicated date after last_notified_update_syndicated
;
