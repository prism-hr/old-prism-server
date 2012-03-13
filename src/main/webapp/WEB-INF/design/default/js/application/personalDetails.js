$(document).ready(function(){
	
	
	$('#addLanguageButton').on("click", function(){
		$('#existingProficiencies').append(
				'<span>' + $('#languageSelect option:selected').text() + " " + $('#aptitude option:selected').text() +
				"<input type='hidden' name='languageProficiencies' value='{" +'"aptitude":"' + $('#aptitude option:selected').val() + 
				'", "language":' + $('#languageSelect option:selected').val()  + "}'/>" + 
				'</span>'
		);
		
	});
	
	
	
	$('#addCandidateNationalityButton').on("click", function(){
		$('#currentCandiateNationality').val('{"type": "CANDIDATE", "country": ' + $('#candidateNationalityCountry').val() + ', "supportingDocuments":[]}');
		
		$('#existingcandidatenationalities').append(
				  '<span name="candidatenationality">'+
            	   '<div class="row">'+
            	  		'<label class="label">Nationality</label>'+
                		'<div class="field">'+ $('#candidateNationalityCountry option:selected').text() +
             	 		'<a class="button">delete</a> </div>'+                  	 	
           		'</div>'+		
				
				"<input type='text' value='" +  $('#currentCandiateNationality').val() + "'/>" +
				 '</span>');
		
	});
	
	
	$('#personalDetailsSaveButton').on("click", function(){		
		if( $('#languageSelect option:selected').val()!= ''){
			$('#existingProficiencies').append(
					"<span><input type='hidden' name='languageProficiencies' value='{" +'"aptitude":"' + $('#aptitude option:selected').val() + 
					'", "language":' + $('#languageSelect option:selected').val()  + "}'/>" + 
					'</span>'
			);
		}
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
				languageProficiencies:""
				
				
			}
			
		
			$.post( "/pgadmissions/personalDetails" ,$.param(postData) + 
					"&" + $('[input[name="languageProficiencies"]').serialize()+ 
					"&" + $('[input[name="candidateNationalities"]').serialize()+
					"&" + $('[input[name="phoneNumbers"]').serialize(),
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
	
	$('#primaryNationalityUploadButton').on("click", function(){
		$('#documentUploadForm').attr("action", "/pgadmissions/documents");
		$('#documentUploadForm').submit();
	});

	
	
	/// delete collection items
	$("#phonenumbers").on("click", "a", function(){	
		$(this).parent("span").remove();
		
	});
	
	$("#existingcandidatenationalities").on("click", "a", function(){	
		$(this).parent("div").parent("div").parent("span").remove();
		
	});
	
	$("#existingProficiencies").on("click", "a", function(){	
		$(this).parent("span").remove();
		
	});
});
