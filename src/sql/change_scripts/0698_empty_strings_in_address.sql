UPDATE ADDRESS
SET address_code = NULL
WHERE LENGTH(TRIM(address_code)) = 0
;
