alter table advert
	add column scope_id varchar(50) after project_id,
	add index (scope_id),
	add foreign key (scope_id) references scope (id)
;

update advert
set scope_id = "SYSTEM"
where system_id is not null
;

update advert
set scope_id = "INSTITUTION"
where institution_id is not null
;

update advert
set scope_id = "DEPARTMENT"
where department_id is not null
;

update advert
set scope_id = "PROGRAM"
where program_id is not null
;

update advert
set scope_id = "PROJECT"
where project_id is not null
;

alter table advert
	modify column scope_id varchar(50) not null
;

alter table advert
	change column target_opportunity_type study_option text
;

update project inner join advert
	on project.advert_id = advert.id
inner join (
	select project_id as project_id,
		group_concat(study_option separator "|") as study_option
	from resource_study_option
	where project_id is not null
	group by project_id) as project_study_option
	on project.id = project_study_option.project_id
set advert.study_option = project_study_option.study_option
;

update program inner join advert
	on program.advert_id = advert.id
inner join (
	select program_id as program_id,
		group_concat(study_option separator "|") as study_option
	from resource_study_option
	where program_id is not null
	group by program_id) as program_study_option
	on program.id = program_study_option.program_id
set advert.study_option = program_study_option.study_option
;

alter table advert
	add column duration_minimum int(3) unsigned after address_id,
	add column duration_maximum int(3) unsigned after duration_minimum,
	add index (duration_minimum),
	add index (duration_maximum)
;

update project inner join advert
	on project.advert_id = advert.id
set advert.duration_minimum = project.duration_minimum,
	advert.duration_maximum = project.duration_maximum
;

update program inner join advert
	on program.advert_id = advert.id
set advert.duration_minimum = program.duration_minimum,
	advert.duration_maximum = program.duration_maximum
;

alter table project
	drop column duration_minimum,
	drop column duration_maximum
;

alter table program
	drop column duration_minimum,
	drop column duration_maximum
;
