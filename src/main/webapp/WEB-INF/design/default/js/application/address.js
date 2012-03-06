$(document).ready(function(){
$('#addressSaveButton').click(function(){
		$.post("/pgadmissions/update/editAddress", { street: $("#street").val(),
								postCode: $("#postCode").val(), 
								city: $("#city").val(), 
								country: $("#country").val(), 
								startDate: $("#startDate").val(),
								endDate: $("#endDate").val(), 
								id: $("#id").val(), 
								appId: $("#appId").val()
								},
				   function(data) {
				     $('#addressSection').html(data);
				   });
	});
});