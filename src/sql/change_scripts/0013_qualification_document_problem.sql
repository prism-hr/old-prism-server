update application_qualification inner join document
	on application_qualification.document_id = document.id
	set application_qualification.document_id = null
where document.file_name = "Amjad AlQahtani UCL 2014.pdf"
;
