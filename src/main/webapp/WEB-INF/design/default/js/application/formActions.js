/**
 * Fetches a single POJO to represent personal details of the user. 
 */
function fetchPersonalDetails() {
	//alert("fetchPersonalDetails - begin");

	/*applicationDWR.displayPersonalDetails(function(data) {
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

function takeAction(row){
	/*
	var actionText = row.value;
	if(actionText == "Reject"){
		
		alert($('#row_app_id').val());
		//acceptApplication(id);
		
	}
	
	alert(actionText); */
}

$(document).ready(function(){
	$('#submitButton').click(function(){
		$('#submitApplicationForm').submit();
	});
})