update comment
set state_id = "PROGRAM_APPROVED",
	transition_state_id = "PROGRAM_APPROVED"
where program_id in (904, 905)
;
