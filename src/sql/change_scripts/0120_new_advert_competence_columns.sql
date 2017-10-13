alter table advert_competence
  add column mode varchar(50) after importance
;

alter table advert_competence
  add column fulfil int(1) unsigned after importance
;

update advert_competence
  set mode = 'RATING'
;

alter table advert_competence
  modify column mode varchar(50) not null
;
