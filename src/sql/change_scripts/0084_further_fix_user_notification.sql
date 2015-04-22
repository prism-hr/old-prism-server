

insert into user_notification (system_id, institution_id, program_id, project_id,
	application_id, user_id, notification_definition_id, last_notified_date)
	select old_user_notification.system_id, old_user_notification.institution_id,
		old_user_notification.program_id, old_user_notification.project_id,
		old_user_notification.application_id, user_role.user_id, 
		old_user_notification.notification_definition_id, 
		max(old_user_notification.last_notified_date)
	from old_user_notification inner join user_role
		on old_user_notification.user_role_id = user_role.id
	where old_user_notification.notification_definition_id is not null
	group by user_role.user_id
;

drop table old_user_notification
;
