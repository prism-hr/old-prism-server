INSERT INTO imported_program_mapping (institution_id, imported_program_id, enabled)
  SELECT
    5243,
    p.id,
    1
  FROM imported_program p
  where p.ucas_code is not null;
;
