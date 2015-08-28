delete 
from action
where id like "%_APPROVAL_PARTNER_STAGE"
;

update state_group set ordinal = ordinal + 100
;
