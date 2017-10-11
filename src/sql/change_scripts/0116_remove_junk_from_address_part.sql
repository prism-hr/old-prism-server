delete from address_location
where address_location_part_id in (
	select id
	from address_location_part
	where name regexp '^-?[0-9]+$')
;

delete
from address_location_part
where name regexp '^-?[0-9]+$'
;
