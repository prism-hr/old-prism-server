alter table user
    add column last_logged_in_timestamp datetime after parent_user_id,
    add index (last_logged_in_timestamp)
;

alter table user_notification
    change last_notified_date last_notified_timestamp datetime not null
;

alter table user_account
    change column send_application_recommendation_notification send_activity_notification int(1) unsigned not null
;

update user_account
set send_activity_notification = true
;
