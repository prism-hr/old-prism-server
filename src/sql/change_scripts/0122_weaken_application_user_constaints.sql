set foreign_key_checks = 0
;

alter table application_qualification
  drop index application_id,
  add unique index (application_id, advert_id, start_year, start_month)
;

alter table application_employment_position
  drop index application_id,
  add unique index (application_id, advert_id, start_year, start_month)
;

alter table user_qualification
  drop index user_id,
  add unique index (user_id, advert_id, start_year, start_month)
;

alter table user_employment_position
  drop index user_id,
  add unique index (user_id, advert_id, start_year, start_month)
;

alter table user_referee
  drop index user_account_id,
  add unique index (user_account_id, user_id)
;

set foreign_key_checks = 1
;
