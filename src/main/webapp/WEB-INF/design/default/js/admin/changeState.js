$(document).ready(function(){

	$('#changeStateButton').click(function(){
		if($('#status').val() == 'APPROVAL'){
			var application = $('#applicationId').val();
			if(confirm("Are you sure you want to move this application to the approval stage?"))
			{
				$.post("/pgadmissions/approval",
				{
					application: application
				}, 
				function(data) {
//					$('#statusColumn').html('Withdrawn');
					window.location.href = "/pgadmissions/applications";
				}
			);
			}
		}
		
	});
});