$(document).ready(function(){

	$('#changeStateButton').click(
		function(){

			if($('#status').val() == 'APPROVAL'){
				var application = $('#applicationId').val();
				if(confirm("Are you sure you want to move this application to the approval stage?"))
				{
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
			}
		
			if($('#status').val() == 'REJECTED'){
				var application = $('#applicationId').val();
				if(confirm("Are you sure you want to reject this application?"))
				{
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
			
			}
			
	});
});