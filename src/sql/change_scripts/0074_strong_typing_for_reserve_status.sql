alter table resource_list_filter_constraint
	add column value_reserve_status varchar(50) after value_state_group_id,
	add unique index (resource_list_filter_id, filter_property, filter_expression, value_reserve_status)
;
