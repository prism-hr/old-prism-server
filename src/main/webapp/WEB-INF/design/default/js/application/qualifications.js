$(document).ready(function(){
	$('#qualificationsSaveButton').click(function(){
		$.post("/pgadmissions/update/editQualification", { degree: $("#degree").val(), 
			id: $("#id").val(), 
			institution: $("#institution").val(), 
			date_taken: $("#date_taken").val(), 
			grade: $("#grade").val(), 
			appId: $("#appId").val()
		},
		function(data) {
			alert(data);
			$('#qualificationsSection').html(data);
		});
	});