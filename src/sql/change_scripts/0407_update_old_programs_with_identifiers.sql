UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0007" WHERE 
b.code = "DDNBENSING09" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0007" WHERE 
b.code = "DDNCIVSUSR09" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0007" WHERE 
b.code = "DDNCOMSVEI09" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0012" WHERE 
b.code = "DDNENVSENG01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0010" WHERE 
b.code = "DDNPRFSING01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0004" WHERE 
b.code = "RAFMANSING07" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0005" WHERE 
b.code = "RRDBENSING01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0005" WHERE 
b.code = "RRDCIVSGEO01" AND a.identifier IS NULL
;
	
UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0012" WHERE 
b.code = "RRDCOMSFNC01" AND a.identifier IS NULL
;
	
UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0012" WHERE 
b.code = "RRDCOMSING01" AND a.identifier IS NULL
;
	
UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0012" WHERE 
b.code = "RRDEENSING01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0012" WHERE 
b.code = "RRDEENSPHT01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0012" WHERE 
b.code = "RRDMANSING01" AND a.identifier IS NULL
;
	 
UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0005" WHERE 
b.code = "RRDMBISING01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0012" WHERE 
b.code = "RRDMECSING01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0010" WHERE 
b.code = "RRDMPHSING01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0007" WHERE 
b.code = "RRDSECSING01" AND a.identifier IS NULL
;
	
UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0001" WHERE 
b.code = "TMRBENSING01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0007" WHERE 
b.code = "TMRCIVSUSR01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0004" WHERE 
b.code = "TMRCOMSFNC01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0006" WHERE 
b.code = "TMRCOMSVEI01" AND a.identifier IS NULL
;	
		
UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0007" WHERE 
b.code = "TMRCOMSWEB01" AND a.identifier IS NULL
;
	
UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0004" WHERE 
b.code = "TMREENSPHT01" AND a.identifier IS NULL
;
	
UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0005" WHERE 
b.code = "TMRHEAAWLB01" AND a.identifier IS NULL 
AND a.study_code = '1';
;	

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0003" WHERE 
b.code = "TMRHEAAWLB01" AND a.identifier IS NULL
AND a.study_code = '31';
;	

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0010" WHERE 
b.code = "TMRMBISING01" AND a.identifier IS NULL 
AND a.study_code = '1';
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0007" WHERE 
b.code = "TMRMPHSING01" AND a.identifier IS NULL
AND a.study_code = '31';
;	

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0009" WHERE 
b.code = "TMRMPHSING01" AND a.identifier IS NULL 
AND a.study_code = '1';
;	

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0015" WHERE 
b.code = "TMRSECSING01" AND a.identifier IS NULL
AND a.study_code = '31';
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0013" WHERE 
b.code = "TMRMSISING01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0013" WHERE 
b.code = "TMRSECSING01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE a 
INNER JOIN PROGRAM b ON a.program_id = b.id 
SET a.identifier = "0015" WHERE 
b.code = "TMRTELSING01" AND a.identifier IS NULL
;

UPDATE PROGRAM_INSTANCE 
SET study_option = 'Modular/flexible study', study_code = 'B+++++'
WHERE study_code = '31'
;

UPDATE PROGRAM_INSTANCE 
SET study_option = 'Full-time', study_code = 'F+++++'
WHERE study_code = '1'
;

