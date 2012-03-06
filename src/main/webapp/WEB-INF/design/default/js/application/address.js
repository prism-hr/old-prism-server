$(document).ready(function(){
$('#addressSaveButton').click(function(){
		$.post("/pgadmissions/update/editAddress", { location: $("#location").val(),
								postCode: $("#postCode").val(), 
								country: $("#country").val(), 
								startDate: $("#startDate").val(),
								endDate: $("#endDate").val(), 
								purpose: $("#purpose").val(), 
								id: $("#id").val(), 
								appId: $("#appId").val()
								},
				   function(data) {
				     $('#addressSection').html(data);
				   });
	});
});