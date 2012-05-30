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
				if($('#appliationAdmin').length == 0 ||  $('#appliationAdmin').val() ==''){
					saveComment(moveToInterview);
				}else{
					$('#delegateForm').submit();
				}
				
			}			
		});
	
	$('#status').change(function(){
		if($('#status').val() == 'INTERVIEW'){
			$('#appliationAdmin').removeAttr('disabled');
		}else{
			$('#appliationAdmin')
		}
	});
	
	$('#notifyRegistryButton').click(function(){
		$('#notifyRegistryButton').attr('disabled', 'disabled');
		$('#notifyRegistryButton').removeClass("blue");
		$('body').css('cursor', 'wait');
		$.post( 
			"/pgadmissions/registryHelpRequest",
			{
				applicationId: $('#applicationId').val()
			
			},
			function(data) {
			    $('#emailMessage').html(data);
			    $('#notifyRegistryButton').removeAttr('disabled');
			    $('#notifyRegistryButton').addClass("blue");
				$('body').css('cursor', 'auto');
			 }
		);
	});
});

function saveComment(callback){
	var application = $('#applicationId').val();	
	var commentType = $('#commentType').val();
	var postData = {
			application: application,
			type: commentType,
			comment: $('#comment').val()
		};
	
	if( $('input:radio[name=qualifiedForPhd]:checked').length > 0) {
		postData.qualifiedForPhd = $('input:radio[name=qualifiedForPhd]:checked').val();
	}
	if ($('input:radio[name=englishCompentencyOk]:checked').length > 0) {
		postData.qualifiedForPhd = $('input:radio[name=englishCompentencyOk]:checked').val();
	}
	if ($('input:radio[name=homeOrOverseas]:checked').length > 0) {
		postData.homeOrOverseas = $('input:radio[name=homeOrOverseas]:checked').val();
	}
	$.post( 
		"/pgadmissions/progress",
		postData,
		callback
	);

}

function moveToApproval(data){

	var application = $('#applicationId').val();
	window.location.href = "/pgadmissions/approval/moveToApproval?applicationId=" + application;
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
