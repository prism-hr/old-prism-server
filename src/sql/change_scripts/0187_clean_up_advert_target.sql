create table advert_resource (
	id int(10) unsigned not null auto_increment,
	advert_id int(10) unsigned not null,
	institution_id int(10) unsigned,
	department_id int(10) unsigned,
	primary key (id),
	unique index (advert_id, institution_id, department_id),
	index (institution_id), 
	index (department_id),
	foreign key (advert_id) references advert (id),
	foreign key (institution_id) references institution (id),
	foreign key (department_id) references department (id))
collate = utf8_general_ci
engine = innodb
;

create table advert_resource_selected (
	id int(10) unsigned not null auto_increment,
	advert_id int(10) unsigned not null,
	institution_id int(10) unsigned,
	department_id int(10) unsigned,
	endorsed int(1) unsigned not null,
	primary key (id),
	unique index (advert_id, institution_id, department_id),
	index (institution_id), 
	index (department_id),
	index (advert_id, endorsed),
	foreign key (advert_id) references advert (id),
	foreign key (institution_id) references institution (id),
	foreign key (department_id) references department (id))
collate = utf8_general_ci
engine = innodb
;

insert into advert_resource (advert_id, institution_id)
select advert_id, institution_id
from advert_institution
;

insert into advert_resource (advert_id, department_id)
select advert_id, department_id
from advert_department
;

insert into advert_resource_selected (advert_id, institution_id, endorsed)
select advert_id, institution_id, false
from advert_institution
;

insert into advert_resource_selected (advert_id, department_id, endorsed)
select advert_id, department_id, false
from advert_department
;

drop table advert_institution
;

drop table advert_department
;

alter table imported_entity_type
	modify column last_imported_timestamp timestamp null
;
