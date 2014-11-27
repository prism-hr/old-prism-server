DELETE FROM RESOURCE_STATE_TRANSITION_SUMMARY
WHERE transition_state_selection IS NULL
	OR LENGTH(transition_state_selection) = 0
;
