insert into action
	select concat("APPLICATION_COMPLETE_", replace(state_group.id, "APPLICATION_", ""), "_STAGE"), action.action_type, 
		action.action_category, action.rating_action, action.transition_action, 
		action.declinable_action, action.visible_action, 
		action.action_custom_question_definition_id, action.fallback_action_id, 
		action.scope_id, action.creation_scope_id
	from action inner join state_group
	where action.id = "APPLICATION_COMPLETE_STAGE"
		and state_group.id in ("APPLICATION_VALIDATION", "APPLICATION_VERIFICATION", "APPLICATION_REFERENCE",
			"APPLICATION_REVIEW", "APPLICATION_INTERVIEW", "APPLICATION_APPROVAL", "APPLICATION_APPROVED",
			"APPLICATION_RESERVED", "APPLICATION_REJECTED")
;

update comment
inner join state
	on comment.state_id = state.id
set comment.action_id = concat("APPLICATION_COMPLETE_", replace(state.state_group_id, "APPLICATION_", ""), "_STAGE")
where comment.action_id = "APPLICATION_COMPLETE_STAGE"
;

update user_feedback inner join (
	select user_feedback.id, user_feedback.application_id, 
		max(timeline.created_timestamp) as created_timestamp 
	from user_feedback inner join (
		select comment.application_id as application_id,
			comment.created_timestamp as created_timestamp
			from comment) as timeline
		on user_feedback.application_id = timeline.application_id
			and user_feedback.created_timestamp >= timeline.created_timestamp
	where user_feedback.action_id = "APPLICATION_COMPLETE_STAGE"
	group by user_feedback.id) as complete_state
	on user_feedback.id = complete_state.id
inner join comment
	on complete_state.application_id = user_feedback.application_id
	and complete_state.created_timestamp = comment.created_timestamp
inner join state
	on comment.state_id = state.id
set user_feedback.action_id = concat("APPLICATION_COMPLETE_", replace(state.state_group_id, "APPLICATION_", ""), "_STAGE")
;

update state_action inner join state
	on state_action.state_id = state.id
set state_action.action_id = concat("APPLICATION_COMPLETE_", replace(state.state_group_id, "APPLICATION_", ""), "_STAGE")
where state_action.action_id = "APPLICATION_COMPLETE_STAGE"
;

update state_transition inner join state
	on state_transition.transition_state_id = state.id
set state_transition.transition_action_id = concat("APPLICATION_COMPLETE_", replace(state.state_group_id, "APPLICATION_", ""), "_STAGE")
where state_transition.transition_action_id = "APPLICATION_COMPLETE_STAGE"
;

delete
from action_redaction
where action_id = "APPLICATION_COMPLETE_STAGE"
;

delete
from action
where id = "APPLICATION_COMPLETE_STAGE"
;
