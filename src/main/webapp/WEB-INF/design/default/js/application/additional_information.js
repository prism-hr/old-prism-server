$(document).ready(function(){
	$('#informationSaveButton').click(function(){
		$.post("/pgadmissions/update/addAdditionalInformation", { 
			additionalInformation: $("#additionalInformation").val(),
			id: $("#id").val(), 
			appId: $("#appId").val()
		},
		
		function(data) {
			$('#additionalInformationSection').html(data);
		});
	});
	
	$('a[name="informationCancelButton"]').click(function(){
		$("#additionalInformation").val("");
	});
	
});