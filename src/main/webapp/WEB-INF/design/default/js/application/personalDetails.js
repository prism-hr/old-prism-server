$(document).ready(function(){
	
	$('#personalDetailsSaveButton').on("click", function(){
		$.post("/pgadmissions/update/editPersonalDetails",
				{ 
					firstName: $("#firstName").val(), 
					lastName: $("#lastName").val(), 
					email: $("#email").val(), 
					id: $("#id").val(), 
					appId: $("#appId").val()
				},
				 function(data) {
				    $('#personalDetailsSection').html(data);
				  });
	});
});