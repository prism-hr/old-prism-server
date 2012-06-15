$(document).ready(function()
{
	
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