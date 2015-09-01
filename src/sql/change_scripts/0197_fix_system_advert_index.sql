alter table system
	drop index advert_id,
	add unique index(advert_id)
;
