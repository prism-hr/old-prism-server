alter table address_location_part
	drop foreign key address_location_part_ibfk_1,
	drop index root_id,
	drop column root_id
;
