alter table user_account
	add column activity_cache text after shared,
	add column activity_cached_timestamp datetime after activity_cache,
	add index (activity_cached_timestamp)
;

alter table application
	add column activity_cached_timestamp datetime after updated_timestamp,
	add index (activity_cached_timestamp)
;

alter table project
	add column activity_cached_timestamp datetime after updated_timestamp,
	add index (activity_cached_timestamp)
;

alter table program
	add column activity_cached_timestamp datetime after updated_timestamp,
	add index (activity_cached_timestamp)
;

alter table department
	add column activity_cached_timestamp datetime after updated_timestamp,
	add index (activity_cached_timestamp)
;

alter table institution
	add column activity_cached_timestamp datetime after updated_timestamp,
	add index (activity_cached_timestamp)
;

alter table system
	add column activity_cached_timestamp datetime after updated_timestamp,
	add index (activity_cached_timestamp)
;
