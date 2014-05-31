/* Inherited state action to state transition table */

--ALTER TABLE STATE_TRANSITION
--	ADD COLUMN delegate_state_transition_id INT(10) UNSIGNED,
--	ADD INDEX (delegate_state_transition_id),
--	ADD FOREIGN KEY (delegate_state_transition_id) REFERENCES STATE_TRANSITION (id)
--;
--
--UPDATE STATE_ACTION INNER JOIN STATE_ACTION_INHERITANCE
--	ON STATE_ACTION.id = STATE_ACTION_INHERITANCE.state_action_id
--SET STATE_ACTION.delegate_state_action_id = STATE_ACTION_INHERITANCE.inherited_state_action_id
--;
--
--DROP TABLE STATE_ACTION_INHERITANCE
--;
--
--ALTER TABLE STATE_ACTION
--	DROP INDEX state_id,
--	ADD UNIQUE INDEX (state_id, action_id)
--;

/* Drop the primary key columns in the join table entities (role transition exclusion, state action inheritance) */

/* Simplify update notification functionality */

/* Configuration parameter value not nullable */

/* State duration expiry not null */

/* Remove fertile and assessment states (unused) */

/* User specified due date in comment */