update application inner join (
	select application_id as application_id,
		count(id) as rating_count,
		round(avg(application_rating), 2) as rating_average
	from comment
	where application_rating is not null
	group by application_id) as application_rating
	on application.id = application_rating.application_id
set application.application_rating_count = application_rating.rating_count,
	application.application_rating_average = application_rating.rating_average
;

update project inner join (
	select project_id as project_id,
		sum(application_rating_count) as rating_count,
		round(avg(application_rating_count), 2) as rating_frequency,
		round(avg(application_rating_average), 2) as rating_average
	from application
	where application_rating_average is not null
	group by project_id) as application_rating
	on project.id = application_rating.project_id
set project.application_rating_count = application_rating.rating_count,
	project.application_rating_frequency = application_rating.rating_frequency,
	project.application_rating_average = application_rating.rating_average
;

update program inner join (
	select program_id as program_id,
		sum(application_rating_count) as rating_count,
		round(avg(application_rating_count), 2) as rating_frequency,
		round(avg(application_rating_average), 2) as rating_average
	from application
	where application_rating_average is not null
	group by program_id) as application_rating
	on program.id = application_rating.program_id
set program.application_rating_count = application_rating.rating_count,
	program.application_rating_frequency = application_rating.rating_frequency,
	program.application_rating_average = application_rating.rating_average
;

update institution inner join (
	select institution_id as institution_id,
		sum(application_rating_count) as rating_count,
		round(avg(application_rating_count), 2) as rating_frequency,
		round(avg(application_rating_average), 2) as rating_average
	from application
	where application_rating_average is not null
	group by institution_id) as application_rating
	on institution.id = application_rating.institution_id
set institution.application_rating_count = application_rating.rating_count,
	institution.application_rating_frequency = application_rating.rating_frequency,
	institution.application_rating_average = application_rating.rating_average
;
