alter table resource_state
	add column created_date date,
	add index (state_id, created_date)
;

alter table resource_previous_state
	add column created_date date,
	add index (state_id, created_date)
;

update resource_state inner join (
	select comment.system_id as system_id, 
		comment.institution_id as institution_id, 
		comment.program_id as program_id,
		comment.project_id as project_id, 
		comment.application_id as application_id, 
		comment_transition_state.state_id as state_id,
		max(comment.created_timestamp) as created_timestamp
	from comment inner join comment_transition_state
		on comment.id = comment_transition_state.comment_id
	group by comment.system_id, comment.institution_id, comment.program_id,
		comment.project_id, comment.application_id, 
		comment_transition_state.state_id) as transition
on (resource_state.system_id = transition.system_id
	or resource_state.institution_id = transition.institution_id
	or resource_state.program_id = transition.program_id
	or resource_state.project_id = transition.project_id
	or resource_state.application_id = transition.application_id)
	and resource_state.state_id = transition.state_id
set resource_state.created_date = date(transition.created_timestamp)
;

update resource_state inner join application
	on resource_state.application_id = application.id
set resource_state.created_date = date(application.updated_timestamp)
where resource_state.created_date is null
;

update resource_state inner join project
	on resource_state.project_id = project.id
set resource_state.created_date = date(project.updated_timestamp)
where resource_state.created_date is null
;

update resource_state inner join program
	on resource_state.program_id = program.id
set resource_state.created_date = date(program.updated_timestamp)
where resource_state.created_date is null
;

update resource_state inner join institution
	on resource_state.institution_id = institution.id
set resource_state.created_date = date(institution.updated_timestamp)
where resource_state.created_date is null
;

update resource_state inner join system
	on resource_state.system_id = system.id
set resource_state.created_date = date(system.updated_timestamp)
where resource_state.created_date is null
;

alter table resource_state
	modify column created_date date not null
;

update resource_previous_state inner join (
	select comment.system_id as system_id, 
		comment.institution_id as institution_id, 
		comment.program_id as program_id,
		comment.project_id as project_id, 
		comment.application_id as application_id, 
		comment_state.state_id as state_id,
		max(comment.created_timestamp) as created_timestamp
	from comment inner join comment_state
		on comment.id = comment_state.comment_id
	group by comment.system_id, comment.institution_id, comment.program_id,
		comment.project_id, comment.application_id, comment_state.state_id) as transition
on (resource_previous_state.system_id = transition.system_id
	or resource_previous_state.institution_id = transition.institution_id
	or resource_previous_state.program_id = transition.program_id
	or resource_previous_state.project_id = transition.project_id
	or resource_previous_state.application_id = transition.application_id)
	and resource_previous_state.state_id = transition.state_id
set resource_previous_state.created_date = date(transition.created_timestamp)
;

update resource_previous_state inner join application
	on resource_previous_state.application_id = application.id
set resource_previous_state.created_date = date(application.updated_timestamp)
where resource_previous_state.created_date is null
;

update resource_previous_state inner join project
	on resource_previous_state.project_id = project.id
set resource_previous_state.created_date = date(project.updated_timestamp)
where resource_previous_state.created_date is null
;

update resource_previous_state inner join program
	on resource_previous_state.program_id = program.id
set resource_previous_state.created_date = date(program.updated_timestamp)
where resource_previous_state.created_date is null
;

update resource_previous_state inner join institution
	on resource_previous_state.institution_id = institution.id
set resource_previous_state.created_date = date(institution.updated_timestamp)
where resource_previous_state.created_date is null
;

update resource_previous_state inner join system
	on resource_previous_state.system_id = system.id
set resource_previous_state.created_date = date(system.updated_timestamp)
where resource_previous_state.created_date is null
;

alter table resource_previous_state
	modify column created_date date not null
;

