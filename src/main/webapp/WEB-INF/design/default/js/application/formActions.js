$(document).ready(function(){
	
	
	$('#personalDetailsSaveButton').click(function(){
		$.post("/pgadmissions/apply/editPersonalDetails", { firstName: $("#firstName").val(), 
								lastName: $("#lastName").val(), 
								email: $("#email").val(), 
								id: $("#id").val(), 
								appId: $("#appId").val()
								},
				   function(data) {
				     $('#personalDetailsSection').html(data);
				   });
	});
	
	$('#addressSaveButton').click(function(){
		$.post("/pgadmissions/apply/editAddress", { address: $("#address").val(), 
								id: $("#id").val(), 
								appId: $("#appId").val()
								},
				   function(data) {
					alert(data);
				     $('#addressSection').html(data);
				   });
	});
	
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
		
		if($("input#form-view-state").val() == "close"){
			// close everything
			$('section.folding > div').removeClass("open").hide();
			$('section.folding > h2').removeClass("open");
		}
		
		// To make uncompleted functionalities disable.
		$(".disabledEle").attr("disabled", "disabled");
		
	});
	
	/*
	 * On click of the close button inside comment div, the div should be returned to its contrasted state.
	 * */
	$('#comment-close').click(function(){
		$('#comment').css("display", "none");
	});
	
});