alter table comment
	drop column sponsorship_confirmed,
	add column sponsorship_rejection_id int(10) unsigned after sponsorship_target_fulfilled,
	add index (sponsorship_rejection_id),
	add foreign key (sponsorship_rejection_id) references comment (id)
;
