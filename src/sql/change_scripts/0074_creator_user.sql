alter table user
	add column creator_user_id int(10) unsigned after user_account_id,
	add index (creator_user_id),
	add foreign key (creator_user_id) references user (id)
;
