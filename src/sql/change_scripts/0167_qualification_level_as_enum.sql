alter table imported_program
	modify column level varchar(50)
;

update imported_program
set level = "POSTGRADUATE"
where level = "Postgraduate Degree"
;

update imported_program
set level = "UNDERGRADUATE"
where level = "Undergraduate Degree"
;

update imported_program
set level = "HE_LEVEL_2"
where level = "HE Level 2 (HND's DipHEs and Foundation Degrees)"
;

update imported_program
set level = "HE_LEVEL_1"
where level = "HE Level 1 (HNCs and CertHEs)"
;

update imported_program
set level = "HE_LEVEL_0"
where level = "HE Level 0 (Foundation Year)"
;

update imported_program
set level = "OTHER"
where level = "Other"
;
