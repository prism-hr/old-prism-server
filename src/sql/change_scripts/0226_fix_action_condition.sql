alter table resource_condition
	drop index system_id_2,
	drop index institution_id_2,
	drop index program_id_2,
	drop index project_id_2,
	drop index application_id_2,
	add column internal_mode int(1) unsigned not null default 1 after action_condition,
	add index (system_id, internal_mode),
	add index (institution_id, internal_mode),
	add index (department_id, internal_mode),
	add index (program_id, internal_mode),
	add index (project_id, internal_mode),
	add index (application_id, internal_mode),
	add index (resume_id, internal_mode),
	change column partner_mode external_mode int(1) unsigned not null,
	add index (system_id, external_mode),
	add index (institution_id, external_mode),
	add index (department_id, external_mode),
	add index (program_id, external_mode),
	add index (project_id, external_mode),
	add index (application_id, external_mode),
	add index (resume_id, external_mode)
;

update resource_condition
set internal_mode = 0 
where action_condition = "ACCEPT_APPLICATION"
;

alter table resource_condition
	modify column internal_mode int(10) unsigned
;

alter table state_action_assignment
	change column partner_mode external_mode int(1) unsigned not null
;
