UPDATE APPLICATION_FORM_QUALIFICATION q, QUALIFICATION_TYPE qt
SET q.award_date = 
	(CASE 
		WHEN qt.name LIKE 'Bachelors%' THEN '2013-07-01'
		WHEN qt.name LIKE 'MEng%' THEN '2013-07-01'
		WHEN qt.name LIKE 'MPhil%' THEN '2013-09-02'
		WHEN qt.name LIKE 'MSci%' THEN '2013-07-01'
		WHEN qt.name LIKE 'Masters%' THEN '2013-09-02'
		WHEN qt.name LIKE 'Non-Honours%' THEN '2013-07-01'
		WHEN qt.name LIKE 'Other%' THEN '2013-07-01'
		WHEN qt.name LIKE 'PhD%' THEN '2013-09-02'
		ELSE NULL
	END)
WHERE q.qualification_type_id = qt.id AND q.award_date IS NULL
;

ALTER TABLE APPLICATION_FORM_QUALIFICATION MODIFY award_date DATE NOT NULL
;