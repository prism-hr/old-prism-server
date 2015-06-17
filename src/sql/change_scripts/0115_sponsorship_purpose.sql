alter table advert
	add column sponsorship_purpose text after institution_address_id,
	modify column sponsorship_target decimal(10, 2) unsigned after sponsorship_purpose,
	modify column sponsorship_secured decimal(10, 2) unsigned after sponsorship_target
;
