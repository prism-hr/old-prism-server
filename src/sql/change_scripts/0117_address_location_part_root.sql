alter table address_location_part
	add column root_id int(10) unsigned after id,
	add index (root_id),
	add foreign key (root_id) references address_location_part (id),
	add foreign key (parent_id) references address_location_part (id)
;
