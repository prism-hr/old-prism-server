UPDATE APPLICATION_FORM SET status = 'VALIDATION' where submission_status = 'SUBMITTED' and approval_status is null
;
UPDATE APPLICATION_FORM SET status = 'APPROVED' where approval_status ='APPROVED'
;
UPDATE APPLICATION_FORM SET status = 'REJECTED' where approval_status ='REJECTEE'
;