$(document).ready(function(){
	
	
	$('#addLanguageButton').on("click", function(){
		
		var primary = "";
		var jsonPrimary = "false";
		if($('#primaryLanguage').is(':checked') ){
			primary = "Primary";
			jsonPrimary = "true"
		}
		var html = 	'<span>' + $('#languageSelect option:selected').text() + " " + $('#aptitude option:selected').text() +" " + primary +
		"<input type='hidden' name='languageProficiencies' value='{" +'"aptitude":"' + $('#aptitude option:selected').val() + 
		'", "language":' + $('#languageSelect option:selected').val()  +
		', "primary": "' + jsonPrimary + '"}' + "'/>" + 
		'<a class="button-delete">Delete</a><br/></span>';
		            
				  	
	  	
		$('#existingProficiencies').append(html);
		$('#languageSelect').val("");
		$('#primaryLanguage').prop('checked', false);
	});
	
	
	//candidate nationalities
	$('#addCandidateNationalityButton').on("click", function(){
		if( $('#candidateNationalityCountry option:selected').val()!= ''){
			var primary = "";
			var jsonPrimary = "false";
			if($('#primaryCandidateNationality').is(':checked') ){
				primary = "This is my primary nationality";
				jsonPrimary = "true"
			}
					
			var html = '<span>' +
	  	 	'<div class="row">'+
	  	 	'	<label class="label">Nationality</label>'+
			'	<div class="field">'+
			'		<label class="full">' + $('#candidateNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='candidateNationalities' value='{" +'"type":"CANDIDATE", "country":' +$('#candidateNationalityCountry option:selected').val()  +	', "primary": "' + jsonPrimary + '"}' + "'/>" +
	  		'		<a class="button-delete">Delete</a>'+ primary +' <br/>'+
	  		'	</div>'+
	  		'</div>'+
	  	'</span> ';
			
			$('#existingCandidateNationalities').append(html);
			
			$('#candidateNationalityCountry').val("");
			$('#primaryCandidateNationality').prop('checked', false);
		}
		
	});
	
	
	//maternal guardian nationalities
	$('#addMaternalNationalityButton').on("click", function(){
		
		var primary = "";
		var jsonPrimary = "false";
		if($('#primaryMaternalNationality').is(':checked') ){
			primary = "This is her primary nationality";
			jsonPrimary = "true"
		}
			
		
		var html = '<span>' +
  	 	'<div class="row">'+
  	 	'	<label class="label">Maternal Guardian Nationality</label>'+
		'	<div class="field">'+
		'		<label class="full">' + $('#maternalNationalityCountry option:selected').text() + '</label>'  +
  		"		<input type='hidden' name='maternalGuardianNationalities' value='{" +'"type":"MATERNAL_GUARDIAN", "country":' +$('#maternalNationalityCountry option:selected').val()  +	', "primary": "' + jsonPrimary + '"}' + "'/>" +
  		'		<a class="button-delete">Delete</a>'+ primary +' <br/>'+
  		'	</div>'+
  		'</div>'+
  	'</span> ';
		
		
		$('#existingMaternalNationalities').append(html);
		
		$('#maternalNationalityCountry').val("");
		$('#primaryMaternalNationality').prop('checked', false);
		
	});
	
	//paternal guardian nationalities
	$('#addPaternalNationalityButton').on("click", function(){
		var primary = "";
		var jsonPrimary = "false";
		if($('#primaryPaternalNationality').is(':checked') ){
			primary = "This is his primary nationality";
			jsonPrimary = "true"
		}
		
		var html = '<span>' +
  	 	'<div class="row">'+
  	 	'	<label class="label">Paternal Guardian Nationality</label>'+
		'	<div class="field">'+
		'		<label class="full">' + $('#paternalNationalityCountry option:selected').text() + '</label>'  +
  		"		<input type='hidden' name='paternalGuardianNationalities' value='{" +'"type":"PATERNAL_GUARDIAN", "country":' +$('#paternalNationalityCountry option:selected').val()  +	', "primary": "' + jsonPrimary + '"}' + "'/>" +
  		'		<a class="button-delete">Delete</a>'+ primary +' <br/>'+
  		'	</div>'+
  		'</div>'+
  	'</span> ';
		
		
		$('#existingPaternalNationalities').append(html);
		
		$('#paternalNationalityCountry').val("");
		$('#primaryPaternalNationality').prop('checked', false);
		
	});
	
	
	$('#personalDetailsSaveButton').on("click", function(){		
		
		//language proficiencies
		if( $('#languageSelect option:selected').val()!= ''){
			var primary = "";
			var jsonPrimary = "false";
			if($('#primaryLanguage').is(':checked') ){
				primary = "Primary";
				jsonPrimary = "true"
			}
			var html = 	"<span><input type='hidden' name='languageProficiencies' value='{" +'"aptitude":"' + $('#aptitude option:selected').val() + 
			'", "language":' + $('#languageSelect option:selected').val()  +
			', "primary": "' + jsonPrimary + '"}' + "'/></span>";
			$('#existingProficiencies').append(	html);
		}
		
		//candidate nationalities
		if( $('#candidateNationalityCountry option:selected').val()!= ''){
			var primary = "";
			var jsonPrimary = "false";
			if($('#primaryCandidateNationality').is(':checked') ){
				primary = "Primary";
				jsonPrimary = "true"
			}
			
			var html = 	"<span><input type='hidden' name='candidateNationalities' value='{" +'"type":"CANDIDATE", "country":' +$('#candidateNationalityCountry option:selected').val()  +
			', "primary": "' + jsonPrimary + '"}' + "'/></span>";
			
			$('#existingCandidateNationalities').append(html);
		}
		

		//maternal nationalities
		if( $('#maternalNationalityCountry option:selected').val()!= ''){
			var primary = "";
			var jsonPrimary = "false";
			if($('#primaryMaternalNationality').is(':checked') ){
				primary = "Primary";
				jsonPrimary = "true"
			}
			
			var html = 	"<span><input type='hidden' name='maternalGuardianNationalities' value='{" +'"type":"MATERNAL_GUARDIAN", "country":' +$('#maternalNationalityCountry option:selected').val()  +
			', "primary": "' + jsonPrimary + '"}' + "'/></span>";
			
			$('#existingMaternalNationalities').append(html);
		}
		
		//paternal nationalities
		if( $('#paternalNationalityCountry option:selected').val()!= ''){
			var primary = "";
			var jsonPrimary = "false";
			if($('#primaryPaternalNationality').is(':checked') ){
				primary = "Primary";
				jsonPrimary = "true"
			}
			
			var html = 	"<span><input type='hidden' name='paternalGuardianNationalities' value='{" +'"type":"PATERNAL_GUARDIAN", "country":' +$('#paternalNationalityCountry option:selected').val()  +
			', "primary": "' + jsonPrimary + '"}' + "'/></span>";
			
			$('#existingPaternalNationalities').append(html);
		}
		
		
		//general post data
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
				languageProficiencies:"",
				candidateNationalities:"",
				maternalGuardianNationalities:"",
				paternalGuardianNationalities:""
				
			}
		var gender =  $("input[name='genderRadio']:checked").val();
		if(gender){
			postData.gender = gender;
		}
			//do the post!
			$.post( "/pgadmissions/personalDetails" ,
					$.param(postData) + 
					"&" + $('[input[name="languageProficiencies"]').serialize()+ 
					"&" + $('[input[name="candidateNationalities"]').serialize()+
					"&" + $('[input[name="maternalGuardianNationalities"]').serialize()+
					"&" + $('[input[name="paternalGuardianNationalities"]').serialize()+
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
	
	
	/// delete collection items
	$("#phonenumbers").on("click", "a", function(){	
		$(this).parent("span").remove();
		
	});
	
	$("#existingCandidateNationalities").on("click", "a", function(){	
		$(this).parent("div").parent("div").parent("span").remove();
		
	});
	
	$("#existingMaternalNationalities").on("click", "a", function(){	
		$(this).parent("div").parent("div").parent("span").remove();
		
	});
	
	$("#existingPaternalNationalities").on("click", "a", function(){	
		$(this).parent("div").parent("div").parent("span").remove();
		
	});
	
	$("#existingProficiencies").on("click", "a", function(){	
		$(this).parent("span").remove();
		
	});
		
	
});
