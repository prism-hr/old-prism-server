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
		
//		alert($("input#form-view-state").val());
//		var className = $("div#personal-details-section").attr('class');
//		alert("the class name is " + className);
		
		if($("input#form-view-state").val() == "close"){
			// close everything
			$('section.folding > div').removeClass("open").hide();
			
			//$("div#personal-details-section").removeClass("open").hide();
			
		}
	});
});