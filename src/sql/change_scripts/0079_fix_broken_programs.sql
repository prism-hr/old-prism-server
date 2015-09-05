update resource_state
set state_id = "PROGRAM_APPROVED"
where program_id in (904, 905)
;

update program
set state_id = "PROGRAM_APPROVED",
	previous_state_id = "PROGRAM_APPROVED",
	due_date = end_date
where id in (904, 905)
;

delete
from comment_state
where comment_id in (
	select id
	from comment
	where action_id = "PROGRAM_ESCALATE"
		and program_id in (904, 905))
;

delete
from comment_transition_state
where comment_id in (
	select id
	from comment
	where action_id = "PROGRAM_ESCALATE"
		and program_id in (904, 905))
;

delete
from comment
where action_id = "PROGRAM_ESCALATE"
	and program_id in (904, 905)
;

delete
from comment_assigned_user
where comment_id in (
	select id
	from comment
	where action_id = "INSTITUTION_IMPORT_PROGRAM")
;

delete
from comment_state
where comment_id in (
	select id
	from comment
	where action_id = "INSTITUTION_IMPORT_PROGRAM")
;

delete
from comment_transition_state
where comment_id in (
	select id
	from comment
	where action_id = "INSTITUTION_IMPORT_PROGRAM")
;

delete
from comment
where action_id = "INSTITUTION_IMPORT_PROGRAM"
;

update comment inner join program
	on comment.program_id = program_id
set comment.action_id = "INSTITUTION_CREATE_PROGRAM"
where comment.action_id = "PROGRAM_COMPLETE_APPROVAL_STAGE"
	and program.imported is true
;

