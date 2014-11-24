UPDATE COMMENT
SET state_id = "APPLICATION_VALIDATION",
	transition_state_id = "APPLICATION_VALIDATION_PENDING_FEEDBACK"
WHERE action_id = "APPLICATION_ASSESS_ELIGIBILITY"
;

UPDATE COMMENT
SET state_id = "APPLICATION_VALIDATION_PENDING_FEEDBACK",
	transition_state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
WHERE action_id = "APPLICATION_CONFIRM_ELIGIBILITY"
;
