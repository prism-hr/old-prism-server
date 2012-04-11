$(document).ready(function(){	
	
	$.get("/pgadmissions/update/getProgrammeDetails",
			{
				applicationId:  $('#applicationId').val()					
			},
			function(data) {
				$('#programmeDetailsSection').html(data);
			}
	);
	$.get("/pgadmissions/update/getPersonalDetails",
			{
				applicationId:  $('#applicationId').val()					
			},
			function(data) {
				$('#personalDetailsSection').html(data);
			}
	);
	$.get("/pgadmissions/update/getAddress",
			{
				applicationId:  $('#applicationId').val()					
			},
			function(data) {
				$('#addressSection').html(data);
			}
	);
	$.get("/pgadmissions/update/getQualification",
			{
				applicationId:  $('#applicationId').val()					
			},
			function(data) {
				$('#qualificationsSection').html(data);
			}
	);
	$.get("/pgadmissions/update/getEmploymentPosition",
			{
				applicationId:  $('#applicationId').val()					
			},
			function(data) {
				$('#positionSection').html(data);
			}
	);
	$.get("/pgadmissions/update/getFunding",
			{
				applicationId:  $('#applicationId').val()					
			},
			function(data) {
				$('#fundingSection').html(data);
			}
	);
	$.get("/pgadmissions/update/getReferee",
			{
				applicationId:  $('#applicationId').val()					
			},
			function(data) {
				$('#referencesSection').html(data);
			}
	);

	$.get("/pgadmissions/update/getDocuments",
			{
				applicationId:  $('#applicationId').val()					
			},
			function(data) {
				$('#documentSection').html(data);
			}
	);
	
	$.get("/pgadmissions/update/getAdditionalInformation",
			{
				applicationId:  $('#applicationId').val()					
			},
			function(data) {
				$('#additionalInformationSection').html(data);
			}
	);
	/*
	 * Submit application form on click of submit button.
	 */ 
	$('#submitButton').click(function(){
		$('#submitApplicationForm').submit();
	});
	
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
		if($("input#form-display-state").val() == "close"){
			// close everything
			$('section.folding:not(.error) > div').removeClass("open").hide();
			$('section.folding:not(.error) > h2').removeClass("open");
			

		}
		
		// To make uncompleted functionalities disable.
		$(".disabledEle").attr("disabled", "disabled");
		
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