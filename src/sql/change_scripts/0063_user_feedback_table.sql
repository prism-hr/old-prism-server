create table user_feedback (
	id int(10) unsigned not null,
	user_id int(10) unsigned not null,
	role_category varchar(50) not null,
	institution_id int(10) unsigned not null,
	declined_response int(1) unsigned not null,
	rating int(1) unsigned,
	content TEXT,
	recommended int(1),
	created_timestamp datetime not null,
	sequence_identifier varchar(23),
	primary key (id),
	index (user_id),
	index (institution_id),
	index (declined_response, sequence_identifier),
	foreign key (user_id) references user (id),
	foreign key (institution_id) references institution (id)
) engine = innodb
;

alter table role
	add column role_category varchar(50) not null default "APPLICANT" after id,
	add index (role_category)
;

alter table role
	modify column role_category varchar(50) not null
;
