alter table advert_target
	drop index advert_id,
	add unique index (advert_id, advert_user_id, target_advert_id, target_advert_user_id, accept_advert_id, accept_advert_user_id),
	add index (accept_advert_id),
	add foreign key (accept_advert_id) references advert (id)
;
