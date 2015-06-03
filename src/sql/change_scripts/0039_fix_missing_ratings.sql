update comment inner join (
	select comment_custom_response.comment_id as comment_id,
		round(sum(comment_custom_response.property_value * (5 / 8) * action_custom_question_configuration.display_weighting), 2) as rating
	from comment_custom_response inner join action_custom_question_configuration
		on comment_custom_response.action_custom_question_configuration_id = action_custom_question_configuration.id
	where action_custom_question_configuration.custom_question_type = "RATING_WEIGHTED"
	group by comment_custom_response.comment_id) as comment_rating
	on comment.id = comment_rating.comment_id
inner join application
	on comment.application_id = application.id
set comment.application_rating = comment_rating.rating
where comment.application_rating is null
	and application.program_id = 93
;

update application inner join (
	select application_id as application_id,
		count(id) as rating_count,
		round(avg(application_rating), 2) as rating_average
	from comment
	where application_rating is not null
	group by application_id) as application_summary
	on application.id = application_summary.application_id
set application.application_rating_count = application_summary.rating_count,
	application.application_rating_average = application_summary.rating_average
where program_id = 93
;

update project inner join (
	select project_id as project_id,
		sum(application_rating_count) as rating_count,
		round(avg(application_rating_average), 2) as rating_average
	from application
	where application_rating_average is not null
		and project_id is not null
	group by project_id) as project_summary
	on project.id = project_summary.project_id
set project.application_rating_count = project_summary.rating_count,
	project.application_rating_average = project_summary.rating_average
where program_id = 93
;

update program inner join (
	select program_id as program_id,
		sum(application_rating_count) as rating_count,
		round(avg(application_rating_average), 2) as rating_average
	from application
	where application_rating_average is not null
		and program_id is not null
	group by program_id) as program_summary
	on program.id = program_summary.program_id
set program.application_rating_count = program_summary.rating_count,
	program.application_rating_average = program_summary.rating_average
where id = 93
;

update institution inner join (
	select institution_id as institution_id,
		sum(application_rating_count) as rating_count,
		round(avg(application_rating_average), 2) as rating_average
	from application
	where application_rating_average is not null
		and institution_id is not null
	group by institution_id) as institution_summary
	on institution.id = institution_summary.institution_id
set institution.application_rating_count = institution_summary.rating_count,
	institution.application_rating_average = institution_summary.rating_average
where id = 6856
;

update project inner join (
	select project_id as project_id,
		round(avg(application_rating_count), 2) as rating_count_average
	from application
	where application_rating_count is not null
		and project_id is not null
	group by project_id) as project_summary
	on project.id = project_summary.project_id
set project.application_rating_count_average_non_zero = project_summary.rating_count_average
where program_id = 93
;

update program inner join (
	select program_id as program_id,
		round(avg(application_rating_count), 2) as rating_count_average
	from application
	where application_rating_count is not null
		and program_id is not null
	group by program_id) as program_summary
	on program.id = program_summary.program_id
set program.application_rating_count_average_non_zero = program_summary.rating_count_average
where id = 93
;

update institution inner join (
	select institution_id as institution_id,
		round(avg(application_rating_count), 2) as rating_count_average
	from application
	where application_rating_count is not null
		and institution_id is not null
	group by institution_id) as institution_summary
	on institution.id = institution_summary.institution_id
set institution.application_rating_count_average_non_zero = institution_summary.rating_count_average
where id = 6856
;
