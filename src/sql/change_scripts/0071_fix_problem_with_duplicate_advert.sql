alter table advert
    change column advert_address_id address_id int(10) unsigned
;

delete advert_location.*
from advert_location inner join advert
    on advert_location.advert_id = advert.id
where advert.institution_id is null
    and advert.department_id is null
    and advert.program_id is null
    and advert.project_id is null
;

set session foreign_key_checks = 0
;

delete address.*
from advert inner join address
    on advert.address_id = address.id
where advert.institution_id is null
    and advert.department_id is null
    and advert.program_id is null
    and advert.project_id is null
;

update advert
set address_id = null
where institution_id is null
    and department_id is null
    and program_id is null
    and project_id is null
;

set session foreign_key_checks = 1
;

delete
from advert
where institution_id is null
    and department_id is null
    and program_id is null
    and project_id is null
;

delete address_location.*
from address_location left join address
    on address_location.address_id = address.id
where address.id is null
;

delete address_location_part.*
from address_location_part left join address_location
    on address_location_part.id = address_location.address_location_part_id
where address_location.address_location_part_id is null
;

alter table address_location
    drop primary key,
    add column id int(10) unsigned not null auto_increment,
    add primary key (id),
    add unique index (address_id, address_location_part_id)
;

alter table address_location
    modify column id int(10) unsigned not null auto_increment first
;
