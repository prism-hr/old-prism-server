set foreign_key_checks = 0
;

update action_redaction
set action_id = "APPLICATION_COMPLETE_IDENTIFICATION_STAGE"
where action_id = "APPLICATION_COMPLETE_IDENTIFICATION_STATE"
;

update state_action
set action_id = "APPLICATION_COMPLETE_IDENTIFICATION_STAGE"
where action_id = "APPLICATION_COMPLETE_IDENTIFICATION_STATE"
;

set foreign_key_checks = 1
;

update action
set id = "APPLICATION_COMPLETE_IDENTIFICATION_STAGE"
where id = "APPLICATION_COMPLETE_IDENTIFICATION_STATE"
;
