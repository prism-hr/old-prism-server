update resource_condition
set internal_mode = false
where institution_id is not null
	or department_id is not null
;

delete
from resource_condition
where internal_mode is false
	and external_mode is false
;
