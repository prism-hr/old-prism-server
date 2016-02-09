create table comment_thread (
	id int(10) unsigned not null auto_increment,
	subject varchar(255) not null,
	created_timestamp datetime not null,
	primary key (id),
	index (subject),
	index (created_timestamp))
collate = utf8_general_ci
engine = innodb
;

alter table comment
	modify column declined_response int(1) unsigned not null after content,
	add column comment_thread_id int(10) unsigned after action_id,
	add column submitted_timestamp datetime after created_timestamp,
	add index (comment_thread_id),
	add foreign key (comment_thread_id) references comment_thread (id)
;

update comment
set submitted_timestamp = created_timestamp
;
