$(document).ready(function()
{

	/*
	$('#application').html("");
	$('#application').hide();
	$.get("/pgadmissions/comments/view",
			{
				id:  $('#applicationId').val(),				
				cacheBreaker: new Date().getTime() 
			},
			function(data) {
				$('#timeline').html(data);				
			}
	);
	*/
	

	// Timeline tab.	
	$('#timelineBtn').click(function(e)
	{
		// Set the current tab.
		$('#timeline ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');
		
		$('#application').hide();
		$('#timeline').show();
		
		$.get("/pgadmissions/comments/view",
				{
					id:  $('#applicationId').val(),				
					cacheBreaker: new Date().getTime() 
				},
				function(data) {
					$('#timeline').html(data);				
				}
		);
		
		e.preventDefault();
	});
	
	// Application tab.
	$('#applicationBtn').click(function(e)
	{
		// Set the current tab.
		$('#timeline ul.tabs li').removeClass('current');
		$(this).parent('li').addClass('current');

		$('#timeline').html("").hide();
		$('#application').show();
		
		if ($('#application').html()=="")
		{
			// Only fetch the application form if it hasn't been fetched already.
			$.get("/pgadmissions/application?view=view",
				{
					embeddedApplication: "true",				
					applicationId:  $('#applicationId').val(),				
					cacheBreaker: new Date().getTime() 
				},
				function(data)
				{
					$('#application').html(data);
				}
			);
		}

		e.preventDefault();
	});
	
	// "Open" the timeline tab by default.
	$('#timelineBtn').trigger('click');
	
});