
var sections = 9;  // as in sections to load.
var firstTimeLoading = true;

$(document).ready(function()
{	
	
	$("#acceptTermsValue").val("NO");
	
	// --------------------------------------------------------------------------------
	// LOAD APPLICATION FORM SECTIONS
	// --------------------------------------------------------------------------------
	
	if ($('#timeline').length > 0)
	{
		// Timeline is on the page, so place the loading prompt inside the application tab.
		$('#ajaxloader').show();
	}
	else
	{
		// Place the loading prompt in the main section.
		$('#ajaxloader').show();
	}
	
	/* Programme Details. */
	loadProgrammeSection(false, function () {
		
		$('#ajaxloader').fadeOut('fast');
		
		/* Personal Details. */
		 loadPersonalDetails();

		 
		/* Terms and conditions. */
		$.ajax({
				type: 'GET',
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
				url:"/pgadmissions/acceptTerms/getTermsAndConditions",
				data:{
					applicationId:  $('#applicationId').val(),
					errorCode: $('#termsAndConditionsError').val()
				},
				success: function(data)
				{
					$('#acceptTermsSection').html(data);
				}
		});
	});	
	
	
	
	
	/*
	 * Submit commnet on click of comment submit button.
	 */ 
	$('#commentSubmitButton').click(function()
	{
		$('#commentField').val($('#comment').val());
		$('#commentForm').submit();
	});
	
	/*
	 * Keep the view state of the form open or close depending on the user role.
	 */
	$(window).load(function(){
		
		
		// To make uncompleted functionalities disable.
		$(".disabledEle").attr("disabled", "disabled");
		
	});
	
	// --------------------------------------------------------------------------------
	// "SAVE AND CLOSE" BUTTON
	// --------------------------------------------------------------------------------
	$('#saveAndClose').click(function()
	{
		window.location.href = '/pgadmissions/applications';
	});
	
	// --------------------------------------------------------------------------------
	// SUBMIT APPLICATION FORM
	// --------------------------------------------------------------------------------
	$('#submitAppButton').click(function()
	{
		/*if ($("#acceptTermsValue").val() == 'NO')
		{ 
			$("#acceptTermsSection .row-group").css({borderColor: 'red'});
			$('.terms-label').css({color: 'red'});
		}
		else
		{*/
			$("span[name='nonAccepted']").html('');
			$('#submitApplicationForm').append('<input type="hidden" name="acceptedTermsOnSubmission" value="'+$('#acceptTermsValue').val() +'"/>');
			$('#ajaxloader').show();
			
			$('#submitApplicationForm').submit();
			
		//}
	});
	
	/* Cases for comment section */
	
	/*
	 * 1. Depending on the which view user has requested, the comment form will
	 * be displayed. Temparary. commentForm
	 */
	
	var viewType = $('input#view-type-personal-form').val();
	
	/* 1.1 Show / Hide the whole Comment Box */
	
	if (viewType == "comments")
	{
		$('div#show-comment-button-div').hide();
		$('#commentForm').show();
	
		var commentCount = $('#prev-comment-div').val();
		
		if (commentCount > 0)
		{
			$('.comment').show();
		}
		else
		{
			$('.comment').hide();
		}
		
	}
	else
	{
		$('div#show-comment-button-div').show();
		$('#commentForm').hide();
	}

	
	/*
	 * 2. Inside the Comment Box, on click of the close button, hide the Comment
	 * Box
	 */
	
	$('#comment-close-button').click(function()
	{
		// $('#comment').css("display", "none");
		$('div#show-comment-button-div').show();
		$('#commentForm').hide();
	});

	/*
	 * 3. Outside the Comment Box, on click of the comment button, show the
	 * Comment Box and hide the comment button
	 */
	
	$('#comment-button').click(function()
	{
		
		$('div#show-comment-button-div').hide();
		$('#commentForm').show();
		
		var commentCount = $('#prev-comment-div').val();
		
		if (commentCount > 0)
		{
			$('.comment').show();
		}
		else
		{
			$('.comment').hide();
		}
		
	});
	

	
	
});

function closeSections()
{
	if ($("input#form-display-state").val() == "close")
	{
		// close everything
		$('section.folding:not(.error) > div').removeClass("open").hide();
		$('section.folding:not(.error) > h2').removeClass("open");
	}
}


