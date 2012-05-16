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
			
			if($('#status').val() == 'INTERVIEW'){				
				saveComment(moveToInterview);
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
function moveToInterview(data){
	
	var application = $('#applicationId').val();
	window.location.href = "/pgadmissions/interview/moveToInterview?assignOnly=false&applicationId=" + application;
}

function moveToReview(data){
	var application = $('#applicationId').val();
	window.location.href = "/pgadmissions/review/moveToReview?applicationId=" + application;
}

function moveToRejected(data){
	var application = $('#applicationId').val();
	window.location.href = "/pgadmissions/rejectApplication?applicationId=" + application;
}