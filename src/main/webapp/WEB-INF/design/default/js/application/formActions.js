
var sections = 9;  // as in sections to load.


$(document).ready(function()
{	
	$("#acceptTermsValue").val("NO");
	
	// --------------------------------------------------------------------------------
	// LOAD APPLICATION FORM SECTIONS
	// --------------------------------------------------------------------------------
	
	if ($('section.folding').length > 0)
	{
		$('.content-box-inner').append('<div class="ajax" />');
	}
	
	loadProgrammeSection();
	
	
	/* Personal Details. */
	 loadPersonalDetails();
	
	/* Address. */
	 loadAddresSection();
	
	/* Qualifications. */	
	 loadQualificationsSection();
	 
	/* (Employment) Position. */
	 loadEmploymentSection();
	
	/* Funding. */
	$.ajax({
			type: 'GET',
			statusCode: {
				401: function() {
					window.location.reload();
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
				if ($('#fundingSection .section-error-bar').length == 0)
				{
					$('#funding-H2').trigger('click');
				}
			}
	});
	
	/* Referees. */
	$.ajax({
			type: 'GET',
			statusCode: {
				401: function() {
					window.location.reload();
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
				$('#referencesSection').prepend(data);
				checkLoadedSections();
				if ($('#referencesSection .section-error-bar').length == 0)
				{
					$('#referee-H2').trigger('click');
				}
			}
	});

	/* Documents. */
	$.ajax({
			type: 'GET',
			statusCode: {
				401: function() {
					window.location.reload();
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
				$('#documentSection').prepend(data);
				checkLoadedSections();
				if ($('#documentSection .section-error-bar').length == 0)
				{
					$('#documents-H2').trigger('click');
				}
			}
	});
	
	/* Additional Information. */
	$.ajax({
			type: 'GET',
			statusCode: {
				401: function() {
					window.location.reload();
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
				$('#additionalInformationSection').prepend(data);
				checkLoadedSections();
				if ($('#additionalInformationSection .section-error-bar').length == 0)
				{
					$('#additional-H2').trigger('click');
				}
			}
	});
	
	/* Terms and conditions. */
	$.ajax({
			type: 'GET',
			statusCode: {
				401: function() {
					window.location.reload();
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
			$('div.content-box-inner').append('<div class="ajax" />');
			
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
		$('.content-box-inner div.ajax').remove();
	}
}


function loadProgrammeSection(clear){
	/* Programme Details. */
	$.ajax({
		 type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
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
		
			}	
	});
	
}

function loadPersonalDetails(clear){
	$.ajax({
		 type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
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
				}else{
					if ($('#personalDetailsSection .section-error-bar').length == 0)
					{
						$('#personalDetails-H2').trigger('click');
					}
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
					$('#currentAddressCountry').val('');
					$('#contactAddressLocation').empty();
					$('#contactAddressLocation').removeAttr('disabled');
					$('#contactAddressCountry').val('');
					$('#contactAddressCountry').removeAttr('disabled');
					$('#sameAddressCB').prop('checked', false);
				}else{
					if ($('#addressSection .section-error-bar').length == 0)
					{
						$('#address-H2').trigger('click');
					}
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
			}
		},
		url:"/pgadmissions/update/getQualification",
		data:data,
		success: function(data)
		{
			$('#qualificationsSection').html(data);
			checkLoadedSections();
			if(clear){
				
			}else{
			
				if ($('#qualificationsSection .section-error-bar').length == 0)
				{
					$('#qualifications-H2').trigger('click');
				}
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
				if ($('#positionSection .section-error-bar').length == 0)		
				{
					$('#position-H2').trigger('click');
				}
			}
		}
});
}