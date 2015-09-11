create table user_advert (
	id int(10) unsigned not null auto_increment,
	user_id int(10) unsigned not null,
	advert_id int(10) unsigned not null,
	primary key (id),
	unique index (user_id, advert_id),
	index (advert_id),
	foreign key (user_id) references user (id),
	foreign key (advert_id) references advert (id))
collate = utf8_general_ci
	engine = innodb
;

insert into user_advert (user_id, advert_id)
	select user_program.user_id, department.advert_id
	from user_program inner join department_imported_program
		on user_program.imported_program_id = department_imported_program.imported_program_id
	inner join department
		on department_imported_program.department_id = department.id
	group by user_program.user_id, department.advert_id
;

insert into user_advert (user_id, advert_id)
	select user_program.user_id, institution.advert_id
	from user_program inner join department_imported_program
		on user_program.imported_program_id = department_imported_program.imported_program_id
	inner join department
		on department_imported_program.department_id = department.id
	inner join institution
		on department.institution_id = institution.id
	group by user_program.user_id, institution.advert_id
;
