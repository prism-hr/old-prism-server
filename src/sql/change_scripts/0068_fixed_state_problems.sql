UPDATE resource_state_transition_summary
SET transition_state_selection = replace(transition_state_selection, "APPLICATION_VALIDATION_PENDING_COMPLETION",
                                         "APPLICATION_VALIDATION")
;
