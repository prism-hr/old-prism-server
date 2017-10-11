alter table advert
	add column system_advert_id int(10) unsigned after system_id,
	add index (system_advert_id),
	add foreign key (system_advert_id) references advert (id),
	add column institution_advert_id int(10) unsigned after institution_id,
	add index (institution_advert_id),
	add foreign key (institution_advert_id) references advert (id),
	add column department_advert_id int(10) unsigned after department_id,
	add index (department_advert_id),
	add foreign key (department_advert_id) references advert (id),
	add column program_advert_id int(10) unsigned after program_id,
	add index (program_advert_id),
	add foreign key (program_advert_id) references advert (id),
	add column project_advert_id int(10) unsigned after project_id,
	add index (project_advert_id),
	add foreign key (project_advert_id) references advert (id)
;

update advert inner join system
	on advert.system_id = system.id
set advert.system_advert_id = system.advert_id
;

update advert inner join institution
	on advert.institution_id = institution.id
set advert.institution_advert_id = institution.advert_id
;

update advert inner join department
	on advert.department_id = department.id
set advert.department_advert_id = department.advert_id
;

update advert inner join program
	on advert.program_id = program.id
set advert.program_advert_id = program.advert_id
;

update advert inner join project
	on advert.project_id = project.id
set advert.project_advert_id = project.advert_id
;

alter table advert
	drop foreign key advert_ibfk_14,
	drop column system_advert_id,
	drop index project_advert_id,
	add index (project_advert_id, sequence_identifier),
	drop index program_advert_id,
	add index (program_advert_id, sequence_identifier),
	drop index department_advert_id,
	add index (department_advert_id, sequence_identifier),
	drop index institution_advert_id,
	add index (institution_advert_id, sequence_identifier)
;
