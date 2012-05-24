$(document).ready(function()
{

	// Tabs.
	$('#timelineview ul.tabs').tabs();
	
	$('#application').html("");
	/*$('#application').hide();*/
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
		/*$('#application').hide();
		$('#timeline').show();*/
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
		/*$('#timeline').hide();
		$('#application').show();*/
		if($('#application').html()==""){
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
		}
	});
	
	
});