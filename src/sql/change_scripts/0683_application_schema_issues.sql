/* Mistakes in workflow definition */

DELETE ROLE_TRANSITION.*
FROM ROLE_TRANSITION INNER JOIN STATE_TRANSITION
	ON ROLE_TRANSITION.state_transition_id = STATE_TRANSITION.id
INNER JOIN STATE_ACTION
	ON STATE_TRANSITION.state_action_id = STATE_ACTION.id
WHERE STATE_TRANSITION.transition_state_id LIKE "APPLICATION_UNSUBMITTED%"
	AND STATE_ACTION.action_id ="APPLICATION_COMPLETE"
;

DELETE STATE_TRANSITION.* 
FROM STATE_TRANSITION INNER JOIN STATE_ACTION
	ON STATE_TRANSITION.state_action_id = STATE_ACTION.id
WHERE STATE_TRANSITION.transition_state_id LIKE "APPLICATION_UNSUBMITTED%"
	AND STATE_ACTION.action_id = "APPLICATION_COMPLETE"
;

ALTER TABLE STATE_ACTION
	ADD COLUMN do_post_comment INT(1) UNSIGNED
;

UPDATE STATE_ACTION INNER JOIN (
	SELECT state_action_id AS state_action_id,
		MAX(do_post_comment) AS do_post_comment
	FROM STATE_TRANSITION
	GROUP BY state_action_id) AS AUDITABLE_TRANSITION
	ON STATE_ACTION.id = AUDITABLE_TRANSITION.state_action_id
SET STATE_ACTION.do_post_comment = AUDITABLE_TRANSITION.do_post_comment
;

UPDATE STATE_ACTION
SET do_post_comment = 0
WHERE do_post_comment IS NULL
;

ALTER TABLE STATE_TRANSITION
	DROP COLUMN do_post_comment
;

ALTER TABLE STATE_ACTION
	MODIFY COLUMN do_post_comment INT(1) UNSIGNED NOT NULL AFTER is_default_action
;

UPDATE STATE_TRANSITION
SET state_transition_evaluation = NULL
WHERE state_transition_evaluation = "APPLICATION_COMPLETED_OUTCOME"
;

/* Explicity identify resource creation actions */

ALTER TABLE ACTION
	ADD COLUMN creation_scope_id VARCHAR(50) AFTER scope_id,
	ADD INDEX (creation_scope_id),
	ADD FOREIGN KEY (creation_scope_id) REFERENCES SCOPE (id)
;

UPDATE ACTION
SET creation_scope_id = "INSTITUTION"
WHERE id LIKE "%_CREATE_INSTITUTION"
;

UPDATE ACTION
SET creation_scope_id = "PROGRAM"
WHERE id LIKE "%_CREATE_PROGRAM"
;

UPDATE ACTION
SET creation_scope_id = "PROJECT"
WHERE id LIKE "%_CREATE_PROJECT"
;

UPDATE ACTION
SET creation_scope_id = "APPLICATION"
WHERE id LIKE "%_CREATE_APPLICATION"
;
