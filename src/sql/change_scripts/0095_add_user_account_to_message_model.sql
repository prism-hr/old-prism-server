alter table message_thread
	add column comment_id int(10) unsigned,
	add column user_account_id int(10) unsigned,
	add index (comment_id),
	add index (user_account_id),
	add foreign key (comment_id) references comment (id),
	add foreign key (user_account_id) references user_account (id)
;

update comment inner join message_thread
	on comment.message_thread_id = message_thread.id
set message_thread.comment_id = comment.id
;

alter table comment
	drop index message_thread_id,
	drop foreign key comment_ibfk_20,
	drop column message_thread_id
;
