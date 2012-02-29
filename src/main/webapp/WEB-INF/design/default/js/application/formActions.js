/**
 * Fetches a single POJO to represent personal details of the user. 
 */
function fetchPersonalDetails() {
	//alert("fetchPersonalDetails - begin");
	
	/*
	applicationDWR.displayPersonalDetails(function(data) {
		dwr.util.setValue("demoStatus", data);

		//alert(data.firstName + " " + data.lastName);

		$("#firstName").val(data.firstName);
		$("#lastName").val(data.lastName);

	});*/

}


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
	
	$(window).load(function(){
		
//		alert($("input#form-view-state").val());
//		var className = $("div#personal-details-section").attr('class');
//		alert("the class name is " + className);
		
		if($("input#form-view-state").val() == "close"){
			// close everything
			$('section.folding > div').removeClass("open").hide();
			$('section.folding > h2').removeClass("open");
			
			//$("div#personal-details-section").removeClass("open").hide();
			
		}
		
		// To make uncompleted functionalities disable.
		$(".disabledEle").attr("disabled", "disabled");
		
	});
	
});