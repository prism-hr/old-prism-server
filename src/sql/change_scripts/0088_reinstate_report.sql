alter table project
	add column application_rating_count int(10) unsigned after duration_maximum,
	add column application_rating_frequency decimal(10, 2) unsigned after application_rating_count,
	add column application_rating_average decimal(3, 2) unsigned after application_rating_frequency,
	add index (application_rating_count, sequence_identifier),
	add index (application_rating_frequency, sequence_identifier),
	add index (application_rating_average, sequence_identifier)
;

update project inner join (	
	select project_id as project_id,
		sum(application_rating_count) as rating_count,
		round(sum(application_rating_count) / count(id), 2) as rating_frequency,
		round(avg(application_rating_average), 2) as rating_average
	from application
	where project_id is not null 
		and application_rating_count is not null
	group by project_id) as project_summary
	on project.id = project_summary.project_id
set project.application_rating_count = project_summary.rating_count,
	project.application_rating_frequency = project_summary.rating_frequency,
	project.application_rating_average = project_summary.rating_average
;

alter table program
	add column application_rating_count int(10) unsigned after imported,
	add column application_rating_frequency decimal(10, 2) unsigned after application_rating_count,
	add column application_rating_average decimal(3, 2) unsigned after application_rating_frequency,
	add index (application_rating_count, sequence_identifier),
	add index (application_rating_frequency, sequence_identifier),
	add index (application_rating_average, sequence_identifier)
;

update program inner join (	
	select program_id as program_id,
		sum(application_rating_count) as rating_count,
		round(sum(application_rating_count) / count(id), 2) as rating_frequency,
		round(avg(application_rating_average), 2) as rating_average
	from application
	where program_id is not null 
		and application_rating_count is not null
	group by program_id) as program_summary
	on program.id = program_summary.program_id
set program.application_rating_count = program_summary.rating_count,
	program.application_rating_frequency = program_summary.rating_frequency,
	program.application_rating_average = program_summary.rating_average
;

alter table institution
	add column application_rating_count int(10) unsigned after ucl_institution,
	add column application_rating_frequency decimal(10, 2) unsigned after application_rating_count,
	add column application_rating_average decimal(3, 2) unsigned after application_rating_frequency,
	add index (application_rating_count, sequence_identifier),
	add index (application_rating_frequency, sequence_identifier),
	add index (application_rating_average, sequence_identifier)
;

update institution inner join (	
	select institution_id as institution_id,
		sum(application_rating_count) as rating_count,
		round(sum(application_rating_count) / count(id), 2) as rating_frequency,
		round(avg(application_rating_average), 2) as rating_average
	from application
	where institution_id is not null 
		and application_rating_count is not null
	group by institution_id) as institution_summary
	on institution.id = institution_summary.institution_id
set institution.application_rating_count = institution_summary.rating_count,
	institution.application_rating_frequency = institution_summary.rating_frequency,
	institution.application_rating_average = institution_summary.rating_average
;

