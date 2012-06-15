$(document).ready(function()
{	
	$("#acceptTermsValue").val("NO");
	
	// --------------------------------------------------------------------------------
	// LOAD APPLICATION FORM SECTIONS
	// --------------------------------------------------------------------------------
	
	/* Programme Details. */
	$('#programmeDetailsSection').append('<div class="ajax" />');
	$.get("/pgadmissions/update/getProgrammeDetails",
			{
				applicationId:  $('#applicationId').val(),
				errorCode: $('#programDetailsError').val(),
				studyOptionError: $('#studyOptionError').val(),
				programError: $('#programError').val(),
				cacheBreaker: new Date().getTime() 
			},
			function(data)
			{
				$('#programmeDetailsSection').html(data);
				$('#programmeDetailsSection div.ajax').remove();
			}
	);
	
	/* Personal Details. */
	$('#personalDetailsSection').append('<div class="ajax" />');
	$.get("/pgadmissions/update/getPersonalDetails",
			{
				applicationId:  $('#applicationId').val(),
				errorCode: $('#personalDetailsError').val(),
				cacheBreaker: new Date().getTime() 
			},
			function(data)
			{
				$('#personalDetailsSection').html(data);
				$('#personalDetailsSection div.ajax').remove();
				if ($('#personalDetailsSection .section-error-bar').length == 0)
				{
					$('#personalDetails-H2').trigger('click');
				}
			}
	);
	
	/* Address. */
	$('#addressSection').append('<div class="ajax" />');
	$.get("/pgadmissions/update/getAddress",
			{
				applicationId:  $('#applicationId').val(),				
				errorCode: $('#addressError').val(),
				cacheBreaker:  new Date().getTime() 
			},
			function(data)
			{
				$('#addressSection').html(data);
				$('#addressSection div.ajax').remove();
				if ($('#addressSection .section-error-bar').length == 0)
				{
					$('#address-H2').trigger('click');
				}
			}
	);
	
	/* Qualifications. */
	$('#qualificationsSection').append('<div class="ajax" />');
	$.get("/pgadmissions/update/getQualification",
			{
				applicationId:  $('#applicationId').val(),
				errorCode: false,
				cacheBreaker: new Date().getTime() 				
			},
			function(data)
			{
				$('#qualificationsSection').html(data);
				$('#qualificationsSection div.ajax').remove();
				if ($('#qualificationsSection .section-error-bar').length == 0)
				{
					$('#qualifications-H2').trigger('click');
				}
			}
	);
	
	/* (Employment) Position. */
	$('#positionSection').append('<div class="ajax" />');
	$.get("/pgadmissions/update/getEmploymentPosition",
			{
				applicationId:  $('#applicationId').val(),
				errorCode: false,
				cacheBreaker: new Date().getTime() 									
			},
			function(data)
			{
				$('#positionSection').html(data);
				$('#positionSection div.ajax').remove();
				if ($('#positionSection .section-error-bar').length == 0)
				{
					$('#position-H2').trigger('click');
				}
			}
	);
	
	/* Funding. */
	$('#fundingSection').append('<div class="ajax" />');
	$.get("/pgadmissions/update/getFunding",
			{
				applicationId:  $('#applicationId').val(),
				errorCode: false,
				cacheBreaker: new Date().getTime() 									
			},
			function(data)
			{
				$('#fundingSection').html(data);
				$('#fundingSection div.ajax').remove();
				if ($('#fundingSection .section-error-bar').length == 0)
				{
					$('#funding-H2').trigger('click');
				}
			}
	);
	
	/* Referees. */
	$('#referencesSection').append('<div class="ajax" />');
	$.get("/pgadmissions/update/getReferee",
			{
				applicationId:  $('#applicationId').val(),
				errorCode: $('#refereesError').val(),
				cacheBreaker: new Date().getTime() 
			},
			function(data)
			{
				$('#referencesSection').html(data);
				$('#referencesSection div.ajax').remove();
				if ($('#referencesSection .section-error-bar').length == 0)
				{
					$('#referee-H2').trigger('click');
				}
			}
	);

	/* Documents. */
	$('#documentSection').append('<div class="ajax" />');
	$.get("/pgadmissions/update/getDocuments",
			{
				applicationId:  $('#applicationId').val(),
				errorCode: $('#personalStatementError').val(),
				cacheBreaker:new Date().getTime() 
			},
			function(data)
			{
				$('#documentSection').html(data);
				$('#documentSection div.ajax').remove();
				if ($('#documentSection .section-error-bar').length == 0)
				{
					$('#documents-H2').trigger('click');
				}
			}
	);
	
	/* Additional Information. */
	$('#additionalInformationSection').append('<div class="ajax" />');
	$.get("/pgadmissions/update/getAdditionalInformation",
			{
				applicationId:  $('#applicationId').val(),
				errorCode:  $('#additionalInformationError').val(),
				cacheBreaker: new Date().getTime() 								
			},
			function(data)
			{
				$('#additionalInformationSection').html(data);
				$('#additionalInformationSection div.ajax').remove();
				if ($('#additionalInformationSection .section-error-bar').length == 0)
				{
					$('#additional-H2').trigger('click');
				}
			}
	);
	
	/* Terms and conditions. */
	$.get("/pgadmissions/acceptTerms/getTermsAndConditions",
			{
				applicationId:  $('#applicationId').val()
			},
			function(data)
			{
				$('#acceptTermsSection').html(data);
			}
	);
	
	// Moved to withdraw_modal_window.js
//	$('#withdrawButton1').click(function(){
//		if(confirm("Are you sure you want to withdraw the application? You will not be able to submit a withdrawn application."))
//		{
//			$.post("/pgadmissions/withdraw",
//			{
//				applicationId:  $('#wapplicationFormId').val()
//			}, 
//			function(data) {
//			}	
//		);
//		}
//	});

	
	/*
	 * Submit commnet on click of comment submit button.
	 */ 
	$('#commentSubmitButton').click(function(){
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
	// SUBMIT APPLICATION FORM
	// --------------------------------------------------------------------------------
	$('#submitAppButton').click(function()
	{
		if ($("#acceptTermsValue").val() == 'NO')
		{ 
			$("#acceptTermsSection .row-group").css({borderColor: 'red'});
			$('.terms-label').css({color: 'red'});
		}
		else
		{
			$("span[name='nonAccepted']").html('');
			$('div.content-box-inner').append('<div class="ajax" />');
			$('#submitApplicationForm').submit();
		}
	});
	
	/* Cases for comment section */
	
	/*
	 * 1. Depending on the which view user has requested, the comment form will
	 * be displayed. Temparary. commentForm
	 */
	
	var viewType = $('input#view-type-personal-form').val();
	
	/* 1.1 Show / Hide the whole Comment Box */
	
	if(viewType == "comments"){
		$('div#show-comment-button-div').hide();
		$('#commentForm').show();
	
		var commentCount = $('#prev-comment-div').val();
		
		if(commentCount > 0){
			$('.comment').show();
		}else{
			$('.comment').hide();
		}
		
	}else{
		$('div#show-comment-button-div').show();
		$('#commentForm').hide();
	}

	
	/*
	 * 2. Inside the Comment Box, on click of the close button, hide the Comment
	 * Box
	 */
	
	$('#comment-close-button').click(function(){
		// $('#comment').css("display", "none");
		$('div#show-comment-button-div').show();
		$('#commentForm').hide();
	});

	/*
	 * 3. Outside the Comment Box, on click of the comment button, show the
	 * Comment Box and hide the comment button
	 */
	
	$('#comment-button').click(function(){
		
		$('div#show-comment-button-div').hide();
		$('#commentForm').show();
		
		var commentCount = $('#prev-comment-div').val();
		
		if(commentCount > 0){
			$('.comment').show();
		}else{
			$('.comment').hide();
		}
		
	});




});

function closeSections(){
	if($("input#form-display-state").val() == "close"){
		// close everything
		$('section.folding:not(.error) > div').removeClass("open").hide();
		$('section.folding:not(.error) > h2').removeClass("open");
		

	}
}

