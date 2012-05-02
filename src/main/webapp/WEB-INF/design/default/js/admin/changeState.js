$(document).ready(function(){

	$('#changeStateButton').click(
		function(){
			
			if($('#status').val() == 'APPROVAL'){			
				if(confirm("Are you sure you want to move this application to the approval stage?"))
				{
					saveComment(moveToApproval);					
				}
				
			}
		
			if($('#status').val() == 'REJECTED'){
				if(confirm("Are you sure you want to reject this application?"))
				{
					saveComment(moveToRejected);
				}
			}
			
			if($('#status').val() == 'REVIEW'){
				if(confirm("Are you sure you want to move this application to the review stage?"))
				{
					saveComment(moveToReview);
				}
			}
			
		});
});

function saveComment(callback){
	var application = $('#applicationId').val();	
	$.post( 
			"/pgadmissions/progress",
			{
				application: application,
				type: 'VALIDATION',
				comment: $('#comment').val()
			},
			callback
		);

}

function moveToApproval(data){

	var application = $('#applicationId').val();
	$.post(
		"/pgadmissions/approval",
		{
			application: application
		}, 
		function(data) {
			window.location.href = "/pgadmissions/applications";
		}
	);
}

function moveToReview(data){

	var application = $('#applicationId').val();
	$.post(
		"/pgadmissions/review",
		{
			application: application
		}, 
		function(data) {
			window.location.href = "/pgadmissions/applications";
		}
	);
}

function moveToRejected(data){
	var application = $('#applicationId').val();

	$.post(
		"/pgadmissions/approveOrReject",
		{
			id: application,
			decision: 'REJECTED'
		}, 
		function(data) {
			window.location.href = "/pgadmissions/applications";
		}
	);
}