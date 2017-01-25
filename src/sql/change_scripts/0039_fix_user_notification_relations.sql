set session foreign_key_checks = 0
;

update state_action
set notification_definition_id = "APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_NOTIFICATION"
where notification_definition_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
;

update state_action_notification
set notification_definition_id = "APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_NOTIFICATION"
where notification_definition_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
;

update notification_configuration
set notification_definition_id = "APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_NOTIFICATION"
where notification_definition_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
;

update user_notification
set notification_definition_id = "APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_NOTIFICATION"
where notification_definition_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
;

update notification_definition
set id = "APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_NOTIFICATION"
where id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
;

set session foreign_key_checks = 1
;

alter table user_notification
    add index (notification_definition_id),
    add foreign key (notification_definition_id) references notification_definition (id)
;
