alter table advert
	add column submitted int(1) unsigned not null default 1 after globally_visible,
	add index (submitted)
;

alter table advert
	modify column submitted int(1) unsigned not null
;

update resource_state inner join project
	on resource_state.project_id = project.id
inner join advert 
	on project.advert_id = advert.id
set advert.submitted = 0
	where resource_state.state_id like "%_UNSUBMITTED"
;

update resource_state inner join program
	on resource_state.program_id = program.id
inner join advert 
	on program.advert_id = advert.id
set advert.submitted = 0
	where resource_state.state_id like "%_UNSUBMITTED"
;

update resource_state inner join department
	on resource_state.department_id = department.id
inner join advert 
	on department.advert_id = advert.id
set advert.submitted = 0
	where resource_state.state_id like "%_UNSUBMITTED"
;

update resource_state inner join institution
	on resource_state.institution_id = institution.id
inner join advert 
	on institution.advert_id = advert.id
set advert.submitted = 0
	where resource_state.state_id like "%_UNSUBMITTED"
;
