create table department_imported_subject_area (
	id int(10) unsigned not null auto_increment,
	department_id int(10) unsigned not null,
	imported_subject_area_id int(10) unsigned not null,
	relation_strength decimal(20,10) unsigned not null,
	primary key (id),
	unique index (department_id, imported_subject_area_id),
	index (imported_subject_area_id),
	index (department_id, relation_strength),
	foreign key (department_id) references department (id),
	foreign key (imported_subject_area_id) references imported_subject_area (id))
collate = utf8_general_ci
engine = innodb
;
