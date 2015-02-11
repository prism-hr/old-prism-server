create table department (
	id int(10) unsigned not null auto_increment,
	institution_id int(10) unsigned not null,
	title varchar(255) not null,
	primary key (id),
	unique index (institution_id, title),
	foreign key (institution_id) references institution (id)
) engine = innodb
;

alter table program
	add column department_id int(10) unsigned after institution_id,
	add index (department_id, sequence_identifier),
	add foreign key (department_id) references department (id)
;
