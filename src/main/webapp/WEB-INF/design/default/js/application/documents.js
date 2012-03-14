$(document).ready(function(){

	
	$('#primaryNationalityUploadButton').on("click", function(){
		$('#documentUploadForm').attr("action", "/pgadmissions/documents");
		$('#documentUploadForm').submit();
	});

	
});
