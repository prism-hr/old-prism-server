alter table user_qualification
	drop foreign key user_qualification_ibfk_1
;

alter table user_qualification
	change column user_id user_account_id int(10) unsigned not null,
	add index (user_account_id),
	add foreign key (user_account_id) references user_account (id)
;

alter table user_account
	modify column sequence_identifier varchar(23)
;
