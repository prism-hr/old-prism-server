INNER JOIN ADVERT
    ON ADVERT.id = PROGRAM.id
INNER JOIN USER
    ON ADVERT.user_id = USER.id
INNER JOIN INSTITUTION
    ON PROGRAM.institution_id = INSTITUTION.id
