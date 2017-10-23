update advert inner join application
    on advert.id = application.advert_id
set application.opportunity_category = advert.opportunity_category
;
