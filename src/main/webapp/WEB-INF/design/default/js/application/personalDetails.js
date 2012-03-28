$(document).ready(function(){

	$('#personalDetailsCloseButton').click(function(){		
		$('#personalDetails-H2').trigger('click');
		return false;
	});
	
	$('#personalDetailsCancelButton').click(function(){
		$("span[class='invalid']").each(function(){
			$(this).html("");
		});
	});
	
	$('#addPhoneButton').on('click', function(){
		if($('#phoneNumber').val() !="Number" && $('#phoneNumber').val()!= ''){	
		var html = ''+	
			'<span>'+
		  	'	<div class="row">'+
		  	' 		<span class="label">Telephone</span> '+   
			'		<div class="field">'+
			'			<label class="half">' +$('#phoneType option:selected').text() + '</label>'+
			'			<label class="half">'+ $('#phoneNumber').val()+ '</label>'+ 
		  	'			<a class="button-delete">Delete</a> '+             
		  	'		</div>'+		  			
		  	'	</div>  '+ 
	        "<input type='hidden' name='phoneNumbers' value='{" + '"type": "'+ $('#phoneType option:selected').val() + '", "number": "' +  $('#phoneNumber').val() + '"}' + "'/>" +
		  	'</span>';
		  	  	
			$('#phonenumbers').append(html);
			
			$('#phoneNumber').val('');
		}
	});
	
	
	
	$('#addLanguageButton').on("click", function(){
		if( $('#languageSelect option:selected').val()!= ''){
			var primary = "";
			var jsonPrimary = "false";
			if($('#primaryLanguage').is(':checked') ){
				primary = "This is my primary language";
				jsonPrimary = "true";
			}			
			            
			
			var html =  '<span>'+
	  	  	'	<div class="row">'+
		  	'		<label class="label">Language</label>'+    
			'		<div class="field">'+
			'			<label class="full">'+ $('#languageSelect option:selected').text()  +'</label>'+ 
		  	'			<a class="button-delete">Delete</a> ' + primary +             
		  	'		</div>'+
		  	'		<span class="label">Aptitude</span>    '+
			'		<div class="field">'+
			'			<label class="full">' +  $('#aptitude option:selected').text() + '</label>'+
		  	'		</div>'+
		  	'	</div>   '+
	        "<input type='hidden' name='languageProficiencies' value='{" +'"aptitude":"' + $('#aptitude option:selected').val() + '", "language":' + $('#languageSelect option:selected').val()  +', "primary": "' + jsonPrimary + '"}' + "'/>" + 
		  	'</span>';
	        
		  	
			$('#existingProficiencies').append(html);
			$('#languageSelect').val("");
			$('#primaryLanguage').prop('checked', false);
		}
	});
	
	
	//candidate nationalities
	$('#addCandidateNationalityButton').on("click", function(){
		if( $('#candidateNationalityCountry option:selected').val()!= ''){
					
			var html = '<span>' +
	  	 	'<div class="row">'+
	  	 	'	<label class="label">Nationality</label>'+
			'	<div class="field">'+
			'		<label class="full">' + $('#candidateNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='candidateNationalities' value='{" +'"type":"CANDIDATE", "country":' +$('#candidateNationalityCountry option:selected').val() + '}' + "'/>" +
	  		'		<a class="button-delete">Delete</a><br/>'+
	  		'	</div>'+
	  		'</div>'+
	  	'</span> ';
			
			$('#existingCandidateNationalities').append(html);
			
			$('#candidateNationalityCountry').val("");
		}
		
	});
	
	
	//maternal guardian nationalities
	$('#addMaternalNationalityButton').on("click", function(){
		if( $('#maternalNationalityCountry option:selected').val()!= ''){
			
			var html = '<span>' +
	  	 	'<div class="row">'+
	  	 	'	<label class="label">Maternal Guardian Nationality</label>'+
			'	<div class="field">'+
			'		<label class="full">' + $('#maternalNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='maternalGuardianNationalities' value='{" +'"type":"MATERNAL_GUARDIAN", "country":' +$('#maternalNationalityCountry option:selected').val() + '}' + "'/>" +
	  		'		<a class="button-delete">Delete</a><br/>'+
	  		'	</div>'+
	  		'</div>'+
	  	'</span> ';
			
			
			$('#existingMaternalNationalities').append(html);
			
			$('#maternalNationalityCountry').val("");
		}
		
	});
	
	//paternal guardian nationalities
	$('#addPaternalNationalityButton').on("click", function(){
		if( $('#paternalNationalityCountry option:selected').val()!= ''){
			var html = '<span>' +
	  	 	'<div class="row">'+
	  	 	'	<label class="label">Paternal Guardian Nationality</label>'+
			'	<div class="field">'+
			'		<label class="full">' + $('#paternalNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='paternalGuardianNationalities' value='{" +'"type":"PATERNAL_GUARDIAN", "country":' +$('#paternalNationalityCountry option:selected').val()   + '}' + "'/>" +
	  		'		<a class="button-delete">Delete</a> <br/>'+
	  		'	</div>'+
	  		'</div>'+
	  	'</span> ';
			
			
			$('#existingPaternalNationalities').append(html);
			$('#paternalNationalityCountry').val("");
		}
	});
	
	
	$('#personalDetailsSaveButton').on("click", function(){		
		
		//phonenumbers
		if($('#phoneNumber').val() !="Number" && $('#phoneNumber').val()!= ''){	
			var html ="<input type='hidden' name='phoneNumbers' value='{" + '"type": "'+ $('#phoneType option:selected').val() + '", "number": "' +  $('#phoneNumber').val() + '"}' + "'/>" ;
			  	  	
			$('#phonenumbers').append(html);		
			
		}
		
		//language proficiencies
		if( $('#languageSelect option:selected').val()!= ''){
			var primary = "";
			var jsonPrimary = "false";
			if($('#primaryLanguage').is(':checked') ){
				primary = "This is my primary language";
				jsonPrimary = "true"
			}
			var html =  '<span>'+
	        "<input type='hidden' name='languageProficiencies' value='{" +'"aptitude":"' + $('#aptitude option:selected').val() + '", "language":' + $('#languageSelect option:selected').val()  +', "primary": "' + jsonPrimary + '"}' + "'/>" + 
		  	'</span>';
			

			$('#existingProficiencies').append(	html);
		}
		
		//candidate nationalities
		if( $('#candidateNationalityCountry option:selected').val()!= ''){
			var html = 	"<span><input type='hidden' name='candidateNationalities' value='{" +'"type":"CANDIDATE", "country":' +$('#candidateNationalityCountry option:selected').val()  + '"}' + "'/>" + '</span>';
				
			
			$('#existingCandidateNationalities').append(html);
		}
		

		//maternal nationalities
		if( $('#maternalNationalityCountry option:selected').val()!= ''){
			var html = 	"<span><input type='hidden' name='maternalGuardianNationalities' value='{" +'"type":"MATERNAL_GUARDIAN", "country":' +$('#maternalNationalityCountry option:selected').val()  + 
			'}' + "'/></span>";
			
			$('#existingMaternalNationalities').append(html);
		}
		
		//paternal nationalities
		if( $('#paternalNationalityCountry option:selected').val()!= ''){
			
			var html = 	"<span><input type='hidden' name='paternalGuardianNationalities' value='{" +'"type":"PATERNAL_GUARDIAN", "country":' +$('#paternalNationalityCountry option:selected').val()  +
			'}' + "'/></span>";
			
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
				messenger: $("#pd_messenger").val(),
				personalDetailsId: $("#id").val(), 
				application: $("#appId").val(),		
				languageProficiencies:"",
				candidateNationalities:"",
				maternalGuardianNationalities:"",
				paternalGuardianNationalities:"",
				phoneNumbers:""
				
			};
		
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
				  }
		);
});
	

	
	// To make uncompleted functionalities disable.
	$(".disabledEle").attr("disabled", "disabled");	
	
	
	/// delete collection items
	if($('#submissionStatus').val()=="UNSUBMITTED"){
		$("#phonenumbers").on("click", "a", function(){	
			$(this).parent("div").parent("div").parent("span").remove();
			
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
			$(this).parent("div").parent("div").parent("span").remove();
			
		});
		
	}
	
	 bindDatePickers();

		//open/close
	var $header  = $('#personalDetails-H2');
	var $content = $header.next('div');
	$header.bind('click', function()
	{	
	  $content.toggle();
	  $(this).toggleClass('open', $content.is(':visible'));
	  return false;
	});
		
});
