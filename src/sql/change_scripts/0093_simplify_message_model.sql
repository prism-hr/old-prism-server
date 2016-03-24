create table message_thread_participant (
	id int(10) unsigned not null auto_increment,
	message_thread_id int(10) unsigned not null,
	user_id int(10) unsigned not null,
	assigned_timestamp datetime not null,
	last_viewed_timestamp datetime,
	primary key (id),
	unique index (message_thread_id, user_id),
	index (user_id),
	index (message_thread_id, assigned_timestamp),
	index (message_thread_id, last_viewed_timestamp),
	foreign key (message_thread_id) references message_thread (id),
	foreign key (user_id) references user (id))
collate = utf8_general_ci
engine = innodb
;

alter table message_recipient
	drop index message_id,
	add unique index (message_id, user_id),
	drop foreign key message_recipient_ibfk_2,
	drop index role_id,
	drop column role_id,
	modify column user_id int(10) unsigned not null,
	drop column view_timestamp,
	drop index send_timestamp,
	add index (message_id, send_timestamp)
;

rename table message_recipient to message_notification
;

alter table message_thread_participant
	drop index message_thread_id_3,
	drop column last_viewed_timestamp,
	add column last_viewed_message_id int(10) unsigned,
	add index (message_thread_id, last_viewed_message_id),
	add index (last_viewed_message_id),
	add foreign key (last_viewed_message_id) references message (id)
;

alter table message_thread_participant
	drop index message_thread_id_2,
	drop column assigned_timestamp
;

alter table message_notification
	drop column send_timestamp
;
