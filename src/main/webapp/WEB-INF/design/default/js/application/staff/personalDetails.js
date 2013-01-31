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
		  	  	
			$('#personal_details_phonenumbers').append(html);
			
			$('#phoneNumber').val('');
			
			$('#telephone-em').remove();
		}
	});
	
	
	
	$('#addLanguageButton').on("click", function(){
		if( $('#languageSelect option:selected').val()!= ''){
			
			var html =  '<span>'+
	  	  	'	<div class="row">'+
		  	'		<label class="label">Language</label>'+    
			'		<div class="field">'+
			'			<label class="full">'+ $('#languageSelect option:selected').text()  +'</label>'+ 
		  	'			<a class="button-delete">Delete</a> ' +             
		  	'		</div>'+
		  	'		<span class="label">Aptitude</span>    '+
			'		<div class="field">'+
			'			<label class="full">' +  $('#aptitude option:selected').text() + '</label>'+
		  	'		</div>'+
		  	'	</div>   '+
	        "<input type='hidden' name='languageProficiencies' value='{" +'"aptitude":"' + $('#aptitude option:selected').val() + '", "language":' + $('#languageSelect option:selected').val()  + '}' + "'/>" + 
		  	'</span>';
	        
		  	
			$('#existingProficiencies').append(html);
			$('#languageSelect').val("");
			
			$('#aptitude-em').remove();
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
			
			$('#nationality-em').remove();
		}
		
	});
	
	$('#personalDetailsSaveButton').on("click", function(){		
		

		//phonenumbers
		if($('#phoneNumber').val() !="Number" && $('#phoneNumber').val() != ''){	
			var html ="<input type='hidden' name='phoneNumbers' value='{" + '"type": "'+ $('#phoneType option:selected').val() + '", "number": "' +  $('#phoneNumber').val() + '"}' + "'/>" ;
			  	  	
			$('#personal_details_phonenumbers').append(html);		
			
		}
		
		//language proficiencies
		if( $('#languageSelect option:selected').val()!= ''){
			var html =  '<span>'+
	        "<input type='hidden' name='languageProficiencies' value='{" +'"aptitude":"' + $('#aptitude option:selected').val() + '", "language":' + $('#languageSelect option:selected').val()  + '}' + "'/>" + 
		  	'</span>';
			

			$('#existingProficiencies').append(	html);
		}
		
		//candidate nationalities
		if( $('#candidateNationalityCountry option:selected').val()!= ''){
			var html = 	"<span><input type='hidden' name='candidateNationalities' value='{" +'"type":"CANDIDATE", "country":' +$('#candidateNationalityCountry option:selected').val()  + '}' + "'/>" + '</span>';
			$('#existingCandidateNationalities').append(html);
		}
		
		//general post data
		var postData ={ 
		        title: $("#title").val(),
				firstName: $("#firstName").val(), 
				lastName: $("#lastName").val(), 
				email: $("#email").val(),
				country: $("#country").val(), 
				dateOfBirth: $("#dateOfBirth").val(),
				residenceCountry: $("#residenceCountry").val(),
				residenceStatus: $("#residenceStatus").val(),
				messenger: $("#pd_messenger").val(),				
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
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  },
				  500: function() {
					  window.location.href = "/pgadmissions/error";
				  },
				  404: function() {
					  window.location.href = "/pgadmissions/404";
				  },
				  400: function() {
					  window.location.href = "/pgadmissions/400";
				  },				  
				  403: function() {
					  window.location.href = "/pgadmissions/404";
				  }
			  },
			url:"/pgadmissions/personalDetails" ,
			data:	$.param(postData) + 
				"&" + $('input[name="languageProficiencies"]').serialize()+ 
				"&" + $('input[name="candidateNationalities"]').serialize()+
				"&" + $('input[name="maternalGuardianNationalities"]').serialize()+
				"&" + $('input[name="paternalGuardianNationalities"]').serialize()+
				"&" + $('#personal_details_phonenumbers input[name="phoneNumbers"]').serialize(),
			success: function(data) {
				    $('#personalDetailsSection').html(data);
				  }
		});
});
	

	
	// To make uncompleted functionalities disable.
	$(".disabledEle").attr("disabled", "disabled");	
	
	
	/// delete collection items
	if($('#submissionStatus').val()=="UNSUBMITTED"){
		$("#personal_details_phonenumbers").on("click", "a", function(){	
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