function checkLoadedSections()
{
	sections--;
	if (sections <= 0)
	{
		if ($('#timeline').length > 0)
		{
			// Timeline is on the page, so place the loading prompt inside the application tab.
			$('#ajaxloader').fadeOut('fast');
		}
		else
		{
			// Place the loading prompt in the main section.
			$('#ajaxloader').fadeOut('fast');
		}
	}
}


function loadProgrammeSection(clear, onComplete){
	/* Programme Details. */
	$.ajax({
		 type: 'GET',
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
		  url: "/pgadmissions/update/getProgrammeDetails",
		  data:{
				applicationId:  $('#applicationId').val(),
				errorCode: $('#programDetailsError').val(),
				studyOptionError: $('#studyOptionError').val(),
				programError: $('#programError').val(),
				cacheBreaker: new Date().getTime() 
			}, 
		  success: function(data)
			{
			
				$('#programmeDetailsSection').html(data);
				checkLoadedSections();
				if(clear){
					$("#addSupervisorButton").show();
					$("#updateSupervisorButton").hide();				
					$("#studyOption").val("");
					$("#startDate").val("");
					$("#referrer").val("");
					$("#supervisorFirstname").val("");
					$("#supervisorLastname").val("");
					$("#supervisorEmail").val("");
					$("#awareYes").prop('checked', false);
					$("#awareNo").prop('checked', false);
				}
			},
		  complete: function() {
			  if (firstTimeLoading == true) {
			 	 onComplete();
			  }
		  }
	});
	
}

function loadPersonalDetails(clear){
	$.ajax({
		 type: 'GET',
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
			url:"/pgadmissions/update/getPersonalDetails",
			data:{
				applicationId:  $('#applicationId').val(),
				errorCode: $('#personalDetailsError').val(),
				cacheBreaker: new Date().getTime() 
			},
			success: function(data)
			{
				$('#personalDetailsSection').html(data);
				checkLoadedSections();
				if(clear){
					$("input[name*='genderRadio']").prop('checked', false);
					$("#title").val("");
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
					$("#passportNumber").val("");
			        $("#nameOnPassport").val("");
			        $("#passportIssueDate").val("");
			        $("#passportExpiryDate").val("");
					$("input[name='languageQualificationAvailable']").prop('checked', false);
					$("input[name='passportAvailable']").prop('checked', false);
			        disablePassportInformation();
					disableLanguageQualifications();
					$('#personalDetails-H2').trigger('click');
				}else{
					if ($('#personalDetailsSection.error').length > 0)
					{
						$('#personalDetails-H2').trigger('click');
					}
				}
				
			}, 
			complete: function() {
				/* Address. */
			  if (firstTimeLoading == true) {
			 	 loadAddresSection();
			  }
			}
	});
}

function loadAddresSection(clear){
	$.ajax({
		 type: 'GET',
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
			url:"/pgadmissions/update/getAddress",
			data:{
				applicationId:  $('#applicationId').val(),				
				errorCode: $('#addressError').val(),
				cacheBreaker:  new Date().getTime() 
			},
			success:function(data)
			{
				$('#addressSection').html(data);
				checkLoadedSections();
				if(clear){
					$('#currentAddressLocation').empty();
					$('#currentAddressDomicile').val('');
					$('#contactAddressLocation').empty();
					$('#contactAddressLocation').removeAttr('disabled');
					$('#contactAddressDomicile').val('');
					$('#contactAddressDomicile').removeAttr('disabled');
					$('#currentAddress1, #currentAddress2, #currentAddress3, #currentAddress4, #currentAddress5').val('');
					$('#contactAddress1, #contactAddress2, #contactAddress3, #contactAddress4, #contactAddress5').val('');
					$('#contactAddress1, #contactAddress2, #contactAddress3, #contactAddress4, #contactAddress5').removeAttr('disabled');
					$('#sameAddressCB').prop('checked', false);
					$('#address-H2').trigger('click');
				}else{
					if ($('#addressSection.error').length > 0)
					{
						$('#address-H2').trigger('click');
					}
				}
			}, 
			complete: function() {
				/* Qualifications. */
				if (firstTimeLoading == true) {	
					loadQualificationsSection();
				}
			}
	});
}


