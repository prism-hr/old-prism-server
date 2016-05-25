alter table application_location
    add column description_year int(4) after description,
    add column description_month int(2) after description_year
;

update application_personal_detail
set gender = null
where gender not in ("FEMALE", "MALE")
;

update user_personal_detail
set gender = null
where gender not in ("FEMALE", "MALE")
;
