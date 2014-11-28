/* Error in workflow configuration */

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE STATE_TRANSITION
SET state_transition_evaluation_id = "APPLICATION_STATE_COMPLETED_OUTCOME"
WHERE state_transition_evaluation_id = "APPLICATION_STAGE_COMPLETED_OUTCOME"
;

SET FOREIGN_KEY_CHECKS = 1
;
