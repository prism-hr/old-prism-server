alter table user_notification
	drop index system_id,
	add unique index (system_id, user_role_id, notification_definition_id),
	drop index institution_id,
	add unique index (institution_id, user_role_id, notification_definition_id),
	drop index program_id,
	add unique index (program_id, user_role_id, notification_definition_id),
	drop index project_id,
	add unique index (project_id, user_role_id, notification_definition_id),
	drop index application_id,
	add unique index (application_id, user_role_id, notification_definition_id)
;

update application inner join user 
	on application.user_id = user.id
inner join user_account
	on user.user_account_id = user_account.id
set user_account.send_application_recommendation_notification = 0
where application.state_id like "APPLICATION_APPROVED_%"
;

delete
from user_notification
where last_notified_date is null
;

alter table user_notification
	modify column last_notified_date date not null
;
