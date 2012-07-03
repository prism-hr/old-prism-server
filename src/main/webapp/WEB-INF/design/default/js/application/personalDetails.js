$(document).ready(function(){

	
	var persImgCount = 0;
	
	//Adjust CSS to display multiple values in one line, delimited by comma ','
	// in admin view.
	$(".half,.multiples").each(function()
	{
		$(this).css({ display: 'inline' });
	});
	
	$("#acceptTermsPEDValue").val("NO");


	// -------------------------------------------------------------------------------
	// Close button.
	// -------------------------------------------------------------------------------
	$('#personalDetailsCloseButton').click(function()
	{
		$('#personalDetails-H2').trigger('click');
		return false;
	});

	
	$('#personalDetailsClearButton').click(function(){
		
		$("input[name*='genderRadio']").prop('checked', false);
		$("#dateOfBirth").val("");
		$("#country").val("");
		$("#candidateNationalityCountry").val("");
		
		$("#maternalNationalityCountry").val("");
		
		$("#paternalNationalityCountry").val("");
		$("#englishFirstLanguageYes").prop('checked', false);
		$("#englishFirstLanguageNo").prop('checked', false);
		
		$("#residenceCountry").val("");
		
		$("#requiresVisaYes").prop('checked', false);
		$("#requiresVisaNo").prop('checked', false);
		
		$("#pd_telephone").val("");
		
		$("#pd_messenger").val("");
		
		$("#ethnicity").val("");
		$("#disability").val("");
		
	});

	// -------------------------------------------------------------------------------
	// Clear button.
	// -------------------------------------------------------------------------------
//	$('#personalDetailsCancelButton').click(function()
//	{
//		$.ajax({
//			 	type: 'GET',
//			 	statusCode: {
//			 		401: function() {
//			 			window.location.reload();
//			 		}
//			 	},
//				url:"/pgadmissions/update/getPersonalDetails",
//				data:{
//					applicationId:  $('#applicationId').val(),					
//					cacheBreaker: new Date().getTime()					
//				},
//				success: function(data) {
//					$('#personalDetailsSection').html(data);
//				}
//		});
//	});
	

	
	
	// -------------------------------------------------------------------------------
	// Add a nationality (candidate).
	// -------------------------------------------------------------------------------
	$('#addCandidateNationalityButton').on("click", function()
	{
		var selected = $('#candidateNationalityCountry option:selected').val();
		if (selected != '')
		{
			// Find duplicate nationalities.
			var duplicate = false;
			$('#my-nationality-div input[type="hidden"]').each(function()
			{
				if ($(this).val() == selected)
				{
					duplicate = true;
					return false;
				}
			});
			
			if (!duplicate)
			{
				var html = 
				'	<div class="nationality-item">'+
				'		<label class="full">' + $('#candidateNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='candidateNationalities' value='" +$('#candidateNationalityCountry option:selected').val()+ "'/>" +
	  		'		<a class="button-delete" data-desc="Delete">Delete</a><br/>'+
	  		'	</div>';
				$('#my-nationality-div').append(html);
				$('#nationality-em').remove();
				addToolTips();
			}

		}
		
	});
	

	// -------------------------------------------------------------------------------
	// Add a nationality (mother).
	// -------------------------------------------------------------------------------
	$('#addMaternalNationalityButton').on("click", function()
	{
		var selected = $('#maternalNationalityCountry option:selected').val();
		if (selected != '')
		{
			// Find duplicate nationalities.
			var duplicate = false;
			$('#maternal-nationality-div input[type="hidden"]').each(function()
			{
				if ($(this).val() == selected)
				{
					duplicate = true;
					return false;
				}
			});
			
			if (!duplicate)
			{
				var html = 
				'	<div class="nationality-item">'+
				'		<label class="full">' + $('#maternalNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='maternalGuardianNationalities' value='" +$('#maternalNationalityCountry option:selected').val()+ "'/>" +
	  		'		<a class="button-delete" data-desc="Delete">Delete</a><br/>'+
	  		'	</div>';
				$('#maternal-nationality-div').append(html);
				addToolTips();
			}
		}
	});
	

	// -------------------------------------------------------------------------------
	// Add a nationality (father).
	// -------------------------------------------------------------------------------
	$('#addPaternalNationalityButton').on("click", function()
	{
		var selected = $('#paternalNationalityCountry option:selected').val();
		if (selected != '')
		{
			// Find duplicate nationalities.
			var duplicate = false;
			$('#paternal-nationality-div input[type="hidden"]').each(function()
			{
				if ($(this).val() == selected)
				{
					duplicate = true;
					return false;
				}
			});
			
			if (!duplicate)
			{
				var html = 
				'	<div class="nationality-item">'+
				'		<label class="full">' + $('#paternalNationalityCountry option:selected').text() + '</label>'  +
	  		"		<input type='hidden' name='paternalGuardianNationalities' value='" +$('#paternalNationalityCountry option:selected').val()+ "'/>" +
	  		'		<a class="button-delete" data-desc="Delete">Delete</a><br/>'+
	  		'	</div>';
				$('#paternal-nationality-div').append(html);
				addToolTips();
			}

		}
	});
	
	
	// -------------------------------------------------------------------------------
	// Remove a nationality (all sections).
	// -------------------------------------------------------------------------------
	$(document).on('click', '.nationality-item a.button-delete', function()
	{
		$(this).parent().remove();
		return false;
	});
	

	// -------------------------------------------------------------------------------
	// "Accept terms" checkbox.
	// -------------------------------------------------------------------------------
	$("input[name*='acceptTermsPEDCB']").click(function() {
		if ($("#acceptTermsPEDValue").val() =='YES'){
			$("#acceptTermsPEDValue").val("NO");
		} else {	
			$("#acceptTermsPEDValue").val("YES");			
			
			persImgCount = 0;
			
			}
		});
	

	// -------------------------------------------------------------------------------
	// Save button.
	// -------------------------------------------------------------------------------
	$('#personalDetailsSaveButton').on("click", function()
	{	
	
		$("span[name='nonAcceptedPED']").html('');

		// Attempt saving of "dirty" nationalities.
		$('#addCandidateNationalityButton').trigger('click');
		$('#addMaternalNationalityButton').trigger('click');
		$('#addPaternalNationalityButton').trigger('click');
		
		postPersonalDetailsData('close');
	

	});
	

	
	// To make uncompleted functionalities disabled
	$(".disabledEle").attr("disabled", "disabled");	
	
	/// delete collection items		
	$("#existingCandidateNationalities").on("click", "a", function(){	
		$(this).parent("div").remove();
		
		if ( $('#existingCandidateNationalities').children().length <= 1 ) {
			$('#candidateNationalitiesLabel').remove();
			$('#my-nationality-lb').html("My Nationality");
		}
		
		if($('#existingCandidateNationalities').children().length == 1 ) {
			
			$('#my-hint').remove();
			$('#my-nationality-hint').show();
		}
					
	});
	
	$("#existingMaternalNationalities").on("click", "a", function(){	
		$(this).parent("div").remove();
		
		if ( $('#existingMaternalNationalities').children().length <= 1 ) {
			$('#maternalNationalitiesLabel').remove();
			$('#maternal-nationality-lb').html("Mother's Nationality");
		}
	});
	
	$("#existingPaternalNationalities").on("click", "a", function(){	
		$(this).parent("div").remove();
		
		if ( $('#existingPaternalNationalities').children().length <= 1 ) {
			$('#paternalNationalitiesLabel').remove();
			$('#paternal-nationality-lb').html("Father's Nationality");
		}
	});
		
	
	
	bindDatePicker('#dateOfBirth');
	addToolTips();
		
});


function postPersonalDetailsData(message){

	//candidate nationalities
	if( $('#candidateNationalityCountry option:selected').val()!= ''){
		var html = 	"<span><input type='hidden' name='candidateNationalities' value='" +$('#candidateNationalityCountry option:selected').val() + "'/>" + '</span>';
			
		
		$('#existingCandidateNationalities').append(html);
	}
	

	//maternal nationalities
	if( $('#maternalNationalityCountry option:selected').val()!= ''){
		var html = 	"<span><input type='hidden' name='maternalGuardianNationalities' value='"  +$('#maternalNationalityCountry option:selected').val() + "'/></span>";
		
		$('#existingMaternalNationalities').append(html);
	}
	
	//paternal nationalities
	if( $('#paternalNationalityCountry option:selected').val()!= ''){
		
		var html = 	"<span><input type='hidden' name='paternalGuardianNationalities' value='{" +$('#paternalNationalityCountry option:selected').val() + "'/></span>";
		
		$('#existingPaternalNationalities').append(html);
	}
	
	var acceptedTheTerms;
	if ($("#acceptTermsPEDValue").val() == 'NO'){
		acceptedTheTerms = false;
	}
	else{
		acceptedTheTerms = true;
	}
	//general post data
	var postData ={ 
			firstName: $("#firstName").val(), 
			lastName: $("#lastName").val(), 
			email: $("#email").val(),
			country: $("#country").val(), 
			dateOfBirth: $("#dateOfBirth").val(),
			residenceCountry: $("#residenceCountry").val(),
			messenger: $("#pd_messenger").val(),
			phoneNumber: $('#pd_telephone').val(),			
			application: $('#applicationId').val(),
			applicationId: $('#applicationId').val(),			
			candidateNationalities:"",
			maternalGuardianNationalities:"",
			paternalGuardianNationalities:"",
			ethnicity: $("#ethnicity").val(), 
			disability: $("#disability").val(), 
			message: message,
			acceptedTerms: acceptedTheTerms
			
		};
	

	if ($('input:radio[name=englishFirstLanguage]:checked').length > 0) {
		postData.englishFirstLanguage = $('input:radio[name=englishFirstLanguage]:checked').val();
	}
	

	if ($('input:radio[name=requiresVisa]:checked').length > 0 ) {
		postData.requiresVisa = $('input:radio[name=requiresVisa]:checked').val();
	}
	
	var gender =  $("input[name='genderRadio']:checked").val();
	if(gender){
		postData.gender = gender;
	}
	
	//do the post!
	$('#personalDetailsSection > div').append('<div class="ajax" />');

	$.ajax({ 
		type: 'POST',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
			url:"/pgadmissions/update/editPersonalDetails" ,
			data:$.param(postData) + 
			"&" + $('input[name="candidateNationalities"]').serialize()+
			"&" + $('input[name="maternalGuardianNationalities"]').serialize()+
			"&" + $('input[name="paternalGuardianNationalities"]').serialize(),
			
			 success: function(data)
			 {
			    $('#personalDetailsSection').html(data);
				

					if (message == 'close')
					{
						// Close the section only if there are no errors.
						var errorCount = $('#personalDetailsSection .invalid:visible').length  ;
						if (errorCount == 0)
						{
							$('#personalDetails-H2').trigger('click');
						}else{
							markSectionError('#personalDetailsSection');
						}
					}
			  },
    complete: function()
    {
      $('#personalDetailsSection div.ajax').remove();
    }
	});
	
}
