$(document).ready(function(){
	$('#addressSaveButton').click(function(){
		$.post("/pgadmissions/update/editAddress", { 
			location: $("#location").val(),
			postCode: $("#postCode").val(), 
			country: $("#country").val(), 
			startDate: $("#startDate").val(),
			endDate: $("#endDate").val(), 
			purpose: $("#purpose").val(), 
			contactAddress: $("#contactAddress").val(),
			id: $("#id").val(), 
			appId: $("#appId").val()
		},
		function(data) {
			$('#addressSection').html(data);
		});
	});
	
	$("input[name*='isCA']").click(function() {
		var verb = "";
		if($(this).val() == 'YES'){
			verb = "YES";
		}else{
			verb= "NO";
		}
		$("#contactAddress").val(verb);

	});	
});