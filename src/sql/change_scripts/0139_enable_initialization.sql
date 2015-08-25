set foreign_key_checks = 0
;

update state_group
set id = replace(id, "_PARTNER_", "_")
;

update state
set state_group_id = replace(state_group_id, "_PARTNER_", "_")
;

set foreign_key_checks = 1
;
