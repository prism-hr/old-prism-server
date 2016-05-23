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

rename table advert_Location to advert_location_old
;

create table advert_location (
	id int(10) unsigned not null auto_increment,
	advert_id int(10) unsigned,
	institution_id int(10) unsigned,
	department_id int(10) unsigned,
	program_id int(10) unsigned,
	project_id int(10) unsigned,
	address_id int(10) unsigned not null,
	primary key (id),
	unique index (advert_id, institution_id, department_id, program_id, project_id, address_id),
	index (institution_id),
	index (department_id),
	index (program_id),
	index (project_id),
	index (address_id),
	foreign key (advert_id) references advert (id),
	foreign key (institution_id) references institution (id),
	foreign key (department_id) references department (id),
	foreign key (program_id) references program (id),
	foreign key (project_id) references project (id),
	foreign key (address_id) references address (id))
collate = utf8_general_ci
engine = innodb
;

delete advert_location_old.*
from advert_location_old inner join advert as location_advert
	on advert_location_old.location_advert_id = location_advert.id
where location_advert.scope_id not in ("INSTITUTION", "DEPARTMENT")
;

insert into advert_location (advert_id, institution_id, department_id, program_id, project_id, address_id)
	select advert_location_old.advert_id, location_advert.institution_id, location_advert.department_id,
		location_advert.program_id, location_advert.project_id, location_advert.address_id
	from advert_location_old inner join advert as location_advert
		on advert_location_old.location_advert_id = location_advert.id
;

insert into advert_location (advert_id, institution_id, department_id, address_id)
	select id, institution_id, department_id, address_id
	from advert
	where scope_id in ("INSTITUTION", "DEPARTMENT")
;

drop table advert_location_old
;

alter table advert_location
	drop index advert_id,
	drop foreign key advert_location_ibfk_4,
	drop foreign key advert_location_ibfk_5,
	drop column program_id,
	drop column project_id,
	add unique index (advert_id, institution_id, department_id, address_id)
;

alter table advert_location
	modify column institution_id int(10) unsigned not null,
	modify column address_id int(10) unsigned not null
;

alter table advert_location
	add column location_advert_id int(10) unsigned after advert_id,
	add index (location_advert_id),
	add foreign key (location_advert_id) references advert (id)
;

update advert_location inner join department
	on advert_location.department_id = department.id
set advert_location.location_advert_id = department.advert_id
;

update advert_location inner join institution
	on advert_location.institution_id = institution.id
set advert_location.location_advert_id = institution.advert_id
where advert_location.location_advert_id is null
;

alter table advert_location
	drop foreign key advert_location_ibfk_2,
	drop foreign key advert_location_ibfk_3,
	drop index advert_id,
	add unique index (advert_id, location_advert_id),
	modify column location_advert_id int(10) unsigned not null
;

