alter table user
	drop index user_id,
	drop index portrait_document_id,
	drop foreign key user_ibfk_1,
	drop foreign key user_ibfk_2
;

alter table user
	add column institution_id int(10) unsigned after email_bounced_message,
	add column position_title varchar(255) after institution_id,
	add index (parent_user_id),
	add foreign key (parent_user_id) references user (id),
	change column portrait_document_id portrait_image_id int(10) unsigned,
	add foreign key (portrait_image_id) references document (id),
	add index (institution_id),
	add foreign key (institution_id) references institution (id)
;

create table user_connection (
	id int(10) unsigned not null auto_increment,
	user_requested_id int(10) unsigned not null,
	user_connected_id int(10) unsigned not null,
	connected int(1) unsigned not null,
	created_timestamp datetime not null,
	primary key (id),
	unique index (user_requested_id, user_connected_id),
	unique index (user_connected_id, user_requested_id),
	foreign key (user_requested_id) references user (id),
	foreign key (user_connected_id) references user (id))
collate = utf8_general_ci
engine = innodb
;
