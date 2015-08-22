alter table system
	add column advert_id int(10) unsigned after user_id,
	add index (advert_id),
	add foreign key (advert_id) references system (id)
;

alter table advert
	add column system_id int(10) unsigned after id,
	add index (system_id, sequence_identifier),
	add foreign key (system_id) references system (id)
;

update advert
set system_id = 1
;
