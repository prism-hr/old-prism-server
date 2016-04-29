alter table advert
	add column published int(1) unsigned after globally_visible
;

update advert
set published = 0
;

alter table advert
	modify column published int(1) unsigned not null
;

update project inner join advert
	on project.advert_id = advert.id
inner join resource_state
	on project.id = resource_state.project_id
set advert.published = 1
where resource_state.state_id = "PROJECT_APPROVED"
;

update program inner join advert
	on program.advert_id = advert.id
inner join resource_state
	on program.id = resource_state.program_id
set advert.published = 1
where resource_state.state_id = "PROGRAM_APPROVED"
;

update department inner join advert
	on department.advert_id = advert.id
inner join resource_state
	on department.id = resource_state.department_id
set advert.published = 1
where resource_state.state_id = "DEPARTMENT_APPROVED"
;

update institution inner join advert
	on institution.advert_id = advert.id
inner join resource_state
	on institution.id = resource_state.institution_id
set advert.published = 1
where resource_state.state_id = "INSTITUTION_APPROVED"
;
