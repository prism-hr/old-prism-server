$(document).ready(function(){

	$('#changeStateButton').click(
		function(){
			
			if($('#status').val() == 'APPROVAL'){						
				saveComment(moveToApproval);					
			}
		
			if($('#status').val() == 'REJECTED'){			
				saveComment(moveToRejected);				
			}
			
			if($('#status').val() == 'REVIEW'){				
				saveComment(moveToReview);
			}
			
		});
});

function saveComment(callback){
	var application = $('#applicationId').val();	
	var commentType = $('#commentType').val();
	$.post( 
			"/pgadmissions/progress",
			{
				application: application,
				type: commentType,
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
	//var application = $('#applicationId').val();
	//window.location.href = "/pgadmissions/assignReviewers?applicationId=" + application;
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