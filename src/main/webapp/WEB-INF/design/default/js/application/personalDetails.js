$(document).ready(function(){
	$("#phonenumbers").on("click", "a", function(){	
		$(this).parent("span").remove();
		
	});
	
	$('#personalDetailsSaveButton').on("click", function(){
		var postData ={ 
				firstName: $("#firstName").val(), 
				lastName: $("#lastName").val(), 
				email: $("#email").val(),
				country: $("#country").val(), 
				dateOfBirth: $("#dateOfBirth").val(),
				residenceCountry: $("#residenceCountry").val(),
				residenceStatus: $("#residenceStatus").val(),
				personalDetailsId: $("#id").val(), 
				application: $("#appId").val(),
				gender: $("input[name='genderRadio']:checked").val(),
				languages: $('#languageSelect').val()
			}
			
		
			$.post( "/pgadmissions/personalDetails" ,$.param(postData) + "&" + $('[input[name="languages"]').serialize()+ "&" + $('[input[name="phoneNumbers"]').serialize(),
				 function(data) {
				    $('#personalDetailsSection').html(data);
				  });
	});
	
	$('#addPhoneButton').on('click', function(){
		if($('#phoneNumber').val() =="Number" || $('#phoneNumber').val().trim()== ''){
			alert("Please enter phone number");
			return;
		}
		$('#phonenumbers').append('<span name="phone_number">'+ 
				$('#phoneType option:selected').text() + " " + $('#phoneNumber').val()+ " "+'<a class="button">delete</a>'+
				'<input type="hidden" name="phoneNumbers" value=' +"'" + '{"type":"' +  $('#phoneType').val()+ '", "number":"' + $('#phoneNumber').val()+ '"} ' + "'" + "/>"									
				+'<br/></span>');
	})
	
	// To make uncompleted functionalities disable.
	$(".disabledEle").attr("disabled", "disabled");	
	

});
