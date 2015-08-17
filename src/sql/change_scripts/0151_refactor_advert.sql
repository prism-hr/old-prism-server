alter table advert
	add column institution_id int(10) unsigned after id,
	add column department_id int(10) unsigned after institution_id,
	add column program_id int(10) unsigned after department_id,
	add column project_id int(10) unsigned after program_id,
	add unique index (institution_id, department_id, program_id, project_id),
	add index (institution_id, sequence_identifier),
	add index (department_id, sequence_identifier),
	add index (program_id, sequence_identifier),
	add index (project_id, sequence_identifier),
	add foreign key (institution_id) references institution (id),
	add foreign key (department_id) references department (id),
	add foreign key (program_id) references program (id),
	add foreign key (project_id) references project (id)
;

update advert inner join institution
	on advert.id = institution.advert_id
set advert.institution_id = institution.id
;

update advert inner join department
	on advert.id = department.advert_id
set advert.department_id = department.id,
	advert.institution_id = department.institution_id
;

update advert inner join program
	on advert.id = program.advert_id
set advert.program_id = program.id,
	advert.department_id = program.department_id,
	advert.institution_id = program.institution_id
;

update advert inner join project
	on advert.id = project.advert_id
set advert.project_id = project.id,
	advert.program_id = project.program_id,
	advert.department_id = project.department_id,
	advert.institution_id = project.institution_id
;

delete
from advert
where institution_id is null
	and department_id is null
	and program_id is null
	and project_id is null
;
