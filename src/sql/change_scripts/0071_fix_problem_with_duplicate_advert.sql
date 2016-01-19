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
