$(document).ready(function(){

	$('#application').html("");
	$.get("/pgadmissions/comments/view",
			{
				id:  $('#applicationId').val(),				
				cacheBreaker: new Date().getTime() 
			},
			function(data) {
				$('#timeline').html(data);
			}
	);
	
	
	$('#timelineBtn').click(function() {
		$('#application').html("");
		$.get("/pgadmissions/comments/view",
				{
					id:  $('#applicationId').val(),				
					cacheBreaker: new Date().getTime() 
				},
				function(data) {
					$('#timeline').html(data);
				}
		);
	});
	
	$('#applicationBtn').click(function() {
		$('#timeline').html("");
		$.get("/pgadmissions/application?view=view",
				{
			embeddedApplication: "true",				
			applicationId:  $('#applicationId').val(),				
			cacheBreaker: new Date().getTime() 
				},
				function(data) {
					$('#application').html(data);
				}
		);
	});
	
	
});