$(document).ready(function(){

	$("#country").val($("#countryDP").val());
	$("#residenceCountry").val($("#residenceCountryDP").val());
	$("#residenceStatus").val($("#residenceStatusDP").val());
	var savedGender = $("#gender").val();
	if (savedGender =='Male'){
		$("#maleGender").attr('checked', true);
	} else if (savedGender =='Female'){
		$("#femaleGender").attr('checked', true);
	} else {
		$("#notSaidGender").attr('checked', true);
	}
	
	$('#personalDetailsSaveButton').on("click", function(){
		$.post("/pgadmissions/update/editPersonalDetails",
				{ 
					firstName: $("#firstName").val(), 
					lastName: $("#lastName").val(), 
					email: $("#email").val(), 
					dateOfBirth: $("#dateOfBirth").val(),
					country: $("#country").val(),
					residenceCountry: $("#residenceCountry").val(),
					residenceStatus: $("#residenceStatus").val(),
					id: $("#id").val(), 
					appId: $("#appId").val(),
					gender: $("#gender").val()
				},
				 function(data) {
				    $('#personalDetailsSection').html(data);
				  });
	});
	
	$("input[name*='genderRadio']").click(function() {
		var verb = "";
		if($(this).val() == 'MALE'){
			verb = "Male";
		}
		if($(this).val() == 'FEMALE'){
			verb = "Female";
		}
		
		if($(this).val() == 'PREFER NOT TO SAY'){
			verb = "Prefer not to say";
		}
		
		$("#gender").val(verb);

	});
	
	// To make uncompleted functionalities disable.
	$(".disabledEle").attr("disabled", "disabled");
});