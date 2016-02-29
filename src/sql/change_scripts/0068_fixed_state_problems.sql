delete from resource_state_transition_summary
where transition_state_selection like "%APPLICATION_VALIDATION_PENDING_COMPLETION%"
;
