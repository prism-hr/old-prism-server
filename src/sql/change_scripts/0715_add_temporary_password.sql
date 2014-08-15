alter table user_account
add column temporary_password varchar(32),
add column temporary_password_expiry_datetime datetime
;
