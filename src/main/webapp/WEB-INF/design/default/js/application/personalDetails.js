$(document).ready(function(){
	
	$('#personalDetailsSaveButton').on("click", function(){
		$.post("/pgadmissions/apply/editPersonalDetails",
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
	
	// To make uncompleted functionalities disable.
	$(".disabledEle").attr("disabled", "disabled");
});