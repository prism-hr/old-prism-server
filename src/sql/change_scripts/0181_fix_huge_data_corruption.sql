delete
from comment_state
where comment_id in (
	select comment.id
	from comment inner join (
		select project_id as project_id,
			action_id as action_id, 
			min(id) as comment_id
		from comment
		where state_id like "%_DISABLED_COMPLETED"
		group by project_id, action_id
		having count(id) > 1) as duplicate_comment
		on comment.project_id = duplicate_comment.project_id
			and comment.action_id = duplicate_comment.action_id
	where comment.id != duplicate_comment.comment_id)
;

delete
from comment_transition_state
where comment_id in (
	select comment.id
	from comment inner join (
		select project_id as project_id,
			action_id as action_id, 
			min(id) as comment_id
		from comment
		where state_id like "%_DISABLED_COMPLETED"
		group by project_id, action_id
		having count(id) > 1) as duplicate_comment
		on comment.project_id = duplicate_comment.project_id
			and comment.action_id = duplicate_comment.action_id
	where comment.id != duplicate_comment.comment_id)
;

delete
from comment_assigned_user
where comment_id in (
	select comment.id
	from comment inner join (
		select project_id as project_id,
			action_id as action_id, 
			min(id) as comment_id
		from comment
		where state_id like "%_DISABLED_COMPLETED"
		group by project_id, action_id
		having count(id) > 1) as duplicate_comment
		on comment.project_id = duplicate_comment.project_id
			and comment.action_id = duplicate_comment.action_id
	where comment.id != duplicate_comment.comment_id)
;

delete comment.*
from comment inner join (
	select project_id as project_id,
		action_id as action_id, 
		min(id) as comment_id
	from comment
	where state_id like "%_DISABLED_COMPLETED"
	group by project_id, action_id
	having count(id) > 1) as duplicate_comment
	on comment.project_id = duplicate_comment.project_id
		and comment.action_id = duplicate_comment.action_id
where comment.id != duplicate_comment.comment_id
;

delete
from comment_state
where comment_id in (
	select comment.id
	from comment inner join (
		select program_id as program_id,
			action_id as action_id, 
			min(id) as comment_id
		from comment
		where state_id like "%_DISABLED_COMPLETED"
		group by program_id, action_id
		having count(id) > 1) as duplicate_comment
		on comment.program_id = duplicate_comment.program_id
			and comment.action_id = duplicate_comment.action_id
	where comment.id != duplicate_comment.comment_id)
;

delete
from comment_transition_state
where comment_id in (
	select comment.id
	from comment inner join (
		select program_id as program_id,
			action_id as action_id, 
			min(id) as comment_id
		from comment
		where state_id like "%_DISABLED_COMPLETED"
		group by program_id, action_id
		having count(id) > 1) as duplicate_comment
		on comment.program_id = duplicate_comment.program_id
			and comment.action_id = duplicate_comment.action_id
	where comment.id != duplicate_comment.comment_id)
;

delete
from comment_assigned_user
where comment_id in (
	select comment.id
	from comment inner join (
		select program_id as program_id,
			action_id as action_id, 
			min(id) as comment_id
		from comment
		where state_id like "%_DISABLED_COMPLETED"
		group by program_id, action_id
		having count(id) > 1) as duplicate_comment
		on comment.program_id = duplicate_comment.program_id
			and comment.action_id = duplicate_comment.action_id
	where comment.id != duplicate_comment.comment_id)
;

delete comment.*
from comment inner join (
	select program_id as program_id,
		action_id as action_id, 
		min(id) as comment_id
	from comment
	where state_id like "%_DISABLED_COMPLETED"
	group by program_id, action_id
	having count(id) > 1) as duplicate_comment
	on comment.program_id = duplicate_comment.program_id
		and comment.action_id = duplicate_comment.action_id
where comment.id != duplicate_comment.comment_id
;
