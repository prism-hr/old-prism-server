alter table advert
    add column pay_benefit text after pay_maximum_normalized_hour,
    add column pay_benefit_description text after pay_benefit,
    change column last_pay_conversion_date pay_last_conversion_date date
;
