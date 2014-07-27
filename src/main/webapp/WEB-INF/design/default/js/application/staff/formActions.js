$(document).ready(function(){	
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
	$(window).on( "load", function(){
		if($("input#form-display-state").val() == "close"){
			// close everything
			$('section.folding:not(.error) > div').removeClass("open").hide();
			$('section.folding:not(.error) > h2').removeClass("open");
			

		}
		
		// To make uncompleted functionalities disable.
		$(".disabledEle").attr("disabled", "disabled");
		
	});
	
	
	/* Cases for comment section */
	
	/* 1. Depending on the which view user has requested, the comment form will be displayed. Temparary.
	commentForm */
	
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

	
	/* 2. Inside the Comment Box, on click of the close button, hide the Comment Box */
	
	$('#comment-close-button').click(function(){
		//$('#comment').css("display", "none");
		$('div#show-comment-button-div').show();
		$('#commentForm').hide();
	});

	/* 3. Outside the Comment Box, on click of the comment button, show the Comment Box and 
	 * hide the comment button */
	
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


	
	
	/* Tasks */
	

	/* Admin: Until the comment box have any characters, the submit button should not
	 * be enabled.*/

	
	/* Extra */
	//<input type ="hidden" id="prev-comment-div" value="${prevComments}"/>
	


});