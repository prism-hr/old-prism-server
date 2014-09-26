/* Mistakes in workflow definition */

UPDATE STATE_ACTION INNER JOIN STATE_TRANSITION
	ON STATE_ACTION.id = STATE_TRANSITION.state_action_id
SET STATE_TRANSITION.transition_state_id = "INSTITUTION_APPROVED"
WHERE STATE_ACTION.action_id = "SYSTEM_CREATE_INSTITUTION"
;

UPDATE STATE_ACTION INNER JOIN STATE_TRANSITION
	ON STATE_ACTION.id = STATE_TRANSITION.state_action_id
SET STATE_TRANSITION.transition_action_id = "PROGRAM_CREATE_PROJECT"
WHERE STATE_ACTION.action_id = "PROGRAM_CREATE_PROJECT"
;

UPDATE STATE_ACTION INNER JOIN STATE_TRANSITION
	ON STATE_ACTION.id = STATE_TRANSITION.state_action_id
SET STATE_TRANSITION.transition_action_id = "INSTITUTION_CREATE_PROGRAM"
WHERE STATE_ACTION.action_id = "INSTITUTION_CREATE_PROGRAM"
	AND STATE_TRANSITION.transition_state_id = "PROGRAM_APPROVED"
;