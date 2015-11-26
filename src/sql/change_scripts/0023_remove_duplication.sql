update role_transition
set role_transition_type = "DELETE"
where role_transition_type = "RETIRE"
;

update role_transition
set role_transition_type = "UPDATE"
where role_transition_type = "EXHUME"
;
