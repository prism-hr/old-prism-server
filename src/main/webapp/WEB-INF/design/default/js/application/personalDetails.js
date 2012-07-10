$(document).ready(function(){

	
	var persImgCount = 0;
	
	$("#acceptTermsPEDValue").val("NO");


	// -------------------------------------------------------------------------------
	// Close button.
	// -------------------------------------------------------------------------------
	$('#personalDetailsCloseButton').click(function()
	{
		$('#personalDetails-H2').trigger('click');
		return false;
	});

	
	// -------------------------------------------------------------------------------
	// Clear button.
	// -------------------------------------------------------------------------------
	$('#personalDetailsClearButton').click(function()
	{
		$('#personalDetailsSection > div').append('<div class="ajax" />');
		loadPersonalDetails(true);
	});

	
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
	  		'		<button class="button-delete" data-desc="Delete">Delete</button><br/>'+
	  		'	</div>';
				$('#my-nationality-div').append(html);
				$('#nationality-em').remove();
				addToolTips();
				
				// Reset field.
				$('#candidateNationalityCountry option:selected').removeAttr('selected');
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
	  		'		<button class="button-delete" data-desc="Delete">Delete</button><br/>'+
	  		'	</div>';
				$('#maternal-nationality-div').append(html);
				addToolTips();

				// Reset field.
				$('#maternalNationalityCountry option:selected').removeAttr('selected');
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
	  		'		<button class="button-delete" data-desc="Delete">Delete</button><br/>'+
	  		'	</div>';
				$('#paternal-nationality-div').append(html);
				addToolTips();

				// Reset field.
				$('#paternalNationalityCountry').val('');
			}

		}
	});
	
	
	// -------------------------------------------------------------------------------
	// Remove a nationality (all sections).
	// -------------------------------------------------------------------------------
	$(document).on('click', '.nationality-item button.button-delete', function()
	{
		// Clear the corresponding dropdown box if the deleted nationality is the same
		// as the one currently selected.
		var $select = $(this).parent().parent().next('select');
		var $field  = $(this).parent().find('input:hidden');
		if ($select.val() == $field.val())
		{
			$select.val('');
		}
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


function postPersonalDetailsData(message)
{

	//candidate nationalities
	if ($('#candidateNationalityCountry option:selected').val()!= '')
	{
		var html = 	"<span><input type='hidden' name='candidateNationalities' value='" +$('#candidateNationalityCountry option:selected').val() + "'/>" + '</span>';
		$('#existingCandidateNationalities').append(html);
	}
	

	//maternal nationalities
	if ($('#maternalNationalityCountry option:selected').val()!= '')
	{
		var html = 	"<span><input type='hidden' name='maternalGuardianNationalities' value='"  +$('#maternalNationalityCountry option:selected').val() + "'/></span>";
		$('#existingMaternalNationalities').append(html);
	}
	
	//paternal nationalities
	if ($('#paternalNationalityCountry option:selected').val()!= '')
	{
		var html = 	"<span><input type='hidden' name='paternalGuardianNationalities' value='{" +$('#paternalNationalityCountry option:selected').val() + "'/></span>";
		$('#existingPaternalNationalities').append(html);
	}
	
	var acceptedTheTerms;
	if ($("#acceptTermsPEDValue").val() == 'NO')
	{
		acceptedTheTerms = false;
	}
	else
	{
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
	

	if ($('input:radio[name=englishFirstLanguage]:checked').length > 0)
	{
		postData.englishFirstLanguage = $('input:radio[name=englishFirstLanguage]:checked').val();
	}
	

	if ($('input:radio[name=requiresVisa]:checked').length > 0 )
	{
		postData.requiresVisa = $('input:radio[name=requiresVisa]:checked').val();
	}
	
	var gender = $("input[name='genderRadio']:checked").val();
	if (gender)
	{
		postData.gender = gender;
	}
	
	//do the post!
	$('#personalDetailsSection > div').append('<div class="ajax" />');

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
				var errorCount = $('#personalDetailsSection .invalid:visible').length;
				if (errorCount == 0)
				{
					$('#personalDetails-H2').trigger('click');
				}
				else
				{
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
