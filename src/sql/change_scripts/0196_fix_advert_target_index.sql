alter table advert_target_advert
	drop index advert_id,
	drop index advert_id_2,
	add unique index (advert_id, target_advert_id)
;