function loadQualificationsSection(clear){
	var data = {
			applicationId:  $('#applicationId').val(),
			errorCode: false,
			cacheBreaker: new Date().getTime() 				
		};

	$.ajax({
		type: 'GET',
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
		url:"/pgadmissions/update/getQualification",
		data:data,
		success: function(data)
		{
			$('#qualificationsSection').html(data);
			checkLoadedSections();
			if(clear){
				$('#qualifications-H2').trigger('click');
			}else{
			
				if ($('#qualificationsSection.error').length > 0)
				{
					$('#qualifications-H2').trigger('click');
				}
			}
		}, 
			complete: function() {
				/* (Employment) Position. */
				if (firstTimeLoading == true) {
					loadEmploymentSection();
				}

			}
	});
}

function loadEmploymentSection(clear){
	$.ajax({
		type: 'GET',
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
		url:"/pgadmissions/update/getEmploymentPosition",
		data:{
			applicationId:  $('#applicationId').val(),
			errorCode: false,
			cacheBreaker: new Date().getTime() 									
		},
		success: function(data)
		{
			$('#positionSection').html(data);
			checkLoadedSections();
			if(!clear){
				if ($('#positionSection.error').length > 0)		
				{
					$('#position-H2').trigger('click');
				}
			} else {
				$('#position-H2').trigger('click');
			}
		}, 
			complete: function() {
				/* Funding. */
				if (firstTimeLoading == true) {
					loadFundingSection();
				}
			}
	});
}

function loadFundingSection(clear){
	$.ajax({
		type: 'GET',
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
		url:"/pgadmissions/update/getFunding",
		data:{
			applicationId:  $('#applicationId').val(),
			errorCode: false,
			cacheBreaker: new Date().getTime() 									
		},
		success:function(data)
		{
			$('#fundingSection').html(data);
			checkLoadedSections();
			if(!clear){
				if ($('#fundingSection.error').length > 0)
				{
					$('#funding-H2').trigger('click');
				}
			} else {
				$('#funding-H2').trigger('click');
			}
		}, 
		complete: function() {
			/* Referees. */
			if (firstTimeLoading == true) {
				loadReferenceSection();
			}
		}
	});
}


function loadReferenceSection(clear){
	$.ajax({
		type: 'GET',
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
		url:"/pgadmissions/update/getReferee",
		data:{
			applicationId:  $('#applicationId').val(),
			errorCode: $('#refereesError').val(),
			cacheBreaker: new Date().getTime() 
		},
		success:function(data)
		{
			$('#referencesSection').html(data);
			checkLoadedSections();
			if(!clear){
				if ($('#referencesSection.error').length > 0)
				{
					$('#referee-H2').trigger('click');
				}
			} else {
				$('#referee-H2').trigger('click');
			}
		}, 
		complete: function() {
				/* Documents. */
				if (firstTimeLoading == true) {
					loadDocumentsSection();
				}
			}
	});

}

function loadDocumentsSection(clear){
	$.ajax({
		type: 'GET',
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
		url:"/pgadmissions/update/getDocuments",
		data:{
			applicationId:  $('#applicationId').val(),
			errorCode: $('#personalStatementError').val(),
			cacheBreaker:new Date().getTime() 
		},
		success:function(data)
		{
			$('#documentSection').html(data);
			checkLoadedSections();
			if(clear){
				$('#psUploadFields').removeClass("uploaded");
				$('#document_PERSONAL_STATEMENT').val('');
				$('#psLink').remove();
				
				$('#cvUploadFields').removeClass("uploaded");
				$('#document_CV').val('');
				$('#cvLink').remove();
				$('#documents-H2').trigger('click');
			}else{
				if ($('#documentSection.error').length > 0)
				{
					$('#documents-H2').trigger('click');
				}
			}
		},
		complete: function() {
			/* Additional Information. */
			if (firstTimeLoading == true) {
				loadAdditionalInformationSection();
			}
		}
	});
}


function loadAdditionalInformationSection(clear){
	$.ajax({
		type: 'GET',
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
		url:"/pgadmissions/update/getAdditionalInformation",
		data:{
			applicationId:  $('#applicationId').val(),
			errorCode:  $('#additionalInformationError').val(),
			cacheBreaker: new Date().getTime() 								
		},
		success: function(data)
		{
			$('#additionalInformationSection').html(data);
			checkLoadedSections();
			if(clear){
				$('#informationText').empty();
				$('#convictionsText').empty();
				$('input[name="convictionRadio"]').prop('checked', false);
				$('#convictionsText').attr("disabled","disabled");
				$('#additional-H2').trigger('click');
			}
			else{
				if ($('#additionalInformationSection.error').length > 0)
				{
					$('#additional-H2').trigger('click');
				}
			}
		}, complete: function() {
			firstTimeLoading = false;
		}
});
}