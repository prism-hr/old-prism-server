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

/**
 * Change the application status to Accepted Application.
 */
function acceptApplication(id) {
	//alert("adasdfa");
	
	applicationDWR.acceptApplication(id, function(data) {
		dwr.util.setValue("demoStatus", data);
	});
	
	//alert("adasdfa");

}

$(document).ready(function(){
	
	/*
	 * Submit application form on click of submit button.
	 */ 
	$('#submitButton').click(function(){
		$('#submitApplicationForm').submit();
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