alter table notification_definition
	drop foreign key notification_definition_ibfk_2,
	drop index reminder_notification_template_id,
	drop column reminder_definition_id
;

alter table notification_configuration
	drop column reminder_interval
;

drop table user_notification
;
