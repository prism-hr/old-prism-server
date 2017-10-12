alter table advert_competence
  drop column fulfil
;

alter table comment_competence
  add column fulfil int(1) unsigned after importance
;
