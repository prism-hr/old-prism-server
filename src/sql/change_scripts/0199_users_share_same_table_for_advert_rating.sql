alter table advert_target_advert
	change column rating rating_average decimal(3,2),
	add column rating_count int(10) unsigned after selected,
	add index (advert_id, rating_count)
;
