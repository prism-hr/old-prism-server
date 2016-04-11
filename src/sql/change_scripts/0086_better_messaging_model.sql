rename table comment_thread to message_thread
;

alter table comment
drop index comment_thread_id,
add index message_thread_id (comment_thread_id)
;

alter table comment
change column comment_thread_id message_thread_id INT(10) unsigned after action_id
;

create table message (
	id int(10) unsigned not null auto_increment,
	user_id int(10) unsigned not null,
	message_thread_id int(10) unsigned not null,
	content text not null,
	created_timestamp datetime not null,
	primary key (id),
	index (user_id),
	index (message_thread_id),
	foreign key (user_id) references user (id),
	foreign key (message_thread_id) references message_thread (id))
collate = utf8_general_ci,
engine = innodb
;

create table message_document (
	id int(10) unsigned not null auto_increment,
	message_id int(10) unsigned not null,
	document_id int(10) unsigned not null,
	primary key (id),
	index (message_id, document_id),
	unique index (document_id),
	foreign key (message_id) references message (id),
	foreign key (document_id) references document (id))
collate = utf8_general_ci
engine = innodb
;

create table state_action_recipient (
	id int(10) unsigned not null auto_increment,
	state_action_assignment_id int(10) unsigned not null,
	role_id varchar(50) not null,
	primary key (id),
	unique index (state_action_assignment_id, role_id),
	index (role_id),
	foreign key (state_action_assignment_id) references state_action_assignment (id),
	foreign key (role_id) references role (id))
collate = utf8_general_ci
engine = innodb
;

create table message_recipient (
	id int(10) unsigned not null auto_increment,
	message_id int(10) unsigned not null,
	role_id varchar(50) not null,
	user_id int(10) unsigned,
	primary key (id),
	unique index (message_id, role_id, user_id),
	index (role_id),
	index (user_id),
	foreign key (message_id) references message (id),
	foreign key (role_id) references role (id),
	foreign key (user_id) references user (id))
collate = utf8_general_ci
engine = innodb
;

alter table message_recipient
	add column viewed int(1) unsigned not null,
	add index (viewed)
;
