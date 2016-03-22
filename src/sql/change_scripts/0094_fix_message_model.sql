alter table message_thread_participant
	drop index message_thread_id,
	add column start_message_id int(10) unsigned after user_id,
	add column close_message_id int(10) unsigned after start_message_id,
	add unique index (message_thread_id, user_id, start_message_id, close_message_id),
	add index (start_message_id),
	add index (close_message_id),
	add foreign key (start_message_id) references message (id),
	add foreign key (close_message_id) references message (id)
;

delete
from message_document
;

delete
from message_notification
;

delete
from message_thread_participant
;

delete
from message
;

update comment
set message_thread_id = null
;

delete
from message_thread
;

alter table message_thread_participant
	modify column start_message_id int(10) unsigned not null
;

update user_account
set activity_cache = null,
	activity_cached_timestamp = null
;

update application
set activity_cached_timestamp = null
;

update project
set activity_cached_timestamp = null
;

update program
set activity_cached_timestamp = null
;

update department
set activity_cached_timestamp = null
;

update institution
set activity_cached_timestamp = null
;

update system
set activity_cached_timestamp = null
;
