INSERT INTO STATE (id, state_group_id, parallelizable, hidden, scope_id)
VALUES ("APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED", "APPLICATION_WITHDRAWN", 0, 0, "APPLICATION")
;

UPDATE APPLICATION
SET state_id = "APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED"
WHERE state_id = "APPLICATION_WITHDRAWN_COMPLETED"
AND previous_state_id IN ("APPLICATION_UNSUBMITTED", "APPLICATION_UNSUBMITTED_PENDING_COMPLETION")
;
