set foreign_key_checks = 0
;

alter table program
	modify column user_id int(10) unsigned not null
;

alter table project
	modify column user_id int(10) unsigned not null,
	modify column advert_id int(10) unsigned after program_id,
	add column department_id int(10) unsigned after institution_id,
	add index (department_id, sequence_identifier),
	add foreign key (department_id) references department (id)
;

set foreign_key_checks = 1
;

update project inner join program
	on project.program_id = program.id
set project.department_id = program.department_id
;
