alter table advert_target
	add column accept_advert_id int(10) unsigned not null after target_advert_user_id,
	change column accepting_user_id accept_advert_user_id int(10) unsigned not null,
	drop foreign key advert_target_ibfk_9,
	drop index accepting_user_id,
	add index (accept_advert_user_id),
	add foreign key (accept_advert_user_id) references user (id)
;
