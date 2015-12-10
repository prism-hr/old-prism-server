set session foreign_key_checks = 0
;

update state_transition_evaluation
set id = "APPLICATION_PROVIDED_HIRING_MANAGER_APPROVAL_OUTCOME"
where id = "APPLICATION_CONFIRMED_MANAGEMENT_OUTCOME"
;

update state_transition
set state_transition_evaluation_id = "APPLICATION_PROVIDED_HIRING_MANAGER_APPROVAL_OUTCOME"
where state_transition_evaluation_id = "APPLICATION_CONFIRMED_MANAGEMENT_OUTCOME"
;

set session foreign_key_checks = 1
;

set session foreign_key_checks = 0
;

update notification_configuration
set notification_definition_id = "SYSTEM_USER_INVITATION_NOTIFICATION"
where notification_definition_id = "SYSTEM_INVITATION_NOTIFICATION"
;

update notification_definition
set id = "SYSTEM_USER_INVITATION_NOTIFICATION"
where id = "SYSTEM_INVITATION_NOTIFICATION"
;

set session foreign_key_checks = 1
;
