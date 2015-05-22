create table imported_age_range (
	id int(10) unsigned not null auto_increment,
	institution_id int(10) unsigned not null,
	code varchar(50) not null,
	name text not null,
	lower_bound int(3) unsigned not null,
	upper_bound int(3) unsigned,
	enabled int(1) unsigned not null,
	primary key (id),
	unique index (institution_id, code),
	index (institution_id, enabled),
	foreign key (institution_id) references institution (id))
collate = utf8_general_ci
engine = innodb
;
